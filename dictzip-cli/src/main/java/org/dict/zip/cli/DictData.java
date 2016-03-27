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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.Deflater;


import org.dict.zip.DictZipHeader;
import org.dict.zip.DictZipInputStream;
import org.dict.zip.DictZipOutputStream;
import org.dict.zip.RandomAccessInputStream;
import org.dict.zip.RandomAccessOutputStream;


/**
 * Dictionary data handling class.
 * @author Hiroshi Miura
 */
public class DictData {

    static ResourceBundle messages = ResourceBundle.getBundle("org/dict/zip/cli/Bundle",
            Locale.getDefault());

    private final String originalFileName;
    private final String compressedFileName;
    private final static int bufLen = 58315;

    /**
     * Default constructor for reader.
     * @param originalFileName to handle
     */
    public DictData(final String originalFileName, String compressedFileName) {
        this.originalFileName = originalFileName;
        this.compressedFileName = compressedFileName;
    }

    public void printHeader() throws IOException {
        File targetFile = new File(originalFileName);
        RandomAccessFile targetRaFile = new RandomAccessFile(targetFile, "r");
        try (RandomAccessInputStream in = new RandomAccessInputStream(targetRaFile);
             DictZipInputStream din = new DictZipInputStream(in);) {
            long uncomp = din.getLength();
            long comp = din.getCompLength();
            long crc = din.getCrc();
            DictZipHeader header = din.readHeader();
            String type = header.getType();
            int chunkLength = header.getChunkLength();
            int chunkCount = header.getChunkCount();
            Date mtime = new Date(header.getMtime() * 1000);
            String filename = header.getFilename();
            Format timeFormatter = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");
            System.out.println(messages.getString("dictzip.header.title"));
            System.out.print(String.format("%s\t%08x\t%s\t", type, crc, timeFormatter.format(mtime)));
            System.out.print(String.format("%6d\t%d\t%d\t  %d\t", chunkCount, chunkLength, comp,
                    uncomp));
            System.out.println(String.format("%3.1f%%\t%s", (100.0 * comp) / uncomp, filename));
        } catch (RuntimeException ex) {
            throw ex;
        }
    }

    /**
     * Do compression.
     * @throws IOException if file I/O error.
     */
    public void doZip() throws IOException {
        byte[] buf = new byte[bufLen];
        File originalFile = new File(originalFileName);
        try (FileInputStream ins = new FileInputStream(originalFile);
             DictZipOutputStream dout = new DictZipOutputStream(
                    new RandomAccessOutputStream(new RandomAccessFile(compressedFileName, "rws")), bufLen, originalFile.length(), Deflater.BEST_COMPRESSION)) {
            int len;
            while ((len = ins.read(buf, 0, bufLen)) > 0) {
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
    public void doUnzip(long start, int size) throws IOException {
        try (  DictZipInputStream din = new DictZipInputStream(new RandomAccessInputStream(new
                        RandomAccessFile(new File(compressedFileName), "r")));
                OutputStream unzipOut = new RandomAccessOutputStream(originalFileName, "rw")) {
            byte[] buf = new byte[bufLen];
            din.seek(start);
            if (size == 0) {
                int len;
                while ((len = din.read(buf, 0, bufLen)) > 0) {
                    unzipOut.write(buf, 0, len);
                }
            } else {
                try {
                    int len;
                    int readSize = 0;
                    while (size - readSize > 0) {
                        if (size - readSize < bufLen) {
                            len = din.read(buf, 0, size - readSize);
                        } else {
                            len = din.read(buf, 0, bufLen);
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
