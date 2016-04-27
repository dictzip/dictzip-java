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

    private static final int OPTS_LEN = 19;
    /**
     * Parse command line and set preferences.
     *
     * @param argv command line argument
     * @return exit code, if success return 0, otherwise return exit code.
     */
    protected int parse(final String[] argv) {
        int c;
        String arg;

        LongOpt[] longOpts = new LongOpt[OPTS_LEN];
        StringBuffer debugLevelVal = new StringBuffer();
        StringBuffer startVal = new StringBuffer();
        StringBuffer sizeVal = new StringBuffer();
        StringBuffer preFilterName = new StringBuffer();
        StringBuffer postFilterName = new StringBuffer();
        longOpts[0] = new LongOpt("stdout", LongOpt.NO_ARGUMENT, null, 'c');
        longOpts[1] = new LongOpt("decompress", LongOpt.NO_ARGUMENT, null, 'd');
        longOpts[2] = new LongOpt("force", LongOpt.NO_ARGUMENT, null, 'f');
        longOpts[3] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longOpts[4] = new LongOpt("keep", LongOpt.NO_ARGUMENT, null, 'k');
        longOpts[5] = new LongOpt("list", LongOpt.NO_ARGUMENT, null, 'l');
        longOpts[6] = new LongOpt("license", LongOpt.NO_ARGUMENT, null, 'L');
        longOpts[7] = new LongOpt("test", LongOpt.NO_ARGUMENT, null, 't');
        longOpts[8] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');
        longOpts[9] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V');
        longOpts[10] = new LongOpt("debug", LongOpt.REQUIRED_ARGUMENT, debugLevelVal, 'D');
        longOpts[11] = new LongOpt("start", LongOpt.REQUIRED_ARGUMENT, startVal, 's');
        longOpts[12] = new LongOpt("size", LongOpt.REQUIRED_ARGUMENT, sizeVal, 'e');
        longOpts[15] = new LongOpt("pre", LongOpt.REQUIRED_ARGUMENT, preFilterName, 'p');
        longOpts[16] = new LongOpt("post", LongOpt.REQUIRED_ARGUMENT, postFilterName, 'P');
        longOpts[17] = new LongOpt("fast", LongOpt.NO_ARGUMENT, null, '1');
        longOpts[18] = new LongOpt("best", LongOpt.NO_ARGUMENT, null, '9');
        assert(longOpts.length == OPTS_LEN);
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
                    System.out.println(MessageFormat.format(getString("help.copyright.template"),
                            AppConsts.YEAR, AppConsts.AUTHORS));
                    System.out.println();
                    System.out.println(MessageFormat.format(getString("help.message"),
                            AppConsts.NAME));
                    break;
                case 'k':
                    options.setKeep(true);
                    break;
                case 'l':
                    options.setList(true);
                    break;
                case 'L':
                    System.out.println(AppConsts.getNameAndVersion());
                    System.out.println(MessageFormat.format(getString("help.copyright.template"),
                            AppConsts.YEAR, AppConsts.AUTHORS));
                    System.out.println();
                    System.out.println(getString("help.license"));
                    break;
                case 'e':
                    arg = g.getOptarg();
                    options.setSize(Integer.getInteger(arg));
                    break;
               case 's':
                    arg = g.getOptarg();
                    options.setStart(Integer.getInteger(arg));
                    break;
                case 't':
                    options.setTest(true);
                    break;
                case 'v':
                    System.out.println(AppConsts.getNameAndVersion());
                    System.out.println(MessageFormat.format(getString("help.copyright.template"),
                            AppConsts.YEAR, AppConsts.AUTHORS));
                    System.out.println();
                    break;
                case ':':
                    System.out.println("Doh! You need an argument for option "
                            + (char) g.getOptopt());
                    break;
                case '?':
                    System.out.println("The option '" + (char) g.getOptopt()
                            + "' is not valid");
                    return 1;
                default:
                    System.out.println("getopt() returned " + c);
                    break;
            }
        }
        //
        targetFiles.addAll(Arrays.asList(argv).subList(g.getOptind(), argv.length));
        return 0;
    }
}
