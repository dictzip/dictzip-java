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

import org.dict.zip.DictZipHeader.CompressionLevel;

/**
 *
 * @author Hiroshi Miura
 */
public class Options {

    private boolean decompress = false;
    private boolean force = false;
    private boolean keep = false;
    private boolean list = false;
    private boolean stdout = false;
    private boolean test = false;
    private boolean verbose = false;
    private boolean debugVerbose = false;
    private long start = 0;
    private int size = 0;
    private CompressionLevel level = CompressionLevel.DEFAULT_COMPRESSION;


    /**
     * Whether -d option is spedified.
     * @return true if -d option is specified.
     */
    boolean isDecompress() {
        return decompress;
    }

    /**
     * Set -d option.
     * @param decompress true if -d option is specified.
     */
    void setDecompress(final boolean decompress) {
        this.decompress = decompress;
    }

    /**
     * Whether -f/--force option is specified.
     * @return true if -f option is specified.
     */
    boolean isForce() {
        return force;
    }

    /**
     * Set -f/--force option.
     * @param force true if -f option is specified.
     */
    void setForce(final boolean force) {
        this.force = force;
    }

    /**
     * Whether -k/--keep is specified or not.
     * @return true if -k is specified
     */
    boolean isKeep() {
        return keep;
    }

    /**
     * Set -k/--keep option.
     * @param keep true if -k is specified.
     */
    void setKeep(final boolean keep) {
        this.keep = keep;
    }

    /**
     * Whether -l/--list option is specified.
     * @return true if -l specified.
     */
    boolean isList() {
        return list;
    }

    /**
     * Set -l option.
     * @param list true if -l option is specified.
     */
    void setList(final boolean list) {
        this.list = list;
    }

    /**
     * Whther -c option is specified.
     * @return true if -l option is specified.
     */
    boolean isStdout() {
        return stdout;
    }

    /**
     * Set -c option.
     * @param stdout true when -c option specified.
     */
    void setStdout(final boolean stdout) {
        this.stdout = stdout;
    }

    /**
     * Whether -t/--test option specified.
     * @return true if -t specified.
     */
    boolean isTest() {
        return test;
    }

    /**
     * Set -t option.
     * @param test true if -t specified.
     */
    void setTest(final boolean test) {
        this.test = test;
    }

    /**
     * Whether -v/--verbose option specified.
     * @return true if -v specified.
     */
    boolean isVerbose() {
        return verbose;
    }

    /**
     * Set -v option.
     * @param verbose true if -v option specified.
     */
    void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Whether -Dverbose/--debug=verbose specified.
     * @return true if -Dverbose specified.
     */
    boolean isDebugVerbose() {
        return debugVerbose;
    }

    /**
     * Set -Dverbose/--debug=verbose specified.
     * @param debugVerbose true if specified.
     */
    void setDebugVerbose(final boolean debugVerbose) {
        this.debugVerbose = debugVerbose;
    }

    /**
     * Get value of -s/--start option.
     * @return Value of -s/--start option. If not specified, return 0.
     */
    long getStart() {
        return start;
    }

    /**
     * Set value of start option.
     * @param start value of start option.
     */
    void setStart(final long start) {
        this.start = start;
    }

    /**
     * Get value of -S/--size option.
     * @return value of size option.
     */
    int getSize() {
        return size;
    }

    /**
     * Set value of size option.
     * @param size value of size option.
     */
    void setSize(final int size) {
        this.size = size;
    }

    CompressionLevel getLevel() {
        return level;
    }

    void setLevel(CompressionLevel level) {
        this.level = level;
    }
}
