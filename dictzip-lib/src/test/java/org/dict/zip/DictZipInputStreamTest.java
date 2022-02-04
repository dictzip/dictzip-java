/*
 * DictZip library test.
 *
 * Copyright (C) 2016,2019,2022 Hiroshi Miura
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
 *
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */

package org.dict.zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tokyo.northside.io.IOUtils2;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test of DictZipInputStream.
 * @author Hiroshi Miura
 */
public class DictZipInputStreamTest {

    private final String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
    private final String dataFile2 = this.getClass().getResource("/test.dsl.dz").getFile();

    /**
     * Test constructor @TestFactory.
     * @throws Exception when i/o error.
     */
    @Test
    public void testConstructorDefaultBufSize() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"))) {
            assertNotNull(din);
        }
    }

    /**
     * Test constructor from filename.
     * @throws Exception when i/o error.
     */
    @Test
    public void testConstructorFromFilename() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(dataFile)) {
            assertNotNull(din);
        }
    }

    /**
     * Test of read method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testRead() throws Exception {
        int len = 10;
        byte[] buf = new byte[len];
        byte[] expResult = {0x70, 0x72, (byte) 0xc3, (byte) 0xa9, 0x70, 0x2e, 0x20, 0x3a, 0x20, 0x2b};
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            din.read(buf, 0, len);
            assertTrue(Arrays.equals(expResult, buf));
        }
    }

    /**
     * Test of read method with another file, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testRead2() throws Exception {
        int len = 10;
        byte[] buf = new byte[len];
        byte[] expResult = {(byte) 0xFF, (byte) 0xFE, 35, 0, 78, 0, 65, 0, 77, 0};
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile2, "r"))) {
            din.read(buf, 0, len);
            assertTrue(Arrays.equals(expResult, buf));
        }
    }

    /**
     * Test mark and reset methods.
     * @throws Exception when i/o error.
     */
    @Test
    public void testMarkReset() throws Exception {
        int len = 10;
        byte[] buf1 = new byte[len];
        byte[] buf2 = new byte[len];
        byte[] buf3 = new byte[len];
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"))) {
            assertTrue(din.markSupported());
            din.read(buf1, 0, len);
            din.mark(20);
            din.read(buf2, 0, len);
            din.reset();
            din.read(buf3, 0, len);
            assertTrue(Arrays.equals(buf2, buf3));
        }
    }
    /**
     * Test of read method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadWithSeek() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int start = 0x20;
            int len = 10;
            din.seek(start);
            byte[] buf = new byte[len];
            byte[] expResult = {
                    0x61, 0x70, 0x72, (byte) 0xc3, (byte) 0xa8, 0x73, 0x20, 0x75, 0x6e, 0x20
            };
            din.read(buf, 0, len);
            assertTrue(Arrays.equals(buf, expResult));
        }
    }

    /**
     * Test of read method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadNull() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int len = 10;
            byte[] buf = null;
            boolean r = false;
            try {
                din.read(buf, 0, len);
                Assertions.fail("Should be throw exception.");
            } catch (NullPointerException e) {
                // expected.
                r = true;
            }
            assertTrue(r, "Got NullPointerException when buffer is null");
        }
    }

    /**
     * Test of read method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadOutOfBound() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int len = 10;
            byte[] buf = new byte[len];
            boolean r = false;
            try {
                din.read(buf, 0, len + 10);
                Assertions.fail("Should be throw exception.");
            } catch (IndexOutOfBoundsException e) {
                // expected.
                r = true;
            }
            assertTrue(r, "Got IndexOutOfBoundException when size is over the buffer size");
        }
    }

    /**
     * Test of read method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadZeroSize() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int len = 512;
            byte[] buf = new byte[len];
            int size = din.read(buf, 0, 0);
            assertEquals(size, 0);
        }
    }

    /**
     * Test of read method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadWithSeekLast() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int start = 383273;
            int len = 512;
            din.seek(start);
            byte[] buf = new byte[len];
            din.read(buf, 0, len);
            int result = din.read(buf, 0, len);
            assertEquals(result, -1);
        }
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadFullyByteArr() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int start = 100;
            int len = 10;
            din.seek(start);
            byte[] buf = new byte[len];
            din.readFully(buf);
        }
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadFully3args() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int start = 200;
            int len = 10;
            din.seek(start);
            byte[] buf = new byte[len];
            din.readFully(buf, 0, len);
        }
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws IOException when i/o error.
     */
    @Test
    public void testReadFullyReadTrailer() throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            byte[] buf = new byte[512];
            try {
                din.readFully(buf);
            } catch (EOFException e) {
                // Normal, continue
            }
            // read trailer
            din.readTrailer();
            assertEquals(din.getCrc(), 0x024d1f37);
            assertEquals(din.getLength(), 383783);
        }
    }

    /**
     * Test getComplength method.
     * @throws IOException when i/o error.
     */
    @Test
    public void testGetCompLength() throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            System.out.println("getCompLength");
            assertEquals(din.getCompLength(), 136856);
        }
    }

    /**
     * Test getMtime method.
     * @throws IOException when i/o error.
     */
    @Test
    public void testGetMtime() throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            assertEquals(din.getMtime(), 1193780332);
        }
    }

    /**
     * Test getChunkLength method.
     * @throws IOException when i/o error.
     */
    @Test
    public void testGetChunkLength() throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            System.out.println("getChunkLength");
            assertEquals(din.getChunkLength(), 58315);
        }
    }

    /**
     * Test getChunkCount method.
     * @throws IOException when i/o error.
     */
    @Test
    public void testGetChunkCount() throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            System.out.println("getChunkCount");
            assertEquals(din.getChunkCount(), 7);
        }
    }

    /**
     * Test getFilename method.
     * @throws IOException when i/o error.
     */
    @Test
    public void testGetFilename() throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            assertEquals(din.getFilename(), "results.dict");
        }
    }

    /**
     * Test readFully with large seek.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadFullySeek2() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int start = 56003;
            int len = 195;
            try {
                din.seek(start);
            } catch (IOException ioe) {
                Assertions.fail("Unexpected IOException");
            }
            byte[] buf = new byte[len];
            try {
                din.readFully(buf);
            } catch (EOFException eofe) {
                Assertions.fail("Unexpected EOF");
            }
        }
    }

    /**
     * Test with large  seek.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadSeek2() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            System.out.println("read_seek2");
            int start = 56003;
            int len = 195;
            try {
                din.seek(start);
            } catch (IOException ioe) {
                Assertions.fail("Unexpected IOException");
            }
            byte[] buf = new byte[len];
            int result = 0;
            try {
                result = din.read(buf, 0, len);
            } catch (EOFException eofe) {
                Assertions.fail("Unexpected EOF");
            }
            assertEquals(result, len);
        }
    }

    /**
     * Test with large seek comparison content.
     * @throws Exception when i/o error.
     */
    @Test
    public void testReadSeek3() throws Exception {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534)) {
            int start = 56003;
            int len = 195;
            try {
                din.seek(start);
            } catch (IOException ioe) {
                Assertions.fail("Unexpected IOException");
            }
            FileInputStream in2 = new FileInputStream(this.getClass().getResource("/test.dict.expected").getFile());
            in2.skip(start);
            assertTrue(IOUtils2.contentEquals(din, in2, 0, len));
        }
    }
}
