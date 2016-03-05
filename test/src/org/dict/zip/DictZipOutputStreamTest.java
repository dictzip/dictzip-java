/*
 * Copyright (C) 2016 miurahr
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.Checksum;

/**
 *
 * @author Hiroshi Miura
 */
public class DictZipOutputStreamTest extends junit.framework.TestCase {

    public DictZipOutputStreamTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of close method, of class DictZipOutputStream.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        byte byteArray[] = {3, 5, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        try {
            FileOutputStream outFile = new FileOutputStream(
                    File.createTempFile("DictZipOutCon", ".txt"));
            TestDictZipOutputStream outDictZip = new TestDictZipOutputStream(outFile, byteArray.length);
            outDictZip.close();
            int r = 0;
            try {
                outDictZip.write(byteArray, 0, 1);
            } catch (IOException e) {
                r = 1;
            }
            assertEquals(
                    "DictZip instance can still be used after close is called", 1,
                    r);
        } catch (IOException e) {
            fail("an IO error occured while trying to find the output file or creating DictZip constructor");
        }
    }

    /**
     * Test of deflate method, of class DictZipOutputStream.
     */
    @Test
    public void testDeflate() throws Exception {
        System.out.println("deflate");
        byte byteArray[] = {3, 5, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        FileOutputStream outFile = new FileOutputStream(
                File.createTempFile("DictZipOutCon", ".txt"));
        TestDictZipOutputStream instance = new TestDictZipOutputStream(outFile, byteArray.length);
        instance.deflate();
    }

    /**
     * Test of write method, of class DictZipOutputStream.
     */
    @Test
    public void testWrite_3args() throws Exception {
        System.out.println("write");
        byte b[] = {3, 5, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        int off = 0;
        int len = 0;
        FileOutputStream outFile = new FileOutputStream(
                File.createTempFile("DictZipOutCon", ".txt"));
        TestDictZipOutputStream instance = new TestDictZipOutputStream(outFile, 512, 100);
        instance.write(b, off, len);
    }

    /**
     * Test of write method, of class DictZipOutputStream.
     */
    @Test
    public void testWrite_int() {
        System.out.println("write");
        int b = 100;
        TestDictZipOutputStream instance = null;
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(
                    File.createTempFile("DictZipOutCon", ".txt"));
            instance = new TestDictZipOutputStream(outFile, 10);
            instance.write(b);
        } catch (Exception e) {
            fail("Unwanted exception happens.");
        }
    }

    /**
     * Test of finish method, of class DictZipOutputStream.
     */
    @Test
    public void testFinish() {
        System.out.println("finish");
        byte byteArray[] = {3, 5, 2, 'r', 'g', 'e', 'f', 'd', 'e', 'w'};
        TestDictZipOutputStream instance = null;
        FileOutputStream outFile = null;
        try {
            outFile = new FileOutputStream(
                    File.createTempFile("DictZipOutCon", ".txt"));
            instance = new TestDictZipOutputStream(outFile, 10);
            instance.finish();
            int r = 0;
            try {
                instance.write(byteArray, 0, 1);
            } catch (Exception ex) {
                r = 1;
            }
            assertEquals("DictZip instance can still be used after finish is called",
                    1, r);
        } catch (Exception ex) {
            fail("an IO error occured while trying to find the output file or creating DictZip constructor");
        }
        try {
            outFile = new FileOutputStream("GZIPOutFinish.txt");
            instance = new TestDictZipOutputStream(outFile, 10);
            outFile.close();

            instance.finish();
            fail("Expected IOException");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * Stub for DictZipOutputStream, for test
     */
    class TestDictZipOutputStream extends DictZipOutputStream {

        TestDictZipOutputStream(OutputStream out, long dataSize) throws IOException {
            super(out, dataSize);
        }

        TestDictZipOutputStream(OutputStream out, int size, long dataSize) throws IOException {
            super(out, size, dataSize);
        }

        Checksum getChecksum() {
            return crc;
        }
    }
}
