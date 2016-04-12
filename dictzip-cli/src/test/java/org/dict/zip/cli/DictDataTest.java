/*
 * DictZip library test.
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

import static org.testng.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.dict.zip.DictZipHeader;

import org.testng.annotations.Test;


/**
 * DictData test.
 * @author Hiroshi Miura
 */
public class DictDataTest {
    /**
     * Test of printHeader method, of class DictData
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testPrintHeader() throws Exception {
        System.out.println("printHeader");
        URL url = this.getClass().getResource("/test.dict.dz");
        String testFile = url.getFile();
        DictData instance = new DictData(testFile, null);
        instance.printHeader();
    }

    /**
     * Test of doZip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoZip() throws Exception {
        System.out.println("doZip");
        String testFile = this.getClass().getResource("/test_dozip.dict").getFile();
        String zippedFile = DictZipUtils.compressedFileName(testFile);
        DictData instance = new DictData(testFile, zippedFile);
        instance.doZip(DictZipHeader.CompressionLevel.DEFAULT_COMPRESSION);
        File resultFile = new File(testFile + ".dz");
        File expectFile = new File(this.getClass().getResource("/test_dozip.dict.dz.expected").getFile());
        assertTrue(isFileBinaryEquals(resultFile, expectFile, 10, 512));
        resultFile.deleteOnExit();
    }

    /**
     * Test of doUnzip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoZip_best() throws Exception {
        System.out.println("doZip_best");
        String testFile = this.getClass().getResource("/test_dozip.dict").getFile();
        String zippedFile = testFile + "_best.dz";
        DictData instance = new DictData(testFile, zippedFile);
        instance.doZip(DictZipHeader.CompressionLevel.BEST_COMPRESSION);
        File resultFile = new File(zippedFile);
        File expectFile = new File(this.getClass().getResource("/test_dozip.dict.dz.expected.best")
                 .getFile());
        assertTrue(isFileBinaryEquals(resultFile, expectFile, 10, 512));
        resultFile.deleteOnExit();
    }

    /**
     * Test of doUnzip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoZip_fast() throws Exception {
        System.out.println("doZip_fast");
        String testFile = this.getClass().getResource("/test_dozip.dict").getFile();
        String zippedFile = testFile + "_fast.dz";
        DictData instance = new DictData(testFile, zippedFile);
        instance.doZip(DictZipHeader.CompressionLevel.BEST_SPEED);
        File resultFile = new File(zippedFile);
        File expectFile = new File(this.getClass().getResource("/test_dozip.dict.dz.expected.fast")
                 .getFile());
        assertTrue(isFileBinaryEquals(resultFile, expectFile, 10, 512));
        resultFile.deleteOnExit();
    }

    /**
     * Test of doUnzip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoUnzip() throws Exception {
        System.out.println("doUnzip");
        URL url = this.getClass().getResource("/test.dict.dz");
        String dzFile = url.getFile();
        String file = DictZipUtils.uncompressedFileName(dzFile);
        long start = 0L;
        int size = 0;
        DictData instance = new DictData(file, dzFile);
        instance.doUnzip(start, size);
        URL resultUrl = this.getClass().getResource("/test.dict");
        File resultFile = new File(resultUrl.getFile());
        URL expectedUrl = this.getClass().getResource("/test.dict.expected");
        assertTrue(isFileBinaryEquals(resultFile, new File(expectedUrl.getFile())));
        resultFile.deleteOnExit();
    }

    /**
     * Compare binary files. Both files must be files (not directories) and exist.
     *
     * @param first  - first file
     * @param second - second file
     * @return boolean - true if files are binery equal
     * @throws IOException - error in function
     */
    static boolean isFileBinaryEquals(File first, File second) throws IOException {
        return isFileBinaryEquals(first, second, 0, first.length());
    }

    /**
     * Compare binary files (for test). Both files must be files (not directories) and exist.
     *
     * @param first  - first file
     * @param second - second file
     * @param off    - compare from offset
     * @param len    - comparison length
     * @return boolean - true if files are binery equal
     * @throws IOException - error in function
     */
    static boolean isFileBinaryEquals(File first, File second, final long off, final long len) throws IOException {
        boolean result = false;
        final int BUFFER_SIZE = 65536;
        final int COMP_SIZE = 512;

        if (len <= 1) {
            throw new IllegalArgumentException();
        }

        if ((first.exists()) && (second.exists())
                && (first.isFile()) && (second.isFile())) {
            if (first.getCanonicalPath().equals(second.getCanonicalPath())) {
                result = true;
            } else {
                FileInputStream firstInput;
                FileInputStream secondInput;
                BufferedInputStream bufFirstInput = null;
                BufferedInputStream bufSecondInput = null;

                try {
                    firstInput = new FileInputStream(first);
                    secondInput = new FileInputStream(second);
                    bufFirstInput = new BufferedInputStream(firstInput, BUFFER_SIZE);
                    bufSecondInput = new BufferedInputStream(secondInput, BUFFER_SIZE);

                    byte[] firstBytes = new byte[COMP_SIZE];
                    byte[] secondBytes = new byte[COMP_SIZE];

                    bufFirstInput.skip(off);
                    bufSecondInput.skip(off);

                    long readLengthTotal = 0;
                    result = true;
                    while (readLengthTotal < len) {
                        int readLength = COMP_SIZE;
                        if (len - readLengthTotal < (long) COMP_SIZE) {
                            readLength = (int) (len - readLengthTotal);
                        }
                        int lenFirst = bufFirstInput.read(firstBytes, 0, readLength);
                        int lenSecond = bufSecondInput.read(secondBytes, 0, readLength);
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
                } catch (RuntimeException e) {
                    throw e;
                } finally {
                    try {
                        if (bufFirstInput != null) {
                            bufFirstInput.close();
                        }
                    } finally {
                        if (bufSecondInput != null) {
                            bufSecondInput.close();
                        }
                    }
                }
            }
        }

        return result;
    }

}
