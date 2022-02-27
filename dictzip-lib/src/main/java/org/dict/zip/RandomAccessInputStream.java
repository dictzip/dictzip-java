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

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * RandomAccessInputStream.
 *
 * @author Ho Ngoc Duc
 * @author Hiroshi Miura
 */
public class RandomAccessInputStream extends InputStream {

    private RandomAccessFile in;

    private static final int DEFAULT_BUFSIZE = 8192;
    private ByteBuffer byteBuffer;
    private FileChannel fileChannel;

    private long mark = 0;
    private long currentpos = 0;
    private long startpos = -1;
    private long endpos = -1;
    private int bufsize;

    public RandomAccessInputStream(final RandomAccessFile inFile, final int bufsize) throws IOException {
        this.in = inFile;
        this.bufsize = bufsize;
        fileChannel = inFile.getChannel();
        byteBuffer = ByteBuffer.allocate(bufsize);
    }

    /**
     * Construct RandomAccessInputStream from file.
     *
     * @param inFile RandomAccessFile
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
        try {
            mark = position();
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
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
    public final int read() throws IOException {
        return read(currentpos++);
    }

    public final synchronized int read(final long pos) throws IOException {
        if (pos < startpos || pos > endpos) {
            long blockstart = (pos/ bufsize) * bufsize;
            long n = 0;
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
    public final int read(final byte[] buf, final int off, final int len) throws IOException {
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
        int size;
        if (len > byteBuffer.remaining()) {
            size = byteBuffer.remaining();
            byteBuffer.get(buf, off, size);
        } else {
            size = len;
            byteBuffer.get(buf, off, len);
        }
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
    public final synchronized void reset() throws IOException {
        currentpos = mark;
    }

    /**
     * Seek file position.
     *
     * @param pos file position in byte.
     * @exception IOException if an I/O error has occurred.
     */
    public final void seek(final long pos) throws IOException {
        currentpos = pos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final long skip(final long size) throws IOException {
        long fileSize = fileChannel.size();
        if (currentpos + size > fileSize) {
            currentpos = fileSize;
        } else {
            currentpos += size;
        }
        return currentpos;
    }
}
