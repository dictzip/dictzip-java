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

/**
 * Test archive creation and extraction.
 */
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
        }
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
     * Test case to create large archive.
     * @param tempDir JUnit5 temporary directory..
     * @throws IOException when file wreite filed.
     * @throws InterruptedException when external dictzip not executed well.
     */
    @Test
    public void testFileCreation(@TempDir final Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = (BUF_LEN * 512 + 100) / 100000 * 100000;
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
        String[] command = {"/usr/bin/dictzip", "-d", "-c", "-s", null, "-e", "10", zippedPath.toAbsolutePath().toString()};
        for (int i = 0; i < positions.length; i++) {
            System.out.printf("seek position: %d%n", positions[i]);
            command[4] = Integer.toString(positions[i]);
            Process process = Runtime.getRuntime().exec(command);
            int b = process.getInputStream().read();
            int returnCode = process.waitFor();
            assertEquals(0, returnCode);
            assertEquals(expected[i], (byte) b);
        }
    }

    /**
     * Test case to extract large archive file.
     * @param tempDir JUnit5 temporary directory.
     * @throws IOException when i/o error occurred.
     * @throws InterruptedException when external dictzip not executed well.
     */
    @Test
    public void testFileReadAceess(@TempDir final Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = (BUF_LEN * 512 + 100) / 100000 * 100000;
        // --- preparation of data
        int len;
        int numChunk = size / BUF_LEN + 1;
        byte[] buf = new byte[BUF_LEN];
        int[] positions = new int[] {
                BUF_LEN - 10,
                BUF_LEN + 10,
                BUF_LEN * 2 + 10,
                BUF_LEN * 256 - 10,
                BUF_LEN * 256 + 10,
                BUF_LEN * (numChunk / 2 - 1) - 10,
                BUF_LEN * (numChunk / 2 + 1) + 10,
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
        String[] command = {"/usr/bin/dictzip", outTextPath.toAbsolutePath().toString()};
        Process process = Runtime.getRuntime().exec(command);
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
     * Test case to reproduce issue #24.
     * <p>
     *     When seek to almost end of large dictionary, it cause error
     *     Caused by: java.util.zip.ZipException: invalid distance too far back
     * </p>
     * @param tempDir JUnit5 temporary directory.
     * @throws IOException when i/o error occurred.
     * @throws InterruptedException when external dictzip not executed well.
     */
    @Test
    public void testFileInputOutput(@TempDir final Path tempDir) throws IOException, InterruptedException {
        // Run test when running on Linux and dictzip command installed
        Assumptions.assumeTrue(Paths.get("/usr/bin/dictzip").toFile().exists());
        int size = (BUF_LEN * 512 + 100) / 100000 * 100000;
        // int size = 45000000;  // about 45MB
        int numChunk = size / BUF_LEN + 1;
        byte[] buf = new byte[BUF_LEN];
        int[] positions = new int[] {
                BUF_LEN - 10,
                BUF_LEN + 10,
                BUF_LEN * 2 + 10,
                BUF_LEN * (numChunk / 2 - 1) - 10,
                BUF_LEN * (numChunk / 2 + 1) + 10,
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
        // check archive
        String[] command = {"/usr/bin/dictzip", "-d", "-c", "-s", null, "-e", "10", zippedPath.toAbsolutePath().toString()};
        for (int i = 0; i < positions.length; i++) {
            System.out.printf("seek position: %d%n", positions[i]);
            command[4] = Integer.toString(positions[i]);
            Process process = Runtime.getRuntime().exec(command);
            int b = process.getInputStream().read();
            int returnCode = process.waitFor();
            assertEquals(0, returnCode);
            assertEquals(expected[i], (byte) b);
        }
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
