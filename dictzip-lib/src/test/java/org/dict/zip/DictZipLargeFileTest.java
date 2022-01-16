/*
 * DictZip Library test.
 *
 * Copyright (C) 2016,2019,2021 Hiroshi Miura
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


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Random;
import java.util.zip.Deflater;

public class DictZipLargeFileTest {

    private static final int BUF_LEN = 58315;

    public File prepareTextData() throws IOException {
        int size = 45000000;  // 45MB
        Random random = new Random();
        File outTextFile = File.createTempFile("DictZipLargeText", ".txt");
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outTextFile), StandardCharsets.UTF_8)), false);
        int counter = 0;
        while (true) {
            for (int i = 0; i < 100; i++) {
                int number = random.nextInt(96);
                writer.print((char) (32 + number));
            }
            writer.println();
            if (++counter >= 10000) {
                if (outTextFile.length() > size) {
                    writer.close();
                    break;
                } else {
                    counter = 0;
                }
            }
        }
        return outTextFile;
    }

    /**
     * Test case to reproduce issue #24.
     * <p>
     *     When seek to almost end of large dictionary, it cause error
     *     Caused by: java.util.zip.ZipException: invalid distance too far back
     * </p>
     */
    @Test
    @ExtendWith(TempDirectory.class)
    public void testLargeFileInputOutput(@TempDirectory.TempDir Path tempDir) throws IOException {
        File inputFile = prepareTextData();
        Path zippedFile = tempDir.resolve("DictZipLargeText.dz");

         int defLevel = Deflater.DEFAULT_COMPRESSION;
         byte[] buf = new byte[BUF_LEN];
         try (FileInputStream ins = new FileInputStream(inputFile);
             DictZipOutputStream dout = new DictZipOutputStream(
                    new RandomAccessOutputStream(new RandomAccessFile(zippedFile.toFile(), "rws")),
                     BUF_LEN, inputFile.length(), defLevel)) {
            int len;
            while ((len = ins.read(buf, 0, BUF_LEN)) > 0) {
                dout.write(buf, 0, len);
            }
        } catch (EOFException eof) {
                // ignore it.
        }
        // start assertion, read last bytes
        readFromZip(zippedFile.toFile(), inputFile.length() - 2, 1);
    }

    private byte[] readFromZip(final File zippedFile, final long start, final int size) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(new
                RandomAccessFile(zippedFile, "r")))) {
            din.seek(start);
            byte[] buf = new byte[BUF_LEN];
            try {
                int len;
                int readSize = 0;
                while (size - readSize > 0) {
                    if (size - readSize < BUF_LEN) {
                        len = din.read(buf, 0, size - readSize);
                    } else {
                        len = din.read(buf, 0, BUF_LEN);
                    }
                    if (len > 0) {
                        outputStream.write(buf, 0, len);
                        readSize += len;
                    } else {
                        break;
                    }
                }
            } catch (EOFException eof) {
                // ignore it.
            }
        }
        return outputStream.toByteArray();
    }
}
