package com.xyl.spark.schedule.common.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by XXX on 2017/9/6.
 *
 *针对xml文件的最小单元配置 递归 到这里就代表文件解析完成结束，例如：
 *
 * ib="common,spark/common,spark/jdbc,spark/kafka" 最小单元
 master="yarn"  最小单元
 deployMode="cluster"  最小单元
 driverMemory="1g"  最小单元
 executorMemory="12g"  最小单元
 executorCores="8"  最小单元
 jar="spark/com.axon.icloud.spark.probe.jiangsu-1.0.jar"  最小单元

 最小单元其实就是具体到最后的key-value
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlAttribute {
    public String name() default "";
}
