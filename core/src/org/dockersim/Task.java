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

    private int taskId;

    private List<Task> parentList;

    private List<Task> childList;


    private List<FileItem> fileList;

    /*任务长度：parser解析出来的
    runtime = 1000 * Double.parseDouble(nodeTime);
    length = (long) runtime;
    length *= Parameters.getRuntimeScale();*/
    private double taskLength;

    // todo runTime是任务实际的执行时长，后续代码完善时，与taskLength二选一！！！
    private double runTime;

    private String type;

    private int userId;

    private int depth;

    private ResourceVector resourceVector;


    public int getMfId() {
        return mfId;
    }

    public void setMfId(int mfId) {
        this.mfId = mfId;
    }


    public ResourceVector getResourceVector() {
        return resourceVector;
    }

    public void setResourceVector(ResourceVector resourceVector) {
        this.resourceVector = resourceVector;
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

    public double getTaskLength() {
        return taskLength;
    }

    public void setTaskLength(double taskLength) {
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

    public List<String> getRequiredFiles() {
        return requiredFiles;
    }

    public void setRequiredFiles(List<String> requiredFiles) {
        this.requiredFiles = requiredFiles;
    }

    public double getRunTime() {
        return runTime;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    @Override
    public String toString() {
        return "Task{" +
                "mfId=" + mfId +
                ", taskId=" + taskId +
                ", runTime=" + runTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return mfId == task.mfId &&
                taskId == task.taskId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(mfId, taskId);
    }
}
