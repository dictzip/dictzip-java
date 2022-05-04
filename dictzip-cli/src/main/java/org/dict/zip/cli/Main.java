/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GNU General Public License v2.0 or later
 */

package org.dict.zip.cli;

import org.dict.zip.DictZipFiles;
import org.dict.zip.DictZipHeader.CompressionLevel;

import java.io.File;
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

    /**
     * main method.
     *
     * @param argv command line argument
     */
    public static void main(final String[] argv) {
        int res = commandLine.parse(argv);
        // If error in command line, exit with code
        if (res > 1) {
            System.exit(res);
        } else if (res == 1) {
            // normal exit.
            System.exit(0);
        }
        for (String fName: commandLine.getTargetFiles()) {
            try {
                DictData dict;
                if (commandLine.options.isList()) {
                    commandLine.options.setKeep(true);
                    dict = new DictData(fName, null);
                    dict.printHeader();
                } else if (commandLine.options.isTest()) {
                    boolean result = false;
                    try {
                        result = DictZipFiles.checkDictZipFile(fName);
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.exit(2);
                    }
                    if (result) {
                        System.exit(0);
                    } else {
                        System.err.println(messages.getString("main.test.error"));
                        System.exit(1);
                    }
                } else if (commandLine.options.isDecompress()) {
                    String extractFile = DictZipUtils.uncompressedFileName(fName);
                    long start = commandLine.options.getStart();
                    int size = commandLine.options.getSize();
                    dict = new DictData(extractFile, fName);
                    dict.doUnzip(start, size);
                } else { // compression.
                    String zippedFile = DictZipUtils.compressedFileName(fName);
                    CompressionLevel level = commandLine.options.getLevel();
                    dict = new DictData(fName, zippedFile);
                    dict.doZip(level);
                }
                if (!commandLine.options.isKeep()) {
                    File targetFile = new File(fName);
                    if (!targetFile.delete()) {
                        System.err.println(messages.getString("main.delete.error"));
                        System.exit(2);
                    }
                }
            } catch (IOException ex) {
                System.err.println(messages.getString("main.io.error"));
                System.err.println(ex.getLocalizedMessage());
                System.exit(1);
            }
        }
        System.exit(0);
    }

    private Main() {
    }

}
