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

    public ScheduleAlgorithm getScheduleAlgorithm(Parameters.ScheduleAlgorithm name) {
        ScheduleAlgorithm scheduleAlgorithm;

        switch (name){
            case INVALID:
                scheduleAlgorithm = new ScheduleAlgorithmDefault();
                break;
            case DEFAULT:
                scheduleAlgorithm = new ScheduleAlgorithmDefault();
                break;
            case RANDOM:
                scheduleAlgorithm = new ScheduleAlgorithmDefault();
                break;
            case HEFT:
                scheduleAlgorithm = new ScheduleAlgorithmDefault();
                break;
            case DHEFT:
                scheduleAlgorithm = new ScheduleAlgorithmDefault();
                break;
            default:
                scheduleAlgorithm = null;
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
    public MicroFlowEngine(List<VmType> vmTypeList, List<Vm> vmList, VmProvisoner vmProvisoner) {
        this.vmTypeList = vmTypeList;
        this.vmList = vmList;
        this.vmProvisoner = vmProvisoner;
    }

    /**
     * 处理工作流集合
     *
     * @Param 微服务工作流集合
     * @Return
     */
    public void process(List<MicroFlow> microFlows) {
        if (Parameters.getScheduleAlgorithm().equals(Parameters.ScheduleAlgorithm.INVALID)) {
            Log.printLine("The schedule algorithm is invaild!");
            return;
        }
        scheduleAlgorithm = getScheduleAlgorithm(Parameters.getScheduleAlgorithm());

        scheduleAlgorithm.setMicroFlows(microFlows);
        scheduleAlgorithm.setVmTypeList(vmTypeList);
        scheduleAlgorithm.setVmList(vmList);
        scheduleAlgorithm.setVmProvisoner(vmProvisoner);

        scheduleAlgorithm.runSchedule();
    }

    public double getTotalCost() {

        double cost = 0;
        int leaseTime = 0;
        int releaseTime = 0;
        double rentDuration = 0;
        double rentDur = 0;

        for (Vm vm : vmList) {
            if(vm.getDockerList().size()!= 0){ // 统计按需实例上有真实Docker容器运行所产生的租赁费用
                leaseTime = vm.getLeaseTime();
                releaseTime = vm.getReleaseTime();
                rentDuration = releaseTime - leaseTime;
            }

            rentDur = Math.ceil(rentDuration / 60.0);

            // 租赁单位时长为1个小时，60分钟
            cost += rentDur * vm.getVmType().cost;
        }

        return cost;
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
