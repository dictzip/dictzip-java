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

import org.apache.commons.codec.binary.Base64;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.text.MessageFormat;
import java.util.ArrayList;
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
    protected final List<String> targetFiles = new ArrayList<String>();

    private static String getString(final String key) {
        return AppConsts._messages.getString(key);
    }

    protected List<String> getTargetFiles() {
        return targetFiles;
    }

    /**
     * Parse command line and set preferences.
     *
     * @param argv command line argument
     * @return exit code, if success return 0, otherwise return exit code.
     */
    protected int parse(final String[] argv) {
        int c;
        String arg;

        LongOpt[] longopts = new LongOpt[17];
        StringBuffer debugLevelVal = new StringBuffer();
        StringBuffer startVal = new StringBuffer();
        StringBuffer sizeVal = new StringBuffer();
        StringBuffer preFilterName = new StringBuffer();
        StringBuffer postFilterName = new StringBuffer();
        longopts[0] = new LongOpt("stdout", LongOpt.NO_ARGUMENT, null, 'c');
        longopts[1] = new LongOpt("decompress", LongOpt.NO_ARGUMENT, null, 'd');
        longopts[2] = new LongOpt("force", LongOpt.NO_ARGUMENT, null, 'f');
        longopts[3] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
        longopts[4] = new LongOpt("keep", LongOpt.NO_ARGUMENT, null, 'k');
        longopts[5] = new LongOpt("list", LongOpt.NO_ARGUMENT, null, 'l');
        longopts[6] = new LongOpt("license", LongOpt.NO_ARGUMENT, null, 'L');
        longopts[7] = new LongOpt("test", LongOpt.NO_ARGUMENT, null, 't');
        longopts[8] = new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v');
        longopts[9] = new LongOpt("version", LongOpt.NO_ARGUMENT, null, 'V');
        longopts[10] = new LongOpt("debug", LongOpt.REQUIRED_ARGUMENT, debugLevelVal, 'D');
        longopts[11] = new LongOpt("start", LongOpt.REQUIRED_ARGUMENT, startVal, 's');
        longopts[12] = new LongOpt("size", LongOpt.REQUIRED_ARGUMENT, sizeVal, 'e');
        longopts[13] = new LongOpt("Start", LongOpt.REQUIRED_ARGUMENT, startVal, 'S');
        longopts[14] = new LongOpt("Size", LongOpt.REQUIRED_ARGUMENT, sizeVal, 'E');
        longopts[15] = new LongOpt("pre", LongOpt.REQUIRED_ARGUMENT, preFilterName, 'p');
        longopts[16] = new LongOpt("post", LongOpt.REQUIRED_ARGUMENT, postFilterName, 'P');
        Getopt g = new Getopt("testprog", argv, "cdfhklLe:E:s:S:tvVD:p:P:", longopts);
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
                            + longopts[g.getLongind()].getName()
                            + " with value "
                            + ((arg != null) ? arg : "null"));
                    break;
                case 'c':
                    options.setStdoutput(true);
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
                    //cdfhklLe:E:s:S:tvVD:p:P
                case 'e':
                    arg = g.getOptarg();
                    options.setSize(Integer.getInteger(arg));
                case 'E':
                    arg = g.getOptarg();
                    options.setSize(Base64.decodeInteger(Base64.decodeBase64(arg)).intValue());
                case 'p':
                    arg = g.getOptarg();
                    options.setPre(arg);
                    break;
                case 'P':
                    arg = g.getOptarg();
                    options.setPost(arg);
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
        for (int i = g.getOptind(); i < argv.length; i++) {
            targetFiles.add(argv[i]);
        }
        return 0;
    }
}
