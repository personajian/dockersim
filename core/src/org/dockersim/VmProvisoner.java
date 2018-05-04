package org.dockersim;


import java.util.ArrayList;
import java.util.List;

/**
 * 虚拟机管理器
 *
 * @Author personajian
 * @Date 2018/4/24 0024 10:51
 * 负责VM实例的创建和销毁
 */
public class VmProvisoner {

    private List<Vm> vmList = new ArrayList<>();

    private List<VmType> vmTypes = new ArrayList<>();

    public VmProvisoner() {
    }

    public VmProvisoner(List<Vm> vmList) {
        this.vmList = vmList;
    }

    public VmProvisoner(List<Vm> vmList, List<VmType> vmTypeList) {
        this.vmList = vmList;
        this.vmTypes = vmTypeList;
    }

    public List<Vm> getVmList() {
        return vmList;
    }

    public void setVmList(List<Vm> vmList) {
        this.vmList = vmList;
    }

    public Vm leaseNewVm(Task task){
        // 启发式规则选择合适虚拟机类型进行租赁
        VmType vmType = leaseVmType(task);
        Vm newVm = new Vm(vmType,vmList.size(),new ArrayList<>());
        vmList.add(newVm);
        return newVm;
    }

    private VmType leaseVmType (Task task){
        double min = Double.MAX_VALUE;
        double val = Double.MAX_VALUE;
        VmType leaseVmType=  new VmType();
        for (VmType vmType : vmTypes) {
            if(UtilAlgo.enoughRes(vmType,task)){
                switch (Parameters.leaseVmTypeRule) {
                    case KLD:
                        val = UtilAlgo.KLD(task,vmType,null,0 );
                        break;
                    case RVD:
                        val = UtilAlgo.RVD(task,vmType,null,0 );
                        break;
                    case LBR:
                        val = UtilAlgo.LBR(task,vmType,null,0 );
                        break;
                    default:
                        break;
                }
                if (min > val) {
                    min = val;
                    leaseVmType = vmType;
                }
            }
        }
        return leaseVmType;
    }
    /**
     * 测试，默认租赁，满足资源需求的虚拟机
     * @Param
     * @Return
     */
    private VmType leaseVmTypeByDefault(Task task) {
        VmType leaseVmtype = new VmType();
        for(VmType vmType:vmTypes){
            if(task.getVcpu() < vmType.getVcpu() && task.getRam() < vmType.getRam()){
                leaseVmtype = vmType;
                break;
            }
        }
        return leaseVmtype;
    }
}
