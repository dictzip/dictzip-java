/*
 * FileUtils library test.
 *
 * Copyright (C) 2016,2019,2022 Hiroshi Miura
 *
 *  SPDX-License-Identifier: Apache-2.0
 */
package tokyo.northside;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tokyo.northside.io.FileUtils2.contentEquals;

/**
 * Created by Hiroshi Miura on 16/04/09.
 */
public class FileUtils2Test {

    /**
     * Check equals two file contentss.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEquals() throws Exception {
        Path firstFile = Paths.get(
                Objects.requireNonNull(this.getClass().getResource("/test_util.txt")).toURI());
        Path secondFile = Paths.get(
                Objects.requireNonNull(this.getClass().getResource("/test_util1.txt")).toURI());
        assertTrue(contentEquals(firstFile, secondFile));
    }

    /**
     * Check equals two file paths which is canonical path.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsSameCanonicalPath() throws Exception {
        Path firstFile = Paths.get(
                Objects.requireNonNull(this.getClass().getResource("/test_util.txt")).toURI());
        Path secondFile = Paths.get(
                Objects.requireNonNull(this.getClass().getResource("/test_util.txt")).toURI());
        assertTrue(contentEquals(firstFile, secondFile));
    }

    /**
     * Check not equals two file contents.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsFalse() throws Exception {
        File firstFile = new File(
                Objects.requireNonNull(this.getClass().getResource("/test_util.txt")).getFile());
        File secondFile = new File(
                Objects.requireNonNull(this.getClass().getResource("/test_util2.txt")).getFile());
        assertFalse(contentEquals(firstFile, secondFile));
    }

    /**
     * Check equals tow files content with range.
     * @throws Exception when fails to open.
     */
    @Test
    public void testContentEqualsRange() throws Exception {
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
        File firstFile = new File(this.getClass().getResource("/test_util.txt").getFile());
        File secondFile = new File(this.getClass().getResource("/test_util2.txt").getFile());
        assertFalse(contentEquals(firstFile, secondFile, 0, 64));
    }
}
