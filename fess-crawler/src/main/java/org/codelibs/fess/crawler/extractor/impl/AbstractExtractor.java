package org.codelibs.fess.crawler.extractor.impl;

import java.util.List;

import javax.annotation.Resource;

import org.codelibs.fess.crawler.container.CrawlerContainer;
import org.codelibs.fess.crawler.extractor.Extractor;
import org.codelibs.fess.crawler.extractor.ExtractorFactory;

public abstract class AbstractExtractor implements Extractor {

    @Resource
    protected CrawlerContainer crawlerContainer;

    public void register(final List<String> keyList) {
        final ExtractorFactory extractorFactory = crawlerContainer.getComponent("extractorFactory");
        extractorFactory.addExtractor(keyList, this);
    }

}
