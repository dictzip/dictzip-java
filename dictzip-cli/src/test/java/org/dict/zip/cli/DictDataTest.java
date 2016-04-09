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

import java.io.File;
import java.net.URL;

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
        assertTrue(StaticUtils.isFileBinaryEquals(resultFile, expectFile, 10, 512));
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
        assertTrue(StaticUtils.isFileBinaryEquals(resultFile, expectFile, 10, 512));
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
        assertTrue(StaticUtils.isFileBinaryEquals(resultFile, expectFile, 10, 512));
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
        assertTrue(StaticUtils.isFileBinaryEquals(resultFile, new File(expectedUrl.getFile())));
        resultFile.deleteOnExit();
    }
}
