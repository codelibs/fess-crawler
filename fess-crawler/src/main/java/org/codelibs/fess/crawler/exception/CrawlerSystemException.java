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
 * The class CrawlerSystemException is a runtime exception that is thrown when a system error occurs during crawling.
 * It extends the RuntimeException class and provides constructors for creating exceptions with a message, a cause, or both.
 * It also provides a protected constructor that allows to specify whether or not suppression is enabled or stack trace is writable.
 */
public class CrawlerSystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new {@code CrawlerSystemException} with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public CrawlerSystemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CrawlerSystemException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public CrawlerSystemException(final String message) {
        super(message);
    }

    /**
     * Constructs a new CrawlerSystemException with the specified cause.
     *
     * @param cause The cause (which is saved for later retrieval by the
     *        {@link #getCause()} method).  (A {@code null} value is
     *        permitted, and indicates that the cause is nonexistent or
     *        unknown.)
     */
    public CrawlerSystemException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new CrawlerSystemException with the specified detail message and controls suppression and stack trace writing.
     *
     * @param message the detail message
     * @param enableSuppression whether or not suppression is enabled
     * @param writableStackTrace whether or not the stack trace should be writable
     */
    protected CrawlerSystemException(final String message, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, null, enableSuppression, writableStackTrace);
    }
}
