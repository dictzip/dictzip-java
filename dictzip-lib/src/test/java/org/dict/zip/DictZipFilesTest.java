/*
 * DictZip library.
 *
 * Copyright (C) 2021-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GPL-2.0-or-later WITH Classpath-exception-2.0
 */
package org.dict.zip;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test of DictZipFileUtils.
 * @author Hiroshi Miura
 */
public class DictZipFilesTest {

    /**
     * Check dictzip inputstream.
     * @throws Exception when fails.
     */
    @Test
    public void testCheckDictZipInputStreamString() throws Exception {
        String targetFile = Objects.requireNonNull(this.getClass().getResource("/test.dict.dz")).getFile();
        Assertions.assertTrue(DictZipFiles.checkDictZipFile(targetFile));
    }

    /**
     * Check dictzip input streasm which is not exist.
     */
    @Test
    public void testCheckDictZipInputStreamStringNoExist() {
        String targetFile = "false.dict.dz";
        boolean result;
        try {
            DictZipFiles.checkDictZipFile(targetFile);
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
        URI targetFile = this.getClass().getResource("/test.dict.dz").toURI();
        try (DictZipInputStream dzin = DictZipFiles.newDictZipInputStream(Paths.get(targetFile))) {
            Assertions.assertTrue(DictZipFiles.checkDictZipInputStream(dzin));
        }
    }
}
