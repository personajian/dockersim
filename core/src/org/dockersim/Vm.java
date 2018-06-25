package org.dockersim;

import java.util.ArrayList;
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

    private boolean leased;

    private int leaseBTU;

    private int releaseBTU;

    public boolean isLeased() {
        return leased;
    }

    public void setLeased(boolean leased) {
        this.leased = leased;
    }

    public int getLeaseBTU() {
        return leaseBTU;
    }

    public void setLeaseBTU(int leaseBTU) {
        this.leaseBTU = leaseBTU;
    }

    public int getReleaseBTU() {
        return releaseBTU;
    }

    public void setReleaseBTU(int releaseBTU) {
        this.releaseBTU = releaseBTU;
    }

    private List<Docker> dockerList;

    public Vm(){}

    public Vm(VmType vmType) {
        this.vmType = vmType;
    }

    public Vm(VmType vmType, int vmId) {
        this.vmType = vmType;
        this.vmId = vmId;
        this.dockerList = new ArrayList<>();
    }

    /**
     * 获取t时刻，VM实例上运行（占用资源）所有Docker容器实例
     * @Param
     * @Return
     */
    public List<Docker> getOccupyDockerList(double time) {
        List<Docker> occupyDockerList = new ArrayList<>();

        /*if (time < leaseBTU || time > releaseBTU) {
            Log.printLine("Error in time out vm lease time and release time!");
            return occupyDockerList;
        }*/

        for (Docker d : dockerList) {
            if (d.getStartTime() <= time && d.getEndTime() > time) {
                occupyDockerList.add(d);
            }
        }
        return occupyDockerList;
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

    public boolean isIdle(double time) {
        List<Docker> occupiedDockerList = getOccupyDockerList(time);

        return (occupiedDockerList.isEmpty() || occupiedDockerList.size()==0)?true:false;
    }



    public ResourceVector getOccupyResource(double time) {
        List<Docker> occupiedDockerList = getOccupyDockerList(time);
        ResourceVector occupyResource = new ResourceVector();

        if(occupiedDockerList.size() != 0){
            for (Docker d : occupiedDockerList) {
                occupyResource = ResourceVector.addResource(occupyResource,d.getTask().getResourceVector());
            }
        }
        return occupyResource;
    }


    public ResourceVector getFreeResource(double time) {
        ResourceVector occupyResource = getOccupyResource(time);
        ResourceVector differ = ResourceVector.differResource(getVmType().getResourceVector(),occupyResource);
        return differ;
    }

}
