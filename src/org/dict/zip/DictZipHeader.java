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
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Deflater;

/**
 * DictZip header structure and handler.
 *
 * @author jdictd project
 * @author Hiroshi Miura
 */
public class DictZipHeader {

    /**
     * Each chunk size.
     */
    protected int[] chunks;

    private int headerLength;
    private static final int GZIPFLAG_SIZE = 16;
    private final BitSet gzipFlag = new BitSet(GZIPFLAG_SIZE);
    private OperatingSystem headerOS = OperatingSystem.FAT;
    private CompressionFlag extraFlag;
    private long[] offsets;
    private int extraLength;
    private byte subfieldID1;
    private byte subfieldID2;
    private int subfieldLength;
    private int subfieldVersion;
    private int chunkLength;
    private int chunkCount;
    private long mtime;
    private String filename = null;
    private String comment = null;

    /**
     * GZIP magic number & flag bits.
     */
    private static final int GZIP_MAGIC = 0x8b1f;
    private static final int FTEXT = 0;      // Content is text(optional)
    private static final int FHCRC = 1;      // Header CRC
    private static final int FEXTRA = 2;     // Extra field
    private static final int FNAME = 3;      // File name
    private static final int FCOMMENT = 4;  // File comment

    /**
     * Header fields length.
     */
    private static final int DICTZIP_HEADER_LEN = 10;
    /* 2 bytes header magic, 1 byte compression method, 1 byte flags
     4 bytes time, 1 byte extra flags, 1 byte OS */

    /**
     * Other constants.
     */
    private static final int BUFLEN = 58315; // Same as C implementation

    /**
     * Default constructor.
     */
    private DictZipHeader() {
    }

    /**
     * Initialize DictZip header from data and buffer size.
     * Constructor for writing dictzip file.
     * @param dataSize total data size.
     * @param bufferSize buffer size.
     */
    public DictZipHeader(final long dataSize, final int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("Buffer size is zero or minus.");
        }
        long tmpCount = dataSize / bufferSize;
        if (dataSize % bufferSize > 0) {
            tmpCount++;
        }
        if (tmpCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("data size is out of DictZip range.");
        }
        gzipFlag.set(FEXTRA);
        extraFlag = CompressionFlag.NORMAL;
        /*
         * Extra Field
         * +---+---+---+---+==================================+
         * |SI1|SI2|  LEN  |... LEN bytes of subfield data ...|
         * +---+---+---+---+==================================+
         */
        subfieldID1 = 'R';
        subfieldID2 = 'A';
        subfieldLength =  6 + (int) tmpCount * 2;
        /*
         * Random Access Field
         * +---+---+---+---+---+---+===============================+
         * |  VER  | CHLEN | CHCNT |  ... CHCNT words of data ...  |
         * +---+---+---+---+---+---+===============================+
         */
        subfieldVersion = 1;
        chunkLength = bufferSize;
        chunkCount = (int) tmpCount;
        chunks = new int[chunkCount];
        // Calculate total length
        extraLength = subfieldLength + 4;
        headerLength = DICTZIP_HEADER_LEN + extraLength;
    }

    private void initOffsets() {
        offsets = new long[chunks.length];
        offsets[0] = headerLength;
        for (int i = 1; i < chunks.length; i++) {
            offsets[i] = offsets[i - 1] + chunks[i - 1];
        }
    }

    /**
     * Read dictzip header from file.
     *
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
     *
     * @param is input stream for retrieve header.
     * @param crc CRC32 value for check.
     * @return dictzip header object.
     * @throws IOException when error in file read.
     */
    public static DictZipHeader readHeader(final InputStream is, final CRC32 crc)
            throws IOException {
        DictZipHeader h = new DictZipHeader();
        readHeader(h, is, crc);
        return h;
    }

    /**
     * Read dictzip header from file.
     *
     * @param h return dictzip header values.
     * @param is input stream for retrieve header.
     * @param crc CRC32 value for check.
     * @throws IOException when error in file read.
     */
    private static void readHeader(final DictZipHeader h, final InputStream is, final CRC32 crc)
            throws IOException {
        CheckedInputStream in = new CheckedInputStream(is, crc);
        crc.reset();

        // Check header magic
        if (readUShort(in) != GZIP_MAGIC) {
            throw new IOException("Not in GZIP format");
        }
        // Check compression method
        if (readUByte(in) != Deflater.DEFLATED) {
            throw new IOException("Unsupported compression method");
        }
        // Read flags
        int flg = readUByte(in);
        for (int i = 0; i < GZIPFLAG_SIZE; i++) {
            int testbit = 1 << i;
            if ((flg & testbit) == testbit) {
                h.gzipFlag.set(i);
            }
        }
        h.mtime = readUInt(in);
        int compFlg = readUByte(in);
        if (compFlg == 0x02) {
            h.extraFlag = CompressionFlag.BEST;
        } else if (compFlg == 0x04) {
            h.extraFlag = CompressionFlag.FASTEST;
        } else if (compFlg == 0x00) {
            h.extraFlag = CompressionFlag.NORMAL;
        } else {
            throw new IOException("Corrupt GZIP header");
        }
        int hos = readUByte(in);
        h.headerOS = OperatingSystem.UNKNOWN;
        for (OperatingSystem os: OperatingSystem.values()) {
            if (hos == os.value) {
                h.headerOS = os;
                break;
            }
        }
        h.headerLength = DICTZIP_HEADER_LEN;
        // Optional extra field
        if (h.gzipFlag.get(FEXTRA)) {
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
        if (h.gzipFlag.get(FNAME)) {
            StringBuilder sb = new StringBuilder();
            int ubyte;
            while ((ubyte = readUByte(in)) != 0) {
                sb.append((char) (ubyte & 0xff));
                h.headerLength++;
            }
            h.filename = sb.toString();
            h.headerLength++;
        }
        // Skip optional file comment
        if (h.gzipFlag.get(FCOMMENT)) {
            while (readUByte(in) != 0) {
                h.headerLength++;
            }
            h.headerLength++;
        }
        // Check optional header CRC
        if (h.gzipFlag.get(FHCRC)) {
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
     *
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
     *
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
     *
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
     * <p>
     * Does not assume that the input stream is capable of seeking.
     *
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
     *
     * @param h DictZipHeader header values.
     * @param buf buffer to write.
     */
    public static void writeHeader(final DictZipHeader h, final byte[] buf) {
        writeHeader(h, buf, 0);
    }

    /**
     * Writes GZIP member header to buffer.
     *
     * @param h DictZipHeader header values.
     * @param buf buffer to write.
     * @param offset in buffer.
     */
    public static void writeHeader(final DictZipHeader h, final byte[] buf, final int offset) {
        // XXX: implement me
    }

    /**
     * Writes GZIP member header.
     *
     * @param h DictZipHeader header values.
     * @param out output stream to write.
     * @throws java.io.IOException when error in file output.
     */
    public static void writeHeader(final DictZipHeader h, final OutputStream out)
            throws IOException {
        CRC32 headerCrc = new CRC32();
        headerCrc.reset();
        writeShort(out, GZIP_MAGIC);     // Magic number
        out.write(Deflater.DEFLATED);    // Compression method (CM)
        out.write(FEXTRA);               // Flags (FLG)
        writeInt(out, 0);                // Modification time (MTIME)
        out.write(0);                    // Extra flags (XFL)
        out.write(0);                    // Operating system (OS)
        writeShort(out, h.extraLength);  // extra field length
        out.write(h.subfieldID1);
        out.write(h.subfieldID2);        // subfield ID
        writeShort(out, h.subfieldLength);  // extra field length
        writeShort(out, h.subfieldVersion); // extra field length
        writeShort(out, h.chunkLength);  // extra field length
        writeShort(out, h.chunkCount);   // extra field length
        if (h.gzipFlag.get(FHCRC)) {
            ByteBuffer bb = ByteBuffer.allocate(22);
            bb.putShort((short)GZIP_MAGIC);
            bb.put((byte)FEXTRA);
            bb.putInt(0);
            bb.put((byte)0);
            bb.put((byte)0);
            bb.putShort((short)h.extraLength);
            bb.put((byte)h.subfieldID1);
            bb.put((byte)h.subfieldID2);
            bb.putShort((short)h.subfieldLength);
            bb.putShort((short)h.subfieldVersion);
            bb.putShort((short)h.subfieldLength);
            bb.putShort((short)h.subfieldVersion);
            bb.putShort((short)h.chunkLength);
            bb.putShort((short)h.chunkCount);
            headerCrc.update(bb.array());
        }
        for (int i = 0; i < h.chunkCount; i++) {
            writeShort(out, h.chunks[i]);
        }
        if (h.gzipFlag.get(FHCRC)) {
            for (int i = 0; i < h.chunkCount; i++) {
                headerCrc.update(ByteBuffer.allocate(2).putShort((short)h.chunks[i]).array());
            }
        }
        if (h.gzipFlag.get(FNAME)) {
            if (h.filename != null) {
                out.write(h.filename.getBytes());
                if (h.gzipFlag.get(FHCRC)) {
                    headerCrc.update(h.filename.getBytes());
                }
            }
            out.write(0);
        }
        if (h.gzipFlag.get(FCOMMENT)) {
            if (h.comment != null) {
                out.write(h.comment.getBytes());
                if (h.gzipFlag.get(FHCRC)) {
                    headerCrc.update(h.comment.getBytes());
                }
            }
            out.write(0);
        }
        if (h.gzipFlag.get(FHCRC)) {
            writeShort(out, (int)headerCrc.getValue());
        }
    }

    /**
     * Writes integer in Intel byte order.
     *
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
     *
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
     *
     * @param start total offset bytes.
     * @return offset in the chunk.
     */
    public final int getOffset(final long start) throws IllegalArgumentException {
        long off = start % this.chunkLength;
        if (off < Integer.MAX_VALUE) {
            return (int) off;
        } else {
            throw new IllegalArgumentException("Index is out of boundary.");
        }
    }

    /**
     * Return dictionary position.
     *
     * @param start total offset bytes.
     * @return chunk position.
     */
    public final long getPosition(final long start) throws IllegalArgumentException {
        long idx = start / this.chunkLength;
        if (idx < Integer.MAX_VALUE) {
            return this.offsets[(int) idx];
        } else {
            throw new IllegalArgumentException("Index is out of boudary.");
        }
    }

    /**
     *　Return zip type, whether gzip or dzip.
     * @return type name.
     */
    public final String getType() {
        if (subfieldID1 == 'R' && subfieldID2 == 'A') {
            return "dzip";
        } else {
            return "gzip";
        }
    }

    /**
     * Return chunk length.
     * @return length in int.
     */
    public int getChunkLength() {
        return chunkLength;
    }

    /**
     * Return chunk count.
     * @return int number.
     */
    public int getChunkCount() {
        return chunkCount;
    }

    /**
     * Return modification date/time in second from epoch.
     * @return long second from epoch.
     */
    public long getMtime() {
        return mtime;
    }

    /**
     * Return filename set to header.
     * @return filename set to header.
     */
    public String getFilename() {
        if (gzipFlag.get(FNAME)) {
            return filename;
        } else {
            return null;
        }
    }

    public void setHeaderOS(OperatingSystem os) {
        this.headerOS = os;
    }

    public OperatingSystem getHeaderOS() {
        return headerOS;
    }

    public void setExtraFlag(CompressionFlag flag) {
        this.extraFlag = flag;
    }

    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    public void setFilename(String filename) {
        if (filename != null && !filename.isEmpty()) {
            this.filename = filename;
            gzipFlag.set(FNAME);
        }
    }

    public int getHeaderLength() {
        return headerLength;
    }

    public enum CompressionFlag {
        NORMAL(0), BEST(2), FASTEST(4);
        private int value;

        private CompressionFlag(int value) {
            this.value = value;
        }
    }

    public enum OperatingSystem {
        FAT(0), AMIGA(1), VMS(2), UNIX(3), VMCMS(4), ATARI(5), HPFS(6), MAC(7), ZSYS(8),
        CPM(9), TOPS(10), NTFS(11), QDOS(12), ACORN(13), UNKNOWN(255);
        private int value;

        private OperatingSystem(int value) {
            this.value = value;
        }
    }
}
