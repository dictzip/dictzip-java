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
package org.dict.zip;

import java.io.IOException;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of DictZipInputStream.
 * @author Hiroshi Miura
 */
public class DictZipInputStreamTest extends TestCase {

    private final String dataFile = "build/test/classes/data/test.dict.dz";
    private RandomAccessInputStream in;
    private DictZipInputStream din;
    private DictZipHeader header;

    /**
     * Open output stream.
     * @throws Exception
     */
    @Before
    @Override
    public void setUp() throws Exception {
        in = new RandomAccessInputStream(dataFile, "r");
        din = new DictZipInputStream(in);
    }

    /**
     * Close output stream.
     * @throws Exception if I/O error occured.
     */
    @After
    @Override
    public void tearDown() throws Exception {
        din.close();
        in.close();
    }

    private synchronized void getDZHeader(DictZipInputStream din) throws IOException {
        if (header == null) {
            header = din.readHeader();
        }
    }

    /**
     * Test of read method, of class DictZipInputStream.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        int start = 1;
        int len = 10;
        getDZHeader(din);
        int off = header.getOffset(start);
        long pos = header.getPosition(start);
        in.seek(pos);
        byte[] buf = new byte[off + len];
        int expResult = len;
        int result = din.read(buf, off, len);
        assertEquals(expResult, result);
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
