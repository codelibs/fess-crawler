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
 * MimeTypeException is a custom exception class that extends CrawlerSystemException.
 * It is used to indicate exceptions related to MIME type handling during the crawling process.
 * This exception can be thrown with a message, a cause, or both.
 */
public class MimeTypeException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new MimeTypeException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the underlying cause of the exception
     */
    public MimeTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new MimeTypeException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public MimeTypeException(final String message) {
        super(message);
    }

    /**
     * Creates a new MimeTypeException with the specified cause.
     *
     * @param cause the underlying cause of the exception
     */
    public MimeTypeException(final Throwable cause) {
        super(cause);
    }

}
