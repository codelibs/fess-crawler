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
package org.seasar.robot.dbflute.helper.mapstring;

import java.util.List;
import java.util.Map;

/**
 * The string for map and list.
 * <pre> 
 * ex) map-string
 *   map:{key1=value1,key2=list:{value21,value22,value23},key3=map:{key31=value31}}
 * 
 * ex) list-string
 *   list:{key1=value1,key2=list:{value21,value22,value23},key3=map:{key31=value31}}
 * </pre>
 * @author jflute
 */
public interface MapListString {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Default of map-mark. */
    public static final String DEFAULT_MAP_MARK = "map:";

    /** Default of list-mark. */
    public static final String DEFAULT_LIST_MARK = "list:";

    /** Default of start-brace. */
    public static final String DEFAULT_START_BRACE = "{";

    /** Default of end-brace. */
    public static final String DEFAULT_END_BRACE = "}";

    /** Default of delimiter. */
    public static final String DEFAULT_DELIMITER = ";";

    /** Default of equal. */
    public static final String DEFAULT_EQUAL = "=";

    // ===================================================================================
    //                                                                               Build
    //                                                                               =====
    /**
     * Build map-string from map.
     * @param map The instance of map. (NotNull)
     * @return The string as map expression. (NotNull)
     */
    String buildMapString(Map<String, ? extends Object> map);

    /**
     * Build list-string from list.
     * @param list The instance of list. (NotNull)
     * @return The string as list expression. (NotNull)
     */
    String buildListString(List<? extends Object> list);

    // ===================================================================================
    //                                                                            Generate
    //                                                                            ========
    /**
     * Generate map from map-string.
     * @param mapString Map-string (NotNull)
     * @return Generated map. (NotNull)
     */
    Map<String, Object> generateMap(String mapString);

    /**
     * Generate map from list-string. {Implement}
     * @param listString List-string (NotNull)
     * @return Generated list. (NotNull)
     */
    List<Object> generateList(String listString);
}