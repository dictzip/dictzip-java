/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GNU General Public License v2.0 or later
 */

package org.dict.zip.cli;

/**
 * Utility class.
 * @author Hiroshi Miura
 */
public final class DictZipUtils {

    /**
     * Return filename that is good for uncompressed output.
     * <p>
     * if input filename is not end with ".dz" or ".gz",
     * adding ".~dz".
     * @param name input filename.
     * @return output filename.
     */
    static String uncompressedFileName(final String name) {
        String result;
        if (name.endsWith(".dz") || name.endsWith(".gz")) {
            result = name.substring(0, name.length() - 3);
        } else {
            result = name + ".~dz";
        }
        return result;
    }

    /**
     * Return file name for compressed output.
     * @param name input file name.
     * @return output filename.
     */
    static String compressedFileName(final String name) {
        return name + ".dz";
    }

    /**
     * Utility class should not be instantiated.
     */
    private DictZipUtils() { }
}
