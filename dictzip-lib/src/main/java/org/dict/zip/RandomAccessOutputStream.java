/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GPL-2.0-or-later WITH Classpath-exception-2.0
 */

package org.dict.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * OutputStream class supporting random access methods.
 *
 * @author Hiroshi Miura
 */
public class RandomAccessOutputStream extends OutputStream {

    private RandomAccessFile out;

    /**
     * Construct RandomAccessOutputStream from file.
     *
     * @param outFile RamdomAccessFile
     */
    public RandomAccessOutputStream(final RandomAccessFile outFile) {
        this.out = outFile;
    }

    /**
     * Construct RandomAccessOutputStream from filename.
     *
     * @param file to write with random access.
     * @param mode open mode.
     * @exception IOException if an I/O error has occurred.
     */
    public RandomAccessOutputStream(final String file, final String mode) throws IOException {
        this(new RandomAccessFile(file, mode));
    }

    @Override
    public final synchronized void write(final byte[] buf, final int off, final int len)
            throws IOException {
        out.write(buf, off, len);
    }

    @Override
    public final synchronized void write(final int b) throws IOException {
        out.write(b);
    }

    /**
     * Seek file position.
     *
     * @param pos file position in byte.
     * @exception IOException if an I/O error has occurred.
     */
    public final void seek(final long pos) throws IOException {
        out.seek(pos);
    }

    /**
     * Get file position.
     * @return position
     * @throws IOException if on I/O error occurred
     */
    public final long position() throws IOException {
        return out.getFilePointer();
    }

    /**
     * Get file length.
     * @return length of file.
     * @throws IOException when on I/O error occurred
     */
    public final long length() throws IOException {
        return out.length();
    }
}
