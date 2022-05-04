/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GNU General Public License v2.0 or later
 */

package org.dict.zip.cli;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.dict.zip.DictZipHeader.CompressionLevel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Parse and keep command options and arguments.
 * @author Hiroshi Miura
 */
public class CommandLine {
    /**
     * Command line options.
     */
    protected final Options options = new Options();

    /**
     * Target files for zip or unzip.
     */
    protected final List<String> targetFiles = new ArrayList<>();

    private static String getString(final String key) {
        return AppConsts.RESOURCE_BUNDLE.getString(key);
    }

    protected List<String> getTargetFiles() {
        return targetFiles;
    }

    private static final int OPTS_LEN = 13;
    /**
     * Parse command line and set preferences.
     *
     * @param argv command line argument
     * @return exit code, if success return 0, otherwise return exit code.
     */
    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    protected int parse(final String[] argv) {
        int c;
        String arg;

        LongOpt[] longOpts = new LongOpt[OPTS_LEN];
        StringBuffer startVal = new StringBuffer();
        StringBuffer sizeVal = new StringBuffer();
        longOpts[0] = new LongOpt("stdout", LongOpt.NO_ARGUMENT, null, 'c');
        longOpts[1] = new LongOpt("decompress", LongOpt.NO_ARGUMENT, null, 'd');
        longOpts[2] = new LongOpt("force", LongOpt.NO_ARGUMENT, null, 'f');
        longOpts[3] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longOpts[4] = new LongOpt("keep", LongOpt.NO_ARGUMENT, null, 'k');
        longOpts[5] = new LongOpt("list", LongOpt.NO_ARGUMENT, null, 'l');
        longOpts[6] = new LongOpt("license", LongOpt.NO_ARGUMENT, null, 'L');
        longOpts[7] = new LongOpt("test", LongOpt.NO_ARGUMENT, null, 't');
        longOpts[8] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'v');
        longOpts[9] = new LongOpt("start", LongOpt.REQUIRED_ARGUMENT, startVal, 's');
        longOpts[10] = new LongOpt("size", LongOpt.REQUIRED_ARGUMENT, sizeVal, 'e');
        longOpts[11] = new LongOpt("fast", LongOpt.NO_ARGUMENT, null, '1');
        longOpts[12] = new LongOpt("best", LongOpt.NO_ARGUMENT, null, '9');
        assert (longOpts.length == OPTS_LEN);
        Getopt g = new Getopt("dictzip", argv, "cdfhklLe:E:s:S:tvVD:p:P:169", longOpts);
        g.setOpterr(false); // We'll do our own error handling
        //
        while ((c = g.getopt()) != -1) {
            switch (c) {
                case 0:
                    break;
                case 1:
                    System.out.println("I see you have return in order set and that "
                            + "a non-option argv element was just found "
                            + "with the value '" + g.getOptarg() + "'");
                    break;

                case 2:
                    arg = g.getOptarg();
                    System.out.println("I know this, but pretend I didn't");
                    System.out.println("We picked option "
                            + longOpts[g.getLongind()].getName()
                            + " with value "
                            + ((arg != null) ? arg : "null"));
                    break;
                case '1':
                    options.setLevel(CompressionLevel.BEST_SPEED);
                    break;
                case '6':
                    options.setLevel(CompressionLevel.DEFAULT_COMPRESSION);
                    break;
                case '9':
                    options.setLevel(CompressionLevel.BEST_COMPRESSION);
                    break;
                case 'c':
                    options.setStdout(true);
                    break;
                case 'd':
                    options.setDecompress(true);
                    break;
                case 'f':
                    options.setForce(true);
                    break;
                case 'h':
                    System.out.println(AppConsts.getNameAndVersion());
                    showCopyright();
                    System.out.println();
                    showHelp();
                    return 1;
                case 'k':
                    options.setKeep(true);
                    break;
                case 'l':
                    options.setList(true);
                    break;
                case 'L':
                    System.out.println(AppConsts.getNameAndVersion());
                    showCopyright();
                    System.out.println();
                    System.out.println(getString("help.license"));
                    return 1;
                case 'e':
                    arg = g.getOptarg();
                    try {
                        options.setSize(parseNumber(arg.trim()));
                    } catch (NumberFormatException nfe) {
                        System.err.println(getString("commandline.error.num_format"));
                        showHelp();
                        return 2;
                    }
                    break;
               case 's':
                    arg = g.getOptarg();
                    try {
                        options.setStart(parseNumber(arg.trim()));
                    } catch (NumberFormatException nfe) {
                        System.err.println(getString("commandline.error.num_format"));
                        showHelp();
                        return 2;
                    }
                    break;
                case 't':
                    options.setTest(true);
                    break;
                case 'v':
                    System.out.println(AppConsts.getNameAndVersion());
                    showCopyright();
                    return 1;
                case ':':
                    System.err.println("Doh! You need an argument for option "
                            + (char) g.getOptopt());
                    showHelp();
                    return 2;
                case '?':
                    System.out.println("The option '" + (char) g.getOptopt()
                            + "' is not valid");
                    showHelp();
                    return 2;
                default:
                    System.out.println("getopt() returned " + c);
                    break;
            }
        }
        //
        targetFiles.addAll(Arrays.asList(argv).subList(g.getOptind(), argv.length));
        return 0;
    }

    private static void showCopyright() {
        System.out.println(MessageFormat.format(getString("help.copyright.template"),
                AppConsts.YEAR, AppConsts.AUTHORS));
    }
    private static void showHelp() {
        System.out.println(MessageFormat.format(getString("help.message"),
                AppConsts.NAME));
    }

    private static Integer parseNumber(final String arg) throws NumberFormatException {
        if (arg.startsWith("0x")) {
            System.err.println(arg.substring(2));
            return Integer.parseInt(arg.substring(2), 16);
        } else if (arg.startsWith("0")) {
            return Integer.parseInt(arg, 8);
        } else {
            return Integer.parseInt(arg);
        }
    }
}
