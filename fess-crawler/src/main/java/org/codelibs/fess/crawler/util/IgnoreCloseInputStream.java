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

import java.io.IOException;
import java.io.InputStream;

/**
 * This inputstream ignores a close method.
 *
 * @author shinsuke
 *
 */
public class IgnoreCloseInputStream extends InputStream {

    private transient InputStream inputStream;

    public IgnoreCloseInputStream(final InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void close() throws IOException {
        // inputStream.close();
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }

    @Override
    public boolean equals(final Object obj) {
        return inputStream.equals(obj);
    }

    @Override
    public int hashCode() {
        return inputStream.hashCode();
    }

    @Override
    public void mark(final int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public int read(final byte[] b, final int off, final int len)
            throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public int read(final byte[] b) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public long skip(final long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public String toString() {
        return inputStream.toString();
    }

}
