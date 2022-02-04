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
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DictZipFiles {
 
    static final int CHECK_BUF_LEN = 65536;

    public static DictZipInputStream newDictZipInputStream(final Path path) throws IOException {
        return new DictZipInputStream(newRandomAccessInputStream(path));
    }

    public static DictZipOutputStream newDictZipOutputStream(final Path path, final long dataSize) throws IOException {
        return new DictZipOutputStream(newRandomAccessOutputStream(path), dataSize);
    }

    public static RandomAccessInputStream newRandomAccessInputStream(final Path path) throws IOException {
        return new RandomAccessInputStream(new RandomAccessFile(path.toFile(), "r"));
    }

    public static RandomAccessOutputStream newRandomAccessOutputStream(final Path path) throws IOException {
        return new RandomAccessOutputStream(new RandomAccessFile(path.toFile(), "w"));
    }

    /**
     * Check gzip member stream w/ CRC and length in trailer.
     * @param path to be checked.
     * @return true if it is a valid dictzip file, otherwise false.
     * @throws IOException when CRC error or total length error.
     */
    public static boolean checkDictZipFile(final Path path) throws IOException {
        try (DictZipInputStream dzin = new DictZipInputStream(newRandomAccessInputStream(path))) {
            return checkDictZipInputStream(dzin);
        }
    }

    /**
     * Check gzip member stream w/ CRC and length in trailer.
     * @param filename to be checked.
     * @return true if it is a valid dictzip file, otherwise false.
     * @throws IOException when CRC error or total length error.
     */
    public static boolean checkDictZipFile(final String filename) throws IOException {
        try (DictZipInputStream dzin = newDictZipInputStream(Paths.get(filename))) {
            return checkDictZipInputStream(dzin);
        }
    }

    /**
     * Check gzip member stream w/ CRC and length in trailer.
     * @param in inputstream to be checked.
     * @return true if inputstream is a valid dictzip, otherwise false.
     * @throws IOException when CRC error or total length error.
     */
    public static boolean checkDictZipInputStream(final DictZipInputStream in) throws IOException {
        byte[] tmpBuf = new byte[CHECK_BUF_LEN];
        in.seek(0);
        long readLen = 0;
        while (readLen < in.getLength()) {
            int len = in.read(tmpBuf, 0, CHECK_BUF_LEN);
            if (len < 0) {
                break;
            }
            readLen += len;
        }
        return readLen == in.getLength();
    }
}
