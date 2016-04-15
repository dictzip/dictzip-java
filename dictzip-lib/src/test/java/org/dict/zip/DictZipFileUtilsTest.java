package org.dict.zip;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Created by Hiroshi Miura on 16/04/09.
 */
public class DictZipFileUtilsTest {
    @Test
    public void testIsFileBinaryEquals() throws Exception {
        System.out.println("isFileBinaryEquals");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util1.txt").getFile());
        assertTrue(DictZipFileUtils.isFileBinaryEquals(firstFile, secondFile));
    }

    @Test
    public void testIsFileBinaryEquals_false() throws Exception {
        System.out.println("isFileBinaryEquals_false");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertFalse(DictZipFileUtils.isFileBinaryEquals(firstFile, secondFile));
    }

    @Test
    public void testIsFileBinaryEquals_range() throws Exception {
        System.out.println("isFileBinaryEquals_range");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertTrue(DictZipFileUtils.isFileBinaryEquals(firstFile, secondFile, 10, 64));
    }

    @Test
    public void testIsFileBinaryEquals_range_false() throws Exception {
        System.out.println("isFileBinaryEquals_range_false");
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertFalse(DictZipFileUtils.isFileBinaryEquals(firstFile, secondFile, 0, 64));
    }

    @Test
    public void testCheckDictZipInputStream_string() throws Exception {
        System.out.println("checkDictZipInputStream_string");
        String targetFile = this.getClass().getResource("/test.dict.dz").getFile();
        assertTrue(DictZipFileUtils.checkDictZipInputStream(targetFile));
    }

    @Test
    public void testCheckDictZipInputStream_stringNoExist() throws Exception {
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