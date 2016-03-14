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
 *
 * @author Hiroshi Miura
 */
public class Options {

    private boolean decompress = false;
    private boolean force = false;
    private boolean keep = false;
    private boolean list = false;
    private boolean stdoutput = false;
    private boolean test = false;
    private boolean verbose = false;
    private boolean debugVerbose = false;
    private long start = 0;
    private long size = 0;
    private String pre;
    private String post;

    /**
     * Whether -d option is spedified.
     * @return true if -d option is specified.
     */
    public boolean isDecompress() {
        return decompress;
    }

    /**
     * Set -d option.
     * @param decompress true if -d option is specified.
     */
    public void setDecompress(final boolean decompress) {
        this.decompress = decompress;
    }

    /**
     * Whether -f/--force option is spedified.
     * @return true if -f option is spefified.
     */
    public boolean isForce() {
        return force;
    }

    /**
     * Set -f/--force option.
     * @param force true if -f option is spefified.
     */
    public void setForce(final boolean force) {
        this.force = force;
    }

    /**
     * Whether -k/--keep is specified or not.
     * @return true if -k is specified
     */
    public boolean isKeep() {
        return keep;
    }

    /**
     * Set -k/--keep option.
     * @param keep true if -k is specified.
     */
    public void setKeep(final boolean keep) {
        this.keep = keep;
    }

    /**
     * Whether -l/--list option is specified.
     * @return true if -l specified.
     */
    public boolean isList() {
        return list;
    }

    /**
     * Set -l option.
     * @param list true if -l option is specified.
     */
    public void setList(final boolean list) {
        this.list = list;
    }

    /**
     * Whther -c option is specified.
     * @return true if -l option is specified.
     */
    public boolean isStdoutput() {
        return stdoutput;
    }

    /**
     * Set -c option.
     * @param stdoutput true when -c option specified.
     */
    public void setStdoutput(final boolean stdoutput) {
        this.stdoutput = stdoutput;
    }

    /**
     * Whether -t/--test option specified.
     * @return true if -t specified.
     */
    public boolean isTest() {
        return test;
    }

    /**
     * Set -t option.
     * @param test true if -t specified.
     */
    public void setTest(final boolean test) {
        this.test = test;
    }

    /**
     * Whether -v/--verbose option specified.
     * @return true if -v specified.
     */
    public boolean isVerbose() {
        return verbose;
    }

    /**
     * Set -v option.
     * @param verbose true if -v option specified.
     */
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * Whether -Dverbose/--debug=verbose specified.
     * @return true if -Dverbose specified.
     */
    public boolean isDebugVerbose() {
        return debugVerbose;
    }

    /**
     * Set -Dverbose/--debug=verbose specified.
     * @param debugVerbose true if specified.
     */
    public void setDebugVerbose(final boolean debugVerbose) {
        this.debugVerbose = debugVerbose;
    }

    /**
     * Get value of -s/--start option.
     * @return Value of -s/--start option. If not specified, return 0.
     */
    public long getStart() {
        return start;
    }

    /**
     * Set value of start option.
     * @param start value of start option.
     */
    public void setStart(final long start) {
        this.start = start;
    }

    /**
     * Get value of -S/--size option.
     * @return value of size option.
     */
    public long getSize() {
        return size;
    }

    /**
     * Set value of size option.
     * @param size value of size option.
     */
    public void setSize(final long size) {
        this.size = size;
    }

    /**
     * Get preset filter option.
     * @return preset filter command.
     */
    public String getPre() {
        return pre;
    }

    /**
     * Set preset filter option.
     * @param pre preset filter command in String.
     */
    public void setPre(final String pre) {
        this.pre = pre;
    }

    /**
     * Get post process filter option.
     * @return post filter command.
     */
    public String getPost() {
        return post;
    }

    /**
     * Set post process filter option.
     * @param post post filter command in String.
     */
    public void setPost(final String post) {
        this.post = post;
    }
}
