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
package org.seasar.robot.dbflute.bhv.core.supplement;

import javax.sql.DataSource;

/**
 * The key generator of sequence cache.
 * @author jflute
 * @since 0.9.6.4 (2010/01/15 Friday)
 */
public interface SequenceCacheKeyGenerator {

    /**
     * Generate the key for sequence cache.
     * @param tableName The name of table. The one of elements for default key. (NotNull)
     * @param sequenceName The name of sequence. The one of elements for default key. (NotNull)
     * @param dataSource The data source for a database connection. (NotNull)
     * @return The generated key for sequence cache. (NotNull)
     */
    String generateKey(String tableName, String sequenceName, DataSource dataSource);
}
