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
package org.codelibs.fess.crawler.client.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codelibs.fess.crawler.entity.ResponseData;
import org.codelibs.fess.crawler.util.CrawlerWebServer;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link Hc5HttpClient} connects through a SOCKS5 proxy when {@code socksProxyHost} and
 * {@code socksProxyPort} are set, and connects directly otherwise.
 */
public class Hc5SocksProxyTest extends PlainTestCase {

    /** A SOCKS5 server without authentication that counts the CONNECT requests it relays. */
    private static class Socks5Server {
        private final ServerSocket serverSocket;

        private final AtomicInteger connectCount = new AtomicInteger();

        Socks5Server() throws IOException {
            serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
            final Thread acceptor = new Thread(() -> {
                try {
                    while (!serverSocket.isClosed()) {
                        final Socket client = serverSocket.accept();
                        final Thread handler = new Thread(() -> handle(client));
                        handler.setDaemon(true);
                        handler.start();
                    }
                } catch (final IOException e) {
                    // closed
                }
            });
            acceptor.setDaemon(true);
            acceptor.start();
        }

        int port() {
            return serverSocket.getLocalPort();
        }

        int connectCount() {
            return connectCount.get();
        }

        void stop() throws IOException {
            serverSocket.close();
        }

        private void handle(final Socket client) {
            try (client) {
                final DataInputStream in = new DataInputStream(client.getInputStream());
                final OutputStream out = client.getOutputStream();
                // greeting: VER NMETHODS METHODS...
                in.readByte();
                in.readFully(new byte[in.readUnsignedByte()]);
                out.write(new byte[] { 5, 0 });
                out.flush();
                // request: VER CMD RSV ATYP DST.ADDR DST.PORT
                final byte[] header = new byte[4];
                in.readFully(header);
                final String host;
                switch (header[3]) {
                case 1: {
                    final byte[] addr = new byte[4];
                    in.readFully(addr);
                    host = InetAddress.getByAddress(addr).getHostAddress();
                    break;
                }
                case 3: {
                    final byte[] name = new byte[in.readUnsignedByte()];
                    in.readFully(name);
                    host = new String(name, "UTF-8");
                    break;
                }
                default:
                    return;
                }
                final int port = in.readUnsignedShort();
                connectCount.incrementAndGet();
                try (Socket target = new Socket(host, port)) {
                    out.write(new byte[] { 5, 0, 0, 1, 0, 0, 0, 0, 0, 0 });
                    out.flush();
                    final Thread upstream = new Thread(() -> pipe(client, target));
                    upstream.setDaemon(true);
                    upstream.start();
                    pipe(target, client);
                    upstream.join(5000);
                }
            } catch (final IOException | InterruptedException e) {
                // the client went away
            }
        }

        private void pipe(final Socket from, final Socket to) {
            try {
                final InputStream in = from.getInputStream();
                final OutputStream out = to.getOutputStream();
                final byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) != -1) {
                    out.write(buf, 0, len);
                    out.flush();
                }
                to.shutdownOutput();
            } catch (final IOException e) {
                // the peer went away
            }
        }
    }

    private Hc5HttpClient createClient(final Map<String, Object> initParams) {
        final Hc5HttpClient client = new Hc5HttpClient();
        client.setInitParameterMap(initParams);
        client.init();
        return client;
    }

    @Test
    public void test_viaSocksProxy_initParameter() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();
        final Socks5Server socks = new Socks5Server();
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.SOCKS_PROXY_HOST_PROPERTY, "127.0.0.1");
            params.put(HcHttpClient.SOCKS_PROXY_PORT_PROPERTY, socks.port());
            final Hc5HttpClient client = createClient(params);
            try {
                final ResponseData responseData = client.doGet("http://127.0.0.1:" + server.getPort() + "/");
                assertEquals(200, responseData.getHttpStatusCode());
                assertEquals(1, socks.connectCount());
            } finally {
                client.close();
            }
        } finally {
            socks.stop();
            server.stop();
        }
    }

    @Test
    public void test_viaSocksProxy_setter() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();
        final Socks5Server socks = new Socks5Server();
        try {
            final Hc5HttpClient client = new Hc5HttpClient();
            client.setSocksProxyHost("127.0.0.1");
            client.setSocksProxyPort(socks.port());
            client.init();
            try {
                final ResponseData responseData = client.doGet("http://127.0.0.1:" + server.getPort() + "/");
                assertEquals(200, responseData.getHttpStatusCode());
                assertEquals(1, socks.connectCount());
            } finally {
                client.close();
            }
        } finally {
            socks.stop();
            server.stop();
        }
    }

    @Test
    public void test_noSocksProxy_connectsDirectly() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();
        final Socks5Server socks = new Socks5Server();
        try {
            final Hc5HttpClient client = createClient(new HashMap<>());
            try {
                final ResponseData responseData = client.doGet("http://127.0.0.1:" + server.getPort() + "/");
                assertEquals(200, responseData.getHttpStatusCode());
                assertEquals(0, socks.connectCount());
            } finally {
                client.close();
            }
        } finally {
            socks.stop();
            server.stop();
        }
    }

    @Test
    public void test_portOnly_connectsDirectly() throws Exception {
        final CrawlerWebServer server = new CrawlerWebServer(0);
        server.start();
        final Socks5Server socks = new Socks5Server();
        try {
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.SOCKS_PROXY_PORT_PROPERTY, socks.port());
            final Hc5HttpClient client = createClient(params);
            try {
                final ResponseData responseData = client.doGet("http://127.0.0.1:" + server.getPort() + "/");
                assertEquals(200, responseData.getHttpStatusCode());
                assertEquals(0, socks.connectCount());
            } finally {
                client.close();
            }
        } finally {
            socks.stop();
            server.stop();
        }
    }
}
