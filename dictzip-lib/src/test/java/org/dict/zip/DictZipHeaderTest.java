/*
 * DictZip library.
 *
 * Copyright (C) 2016,2019-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package org.dict.zip;

import org.dict.zip.DictZipHeader.CompressionLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tokyo.northside.io.FileUtils2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test for DictZip Header.
 * @author Hiroshi Miura
 */
public class DictZipHeaderTest {

    private String toStringExpResult() {
        return "\nHeader length = 49" + "\nSubfield ID = RA" + "\nSubfield length = 20"
               + "\nSubfield version = 1" + "\nChunk length = 58315" + "\nNumber of chunks = 7"
               + "\nLength of member = 136856";
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeaderString() throws Exception {
        String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.toString(), toStringExpResult());
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeaderType() throws Exception {
        String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getType(), "dzip");
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeaderFilename() throws Exception {
        String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getFilename(), "results.dict");
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeaderChunkCount() throws Exception {
        String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getChunkCount(), 7);
    }

   /**
     * Test of readHeader method, of class DictZipHeader.
     *
    * @param tempDir JUnit5 temporary directory.
    * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeaderNonGZip(@TempDir final Path tempDir) throws Exception {
        byte[] b = {3, 5, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        File testFile = tempDir.resolve("DictZipOutCon.txt.dz").toFile();
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
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @param tempDir JUnit5 temporary directory.
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReadHeaderGZipMagic(@TempDir final Path tempDir) throws Exception {
        byte[] b = {(byte) 0x1f, (byte) 0x8b, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        File testFile = tempDir.resolve("DictZipOutCon.txt.dz").toFile();
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
    }

    /**
     * Test of readHeader method on default_compression level file.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReaderHeaderDefaultCompression() throws Exception {
        String dataFile = this.getClass().getResource("/default.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getExtraFlag(), CompressionLevel.DEFAULT_COMPRESSION);
    }

    /**
     * Test of readHeader method on fast_compression level file.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReaderHeaderFastCompression() throws Exception {
        String dataFile = this.getClass().getResource("/fast.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getExtraFlag(), CompressionLevel.BEST_SPEED);
    }

    /**
     * Test of readHeader method on best_compression level file.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReaderHeaderBestCompression() throws Exception {
        String dataFile = this.getClass().getResource("/best.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getExtraFlag(), CompressionLevel.BEST_COMPRESSION);
    }

    /**
     * Test of readHeader method on wrong compression level file.
     *
     * @throws java.lang.Exception if file I/O error occurred.
     */
    @Test
    public void testReaderHeaderWrongCompression() throws Exception {
        String dataFile = this.getClass().getResource("/corrupt.dict.dz").getFile();
        boolean r = false;
        try {
            DictZipHeader result = DictZipHeader.readHeader(dataFile);
        } catch (IOException ex) {
            // expected
            r = true;
        }
        assertTrue(r, "IOException Expected and got");
    }

    /**
     * Test of getMemberLength method.
     * @throws Exception when fails.
     */
    @Test
    public void testGetMemberLength() throws Exception {
        String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(result.getMemberLength(), 136856);
    }

    /**
     * Test of writeHeader method.
     *
     * @param tempDir JUnit5 temporary directory.
     * @throws Exception if file I/O error occurred.
     */
    @Test
    public void testWriteHeader(@TempDir final Path tempDir) throws Exception {
        File testFile = tempDir.resolve("DictZipHeader.dz").toFile();
        FileOutputStream outFile = new FileOutputStream(testFile);
        DictZipHeader header = new DictZipHeader(1024, 256);
        header.setExtraFlag(CompressionLevel.BEST_COMPRESSION);
        header.setComment("test comment");
        header.setFilename("test.dict");
        header.setHeaderOS(DictZipHeader.OperatingSystem.AMIGA);
        header.setMtime(System.currentTimeMillis() / 1000);
        header.setHeaderCRC(true);
        header.chunks[0] = 55;
        header.chunks[1] = 55 + 100;
        header.chunks[2] = 55 + 100 + 128;
        header.chunks[3] = 55 + 100 + 128 + 198; // 512
        DictZipHeader.writeHeader(header, outFile);
        outFile.close();
        String expectedHeader = this.getClass().getResource("/test.header.dz").getFile();
        assertTrue(FileUtils2.contentEquals(testFile, new File(expectedHeader), 8, 45));
        testFile.deleteOnExit();
    }
}
