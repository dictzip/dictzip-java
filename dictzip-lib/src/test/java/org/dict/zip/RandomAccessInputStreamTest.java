/*
 * DictZip library.
 *
 * Copyright (C) 2016,2019,2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GPL-2.0-or-later WITH Classpath-exception-2.0
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
        long expResult = 136856L;
        long result = instance.length();
        assertEquals(result, expResult);
    }

    /**
     * Test of getPos method, of class RandomAccessInputStream.
     * @throws Exception when error.
     */
    @Test
    public void testGetPos() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        long expResult = 0L;
        long result = instance.position();
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

    @Test
    public void testLastByte() throws Exception {
        RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r");
        instance.seek(136854);
        int c = instance.read();
        assertEquals(5, c);
        c = instance.read();
        assertEquals(0, c);
        c = instance.read();
        assertEquals(-1, c);
        long pos = instance.position();
        assertEquals(136856, pos);
    }

    @Test
    public void testLastBytes() throws Exception {
        long pos;
        try (RandomAccessInputStream instance = new RandomAccessInputStream(dataFile, "r")) {
            instance.seek(136848);
            byte[] buf = new byte[9];
            int len = instance.read(buf, 0, buf.length);
            assertEquals(8, len);
            assertEquals(5, buf[len - 2]);
            assertEquals(0, buf[len - 1]);
            pos = instance.position();
        }
        assertEquals(136856, pos);
    }
}
