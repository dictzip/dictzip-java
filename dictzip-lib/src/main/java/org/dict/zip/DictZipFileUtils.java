/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package org.dict.zip;

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by Hiroshi Miura on 16/04/09.
 * @author Hiroshi Miura
 */
public final class DictZipFileUtils {

    static final int CHECK_BUF_LEN = 65536;

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
     * @see DictZipFiles#checkDictZipFile
     * @param filename to be checked.
     * @return true if it is a valid dictzip file, otherwise false.
     * @throws IOException when CRC error or total length error.
     */
    @Deprecated
    public static boolean checkDictZipInputStream(final String filename) throws IOException {
        return DictZipFiles.checkDictZipFile(filename);
    }

    /**
     * Check gzip member stream w/ CRC and length in trailer.
     * @see DictZipFiles#checkDictZipInputStream
     * @param in inputstream to be checked.
     * @return true if inputstream is a valid dictzip, otherwise false.
     * @throws IOException when CRC error or total length error.
     */
    @Deprecated
    public static boolean checkDictZipInputStream(final DictZipInputStream in) throws IOException {
        return DictZipFiles.checkDictZipInputStream(in);
    }

    private DictZipFileUtils() {
    }
}
