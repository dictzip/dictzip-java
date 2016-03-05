/*
 * DictZip library. 
 *
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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;


/**
 *
 * @author miurahr
 */
public class DictZipOutputStream extends FilterOutputStream {
        private boolean closed = false;

        protected Deflater def;
        protected byte[] buf;
        protected int cindex;

        protected final long dataSize;
        protected CRC32 crc;
        protected DictZipHeader header;

        boolean usesDefaultDeflater = false;

        public DictZipOutputStream(final OutputStream out, final long dataSize)
                        throws  IOException, IllegalArgumentException {
                this(out, 512, dataSize);
        }

        public DictZipOutputStream(final OutputStream out, final int bufferSize,
                        final long dataSize) throws  IOException, IllegalArgumentException {
                this(out, Deflater.DEFAULT_COMPRESSION, bufferSize, dataSize);
        }

        public DictZipOutputStream(final OutputStream out, final int level,
                        final int bufferSize, final long dataSize) throws IOException,
                        IllegalArgumentException {
                this(out, new Deflater(level, true), bufferSize, dataSize);
                usesDefaultDeflater = true;
        }

        public DictZipOutputStream(final OutputStream out, final Deflater def,
                        final int bufferSize, final long dataSize) throws IOException,
                        IllegalArgumentException {
                super(out);
                if (out == null || def == null) {
                        throw new NullPointerException();
                }
                if (bufferSize <= 0) {
                        throw new IllegalArgumentException("buffer size <= 0");
                }
                if (dataSize <= 0) {
                        throw new IllegalArgumentException("total data size <= 0");
                }
                
                this.def = def;
                buf = new byte[bufferSize];
                this.dataSize = dataSize;
                crc = new CRC32();

                header= new DictZipHeader();
                header.chunkLength = bufferSize;
                long tmpCount = dataSize / header.chunkLength;
                if (tmpCount > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("data size is out of DictZip range.");
                }
                header.chunkCount = (int) tmpCount;
                header.chunks = new int[header.chunkCount];
                header.extraLength = 10 + header.chunkCount * 2; // standard header + chunks*2
                header.headerLength = 10 + 2 + header.extraLength + 2; // SH+XLEN+extraLength+CRC
                writeHeader();
                crc.reset();
                cindex = 0;
        }

        /**
         * Closes the output stream.
         *
         * @exception IOException if an I/O error has occurred
         */
        public void close() throws IOException {
                if (!closed) {
                        finish();
                        if (usesDefaultDeflater) {
                                def.end();
                        }
                        out.close();
                        closed = true;
                }
        }

        /**
         * Writes next block of compressed data to the output stream.
         * @throws IOException if an I/O error has occurred
         */
        protected void deflate() throws IOException {
                // FIXME: handling chunk
                crc.update(buf, 0, buf.length);
                int len = def.deflate(buf, 0, buf.length);
                if (len > 0) {
                        out.write(buf, 0, len);
                        header.chunks[cindex] = len;
                        cindex++;
                }
        }

        /**
         * Writes an array of bytes to the compressed output stream.
         * <p>
         * This method will block until all the bytes are written.
         *
         * @param b the data to be written
         * @param off the start offset of the data
         * @param len the length of the data
         * @throws IOException if an I/O error has occurred
         */
        public synchronized void write(byte[] b, int off, int len) throws IOException {
                if (def.finished()) {
                        throw new IOException("write beyond end of stream");
                }
                if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
                        throw new IndexOutOfBoundsException();
                } else if (len == 0) {
                        return;
                }
                if (!def.finished()) {
                        // Deflate no more than stride bytes at a time.  This avoids
                        // excess copying in deflateBytes (see Deflater.c)
                        // as same as java.io.DeflaterOutputStream
                        int stride = buf.length;
                        for (int i = 0; i < len; i+= stride) {
                                def.setInput(b, off + i, Math.min(stride, len - i));
                                while (!def.needsInput()) {
                                        deflate();
                                }
                        }
                }
        }
        
        /**
         * Writes a byte to the compressed output stream.
         * <p>
         * This method will block until the byte can be written.
         *
         * @param b the byte to be written
         * @throws IOException if an I/O error has occurred
         */
        @Override
        public synchronized void write(int b) throws IOException {
                // FIXME: implement for chunk handling
                byte[] buf = new byte[1];
                buf[0] = (byte)(b & 0xff);
                write(buf, 0, 1);
        }

        private final static int TRAILER_SIZE = 8;

        public void finish() throws IOException {
                if (!def.finished()) {
                        def.finish();
                        while (!def.finished()) {
                                int len = def.deflate(buf, 0, buf.length);
                                if (def.finished() && len <= buf.length - TRAILER_SIZE) {
                                        // last deflater buffer. Fit trailer at the end
                                        writeTrailer(buf, len);
                                        len = len + TRAILER_SIZE;
                                        out.write(buf, 0, len);
                                        return;
                                }
                                if (len > 0) {
                                        out.write(buf, 0, len);
                                }
                        }
                }
                byte[] trailer = new byte[TRAILER_SIZE];
                writeTrailer(trailer, 0);
                out.write(trailer);
        }

        private void writeHeader() throws IOException {
                DictZipHeader.writeHeader(header, out);
        }

        private void writeHeader(byte[] b, int offset) {
                DictZipHeader.writeHeader(header, b, offset);
        }

        private void writeTrailer(byte[] b, int offset) throws IOException {
                writeInt((int) crc.getValue(), b, offset); // CRC-32 of uncompr. data
                writeInt(def.getTotalIn(), b, offset + 4); // Number of uncompr. bytes
        }

        /*
         * Writes integer in Intel byte order to a byte array, starting at a
         * given offset.
         */
        private void writeInt(int i, byte[] b, int offset) throws IOException {
                writeShort(i & 0xffff, b, offset);
                writeShort((i >> 16) & 0xffff, b, offset + 2);
        }

        /*
         * Writes short integer in Intel byte order to a byte array, starting
         * at a given offset
         */
        private void writeShort(int s, byte[] b, int offset) throws IOException {
                b[offset] = (byte) (s & 0xff);
                b[offset + 1] = (byte) ((s >> 8) & 0xff);
        }
}
