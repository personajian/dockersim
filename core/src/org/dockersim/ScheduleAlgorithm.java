package org.dockersim;

import java.util.List;

/**
 * @Author personajian
 * @Date 2018/5/4 0004 15:26
 */
public abstract class ScheduleAlgorithm {

    private List<MicroFlow> microFlows;

    private List<VmType> vmTypeList;

    private List<Vm> vmList;

    private VmProvisoner vmProvisioner;

    public List<MicroFlow> getMicroFlows() {
        return microFlows;
    }

    public void setMicroFlows(List<MicroFlow> microFlows) {
        this.microFlows = microFlows;
    }

    public List<VmType> getVmTypeList() {
        return vmTypeList;
    }

    public void setVmTypeList(List<VmType> vmTypeList) {
        this.vmTypeList = vmTypeList;
    }

    public List<Vm> getVmList() {
        return vmList;
    }

    public void setVmList(List<Vm> vmList) {
        this.vmList = vmList;
    }

    public VmProvisoner getVmProvisioner() {
        return vmProvisioner;
    }

    public void setVmProvisioner(VmProvisoner vmProvisioner) {
        this.vmProvisioner = vmProvisioner;
    }

    public abstract void runSchedule();

    public abstract double getTotalCost();

}
