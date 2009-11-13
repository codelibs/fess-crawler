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
        assertTrue(TnRowCreatorExtension.isBeanAssignableFromEntity(BsCustomizeEntity.class, entityType));
        assertTrue(TnRowCreatorExtension.isBeanAssignableFromEntity(ExCustomizeEntity.class, entityType));
        assertFalse(TnRowCreatorExtension.isBeanAssignableFromEntity(ManualCustomizeEntity.class, entityType));
    }
    
    protected static class BsCustomizeEntity {
        
    }
    
    protected static class ExCustomizeEntity extends BsCustomizeEntity {
        
    }
    
    protected static class ManualCustomizeEntity extends ExCustomizeEntity {
        
    }
}
