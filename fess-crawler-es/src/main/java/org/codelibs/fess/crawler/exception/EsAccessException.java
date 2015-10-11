package org.codelibs.fess.crawler.exception;

import org.codelibs.fess.crawler.exception.CrawlerSystemException;

public class EsAccessException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    public EsAccessException(final String message) {
        super(message);
    }

}
