/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.robot.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * This inputstream ignores a close method.
 * 
 * @author shinsuke
 *
 */
public class IgnoreCloseInputStream extends InputStream {

    private InputStream inputStream;

    public IgnoreCloseInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void close() throws IOException {
        // inputStream.close();
    }

    public int available() throws IOException {
        return inputStream.available();
    }

    public boolean equals(Object obj) {
        return inputStream.equals(obj);
    }

    public int hashCode() {
        return inputStream.hashCode();
    }

    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    public boolean markSupported() {
        return inputStream.markSupported();
    }

    public int read() throws IOException {
        return inputStream.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return inputStream.read(b);
    }

    public void reset() throws IOException {
        inputStream.reset();
    }

    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    public String toString() {
        return inputStream.toString();
    }

}
