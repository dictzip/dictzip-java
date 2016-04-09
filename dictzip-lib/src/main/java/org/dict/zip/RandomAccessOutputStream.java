/*
 * DictZip library.
 *
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

}
