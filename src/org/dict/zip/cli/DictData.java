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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;


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

    private final String targetFileName;

    private File targetFile;
    private RandomAccessFile targetRaFile;

    /**
     * Operation mode (WRITE or READ).
     */
    public enum OpsMode { WRITE, READ };

    private OpsMode opsMode;
    private RandomAccessInputStream in;
    private DictZipInputStream din;
    private RandomAccessOutputStream out;
    private DictZipOutputStream dout;
    private DictZipHeader header;

    /**
     * Default constructor for reader.
     * @param targetFileName to handle
     */
    public DictData(final String targetFileName) {
        this.targetFileName = targetFileName;
    }

    /**
     * Open target(.dz) file on mode.
     *
     * @param mode READ or WRITE
     * @throws IOException if file I/O error.
     * @throws FileNotFoundException if specified file is not exist for read.
     */
    public void open(final OpsMode mode) throws IOException, FileNotFoundException  {
        targetFile = new File(targetFileName);
        opsMode = mode;
        if (mode.equals(OpsMode.READ)) {
            targetRaFile = new RandomAccessFile(targetFile, "r");
            in = new RandomAccessInputStream(targetRaFile);
            din = new DictZipInputStream(in);
        } else if (mode.equals(OpsMode.WRITE)) {
            targetRaFile = new RandomAccessFile(targetFile, "w");
            out = new RandomAccessOutputStream(targetRaFile);
        } else {
            // Not come here.
            throw new IllegalArgumentException("Unknown file I/O mode");
        }
    }

    public void printHeader() throws IOException {
        if (opsMode.equals(OpsMode.WRITE)) {
            throw new IOException("Cannot read header.");
        }
        Format timeFormatter;
        Format dateFormatter;
        header = din.readHeader();
        String type = header.getType();
        long crc = din.getCrc();
        int chunkLength = header.getChunkLength();
        int chunkCount = header.getChunkCount();
        Date mtime = new Date(header.getMtime() * 1000);
        long uncomp = din.getLength();
        long comp = din.getCompLength();
        String filename = header.getFilename();
        timeFormatter = new SimpleDateFormat("MMMM dd, yyyy hh:mm:ss");
        System.out.println(messages.getString("dictzip.header.title"));
        System.out.print(String.format("%s\t%08x\t%s\t", type, crc, timeFormatter.format(mtime)));
        System.out.print(String.format("%6d\t%d\t%d\t  %d\t", chunkCount, chunkLength, comp,
                uncomp));
        System.out.println(String.format("%3.1f%%\t%s", (100.0 * comp) / uncomp, filename));
    }

    /**
     * Close opened file.
     * @throws IOException if file I/O error.
     */
    public void close() throws IOException {
        if (opsMode.equals(OpsMode.READ)) {
            din.close();
            in.close();
        } else {
            dout.close();
            out.close();
        }
        targetRaFile.close();
    }

    /**
     * Do compression.
     * @throws IOException if file I/O error.
     */
    public void doZip() throws IOException {
        if (opsMode.equals(OpsMode.READ)) {
            throw new IOException("Cannot compress.");
        }
    }

    /**
     * Do uncompression.
     * @throws IOException if file I/O error.
     */
    public void doUnzip() throws IOException {
        if (opsMode.equals(OpsMode.WRITE)) {
            throw new IOException("Cannot decompress.");
        }
    }

    /**
     * Remove file set to target file.
     * @return result of operation.
     * @throws IOException if file I/O error.
     */
    public boolean removeTarget() throws IOException {
        return targetFile.delete();
    }
}
