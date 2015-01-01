package org.codelibs.robot.db.allcommon;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.codelibs.robot.db.exbhv.AccessResultBhv;
import org.codelibs.robot.db.exbhv.AccessResultDataBhv;
import org.codelibs.robot.db.exbhv.UrlFilterBhv;
import org.codelibs.robot.db.exbhv.UrlQueueBhv;
import org.dbflute.bhv.core.BehaviorCommandInvoker;
import org.dbflute.bhv.core.InvokerAssistant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * The Java configuration of DBFlute beans for Spring Framework. <br>
 * You can inject them by importing this class in your auto configuration class.
 * @author DBFlute(AutoGenerator)
 */
@Configuration
public class DBFluteBeansJavaConfig {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    @Autowired
    protected ApplicationContext _container;

    @Resource(name = "dataSource")
    protected DataSource _dataSource; // name basis here for multiple DB

    // ===================================================================================
    //                                                                   Runtime Component
    //                                                                   =================
    @Bean(name = "introduction")
    public DBFluteInitializer createDBFluteInitializer() { // no lazy for initialize-only component
        return new org.codelibs.robot.db.allcommon.DBFluteInitializer(
                _dataSource);
    }

    @Lazy
    @Bean(name = "invokerAssistant")
    public InvokerAssistant createImplementedInvokerAssistant() {
        final ImplementedInvokerAssistant assistant = newImplementedInvokerAssistant();
        assistant.setDataSource(_dataSource);
        return assistant;
    }

    protected ImplementedInvokerAssistant newImplementedInvokerAssistant() {
        return new org.codelibs.robot.db.allcommon.ImplementedInvokerAssistant();
    }

    @Lazy
    @Bean(name = "behaviorCommandInvoker")
    public BehaviorCommandInvoker createBehaviorCommandInvoker() {
        final BehaviorCommandInvoker invoker = newBehaviorCommandInvoker();
        invoker.setInvokerAssistant(createImplementedInvokerAssistant());
        return invoker;
    }

    protected BehaviorCommandInvoker newBehaviorCommandInvoker() {
        return new BehaviorCommandInvoker();
    }

    @Lazy
    @Bean(name = "behaviorSelector")
    public ImplementedBehaviorSelector createImplementedBehaviorSelector() {
        final ImplementedBehaviorSelector selector = newImplementedBehaviorSelector();
        selector.setContainer(_container);
        return selector;
    }

    protected ImplementedBehaviorSelector newImplementedBehaviorSelector() {
        return new ImplementedBehaviorSelector();
    }

    @Lazy
    @Bean(name = "commonColumnAutoSetupper")
    public ImplementedCommonColumnAutoSetupper createImplementedCommonColumnAutoSetupper() {
        return newImplementedCommonColumnAutoSetupper();
    }

    protected ImplementedCommonColumnAutoSetupper newImplementedCommonColumnAutoSetupper() {
        return new ImplementedCommonColumnAutoSetupper();
    }

    // ===================================================================================
    //                                                                            Behavior
    //                                                                            ========
    @Lazy
    @Bean(name = "accessResultBhv")
    public AccessResultBhv createAccessResultBhv() {
        final AccessResultBhv bhv = newAccessResultBhv();
        bhv.setBehaviorCommandInvoker(createBehaviorCommandInvoker());
        bhv.setBehaviorSelector(createImplementedBehaviorSelector());
        bhv.setCommonColumnAutoSetupper(createImplementedCommonColumnAutoSetupper());
        return bhv;
    }

    protected AccessResultBhv newAccessResultBhv() {
        return new AccessResultBhv();
    }

    @Lazy
    @Bean(name = "accessResultDataBhv")
    public AccessResultDataBhv createAccessResultDataBhv() {
        final AccessResultDataBhv bhv = newAccessResultDataBhv();
        bhv.setBehaviorCommandInvoker(createBehaviorCommandInvoker());
        bhv.setBehaviorSelector(createImplementedBehaviorSelector());
        bhv.setCommonColumnAutoSetupper(createImplementedCommonColumnAutoSetupper());
        return bhv;
    }

    protected AccessResultDataBhv newAccessResultDataBhv() {
        return new AccessResultDataBhv();
    }

    @Lazy
    @Bean(name = "urlFilterBhv")
    public UrlFilterBhv createUrlFilterBhv() {
        final UrlFilterBhv bhv = newUrlFilterBhv();
        bhv.setBehaviorCommandInvoker(createBehaviorCommandInvoker());
        bhv.setBehaviorSelector(createImplementedBehaviorSelector());
        bhv.setCommonColumnAutoSetupper(createImplementedCommonColumnAutoSetupper());
        return bhv;
    }

    protected UrlFilterBhv newUrlFilterBhv() {
        return new UrlFilterBhv();
    }

    @Lazy
    @Bean(name = "urlQueueBhv")
    public UrlQueueBhv createUrlQueueBhv() {
        final UrlQueueBhv bhv = newUrlQueueBhv();
        bhv.setBehaviorCommandInvoker(createBehaviorCommandInvoker());
        bhv.setBehaviorSelector(createImplementedBehaviorSelector());
        bhv.setCommonColumnAutoSetupper(createImplementedCommonColumnAutoSetupper());
        return bhv;
    }

    protected UrlQueueBhv newUrlQueueBhv() {
        return new UrlQueueBhv();
    }
}
