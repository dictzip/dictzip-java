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
    private boolean debug_verbose = false;
    private int start = 0;
    private int size = 0;
    private String pre;
    private String post;

    public boolean isDecompress() {
        return decompress;
    }

    public void setDecompress(boolean decompress) {
        this.decompress = decompress;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public boolean isList() {
        return list;
    }

    public void setList(boolean list) {
        this.list = list;
    }

    public boolean isStdoutput() {
        return stdoutput;
    }

    public void setStdoutput(boolean stdoutput) {
        this.stdoutput = stdoutput;
    }

    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isDebug_verbose() {
        return debug_verbose;
    }

    public void setDebug_verbose(boolean debug_verbose) {
        this.debug_verbose = debug_verbose;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
