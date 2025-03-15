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
 * Exception thrown during the extraction process in the crawler.
 * This exception indicates a failure or error that occurred while extracting content from a crawled resource.
 * It extends {@link org.codelibs.fess.crawler.exception.CrawlerSystemException} and provides constructors
 * to handle different scenarios such as wrapping another exception or providing a specific error message.
 */
public class ExtractException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ExtractException with the specified message and cause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ExtractException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ExtractException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ExtractException(final String message) {
        super(message);
    }

    /**
     * Constructs a new ExtractException with the specified cause.
     *
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *              (A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public ExtractException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ExtractException with the specified detail message,
     * suppression enabled or disabled, and writable stack trace enabled or disabled.
     *
     * @param message the detail message.
     * @param enableSuppression whether or not suppression is enabled or disabled.
     * @param writableStackTrace whether or not the stack trace should be writable.
     */
    protected ExtractException(final String message, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, enableSuppression, writableStackTrace);
    }
}
