/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
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
package org.codelibs.robot.processor.impl;

import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.codelibs.robot.builder.RequestDataBuilder;
import org.codelibs.robot.client.fs.ChildUrlsException;
import org.codelibs.robot.container.ComponentContainer;
import org.codelibs.robot.entity.RequestData;
import org.codelibs.robot.entity.ResponseData;
import org.codelibs.robot.entity.Sitemap;
import org.codelibs.robot.entity.SitemapSet;
import org.codelibs.robot.helper.SitemapsHelper;
import org.codelibs.robot.processor.ResponseProcessor;

/**
 * @author shinsuke
 *
 */
public class SitemapsResponseProcessor implements ResponseProcessor {
    @Resource
    protected ComponentContainer componentContainer;

    @Override
    public void process(final ResponseData responseData) {
        final SitemapsHelper sitemapsHelper = componentContainer
                .getComponent("sitemapsHelper");
        final InputStream responseBody = responseData.getResponseBody();
        final SitemapSet sitemapSet = sitemapsHelper.parse(responseBody);
        final Set<RequestData> requestDataSet = new LinkedHashSet<>();
        for (final Sitemap sitemap : sitemapSet.getSitemaps()) {
            requestDataSet.add(RequestDataBuilder.newRequestData().get()
                    .url(sitemap.getLoc()).build());
        }
        throw new ChildUrlsException(requestDataSet);
    }

}
