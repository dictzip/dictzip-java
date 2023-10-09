/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GNU General Public License v2.0 or later
 */

package org.dict.zip.cli;

import picocli.CommandLine;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author Hiroshi Miura
 */
public class Options {

    /**
     * Command line options.
     */
    @CommandLine.Option(names = {"-c", "--stdout"}, description = "return result in stdout.")
    private boolean stdout = false;

    @CommandLine.Option(names = {"-d", "--decompress"}, description = "decompression mode")
    private boolean decompress = false;

    @CommandLine.Option(names = {"-f", "--force"}, description = "force overwrite.")
    private boolean force = false;

    @CommandLine.Option(names = {"-k", "--keep"}, description = "keep source archive")
    private boolean keep = false;

    @CommandLine.Option(names = {"-l", "--list"}, description = "list archive files")
    private boolean list = false;

    @CommandLine.Option(names = {"-t", "--test"}, description = "test archive")
    private boolean test = false;

    @CommandLine.Option(names = {"-L", "--license"}, description = "show license information")
    private boolean license;

    @CommandLine.Option(names = {"-v", "--verbose"}, description = "verbose")
    private boolean verbose = false;

    private boolean debugVerbose = false;

    @CommandLine.Option(names = {"-s", "--start"}, description = "start position")
    private long start = 0;

    @CommandLine.Option(names = {"-e", "--size"}, description = "extract size")
    private int size = 0;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "show help")
    private boolean helpRequested = false;

    @CommandLine.Option(names = {"-1", "--fast"}, description = "fast compression level")
    private boolean fast;

    @CommandLine.Option(names = {"-6", "--moderate"}, description = "moderate compression level")
    private boolean moderate;

    @CommandLine.Option(names = {"-9", "--best"}, description = "best compression level")
    private boolean best;

    @CommandLine.Option(names = {"-V", "--version"}, description = "show version")
    private boolean version;

    @CommandLine.Parameters
    private List<String> targetFiles;

    /**
     * Whether -d option is spedified.
     * @return true if -d option is specified.
     */
    boolean isDecompress() {
        return decompress;
    }

    /**
     * Whether -f/--force option is specified.
     * @return true if -f option is specified.
     */
    boolean isForce() {
        return force;
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
     * Whther -c option is specified.
     * @return true if -l option is specified.
     */
    boolean isStdout() {
        return stdout;
    }

    /**
     * Whether -t/--test option specified.
     * @return true if -t specified.
     */
    boolean isTest() {
        return test;
    }

    /**
     * Whether -v/--verbose option specified.
     * @return true if -v specified.
     */
    boolean isVerbose() {
        return verbose;
    }

    /**
     * Whether -Dverbose/--debug=verbose specified.
     * @return true if -Dverbose specified.
     */
    boolean isDebugVerbose() {
        return debugVerbose;
    }

    /**
     * Get value of -s/--start option.
     * @return Value of -s/--start option. If not specified, return 0.
     */
    long getStart() {
        return start;
    }

    /**
     * Get value of -S/--size option.
     * @return value of size option.
     */
    int getSize() {
        return size;
    }

    public boolean isLicense() {
        return license;
    }

    public boolean isHelpRequested() {
        return helpRequested;
    }

    public boolean isFast() {
        return fast;
    }

    public boolean isBest() {
        return best;
    }

    public boolean isVersion() {
        return version;
    }

    List<String> getTargetFiles() {
        if (targetFiles == null) {
            targetFiles = Collections.emptyList();
        }
        return targetFiles;
    }

}
