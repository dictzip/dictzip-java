/*
 * FileUtils library.
 *
 * Copyright (C) 2016,2022 Hiroshi Miura
 *
 *  SPDX-License-Identifier: Apache-2.0
 */

package tokyo.northside.io;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;


/**
 * General File manipulation utility.
 * <p>
 * This class provides static utility methods for input/output operations.
 * <ul>
 * <li>contentEquals - these methods compare the content of two files
 * </ul>
 * <p>
 * The methods in this class that read a file are buffered internally.
 * The default buffer size of 4K has been shown to be efficient in tests.
 * <p>
 * Created by Hiroshi Miura on 16/04/09.
 *
 * @author Hiroshi Miura
 */
public final class FileUtils2 {

    private FileUtils2() { }

    /**
     * Compare file contents in range. Both files must be files (not directories) and exist.
     *
     * @param first   first file
     * @param second  second file
     * @return boolean  true if files are equal, otherwise false
     * @throws IOException  error in function
     */
    public static boolean contentEquals(@NotNull final Path first, @NotNull final Path second) throws IOException {
        if (first.toFile().length() != second.toFile().length()) {
            return false;
        }
        return contentEquals(first.toFile(), second.toFile(), 0, first.toFile().length());
    }

    /**
     * Compare file contents in range. Both files must be files (not directories) and exist.
     *
     * @param first   first file
     * @param second  second file
     * @return boolean  true if files are equal, otherwise false
     * @throws IOException  error in function
     */
    public static boolean contentEquals(@NotNull final File first, @NotNull final File second) throws IOException {
        if (!first.exists() || !second.exists()) {
            return false;
        }
        if (!first.isFile() || !second.isFile()) {
            return false;
        }
        if (first.getCanonicalPath().equals(second.getCanonicalPath())) {
            return true;
        }
        if (first.length() != second.length()) {
            return false;
        }
        if (first.length() == 0) {
            return true;
        }
        return contentEquals(first, second, 0, first.length());
    }

    /**
     * Compare file contents in range. Both files must be files (not directories) and exist.
     *
     * @param first   first file
     * @param second  second file
     * @param off     compare from offset
     * @param len     comparison length
     * @return boolean  true if files are equal, otherwise false
     * @throws IOException  error in function
     */
    public static boolean contentEquals(@NotNull final File first, @NotNull final File second, final long off,
        final long len) throws IOException {
        if (len < 1) {
            throw new IllegalArgumentException();
        }
        if (off < 0) {
            throw new IllegalArgumentException();
        }
        if (!first.exists() || !second.exists()) {
            return false;
        }
        if (!first.isFile() || !second.isFile()) {
            return false;
        }
        if (first.getCanonicalPath().equals(second.getCanonicalPath())) {
            return true;
        }

        FileInputStream firstInput = null;
        FileInputStream secondInput = null;
        boolean result;
        try {
            firstInput = new FileInputStream(first);
            secondInput = new FileInputStream(second);
            result = IOUtils2.contentEquals(firstInput, secondInput, off, len);
        } finally {
             IOUtils.closeQuietly(firstInput);
             IOUtils.closeQuietly(secondInput);
        }
        return result;
    }

}
