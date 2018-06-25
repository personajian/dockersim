package org.dockersim;

/**
 * Docker容器实体类
 * @Author personajian
 * @Date 2018/4/24 0024 10:43
 */
public class Docker {

    public static int DOCKER_ID_COUNTER = 0;

    private int dockerId;

    private Task task;

    private double startTime;

    private double endTime;

    public Docker(Task task) {
        DOCKER_ID_COUNTER++;
        dockerId = DOCKER_ID_COUNTER;
        this.task = task;
    }

    public int getDockerId() {
        return dockerId;
    }

    public void setDockerId(int dockerId) {
        this.dockerId = dockerId;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @Override
    public String toString() {
        return "Docker{" +
                "dockerId=" + dockerId +
                ", task=" + task +
                '}';
    }
}
