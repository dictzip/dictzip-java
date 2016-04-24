package org.dict.zip;

import java.io.*;
import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created by Hiroshi Miura on 16/04/09.
 */
public class DictZipFileUtils {


    /**
     * Reads unsigned byte.
     *
     * @param in input stream to read.
     * @return unsigned byte value.
     * @throws IOException when error in file reading.
     */
    static int readUByte(final InputStream in) throws IOException {
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
     * @throws IOException when error in file reading.
     */
    static long readUInt(final InputStream in) throws IOException {
        long s = readUShort(in);
        return ((long) readUShort(in) << 16) | s;
    }

    /**
     * Reads unsigned short in Intel byte order.
     *
     * @param in input stream to read.
     * @return unsigned short value.
     * @throws IOException when error in file reading.
     */
    static int readUShort(final InputStream in) throws IOException {
        int b = readUByte(in);
        return (readUByte(in) << 8) | b;
    }

    /**
     * Writes integer in Intel byte order.
     *
     * @param out output stream to write.
     * @param i   integer to write.
     * @throws IOException when error in file output.
     */
    static void writeInt(final OutputStream out, final int i) throws IOException {
        writeShort(out, i & 0xffff);
        writeShort(out, (i >> 16) & 0xffff);
    }

    /**
     * Writes short integer in Intel byte order.
     *
     * @param out output stream to write.
     * @param s   short integer to write.
     * @throws IOException when error in file output.
     */
    static void writeShort(final OutputStream out, final int s) throws IOException {
        out.write(s & 0xff);
        out.write((s >> 8) & 0xff);
    }

    /**
     * Check gzip member stream w/ CRC and length in trailer.
     * @throws IOException when CRC error or total length error.
     */
    public static boolean checkDictZipInputStream(String fileName) throws IOException {
        boolean result;
        try (DictZipInputStream dzin = new DictZipInputStream(new RandomAccessInputStream(fileName, "r"))) {
            result = checkDictZipInputStream(dzin);
            dzin.close();
        }
        return result;
    }

    /**
     * Check gzip member stream w/ CRC and length in trailer.
     * @throws IOException when CRC error or total length error.
     */
    public static boolean checkDictZipInputStream(DictZipInputStream in) throws IOException {
        final int BUF_LEN = 65536;
        byte[] tmpBuf = new byte[BUF_LEN];
        in.seek(0);
        long readLen = 0;
        while (readLen < in.getLength()) {
            int len = in.read(tmpBuf, 0, BUF_LEN);
            if (len < 0) {
                break;
            }
            readLen += len;
        }
        if (readLen != in.getLength()) {
            return false;
        }
        return true;
    }

    private DictZipFileUtils() {
    }
}
