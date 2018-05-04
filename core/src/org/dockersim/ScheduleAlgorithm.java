package org.dockersim;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author personajian
 * @Date 2018/5/4 0004 15:26
 */
public abstract class ScheduleAlgorithm {

    private List<MicroFlow> microFlows;

    private List<VmType> vmTypeList;

    private List<Vm> vmList;

    private VmProvisoner vmProvisoner;

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

    public VmProvisoner getVmProvisoner() {
        return vmProvisoner;
    }

    public void setVmProvisoner(VmProvisoner vmProvisoner) {
        this.vmProvisoner = vmProvisoner;
    }

    public abstract void runSchedule();

}
