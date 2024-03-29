/*
 * FileUtils library.
 *
 * Copyright (C) 2016,2022 Hiroshi Miura
 *
 *  SPDX-License-Identifier: Apache-2.0
 */

package tokyo.northside.io;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * General IO stream manipulation utility.
 * <p>
 * This class provides static utility methods for input/output operations.
 * <ul>
 * <li>contentEquals - these methods compare the content of two streams
 * </ul>
 * <p>
 * The methods in this class that read a stream are buffered internally.
 * This means that there is no cause to use a <code>BufferedInputStream</code>
 * or <code>BufferedReader</code>. The default buffer size of 4K has been shown
 * to be efficient in tests.
 * <p>
 * Wherever possible, the methods in this class do <em>not</em> flush or close
 * the stream. This is to avoid making non-portable assumptions about the
 * streams' origin and further use. Thus the caller is still responsible for
 * closing streams after use.
 * <p>
 * Created by Hiroshi Miura on 16/04/09.
 *
 * @author Hiroshi Miura
 */
public final class IOUtils2 {

   private static final int BUF_LEN = 4096;

    /**
     * Compare the contents of two Streams to determine if they are equal or not.
     *
     * @param first  first input stream.
     * @param second  second input stream.
     * @param off     compare from offset
     * @param len     comparison length
     * @return boolean true if content of input streams are equal, true if streams are equal,
     *     otherwise false.
     * @throws IOException when I/O error occurred.
     */
    public static boolean contentEquals(@NotNull final InputStream first, @NotNull final InputStream second,
                                        final long off, final long len) throws IOException {
        boolean result;

        if (len < 1) {
            throw new IllegalArgumentException();
        }
        if (off < 0) {
            throw new IllegalArgumentException();
        }
        if (first.equals(second)) {
            return false;
        }

        byte[] firstBytes = new byte[BUF_LEN];
        byte[] secondBytes = new byte[BUF_LEN];

        if (off > 0) {
            long totalSkipped = 0;
            while (totalSkipped < off) {
                long skipped = first.skip(off - totalSkipped);
                if (skipped == 0) {
                    throw new IOException("Cannot seek offset bytes.");
                }
                totalSkipped += skipped;
            }
            totalSkipped = 0;
            while (totalSkipped < off) {
                long skipped = second.skip(off - totalSkipped);
                if (skipped == 0) {
                    throw new IOException("Cannot seek offset bytes.");
                }
                totalSkipped += skipped;
            }
        }

        long readLengthTotal = 0;
        result = true;
        while (readLengthTotal < len) {
            int readLength = BUF_LEN;
            if (len - readLengthTotal < (long) BUF_LEN) {
                readLength = (int) (len - readLengthTotal);
            }
            int lenFirst = first.read(firstBytes, 0, readLength);
            int lenSecond = second.read(secondBytes, 0, readLength);
            if (lenFirst != lenSecond) {
                result = false;
                break;
            }
            if ((lenFirst < 0) && (lenSecond < 0)) {
                result = true;
                break;
            }
            readLengthTotal += lenFirst;
            if (lenFirst < firstBytes.length) {
                byte[] a = Arrays.copyOfRange(firstBytes, 0, lenFirst);
                byte[] b = Arrays.copyOfRange(secondBytes, 0, lenSecond);
                if (!Arrays.equals(a, b)) {
                    result = false;
                    break;
                }
            } else if (!Arrays.equals(firstBytes, secondBytes)) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Static utility should not be instantiated.
     */
    private IOUtils2() {
    }
}
