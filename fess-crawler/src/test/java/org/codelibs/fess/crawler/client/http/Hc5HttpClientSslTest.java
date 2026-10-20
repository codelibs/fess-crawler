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

import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.dbflute.utflute.core.PlainTestCase;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

/**
 * Verifies the TLS settings of the connection manager built by {@link Hc5HttpClient}
 * against an HTTPS server whose self-signed certificate does not match the host name.
 */
public class Hc5HttpClientSslTest extends PlainTestCase {

    private int get(final boolean ignoreSslCertificate) throws Exception {
        final Path keyStorePath = Files.createTempDirectory("hc5-ssl").resolve("keystore.p12");
        final Process keytool = new ProcessBuilder(System.getProperty("java.home") + "/bin/keytool", "-genkeypair", "-alias", "test",
                "-keyalg", "RSA", "-keysize", "2048", "-dname", "CN=other.example", "-validity", "2", "-storetype", "PKCS12", "-keystore",
                keyStorePath.toString(), "-storepass", "changeit", "-keypass", "changeit").redirectErrorStream(true).start();
        keytool.getInputStream().readAllBytes();
        assertEquals(0, keytool.waitFor());

        final KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream in = new FileInputStream(keyStorePath.toFile())) {
            keyStore.load(in, "changeit".toCharArray());
        }
        final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, "changeit".toCharArray());
        final SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(kmf.getKeyManagers(), null, null);

        final HttpsServer server = HttpsServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.setHttpsConfigurator(new HttpsConfigurator(serverContext));
        server.createContext("/", exchange -> {
            final byte[] body = "ok".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        try {
            final Hc5HttpClient client = new Hc5HttpClient();
            final Map<String, Object> params = new HashMap<>();
            params.put(HcHttpClient.IGNORE_SSL_CERTIFICATE_PROPERTY, ignoreSslCertificate);
            client.setInitParameterMap(params);
            final PoolingHttpClientConnectionManager connectionManager = client.buildConnectionManager(5000, 5000);
            try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {
                return httpClient.execute(new HttpGet("https://127.0.0.1:" + server.getAddress().getPort() + "/"), r -> r.getCode());
            }
        } finally {
            server.stop(0);
        }
    }

    @Test
    public void test_ignoreSslCertificate_acceptsUntrustedCertificateAndHostMismatch() throws Exception {
        assertEquals(200, get(true));
    }

    @Test
    public void test_validateSslCertificate_rejectsUntrustedCertificate() throws Exception {
        try {
            get(false);
            fail();
        } catch (final javax.net.ssl.SSLException e) {
            // expected
        }
    }
}
