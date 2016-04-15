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
     * Compare binary files. Both files must be files (not directories) and exist.
     *
     * @param first  - first file
     * @param second - second file
     * @return boolean - true if files are binery equal
     * @throws IOException - error in function
     */
    static boolean isFileBinaryEquals(File first, File second) throws IOException {
        return isFileBinaryEquals(first, second, 0, first.length());
    }

    /**
     * Compare binary files (for test). Both files must be files (not directories) and exist.
     *
     * @param first  - first file
     * @param second - second file
     * @param off    - compare from offset
     * @param len    - comparison length
     * @return boolean - true if files are binery equal
     * @throws IOException - error in function
     */
    static boolean isFileBinaryEquals(File first, File second, final long off, final long len) throws IOException {
        boolean result = false;
        final int BUFFER_SIZE = 65536;
        final int COMP_SIZE = 512;

        if (len <= 1) {
            throw new IllegalArgumentException();
        }
        if (off < 0) {
            throw new IllegalArgumentException();
        }

        if ((first.exists()) && (second.exists())
                && (first.isFile()) && (second.isFile())) {
            if (first.getCanonicalPath().equals(second.getCanonicalPath())) {
                result = true;
            } else {
                FileInputStream firstInput;
                FileInputStream secondInput;
                BufferedInputStream bufFirstInput = null;
                BufferedInputStream bufSecondInput = null;

                try {
                    firstInput = new FileInputStream(first);
                    secondInput = new FileInputStream(second);
                    bufFirstInput = new BufferedInputStream(firstInput, BUFFER_SIZE);
                    bufSecondInput = new BufferedInputStream(secondInput, BUFFER_SIZE);

                    byte[] firstBytes = new byte[COMP_SIZE];
                    byte[] secondBytes = new byte[COMP_SIZE];

                    if (off > 0) {
                        long totalSkipped = 0;
                        while (totalSkipped < off) {
                            long skipped = bufFirstInput.skip(off - totalSkipped);
                            if (skipped == 0) {
                                throw new IOException("Cannot seek offset bytes.");
                            }
                            totalSkipped += skipped;
                        }
                        totalSkipped = 0;
                        while (totalSkipped < off) {
                            long skipped = bufSecondInput.skip(off - totalSkipped);
                            if (skipped == 0) {
                                throw new IOException("Cannot seek offset bytes.");
                            }
                            totalSkipped += skipped;
                        }
                    }

                    long readLengthTotal = 0;
                    result = true;
                    while (readLengthTotal < len) {
                        int readLength = COMP_SIZE;
                        if (len - readLengthTotal < (long) COMP_SIZE) {
                            readLength = (int) (len - readLengthTotal);
                        }
                        int lenFirst = bufFirstInput.read(firstBytes, 0, readLength);
                        int lenSecond = bufSecondInput.read(secondBytes, 0, readLength);
                        if (lenFirst != lenSecond) {
                            result = false;
                            break;
                        }
                        if ((lenFirst < 0) && (lenSecond < 0)) {
                            result = true;
                            break;
                        }
                        readLengthTotal += lenFirst;
                        if (lenFirst < firstBytes.length) {
                            byte[] a = Arrays.copyOfRange(firstBytes, 0, lenFirst);
                            byte[] b = Arrays.copyOfRange(secondBytes, 0, lenSecond);
                            if (!Arrays.equals(a, b)) {
                                result = false;
                                break;
                            }
                        } else if (!Arrays.equals(firstBytes, secondBytes)) {
                            result = false;
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    throw e;
                } finally {
                    try {
                        if (bufFirstInput != null) {
                            bufFirstInput.close();
                        }
                    } finally {
                        if (bufSecondInput != null) {
                            bufSecondInput.close();
                        }
                    }
                }
            }
        }

        return result;
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
