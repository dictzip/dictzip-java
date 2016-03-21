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

import java.io.ByteArrayOutputStream;
import java.io.File;

import junit.framework.TestCase;
import org.junit.Test;

import org.apache.commons.io.FileUtils;

/**
 *
 * @author miurahr
 */
public class DictDataTest extends TestCase {
    
    public DictDataTest() {
    }

    public static void compareBinary(File f1, File f2) throws Exception {
        ByteArrayOutputStream d1 = new ByteArrayOutputStream();
        FileUtils.copyFile(f1, d1);

        ByteArrayOutputStream d2 = new ByteArrayOutputStream();
        FileUtils.copyFile(f2, d2);

        assertEquals(d1.size(), d2.size());
        byte[] a1 = d1.toByteArray();
        byte[] a2 = d2.toByteArray();
        for (int i = 0; i < d1.size(); i++) {
          assertEquals(a1[i], a2[i]);
        }
    }

    /**
     * Test of printHeader method, of class DictData
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testPrintHeader() throws Exception {
        System.out.println("printHeader");
        DictData instance = new DictData("build/test/classes/data/test.dict.dz", null);
        instance.printHeader();
    }

    /**
     * Test of doZip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoZip() throws Exception {
        System.out.println("doZip");
        File testFile = new File("build/test/classes/data/test2.dict");
        String zippedFile = DictZipUtils.compressedFileName(testFile.getPath());
        DictData instance = new DictData(testFile.getPath(), zippedFile);
        instance.doZip();
        File resultFile = new File(testFile.getPath() + ".dz");
        //compareBinary(resultFile, new File("test/data/test2.dict.dz.expected"));
        resultFile.deleteOnExit();
    }

    /**
     * Test of doUnzip method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoUnzip() throws Exception {
        System.out.println("doUnzip");
        String dzFile = "build/test/classes/data/test.dict.dz";
        String file = DictZipUtils.uncompressedFileName(dzFile);
        long start = 0L;
        int size = 0;
        DictData instance = new DictData(file, dzFile);
        instance.doUnzip(start, size);
        File resultFile = new File("build/test/classes/data/test.dict");
        compareBinary(resultFile, new File("build/test/classes/data/test.dict.expected"));
        resultFile.deleteOnExit();
    }
}
