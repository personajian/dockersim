package org.example;

import org.dockersim.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @Author personajian
 * @Date 2018/5/1 0001 22:03
 */
public class TestKLD {

    public static void main(String[] args) {

        // 初始化VM类型列表
        List<VmType> vmTypes = new ArrayList<>();

        vmTypes.add(new VmType(1, "t2.small", "AWS EC2", 1, 1, 2, 0, 0.023));
        vmTypes.add(new VmType(2, "t2.medium", "AWS EC2", 1, 2, 4, 0, 0.0464));
        vmTypes.add(new VmType(3, "m4.large", "AWS EC2", 1, 2, 8, 0, 0.1));
        vmTypes.add(new VmType(4, "m4.xlarge", "AWS EC2", 1, 4, 16, 0, 0.2));
        vmTypes.add(new VmType(5, "m5.2xlarge", "AWS EC2", 1, 8, 32, 0, 0.384));
        vmTypes.add(new VmType(6, "m5.4xlarge", "AWS EC2", 1, 16, 64, 0, 0.768));


        List<String> daxPaths = new ArrayList<>();
        daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/leadmm.xml");

        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;
        ReplicaCatalog.init(file_system);

        List<MicroFlow> microFlows = new ArrayList<MicroFlow>();

        Iterator<String> dIt = daxPaths.iterator();
        Iterator<MicroFlow> mIt = microFlows.iterator();

        // 将microflow与daxpath对应起来
        while (dIt.hasNext()) {
            MicroFlow microFlow = new MicroFlow();
            microFlow.setDaxPath(dIt.next());
            microFlows.add(microFlow);
        }

        for (MicroFlow mf : microFlows) {
            // 解析microflow为tasklist
            MicroflowParser wfParser = new MicroflowParser(0);

            wfParser.parse(mf.getDaxPath());
            mf.setTaskList(wfParser.getTaskList());
            for(Task task:mf.getTaskList()){
                Random random = new Random();
                // 均分分布生成task的资源需求
                task.setVcpu((random.nextInt(40) + 1)/10.0);
                task.setRam((random.nextInt(40) + 1)/10.0);
                // 设置任务运行时间：xml文件中runTime是以秒为单位的，taskLength是runTime*1000 以毫秒为单位，本轮为要以整数分钟为单位！！
                // task.taskLength/6000.0 分钟
                task.setRunTime((int)Math.ceil(task.getTaskLength()/(6000.0)));
                Log.printLine(task);
            }
            mf.setArrivalTime(0);
            mf.setDeadline((int)Math.ceil(mf.cirticalPathLength()*1.3));
        }

        for (MicroFlow mf : microFlows) {
            List<Task> taskList = mf.getTaskList();
            double kld = Double.MIN_VALUE;
            double rvs = Double.MIN_VALUE;
            for(Task t:taskList){
                for(VmType vt:vmTypes){
                    kld = UtilAlgo.LBR(t,vt,null,0);
                    System.out.println( t.getVcpu() +" "+ t.getRam() +" "+
                            vt.getVcpu() + " "+vt.getRam() + " "+kld);
                }
            }

            for(Task t:taskList){
                for(VmType vt:vmTypes){
                    kld = UtilAlgo.LBR(t,vt,null,0);
                    System.out.println( t.getVcpu() +" "+ t.getRam() +" "+
                            vt.getVcpu() + " "+vt.getRam() + " "+rvs);
                }
            }

        }

    }

}
