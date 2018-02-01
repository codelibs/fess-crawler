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
package org.codelibs.fess.crawler.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tika.metadata.CreativeCommons;
import org.apache.tika.metadata.DublinCore;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.MSOffice;
import org.apache.tika.metadata.TikaMetadataKeys;
import org.apache.tika.metadata.TikaMimeKeys;
import org.apache.tika.parser.pdf.PDFParser;

/**
 * @author shinsuke
 *
 */
public class ExtractData implements CreativeCommons, DublinCore, HttpHeaders,
        MSOffice, TikaMetadataKeys, TikaMimeKeys, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String URL = "url";

    @SuppressWarnings("deprecation")
    public static final String PDF_PASSWORD = PDFParser.PASSWORD;

    public static final String FILE_PASSWORDS = "file.passwords";

    protected Map<String, String[]> metadata = new HashMap<>();

    protected String content;

    public ExtractData() {
        // nothing
    }

    public ExtractData(final String content) {
        this.content = content;
    }

    public void putValues(final String key, final String[] values) {
        metadata.put(key, values);
    }

    public void putValue(final String key, final String value) {
        metadata.put(key, new String[] { value });
    }

    public String[] getValues(final String key) {
        return metadata.get(key);
    }

    public Set<String> getKeySet() {
        return metadata.keySet();
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ExtractData [metadata=" + metadata + ", content=" + content
                + "]";
    }
}
