package com.xyl.spark.schedule.common.xml;

import com.xyl.spark.schedule.common.utils.ReflectUtils;
import com.xyl.spark.schedule.common.xml.annotation.XmlAttribute;
import com.xyl.spark.schedule.common.xml.annotation.XmlMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by XXX on 2017/9/6.
 *
 * XML自动化解析服务<br>
 * @author xiayulang
 */

@Service
public class XmlParseService {

    Log log = LogFactory.getLog(getClass());

    /**
     * xml文件解析
     * @param clazz
     * @param in
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T parseObject(Class<T> clazz, InputStream in) throws Exception {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(in);
        Element rootElement = document.getRootElement();
        return parseObject(clazz, rootElement);
    }


    /**
     * xml文件解析
     * @param clazz
     * @param element
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T parseObject(Class<T> clazz, Element element) throws Exception {
        if(clazz == null){
            throw new IllegalArgumentException("param is null");
        }
        T target = clazz.newInstance();
        Field[] fields = ReflectUtils.getAllFields(clazz); //class的所有属性

        for(Field field: fields){
            if(field.isAnnotationPresent(XmlAttribute.class)){
                XmlAttribute xmlAttribute = field.getAnnotation(XmlAttribute.class);
                String fieldName = xmlAttribute.name();
                if(fieldName == null || fieldName.length() < 1){
                    fieldName = field.getName();
                }
                String value = element.attributeValue(fieldName);

                if(value == null){
                    continue;
                }
                field.setAccessible(true);
                if(field.getType() == String.class){
                    field.set(target, value);
                }else {
                    //预留属性非String类型时的操作
//                    Object attribute =

                }
            }
            else if(field.isAnnotationPresent(XmlMap.class)){
                XmlMap xmlMap = field.getAnnotation(XmlMap.class); //获取XmlMap注解
                String fieldName = xmlMap.name(); // XmlMap注解的name属性值 (sparkstreaming 或者 spark)
                if(fieldName== null || fieldName.length() < 1){
                    fieldName = field.getName();
                }
                Iterator<Element> elements = (Iterator<Element>)element.elementIterator();
                while (elements.hasNext()){
                    Element childElement = elements.next();
                    if(!childElement.getName().equalsIgnoreCase(fieldName)){
                        continue;
                    }
                    Object childTarget = parseObject(xmlMap.valueClass(), childElement);//递归获取xml数据，凡涉及到层级比较多数据解析使用递归
                    String keyvalue = childElement.attributeValue(xmlMap.key());
                    Object key = keyvalue;
                    if(xmlMap.keyClass() != String.class){
                        //预留属性非String类型时的操作  通过Conver 也即是common-beanutils工具类
                        //key = MyConverUtils.convert(keyValue, xmlChildren.keyClass());
                    }
                    field.setAccessible(true);
                    Map<Object, Object> map = (Map<Object, Object>)field.get(target);
                    if(map == null){
                        map = (Map<Object, Object>) xmlMap.mapClass().newInstance();
                        field.set(target, map);
                    }
                    if(!xmlMap.reduplicatable()){
                        if(map.containsKey(key)){
                            throw new RuntimeException("Reduplicate key define! key=" + key + ",value=" + childTarget);
                        }
                        map.put(key, childTarget);
                        this.checkAndSetParent(target, childTarget);  //将递归取得的 taskconf或者batchtaskconf设置进返回的对象
                    }
                }
            }
        }

        return target;
    }


    /**
     * 检查并且设置孩子节点的父对象
     *
     * @param parent
     * @param child
     */
    private void checkAndSetParent(Object parent, Object child){
        if(child instanceof Parentable ){
            Parentable parentable = (Parentable)child;
            parentable.setParent(parent);
        }
    }

}
