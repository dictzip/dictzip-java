/*
 * DictZip library.
 *
 * Copyright (C) 2001-2004 Ho Ngoc Duc
 * Copyright (C) 2016 Hiroshi Miura
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

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.text.MessageFormat;

import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import static org.dict.zip.DictZipFileUtils.readUInt;


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
    private DictZipHeader header = null;

    /**
     * CRC-32 for uncompressed data.
     */
    private CRC32 crc = new CRC32();

    private long crcVal = 0;
    private long totalLength = 0;
    private long compLength = 0;

    private int offset = 0;

    /**
     * Indicates end of input stream.
     */
    private boolean eos;

    /**
     * Creates a new input stream with a default buffer size.
     *
     * @param in the input stream
     * @exception IOException if an I/O error has occurred
     */
    public DictZipInputStream(final RandomAccessInputStream in) throws IOException {
        this(in, 512);
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
        header = readHeader();
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
        eos = true;
    }

    /**
     * Reads uncompressed data into an array of bytes. Blocks until enough input is available for
     * decompression.
     *
     * @param buf the buffer into which the data is read
     * @param off the start offset of the data
     * @param size the maximum number of bytes read
     * @return the actual number of bytes read, or -1 if the end of the compressed input stream is
     * reached
     * @exception IOException if an I/O error has occurred or the compressed input data is corrupt
     */
    @Override
    public final int read(final byte[] buf, final int off, final int size) throws IOException {
        if (eos) {
            return -1;
        }
        if (buf == null) {
            throw new NullPointerException();
        } else if (off < 0 || size < 0 || size > buf.length - off || off >= buf.length) {
            throw new IndexOutOfBoundsException();
        } else if (size == 0) {
            return 0;
        }
        int readLen;
        if (offset == 0) {
            readLen = super.read(buf, off, size);
            if (readLen == -1) {
                eos = true;
            } else {
                crc.update(buf, off, readLen);
            }
        } else {
            byte[] tmpBuf = new byte[Math.min(offset + size, offset + buf.length - off)];
            readLen = super.read(tmpBuf, 0, tmpBuf.length);
            readLen -= offset;
            if (readLen < 0) {
                eos = true;
                readLen = -1;
            } else {
                for (int i = 0; i < readLen; i++) {
                    buf[off + i] = tmpBuf[offset + i];
                }
                crc.update(buf, off, readLen);
            }
            offset = 0;
        }
        return readLen;
    }

    /**
     * Read full data.
     *
     * @param buf the buffer into which the data is read
     * @exception IOException if an I/O error has occurred or the compressed input data is corrupt
     */
    public final void readFully(final byte[] buf) throws IOException {
        readFully(buf, 0, buf.length);
    }

    /**
     * Read full data by offset/length.
     *
     * @param buf the buffer into which the data is read
     * @param off offset
     * @param len length
     * @exception IOException if an I/O error has occurred or the compressed input data is corrupt
     */
    public final void readFully(final byte[] buf, final int off, final int len)
            throws IOException {
        int num = 0;
        while (num < len) {
            int count = read(buf, off + num, len - num);
            if (count < 0) {
                throw new EOFException();
            }
            num += count;
        }
    }

    /**
     * Read dictzip header.
     *
     * @return header object.
     * @exception IOException if an I/O error has occurred.
     */
    public final DictZipHeader readHeader() throws IOException {
        if (header == null) {
            header = DictZipHeader.readHeader(in, crc);
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
        if (in instanceof RandomAccessInputStream) {
            RandomAccessInputStream rain = (RandomAccessInputStream) in;
            offset = header.getOffset(next);
            long pos = header.getPosition(next);
            rain.seek(pos);
            inf.reset();
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
     * Check gzip member trailer; CRC and length.
     * @throws IOException when CRC error or total length error.
     */
    private void checkTrailer() throws IOException {
        InputStream in = this.in;
        int num = inf.getRemaining();
        if (num > 0) {
            in = new SequenceInputStream(
                    new ByteArrayInputStream(buf, len - num, num), in);
        }
        long val = crc.getValue();
        long crcValue = readUInt(in);
        if (crcValue != val) {
            throw new IOException(MessageFormat
                    .format("Incorrect CRC: Computed CRC = %8x / From input %8x", val, crcValue));
        }
        long total = inf.getTotalOut();
        long trailerTotal = readUInt(in);
        if (trailerTotal != total) {
            throw new IOException(MessageFormat
                    .format("False number of uncompressed bytes: Computed size =%d / From input %d",
                            total, trailerTotal));
        }
    }

    /**
     * Reads GZIP member trailer.
     * @throws java.io.IOException If file I/O error
     */
    public void readTrailer() throws IOException {
        if (in instanceof RandomAccessInputStream) {
            RandomAccessInputStream rain = (RandomAccessInputStream) in;
            compLength = rain.getLength();
            rain.seek(compLength - 8);
            crcVal = readUInt(rain);
            totalLength = readUInt(rain);
        } else {
            throw new IOException("Illegal type of InputStream.");
        }
    }
}
