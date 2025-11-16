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
package org.codelibs.fess.crawler.extractor.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.CrawlerSystemException;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Gets a text from .doc file.
 *
 * @author shinsuke
 *
 */
public class MsWordExtractor extends AbstractExtractor {

    /**
     * Creates a new MsWordExtractor instance.
     */
    public MsWordExtractor() {
        super();
    }

    /**
     * Extracts text from the Word input stream.
     * @param in The input stream.
     * @param params The parameters.
     * @return The extracted data.
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        if (in == null) {
            throw new CrawlerSystemException("The inputstream is null.");
        }
        try (final org.apache.poi.hwpf.extractor.WordExtractor wordExtractor = new org.apache.poi.hwpf.extractor.WordExtractor(in)) {
            return new ExtractData(wordExtractor.getText());
        } catch (final IOException e) {
            throw new ExtractException("Failed to extract text from Word document.", e);
        }
    }

}
