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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;
import org.dbflute.utflute.core.PlainTestCase;

/**
 * Test class for IgnoreCloseInputStream.
 *
 * @author shinsuke
 */
public class IgnoreCloseInputStreamTest extends PlainTestCase {

    @Test
    public void test_close_isIgnored() throws IOException {
        // Test that close() is ignored
        byte[] data = "Test data".getBytes();
        ByteArrayInputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        // Read some data
        assertEquals((int) 'T', stream.read());

        // Close should be ignored
        stream.close();

        // Should still be able to read
        assertEquals((int) 'e', stream.read());
        assertEquals((int) 's', stream.read());
        assertEquals((int) 't', stream.read());
    }

    @Test
    public void test_read_delegatesToUnderlying() throws IOException {
        // Test that read() delegates to underlying stream
        byte[] data = "ABC".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        assertEquals((int) 'A', stream.read());
        assertEquals((int) 'B', stream.read());
        assertEquals((int) 'C', stream.read());
        assertEquals(-1, stream.read()); // EOF
    }

    @Test
    public void test_readBytes_delegatesToUnderlying() throws IOException {
        // Test that read(byte[]) delegates to underlying stream
        byte[] data = "Hello World".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        byte[] buffer = new byte[5];
        int bytesRead = stream.read(buffer);

        assertEquals(5, bytesRead);
        assertEquals("Hello", new String(buffer));
    }

    @Test
    public void test_readBytesWithOffset_delegatesToUnderlying() throws IOException {
        // Test that read(byte[], int, int) delegates to underlying stream
        byte[] data = "0123456789".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        byte[] buffer = new byte[10];
        int bytesRead = stream.read(buffer, 2, 5);

        assertEquals(5, bytesRead);
        assertEquals("01234", new String(buffer, 2, 5));
    }

    @Test
    public void test_available_delegatesToUnderlying() throws IOException {
        // Test that available() delegates to underlying stream
        byte[] data = "Test data".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        assertEquals(9, stream.available());

        stream.read();
        assertEquals(8, stream.available());
    }

    @Test
    public void test_skip_delegatesToUnderlying() throws IOException {
        // Test that skip() delegates to underlying stream
        byte[] data = "0123456789".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        long skipped = stream.skip(5);
        assertEquals(5L, skipped);
        assertEquals((int) '5', stream.read());
    }

    @Test
    public void test_markSupported_delegatesToUnderlying() {
        // Test that markSupported() delegates to underlying stream
        byte[] data = "Test".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        // ByteArrayInputStream supports mark
        assertTrue(stream.markSupported());
    }

    @Test
    public void test_markAndReset_delegatesToUnderlying() throws IOException {
        // Test that mark() and reset() delegate to underlying stream
        byte[] data = "ABCDEFGH".getBytes();
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        assertEquals((int) 'A', stream.read());
        assertEquals((int) 'B', stream.read());

        // Mark current position
        stream.mark(10);

        assertEquals((int) 'C', stream.read());
        assertEquals((int) 'D', stream.read());

        // Reset to marked position
        stream.reset();

        // Should read from marked position again
        assertEquals((int) 'C', stream.read());
        assertEquals((int) 'D', stream.read());
    }

    @Test
    public void test_toString_delegatesToUnderlying() {
        // Test that toString() delegates to underlying stream
        byte[] data = "Test".getBytes();
        ByteArrayInputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        String result = stream.toString();
        assertNotNull(result);
        assertTrue(result.contains("ByteArrayInputStream"));
    }

    @Test
    public void test_multipleCloseCallsDoNothing() throws IOException {
        // Test that multiple close() calls are all ignored
        byte[] data = "Test data".getBytes();
        ByteArrayInputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        // Multiple close calls should all be ignored
        stream.close();
        stream.close();
        stream.close();

        // Should still be able to read
        assertEquals((int) 'T', stream.read());
        assertEquals((int) 'e', stream.read());
    }

    @Test
    public void test_emptyStream() throws IOException {
        // Test with empty stream
        byte[] data = new byte[0];
        InputStream underlyingStream = new ByteArrayInputStream(data);
        IgnoreCloseInputStream stream = new IgnoreCloseInputStream(underlyingStream);

        assertEquals(-1, stream.read());
        assertEquals(0, stream.available());
    }
}
