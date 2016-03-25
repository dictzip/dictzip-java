/*
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

package org.dict.zip.cli;

/**
 * Utility class.
 * @author Hiroshi Miura
 */
public class DictZipUtils {

    /**
     * Return filename that is good for uncompressed output.
     * <p>
     * if input filename is not end with ".dz" or ".gz",
     * adding ".~dz".
     * @param name input filename.
     * @return output filename.
     */
    protected static String uncompressedFileName(String name){
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
    protected static String compressedFileName(String name) {
        return name + ".dz";
    }

    private DictZipUtils() { }
}
