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
package org.seasar.robot.dbflute.resource;

/**
 * The context of internal resource.
 * @author jflute
 */
public class ResourceParameter {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected String _outsideSqlPackage;
    protected String _logDateFormat;
    protected String _logTimestampFormat;

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public String getOutsideSqlPackage() {
        return _outsideSqlPackage;
    }

    public void setOutsideSqlPackage(String outsideSqlPackage) {
        _outsideSqlPackage = outsideSqlPackage;
    }

    public String getLogDateFormat() {
        return _logDateFormat;
    }

    public void setLogDateFormat(String logDateFormat) {
        _logDateFormat = logDateFormat;
    }

    public String getLogTimestampFormat() {
        return _logTimestampFormat;
    }

    public void setLogTimestampFormat(String logTimestampFormat) {
        _logTimestampFormat = logTimestampFormat;
    }
}
