package com.xyl.spark.schedule.common.hadoop.yarn;

import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by XXX on 2017/9/28.
 */
public class ApplicationQuery {

    private List<Application> applicationList;

    public ApplicationQuery(){
        applicationList = new ArrayList<Application>();
    }

    public ApplicationQuery(List<Application> applicationList){
        this.applicationList = applicationList;
    }

    public ApplicationQuery(Application[] applications){
        this.applicationList = Arrays.asList(applications);
    }




    public ApplicationQuery filterByName(String... name){
        Set<String> nameSet = new HashSet<String>(Arrays.asList(name));

        List<Application> applications = new ArrayList<Application>();
        for(Application application : applicationList){
            String jobName = application.getName();
            if(jobName.endsWith("$")){
                jobName = jobName.substring(0, jobName.length()-1);
            }
            if(nameSet.contains(jobName)){
                applications.add(application);
            }
        }
        return new ApplicationQuery(applications);
    }

    public ApplicationQuery filterByStatus(String... state){
        Set<String> stateSet = new HashSet<String>(Arrays.asList(state));
        List<Application> applications = new ArrayList<Application>();
        for(Application application:applicationList){
            if(stateSet.contains(application.getState())){
                applications.add(application);
            }
        }

        return new ApplicationQuery(applications);
    }


    public List<Application> list() {
        return applicationList;
    }

    public void setApplicationList(List<Application> applicationList) {
        this.applicationList = applicationList;
    }


    public Application[] toArray(){
        return  applicationList.toArray(new Application[0]);
    }

}
