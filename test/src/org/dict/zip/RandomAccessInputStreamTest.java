/*
 * DictZip library.
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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 * @author Hiroshi Miura
 */
public class RandomAccessInputStreamTest {
    
    public RandomAccessInputStreamTest() {
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
     * Test of available method, of class RandomAccessInputStream.
     */
    @Test
    public void testAvailable() throws Exception {
        System.out.println("available");
        RandomAccessInputStream instance = null;
        int expResult = 0;
        int result = instance.available();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of close method, of class RandomAccessInputStream.
     */
    @Test
    public void testClose() throws Exception {
        System.out.println("close");
        RandomAccessInputStream instance = null;
        instance.close();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLength method, of class RandomAccessInputStream.
     */
    @Test
    public void testGetLength() throws Exception {
        System.out.println("getLength");
        RandomAccessInputStream instance = null;
        int expResult = 0;
        int result = instance.getLength();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPos method, of class RandomAccessInputStream.
     */
    @Test
    public void testGetPos() throws Exception {
        System.out.println("getPos");
        RandomAccessInputStream instance = null;
        int expResult = 0;
        int result = instance.getPos();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of mark method, of class RandomAccessInputStream.
     */
    @Test
    public void testMark() {
        System.out.println("mark");
        int markpos = 0;
        RandomAccessInputStream instance = null;
        instance.mark(markpos);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of markSupported method, of class RandomAccessInputStream.
     */
    @Test
    public void testMarkSupported() {
        System.out.println("markSupported");
        RandomAccessInputStream instance = null;
        boolean expResult = false;
        boolean result = instance.markSupported();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class RandomAccessInputStream.
     */
    @Test
    public void testRead_0args() throws Exception {
        System.out.println("read");
        RandomAccessInputStream instance = null;
        int expResult = 0;
        int result = instance.read();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class RandomAccessInputStream.
     */
    @Test
    public void testRead_3args() throws Exception {
        System.out.println("read");
        byte[] b = null;
        int off = 0;
        int len = 0;
        RandomAccessInputStream instance = null;
        int expResult = 0;
        int result = instance.read(b, off, len);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of readFully method, of class RandomAccessInputStream.
     */
    @Test
    public void testReadFully() throws Exception {
        System.out.println("readFully");
        byte[] b = null;
        RandomAccessInputStream instance = null;
        instance.readFully(b);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of reset method, of class RandomAccessInputStream.
     */
    @Test
    public void testReset() throws Exception {
        System.out.println("reset");
        RandomAccessInputStream instance = null;
        instance.reset();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of seek method, of class RandomAccessInputStream.
     */
    @Test
    public void testSeek() throws Exception {
        System.out.println("seek");
        long pos = 0L;
        RandomAccessInputStream instance = null;
        instance.seek(pos);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of skip method, of class RandomAccessInputStream.
     */
    @Test
    public void testSkip() throws Exception {
        System.out.println("skip");
        long n = 0L;
        RandomAccessInputStream instance = null;
        long expResult = 0L;
        long result = instance.skip(n);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
