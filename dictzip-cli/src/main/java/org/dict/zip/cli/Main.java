/*
 * DictZip command line interface.
 *
 * This is a part of DictZip-java library.
 *
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

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.io.IOException;

/**
 * dictzip/dictunzip main class.
 * @author Hiroshi Miura
 */
public final class Main {
    /**
     * The localized strings are kept in a separate file.
     */
    private static ResourceBundle messages = ResourceBundle.getBundle(
            "org/dict/zip/cli/Bundle", Locale.getDefault());

    private static CommandLine commandLine = new CommandLine();

    private static void showHelp() {
        System.out.println(AppConsts.getNameAndVersion());
        System.out.println(MessageFormat.format(DictZipUtils.getString("help.copyright.template"),
                AppConsts.YEAR, AppConsts.AUTHORS));
        System.out.println();
        System.out.println(MessageFormat.format(DictZipUtils.getString("help.message"),
                AppConsts.NAME));
    }

    private static void doList(String fName) throws IOException {
        DictData dict;
        dict = new DictData(fName, null);
        dict.printHeader();
    }

    private static void doUnZip(String fName) throws IOException {
        DictData dict;
        String extractFile = DictZipUtils.uncompressedFileName(fName);
        long start = commandLine.options.getStart();
        int size = commandLine.options.getSize();
        dict = new DictData(extractFile, fName);
        dict.doUnzip(start, size);
        if (!commandLine.options.isKeep()) {
            File targetFile = new File(fName);
            if (!targetFile.delete()) {
                System.err.println(messages.getString("main.delete.error"));
                System.exit(2);
            }
        }
        if (!commandLine.options.isKeep()) {
            deleteTarget(fName);
        }
    }

    private static void deleteTarget(String fName) {
       File targetFile = new File(fName);
        if (!targetFile.delete()) {
            System.err.println(messages.getString("main.delete.error"));
            System.exit(2);
        }
    }

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
        try {
            DictData dict;
            List<String> target = commandLine.getTargetFiles();
            if (commandLine.options.isList()) {
                if (target.size() != 1) {
                    showHelp();
                    System.exit(1);
                }
                doList(target.get(0));
            } else if (commandLine.options.isDecompress()) {
                if (target.size() != 1) {
                    showHelp();
                    System.exit(1);
                }
                doUnZip(target.get(0));
             } else { // compression.
                CompressionLevel level = commandLine.options.getLevel();
                String zippedFile = DictZipUtils.compressedFileName(target.get(0));
                for (String fName : target) {
                    dict = new DictData(fName, zippedFile);
                    dict.doZip(level);
                    if (!commandLine.options.isKeep()) {
                        deleteTarget(fName);
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(messages.getString("main.io.error"));
            System.err.println(ex.getLocalizedMessage());
            System.exit(1);
        }
        System.exit(0);
    }

    private Main() {
    }

}
