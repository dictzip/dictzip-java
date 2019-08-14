/*
 * DictZip Library test.
 *
 * Copyright (C) 2016,2019 Hiroshi Miura
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

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static tokyo.northside.io.FileUtils2.contentEquals;

/**
 * Created by Hiroshi Miura on 16/04/09.
 */
public class DictZipFileUtilsTest {
    /**
     * Check equals two file contentss.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEquals() throws Exception {
        System.out.println("isFileBinaryEquals");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util1.txt").getFile());
        assertTrue(contentEquals(firstFile, secondFile));
    }

    /**
     * Check equals two file paths which is canonical path.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsSameCanonicalPath() throws Exception {
        System.out.println("isFileBinaryEquals with same canonical path");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        assertTrue(contentEquals(firstFile, secondFile));
    }

    /**
     * Check not equals two file contents.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsFalse() throws Exception {
        System.out.println("isFileBinaryEquals_false");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertFalse(contentEquals(firstFile, secondFile));
    }

    /**
     * Check equals tow files content with range.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsRange() throws Exception {
        System.out.println("isFileBinaryEquals_range");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertTrue(contentEquals(firstFile, secondFile, 10, 64));
    }

    /**
     * Check not equals two file contents with range.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsRangeFalse() throws Exception {
        System.out.println("isFileBinaryEquals_range_false");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertFalse(contentEquals(firstFile, secondFile, 0, 64));
    }

    /**
     * Check dictzip inputstream.
     * @throws Exception when fails.
     */
    @Test
    public void testCheckDictZipInputStreamString() throws Exception {
        System.out.println("checkDictZipInputStream_string");
        String targetFile = this.getClass().getResource("/test.dict.dz").getFile();
        assertTrue(DictZipFileUtils.checkDictZipInputStream(targetFile));
    }

    /**
     * Check dictzip input streasm which is not exist.
     * @throws Exception when fails.
     */
    @Test
    public void testCheckDictZipInputStreamStringNoExist() throws Exception {
        System.out.println("checkDictZipInputStream_string");
        String targetFile = "false.dict.dz";
        boolean result;
        try {
            DictZipFileUtils.checkDictZipInputStream(targetFile);
            result = false;
        } catch (IOException e) {
            // expected.
            result = true;
        }
        assertTrue(result);
    }

    /**
     * Check dictzip input stream.
     * @throws Exception when fails.
     */
    @Test
    public void testCheckDictZipInputStream() throws Exception {
        System.out.println("checkDictZipInputStream");
        String targetFile = this.getClass().getResource("/test.dict.dz").getFile();
        try (DictZipInputStream dzin = new DictZipInputStream(new
                RandomAccessInputStream(targetFile, "r"))) {
            assertTrue(DictZipFileUtils.checkDictZipInputStream(dzin));
            dzin.close();
        }
    }
}
