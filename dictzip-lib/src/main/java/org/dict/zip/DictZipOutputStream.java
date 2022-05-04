/*
 * DictZip library.
 *
 * Copyright (C) 2016-2022 Hiroshi Miura
 *
 * SPDX-License-Identifier: GPL-2.0-or-later WITH Classpath-exception-2.0
 */

package org.dict.zip;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

/**
 * Test of DictZipOutputStream.
 * @author Hiroshi Miura
 */
public class DictZipOutputStream extends FilterOutputStream {

    /**
     * Compressor for this stream.
     */
    protected Deflater def;

    /**
     * Output buffer for writing compressed data.
     */
    protected byte[] buf;

    /**
     * CRC-32 of uncompressed data.
     */
    protected CRC32 crc;

    private int cindex;
    private boolean closed = false;
    private final long dataSize;
    private long wroteInChunk;
    private final DictZipHeader header;
    private boolean usesDefaultDeflater = false;
    private static final int BUF_LEN = 58315;

    /**
     * Constructor.
     * @param out output stream to filter.
     * @param size total data of file.
     * @throws IOException if I/O error occored.
     * @throws IllegalArgumentException if parameter is invalid.
     */
    public DictZipOutputStream(final RandomAccessOutputStream out, final long size)
            throws IOException, IllegalArgumentException {
        this(out, BUF_LEN, size);
    }

    /**
     * Constructor.
     * @param out output stream to filter.
     * @param buflen size of buffer to write.
     * @param size total data of file.
     * @throws IOException if I/O error occurred.
     * @throws IllegalArgumentException if parameter is invalid.
     */
    public DictZipOutputStream(final RandomAccessOutputStream out, final int buflen,
            final long size) throws IOException, IllegalArgumentException {
        this(out, buflen, size, Deflater.DEFAULT_COMPRESSION);
    }

    /**
     * Constructor.
     * @param out output stream to filter.
     * @param level level of compression, 9=best, 1=fast.
     * @param buflen size of buffer to write.
     * @param size total data of test file.
     * @throws IOException if I/O error occurred.
     * @throws IllegalArgumentException if parameter is invalid.
     */
    public DictZipOutputStream(final RandomAccessOutputStream out, final int buflen,
            final long size, final int level) throws IOException,
            IllegalArgumentException {
        this(out, new Deflater(level, true), buflen, size, level);
        usesDefaultDeflater = true;
    }

    /**
     * Constructor.
     * @param out output stream to filter.
     * @param defl custom deflater class, should be child of Deflater class.
     * @param inBufferSize size of buffer to write.
     * @param size total data of test file.
     * @param level compression level.
     * @throws IOException if I/O error occurred.
     * @throws IllegalArgumentException if parameter is invalid.
     */
    public DictZipOutputStream(final RandomAccessOutputStream out, final Deflater defl,
            final int inBufferSize, final long size, final int level) throws IOException,
            IllegalArgumentException {
        super(out);
        if (out == null || defl == null) {
            throw new NullPointerException();
        }
        if (inBufferSize <= 0) {
            throw new IllegalArgumentException("buffer size <= 0");
        }
        if (inBufferSize > DictZipHeader.MAX_CHUNK_LEN) {
            throw new IllegalArgumentException("buffer size > 64kB/1.1 - 12");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("total data size <= 0");
        }
        wroteInChunk = 0;
        this.def = defl;
        int outBufferSize = (int) ((inBufferSize + 12) * 1.1);
        buf = new byte[outBufferSize];
        this.dataSize = size;
        crc = new CRC32();

        header = new DictZipHeader(dataSize, inBufferSize);
        header.setMtime(System.currentTimeMillis() / 1000);
        switch (level) {
            case Deflater.DEFAULT_COMPRESSION:
                header.setExtraFlag(DictZipHeader.CompressionLevel.DEFAULT_COMPRESSION);
                defl.setLevel(level);
                break;
            case Deflater.BEST_COMPRESSION:
                header.setExtraFlag(DictZipHeader.CompressionLevel.BEST_COMPRESSION);
                defl.setLevel(level);
                break;
            case Deflater.BEST_SPEED:
                header.setExtraFlag(DictZipHeader.CompressionLevel.BEST_SPEED);
                defl.setLevel(level);
                break;
            default:
                header.setExtraFlag(DictZipHeader.CompressionLevel.DEFAULT_COMPRESSION);
                defl.setLevel(Deflater.DEFAULT_COMPRESSION);
        }
        header.setHeaderOS(DictZipHeader.OperatingSystem.UNIX);
        writeHeader(out);
        crc.reset();
        cindex = 0;
    }

    /**
     * Closes the output stream.
     *
     * @exception IOException if an I/O error has occurred
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            finish();
            if (out instanceof RandomAccessOutputStream) {
                RandomAccessOutputStream raout = (RandomAccessOutputStream) out;
                raout.seek(0);
                writeHeader(raout);
            }
            if (usesDefaultDeflater) {
                def.end();
            }
            out.close();
            closed = true;
        }
    }

    /**
     * Writes next block of compressed data to the output stream.
     *
     * @throws IOException if an I/O error has occurred
     */
    protected void deflate() throws IOException {
        crc.update(buf, 0, buf.length);
        int len = def.deflate(buf, 0, buf.length, Deflater.FULL_FLUSH);
        if (len > DictZipHeader.MAX_CHUNK_LEN) {
            throw new IOException("Invalid size of chunk: Compressed chunked data size is larger than 64kB.");
        }
        if (len > 0) {
            out.write(buf, 0, len);
            header.chunks[cindex] = len;
            cindex++;
        }
    }

    /**
     * Writes an array of bytes to the compressed output stream.
     * <p>
     * This method will block until all the bytes are written.
     *
     * @param b the data to be written
     * @param off the start offset of the data
     * @param len the length of the data
     * @throws IOException if an I/O error has occurred
     */
    @Override
    public synchronized void write(final byte[] b, final int off, final int len)
            throws IOException {
        if (def.finished()) {
            throw new IOException("write beyond end of stream");
        }
        if ((off | len | (off + len) | (b.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        } else if (def.getTotalIn() + len > dataSize) {
            throw new IOException("write beyond decelerated data size");
        } else if (len == 0) {
            return;
        }
        if (!def.finished()) {
            // Deflate no more than chunkLength bytes at a time.
            // This avoids excess copying in deflateBytes (see Deflater.c)
            int chunkLength = header.getChunkLength();
            int idx = 0;
            int writeSize = Math.min(chunkLength, len);
            while (writeSize > 0) {
                def.setInput(b, off + idx, writeSize);
                wroteInChunk += writeSize;
                idx += writeSize;
                if (wroteInChunk == chunkLength) {
                    while (!def.needsInput()) {
                        deflate();
                    }
                    wroteInChunk = 0;
                }
                writeSize = Math.min(chunkLength, len - idx);
            }
        }
    }

    /**
     * Writes a byte to the compressed output stream.
     * <p>
     * This method will block until the byte can be written.
     *
     * @param b the byte to be written
     * @throws IOException if an I/O error has occurred
     */
    @Override
    public synchronized void write(final int b) throws IOException {
        // FIXME: implement for chunk handling
        byte[] buf1 = new byte[1];
        buf1[0] = (byte) (b & 0xff);
        write(buf1, 0, 1);
    }

    private static final int TRAILER_SIZE = 8;

    /**
     * Finish compression, as same function as GZIPOutputStream.
     * @throws IOException if I/O error occurred.
     */
    public final void finish() throws IOException {
        if (closed) {
            throw new IOException("Already closed!");
        }
        if (!def.finished()) {
            def.finish();
            while (!def.finished()) {
                int len = def.deflate(buf, 0, buf.length);
                if (def.finished() && len <= buf.length - TRAILER_SIZE) {
                    // last deflater buffer. Fit trailer at the end
                    writeTrailer(buf, len);
                    len = len + TRAILER_SIZE;
                    out.write(buf, 0, len);
                    return;
                }
                if (len > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
        byte[] trailer = new byte[TRAILER_SIZE];
        writeTrailer(trailer, 0);
        out.write(trailer);
    }

    private void writeHeader(final RandomAccessOutputStream raout) throws IOException {
        DictZipHeader.writeHeader(header, raout);
    }

    private void writeTrailer(final byte[] b, final int offset) throws IOException {
        writeInt((int) crc.getValue(), b, offset); // CRC-32 of uncompr. data
        writeInt(def.getTotalIn(), b, offset + 4); // Number of uncompr. bytes
    }

    /*
     * Writes integer in Intel byte order to a byte array, starting at a
     * given offset.
     */
    private void writeInt(final int i, final byte[] b, final int offset) throws IOException {
        writeShort(i & 0xffff, b, offset); // int low short val
        writeShort((i >> 16) & 0xffff, b, offset + 2); // int high short val
    }

    /*
     * Writes short integer in Intel byte order to a byte array, starting
     * at a given offset
     */
    private void writeShort(final int s, final byte[] b, final int offset) throws IOException {
        b[offset] = (byte) (s & 0xff); // low byte
        b[offset + 1] = (byte) ((s >> 8) & 0xff); // high byte
    }
}
