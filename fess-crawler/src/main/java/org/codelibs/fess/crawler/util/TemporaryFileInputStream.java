/*
 * Copyright 2012-2018 CodeLibs Project and the Others.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class TemporaryFileInputStream extends InputStream {
    private static final Logger logger = LoggerFactory
            .getLogger(TemporaryFileInputStream.class);

    private final File tempFile;

    private final FileInputStream fileInputStream;

    public TemporaryFileInputStream(final File tempFile)
            throws FileNotFoundException {
        this.tempFile = tempFile;
        fileInputStream = new FileInputStream(tempFile);
    }

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

    @Override
    public void close() throws IOException {
        try {
            fileInputStream.close();
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                logger.warn("Could not delete a temporary file: "
                        + tempFile.getAbsolutePath());
            }
        }
    }

    @Override
    public synchronized void mark(final int readlimit) {
        fileInputStream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return fileInputStream.markSupported();
    }

    @Override
    public synchronized void reset() throws IOException {
        fileInputStream.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        return fileInputStream.skip(n);
    }

}
