package org.seasar.robot.dbflute.s2dao.extension;

import junit.framework.TestCase;

/**
 * @author jflute
 * @since 0.9.5 (2009/05/27 Wednesday)
 */
public class TnRowCreatorExtensionTest extends TestCase {

    public void test_isBeanAssignableFromEntity() {
        // ## Arrange & Act & Assert ##
        Class<?> entityType = ExCustomizeEntity.class;
        assertTrue(TnRowCreatorExtension.isCreatableByDBMeta(BsCustomizeEntity.class, entityType));
        assertTrue(TnRowCreatorExtension.isCreatableByDBMeta(ExCustomizeEntity.class, entityType));
        assertFalse(TnRowCreatorExtension.isCreatableByDBMeta(ManualCustomizeEntity.class, entityType));
    }

    protected static class BsCustomizeEntity {

    }

    protected static class ExCustomizeEntity extends BsCustomizeEntity {

    }

    protected static class ManualCustomizeEntity extends ExCustomizeEntity {

    }
}
