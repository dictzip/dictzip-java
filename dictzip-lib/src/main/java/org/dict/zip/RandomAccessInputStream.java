/*
 * DictZip library.
 *
 * Copyright (C) 2001-2004 Ho Ngoc Duc
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.dict.zip;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * RandomAccessInputStream.
 * Buffering RandomAccessFile and provide InputStream interface.
 *
 * @author Ho Ngoc Duc
 * @author Hiroshi Miura
 */
public class RandomAccessInputStream extends InputStream {
    private static final int DEFAULT_BUFSIZE = 4096;
    private final RandomAccessFile in;
    private final ByteBuffer byteBuffer;
    private final int bufsize;

    private long currentpos = 0;
    private long startpos = -1;
    private long endpos = -1;
    private long mark = 0;
    private FileChannel fileChannel;


    /**
     * Construct RandomAccessInputStream from file and buffer size.
     * @param inFile RandomAccessFile
     * @param bufsize buffer size
     * @throws IOException
     */
    public RandomAccessInputStream(final RandomAccessFile inFile, final int bufsize) throws IOException {
        in = inFile;
        this.bufsize = bufsize;
        fileChannel = inFile.getChannel();
        byteBuffer = ByteBuffer.allocate(bufsize);
    }

    /**
     * Construct RandomAccessInputStream from file.
     *
     * @param inFile RandomAccessFile
     * @throws IOException when i/o error occurred.
     */
    public RandomAccessInputStream(final RandomAccessFile inFile) throws IOException {
        this(inFile, DEFAULT_BUFSIZE);
    }

    /**
     * Construct RandomAccessInputStream from filename.
     *
     * @param file to read with random access.
     * @param mode open mode.
     * @exception IOException if an I/O error has occurred.
     */
    public RandomAccessInputStream(final String file, final String mode) throws IOException {
        this(new RandomAccessFile(file, mode));
    }

    /**
     * Get an unique FileChannel Object related to the file.
     * @return FileChannel object.
     */
    public final FileChannel getChannel() {
        return fileChannel;
    }

    @Override
    public final int available() throws IOException {
        long available =  length() - position();
        if (available > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) available;
    }

    @Override
    public final void close() throws IOException {
        in.close();
        fileChannel = null;
    }

    /**
     * Get file length.
     *
     * @return length of file in byte.
     * @exception IOException if an I/O error has occurred.
     */
    public final long length() throws IOException {
        return fileChannel.size();
    }

    public final int getLength() throws IOException {
        return (int) length();
    }


    /**
     * Get cursor position.
     *
     * @return position in byte.
     * @exception IOException if an I/O error has occurred.
     */
    public final long position() throws IOException {
        return currentpos;
    }

    @Deprecated
    public final int getPos() throws IOException {
        return (int) position();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void mark(final int markpos) {
        mark = currentpos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean markSupported() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized int read() throws IOException {
        int c = read(currentpos);
        if (c == -1) {
            return -1;
        }
        currentpos++;
        return c;
    }

    /**
     * Read one byte from specified position.
     * This method does not modify this stream's position.
     * If the given position is greater than the file's current size then no bytes are read and return -1.
     * @param pos position to read.
     * @return -1 when position is greater than the file's current size, otherwise byte value.
     */
    public synchronized int read(long pos) {
        if (pos < startpos || pos > endpos) {
            long blockstart = (pos/ bufsize) * bufsize;
            int n = 0;
            try {
                fileChannel.position(blockstart);
                byteBuffer.clear();
                n += fileChannel.read(byteBuffer);
            } catch (IOException e) {
                return -1;
            }
            byteBuffer.flip();
            startpos = blockstart;
            endpos = blockstart + n - 1;
            if (pos < startpos || pos > endpos) {
                return -1;
            }
        }
        return byteBuffer.get((int) (pos - startpos)) & 0xff;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int read(final byte @NotNull [] buf, final int off, final int len) throws IOException {
        if (currentpos < startpos || currentpos > endpos) {
            long blockstart = (currentpos / bufsize) * bufsize;
            long n = 0;
            try {
                fileChannel.position(blockstart);
                byteBuffer.clear();
                n += fileChannel.read(byteBuffer);
            } catch (IOException e) {
                return -1;
            }
            startpos = blockstart;
            endpos = blockstart + n - 1;
            if (currentpos < startpos || currentpos > endpos) {
                return -1;
            }
        }
        byteBuffer.position((int) (currentpos - startpos));
        int size = Math.min(Math.min(len, (int)(length() - currentpos)), byteBuffer.remaining());
        byteBuffer.get(buf, off, size);
        currentpos += size;
        return size;
    }

    /**
     * Read full data to byte buffer.
     *
     * @param buf buffer to store data.
     * @exception IOException if an I/O error has occurred.
     */
    public final void readFully(final byte[] buf) throws IOException {
        int offset = read(buf, 0, buf.length);
        while (offset < buf.length) {
            offset += read(buf, offset, buf.length - offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final synchronized void reset() {
        currentpos = mark;
    }

    /**
     * Seek file position.
     *
     * @param pos file position in byte.
     */
    public final void seek(final long pos) throws IOException {
        if (pos < 0) {
            currentpos = 0;
        } else {
            currentpos = Math.min(fileChannel.size(), pos);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long skip(final long size) throws IOException {
        long fileSize = fileChannel.size();
        if (size < 0 && currentpos + size < 0) {
            currentpos = 0;
        } else if (fileSize - size < currentpos) {
            currentpos = fileSize;
        } else {
            currentpos += size;
        }
        return currentpos;
    }
}
