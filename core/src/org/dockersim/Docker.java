package org.dockersim;

/**
 * Docker容器实体类
 * @Author personajian
 * @Date 2018/4/24 0024 10:43
 */
public class Docker {

    private int dockerId;

    private Task task;

    private int vcpu;

    private int ram;

    private int startTime;

    private int endTime;

    public Docker(Task task) {
        this.task = task;
    }

    public int getDockerId() {
        return dockerId;
    }

    public void setDockerId(int dockerId) {
        this.dockerId = dockerId;
    }

    public int getVcpu() {
        return vcpu;
    }

    public void setVcpu(int vcpu) {
        this.vcpu = vcpu;
    }

    public int getRam() {
        return ram;
    }

    public void setRam(int ram) {
        this.ram = ram;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
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
