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

import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.codelibs.fess.crawler.entity.ExtractData;
import org.codelibs.fess.crawler.exception.ExtractException;

/**
 * Extracts text content from Microsoft PowerPoint documents.
 */
public class MsPowerPointExtractor extends AbstractExtractor {

    /**
     * Creates a new MsPowerPointExtractor instance.
     */
    public MsPowerPointExtractor() {
        super();
    }

    /**
     * Extracts text from the PowerPoint input stream.
     * @param in The input stream.
     * @param params The parameters.
     * @return The extracted data.
     */
    @Override
    public ExtractData getText(final InputStream in, final Map<String, String> params) {
        validateInputStream(in);
        try (final HSLFSlideShow slideShow = new HSLFSlideShow(in);
                final SlideShowExtractor<HSLFShape, HSLFTextParagraph> extractor = new SlideShowExtractor<>(slideShow)) {
            return new ExtractData(extractor.getText());
        } catch (final IOException e) {
            throw new ExtractException("Failed to extract text from PowerPoint document.", e);
        }
    }

}
