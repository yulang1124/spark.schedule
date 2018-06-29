package com.xyl.spark.schedule.common;

import bootstrap.Executor;
import org.springframework.context.access.ContextSingletonBeanFactoryLocator;

/**
 * Created by XXX on 2017/8/31.
 */
public class MainExecutor implements Executor {


    public static final String FACTORY_KEY = "qiaofangApplicationContext";

    @Override
    public void executor() {
        ContextSingletonBeanFactoryLocator.getInstance().useBeanFactory(FACTORY_KEY);
    }

}
