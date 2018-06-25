package org.dockersim;

import java.util.*;

/**
 * @Author personajian
 * @Date 2018/5/4 0004 15:16
 */
public class ScheduleAlgorithmSMWS extends ScheduleAlgorithm {

    private Map<Task, Double> rank;
    private Map<Task, Double> subdeadline;
    private Map<Task, Double> lengthForward;
    private Map<Task, Double> lengthBackward;
    private Map<Task, Double> lengthAverage;
    private Map<Task, Double> earliestStartTime;
    private Map<Task, Double> earliestFinishTime;
    private Map<Task, Double> latestStartTime;
    private Map<Task, Double> latestFinishTime;

    // 排序数组，为了不改变原有的taskList
    private class TaskRank implements Comparable<TaskRank>{

        public Task task;
        public Double rank;

        public TaskRank(Task task, Double rank) {
            this.task = task;
            this.rank = rank;
        }

        @Override
        public int compareTo(TaskRank o) {
            return o.rank.compareTo(rank);
        }
    }

    public ScheduleAlgorithmSMWS() {
        rank = new HashMap<>();
        subdeadline = new HashMap<>();
        lengthForward = new HashMap<>();
        lengthBackward = new HashMap<>();
        lengthAverage = new HashMap<>();
        earliestStartTime = new HashMap<>();
        earliestFinishTime = new HashMap<>();
        latestStartTime = new HashMap<>();
        latestFinishTime = new HashMap<>();
    }

    public void runSchedule(){
        // 逐个预处理微服务工作流
        for (MicroFlow mf : getMicroFlows()) {
            processMicroFlow(mf);
        }
        // 工作流排序
        sortMicroFlows(getMicroFlows());

        // 生成任务调度序列
        PriorityQueue<Task> tasksQueue = tasksQueue();

        // 总的租赁周期
        int L = rentDuration();

        // 任务序列 按照租赁周期 切分
        List<PriorityQueue<Task>> taskSequencePart = taskSequencePartition(tasksQueue,L);

        // 动态任务调度
        for (int l = 0; l < L; l++) {
            dynamicTaskScheduling(taskSequencePart.get(l),l);
            getVmProvisioner().releaseIdleVm(l);
        }

        // 任务调度完成，释放所有虚拟机
        getVmProvisioner().releaseIdleVm(L);

        double cost = getVmProvisioner().getTotalCost();

        Log.printLine("The total renting cost is:  " + cost);

        getVmProvisioner().scheduleResult();
    }

    /**
     * 动态任务调度
     * 逐个租赁周期调度 租赁周期内的任务
     * @Param
     * @Return
     */
    private void dynamicTaskScheduling(PriorityQueue<Task> tasks, int BTU) {

        for(Task task : tasks){
            double subStartTime = subdeadline.get(task) - task.getRunTime();
            List<Vm> eligibleVms =  getVmProvisioner().getEligibleVms(task,subStartTime);
            Vm eligibleVm;

            if(eligibleVms.size() == 0 || eligibleVms.isEmpty()){
                eligibleVm = getVmProvisioner().leaseNewVm(task,BTU);
            }else{
                eligibleVm = getVmProvisioner().selectEligibleVm(task,eligibleVms,subStartTime);
            }

            getVmProvisioner().placeDockerOnVm(task,eligibleVm,subStartTime);
        }
    }


    /**
     * 根据任务子截止期对应的开始时间，将任务序列划分到L个租赁周期内
     * @Param
     * @Return
     */
    private List<PriorityQueue<Task>> taskSequencePartition(PriorityQueue<Task> tasksQueue, int L) {

        List<PriorityQueue<Task>> taskSequencePart = new ArrayList<>();

        // 初始化切分队列，排列因子为任务子截止期对应的开始时间
        for (int i = 0; i < L; i++) {
            taskSequencePart.add(new PriorityQueue<>(new Comparator<Task>() {
                @Override
                public int compare(Task o1, Task o2) {
                    double differ = (subdeadline.get(o1) - o1.getRunTime())- (subdeadline.get(o2) - o2.getRunTime());
                    if(differ < 0.0)
                        return -1;
                    else if(differ > 0.0)
                        return 1;
                    else
                        return 0;
                }
            }));
        }

        for(Task task : tasksQueue){
            double subStartTime = subdeadline.get(task) - task.getRunTime();
            int duration = (int)(subStartTime/Parameters.BTU);
            if(duration < 0)
                duration =0;

            taskSequencePart.get(duration).add(task);
        }

        return taskSequencePart;
    }

    private int rentDuration() {

        double minArrivalTime = Double.MAX_VALUE;
        double maxDeadline = Double.MIN_VALUE;

        for(MicroFlow mf: getMicroFlows()){
            minArrivalTime = Math.min(minArrivalTime,mf.getArrivalTime());
            maxDeadline = Math.max(maxDeadline,mf.getDeadline());
        }

        return (int)Math.ceil((maxDeadline - minArrivalTime)/Parameters.BTU);
    }

    private PriorityQueue<Task> tasksQueue() {

        // 对每个工作流进行任务调度序列生成
        Map<MicroFlow,PriorityQueue<Task>> taskSequencesMap = new HashMap<>();
        for (MicroFlow mf : getMicroFlows()) {
            taskSequencesMap.put(mf,taskSequence(mf.getTaskList()));
        }

        // 合并到总的任务调度序列
        PriorityQueue<Task> tasksQueue = new PriorityQueue<>(new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return (int)(subdeadline.get(o1) - subdeadline.get(o2));
            }
        });
        for(MicroFlow mf: taskSequencesMap.keySet()){
            PriorityQueue<Task> taskSequence = taskSequencesMap.get(mf);
            Iterator<Task> it = taskSequence.iterator();
            while(it.hasNext()){
                tasksQueue.add(it.next());
                it.remove();
            }
        }
        return tasksQueue;

    }

    private void processMicroFlow(MicroFlow mf) {
        double arrivalTime = mf.getArrivalTime();
        double deadline = mf.getDeadline();
        double criticalPath = mf.criticalPathLength();

        List<Task> taskList = mf.getTaskList();

        calculateEarliestStartTime(taskList,arrivalTime);
        calculateLatestFinishTime(taskList,deadline);
        calculateLengthBackward(taskList);
        calculateLengthForward(taskList);
        calculateLengthAverage(taskList);
        calculateSubdeadline(taskList,arrivalTime,deadline,criticalPath);
    }

    private void calculateLengthAverage(List<Task> taskList) {

        for(Task task : taskList)
            calculateLengthAverage(task);

    }

    private void calculateLengthAverage(Task task) {
        lengthAverage.put(task, (lengthForward.get(task) + lengthBackward.get(task)) / 2);
    }

    private void calculateEarliestStartTime(List<Task> taskList,double arrivalTime){
        for(Task task:taskList){
            calculateEarliestStartTime(task,arrivalTime);
        }
    }

    private double calculateEarliestStartTime(Task task, double arrivalTime) {
        if(earliestStartTime.containsKey(task)){
            return earliestStartTime.get(task);
        }else{
            double maxEst = arrivalTime;
            for(Task parent : task.getParentList()){
                double est = parent.getRunTime() + calculateEarliestStartTime(parent,arrivalTime);
                maxEst = Math.max(maxEst,est);
            }
            earliestStartTime.put(task,maxEst);
            earliestFinishTime.put(task,maxEst + task.getRunTime());
            return earliestStartTime.get(task);
        }
    }

    private void calculateLatestFinishTime(List<Task> taskList, double deadline){
        for(Task task:taskList){
            calculateLatestFinishTime(task,deadline);
        }
    }

    private double calculateLatestFinishTime(Task task, double deadline) {
        if(latestFinishTime.containsKey(task)){
            return latestFinishTime.get(task);
        }else{
            double minLFT = deadline;
            for(Task child : task.getChildList()){
                double lft = calculateLatestFinishTime(child,deadline) - child.getRunTime() ;
                minLFT = Math.min(minLFT,lft);
            }
            latestFinishTime.put(task,minLFT);
            latestStartTime.put(task,minLFT - task.getRunTime());
            return latestFinishTime.get(task);
        }
    }

    private void calculateLengthBackward(List<Task> taskList){
        for(Task task:taskList){
            calculateLengthBackward(task);
        }
    }

    private double calculateLengthBackward(Task task) {
        if(lengthBackward.containsKey(task)){
            return lengthBackward.get(task);
        }else{
            double maxL = task.getRunTime();
            for(Task child : task.getChildList()){
                double L = calculateLengthBackward(child) ;
                maxL = Math.max(maxL,L);
            }
            lengthBackward.put(task,maxL + task.getRunTime());
            return lengthBackward.get(task);
        }
    }

    private void calculateLengthForward(List<Task> taskList){
        for(Task task:taskList){
            calculateLengthForward(task);
        }
    }

    private double calculateLengthForward(Task task) {
        if(lengthForward.containsKey(task)){
            return lengthForward.get(task);
        }else{
            double maxL = task.getRunTime();
            for(Task parent : task.getParentList()){
                double L = calculateLengthForward(parent) ;
                maxL = Math.max(maxL,L);
            }
            lengthForward.put(task,maxL + task.getRunTime());
            return lengthForward.get(task);
        }
    }


    private void calculateSubdeadline(List<Task> taskList, double arrivalTime, double deadline, double criticalPath) {
        switch (Parameters.subdeadlineDivisionRule){
            case PDD:
                calculateSubdeadlineByPDD(taskList,arrivalTime,deadline,criticalPath);
                break;
            case EDD:
                calculateSubdeadlineByEDD(taskList,arrivalTime,deadline,criticalPath);
                break;
            case RCPD:
                calculateSubdeadlineByRCPD(taskList,arrivalTime,deadline,criticalPath);
                break;
            default:
                calculateSubdeadlineByPDD(taskList,arrivalTime,deadline,criticalPath);
                break;
        }
    }


    private void calculateSubdeadlineByPDD(List<Task> taskList, double arrivalTime, double deadline, double criticalPath) {

        for(Task task :  taskList){
            double sdl = arrivalTime +
                    (deadline - arrivalTime) * (lengthAverage.get(task)/criticalPath) ;
            subdeadline.put(task,sdl);
        }
    }

    private void calculateSubdeadlineByEDD(List<Task> taskList, double arrivalTime, double deadline, double criticalPath) {

        List<TaskRank> taskRankList = new ArrayList<>(taskList.size());

        for(Task task : taskList){
            taskRankList.add(new TaskRank(task,lengthAverage.get(task)));
        }

        Collections.sort(taskRankList);

        for(int i = 0; i < taskRankList.size(); i++){
            TaskRank taskRank= taskRankList.get(i);
            double sdl = arrivalTime + taskRank.rank +
                    (deadline - arrivalTime - criticalPath) * ((double)(i+1) /(double)taskRankList.size()) ;
            subdeadline.put(taskRank.task,sdl);
        }
    }


    private void calculateSubdeadlineByRCPD(List<Task> taskList, double arrivalTime, double deadline, double criticalPath) {

        Task dummySinkNode = addDummySinkNode(taskList);

        /*Task lastestTask = taskList.get(0);

        for(Task t : taskList){
            if(earliestFinishTime.get(t) > earliestFinishTime.get(lastestTask))
                lastestTask = t;
        }*/

        assignParents(dummySinkNode);
        //removeDummySinkNode(taskList);
    }

    private void removeDummySinkNode(List<Task> taskList) {

        taskList.remove(taskList.size() - 1);

    }

    private Task addDummySinkNode(List<Task> taskList) {

        Task dummySinkNode = new Task(taskList.size() + 1, 0);

        for(Task t :taskList){
            if(t.getChildList().size() == 0)
                dummySinkNode.getParentList().add(t);
        }

        return dummySinkNode;
        //taskList.add(dummySinkNode);
        //subdeadline.put(dummySinkNode,0.0);
    }

    private void assignParents(Task task){


        //while(task.getParentList().size() != 0){
        while(unassignParents(task) != 0){
            LinkedList<Task> pcp = new LinkedList<>();
            //pcp.addFirst(task);
            Task temp = task;

            //while(temp.getParentList().size() != 0){
            while(unassignParents(temp) != 0){
                Task criticalParent = criticalParent(temp);
                pcp.addFirst(criticalParent);
                temp = criticalParent;
            }

            assignPath(pcp);

            Iterator<Task> it = pcp.descendingIterator();

            while(it.hasNext()){
                Task t = it.next();
                assignParents(t);
            }
            /*for(Task t : pcp){
                assignParents(t);
            }*/
        }
        return;
    }

    private int unassignParents(Task task) {

        int count = 0;

        for(Task t : task.getParentList()){
            if(!subdeadline.containsKey(t))
                count++;
        }

        return count;
    }

    private Task criticalParent(Task temp) {

        Task criticalParent = new Task();

        for(Task t : temp.getParentList()){
            if(!subdeadline.containsKey(t))
                criticalParent = t;
        }

        for(Task t : temp.getParentList()){
            if(earliestFinishTime.get(t) > earliestFinishTime.get(criticalParent) &&
                    !subdeadline.containsKey(t))
                criticalParent = t;
        }

        return criticalParent;
    }

    private void assignPath(LinkedList<Task> pcp) {

        Task firstTask = pcp.getFirst();
        Task lastTask = pcp.getLast();

        double psd = latestFinishTime.get(lastTask) - earliestStartTime.get(firstTask);

        for(Task task : pcp){

            double sdl = (earliestFinishTime.get(task) - earliestStartTime.get(firstTask))/
                    (earliestFinishTime.get(lastTask) - earliestStartTime.get(firstTask)) * psd;

            subdeadline.put(task,sdl);
        }
    }


    /**
     * 微服务工作流排序
     *
     * @Param
     * @Return
     */
    private void sortMicroFlows(List<MicroFlow> microFlows) {

        switch (Parameters.sortMicroFlowsRule){
            case SWS1:
                sortMicroFlowsByCP(microFlows);
                break;
            case SWS2:
                sortMicroFlowsByRE(microFlows);
                break;
            default:
                sortMicroFlowsByCP(microFlows);
                break;
        }
    }

    private void sortMicroFlowsByRE(List<MicroFlow> microFlows) {

        ResourceVector rvSum = new ResourceVector();

        for(int i = 0; i <Parameters.RESOURE_NUM; i++){
            double resource = 0.0;
            for(MicroFlow microFlow: microFlows){
                for(Task task : microFlow.getTaskList())
                    resource += task.getResourceVector().getRes().get(i);
            }
            rvSum.getRes().set(i,resource);
        }

        Collections.sort(microFlows, new Comparator<MicroFlow>() {
            @Override
            public int compare(MicroFlow o1, MicroFlow o2) {
                List<Double> resourceSumO1 = new ArrayList<>(Parameters.RESOURE_NUM);
                List<Double> resourceSumO2 = new ArrayList<>(Parameters.RESOURE_NUM);

                for(int i = 0; i <Parameters.RESOURE_NUM; i++){
                    double resource1 = 0.0;
                    double resource2 = 0.0;
                    for(Task task : o1.getTaskList())
                        resource1 += task.getResourceVector().getRes().get(i);
                    for(Task task : o2.getTaskList())
                        resource2 += task.getResourceVector().getRes().get(i);
                    resourceSumO1.add(i,resource1);
                    resourceSumO2.add(i,resource2);
                }

                double rateSumO1 = 0.0;
                double rateSumO2 = 0.0;

                for(int i = 0; i <Parameters.RESOURE_NUM; i++){
                    rateSumO1 = resourceSumO1.get(i)/rvSum.getRes().get(i);
                    rateSumO2 = resourceSumO2.get(i)/rvSum.getRes().get(i);
                }

                return (int)(rateSumO1 - rateSumO2);
            }
        });

    }

    private void sortMicroFlowsByCP(List<MicroFlow> microFlows) {
        Collections.sort(microFlows, new Comparator<MicroFlow>() {
            @Override
            public int compare(MicroFlow o1, MicroFlow o2) {
                double urgent1 = (o1. getDeadline() - o1.getArrivalTime() - o1.criticalPathLength())/o1.criticalPathLength();
                double urgent2 = (o2. getDeadline() - o2.getArrivalTime() - o2.criticalPathLength())/o2.criticalPathLength();
                return (int)(urgent1 - urgent2);
            }
        });
    }

    private void calculateRanks(List<Task> taskList){
        for(Task task:taskList){
            calculateRank(task);
        }
    }

    private double calculateRank(Task task) {
        if(rank.containsKey(task)){
            return rank.get(task);
        }else{
            double maxRank = 0;
            for(Task child : task.getChildList()){
                double rank = child.getRunTime() + calculateRank(child);
                maxRank = Math.max(maxRank,rank);
            }
            rank.put(task,maxRank);
            return rank.get(task);
        }
    }

    /**
     * 初始任务调度序列
     * 按照子截止期排序
     * @Param
     * @Return
     */
    private PriorityQueue taskSequence(List<Task> taskList) {

        PriorityQueue<Task> taskSequence = new PriorityQueue(taskList.size(), new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return (int)(subdeadline.get(o1) - subdeadline.get(o2));
            }
        });

        for (Task task : taskList) {
            taskSequence.add(task);
        }
        return taskSequence;
    }


    @Override
    public double getTotalCost() {

        double cost = 0.0;

        for (Vm vm : getVmProvisioner().getVmReleasedList()) {

            int numBUT = vm.getReleaseBTU() - vm.getLeaseBTU() + 1;

            cost += numBUT * vm.getVmType().getCost();

        }

        return cost;
    }

}
