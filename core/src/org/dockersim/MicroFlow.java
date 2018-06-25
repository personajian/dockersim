package org.dockersim;

import java.util.*;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 15:17
 */
public class MicroFlow {

    private int id;

    private String name;

    private String daxPath;

    private List<Task> taskList;

    private double arrivalTime;

    private double deadline;

    public MicroFlow() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDaxPath() {
        return daxPath;
    }

    public void setDaxPath(String daxPath) {
        this.daxPath = daxPath;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getDeadline() {
        return deadline;
    }

    public void setDeadline(double deadline) {
        this.deadline = deadline;
    }

    public double criticalPathLength(){
        Map<Task, Double> rankMap = new HashMap<>();
        calculateRanks(rankMap);

        double criticalPathLength = Double.MIN_VALUE;

        for(Task task : rankMap.keySet())
            criticalPathLength = Math.max(criticalPathLength, rankMap.get(task) + task.getRunTime());

        return  criticalPathLength;
    }

    private void calculateRanks(Map rank) {
        for(Task task:taskList)
            calculateRank(task,rank);
    }

    private double calculateRank(Task task, Map<Task,Double> estMap) {
        if(estMap.containsKey(task)){
            return estMap.get(task);
        }else{
            double maxEst = 0;
            for(Task parent : task.getParentList()){
                double est = parent.getRunTime() + calculateRank(parent,estMap);
                maxEst = Math.max(maxEst,est);
            }
            //task.setEst(maxEst);
            //task.setEft(maxEst + task.getRunTime());
            estMap.put(task,maxEst);
            return estMap.get(task);
        }
    }
}
