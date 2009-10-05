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
package org.seasar.robot;

/**
 * @author shinsuke
 *
 */
public class RobotCrawlAccessException extends RobotSystemException {

    private static final long serialVersionUID = 1L;

    public static final String DEBUG = "DEBUG";

    public static final String INFO = "ERROR";

    public static final String WARN = "WARN";

    public static final String ERROR = "ERROR";

    private String logLevel = INFO;

    public RobotCrawlAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public RobotCrawlAccessException(String message) {
        super(message);
    }

    public RobotCrawlAccessException(Throwable cause) {
        super(cause);
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isDebugEnabled() {
        return DEBUG.equals(logLevel);
    }

    public boolean isInfoEnabled() {
        return INFO.equals(logLevel);
    }

    public boolean isWarnEnabled() {
        return WARN.equals(logLevel);
    }

    public boolean isErrorEnabled() {
        return ERROR.equals(logLevel);
    }

}
