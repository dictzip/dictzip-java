/*
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import junit.framework.TestCase;
import org.junit.Test;

/**
 *
 * @author miurahr
 */
public class DictZipHeaderTest extends TestCase {

    private final String dataFile = "build/test/classes/data/test.dict.dz";

    private String expResult() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nHeader length = 49");
        sb.append("\nSubfield ID = RA");
        sb.append("\nSubfield length = 20");
        sb.append("\nSubfield version = 1");
        sb.append("\nChunk length = 58315");
        sb.append("\nNumber of chunks = 7");
        return sb.toString();
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurd.
     */
    @Test
    public void testReadHeader_String() throws Exception {
        System.out.println("readHeader");
        DictZipHeader result = DictZipHeader.readHeader(dataFile);
        assertEquals(expResult(), result.toString());
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurd.
     */
    @Test
    public void testReadHeader_NonGZip() throws Exception {
        System.out.println("readHeader");
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
        assertTrue("IOException Expected and got", r);
    }

    /**
     * Test of readHeader method, of class DictZipHeader.
     *
     * @throws java.lang.Exception if file I/O error occurd.
     */
    @Test
    public void testReadHeader_GZipMagic() throws Exception {
        System.out.println("readHeader");
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
        assertTrue("IOException Expected and got", r);
    }
}
