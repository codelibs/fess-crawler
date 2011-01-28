/*
 * Copyright 2004-2011 the Seasar Foundation and the Others.
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
package org.seasar.robot.dbflute.twowaysql.factory;

import org.seasar.robot.dbflute.twowaysql.SqlAnalyzer;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/24 Monday)
 */
public class DefaultSqlAnalyzerFactory implements SqlAnalyzerFactory {

    /**
     * {@inheritDoc}
     */
    public SqlAnalyzer create(String sql, boolean blockNullParameter) {
        return new SqlAnalyzer(sql, blockNullParameter);
    }
}
