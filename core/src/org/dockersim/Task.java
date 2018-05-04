package org.dockersim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @Author personajian
 * @Date 2018/4/25 0025 19:57
 */
public class Task {

    private int mfId;

    public int getMfId() {
        return mfId;
    }

    public void setMfId(int mfId) {
        this.mfId = mfId;
    }

    private List<Task> parentList;


    private List<Task> childList;


    private List<FileItem> fileList;

    private boolean isDummyTask = false;

    public boolean isDummyTask() {
        return isDummyTask;
    }

    public void setDummyTask(boolean dummyTask) {
        isDummyTask = dummyTask;
    }

    private int taskId;

    /*任务长度：parser解析出来的
    runtime = 1000 * Double.parseDouble(nodeTime);
    length = (long) runtime;
    length *= Parameters.getRuntimeScale();*/
    private long taskLength;

    // todo runTime是任务实际的执行时长，后续代码完善时，与taskLength二选一！！！
    private int runTime;

    private String type;

    private int userId;

    private int depth;

    //add by cj
    private int startTime;
    private int finishTime;


    private int est = -1;// 最早开始时间
    private int eft = -1;// 最造完成时间
    private int lst = -1;// 最晚开始时间
    private int lft = -1;// 最晚完成时间
    private int rank = -1;
    private int subDeadline;
    private double vcpu;
    private double ram;
    // 资源向量
    private List<Integer> vector;

    public List<Integer> getVector() {
        return vector;
    }

    public void setVector(List<Integer> vector) {
        this.vector = vector;
    }


    public Task(){}

    public Task(
            final int taskId,
            final long taskLength) {

        this.taskId = taskId;
        this.taskLength = taskLength;

        this.childList = new ArrayList<>();
        this.parentList = new ArrayList<>();
        this.fileList = new ArrayList<>();
        this.startTime = -1;
        this.finishTime = -1;
    }

    public Task(boolean isDummyTask, int taskId, long taskLength,String type) {
        this.isDummyTask = isDummyTask;
        this.taskId = taskId;
        this.taskLength = taskLength;
        this.type = type;

        this.childList = new ArrayList<>();
        this.parentList = new ArrayList<>();
        this.fileList = new ArrayList<>();
        this.startTime = -1;
        this.finishTime = -1;

    }

    private List<String> requiredFiles = null;   // list of required filenames


    public boolean addRequiredFile(final String fileName) {
        // if the list is empty
        if (getRequiredFiles() == null) {
            setRequiredFiles(new LinkedList<String>());
        }

        // then check whether filename already exists or not
        boolean result = false;
        for (int i = 0; i < getRequiredFiles().size(); i++) {
            final String temp = getRequiredFiles().get(i);
            if (temp.equals(fileName)) {
                result = true;
                break;
            }
        }

        if (!result) {
            getRequiredFiles().add(fileName);
        }

        return result;
    }

    public void addChild(Task task) {
        this.childList.add(task);
    }

    public void addParent(Task task) {
        this.parentList.add(task);
    }

    public List<Task> getParentList() {
        return parentList;
    }

    public void setParentList(List<Task> parentList) {
        this.parentList = parentList;
    }

    public List<Task> getChildList() {
        return childList;
    }

    public void setChildList(List<Task> childList) {
        this.childList = childList;
    }

    public List<FileItem> getFileList() {
        return fileList;
    }

    public void setFileList(List<FileItem> fileList) {
        this.fileList = fileList;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public long getTaskLength() {
        return taskLength;
    }

    public void setTaskLength(long taskLength) {
        this.taskLength = taskLength;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public int getEst() {
        return est;
    }

    public void setEst(int est) {
        this.est = est;
    }

    public int getEft() {
        return eft;
    }

    public void setEft(int eft) {
        this.eft = eft;
    }

    public int getLst() {
        return lst;
    }

    public void setLst(int lst) {
        this.lst = lst;
    }

    public int getLft() {
        return lft;
    }

    public void setLft(int lft) {
        this.lft = lft;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getSubDeadline() {
        return subDeadline;
    }

    public void setSubDeadline(int subDeadline) {
        this.subDeadline = subDeadline;
    }

    public double getVcpu() {
        return vcpu;
    }

    public void setVcpu(double vcpu) {
        this.vcpu = vcpu;
    }

    public double getRam() {
        return ram;
    }

    public void setRam(double ram) {
        this.ram = ram;
    }

    public List<String> getRequiredFiles() {
        return requiredFiles;
    }

    public void setRequiredFiles(List<String> requiredFiles) {
        this.requiredFiles = requiredFiles;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "mfId=" + mfId +
                ", taskId=" + taskId +
                //", taskLength=" + taskLength +
                ", type='" + type + '\'' +
                ", vcpu=" + vcpu +
                ", ram=" + ram +
                ", st=" + startTime +
                ", ft=" + finishTime +
                ", est=" + est +
                ", eft=" + eft +
                ", lst=" + lst +
                ", lft=" + lft +
                ", rt=" + runTime +
                ", rank=" + rank +
                ", sbl=" + subDeadline +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId &&
                taskLength == task.taskLength &&
                userId == task.userId &&
                depth == task.depth &&
                Objects.equals(parentList, task.parentList) &&
                Objects.equals(childList, task.childList) &&
                Objects.equals(fileList, task.fileList) &&
                Objects.equals(type, task.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(taskId,type);
    }

    // 更新后继任务的最早开始时间和完成时间
    public void updateAllChildEstEft() {
        for(Task task: getChildList()){
            task.setEst(Math.max(task.getEst(),finishTime));
            task.setEft(task.getEst()+task.getRunTime());
        }

    }
}
