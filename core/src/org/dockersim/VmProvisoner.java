package org.dockersim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 虚拟机管理器
 *
 * @Author personajian
 * @Date 2018/4/24 0024 10:51
 * 负责VM实例的创建和销毁
 */
public class VmProvisoner {

    // 虚拟机个数计数器
    private static int vmCounter = 0;

    private List<VmType> vmTypes;

    private List<Vm> vmLeasingList;

    private List<Vm> vmReleasedList;

    public VmProvisoner(List<VmType> vmTypeList) {
        vmCounter = 0;
        vmTypes = vmTypeList;
        vmLeasingList = new ArrayList<>();
        vmReleasedList = new ArrayList<>();
    }

    public List<VmType> getVmTypes() {
        return vmTypes;
    }

    public void setVmTypes(List<VmType> vmTypes) {
        this.vmTypes = vmTypes;
    }

    public List<Vm> getVmLeasingList() {
        return vmLeasingList;
    }

    public void setVmLeasingList(List<Vm> vmLeasingList) {
        this.vmLeasingList = vmLeasingList;
    }

    public List<Vm> getVmReleasedList() {
        return vmReleasedList;
    }

    public void setVmReleasedList(List<Vm> vmReleasedList) {
        this.vmReleasedList = vmReleasedList;
    }

    public Vm leaseNewVm(Task task, int BTU) {

        ResourceVector taskResource = task.getResourceVector();

        VmType rentVmType = new VmType();
        Double similarityMin = Double.MAX_VALUE;
        Double similarity = Double.MAX_VALUE;

        for (VmType vmType : vmTypes) {
            ResourceVector freeResource = vmType.getResourceVector();
            if (freeResource.isGreatter(task.getResourceVector())) {
                switch (Parameters.leaseVmTypeRule) {
                    case KLD:
                        similarity = UtilAlgo.Similarity1(taskResource, freeResource);
                        break;
                    case RVD:
                        similarity = UtilAlgo.Similarity2(taskResource, freeResource);
                        break;
                    case LBR:
                        similarity = UtilAlgo.Similarity3(taskResource, freeResource);
                        break;
                    default:
                        similarity = UtilAlgo.Similarity1(taskResource, freeResource);
                        break;
                }

                if (similarity < similarityMin) {
                    similarityMin = similarity;
                    rentVmType = vmType;
                }
            }
        }

        Vm rentVm = leaseNewVm(rentVmType, BTU);

        return rentVm;

    }

    private Vm leaseNewVm(VmType rentVmType, int BTU) {
        vmCounter++;
        Vm vm = new Vm(rentVmType, vmCounter);
        vm.setLeaseBTU(BTU);
        vmLeasingList.add(vm);
        Log.printLine("VM instance" + vm.getVmId() + " is lease on " + BTU + "th BTU.");
        return vm;

    }

    public void placeDockerOnVm(Task task, Vm eligibleVm, double subStartTime) {

        Docker docker = new Docker(task);

        docker.setStartTime(subStartTime);
        docker.setEndTime(subStartTime + task.getRunTime());

        eligibleVm.getDockerList().add(docker);

        Log.printLine("A Docker instance " + docker.getDockerId() + " is created for task " + task.getTaskId() +
                " and deployed on VM " + eligibleVm.getVmId() +
                " running from " + docker.getStartTime() + " minutes to " + docker.getEndTime() + " minutes.");

    }

    /**
     * 释放在第l个租赁周期结束时 闲置的Vm实例
     *
     * @Param
     * @Return
     */
    public void releaseIdleVm(int BTU) {

        double time = (BTU + 1) * Parameters.BTU;

        Iterator<Vm> it = vmLeasingList.iterator();

        while (it.hasNext()) {
            Vm vm = it.next();
            if (vm.isIdle(time)) {
                it.remove();
                vm.setReleaseBTU(BTU);
                vmReleasedList.add(vm);
                Log.printLine("VM instance " + vm.getVmId() + " is release on " + BTU + "th BTU.");

            }

        }
    }

    public Vm selectEligibleVm(Task task, List<Vm> eligibleVms, double subStartTime) {

        ResourceVector taskResource = task.getResourceVector();

        Vm eligibleVm = new Vm();
        Double similarityMin = Double.MAX_VALUE;
        Double similarity = Double.MAX_VALUE;


        for (Vm vm : eligibleVms) {
            ResourceVector freeResource = vm.getFreeResource(subStartTime);
            switch (Parameters.selectVmRule) {
                case KLD:
                    similarity = UtilAlgo.Similarity1(taskResource, freeResource);
                    break;
                case RVD:
                    similarity = UtilAlgo.Similarity2(taskResource, freeResource);
                    break;
                case LBR:
                    similarity = UtilAlgo.Similarity3(taskResource, freeResource);
                    break;
                default:
                    similarity = UtilAlgo.Similarity1(taskResource, freeResource);
                    break;
            }

            if (similarity < similarityMin) {
                similarityMin = similarity;
                eligibleVm = vm;
            }

        }
        return eligibleVm;
    }


    public List<Vm> getEligibleVms(Task task, double subStartTime) {

        List<Vm> eligibleVms = new ArrayList<>();

        for (Vm vm : getVmLeasingList()) {
            ResourceVector freeResource = vm.getFreeResource(subStartTime);
            if (freeResource.isGreatter(task.getResourceVector()))
                eligibleVms.add(vm);
        }
        return eligibleVms;
    }

    public double getTotalCost() {

        double cost = 0;

        for (Vm vm : getVmReleasedList()) {

            int numBUT = vm.getReleaseBTU() - vm.getLeaseBTU() + 1;

            cost += numBUT * vm.getVmType().getCost();

        }

        return cost;
    }

    public void scheduleResult() {

        /*for(Vm vm : getVmReleasedList()){
            Log.printLine(vm);
            for(Docker d:vm.getDockerList()){
                Log.printLine(d);
            }
        }*/
    }


}
