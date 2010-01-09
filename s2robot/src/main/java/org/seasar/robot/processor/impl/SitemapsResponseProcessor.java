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
package org.seasar.robot.processor.impl;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import org.seasar.framework.container.SingletonS2Container;
import org.seasar.robot.client.fs.ChildUrlsException;
import org.seasar.robot.entity.ResponseData;
import org.seasar.robot.entity.Sitemap;
import org.seasar.robot.entity.SitemapSet;
import org.seasar.robot.helper.SitemapsHelper;
import org.seasar.robot.processor.ResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author shinsuke
 *
 */
public class SitemapsResponseProcessor implements ResponseProcessor {
    private static final Logger logger = LoggerFactory
            .getLogger(SitemapsResponseProcessor.class);

    /* (non-Javadoc)
     * @see org.seasar.robot.processor.impl.ResponseProcessor#process(org.seasar.robot.entity.ResponseData)
     */
    public void process(ResponseData responseData) {
        SitemapsHelper sitemapsHelper = SingletonS2Container
                .getComponent(SitemapsHelper.class);
        InputStream responseBody = responseData.getResponseBody();
        SitemapSet sitemapSet = sitemapsHelper.parse(responseBody);
        Set<String> urlSet = new LinkedHashSet<String>();
        for (Sitemap sitemap : sitemapSet.getSitemaps()) {
            urlSet.add(sitemap.getLoc());
        }
        throw new ChildUrlsException(urlSet);
    }

}
