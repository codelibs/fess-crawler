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
package org.seasar.robot.dbflute.s2dao.rshandler;

/**
 * {Refers to Seasar and Extends its class}
 * @author jflute
 */
public final class TnRelationKey {

    private Object[] values;

    private int hashCode;

    public TnRelationKey(Object[] values) {
        this.values = values;
        for (int i = 0; i < values.length; ++i) {
            hashCode += values[i].hashCode();
        }
    }

    public Object[] getValues() {
        return values;
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals(Object o) {
        if (!(o instanceof TnRelationKey)) {
            return false;
        }
        Object[] otherValues = ((TnRelationKey) o).values;
        if (values.length != otherValues.length) {
            return false;
        }
        for (int i = 0; i < values.length; ++i) {
            if (!values[i].equals(otherValues[i])) {
                return false;
            }
        }
        return true;
    }
}
