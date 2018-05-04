package org.dockersim;

import java.util.*;

/**
 * @Author personajian
 * @Date 2018/5/4 0004 15:16
 */
public class ScheduleAlgorithmDefault extends ScheduleAlgorithm {


    public ScheduleAlgorithmDefault() {
    }

    public void runSchedule(){
        // 工作流排序
        sortMicroFlows(getMicroFlows());

        for (MicroFlow mf : getMicroFlows()) {
            processMicroFlow(mf); // 逐个处理微服务工作流
        }
    }

    private void processMicroFlow(MicroFlow mf) {
        int arrivalTime = mf.getArrivalTime();
        int deadline = mf.getDeadline();


        List<Task> taskList = mf.getTaskList(); // 获取微服务工作流中的任务列表
        addDummyTask(taskList, arrivalTime, deadline);
        slackTime(taskList, arrivalTime, deadline); // 任务的松弛时间计算
        divideDeadline(taskList, arrivalTime, deadline); // 任务的子截止期划分
        Queue<Task> taskSequence = taskSequence(taskList); // 任务初始调度顺序
        dockerPlacement(taskSequence, arrivalTime, deadline); //容器安置算法
        removeDummyDocker();//移除添加的虚拟开始、结束任务对应的Docker容器实例

        //scheduleImprove();
    }


    /**
     * 调度解提高
     * todo 寻找预租赁计划中的VM实例列表中可能存在的空闲时间槽，进行重新预租赁，减少空闲时间槽，优化VM租赁计划，减少租赁费用！！
     *
     * @Param
     * @Return
     */
    private void scheduleImprove() {

        Iterator<Vm> it = getVmList().iterator();

        while (it.hasNext()) {
            Vm vm = it.next();
            List<List<Integer>> slots = idleTimeSlots(vm);
            for (List<Integer> slot : slots) {
                // todo 空闲时间槽大于5分钟？ 可以判断idleTimeSlot是否在租赁周期内？
                if (slot.get(1) - slot.get(0) > 5) {
                    //vm = vmProvisoner.leaseNewVm();
                }
            }
        }


    }


    /**
     * 寻找VM实例上的空闲时间块，以优化虚拟机租赁
     * todo 待完善
     *
     * @Param
     * @Return
     */
    private List<List<Integer>> idleTimeSlots(Vm vm) {

        List<List<Integer>> slots = new ArrayList<>();

        List<Docker> dockerList = vm.getDockerList();

        Collections.sort(dockerList, new Comparator<Docker>() {
            // vm上的安置的Docker实例按照开始时间排序，最早开始的排前面
            @Override
            public int compare(Docker o1, Docker o2) {
                return o1.getStartTime() - o2.getStartTime();
            }
        });

        Docker d = dockerList.get(0);

        // 时间槽t1的开始时间和结束时间
        int t1 = d.getEndTime();
        //int t2 = d.getEndTime();
        // dockerList中最晚的Docker实例的结束时间，其实就是VM是releaseTime
        int tMax = Integer.MIN_VALUE;
        for (Docker docker : dockerList) {
            tMax = Math.max(tMax, docker.getEndTime());
        }

        while (t1 <= tMax) {
            // t1时刻的所有运行的Docker容器实例
            List<Docker> occupyDockers1 = vm.getOccupyDockerList(t1);
            // 选取t1时刻的所有运行的Docker容器实例中的最晚的结束时间
            if (occupyDockers1.size() > 0) {
                int ftMax = Integer.MIN_VALUE;
                for (Docker od : occupyDockers1) {
                    ftMax = Math.max(ftMax, od.getEndTime());
                }
                t1 = ftMax;
                continue;
            } else if (occupyDockers1.size() == 0) {
                int t2 = t1 + 1;
                // 找到时间槽的结束时间t2
                while (vm.getOccupyDockerList(t2).size() == 0 && t2 <= tMax) {
                    t2++;
                }

                if (t2 > t1) {
                    List<Integer> slot = new ArrayList<>();
                    slot.add(t1);
                    slot.add(t2);
                    slots.add(slot);
                    t1 = t2;
                }
            }
        }

        return slots;
    }


    /**
     * 工作流实例预处理：添加虚拟开始结点和虚拟结束结点
     *
     * @Param
     * @Return
     */
    private void addDummyTask(List<Task> taskList, int arrivalTime, int deadline) {
        int size = taskList.size();
        Task dummyStartTask = new Task(true,0, 0,"DummyStart");
        Task dummyEndTask = new Task(true,size + 1, 0,"DummyEnd");

        List<Task> parentList = new ArrayList<Task>();
        List<Task> childList = new ArrayList<>();

        parentList.add(dummyStartTask);
        childList.add(dummyEndTask);

        // 先更新偏序关系：链表，双向更新！！
        // 主要从原taskList开始，不含新添加的dummy task，否则形成环形链表

        for (Task task : taskList) {
            if (task.getParentList().isEmpty()) {
                task.setParentList(parentList);
                dummyStartTask.getChildList().add(task);

            }
            if (task.getChildList().isEmpty()) {
                task.setChildList(childList);
                dummyEndTask.getParentList().add(task);
            }
        }

        // 再添加虚拟开始、结束结点到taskList中
        taskList.add(0, dummyStartTask);
        taskList.add(size + 1, dummyEndTask);

    }


    /**
     * 微服务工作流排序
     *
     * @Param
     * @Return
     */
    private void sortMicroFlows(List<MicroFlow> microFlows) {
        Collections.sort(microFlows, new Comparator<MicroFlow>() {
            @Override
            public int compare(MicroFlow o1, MicroFlow o2) {
                return o1.cirticalPathLength() - o2.cirticalPathLength();
            }
        });
    }

    /**
     * 微服务任务松弛时间计算
     *
     * @Param
     * @Return
     */
    private void slackTime(List<Task> taskList, int arrivalTime, int deadline) {
        calculateESTs(taskList,arrivalTime);
        calculateLFTs(taskList,deadline);
    }

    /**
     * 微服务任务虚拟子截止期划分 cp-p
     *
     * @Param
     * @Return
     */
    private void divideDeadline(List<Task> taskList, int arrivalTime, int deadline) {

        //calculateRank(taskList);
        calculateRanks(taskList);
        int at = arrivalTime;

        int dl = deadline;

        int differ = deadline - arrivalTime;

        double rank0 = taskList.get(0).getRank();

        for (Task task : taskList) {
            // cpp划分子截止期
            double subDl = at + differ * ((rank0 - task.getRank()) / rank0);
            task.setSubDeadline((int) Math.ceil(subDl));
        }

    }

    /**
     * 递归计算rank
     * @Param
     * @Return
     */
    private void calculateRanks(List<Task> taskList){
        for(Task task:taskList){
            calculateRank(task);
        }
    }

    private int calculateRank(Task task) {
        if(task.getRank() >-1){
            return task.getRank();
        }else{
            int maxRank = 0;
            for(Task child : task.getChildList()){
                int rank = child.getRunTime() + calculateRank(child);
                maxRank = Math.max(maxRank,rank);
            }
            task.setRank(maxRank);
            return maxRank;
        }
    }

    /**
     * 递归计算est，eft
     * @Param
     * @Return
     */
    private void calculateESTs(List<Task> taskList,int arrivalTime){
        for(Task task:taskList){
            calculateEST(task,arrivalTime);
        }
    }

    private int calculateEST(Task task,int arrivalTime) {
        if(task.getEst() > -1){
            return task.getEst();
        }else{
            int maxEst = arrivalTime;
            for(Task parent : task.getParentList()){
                int est = parent.getRunTime() + calculateEST(parent,arrivalTime);
                maxEst = Math.max(maxEst,est);
            }
            task.setEst(maxEst);
            task.setEft(maxEst + task.getRunTime());
            return maxEst;
        }
    }

    /**
     * 递归计算lft,lst
     * @Param
     * @Return
     */
    private void calculateLFTs(List<Task> taskList, int deadline){
        for(Task task:taskList){
            calculateLFT(task,deadline);
        }
    }

    private int calculateLFT(Task task,int deadline) {
        if(task.getLft() > -1){
            return task.getLft();
        }else{
            int minLFT = deadline;
            for(Task child : task.getChildList()){
                int lft = calculateLFT(child,deadline) - child.getRunTime() ;
                minLFT = Math.min(minLFT,lft);
            }
            task.setLft(minLFT);
            task.setLst(minLFT - task.getRunTime());
            return minLFT;
        }
    }


    /**
     * rank排列因子计算
     *
     * @Param
     * @Return
     */
    private void calculateRank(List<Task> taskList) {

        // 先计算虚拟结束结点的rank=0
        Task endTask = taskList.get(taskList.size() - 1);
        endTask.setRank(0);

        // 已分配、未分配rank的任务集合
        Set<Task> alRankSet = new HashSet<>();
        Set<Task> unRankSet = new HashSet<>();

        // 初始化rank任务集合
        for (Task task : taskList) {
            unRankSet.add(task);
        }
        // 虚拟开始结点已分配est，虚拟结束结点已分配lft
        alRankSet.add(endTask);
        unRankSet.remove(endTask);


        Iterator<Task> itUnRank = unRankSet.iterator();
        // 计算 未分配est集合中所有结点的 est
        while (!unRankSet.isEmpty()) {

            if (!itUnRank.hasNext())
                itUnRank = unRankSet.iterator();

            Task task = itUnRank.next();

            boolean flag = true;
            // 父结点都计算过了est才能计算其est
            for (Task t : task.getChildList()) {
                if (!alRankSet.contains(t)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                int maxRank = Integer.MIN_VALUE;
                for (Task t : task.getChildList())
                    // 最早开始时间：取前驱任务中的（开始时间+执行之间）最大者
                    maxRank = Math.max(maxRank, t.getRank() + t.getRunTime());
                task.setRank(maxRank);
                alRankSet.add(task);
                itUnRank.remove();
            }

        }


    }

    /**
     * 初始任务调度序列
     *
     * @Param
     * @Return
     */
    private Queue taskSequence(List<Task> taskList) {
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getEst() - o2.getEst();
            }
        });

        Queue<Task> taskSequence = new LinkedList<>();

        for (Task task : taskList) {
            taskSequence.offer(task);
        }
        return taskSequence;
    }

    /**
     * 容器实例安置
     *
     * @Param
     * @Return
     */
    private void dockerPlacement(Queue<Task> taskSequence, int arrivalTime, int deadline) {
        //可用资源向量块的开始时间列表
        List<Integer> eligSTs = new ArrayList<>();


        while (!taskSequence.isEmpty()) {
            // 初始调度序列中逐个取出任务
            Task task = taskSequence.poll();
            if(!task.isDummyTask()){
                //可用资源向量块：<vm, startTime>
                Map<Vm, List<Integer>> eligVmStMap = new HashMap<>();
                for (Vm vm : getVmList()) {
                    // eligSTs = searchResourceBlock(task, vm, deadline);
                    eligSTs = searchResourceBlock(task, vm, deadline);
                    //eligSTs = searchResourceBlockSDL(task, vm, deadline);
                    if (eligSTs.size() > 0)
                        eligVmStMap.put(vm, eligSTs);
                }

                if (eligVmStMap.size() != 0) {// 在可用资源向量上选择合适的开始时间
                    // 先选择合适的VM
                    // Vm vm = selectVm(taskEliglibleSTMap, task);
                    // 再选择合适的开始时间
                    //int startTime = selectStartTime(taskEliglibleSTMap.get(vm), vm, task);

                    //Map<String, Object> selecVmSt = selectVmStByKld(eligVmStMap, task);
                    //Map<String, Object> selecVmSt = selectVmStByDefault(eligVmStMap, task);
                    Map<String, Object> selecVmSt = selectVmSt(eligVmStMap, task);

                    int startTime = (Integer) selecVmSt.get("startTime");
                    Vm selectVm = (Vm) selecVmSt.get("selectVm");

                    placeDockerOnVm(task, selectVm, startTime);
                } else { // 租赁新的虚拟机
                    Vm newVm = getVmProvisoner().leaseNewVm(task);
                    int startTime = selectStartTime(task);
                    placeDockerOnVm(task, newVm, startTime);
                }
            }
        }
    }

    /**
     * 在可用资源向量的VM上，选择合适的任务开始时间
     * 根据KLD，选择最大KLD
     *
     * @Param
     * @Return
     */

    private Map<String, Object> selectVmStByDefault(Map<Vm, List<Integer>> eligVmStMap, Task task) {

        Map<String, Object> selecVmSt = new HashMap<>();

        Vm selectVm = new Vm();
        int startTime = -1;
        double kldMax = Double.MIN_VALUE;


        List<Integer> startTimeList = new ArrayList<>();
        // 取具有最大KLD值的vm上的开始时间
        for (Vm vm : eligVmStMap.keySet()) {
            startTimeList = eligVmStMap.get(vm);
            for (int st : startTimeList) {
                selectVm = vm;
                startTime =  st;
                break;
            }
        }

        selecVmSt.put("selectVm", selectVm);
        selecVmSt.put("startTime", startTime);


        return selecVmSt;
    }

    /**
     * 在可用资源向量的VM上，选择合适的任务开始时间
     * 根据KLD，选择最大KLD
     *
     * @Param
     * @Return
     */

    private Map<String, Object> selectVmSt(Map<Vm, List<Integer>> eligVmStMap, Task task) {

        Map<String, Object> selecVmSt = new HashMap<>();

        Vm selectVm = new Vm();
        int startTime = -1;
        double min = Double.MAX_VALUE;
        double cal = Double.MAX_VALUE;

        List<Integer> startTimeList;
        // 取具有最大KLD值的vm上的开始时间
        for (Vm vm : eligVmStMap.keySet()) {
            startTimeList = eligVmStMap.get(vm);
            for (int st : startTimeList) {
                switch (Parameters.selectVmStRule){
                    case KLD:
                        cal = UtilAlgo.KLD(task,null,vm, st);
                        break;
                    case RVD:
                        cal = UtilAlgo.RVD(task,null,vm, st);
                        break;
                    case LBR:
                        cal = UtilAlgo.LBR(task,null,vm, st);
                        break;
                    default:
                        break;
                }

                if (min > cal) {
                    min = cal;
                    startTime = st;
                    selectVm = vm;
                }
            }
        }

        selecVmSt.put("selectVm", selectVm);
        selecVmSt.put("startTime", startTime);

        return selecVmSt;
    }

    /**
     * 新租赁的虚拟机上选择开始时间，默认subdeadline - runTime
     * 在可用资源向量的VM上，选择合适的任务开始时间
     *
     * @Param
     * @Return
     */
    private int selectStartTime(Task task) {
        return task.getSubDeadline() - task.getRunTime();
    }

    private void placeDockerOnVm(Task task, Vm vm, int startTime) {
        task.setStartTime(startTime);
        task.setFinishTime(startTime + task.getRunTime());
        task.updateAllChildEstEft();

        Docker docker = new Docker(task);
        vm.getDockerList().add(docker);
        //Log.printLine("添加"+docker+"到"+vm);
    }

    // 搜索task在VM上的可用资源向量块
    private List<Integer> searchResourceBlockBack(Task task, Vm vm, int deadline) {
        List<Integer> eligSTs = new ArrayList<>();

        // 前提是VM的配置就要满足task资源需求才计算可用资源向量块，否则不计算（节省时间）
        if (vm.getVmType().getVcpu() < task.getVcpu() ||
                vm.getVmType().getRam() < task.getRam()) {
            return null;
        }else{
            // 最早开始时间
            int t1 = task.getEst();
            int subDeadline = task.getSubDeadline();
            int runTime = task.getRunTime();

            int subStartTime = subDeadline - runTime;

            // 松弛的开始时间
            while (t1 <= subStartTime) {
                // t1时间为可用开始时间？
                boolean flag = true;
                // 游标t2
                int t2 = t1;
                while (t2 < t1 + runTime) {
                    // t2时刻满足资源需求么？
                    if (vm.getFreeRam(t2) >= task.getRam() && vm.getFreeVcpu(t2) >= task.getVcpu()) {
                        t2++;
                        continue;
                    } else {
                        flag = false;
                        t1 = t2;
                        break;
                    }
                }
                // t1可用？
                if (flag) {
                    eligSTs.add(t1);
                }
                t1++;
            }
            return eligSTs;
        }
    }

    /**
     * 搜索task在VM实例上的可用资源向量块的任务开始时间
     * @Param
     * @Return
     */
    private List<Integer> searchResourceBlock(Task task, Vm vm, int deadline) {
        List<Integer> eligSTs = new ArrayList<>();

        // 前提是VM的配置就要满足task资源需求才计算可用资源向量块，否则不计算（节省时间）
        if (!UtilAlgo.enoughRes(vm.getVmType(),task)) {
            return eligSTs;
        }else{
            // 最早开始时间
            int t1 = task.getEst();
            int subDeadline = task.getSubDeadline();
            int runTime = task.getRunTime();

            int subStartTime = subDeadline - runTime;

            // 松弛的开始时间
            while (t1 <= subStartTime) {
                // t1时间为可用开始时间？
                boolean flag = true;
                // 游标t2
                int t2 = t1;
                while (t2 < t1 + runTime) {
                    // t2时刻满足资源需求么？
                    if (UtilAlgo.enoughRes(vm,task,t2)) {
                        t2++;
                        continue;
                    } else {
                        flag = false;
                        t1 = t2;
                        break;
                    }
                }
                // t1可用？
                if (flag) {
                    eligSTs.add(t1);
                }
                t1++;
            }
            return eligSTs;
        }
    }
    // 从任务的子截止期sdl->eft方向搜索
    // 搜索task在VM上的可用资源向量块


    // 搜索task在VM上的可用资源向量块
    // 测试，默认subdeadline为任务实际完成时间
    private List<Integer> searchResourceBlockSDL(Task task, Vm vm, int deadline) {
        List<Integer> eligSTs = new ArrayList<>();

        // 前提是VM的配置就要满足task资源需求才计算可用资源向量块，否则不计算（节省时间）
        if (vm.getVmType().getVcpu() < task.getVcpu() ||
                vm.getVmType().getRam() < task.getRam()) {
            return eligSTs;
        }else{
            // 最早开始时间
            int runTime = task.getRunTime();
            int subDeadline = task.getSubDeadline();
            int subStartTime = subDeadline - runTime;

            int t1 = subStartTime;

            boolean flag = true;
            // 游标t2
            int t2 = t1;
            while (t2 < t1 + runTime) {
                // t2时刻满足资源需求么？
                if (vm.getFreeRam(t2) >= task.getRam() && vm.getFreeVcpu(t2) >= task.getVcpu()) {
                    t2++;
                    continue;
                } else {
                    flag = false;
                    t1 = t2;
                    break;
                }
            }
            // t1可用？
            if (flag) {
                eligSTs.add(t1);
            }
            return eligSTs;
        }
    }


    /**
     * 移除虚拟/哑 开始任务和结束任务
     * @Param
     * @Return
     */
    public void removeDummyDocker() {

        for(Vm vm : getVmList()){
            Iterator<Docker> it = vm.getDockerList().iterator();
            while(it.hasNext()){
                Docker d = it.next();
                if(d.getTask().getStartTime() == d.getTask().getFinishTime())
                    it.remove();
            }
        }
    }



    public double getTotalCost() {

        double cost = 0;
        int leaseTime = 0;
        int releaseTime = 0;
        double rentDuration = 0;
        double rentDur = 0;

        for (Vm vm : getVmList()) {
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

        for(Vm vm:getVmList()){
            Log.printLine(vm);
            for(Docker d:vm.getDockerList()){
                Log.printLine(d);
            }
        }
    }


}
