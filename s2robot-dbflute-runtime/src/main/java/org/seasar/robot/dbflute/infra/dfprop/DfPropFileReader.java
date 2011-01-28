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
package org.seasar.robot.dbflute.infra.dfprop;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * The reader for DBFlute property file (dfprop).
 * @author jflute
 * @since 0.9.6 (2009/10/28 Wednesday)
 * @deprecated Please use DfPropFile.java
 */
public class DfPropFileReader {

    // ===================================================================================
    //                                                                            Read Map
    //                                                                            ========
    /**
     * Read the map string file. <br />
     * If the type of values is various type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment. <br />
     * This is the most basic method here.
     * <pre>
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = list:{element1 ; element2 }
     *     ; key3 = map:{key1 = value1 ; key2 = value2 }
     *     ; ... = ...
     * }
     * </pre>
     * @param ins The input stream for DBFlute property file. (NotNull)
     * @return The read map. (NotNull)
     */
    public Map<String, Object> readMap(InputStream ins) {
        return new DfPropFile().readMap(ins);
    }

    /**
     * Read the map string file as string value. <br />
     * If the type of all values is string type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = string-value1
     *     ; key2 = string-value2
     *     ; ... = ...
     * }
     * </pre>
     * @param ins The input stream for DBFlute property file. (NotNull)
     * @return The read map whose values is string. (NotNull)
     */
    public Map<String, String> readMapAsStringValue(InputStream ins) {
        return new DfPropFile().readMapAsStringValue(ins);
    }

    /**
     * Read the map string file as string list value. <br />
     * If the type of all values is string list type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = list:{string-element1 ; string-element2 ; ...}
     *     ; key2 = list:{string-element1 ; string-element2 ; ...}
     *     ; ... = list:{...}
     * }
     * </pre>
     * @param ins The input stream for DBFlute property file. (NotNull)
     * @return The read map whose values is string list. (NotNull)
     */
    public Map<String, List<String>> readMapAsStringListValue(InputStream ins) {
        return new DfPropFile().readMapAsStringListValue(ins);
    }

    /**
     * Read the map string file as string map value. <br />
     * If the type of all values is string map type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * <pre>
     * ex)
     * map:{
     *     ; key1 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; key2 = map:{string-key1 = string-value1 ; string-key2 = string-value2 }
     *     ; ... = map:{...}
     * }
     * </pre>
     * @param ins The input stream for DBFlute property file. (NotNull)
     * @return The read map whose values is string map. (NotNull)
     */
    public Map<String, Map<String, String>> readMapAsStringMapValue(InputStream ins) {
        return new DfPropFile().readMapAsStringMapValue(ins);
    }

    // ===================================================================================
    //                                                                           Read List
    //                                                                           =========
    /**
     * Read the list string file. <br />
     * If the type of values is various type, this method is available. <br />
     * A trimmed line that starts with '#' is treated as line comment. <br />
     * <pre>
     * list:{
     *     ; element1
     *     ; list:{element2-1 ; element2-2 }
     *     ; map:{key3-1 = value3-1 ; key3-2 = value3-2 }
     *     ; ... = ...
     * }
     * </pre>
     * @param ins The input stream for DBFlute property file. (NotNull)
     * @return The read list. (NotNull)
     */
    public List<Object> readList(InputStream ins) {
        return new DfPropFile().readList(ins);
    }

    // ===================================================================================
    //                                                                              String
    //                                                                              ======
    // -----------------------------------------------------
    //                                                  Read
    //                                                  ----
    /**
     * Read the string file. <br />
     * A trimmed line that starts with '#' is treated as line comment.
     * @param ins The input stream for DBFlute property file. (NotNull)
     * @return The read string. (NotNull)
     */
    public String readString(InputStream ins) {
        return new DfPropFile().readString(ins);
    }
}