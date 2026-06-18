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
import java.net.ServerSocket;

/**
 * Test helper for {@code OpenSearchRunner}.
 *
 * <p>
 * {@code OpenSearchRunner} decides an HTTP port by probing {@code localhost}
 * (IPv4) with a TCP connect. That probe can report a port as free even when it
 * is already bound on {@code [::1]} (IPv6), which makes the node fail to start
 * with "Address already in use". To avoid this, let the OS assign an ephemeral
 * port and pass it to the runner directly.
 * </p>
 */
public final class OpenSearchRunnerUtil {

    private OpenSearchRunnerUtil() {
        // nothing
    }

    /**
     * Returns an ephemeral port assigned by the OS.
     *
     * @return a free TCP port
     */
    public static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to find a free port.", e);
        }
    }
}
