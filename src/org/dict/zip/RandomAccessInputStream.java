/*
 * DictZip library. 
 *
 * Copyright (C) 2001-2004 JDictd project.
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
import java.io.InputStream;
import java.io.RandomAccessFile;


/**
 * RandomAccessInputStream.
 *
 * @author jdictd project
 * @author Hiroshi Miura
 */
public class RandomAccessInputStream extends InputStream {

        private RandomAccessFile in;

        private int mark = 0;

        /**
         * Construct RandomAccessInputStream from file.
         * @param inFile RamdomAccessFile
         */
        public RandomAccessInputStream(final RandomAccessFile inFile) {
                this.in = inFile;
        }

        /**
         * Construct RamdonAccessInputStream from filename.
         * @param file to read with random access.
         * @param mode open mode.
         * @exception IOException if an I/O error has occurred.
         */
        public RandomAccessInputStream(final String file, final String mode) throws IOException {
                this(new RandomAccessFile(file, mode));
        }

        @Override
        public final int available() throws IOException {
                return getLength() - getPos();
        }

        @Override
        public final void close() throws IOException {
                in.close();
        }

        /**
         * Get file length.
         * @return length of file in byte.
         * @exception IOException if an I/O error has occurred.
         */
        public final int getLength() throws IOException {
                return (int) in.length();
        }

        /**
         * Get cursor position.
         * @return position in byte.
         * @exception IOException if an I/O error has occurred.
         */
        public final int getPos() throws IOException {
                return (int) in.getFilePointer();
        }

        @Override
        public final synchronized void mark(final int markpos) {
                try {
                        mark = getPos();
                } catch (IOException e) {
                        throw new RuntimeException(e.toString());
                }
        }

        @Override
        public final boolean markSupported() {
                return true;
        }

        @Override
        public final synchronized int read() throws IOException {
                return in.read();
        }

        @Override
        public final int read(final byte[] buf, final int off, final int len) throws IOException {
                return in.read(buf, off, len);
        }

        /**
         * Read full data to byte buffer.
         * @param buf buffer to store data.
         * @exception IOException if an I/O error has occurred.
         */
        public final void readFully(final byte[] buf)throws IOException {
                in.readFully(buf);
        }

        @Override
        public final synchronized void reset() throws IOException {
                in.seek(mark);
        }

        /**
         * Seek file position.
         * @param pos file position in byte.
         * @exception IOException if an I/O error has occurred.
         */
        public final void seek(final long pos) throws IOException {
                in.seek(pos);
        }

        @Override
        public final long skip(final long size) throws IOException {
                return (long) in.skipBytes((int) size);
        }
}
