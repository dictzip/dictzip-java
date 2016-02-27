package org.dict.zip;

import java.io.*;
import java.util.zip.*;

public class DictZipHeader {

	int headerLength;
	int[] chunks;
	int[] offsets;
	int extraLength;
	byte subfieldID1, subfieldID2;
	int subfieldLength;
	int subfieldVersion;
	int chunkLength;
	int chunkCount;

	/**
	 * GZIP header magic number & file header flags
	 */
	public final static int GZIP_MAGIC = 0x8b1f;
	public final static int FTEXT	= 1;	// Extra text
	public final static int FHCRC	= 2;	// Header CRC
	public final static int FEXTRA	= 4;	// Extra field
	public final static int FNAME	= 8;	// File name
	public final static int FCOMMENT	= 16;	// File comment

	private void initOffsets() {
		offsets = new int[chunks.length];
		offsets[0] = headerLength;
		for (int i=1; i<chunks.length; i++) {
			offsets[i] = offsets[i-1]+chunks[i-1];
		}
	}
	private static void log(Object s) {
		System.out.println(s);
	}
public static DictZipHeader readHeader(String s) throws IOException {
	DictZipHeader h = new DictZipHeader();
	CRC32 crc = new CRC32();
	InputStream in = new FileInputStream(s);
	readHeader(h, in, crc);
	in.close();
	return h;
}
public static void readHeader(DictZipHeader h, InputStream is, CRC32 crc) throws IOException {
	CheckedInputStream in = new CheckedInputStream(is, crc);
	crc.reset();

	// Check header magic
	if (readUShort(in) != GZIP_MAGIC) {
	    throw new IOException("Not in GZIP format");
	}
	// Check compression method
	if (readUByte(in) != 8) {
	    throw new IOException("Unsupported compression method");
	}
	// Read flags
	int flg = readUByte(in);
	// Skip MTIME, XFL, and OS fields
	skipBytes(in, 6);
	h.headerLength = 10;
	/* 2 bytes header magic, 1 byte compression method, 1 byte flags
	 4 bytes time, 1 byte extra flags, 1 byte OS */
	// Optional extra field
	if ((flg & FEXTRA) == FEXTRA) {
		h.extraLength = readUShort(in);
		h.headerLength += h.extraLength + 2;
		h.subfieldID1 = (byte)readUByte(in);
		h.subfieldID2 = (byte)readUByte(in);
		h.subfieldLength = readUShort(in); // 2 bytes subfield length
		h.subfieldVersion = readUShort(in); // 2 bytes subfield version
		h.chunkLength = readUShort(in); // 2 bytes chunk length
		h.chunkCount = readUShort(in); // 2 bytes chunk count
		h.chunks = new int[h.chunkCount];
		for (int i=0; i<h.chunkCount; i++) {
			h.chunks[i] = readUShort(in);
		}
	}
	// Skip optional file name
	if ((flg & FNAME) == FNAME) {
	    while (readUByte(in) != 0) { h.headerLength++; }
		h.headerLength++;
	}
	// Skip optional file comment
	if ((flg & FCOMMENT) == FCOMMENT) {
	    while (readUByte(in) != 0) { h.headerLength++; }
		h.headerLength++;
	}
	// Check optional header CRC
	if ((flg & FHCRC) == FHCRC) {
	    int v = (int)crc.getValue() & 0xffff;
	    if (readUShort(in) != v) {
			throw new IOException("Corrupt GZIP header");
	    }
		h.headerLength += 2;
	}
	h.initOffsets();
}
	/*
	 * Reads unsigned byte.
	 */
	public static int readUByte(InputStream in) throws IOException {
	int b = in.read();
	if (b == -1) {
	    throw new EOFException();
	}
	return b;
	}
	/*
	 * Reads unsigned integer in Intel byte order.
	 */
	public static long readUInt(InputStream in) throws IOException {
	long s = readUShort(in);
	return ((long)readUShort(in) << 16) | s;
	}
	/*
	 * Reads unsigned short in Intel byte order.
	 */
	public static int readUShort(InputStream in) throws IOException {
	int b = readUByte(in);
	return ((int)readUByte(in) << 8) | b;
	}
	/*
	 * Skips bytes of input data blocking until all bytes are skipped.
	 * Does not assume that the input stream is capable of seeking.
	 */
	public static void skipBytes(InputStream in, int n) throws IOException {
	byte[] buf = new byte[128];
	while (n > 0) {
	    int len = in.read(buf, 0, n < buf.length ? n : buf.length);
	    if (len == -1) {
		throw new EOFException();
	    }
	    n -= len;
	}
	}
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("\nHeader length = "+headerLength);
		sb.append("\nSubfield ID = "+(char)subfieldID1+(char)subfieldID2);
		sb.append("\nSubfield length = "+subfieldLength);
		sb.append("\nSubfield version = "+subfieldVersion);
		sb.append("\nChunk length = "+chunkLength);
		sb.append("\nNumber of chunks = "+chunkCount);
		return sb.toString();
	}
	/*
	 * Writes GZIP member header.
	 */
	public static void writeHeader(DictZipHeader h, OutputStream out) throws IOException {
		writeShort(out, GZIP_MAGIC);	    // Magic number
		out.write(Deflater.DEFLATED);    // Compression method (CM)
		out.write(FEXTRA);		    // Flags (FLG)
		writeInt(out, 0);		    // Modification time (MTIME)
		out.write(0);		    // Extra flags (XFL)
		out.write(0);		    // Operating system (OS)
		writeShort(out, h.extraLength); // extra field length
		out.write(h.subfieldID1);
		out.write(h.subfieldID2); // subfield ID
		writeShort(out, h.extraLength); // extra field length
		writeShort(out, h.subfieldVersion); // extra field length
		writeShort(out, h.chunkLength); // extra field length
		writeShort(out, h.chunkCount); // extra field length
		for (int i=0; i<h.chunkCount; i++) {
			writeShort(out, h.chunks[i]);
		}
	}
	/*
	 * Writes integer in Intel byte order.
	 */
	public static void writeInt(OutputStream out, int i) throws IOException {
	writeShort(out, i & 0xffff);
	writeShort(out, (i >> 16) & 0xffff);
	}
	/*
	 * Writes short integer in Intel byte order.
	 */
	public static void writeShort(OutputStream out, int s) throws IOException {
	out.write(s & 0xff);
	out.write((s >> 8) & 0xff);
	}
	/*
	 * Offset getter
	 */
	public int getOffset(int start){
		return start % this.chunkLength;
	}
	/*
	 * Return dictionary position
	 */
	public int getPosition(int start) {
		int idx = start / this.chunkLength;
		return this.offsets[idx];
	}
}
