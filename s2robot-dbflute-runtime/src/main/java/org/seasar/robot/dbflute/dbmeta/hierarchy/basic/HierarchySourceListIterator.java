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
package org.seasar.robot.dbflute.dbmeta.hierarchy.basic;

/**
 * @author jflute
 * @param <SOURCE_ROW> The type of source.
 */
public class HierarchySourceListIterator<SOURCE_ROW> implements org.seasar.robot.dbflute.dbmeta.hierarchy.HierarchySourceIterator {

    protected java.util.List<? extends Object> sourceRowList;

    protected HierarchySourceRowSetupper<SOURCE_ROW> sourceRowSetupper;

    protected java.util.Iterator<SOURCE_ROW> sourceBeanListIterator;

    protected org.seasar.robot.dbflute.dbmeta.hierarchy.HierarchySourceRow currentSourceEntity;

    public HierarchySourceListIterator(java.util.List<SOURCE_ROW> sourceRowList,
            HierarchySourceRowSetupper<SOURCE_ROW> sourceRowSetupper) {
        this.sourceRowList = sourceRowList;
        this.sourceRowSetupper = sourceRowSetupper;
        this.sourceBeanListIterator = sourceRowList.iterator();
    }

    public boolean hasNext() {
        return this.sourceBeanListIterator.hasNext();
    }

    public org.seasar.robot.dbflute.dbmeta.hierarchy.HierarchySourceRow next() {
        this.currentSourceEntity = this.sourceRowSetupper.setup(this.sourceBeanListIterator.next());
        return this.currentSourceEntity;
    }

    public org.seasar.robot.dbflute.dbmeta.hierarchy.HierarchySourceRow current() {
        return this.currentSourceEntity;
    }
}