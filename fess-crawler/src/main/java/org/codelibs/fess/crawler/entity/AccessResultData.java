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
package org.codelibs.fess.crawler.entity;

/**
 * Interface representing the data of an access result.
 *
 * @param <IDTYPE> the type of the identifier
 */
public interface AccessResultData<IDTYPE> {

    /**
     * Retrieves the unique identifier of the access result data.
     *
     * @return the unique identifier of type IDTYPE.
     */
    IDTYPE getId();

    /**
     * Sets the ID of the access result data.
     *
     * @param id the ID to set
     */
    void setId(IDTYPE id);

    /**
     * Retrieves the name of the transformer associated with this access result data.
     *
     * @return the name of the transformer.
     */
    String getTransformerName();

    /**
     * Sets the name of the transformer.
     *
     * @param transformerName the name of the transformer to set
     */
    void setTransformerName(String transformerName);

    /**
     * Retrieves the data as a byte array.
     *
     * @return a byte array containing the data.
     */
    byte[] getData();

    /**
     * Retrieves the data as a String.
     *
     * @return the data in String format.
     */
    String getDataAsString();

    /**
     * Sets the data for this AccessResultData instance.
     *
     * @param data the byte array containing the data to be set
     */
    void setData(byte[] data);

    /**
     * Retrieves the encoding used for the access result data.
     *
     * @return the encoding as a String.
     */
    String getEncoding();

    /**
     * Sets the encoding for the access result data.
     *
     * @param encoding the encoding to set
     */
    void setEncoding(String encoding);

}
