package org.seasar.robot.dbflute.dbmeta.name;

import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class TableSqlNameTest extends PlainTestCase {

    public void test_basic() throws Exception {
        TableSqlName tableSqlName = new TableSqlName("FOO_SQL", "BAR_DB");
        assertEquals("FOO_SQL", tableSqlName.toString());
        assertEquals("BAR_DB", tableSqlName.getCorrespondingDbName());
        assertFalse(tableSqlName._locked);
        tableSqlName.xacceptFilter(new SqlNameFilter() {
            public String filter(String sqlName, String correspondingDbName) {
                assertEquals("FOO_SQL", sqlName);
                assertEquals("BAR_DB", correspondingDbName);
                return "filtered." + sqlName;
            }
        });
        assertEquals("filtered.FOO_SQL", tableSqlName.toString());
        assertTrue(tableSqlName._locked);
        try {
            tableSqlName.xacceptFilter(null);

            fail();
        } catch (IllegalStateException e) {
            // OK
            log(e.getMessage());
        }
    }
}
