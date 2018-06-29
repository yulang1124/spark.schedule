package com.xyl.spark.schedule.common.cache;

/**
 * Created by XXX on 2018/1/15.
 */
public class Node<T> {

    private String key;
    private T value;


    public Node(){}

    public Node(String key, T value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
