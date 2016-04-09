/*
 * DictZip Library test.
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
package org.dict.zip;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Test for DictZip Header.
 * @author Hiroshi Miura
 */
public class DictZipHeaderTest {

    private String toStringExpResult() {
        return "\nHeader length = 49" +
                "\nSubfield ID = RA" +
                "\nSubfield length = 20" +
                "\nSubfield version = 1" +
                "\nChunk length = 58315" +
                "\nNumber of chunks = 7";
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeader_String() throws Exception {
        System.out.println("readHeader");
        String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(toStringExpResult(), result.toString());
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeader_NonGZip() throws Exception {
        System.out.println("readHeader / not dictzip file");
        byte b[] = {3, 5, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        File testFile = File.createTempFile("DictZipOutCon", ".txt.dz");
        FileOutputStream outFile = new FileOutputStream(testFile);
        outFile.write(b);
        outFile.close();
        boolean r = false;
        try {
            DictZipHeader result = DictZipHeader.readHeader(testFile.getAbsolutePath());
        } catch (IOException ex) {
            // expected
            r = true;
        }
        assertTrue(r, "IOException Expected and got");
        testFile.deleteOnExit();
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeader_GZipMagic() throws Exception {
        System.out.println("readHeader / wrong gzip magic");
        byte b[] = {(byte) 0x1f, (byte) 0x8b, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        File testFile = File.createTempFile("DictZipOutCon", ".txt.dz");
        FileOutputStream outFile = new FileOutputStream(testFile);
        outFile.write(b);
        outFile.close();
        boolean r = false;
        try {
            DictZipHeader result = DictZipHeader.readHeader(testFile.getAbsolutePath());
        } catch (IOException ex) {
            // expected
            r = true;
        }
        assertTrue(r, "IOException Expected and got");
        testFile.deleteOnExit();
    }

    /**
     * Test of readHeader method on fast_compression level file.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReaderHeader_FastCompression() throws Exception {
        System.out.println("readHeader of fast compression");
        String dataFile = this.getClass().getResource("/fast.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getExtraFlag(), DictZipHeader.CompressionLevel.BEST_SPEED);
    }

    /**
     * Test of readHeader method on best_compression level file.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReaderHeader_BestCompression() throws Exception {
        System.out.println("readHeader of best compression");
        String dataFile = this.getClass().getResource("/best.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getExtraFlag(), DictZipHeader.CompressionLevel.BEST_COMPRESSION);
    }
}
