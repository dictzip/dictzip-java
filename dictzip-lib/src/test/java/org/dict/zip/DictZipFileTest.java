/*
 * DictZip Library test.
 *
 * Copyright (C) 2021-2022 Hiroshi Miura
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

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.zip.Deflater;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DictZipFileTest {

    private static final int BUF_LEN = 58315;

    void prepareTextData(final Path outTextPath, final int size) throws IOException {
        Random random = new Random();
        File outTextFile = outTextPath.toFile();
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outTextFile), StandardCharsets.US_ASCII)), false)) {
            for (long i = 0; i < size; i++) {
                int number = random.nextInt(94);
                writer.print((char) (32 + number));
            }
        };
    }

    void prepareLargeTextData(final Path outTextPath, final int size) throws IOException {
        Random random = new Random();
        File outTextFile = outTextPath.toFile();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(outTextFile), StandardCharsets.US_ASCII)), false);
        int outSize = 0;
        while (true) {
            for (int j = 0; j < 1000; j++) {
                for (int i = 0; i < 99; i++) {
                    int number = random.nextInt(94);
                    writer.print((char) (32 + number));
                }
                writer.print("\n");
            }
            outSize += 1000 * 100;
            if (outSize >= size) {
                writer.close();
                break;
            }
        }
    }

    /**
     * Test case to extract an archive file.
     * @param tempDir JUnit5.jupiter TempDir.
     * @throws IOException when i/o error occurred.
     * @throws InterruptedException when external dictzip not executed well.
     */
    @Test
    public void testFileReadAceess(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = 65536;  // 64kB
        byte[] buf = new byte[BUF_LEN];
        // create archive with dictzip command
        Path outTextPath = tempDir.resolve("DictZipText.txt");
        prepareTextData(outTextPath, size);
        File inputFile = outTextPath.toFile();
        assertEquals(size, inputFile.length());
        // get expectation
        try (RandomAccessInputStream is = new RandomAccessInputStream(new RandomAccessFile(inputFile, "r"))) {
            is.seek(size -2);
            int len = is.read(buf, 0, 1);
            assertEquals(1, len);
        }
        byte expected = buf[0];
        Process process = Runtime.getRuntime().exec(String.format("dictzip %s", outTextPath.toAbsolutePath()));
        int returnCode = process.waitFor();
        assertEquals(0, returnCode);
        File zippedFile = tempDir.resolve("DictZipText.txt.dz").toFile();
        // read dictZip archive
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(new
                RandomAccessFile(zippedFile, "r")))) {
            din.seek(size - 2);
            int len = din.read(buf, 0, 1);
            assertTrue(len > 0);
        }
        assertEquals(expected, buf[0]);
    }

    /**
     * Test case to create large archive.
     */
    @Test
    public void testFileCreation(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = BUF_LEN * 512 + 100;
        int len;
        byte[] buf = new byte[BUF_LEN];
        int[] positions = new int[] {
                BUF_LEN - 10,
                BUF_LEN + 10,
                BUF_LEN * 2 + 10,
                BUF_LEN * 256 - 10,
                BUF_LEN * 256 + 10,
                size - BUF_LEN + 5
        };
        int cases = positions.length;
        byte[] expected = new byte[cases];
        // create data
        Path outTextPath = tempDir.resolve("DictZipText.orig.txt");
        prepareTextData(outTextPath, size);
        File inputFile = outTextPath.toFile();
        Path zippedPath = tempDir.resolve("DictZipText.txt.dz");
        assertEquals(size, inputFile.length());
        // get expectations
        try (RandomAccessInputStream is = new RandomAccessInputStream(new RandomAccessFile(inputFile, "r"))) {
            for (int i = 0; i < cases; i++) {
                is.seek(positions[i]);
                len = is.read(buf, 0, buf.length);
                assertTrue(len > 0);
                expected[i] = buf[0];
            }
        }
        // create dictZip archive
        int defLevel = Deflater.DEFAULT_COMPRESSION;
        try (FileInputStream ins = new FileInputStream(inputFile);
             DictZipOutputStream dout = new DictZipOutputStream(
                     new RandomAccessOutputStream(new RandomAccessFile(zippedPath.toFile(), "rws")),
                     BUF_LEN, inputFile.length(), defLevel)) {
            while ((len = ins.read(buf, 0, BUF_LEN)) > 0) {
                dout.write(buf, 0, len);
            }
            dout.finish();
        }
        // check archive
        for (int i = 0; i < positions.length; i++) {
            System.out.printf("seek position: %d%n", positions[i]);
            Process process = Runtime.getRuntime().exec(
                    String.format("dictzip -d -c -k -v -s %d -e %d  %s",
                            positions[i], 10, zippedPath.toAbsolutePath()));
            int b = process.getInputStream().read();
            int returnCode = process.waitFor();
            assertEquals(0, returnCode);
            assertEquals(expected[i], (byte) b);
        }
    }

    /**
     * Test case to extract large archive file.
     * @param tempDir JUnit5.jupiter TempDir.
     * @throws IOException when i/o error occurred.
     * @throws InterruptedException when external dictzip not executed well.
     */
    @Test
    public void testLargeFileReadAceess(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = 45000000;  // about 45MB
        // --- preparation of data
        int len;
        int num_chunk = size / BUF_LEN + 1;
        byte[] buf = new byte[BUF_LEN];
        int[] positions = new int[] {
                BUF_LEN - 10,
                BUF_LEN + 10,
                BUF_LEN * 2 + 10,
                BUF_LEN * 256 - 10,
                BUF_LEN * 256 + 10,
                BUF_LEN * (num_chunk /2 - 1) - 10,
                BUF_LEN * (num_chunk /2 + 1) + 10,
                size - BUF_LEN + 5
        };
        int cases = positions.length;
        byte[] expected = new byte[cases];
        // create archive with dictzip command
        Path outTextPath = tempDir.resolve("DictZipText.txt");
        prepareLargeTextData(outTextPath, size);
        File inputFile = outTextPath.toFile();
        assertEquals(size, inputFile.length());
        // get expectations
        try (RandomAccessInputStream is = new RandomAccessInputStream(new RandomAccessFile(inputFile, "r"))) {
            for (int i = 0; i < cases; i++) {
                is.seek(positions[i]);
                len = is.read(buf, 0, buf.length);
                assertTrue(len > 0);
                expected[i] = buf[0];
            }
        }
        // create dictzip archive with dictzip command
        Process process = Runtime.getRuntime().exec(String.format("dictzip %s", outTextPath.toAbsolutePath()));
        int returnCode = process.waitFor();
        assertEquals(0, returnCode);
        File zippedFile = tempDir.resolve("DictZipText.txt.dz").toFile();
        // -- end of preparation

        // read dictZip archive
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(new
                RandomAccessFile(zippedFile, "r")))) {
            for (int i = 0; i < cases; i++) {
                System.out.printf("seek position: %d%n", positions[i]);
                din.seek(positions[i]);
                len = din.read(buf, 0, 10);
                assertTrue(len > 0);
                assertEquals(expected[i], buf[0], String.format("Read data invalid at position %d", positions[i]));
            }
        }
    }

    /**
     * Test case to create large archive.
     */
    @Test
    public void testLargeFileCreation(@TempDir Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = 45000000;  // about 45MB
        byte[] buf = new byte[BUF_LEN];
        // create data
        Path outTextPath = tempDir.resolve("DictZipText.orig.txt");
        prepareLargeTextData(outTextPath, size);
        File inputFile = outTextPath.toFile();
        Path zippedPath = tempDir.resolve("DictZipText.txt.dz");
        assertEquals(size, inputFile.length());
        // create dictZip archive
        int defLevel = Deflater.DEFAULT_COMPRESSION;
        try (FileInputStream ins = new FileInputStream(inputFile);
             DictZipOutputStream dout = new DictZipOutputStream(
                     new RandomAccessOutputStream(new RandomAccessFile(zippedPath.toFile(), "rws")),
                     BUF_LEN, inputFile.length(), defLevel)) {
            int len;
            while ((len = ins.read(buf, 0, BUF_LEN)) > 0) {
                dout.write(buf, 0, len);
            }
            dout.finish();
        }
        Process process = Runtime.getRuntime().exec(
                String.format("dictzip -d -f -k -v %s", zippedPath.toAbsolutePath()));
        int returnCode = process.waitFor();
        assertEquals(0, returnCode);
    }

    /**
     * Test case to reproduce issue #24.
     * <p>
     *     When seek to almost end of large dictionary, it cause error
     *     Caused by: java.util.zip.ZipException: invalid distance too far back
     * </p>
     */
    @Test
    public void testLargeFileInputOutput(@TempDir Path tempDir) throws IOException, InterruptedException {
        int size = 45000000;  // about 45MB
        int num_chunk = size / BUF_LEN + 1;
        byte[] buf = new byte[BUF_LEN];
        int[] positions = new int[] {
                BUF_LEN - 10,
                BUF_LEN + 10,
                BUF_LEN * 2 + 10,
                BUF_LEN * 256 - 10,
                BUF_LEN * 256 + 10,
                BUF_LEN * (num_chunk /2 - 1) - 10,
                BUF_LEN * (num_chunk /2 + 1) + 10,
                size - BUF_LEN + 5
        };
        int cases = positions.length;
        byte[] expected = new byte[cases];
        int len;
        // create data
        Path outTextPath = tempDir.resolve("DictZipText.txt");
        prepareTextData(outTextPath, size);
        File inputFile = outTextPath.toFile();
        Path zippedPath = tempDir.resolve("DictZipText.txt.dz");
        assertEquals(size, inputFile.length());
        // get expectations
        try (RandomAccessInputStream is = new RandomAccessInputStream(new RandomAccessFile(inputFile, "r"))) {
            for (int i = 0; i < cases; i++) {
                is.seek(positions[i]);
                len = is.read(buf, 0, buf.length);
                assertTrue(len > 0);
                expected[i] = buf[0];
            }
        }
        // create dictZip archive
        int defLevel = Deflater.DEFAULT_COMPRESSION;
        try (FileInputStream ins = new FileInputStream(inputFile);
             DictZipOutputStream dout = new DictZipOutputStream(
                     new RandomAccessOutputStream(new RandomAccessFile(zippedPath.toFile(), "rws")),
                     BUF_LEN, inputFile.length(), defLevel)) {
            while ((len = ins.read(buf, 0, BUF_LEN)) > 0) {
                dout.write(buf, 0, len);
            }
            dout.finish();
        }
        // check created archive
        Process process = Runtime.getRuntime().exec(
                String.format("dictzip -d -f -k -v %s", zippedPath.toAbsolutePath()));
        int returnCode = process.waitFor();
        assertEquals(0, returnCode);
        // read dictZip archive
        try (RandomAccessFile raf = new RandomAccessFile(zippedPath.toFile(), "r");
             DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(raf))) {
            for (int i = 0; i < cases; i++) {
                System.out.printf("seek position: %d%n", positions[i]);
                din.seek(positions[i]);
                len = din.read(buf, 0, 10);
                assertTrue(len > 0);
                assertEquals(expected[i], buf[0], String.format("Read data invalid at position %d", positions[i]));
            }
        }
    }
}
