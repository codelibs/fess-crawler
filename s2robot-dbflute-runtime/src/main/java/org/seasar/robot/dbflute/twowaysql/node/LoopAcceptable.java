package org.seasar.robot.dbflute.twowaysql.node;

import org.seasar.robot.dbflute.twowaysql.context.CommandContext;

/**
 * @author jflute
 * @since 0.9.7.0 (2010/05/29 Saturday)
 */
public interface LoopAcceptable {

    /**
     * Accept context with loop information.
     * @param ctx The context of command. (NotNull)
     * @param loopInfo The information of loop. (NotNull)
     */
    void accept(CommandContext ctx, LoopInfo loopInfo);
}
