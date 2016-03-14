/*
 * DictZip library.
 *
 * Copyright (C) 2001-2004 JDictd project.
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

import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * DictZipInputStream.
 *
 * @author jdictd project
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
    public DictZipInputStream(final InputStream in) throws IOException {
        this(in, 512);
    }

    /**
     * Creates a new input stream with the specified buffer size.
     *
     * @param in the input stream
     * @param size the input buffer size
     * @exception IOException if an I/O error has occurred
     */
    public DictZipInputStream(final InputStream in, final int size) throws IOException {
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
    public final int read(final byte[] buf, final int off, final int size) throws IOException {
        if (eos) {
            return -1;
        }
        int len = super.read(buf, off, size);
        if (len == -1) {
            //readTrailer();
            eos = true;
        } else {
            crc.update(buf, off, len);
        }
        return len;
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

    public long getCompLength() throws IOException {
        if (totalLength == 0) {
            readTrailer();
        }
        return compLength;
    }

    private void checkTrailer() throws IOException {
        InputStream in = this.in;
        int num = inf.getRemaining();
        if (num > 0) {
            in = new SequenceInputStream(
                    new ByteArrayInputStream(buf, len - num, num), in);
        }
        long val = crc.getValue();
        long crcVal = readUInt(in);
        if (crcVal != val) {
            throw new IOException("Incorrect CRC");
        }
        long total = inf.getTotalOut();
        long trailerTotal = readUInt(in);
        //System.out.println("Computed CRC = "+v+" / From input "+crcVal);
        //System.out.println("Computed size = "+total+" / From input "+trailerTotal);
        if (trailerTotal != total) {
            throw new IOException("False number of uncompressed bytes");
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
            crcVal = readUInt(in);
            totalLength = readUInt(in);
        } else {
            // FIXME
            System.err.println("Ask to read gzip trailer when stream is not random accessable.");
        }
    }

    private long readUInt(final InputStream in) throws IOException {
        return DictZipHeader.readUInt(in);
    }
}
