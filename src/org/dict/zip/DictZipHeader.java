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

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;

/**
 * DictZip header structure and handler.
 * @author jdictd project
 * @author Hiroshi Miura
 */
public class DictZipHeader {

        /** Length of header. */
        protected int headerLength;
        /** Each chunk size. */
        protected int[] chunks;
        /** Offsets of each chunks. */
        protected int[] offsets;
        /** Length of extra fields. */
        protected int extraLength;
        /** Subfield ID. */
        protected byte subfieldID1;
        /** Subfield ID. */
        protected byte subfieldID2;
        /** Length of subfield. */
        protected int subfieldLength;
        /** Version of subfield. */
        protected int subfieldVersion;
        /** Length of each chunk. */
        protected int chunkLength;
        /** Count of chunks. */
        protected int chunkCount;

        /**
         * GZIP header magic number & file header flags.
         */
        private static final int GZIP_MAGIC = 0x8b1f;
        private static final int FTEXT = 1;      // Extra text
        private static final int FHCRC = 2;      // Header CRC
        private static final int FEXTRA = 4;     // Extra field
        private static final int FNAME = 8;      // File name
        private static final int FCOMMENT = 16;  // File comment

        /**
         * Other header magic numbers.
         */
        private static final int DICTZIP_COMPRESSION = 8;

        /**
         * Header fields length.
         */
        private static final int HEADER_MAGIC_LEN = 2;
        private static final int COMPRESSION_FLAG_LEN = 1;
        private static final int FLAG_LEN = 1;
        private static final int MTIME_LEN = 4;
        private static final int XFL_LEN = 1;
        private static final int OS_LEN = 1;
        private static final int DICTZIP_HEADER_LEN = 10;
        /* 2 bytes header magic, 1 byte compression method, 1 byte flags
           4 bytes time, 1 byte extra flags, 1 byte OS */

        /** Other constants. */
        private static final int BUFLEN = 128;

        private void initOffsets() {
                offsets = new int[chunks.length];
                offsets[0] = headerLength;
                for (int i = 1; i < chunks.length; i++) {
                        offsets[i] = offsets[i - 1] + chunks[i - 1];
                }
        }

        /**
         * Read dictzip header from file.
         * @param s Filename
         * @return DictZipHeader object.
         * @throws IOException when error in file read.
         */
        public static DictZipHeader readHeader(final String s) throws IOException {
                DictZipHeader h = new DictZipHeader();
                CRC32 crc = new CRC32();
                InputStream in = new FileInputStream(s);
                readHeader(h, in, crc);
                in.close();
                return h;
        }

        /**
         * Read dictzip header from file.
         * @param h return dictzip header values.
         * @param is input stream for retrieve header.
         * @param crc CRC32 value for check.
         * @throws IOException when error in file read.
         */
        public static void readHeader(final DictZipHeader h, final InputStream is, final CRC32 crc)
                throws IOException {
                CheckedInputStream in = new CheckedInputStream(is, crc);
                crc.reset();

                // Check header magic
                if (readUShort(in) != GZIP_MAGIC) {
                        throw new IOException("Not in GZIP format");
                }
                // Check compression method
                if (readUByte(in) != DICTZIP_COMPRESSION) {
                        throw new IOException("Unsupported compression method");
                }
                // Read flags
                int flg = readUByte(in);
                // Skip MTIME, XFL, and OS fields
                skipBytes(in, MTIME_LEN + XFL_LEN + OS_LEN);
                h.headerLength = DICTZIP_HEADER_LEN;
                // Optional extra field
                if ((flg & FEXTRA) == FEXTRA) {
                        h.extraLength = readUShort(in);
                        h.headerLength += h.extraLength + 2;
                        h.subfieldID1 = (byte) readUByte(in);
                        h.subfieldID2 = (byte) readUByte(in);
                        h.subfieldLength = readUShort(in); // 2 bytes subfield length
                        h.subfieldVersion = readUShort(in); // 2 bytes subfield version
                        h.chunkLength = readUShort(in); // 2 bytes chunk length
                        h.chunkCount = readUShort(in); // 2 bytes chunk count
                        h.chunks = new int[h.chunkCount];
                        for (int i = 0; i < h.chunkCount; i++) {
                                h.chunks[i] = readUShort(in);
                        }
                }
                // Skip optional file name
                if ((flg & FNAME) == FNAME) {
                        while (readUByte(in) != 0) {
                                h.headerLength++;
                        }
                        h.headerLength++;
                }
                // Skip optional file comment
                if ((flg & FCOMMENT) == FCOMMENT) {
                        while (readUByte(in) != 0) {
                                h.headerLength++;
                        }
                        h.headerLength++;
                }
                // Check optional header CRC
                if ((flg & FHCRC) == FHCRC) {
                        int v = (int) crc.getValue() & 0xffff;
                        if (readUShort(in) != v) {
                                throw new IOException("Corrupt GZIP header");
                        }
                        h.headerLength += 2;
                }
                h.initOffsets();
        }

        /**
         * Reads unsigned byte.
         * @param in input stream to read.
         * @return unsigned byte value.
         * @throws java.io.IOException when error in file reading.
         */
        public static int readUByte(final InputStream in) throws IOException {
                int b = in.read();
                if (b == -1) {
                        throw new EOFException();
                }
                return b;
        }

        /**
         * Reads unsigned integer in Intel byte order.
         * @param in input stream to read.
         * @return unsigned integer value.
         * @throws java.io.IOException when error in file reading.
         */
        public static long readUInt(final InputStream in) throws IOException {
                long s = readUShort(in);
                return ((long) readUShort(in) << 16) | s;
        }

        /**
         * Reads unsigned short in Intel byte order.
         * @param in input stream to read.
         * @return unsigned short value.
         * @throws java.io.IOException when error in file reading.
         */
        public static int readUShort(final InputStream in) throws IOException {
                int b = readUByte(in);
                return ((int) readUByte(in) << 8) | b;
        }

        /**
         * Skips bytes of input data blocking until all bytes are skipped.
         * <p>Does not assume that the input stream is capable of seeking.
         * @param in input stream to skip
         * @param size byte number to skip
         * @throws java.io.IOException when error in file reading.
         */
        public static void skipBytes(final InputStream in, final int size) throws IOException {
                byte[] buf = new byte[BUFLEN];
                int n = size;
                while (n > 0) {
                        int len = in.read(buf, 0, min(n, buf.length));
                        if (len == -1) {
                                throw new EOFException();
                        }
                        n -= len;
                }
        }

        private static int min(final int numa, final int numb) {
                if (numa > numb) {
                        return numb;
                } else {
                        return numa;
                }
        }

        @Override
        public final String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("\nHeader length = ").append(headerLength);
                sb.append("\nSubfield ID = ").append((char) subfieldID1).append((char) subfieldID2);
                sb.append("\nSubfield length = ").append(subfieldLength);
                sb.append("\nSubfield version = ").append(subfieldVersion);
                sb.append("\nChunk length = ").append(chunkLength);
                sb.append("\nNumber of chunks = ").append(chunkCount);
                return sb.toString();
        }

        /**
         * Writes GZIP member header to buffer.
         * @param h DictZipHeader header values.
         * @param b byte buffer.
         */
        public static void writeHeader(final DictZipHeader h, final byte[] buf, final int offset) {
                // XXX: implement me
        }

        /**
         * Writes GZIP member header.
         * @param h DictZipHeader header values.
         * @param out output stream to write.
         * @throws java.io.IOException when error in file output.
         */
        public static void writeHeader(final DictZipHeader h, final OutputStream out)
                throws IOException {
                writeShort(out, GZIP_MAGIC);     // Magic number
                out.write(Deflater.DEFLATED);    // Compression method (CM)
                out.write(FEXTRA);               // Flags (FLG)
                writeInt(out, 0);                // Modification time (MTIME)
                out.write(0);                    // Extra flags (XFL)
                out.write(0);                    // Operating system (OS)
                writeShort(out, h.extraLength);  // extra field length
                out.write(h.subfieldID1);
                out.write(h.subfieldID2);        // subfield ID
                writeShort(out, h.extraLength);  // extra field length
                writeShort(out, h.subfieldVersion); // extra field length
                writeShort(out, h.chunkLength);  // extra field length
                writeShort(out, h.chunkCount);   // extra field length
                for (int i = 0; i < h.chunkCount; i++) {
                        writeShort(out, h.chunks[i]);
                }
        }

        /**
         * Writes integer in Intel byte order.
         * @param out output stream to write.
         * @param i integer to write.
         * @throws java.io.IOException when error in file output.
         */
        public static void writeInt(final OutputStream out, final int i) throws IOException {
                writeShort(out, i & 0xffff);
                writeShort(out, (i >> 16) & 0xffff);
        }

        /**
         * Writes short integer in Intel byte order.
         * @param out output stream to write.
         * @param s short integer to write.
         * @throws java.io.IOException when error in file output.
         */
        public static void writeShort(final OutputStream out, final int s) throws IOException {
                out.write(s & 0xff);
                out.write((s >> 8) & 0xff);
        }

        /**
         * Offset getter.
         * @param start total offset bytes.
         * @return  offset in the chunk.
         */
        public final int getOffset(final int start) {
                return start % this.chunkLength;
        }

        /**
         * Return dictionary position.
         * @param start total offset bytes.
         * @return chunk position.
         */
        public final int getPosition(final int start) {
                int idx = start / this.chunkLength;
                return this.offsets[idx];
        }
}

