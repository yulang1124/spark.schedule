package com.xyl.spark.schedule.common.hadoop.spark;


import com.xyl.spark.schedule.common.xml.annotation.XmlAttribute;

/**
 * Created by XXX on 2017/9/6.
 *
 * SparkStreaming任务的运行参数
 */
public class TaskConfig {

    @XmlAttribute(name = "class")
    private String className;

    @XmlAttribute
    private String lib;

    @XmlAttribute
    private String master = "yarn";

    @XmlAttribute
    private String deployMode = "cluster";

    @XmlAttribute
    private String driverMemory = "1g";

    @XmlAttribute
    private String driverCores = "1";

    @XmlAttribute
    private String executorMemory = "1g";

    @XmlAttribute
    private String executorCores = "4";

    @XmlAttribute
    private String numExecutors = "4";

    @XmlAttribute
    private String jar;


    public void setClassName(String className) {
        this.className = className;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public void setDeployMode(String deployMode) {
        this.deployMode = deployMode;
    }

    public void setDriverMemory(String driverMemory) {
        this.driverMemory = driverMemory;
    }

    public void setExecutorMemory(String executorMemory) {
        this.executorMemory = executorMemory;
    }

    public void setExecutorCores(String executorCores) {
        this.executorCores = executorCores;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public String getClassName() {
        return className;
    }

    public String getLib() {
        return lib;
    }

    public String getMaster() {
        return master;
    }

    public String getDeployMode() {
        return deployMode;
    }

    public String getDriverMemory() {
        return driverMemory;
    }

    public String getExecutorMemory() {
        return executorMemory;
    }

    public String getExecutorCores() {
        return executorCores;
    }

    public String getJar() {
        return jar;
    }

    public String getDriverCores() {
        return driverCores;
    }

    public void setDriverCores(String driverCores) {
        this.driverCores = driverCores;
    }

    public String getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(String numExecutors) {
        this.numExecutors = numExecutors;
    }

    @Override
    public String toString() {

        return className +"\t" + lib +"\t" + master +"\t" + deployMode + "\t" + driverMemory +"\t" + executorMemory +"\t" + executorCores +"\t" + jar;
    }
}
