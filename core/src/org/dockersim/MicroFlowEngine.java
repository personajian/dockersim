package org.dockersim;

import java.util.*;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 10:16
 */
public class MicroFlowEngine {

    private List<VmType> vmTypeList = new ArrayList<>();

    private List<Vm> vmList = new ArrayList<>();

    private VmProvisoner vmProvisoner;

    //private DockerAllocator dockerProvisioner;

    public ScheduleAlgorithm getScheduleAlgorithm1(){
        return scheduleAlgorithm;
    }

    public ScheduleAlgorithm getScheduleAlgorithm(Parameters.ScheduleAlgorithm name) {
        ScheduleAlgorithm scheduleAlgorithm;

        switch (name){
            case SMWS:
                scheduleAlgorithm = new ScheduleAlgorithmSMWS();
                break;
            case FLTM:
                scheduleAlgorithm = new ScheduleAlgorithmFLTM();
                break;
            default:
                scheduleAlgorithm = new ScheduleAlgorithmSMWS();
                break;
        }
        return scheduleAlgorithm;
    }

    private ScheduleAlgorithm scheduleAlgorithm;

    /**
     * 构造函数：vm类型列表；vm实例列表；vm供应器；docker分配器
     *
     * @Param
     * @Return
     */
    public MicroFlowEngine(List<VmType> vmTypeList, VmProvisoner vmProvisoner) {
        this.vmTypeList = vmTypeList;
        this.vmProvisoner = vmProvisoner;
    }

    /**
     * 处理工作流集合
     *
     * @Param 微服务工作流集合
     * @Return
     */
    public void process(List<MicroFlow> microFlows) {
        /*if (Parameters.getScheduleAlgorithm().equals(Parameters.ScheduleAlgorithm.INVALID)) {
            Log.printLine("The schedule algorithm is invaild!");
            return;
        }*/
        scheduleAlgorithm = getScheduleAlgorithm(Parameters.getScheduleAlgorithm());

        scheduleAlgorithm.setMicroFlows(microFlows);
        scheduleAlgorithm.setVmTypeList(vmTypeList);
        scheduleAlgorithm.setVmProvisioner(vmProvisoner);

        scheduleAlgorithm.runSchedule();
    }

    public double getTotalCost() {
        return scheduleAlgorithm.getVmProvisioner().getTotalCost();
    }

    public void scheduleResult() {

        for(Vm vm:vmList){
            Log.printLine(vm);
            for(Docker d:vm.getDockerList()){
                Log.printLine(d);
            }
        }
    }
}
