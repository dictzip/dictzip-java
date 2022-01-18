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


import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static tokyo.northside.io.FileUtils2.contentEquals;
import org.dict.zip.DictZipHeader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


/**
 * DictData test.
 * @author Hiroshi Miura
 */
public class DictDataTest {
    /**
     * Test of printHeader method, of class DictData.
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testPrintHeader() throws Exception {
        String testFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictData instance = new DictData(testFile, null);
        instance.printHeader();
    }

    /**
     * Test of doZip method, of class DictData.
     * @param tempDir JUnit5 temporary directory support
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoZip(@TempDir final Path tempDir) throws Exception {
        Path testFile = Paths.get(Objects.requireNonNull(
                this.getClass().getResource("/test_dozip.dict")).toURI());
        Path zippedFile = tempDir.resolve("test_dozip.dict.dz");
        DictData instance = new DictData(testFile, zippedFile);
        instance.doZip(DictZipHeader.CompressionLevel.DEFAULT_COMPRESSION);
        Path expectFile = Paths.get(Objects.requireNonNull(
                this.getClass().getResource("/test_dozip.dict.dz.expected")).toURI());
        // There is 7 chunks, so header become 22(basic header length) + 7 * 2(chunks)  + 2(CRC)= 38
        // mtime and crc fields are different on every test, so we compared
        // 1. after mtime to crc
        // 2. after crc.
        assertTrue(contentEquals(zippedFile.toFile(), expectFile.toFile(), 9, 14));
        assertTrue(contentEquals(zippedFile.toFile(), expectFile.toFile(), 39, 512));
    }

    /**
     * Test of doUnzip method, of class DictData.
     * @param tempDir JUnit5 temporary directory support
     * @throws java.lang.Exception if file operation failed.
     */
    @Test
    public void testDoUnzip(@TempDir final Path tempDir) throws Exception {
        String dzFile = this.getClass().getResource("/test.dict.dz").getFile();
        Path decompressed = tempDir.resolve("test.dict");
        long start = 0L;
        int size = 0;
        DictData instance = new DictData(decompressed.toFile(), new File(dzFile));
        instance.doUnzip(start, size);
        String expected = this.getClass().getResource("/test.dict.expected").getFile();
        assertTrue(contentEquals(decompressed.toFile(), new File(expected)));
    }

}
