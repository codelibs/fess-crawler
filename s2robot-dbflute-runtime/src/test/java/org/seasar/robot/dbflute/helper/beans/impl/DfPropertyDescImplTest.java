package org.seasar.robot.dbflute.helper.beans.impl;

import org.seasar.robot.dbflute.helper.beans.DfBeanDesc;
import org.seasar.robot.dbflute.helper.beans.DfPropertyDesc;
import org.seasar.robot.dbflute.helper.beans.exception.DfBeanIllegalPropertyException;
import org.seasar.robot.dbflute.helper.beans.factory.DfBeanDescFactory;
import org.seasar.robot.dbflute.unit.PlainTestCase;

/**
 * @author jflute
 */
public class DfPropertyDescImplTest extends PlainTestCase {

    public void test_getValue_illegalProperty() throws Exception {
        // ## Arrange ##
        DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(MockBean.class);
        DfPropertyDesc pd = beanDesc.getPropertyDesc("writeOnlyName");
        MockBean bean = new MockBean();
        bean.setWriteOnlyName("foo");

        // ## Act ##
        try {
            pd.getValue(bean);

            // ## Assert ##
            fail();
        } catch (DfBeanIllegalPropertyException e) {
            // OK
            log(e.getMessage());
            log(e.getCause().getMessage());
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }

    public void test_setValue_illegalProperty() throws Exception {
        // ## Arrange ##
        DfBeanDesc beanDesc = DfBeanDescFactory.getBeanDesc(MockBean.class);
        DfPropertyDesc pd = beanDesc.getPropertyDesc("readOnlyName");
        MockBean bean = new MockBean();

        // ## Act ##
        try {
            pd.setValue(bean, "foo");

            // ## Assert ##
            fail();
        } catch (DfBeanIllegalPropertyException e) {
            // OK
            log(e.getMessage());
            log(e.getCause().getMessage());
            assertEquals(IllegalStateException.class, e.getCause().getClass());
        }
    }

    protected static class MockBean {
        protected String _readOnlyName;
        protected String _writeOnlyName;

        public String getReadOnlyName() {
            return _readOnlyName;
        }

        public void setWriteOnlyName(String writeOnlyName) {
            this._writeOnlyName = writeOnlyName;
        }
    }
}
