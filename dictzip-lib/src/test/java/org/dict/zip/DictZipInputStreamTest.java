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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;


/**
 * Test of DictZipInputStream.
 * @author Hiroshi Miura
 */
public class DictZipInputStreamTest {

    private final String dataFile = this.getClass().getResource("/test.dict.dz").getFile();
    private RandomAccessInputStream in;
    private DictZipInputStream din;
    private DictZipHeader header;

    /**
     * Open output stream.
     * @throws Exception
     */
    @BeforeTest
    public void setUp() throws Exception {
        in = new RandomAccessInputStream(dataFile, "r");
        din = new DictZipInputStream(in);
    }

    /**
     * Close output stream.
     * @throws Exception if I/O error occured.
     */
    @AfterTest
    public void tearDown() throws Exception {
        din.close();
        in.close();
    }

    private synchronized void getDZHeader(DictZipInputStream din) throws IOException {
        header = din.readHeader();
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        int len = 10;
        getDZHeader(din);
        byte[] buf = new byte[len];
        byte[] expResult = {0x70, 0x72, (byte) 0xc3, (byte) 0xa9, 0x70, 0x2e, 0x20, 0x3a, 0x20, 0x2b};
        din.read(buf, 0, len);
        assertTrue(Arrays.equals(expResult, buf));
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test
    public void testRead_with_seek() throws Exception {
        System.out.println("read with seek");
        int start = 0x20;
        int len = 10;
        getDZHeader(din);
        din.seek(start);
        byte[] buf = new byte[len];
        byte[] expResult = {
            0x61, 0x70, 0x72, (byte) 0xc3, (byte) 0xa8, 0x73, 0x20, 0x75, 0x6e, 0x20
        };
        din.read(buf, 0, len);
        assertTrue(Arrays.equals(buf, expResult));
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFully_byteArr() throws Exception {
        System.out.println("readFully");
        int start = 1;
        int len = 10;
        getDZHeader(din);
        int off = header.getOffset(start);
        long pos = header.getPosition(start);
        in.seek(pos);
        byte[] buf = new byte[off + len];
        din.readFully(buf);
    }

    /**
     * Test of readFully method, of class DictZipInputStream.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadFully_3args() throws Exception {
        System.out.println("readFully");
        int start = 1;
        int len = 10;
        getDZHeader(din);
        int off = header.getOffset(start);
        long pos = header.getPosition(start);
        in.seek(pos);
        byte[] buf = new byte[off + len];
        din.readFully(buf, off, len);
    }

    /**
     * Test of readHeader method, of class DictZipInputStream.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadHeader() throws Exception {
        System.out.println("readHeader");
        header = din.readHeader();
        StringBuilder sb = new StringBuilder();
        sb.append("\nHeader length = 49");
        sb.append("\nSubfield ID = RA");
        sb.append("\nSubfield length = 20");
        sb.append("\nSubfield version = 1");
        sb.append("\nChunk length = 58315");
        sb.append("\nNumber of chunks = 7");
        String expResult = sb.toString();
        assertEquals(expResult, header.toString());
    }

}
