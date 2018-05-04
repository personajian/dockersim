package org.dockersim;

import java.util.*;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 15:17
 */
public class MicroFlow {

    private int id;

    private String name;

    private String daxPath;

    private List<Task> taskList;

    private int arrivalTime;

    private int deadline;

    public MicroFlow() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDaxPath() {
        return daxPath;
    }

    public void setDaxPath(String daxPath) {
        this.daxPath = daxPath;
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    /**
     * 计算微服务工作流的关键路径
     *
     * @Param
     * @Return
     */
    public LinkedList<Task> criticalPath() {
        int size = taskList.size();
        LinkedList<Task> criticalPath = new LinkedList<Task>();

        Task task0 = taskList.get(0);
        task0.setLst(arrivalTime);
        task0.setEft(task0.getEst() + task0.getRunTime());

        criticalPath.addLast(taskList.get(size - 1));

        Task vb = taskList.get(size - 1);

        for (int j = 1; j < size; j++) {
            Task temp = taskList.get(j);
            int maxEft = Integer.MIN_VALUE;

            for (Task task : temp.getParentList()) {
                if (task.getEft() > maxEft)
                    maxEft = task.getEft();
            }

            temp.setEft(maxEft + temp.getRunTime());
        }

        while (!vb.equals(task0)) {
            int counter = 0;
            int maxeft = 0;
            Task maxTask = vb;
            for (Task task : vb.getParentList()) {
                if (task.getEft() > maxeft)
                    maxTask = task;
            }
            criticalPath.addFirst(maxTask);
        }

        return criticalPath;
    }

    /**
     * 计算微服务工作流的关键路径：方法2
     *
     * @Param
     * @Return
     */
    public LinkedList<Task> criticalPath2() {
        int size = taskList.size();
        LinkedList<Task> criticalPath = new LinkedList<Task>();
        // 已分配EFT集合，未非配EFT集合
        Set<Task> unEftSet = new HashSet<>();
        Set<Task> alEftSet = new HashSet<>();

        // 头结点EFT加入alEFTSet，非头结点加入unEFTSet
        for(Task task:taskList){
            if(task.getParentList().size() == 0){
                task.setEst(arrivalTime);
                task.setEft(task.getEst() + task.getRunTime());
                alEftSet.add(task);
            }else{
                unEftSet.add(task);
            }
        }

        Iterator<Task> itUn = unEftSet.iterator();

        // 遍历未分配EFT集合
        while(!unEftSet.isEmpty()){

            if(!itUn.hasNext())
                itUn = unEftSet.iterator();

            Task task = itUn.next();
            // 此结点的前驱结点都已计算过EFT？
            boolean flag = true;
            for(Task t: task.getParentList()){
                if(!alEftSet.contains(t)){
                    flag =false;
                    break;
                }
            }
            // 此结点的前驱结点都已计算过了EFT，取maxEFT+runTime;
            if(flag){
                int maxEFT = Integer.MIN_VALUE;
                for(Task t: task.getParentList())
                    maxEFT = Math.max(maxEFT,t.getEft()+ task.getRunTime());
                task.setEft(maxEFT);
                task.setEst(maxEFT- task.getRunTime());
                alEftSet.add(task);
                itUn.remove();
            }
        }

        int maxEFT = Integer.MIN_VALUE;
        Task maxEFTTask = new Task();

        for(Task task: taskList){
            if(task.getEft() >= maxEFT){
                maxEFT = task.getEft();
                maxEFTTask = task;
            }
        }

        criticalPath.addFirst(maxEFTTask);

        Task maxTask = maxEFTTask;

        while (maxTask.getEst() != 0) {
            int maxeft = Integer.MIN_VALUE;
            for (Task task : maxTask.getParentList()) {
                if (task.getEft() > maxeft){
                    maxeft = task.getEft();
                    maxTask = task;
                }
            }
            criticalPath.addFirst(maxTask);
        }

        return criticalPath;
    }

    /**
     * 计算关键路径长度
     *
     * @Param
     * @Return
     */
    public int cirticalPathLength() {
        int criticalPathLength = criticalPath2().getLast().getEft();
        return criticalPathLength;
    }


}
