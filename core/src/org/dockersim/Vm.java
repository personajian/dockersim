package org.dockersim;

import org.dockersim.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 虚拟机实体类
 *
 * @Author personajian
 * @Date 2018/4/24 0024 10:45
 */
public class Vm {

    private VmType vmType;

    private int vmId;

    //private int leaseTime;

    //private int releaseTime;

    private List<Docker> dockerList = new ArrayList<>();

    public Vm() {
    }

    public Vm(VmType vmType) {
        this.vmType = vmType;
    }

    public Vm(VmType vmType, int vmId, List<Docker> dockerList) {
        this.vmType = vmType;
        this.vmId = vmId;
        this.dockerList = dockerList;
    }

    /**
     * 获取t时刻，VM实例上运行（占用资源）所有Docker容器实例
     * @Param
     * @Return
     */
    public List<Docker> getOccupyDockerList(int time) {
        List<Docker> occupyDockerList = new ArrayList<>();

        /*if (time < leaseTime || time > releaseTime) {
            Log.printLine("Error in time out vm lease time and release time!");
            return occupyDockerList;
        }*/

        for (Docker d : dockerList) {
            if (d.getTask().getStartTime() <= time && d.getTask().getFinishTime() > time) {
                occupyDockerList.add(d);
            }
        }
        return occupyDockerList;
    }

    /**
     * 获取t时刻，VM实例上所有运行Docker容器占用的内存（memory，RAM）
     * @Param
     * @Return
     */
    public double getOccupyRam(int time) {

        List<Docker> occupiedDockerList = getOccupyDockerList(time);
        double sumRam = 0;
        for (Docker d : occupiedDockerList) {
            sumRam += d.getTask().getRam();
        }

        return sumRam;
    }

    /**
     * 获取t时刻，VM实例上运行的所有Docker容器实例占用的CPU个数
     * @Param
     * @Return
     */
    public double getOccupyVcpu(int time) {

        List<Docker> occupiedDockerList = getOccupyDockerList(time);
        double sumVcpu = 0;
        for (Docker d : occupiedDockerList) {
            sumVcpu += d.getTask().getVcpu();
        }

        return sumVcpu;
    }

    /**
     * 获取t时刻，VM实例剩余的RAM
     * @Param
     * @Return
     */
    public double getFreeRam(int time) {

        return vmType.ram - getOccupyRam(time);
    }

    /**
     * 获取t时刻，VM实例剩余的vCPU
     * @Param
     * @Return
     */
    public double getFreeVcpu(int time) {

        return vmType.vcpu - getOccupyVcpu(time);
    }

    public VmType getVmType() {
        return vmType;
    }

    public void setVmType(VmType vmType) {
        this.vmType = vmType;
    }

    public int getVmId() {
        return vmId;
    }

    public void setVmId(int vmId) {
        this.vmId = vmId;
    }

    public int getLeaseTime() {

        int minST = Integer.MAX_VALUE;
        int ST = Integer.MAX_VALUE;

        for(Docker docker:dockerList){
            ST = docker.getTask().getStartTime();
            minST = Math.min(minST,ST);
        }

        return minST;
    }

    public int getReleaseTime() {

        int maxFT = Integer.MIN_VALUE;
        int FT = Integer.MIN_VALUE;

        for(Docker docker:dockerList){
            FT = docker.getTask().getFinishTime();
            maxFT = Math.max(maxFT,FT);
        }

        return maxFT;
    }

    public List<Docker> getDockerList() {
        return dockerList;
    }

    public void setDockerList(List<Docker> dockerList) {
        this.dockerList = dockerList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vm vm = (Vm) o;
        return vmId == vm.vmId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(vmId);
    }

    @Override
    public String toString() {
        return "Vm{" +
                "vmId=" + vmId +
                ",vmType=" + vmType +
                '}';
    }
}
