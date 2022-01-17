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

package org.dict.zip.cli;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.Deflater;


import org.dict.zip.DictZipHeader.CompressionLevel;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.DictZipOutputStream;
import org.dict.zip.RandomAccessInputStream;
import org.dict.zip.RandomAccessOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Dictionary data handling class.
 * @author Hiroshi Miura
 */
public class DictData {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle("org/dict/zip/cli/Bundle", Locale.getDefault());

    private final Path originalFileName;
    private final Path compressedFileName;
    private static final int BUF_LEN = 58315;

    /**
     * Default constructor.
     * @param originalFilePath to handle
     * @param compressedFilePath to handle
     */
    public DictData(@NotNull final Path originalFilePath, @Nullable final Path compressedFilePath) {
        this.originalFileName = originalFilePath;
        this.compressedFileName = compressedFilePath;
    }

    /**
     * Default constructor.
     * @param originalFile to handle
     * @param compressedFile to handle
     */
    public DictData(@NotNull final File originalFile, @Nullable final File compressedFile) {
        this.originalFileName = Paths.get(originalFile.toURI());
        if (compressedFile == null) {
            this.compressedFileName = null;
        } else {
            this.compressedFileName = Paths.get(compressedFile.toURI());
        }
    }

    /**
     * Default constructor for reader.
     * @param originalFileName to handle
     * @param compressedFileName to handle
     */
    public DictData(@NotNull final String originalFileName, @Nullable final String compressedFileName) {
        this.originalFileName = Paths.get(originalFileName);
        if (compressedFileName == null) {
            this.compressedFileName = null;
        } else {
            this.compressedFileName = Paths.get(compressedFileName);
        }
    }

    /**
     * Print header information to STDOUT.
     * @throws IOException when stdout is terminated.
     */
    public void printHeader() throws IOException {
        File targetFile = originalFileName.toFile();
        RandomAccessFile targetRaFile = new RandomAccessFile(targetFile, "r");
        try (RandomAccessInputStream in = new RandomAccessInputStream(targetRaFile);
             DictZipInputStream din = new DictZipInputStream(in);) {
            // These three parameters are able to get only from din.
            long uncomp = din.getLength();
            long comp = din.getCompLength();
            long crc = din.getCrc();
            // Get header parameters.
            String type = din.getType();
            int chunkLength = din.getChunkLength();
            int chunkCount = din.getChunkCount();
            Date mtime = new Date(din.getMtime() * 1000);
            String filename = din.getFilename();
            Format timeFormatter = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");
            System.out.println(RESOURCE_BUNDLE.getString("dictzip.header.title"));
            System.out.printf("%s\t%08x\t%s\t", type, crc,
                    timeFormatter.format(mtime));
            System.out.printf("%6d\t%d\t%d\t  %d\t", chunkCount, chunkLength, comp,
                    uncomp);
            System.out.printf("%3.1f%%\t%s%n", (100.0 * comp) / uncomp, filename);
        }
    }

    /**
     * Do compression.
     * @param level indicate compression level from fast to best.
     * @throws IOException if file I/O error.
     */
    public void doZip(final CompressionLevel level) throws IOException {
        int defLevel;
        byte[] buf = new byte[BUF_LEN];
        File originalFile = originalFileName.toFile();
        switch (level) {
            case BEST_COMPRESSION:
                defLevel = Deflater.BEST_COMPRESSION;
                break;
            case BEST_SPEED:
                defLevel = Deflater.BEST_SPEED;
                break;
            case DEFAULT_COMPRESSION:
            default:
                defLevel = Deflater.DEFAULT_COMPRESSION;
                break;
        }
        try (FileInputStream ins = new FileInputStream(originalFile);
             DictZipOutputStream dout = new DictZipOutputStream(
                    new RandomAccessOutputStream(new RandomAccessFile(compressedFileName.toFile(), "rws")),
                     BUF_LEN, originalFile.length(), defLevel)) {
            int len;
            while ((len = ins.read(buf, 0, BUF_LEN)) > 0) {
                dout.write(buf, 0, len);
            }
        } catch (EOFException eof) {
                // ignore it.
        }
    }

    /**
     * Do uncompression.
     * @param start start offset of data
     * @param size size to retrieve
     * @throws IOException if file I/O error.
     */
    public void doUnzip(final long start, final int size) throws IOException {
        try (DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(new
                        RandomAccessFile(compressedFileName.toFile(), "r")));
                OutputStream unzipOut = new RandomAccessOutputStream(originalFileName.toAbsolutePath().toString(), "rw")) {
            byte[] buf = new byte[BUF_LEN];
            din.seek(start);
            if (size == 0) {
                int len;
                while ((len = din.read(buf, 0, BUF_LEN)) > 0) {
                    unzipOut.write(buf, 0, len);
                }
            } else {
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
                            unzipOut.write(buf, 0, len);
                            readSize += len;
                        } else {
                            break;
                        }
                    }
                } catch (EOFException eof) {
                    // ignore it.
                }
            }
        }
    }

}
