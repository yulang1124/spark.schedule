package com.xyl.spark.schedule.common.hadoop.yarn;

/**
 * Created by XXX on 2017/9/28.
 */
public class Application implements Comparable<Application> {

    private String id;
    private String user;
    private String name;
    private String queue;
    private String state;
    private String finalStatus;
    private Double progress;
    private String trackingUI;
    private String trackingUrl;
    private String diagnostics;
    private Long clusterID;
    private String applicationType;
    private String applicationTags;
    private Long startTime;
    private Long finishedTime;
    private Long elapsedTime;
    private String amContainerLogs;
    private String amHostHttpAddress;
    private Integer allocatedMB;
    private Integer allocatedVCores;
    private Integer runningContainers;
    private Long memorySeconds;
    private Long vcoreSeconds;
    private Integer preemptedResourceMB;
    private Integer preemptedRedourceVCores;
    private Integer numNonAMContainerPreempted;
    private Integer numAMContainerPreeted;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFinalStatus() {
        return finalStatus;
    }

    public void setFinalStatus(String finalStatus) {
        this.finalStatus = finalStatus;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public String getTrackingUI() {
        return trackingUI;
    }

    public void setTrackingUI(String trackingUI) {
        this.trackingUI = trackingUI;
    }

    public String getTrackingUrl() {
        return trackingUrl;
    }

    public void setTrackingUrl(String trackingUrl) {
        this.trackingUrl = trackingUrl;
    }

    public String getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(String diagnostics) {
        this.diagnostics = diagnostics;
    }

    public Long getClusterID() {
        return clusterID;
    }

    public void setClusterID(Long clusterID) {
        this.clusterID = clusterID;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getApplicationTags() {
        return applicationTags;
    }

    public void setApplicationTags(String applicationTags) {
        this.applicationTags = applicationTags;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(Long finishedTime) {
        this.finishedTime = finishedTime;
    }

    public Long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getAmContainerLogs() {
        return amContainerLogs;
    }

    public void setAmContainerLogs(String amContainerLogs) {
        this.amContainerLogs = amContainerLogs;
    }

    public String getAmHostHttpAddress() {
        return amHostHttpAddress;
    }

    public void setAmHostHttpAddress(String amHostHttpAddress) {
        this.amHostHttpAddress = amHostHttpAddress;
    }

    public Integer getAllocatedMB() {
        return allocatedMB;
    }

    public void setAllocatedMB(Integer allocatedMB) {
        this.allocatedMB = allocatedMB;
    }

    public Integer getAllocatedVCores() {
        return allocatedVCores;
    }

    public void setAllocatedVCores(Integer allocatedVCores) {
        this.allocatedVCores = allocatedVCores;
    }

    public Integer getRunningContainers() {
        return runningContainers;
    }

    public void setRunningContainers(Integer runningContainers) {
        this.runningContainers = runningContainers;
    }

    public Long getMemorySeconds() {
        return memorySeconds;
    }

    public void setMemorySeconds(Long memorySeconds) {
        this.memorySeconds = memorySeconds;
    }

    public Long getVcoreSeconds() {
        return vcoreSeconds;
    }

    public void setVcoreSeconds(Long vcoreSeconds) {
        this.vcoreSeconds = vcoreSeconds;
    }

    public Integer getPreemptedResourceMB() {
        return preemptedResourceMB;
    }

    public void setPreemptedResourceMB(Integer preemptedResourceMB) {
        this.preemptedResourceMB = preemptedResourceMB;
    }

    public Integer getPreemptedRedourceVCores() {
        return preemptedRedourceVCores;
    }

    public void setPreemptedRedourceVCores(Integer preemptedRedourceVCores) {
        this.preemptedRedourceVCores = preemptedRedourceVCores;
    }

    public Integer getNumNonAMContainerPreempted() {
        return numNonAMContainerPreempted;
    }

    public void setNumNonAMContainerPreempted(Integer numNonAMContainerPreempted) {
        this.numNonAMContainerPreempted = numNonAMContainerPreempted;
    }

    public Integer getNumAMContainerPreeted() {
        return numAMContainerPreeted;
    }

    public void setNumAMContainerPreeted(Integer numAMContainerPreeted) {
        this.numAMContainerPreeted = numAMContainerPreeted;
    }

    @Override
    public int compareTo(Application o) {
        return (int)(o.getStartTime() - this.startTime);
    }
}
