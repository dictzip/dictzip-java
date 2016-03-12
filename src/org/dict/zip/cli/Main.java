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
import java.io.IOException;

/**
 *
 * @author Hiroshi Miura
 */
public class Main {

    /**
     * The localized strings are kept in a separate file
     */
    private static ResourceBundle _messages = ResourceBundle.getBundle(
            "org/dict/zip/cli/Bundle", Locale.getDefault());

    private static final CommandLine commandLine = new CommandLine();

    /**
     * main method.
     *
     * @param argv command line argument
     */
    public static void main(final String[] argv) {
        int res = commandLine.parse(argv);
        
        // If error in command line, exit with code
        if (res != 0) {
            System.exit(res);
        }
        
        for (String fName: commandLine.getTargetFiles()) {
            try {
            DictData dict = new DictData(fName);
            if (commandLine.options.isList()) {
                commandLine.options.setKeep(true);
                dict.open(DictData.OpsMode.READ);
                dict.printHeader();
                dict.close();
            } else if (commandLine.options.isDecompress()) {
                dict.doUnzip();
            } else {
                dict.doZip();
            }
            if (!commandLine.options.isKeep()) {
                dict.removeTarget();
            }
            } catch (IOException ex) {
                System.err.println(_messages.getString("main.io.error"));
                System.exit(1);
            }
        }
        System.exit(0);
    }
}
