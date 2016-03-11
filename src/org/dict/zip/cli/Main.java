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

import java.util.Locale;
import java.util.ResourceBundle;
import java.text.MessageFormat;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

/**
 *
 * @author Hiroshi Miura
 */
public class Main {

    /**
     * The localized strings are kept in a separate file
     */
    private static ResourceBundle _messages = ResourceBundle.getBundle(
            "org/dict/zip/Bundle", Locale.getDefault());

    /**
     * main method.
     *
     * @param argv command line argument
     */
    public static void main(final String[] argv) {
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

                case 'b':
                    System.out.println("You picked plain old option " + (char) c);
                    break;

                case 'c':
                case 'd':
                    arg = g.getOptarg();
                    System.out.println("You picked option '" + (char) c
                            + "' with argument "
                            + ((arg != null) ? arg : "null"));
                    break;

                case 'h':
                    System.out.println(_messages.getString("version"));
                    System.out.println(_messages.getString("help.copyright"));
                    System.out.println(_messages.getString("help.message"));
                    break;

                case 'W':
                    System.out.println("Hmmm. You tried a -W with an incorrect long "
                            + "option name");
                    break;

                case ':':
                    System.out.println("Doh! You need an argument for option "
                            + (char) g.getOptopt());
                    break;

                case '?':
                    System.out.println("The option '" + (char) g.getOptopt()
                            + "' is not valid");
                    break;

                default:
                    System.out.println("getopt() returned " + c);
                    break;
            }
        }
        //
        for (int i = g.getOptind(); i < argv.length; i++) {
            System.out.println("Non option argv element: " + argv[i] + "\n");
        }
    }
}
