package com.xyl.spark.schedule.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 通过反射获取对象的属性、函数等
 * Created by XXX on 2017/9/6.
 */
public final class ReflectUtils {

    static Log log = LogFactory.getLog(ReflectUtils.class);

    /**
     * 获取类下面所有属性,包括父类
     * @param clazz
     * @return
     */
    public static Field[] getAllFields(Class<?> clazz){
        return getFields(clazz).toArray(new Field[0]);
    }


    private static List<Field> getFields(Class<?> clazz){
        if(clazz == Object.class){
            return new ArrayList<Field>();
        }
        List<Field> fieldList = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for(Field field:fields){
            fieldList.add(field);
        }
        fieldList.addAll(getFields(clazz.getSuperclass()));
        return fieldList;
    }

}
