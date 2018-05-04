package org.dockersim;

/**
 * @Author personajian
 * @Date 2018/5/1 0001 15:45
 */
public class UtilAlgo {

    /**
     * 计算任务资源需求向量与VM剩余资源向量之间的相似性（KL-散度，相似熵）
     * KLD值越大，互补性越高
     * KLD值越小，相似性越高
     * @Param
     * @Return
     */
    public  static double KLD(Task task, VmType vmType,Vm vm, int st) {

        // vm剩余资源向量
        double vmFreeCpu;
        double vmFreeRam;
        double vmFreeSum;
        double vmRamDiv;
        double vmCpuDiv;

        if(vmType != null){
            // vm剩余资源向量
            vmFreeCpu = vmType.getVcpu();
            vmFreeRam = vmType.getRam();
        }else{
            // vm剩余资源向量
            vmFreeCpu = vm.getFreeVcpu(st);
            vmFreeRam = vm.getFreeRam(st);
        }

        vmFreeSum = vmFreeCpu + vmFreeRam;
        vmRamDiv = vmFreeRam / vmFreeSum;
        vmCpuDiv = vmFreeCpu / vmFreeSum;


        // 任务需求资源向量
        double taskReqCpu = task.getVcpu();
        double taskReqRam = task.getRam();
        double taskReqSum = taskReqCpu + taskReqRam;
        double taskCpuDiv = taskReqCpu / taskReqSum;
        double taskRamDiv = taskReqRam / taskReqSum;

        double KLD = vmCpuDiv * (Math.log(vmCpuDiv / taskCpuDiv)) +
                vmRamDiv * (Math.log(vmRamDiv / taskRamDiv));

        return KLD;
    }
    /**
     * 计算任务资源需求向量与VM剩余资源向量之间的相似性（KL-散度，相似熵）
     * KLD值越大，互补性越高
     * KLD值越小，相似性越高
     * @Param
     * @Return
     */
    public  static double RVD(Task task, VmType vmType,Vm vm, int st) {

        // vm剩余资源向量
        double vmFreeCpu;
        double vmFreeRam;
        double vmFreeSum;
        double vmRamDiv;
        double vmCpuDiv;

        if(vmType != null){
            // vm剩余资源向量
            vmFreeCpu = vmType.getVcpu();
            vmFreeRam = vmType.getRam();
        }else{
            // vm剩余资源向量
            vmFreeCpu = vm.getFreeVcpu(st);
            vmFreeRam = vm.getFreeRam(st);
        }

        vmFreeSum = vmFreeCpu + vmFreeRam;
        vmRamDiv = vmFreeRam / vmFreeSum;
        vmCpuDiv = vmFreeCpu / vmFreeSum;


        // 任务需求资源向量
        double taskReqCpu = task.getVcpu();
        double taskReqRam = task.getRam();
        double taskReqSum = taskReqCpu + taskReqRam;
        double taskCpuDiv = taskReqCpu / taskReqSum;
        double taskRamDiv = taskReqRam / taskReqSum;

        double RVD = Math.pow(vmCpuDiv-taskCpuDiv,2) + Math.pow(vmRamDiv-taskRamDiv,2);

        return RVD;
    }

    /**
     * 计算VM在安置完任务对应的容器后的负载均衡率
     * LBR值越小，越均衡
     * @Param
     * @Return
     */
    public static double LBR(Task task, VmType vmType,Vm vm, int st) {

        // vm剩余资源向量
        double vmFreeCpu;
        double vmFreeRam;
        double vmFreeSum;

        // vm配置资源向量
        double vmConfigCpu;
        double vmConfigRam;
        double vmConfigSum;
        // vm配置资源向量 各资源分量的负载比例
        double vmConfigCpuDiv;
        double vmConfigRamDiv;

        if(vmType != null){// 虚拟机类型
            // vm剩余资源向量
            vmFreeCpu = vmType.getVcpu();
            vmFreeRam = vmType.getRam();

            vmConfigCpu = vmType.getVcpu();
            vmConfigRam = vmType.getRam();
        }else{// 虚拟机实例
            // vm剩余资源向量
            vmFreeCpu = vm.getFreeVcpu(st);
            vmFreeRam = vm.getFreeRam(st);
            // vm配置资源向量
            vmConfigCpu = vm.getVmType().getVcpu();
            vmConfigRam = vm.getVmType().getRam();
        }

        vmConfigSum = vmConfigCpu + vmConfigRam;
        // vm配置资源向量 各资源分量的负载比例
        vmConfigCpuDiv = vmConfigCpu/vmConfigSum;
        vmConfigRamDiv = vmConfigRam/vmConfigSum;


        // 任务需求资源向量
        double taskReqCpu = task.getVcpu();
        double taskReqRam = task.getRam();
        double taskReqSum = taskReqCpu + taskReqRam;

        // vm剩余资源向量 任务部署后
        vmFreeCpu -= taskReqCpu;
        vmFreeRam -= taskReqRam;
        vmFreeSum = vmFreeCpu + vmFreeRam;
        double vmFreeCpuDiv = vmFreeCpu/vmFreeSum;
        double vmFreeRamDiv = vmFreeRam/vmFreeSum;

        // 部署容器后，vm的资源负载均衡率
        double LBR = Math.pow((Math.pow(vmFreeCpuDiv - vmConfigCpuDiv,2) +
                Math.pow(vmFreeRamDiv - vmConfigRamDiv,2)),1/2);

        return LBR;
    }

    // 比较vm剩余资源向量 与 task需求资源向量 之间大小
    public static boolean enoughRes(VmType vmType, Task task){
        if(vmType.getVcpu() >= task.getVcpu()
                && vmType.getRam()>= task.getRam()){
            return true;
        }else
            return false;
    }

    // 比较vm配置资源向量 与 task需求资源向量 之间大小
    public static boolean enoughRes(Vm vm, Task task,int time){
        if(vm.getFreeVcpu(time) >= task.getVcpu()
                && vm.getFreeRam(time)>= task.getRam()){
            return true;
        }else
            return false;
    }

    public static boolean nullRes(Task task){
        if(task.getVcpu() + task.getRam() < 0.000001){
            return true;
        }else
            return false;
    }

}
