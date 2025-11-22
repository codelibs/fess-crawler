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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.codelibs.core.io.FileUtil;

/**
 * A custom InputStream that wraps a temporary file. This class ensures that the temporary file
 * is deleted when the stream is closed.
 */
public class TemporaryFileInputStream extends InputStream {

    private final File tempFile;

    private final FileInputStream fileInputStream;

    /**
     * A class that provides an input stream for reading from a temporary file.
     * This class wraps a {@link FileInputStream} to read from the specified temporary file.
     *
     * @param tempFile the temporary file to be read
     * @throws FileNotFoundException if the specified file does not exist
     */
    public TemporaryFileInputStream(final File tempFile) throws FileNotFoundException {
        this.tempFile = tempFile;
        fileInputStream = new FileInputStream(tempFile);
    }

    /**
     * Returns the temporary file associated with this input stream.
     *
     * @return the temporary file
     */
    public File getTemporaryFile() {
        return tempFile;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        return fileInputStream.read();
    }

    @Override
    public int available() throws IOException {
        return fileInputStream.available();
    }

    /**
     * Closes this input stream and releases any system resources associated with the stream.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        try {
            fileInputStream.close();
        } finally {
            FileUtil.deleteInBackground(tempFile);
        }
    }

    /**
     * Marks the current position in this input stream. A subsequent call to the reset method repositions this stream at the last marked position so that subsequent reads re-read the same bytes.
     * This method delegates to {@link FileInputStream#mark(int)}.
     *
     * @param readlimit the maximum limit of bytes that can be read before the mark position becomes invalid
     */
    @Override
    public synchronized void mark(final int readlimit) {
        fileInputStream.mark(readlimit);
    }

    /**
     * Tests if this input stream supports the mark and reset methods.
     * This method delegates to {@link FileInputStream#markSupported()}.
     *
     * @return {@code true} if this stream type supports the mark and reset method; {@code false} otherwise
     */
    @Override
    public boolean markSupported() {
        return fileInputStream.markSupported();
    }

    /**
     * Repositions this stream to the position at the time the mark method was last called on this input stream.
     * This method delegates to {@link FileInputStream#reset()}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public synchronized void reset() throws IOException {
        fileInputStream.reset();
    }

    /**
     * Skips over and discards {@code n} bytes of data from this input stream.
     * This method delegates to {@link FileInputStream#skip(long)}.
     *
     * @param n the number of bytes to be skipped
     * @return the actual number of bytes skipped
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long skip(final long n) throws IOException {
        return fileInputStream.skip(n);
    }

}
