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
package org.seasar.robot.dbflute.infra.diffmap;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.seasar.robot.dbflute.helper.mapstring.MapListFile;

/**
 * The file handling for difference map.
 * @author jflute
 * @since 0.9.7.1 (2010/06/06 Sunday)
 */
public class DiffMapFile {

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public Map<String, Object> readMap(InputStream ins) {
        final MapListFile mapListFile = new MapListFile();
        return mapListFile.readMap(ins);
    }

    // ===================================================================================
    //                                                                               Write
    //                                                                               =====
    public void writeMap(OutputStream ous, Map<String, Object> map) {
        final MapListFile mapListFile = new MapListFile();
        mapListFile.writeMap(ous, map);
    }
}