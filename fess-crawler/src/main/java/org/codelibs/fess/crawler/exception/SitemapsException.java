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
package org.codelibs.fess.crawler.exception;

/**
 * Exception thrown during sitemaps processing in the crawler.
 * This exception extends {@link org.codelibs.fess.crawler.exception.CrawlerSystemException}
 * and provides constructors for wrapping other exceptions or creating a new exception with a message.
 */
public class SitemapsException extends CrawlerSystemException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new SitemapsException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public SitemapsException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new SitemapsException with the specified detail message.
     * @param message the detail message
     */
    public SitemapsException(final String message) {
        super(message);
    }

    /**
     * Constructs a new SitemapsException with the specified cause.
     * @param cause the cause of the exception
     */
    public SitemapsException(final Throwable cause) {
        super(cause);
    }
}
