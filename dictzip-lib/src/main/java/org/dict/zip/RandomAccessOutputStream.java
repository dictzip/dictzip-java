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
     * Get file position;
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
