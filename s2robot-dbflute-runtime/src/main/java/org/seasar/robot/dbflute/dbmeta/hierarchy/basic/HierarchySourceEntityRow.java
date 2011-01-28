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
package org.seasar.robot.dbflute.dbmeta.hierarchy.basic;

/**
 * @author jflute
 */
public class HierarchySourceEntityRow implements org.seasar.robot.dbflute.dbmeta.hierarchy.HierarchySourceRow {

    protected Object sourceBean;

    public HierarchySourceEntityRow(Object sourceBean) {
        this.sourceBean = sourceBean;
    }

    public Object extractColumnValue(org.seasar.robot.dbflute.dbmeta.hierarchy.HierarchySourceColumn columnInfo) {
        if (!(columnInfo instanceof HierarchySourceEntityColumn)) {
            String msg = "The column info should be HierarchySourceEntityColumn! but: " + columnInfo;
            throw new IllegalStateException(msg);
        }
        final HierarchySourceEntityColumn sourceEntityColumn = (HierarchySourceEntityColumn) columnInfo;
        return invoke(sourceEntityColumn.findGetter(), sourceBean, new Object[] {});
    }

    private Object invoke(java.lang.reflect.Method method, Object target, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            Throwable t = ex.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            }
            if (t instanceof Error) {
                throw (Error) t;
            }
            String msg = "target=" + target + " method=" + method + "-" + java.util.Arrays.asList(args);
            throw new RuntimeException(msg, ex);
        } catch (IllegalAccessException ex) {
            String msg = "target=" + target + " method=" + method + "-" + java.util.Arrays.asList(args);
            throw new RuntimeException(msg, ex);
        }
    }
}