package com.xyl.spark.schedule.common.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by XXX on 2017/9/6.
 *
 * 针对xml文件的所有非最小单元节点     如：spark job 配置的每一条数据：
 *
 *  <sparkstreaming class="com.axon.icloud.spark.common.tag.TagStreamingTask"
 lib="common,spark/common,spark/jdbc,spark/kafka" master="yarn"
 deployMode="cluster" driverMemory="1g" executorMemory="12g"
 executorCores="8" jar="spark/com.axon.icloud.spark.probe.jiangsu-1.0.jar" />

 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlMap {

    public String name();

    public String key(); //每个hashmap的 key值对应的xml配置文件中的某个最小单元key-具有唯一性

    public Class<?> keyClass() default String.class;

    public Class<?> valueClass();

    public Class<?> mapClass() default HashMap.class;

    public boolean reduplicatable() default false;


}
