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
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * dictzip/dictunzip main class.
 * @author Hiroshi Miura
 */
public final class Main {

    /**
     * main method.
     *
     * @param argv command line argument
     */
    public static void main(final String[] argv) {
        Options options = new Options();
        new CommandLine(options).parseArgs(argv);

        if (options.isHelpRequested()) {
            System.out.println(AppConsts.getNameAndVersion());
            showCopyright();
            System.out.println();
            showHelp();
            System.exit(0);
        }
        if (options.isVersion()) {
            System.out.println(AppConsts.getVersion());
            System.exit(0);
        }
        for (String fName: options.getTargetFiles()) {
            try {
                DictData dict;
                if (options.isList()) {
                    options.setKeep(true);
                    dict = new DictData(fName, null);
                    dict.printHeader();
                } else if (options.isTest()) {
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
                        System.err.println(getString("main.test.error"));
                        System.exit(1);
                    }
                } else if (options.isDecompress()) {
                    String extractFile = DictZipUtils.uncompressedFileName(fName);
                    long start = options.getStart();
                    int size = options.getSize();
                    dict = new DictData(extractFile, fName);
                    dict.doUnzip(start, size);
                } else { // compression.
                    String zippedFile = DictZipUtils.compressedFileName(fName);
                    CompressionLevel level;
                    if (options.isBest()) {
                        level = CompressionLevel.BEST_COMPRESSION;
                    } else if (options.isFast()) {
                        level = CompressionLevel.BEST_SPEED;
                    } else {
                        level = CompressionLevel.DEFAULT_COMPRESSION;
                    }
                    dict = new DictData(fName, zippedFile);
                    dict.doZip(level);
                }
                if (!options.isKeep()) {
                    File targetFile = new File(fName);
                    if (!targetFile.delete()) {
                        System.err.println(getString("main.delete.error"));
                        System.exit(2);
                    }
                }
            } catch (IOException ex) {
                System.err.println(getString("main.io.error"));
                System.err.println(ex.getLocalizedMessage());
                System.exit(1);
            }
        }
        System.exit(0);
    }

    private Main() {
    }

    private static String getString(final String key) {
        return AppConsts.getString(key);
    }

    private static void showCopyright() {
        System.out.println(AppConsts.getCopyright());
    }
    private static void showHelp() {
        System.out.println(MessageFormat.format(getString("help.message"),
                AppConsts.getApplicationName()));
    }

}
