package org.dict.zip;

import java.io.*;

public class RandomAccessInputStream extends InputStream {

	protected RandomAccessFile in;

	protected int mark = 0;

	public RandomAccessInputStream(RandomAccessFile in) {
	this.in = in;
	}
	public RandomAccessInputStream(String file, String mode) throws IOException {
	this(new RandomAccessFile(file, mode));
	}
	public int available() throws IOException {
	return getLength() - getPos();
	}
	public void close() throws IOException {
		in.close();
	}
	public int getLength() throws IOException {
		return (int)in.length();
	}
	public int getPos() throws IOException {
		return (int)in.getFilePointer();
	}
	public synchronized void mark(int markpos) {
		try {
			mark = getPos();
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}
	public boolean markSupported() {
	return true;
	}
	public synchronized int read() throws IOException {
		return in.read();
	}
	public int read(byte b[], int off, int len) throws IOException {
	return in.read(b, off, len);
	}
	public void readFully(byte b[]) throws IOException {
		in.readFully(b);
	}
	public synchronized void reset() throws IOException {
	in.seek(mark);
	}
	public void seek(long pos) throws IOException {
		in.seek(pos);
	}
	public long skip(long n) throws IOException {
		return (long)in.skipBytes((int)n);
	}
}
