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

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * An exception that indicates multiple crawling access exceptions occurred.
 * This exception holds an array of Throwable objects representing the individual causes.
 * It extends CrawlingAccessException and provides methods to print the stack traces of all causes.
 *
 */
public class MultipleCrawlingAccessException extends CrawlingAccessException {

    private static final long serialVersionUID = 1L;

    /**
     * The array of throwables that caused this exception.
     */
    private final Throwable[] throwables;

    /**
     * Creates a new MultipleCrawlingAccessException with the specified detail message and array of throwables.
     *
     * @param message the detail message explaining the reason for the exception
     * @param throwables the array of throwables that caused this exception
     */
    public MultipleCrawlingAccessException(final String message, final Throwable[] throwables) {
        super(message);
        if (throwables == null) {
            this.throwables = new Throwable[0];
        } else {
            this.throwables = throwables;
        }
    }

    @Override
    public void printStackTrace(final PrintStream s) {
        super.printStackTrace(s);

        int count = 1;
        for (final Throwable t : throwables) {
            s.println("Cause #" + count + ":");
            t.printStackTrace(s);
            count++;
        }
    }

    @Override
    public void printStackTrace(final PrintWriter s) {
        super.printStackTrace(s);

        int count = 1;
        for (final Throwable t : throwables) {
            s.println("Caused " + count + ":");
            t.printStackTrace(s);
            count++;
        }
    }

    /**
     * Returns the array of Throwables that caused this exception.
     * @return The array of Throwables.
     */
    public Throwable[] getCauses() {
        return throwables;
    }
}
