package org.seasar.robot.dbflute.mock;

import org.seasar.robot.dbflute.dbmeta.DBMeta;
import org.seasar.robot.dbflute.dbmeta.DBMetaProvider;
import org.seasar.robot.dbflute.outsidesql.OutsideSqlContext;

public class MockOutsideSqlContext extends OutsideSqlContext {

    public MockOutsideSqlContext() {
        super(new DBMetaProvider() {
            public DBMeta provideDBMeta(String tableFlexibleName) {
                return null;
            }

            public DBMeta provideDBMetaChecked(String tableFlexibleName) {
                return null;
            }
        }, null);
    }
}
