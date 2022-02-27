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

import java.io.EOFException;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;


/**
 * DictZipInputStream.
 *
 * @author Ho Ngoc Duc
 * @author Hiroshi Miura
 */
public class DictZipInputStream extends InflaterInputStream {

    /**
     * DictZip Header.
     */
    private DictZipHeader header;

    /**
     * CRC-32 for uncompressed data.
     */
    private final CRC32 crc = new CRC32();

    private long crcVal = 0;
    private long totalLength = 0;
    private long compLength = 0;

    private int offset = 0;
    private long rawOffset = 0;

    private static final int BUF_LEN = 8192;

    private int markOffset = -1;
    private long mark;

    /**
     * Indicates end of input stream.
     */
    private boolean eos;

    private FileChannel fileChannel;

    /*
     * Super class has three protected variables.
     * protected byte[] buf
     *                         Input buffer for decompression.
     * protected Inflater inf
     *                         Decompressor for this stream.
     * protected int len
     *                         Length of input buffer.
     *
     * We should not use these names in order to avoid confusion.
     */

    /**
     * Creates a new input stream with a default buffer size from given filepath.
     *
     * @param filename the input filename
     * @exception IOException if an I/O error has occurred
     */
    public DictZipInputStream(final String filename) throws IOException {
        this(new RandomAccessInputStream(filename, "r"), BUF_LEN);
    }

    /**
     * Creates a new input stream with a default buffer size.
     *
     * @param in the input stream
     * @exception IOException if an I/O error has occurred
     */
    public DictZipInputStream(final RandomAccessInputStream in) throws IOException {
        this(in, BUF_LEN);
    }

    /**
     * Creates a new input stream with the specified buffer size.
     *
     * @param in the input stream
     * @param size the input buffer size
     * @exception IOException if an I/O error has occurred
     */
    public DictZipInputStream(final RandomAccessInputStream in, final int size) throws IOException {
        super(in, new Inflater(true), size);
        fileChannel = in.getChannel();
        header = readHeader();
        long pos = in.position();
        readTrailer();
        in.seek(pos);
    }

    /**
     * Closes the input stream.
     *
     * @exception IOException if an I/O error has occurred
     */
    @Override
    public final void close() throws IOException {
        inf.end();
        in.close();
        rawOffset = -1L;
        eos = true;
        fileChannel = null;
    }

    /**
     * Get raw content offset in bytes.
     * @return offset.
     */
    public final long position() {
        return rawOffset;
    }

    @Override
    public final boolean markSupported() {
        return true;
    }

    @Override
    public final void mark(final int markOffset) {
        if (markOffset < 0) {
            throw new IllegalArgumentException("markOffset should be positive number.");
        }
        this.markOffset = markOffset;
        mark = position();
    }

    @Override
    public final void reset() throws IOException {
        if (markOffset == -1 || position() > mark + markOffset || position() < mark - markOffset) {
            throw new IOException("Cannot reset to mark because offset overcome.");
        }
        seek(mark);
    }

    /**
     * Reads uncompressed data into an array of bytes. Blocks until enough input is available for
     * decompression.
     *
     * @param buffer the buffer into which the data is read
     * @param off the start offset of the data
     * @param size the maximum number of bytes read
     * @return the actual number of bytes read, or -1 if the end of the compressed input stream is
     * reached
     * @exception IOException if an I/O error has occurred or the compressed input data is corrupt
     */
    @Override
    public final int read(final byte[] buffer, final int off, final int size) throws IOException {
        if (eos) {
            return -1;
        }
        if (buffer == null) {
            throw new NullPointerException();
        } else if (off < 0 || size < 0 || size > buffer.length - off || off >= buffer.length) {
            throw new IndexOutOfBoundsException();
        } else if (size == 0) {
            return 0;
        }
        // skip to offset
        if (offset > 0) {
            int total;
            int len;
            byte[] b = new byte[BUF_LEN];
            for (total = 0; total < offset; total += len) {
                len = offset - total;
                if (len > b.length) {
                    len = b.length;
                }

                len = super.read(b, 0, len);
                if (len == -1) {
                    eos = true;
                    return -1;
                }
            }
            offset = 0;
        }
        // read for buffer size.
        int readLen = super.read(buffer, off, size);
        if (readLen == -1) {
            eos = true;
        } else {
            crc.update(buffer, off, readLen);
            rawOffset += readLen;
        }
        // check mark/markOffset
        if (markOffset >= 0) {
            if (position() > mark + markOffset) {
                markOffset = -1;
            }
        }
        return readLen;
    }

    /**
     * Read full data.
     *
     * @param buffer the buffer into which the data is read
     * @exception IOException if an I/O error has occurred or the compressed input data is corrupt
     */
    public final void readFully(final byte[] buffer) throws IOException {
        readFully(buffer, 0, buffer.length);
    }

    /**
     * Read full data by offset/length.
     *
     * @param buffer the buffer into which the data is read
     * @param off offset
     * @param size length
     * @exception IOException if an I/O error has occurred or the compressed input data is corrupt
     */
    public final void readFully(final byte[] buffer, final int off, final int size)
            throws IOException {
        int num = 0;
        while (num < size) {
            int count = read(buffer, off + num, size - num);
            if (count < 0) {
                throw new EOFException();
            }
            num += count;
            rawOffset += count;
        }
    }

    /**
     * Read dictzip header.
     *
     * @return header object.
     * @exception IOException if an I/O error has occurred.
     */
    private DictZipHeader readHeader() throws IOException {
        if (header == null) {
            header = DictZipHeader.readHeader(fileChannel, crc);
            crc.reset();
        }
        return header;
    }

    /**
     * Seek to a raw index next.
     * @param next a raw index
     * @throws IOException when instance is not a RandomAccessInputStream.
     */
    public void seek(final long next) throws IOException {
        rawOffset = next;
        if (in instanceof RandomAccessInputStream) {
            RandomAccessInputStream rain = (RandomAccessInputStream) in;
            offset = header.getOffset(next);
            long pos = header.getPosition(next);
            rain.seek(pos);
            inf.reset();
            eos = false;
        } else {
            throw new IOException("Illegal type of InputStream.");
        }
    }

    /**
     * Return CRC value set to gzip trailer.
     * @return CRC value.
     * @throws IOException if I/O error.
     */
    public long getCrc() throws IOException {
        if (totalLength == 0) {
            readTrailer();
        }
        return crcVal;
    }

    /**
     * Return length value set to gzip trailer.
     * @return data length.
     * @throws IOException if I/O error.
     */
    public long getLength() throws IOException {
        if (totalLength == 0) {
            readTrailer();
        }
        return totalLength;
    }

    /**
     * Get total length of compressed data.
     * @return total length
     * @throws IOException when I/O error at trailer reading.
     */
    public long getCompLength() throws IOException {
        if (totalLength == 0) {
            readTrailer();
        }
        return compLength;
    }

    /**
     * Get type of compression.
     *
     * @return "DZIP" or "GZIP"
     * @throws IOException if I/O error occurred.
     */
    public String getType() throws IOException {
        return header.getType();
    }

    /**
     * Get length of each chunk.
     * @return size of chunk.
     * @throws IOException if I/O error occurred.
     */
    public int getChunkLength() throws IOException {
        return header.getChunkLength();
    }

    /**
     * Get number of chunks.
     * @return number of chunks.
     * @throws IOException if I/O error occurred.
     */
    public int getChunkCount() throws IOException {
        return header.getChunkCount();
    }

    /**
     * Get mtime in long.
     * @return mtime in long.
     * @throws IOException if I/O error occurred.
     */
    public long getMtime() throws IOException {
        return header.getMtime();
    }

    /**
     * Get Filename field if exist.
     * @return filename or null.
     * @throws IOException if I/O error occurred.
     */
    public String getFilename() throws IOException {
        return header.getFilename();
    }

    /**
     * Reads GZIP member trailer.
     * @throws java.io.IOException If file I/O error
     */
    void readTrailer() throws IOException {
        compLength = fileChannel.size();
        fileChannel.position(compLength - 8);
        ByteBuffer buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        fileChannel.read(buf);
        buf.flip();
        crcVal = buf.getInt() & 0xffffffffL;
        totalLength = buf.getInt() & 0xffffffffL;
    }
}
