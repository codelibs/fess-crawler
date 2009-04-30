package org.seasar.robot.db.allcommon;

import org.seasar.dbflute.Entity;

/**
 * The interface of entity defined common columns.
 * @author DBFlute(AutoGenerator)
 */
public interface EntityDefinedCommonColumn extends Entity {

    /**
	 * Enable common column auto set up. {for after disable because the default is enabled}
	 */
    public void enableCommonColumnAutoSetup();

    /**
	 * Disable common column auto set up.
	 */
    public void disableCommonColumnAutoSetup();
	
    /**
	 * Can the entity set up common column by auto?
	 * @return Determination.
	 */
	public boolean canCommonColumnAutoSetup();
}
