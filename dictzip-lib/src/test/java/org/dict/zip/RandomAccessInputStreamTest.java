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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test for project.
 *
 * @author Hiroshi Miura
 */
public class RandomAccessInputStreamTest {

    private final String dataFile = this.getClass().getResource("/test.dict.dz").getFile();

    /**
     * Test of available method, of class RandomAccessInputStream.
     * @throws Exception when i/o error.
     */
    @Test
    public void testAvailable() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        int expResult = 136856;
        int result = instance.available();
        assertEquals(result, expResult);
    }

    /**
     * Test of close method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testClose() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        instance.close();
    }

    /**
     * Test of getLength method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testGetLength() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        int expResult = 136856;
        int result = instance.getLength();
        assertEquals(result, expResult);
    }

    /**
     * Test of getPos method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testGetPos() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        int expResult = 0;
        int result = instance.getPos();
        assertEquals(result, expResult);
    }

    /**
     * Test of mark method, of class RandomAccessInputStream.
     */
    @Test
    public void testMark() {
        int markpos = 0;
        try {
            RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
            instance.mark(markpos);
        } catch (Exception ex) {
            Assertions.fail("get exception.");
        }
    }

    /**
     * Test of markSupported method, of class RandomAccessInputStream.
     */
    @Test
    public void testMarkSupported() {
        try {
            RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
            boolean result = instance.markSupported();
            assertTrue(result);
        } catch (Exception ex) {
            Assertions.fail("get exception.");
        }
    }

    /**
     * Test of read method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testRead0args() throws Exception {
        try {
            RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
            int expResult = 31;
            int result = instance.read();
            assertEquals(result, expResult);
        } catch (Exception ex) {
            Assertions.fail("get exception.");
        }
    }

    /**
     * Test of read method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testRead3args() throws Exception {
        byte[] b = new byte[512];
        int off = 100;
        int len = 256;
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        int expResult = 256;
        int result = instance.read(b, off, len);
        assertEquals(result, expResult);
    }

    /**
     * Test of readFully method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testReadFully() throws Exception {
        byte[] b = new byte[512];
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        instance.readFully(b);
    }

    /**
     * Test of reset method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testReset() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        instance.reset();
    }

    /**
     * Test of seek method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testSeek() throws Exception {
        long pos = 100L;
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        instance.seek(pos);
    }

    /**
     * Test of skip method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testSkip() throws Exception {
        long n = 100L;
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        long expResult = 100L;
        long result = instance.skip(n);
        assertEquals(result, expResult);
    }
}
