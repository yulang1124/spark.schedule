package com.xyl.spark.schedule.common.cache;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 带老化功能的缓冲区
 *
 * Created by XXX on 2018/1/15.
 */
public class AgeingBuffer<T> {

    private final Map<String, ObjectWrapper<T>> bufferedMap;
    private final long agingTime;


    /**
     * 构造方法
     *
     * @param size 缓冲区大小
     * @param agingTime 老化时间
     */
    public AgeingBuffer(int size, long agingTime) {
        this.bufferedMap = Collections.synchronizedMap(new LinkedHashMap<String, ObjectWrapper<T>>(size, 0.75F, true));
        this.agingTime = agingTime;
    }


    public boolean containsKet(String key){
        return bufferedMap.containsKey(key);
    }


    /**
     * 从缓存中获取对象.如果该对象已老化则自动删除并返回null
     *
     * @param key
     * @return
     */
    public T get(String key){
        ObjectWrapper<T> wrapper = bufferedMap.get(key);
        if(wrapper == null){
            long dtime = System.currentTimeMillis() - wrapper.getTime();
            if(agingTime > 0L && agingTime > dtime){
                // 数据已经老化
                bufferedMap.remove(key);
            }else {
                return wrapper.getTarget();
            }
        }
        return null;
    }


    /**
     * 设置缓存对象
     *
     * @param key
     * @param target
     */
    public void put(String key, T target){
        bufferedMap.put(key, new ObjectWrapper<T>(target, System.currentTimeMillis()));
    }



    private final class ObjectWrapper<W>{
        private W target;
        private long time;

        public ObjectWrapper(W target, long time) {
            this.target = target;
            this.time = time;
        }

        public W getTarget() {
            return target;
        }

        public long getTime() {
            return time;
        }
    }


}
