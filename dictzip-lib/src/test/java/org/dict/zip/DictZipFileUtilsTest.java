package org.dict.zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DictZipFileUtilsTest {

    /**
     * Check dictzip inputstream.
     * @throws Exception when fails.
     */
    @Test
    public void testCheckDictZipInputStreamString() throws Exception {
        String targetFile = this.getClass().getResource("/test.dict.dz").getFile();
        Assertions.assertTrue(DictZipFileUtils.checkDictZipInputStream(targetFile));
    }

    /**
     * Check dictzip input streasm which is not exist.
     */
    @Test
    public void testCheckDictZipInputStreamStringNoExist() {
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
        String targetFile = this.getClass().getResource("/test.dict.dz").getFile();
        try (DictZipInputStream dzin = new DictZipInputStream(new
                RandomAccessInputStream(targetFile, "r"))) {
            Assertions.assertTrue(DictZipFileUtils.checkDictZipInputStream(dzin));
        }
    }
}
