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
 * CrawlingAccessException is an exception class that represents an issue encountered while accessing a resource during the crawling process.
 * It extends CrawlerSystemException and provides functionality to set and check the log level for the exception.
 *
 * <p>
 * This exception can be thrown when there are problems accessing URLs, files, or any other resources needed for crawling.
 * It includes constructors to handle messages, causes, or both.
 * </p>
 *
 * <p>
 * The log level can be set to DEBUG, INFO, WARN, or ERROR, and the class provides methods to check if a specific log level is enabled.
 * </p>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>
 * try {
 *     // Attempt to access a resource
 * } catch (CrawlingAccessException e) {
 *     if (e.isErrorEnabled()) {
 *         // Log the error
 *     }
 * }
 * </pre>
 */
public class CrawlingAccessException extends CrawlerSystemException {

    private static final long serialVersionUID = 1L;

    /**
     * Log level constant for debug messages.
     */
    public static final String DEBUG = "DEBUG";

    /**
     * Log level constant for info messages.
     */
    public static final String INFO = "INFO";

    /**
     * Log level constant for warning messages.
     */
    public static final String WARN = "WARN";

    /**
     * Log level constant for error messages.
     */
    public static final String ERROR = "ERROR";

    /** The log level for this exception, defaults to INFO */
    private String logLevel = INFO;

    /**
     * Constructs a new CrawlingAccessException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public CrawlingAccessException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new CrawlingAccessException with the specified detail message.
     *
     * @param message the detail message
     */
    public CrawlingAccessException(final String message) {
        super(message);
    }

    /**
     * Constructs a new CrawlingAccessException with the specified cause.
     *
     * @param cause the cause
     */
    public CrawlingAccessException(final Throwable cause) {
        super(cause);
    }

    /**
     * Sets the log level for this exception.
     *
     * @param logLevel the log level to set
     */
    public void setLogLevel(final String logLevel) {
        this.logLevel = logLevel;
    }

    /**
     * Checks if the log level is DEBUG.
     *
     * @return true if DEBUG, false otherwise
     */
    public boolean isDebugEnabled() {
        return DEBUG.equals(logLevel);
    }

    /**
     * Checks if the log level is INFO.
     *
     * @return true if INFO, false otherwise
     */
    public boolean isInfoEnabled() {
        return INFO.equals(logLevel);
    }

    /**
     * Checks if the log level is WARN.
     *
     * @return true if WARN, false otherwise
     */
    public boolean isWarnEnabled() {
        return WARN.equals(logLevel);
    }

    /**
     * Checks if the log level is ERROR.
     * @return true if ERROR, false otherwise.
     */
    public boolean isErrorEnabled() {
        return ERROR.equals(logLevel);
    }

}
