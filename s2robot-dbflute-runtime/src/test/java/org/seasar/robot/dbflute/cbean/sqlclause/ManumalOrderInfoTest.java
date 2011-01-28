package org.seasar.robot.dbflute.cbean.sqlclause;

import java.util.ArrayList;
import java.util.List;

import org.seasar.robot.dbflute.cbean.sqlclause.orderby.OrderByClause.ManumalOrderInfo;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * 
 * @author jflute
 * @since 0.9.4 (2009/03/18 Wednesday)
 */
public class ManumalOrderInfoTest extends PlainTestCase {

    public void test_ManumalOrderInfo() {
        // ## Arrange ##
        ManumalOrderInfo manumalOrderInfo = new ManumalOrderInfo();
        List<Object> manualValueList = new ArrayList<Object>();
        manualValueList.add("ABC");
        manualValueList.add(null);
        manualValueList.add("DEF");
        manualValueList.add("GHI");

        // ## Act ##
        manumalOrderInfo.setManualValueList(manualValueList);
        List<? extends Object> actualList = manumalOrderInfo.getManualValueList();

        // ## Assert ##
        log(actualList);
        assertEquals(3, actualList.size());
        assertEquals("ABC", actualList.get(0));
        assertEquals("DEF", actualList.get(1));
        assertEquals("GHI", actualList.get(2));
    }
}
