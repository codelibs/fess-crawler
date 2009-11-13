package org.seasar.robot.dbflute.mock;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;

public class MockDBMetaProvider implements DBMetaProvider {

    public DBMeta provideDBMeta(String tableFlexibleName) {
        return new MockDBMeta();
    }

    public DBMeta provideDBMetaChecked(String tableFlexibleName) {
        return new MockDBMeta();
    }
}
