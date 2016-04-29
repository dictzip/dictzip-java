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

import static org.testng.Assert.*;

import org.testng.SkipException;
import org.testng.annotations.*;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import static tokyo.northside.io.IOUtils2.contentEquals;

/**
 * Test of DictZipInputStream.
 * @author Hiroshi Miura
 */
public class DictZipInputStreamTest {

    private final String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
    private DictZipInputStream din;

    @BeforeMethod
    public void rewind() throws Exception {
        if (din != null) {
            din.seek(0);
        }
    }

    /**
     * Test constructor.
     * @throws Exception
     */
    @Test (groups = "init", dependsOnMethods = { "testConstructor_fromFilename",
            "testConstructor_defaultBufSize"})
    public void testConstructor() throws Exception {
        din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"), 65534);
    }

    /**
     * Test constructor.
     * @throws Exception
     */
    @Test (groups = "init")
    public void testConstructor_defaultBufSize() throws Exception {
        din = new DictZipInputStream(new RandomAccessInputStream(dataFile, "r"));
    }

    /**
     * Test constructor from filename.
     * @throws Exception
     */
    @Test (groups = "init")
    public void testConstructor_fromFilename() throws Exception {
        din = new DictZipInputStream(dataFile);
    }

    /**
     * Test close of stream.
     * @throws Exception if I/O error occurred.
     */
    @Test (groups = "exit", dependsOnGroups = { "test" })
    public void testClose() throws Exception {
        din.close();
        din = null;
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead() throws Exception {
        System.out.println("read");
        int len = 10;
        byte[] buf = new byte[len];
        byte[] expResult = {0x70, 0x72, (byte) 0xc3, (byte) 0xa9, 0x70, 0x2e, 0x20, 0x3a, 0x20, 0x2b};
        din.read(buf, 0, len);
        assertTrue(Arrays.equals(expResult, buf));
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead_with_seek() throws Exception {
        System.out.println("read with seek");
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

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead_null() throws Exception {
        System.out.println("read null buffer");
        int len = 10;
        byte[] buf = null;
        boolean r = false;
        try {
            din.read(buf, 0, len);
            fail("Should be throw exception.");
        } catch (NullPointerException e) {
            // expected.
            r = true;
        }
        assertTrue(r, "Got NullPointerException when buffer is null");
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead_outOfBound() throws Exception {
        System.out.println("read out of buffer size");
        int len = 10;
        byte[] buf = new byte[len];
        boolean r = false;
        try {
            din.read(buf, 0, len + 10);
            fail("Should be throw exception.");
        } catch (IndexOutOfBoundsException e) {
            // expected.
            r = true;
        }
        assertTrue(r, "Got IndexOutOfBoundException when size is over the buffer size");
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead_zeroSize() throws Exception {
        System.out.println("read zero size");
        int len = 512;
        byte[] buf = new byte[len];
        int size = din.read(buf, 0, 0);
        assertEquals(size, 0);
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead_with_seekLast() throws Exception {
        System.out.println("read with seek to last");
        int start = 383273;
        int len = 512;
        din.seek(start);
        byte[] buf = new byte[len];
        din.read(buf, 0, len);
        int result = din.read(buf, 0, len);
        assertEquals(result, -1);
    }

   /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws java.lang.Exception
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testReadFully_byteArr() throws Exception {
        System.out.println("readFully");
        int start = 100;
        int len = 10;
        din.seek(start);
        byte[] buf = new byte[len];
        din.readFully(buf);
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws java.lang.Exception
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testReadFully_3args() throws Exception {
        System.out.println("readFully");
        int start = 200;
        int len = 10;
        din.seek(start);
        byte[] buf = new byte[len];
        din.readFully(buf, 0, len);
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws java.lang.Exception
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testReadFully_readTrailer() throws IOException {
        System.out.println("readFully and readTrailer");
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

    /**
     * Test getComplength method.
     * @throws IOException
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testGetCompLength() throws IOException {
        System.out.println("getCompLength");
        assertEquals(din.getCompLength(), 136856);
    }

    /**
     * Test getMtime method.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testGetMtime() throws IOException {
        System.out.println("getMtime");
        assertEquals(din.getMtime(), 1193780332);
    }

    /**
     * Test getChunkLength method.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testGetChunkLength() throws IOException {
        System.out.println("getChunkLength");
        assertEquals(din.getChunkLength(), 58315);
    }

    /**
     * Test getChunkCount method.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testGetChunkCount() throws IOException {
        System.out.println("getChunkCount");
        assertEquals(din.getChunkCount(), 7);
    }

    /**
     * Test getFilename method.
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testGetFilename() throws IOException {
        System.out.println("getFilename");
        assertEquals(din.getFilename(), "results.dict");
    }

   /**
     * Test readFully with large seek
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testReadFully_seek2() throws Exception {
        System.out.println("readFully_seek2");
        int start = 56003;
        int len = 195;
        try {
            din.seek(start);
        } catch (IOException ioe) {
            fail("Unexpected IOException");
        }
        byte[] buf = new byte[len];
        try {
            din.readFully(buf);
        } catch (EOFException eofe) {
            fail("Unexpected EOF");
        }
    }

   /**
     * Test with large  seek
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor" })
    public void testRead_seek2() throws Exception {
        System.out.println("read_seek2");
        int start = 56003;
        int len = 195;
        try {
            din.seek(start);
        } catch (IOException ioe) {
            fail("Unexpected IOException");
        }
        byte[] buf = new byte[len];
        int result=0;
        try {
            result = din.read(buf, 0, len);
        } catch (EOFException eofe) {
            fail("Unexpected EOF");
        }
        assertEquals(result, len);
    }
    /**
     * Test with large seek comparison content
     */
    @Test (groups = "test", dependsOnMethods = { "testConstructor"})
    public void testRead_seek3() throws Exception {
         System.out.println("read_seek2");
        int start = 56003;
        int len = 195;
        try {
            din.seek(start);
        } catch (IOException ioe) {
            fail("Unexpected IOException");
        }
        FileInputStream in2 = new FileInputStream(this.getClass().getResource("/test.dict.expected").getFile());
        in2.skip(start);
        assertTrue(contentEquals(din, in2, 0, len));
    }
}
