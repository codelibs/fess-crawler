/*
 * Copyright 2012-2025 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.fess.crawler.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * A wrapper for an {@link InputStream} that ignores the {@link #close()} method call.
 * This can be useful when you want to prevent the underlying input stream from being closed.
 *
 * <p>All other methods delegate to the wrapped input stream.</p>
 *
 */
public class IgnoreCloseInputStream extends InputStream {

    /** The wrapped input stream. */
    private InputStream inputStream;

    /**
     * Constructs a new IgnoreCloseInputStream that wraps the specified input stream.
     *
     * @param inputStream the input stream to wrap
     */
    public IgnoreCloseInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Overrides the close method to ignore the close operation.
     * The underlying input stream will not be closed.
     *
     * @throws IOException if an I/O error occurs (not thrown in this implementation)
     */
    @Override
    public void close() throws IOException {
        // inputStream.close();
    }

    /**
     * Returns the number of bytes that can be read from this input stream without blocking.
     *
     * @return the number of bytes available
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    /**
     * Marks the current position in this input stream.
     *
     * @param readlimit the maximum limit of bytes that can be read before the mark position becomes invalid
     */
    @Override
    public synchronized void mark(final int readlimit) {
        inputStream.mark(readlimit);
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
     *
     * @return true if this stream instance supports the mark and reset methods; false otherwise
     */
    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    /**
     * Reads the next byte of data from the input stream.
     *
     * @return the next byte of data, or -1 if the end of the stream is reached
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    /**
     * Reads up to len bytes of data from the input stream into an array of bytes.
     *
     * @param b the buffer into which the data is read
     * @param off the start offset in array b at which the data is written
     * @param len the maximum number of bytes to read
     * @return the total number of bytes read into the buffer, or -1 if there is no more data
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    /**
     * Reads some number of bytes from the input stream and stores them into the buffer array b.
     *
     * @param b the buffer into which the data is read
     * @return the total number of bytes read into the buffer, or -1 if there is no more data
     * @throws IOException if an I/O error occurs
     */
    @Override
    public int read(final byte[] b) throws IOException {
        return inputStream.read(b);
    }

    /**
     * Repositions this stream to the position at the time the mark method was last called.
     *
     * @throws IOException if this stream has not been marked or if the mark has been invalidated
     */
    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    /**
     * Skips over and discards n bytes of data from this input stream.
     *
     * @param n the number of bytes to be skipped
     * @return the actual number of bytes skipped
     * @throws IOException if an I/O error occurs
     */
    @Override
    public long skip(final long n) throws IOException {
        return inputStream.skip(n);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return inputStream.toString();
    }

}
