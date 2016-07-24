package org.codelibs.fess.crawler.extractor.impl;

import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.http.impl.client.CloseableHttpClient;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.extractor.Extractor;

/**
 * Extract a text by using external http server.
 *
 * @author shinsuke
 *
 */
public class ApiExtractor implements Extractor {

    public String url;

    protected CloseableHttpClient httpClient;

    @PostConstruct
    public void init() {
        // TODO initialize HttpClient
    }
    
    @PreDestroy
    public void destroy(){
        // TODO close httpClient
    }

    @Override
    public ExtractData getText(InputStream in, Map<String, String> params) {
        // TODO send multipart POST request
        return null;
    }

}
