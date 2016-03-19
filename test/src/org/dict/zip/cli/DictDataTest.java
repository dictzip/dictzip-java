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

import java.io.File;

import junit.framework.TestCase;
import org.junit.Test;


/**
 *
 * @author miurahr
 */
public class DictDataTest extends TestCase {
    
    public DictDataTest() {
    }

    /**
     * Test of open method, of class DictData.
     * @throws java.lang.Exception if file open failed.
     */
    @Test
    public void testOpen() throws Exception {
        System.out.println("open");
        DictData.OpsMode mode = DictData.OpsMode.WRITE;
        File testFile = File.createTempFile("DictZipDictDataTest", ".txt");
        String targetFileName = testFile.getPath();
        DictData instance = new DictData(targetFileName);
        instance.open(mode);
        testFile.deleteOnExit();
    }

    /**
     * Test of printHeader method, of class DictData
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testPrintHeader() throws Exception {
        System.out.println("printHeader");
        DictData instance = new DictData("test/data/test.dict.dz");
        instance.open(DictData.OpsMode.READ);
        instance.printHeader();
    }

    /**
     * Test of close method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        DictData instance = new DictData("test/data/test.dict.dz");
        instance.open(DictData.OpsMode.READ);
        instance.close();
    }

    /**
     * Test of doZip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoZip() throws Exception {
        System.out.println("doZip");
        DictData.OpsMode mode = DictData.OpsMode.WRITE;
        File testFile = new File("test/data/test2.dict");
        String zippedFile = DictZipUtils.compressedFileName(testFile.getPath());
        DictData instance = new DictData(testFile.getPath());
        instance.open(mode);
        instance.doZip(zippedFile);
        File resultFile = new File(testFile.getPath() + ".dz");
        resultFile.deleteOnExit();
    }

    /**
     * Test of doUnzip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoUnzip() throws Exception {
        System.out.println("doUnzip");
        String dzFile = "test/data/test.dict.dz";
        String file = DictZipUtils.uncompressedFileName(dzFile);
        long start = 0L;
        int size = 0;
        DictData instance = new DictData(dzFile);
        instance.open(DictData.OpsMode.READ);
        instance.doUnzip(file, start, size);
        File resultFile = new File("test/data/test.dict");
        resultFile.deleteOnExit();
    }

    /**
     * Test of removeTarget method, of class DictData.
     * @throws java.lang.Exception
     */
    @Test
    public void testRemoveTarget() throws Exception {
        System.out.println("removeTarget");
        File testFile = File.createTempFile("DictZipTest", ".txt");
        DictData instance = new DictData(testFile.getPath());
        boolean expResult = true;
        boolean result = instance.removeTarget();
        assertEquals(expResult, result);
    }
    
}
