package org.exmaple;

import org.dockersim.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 16:45
 */
public class Example2 {

    public static void main(String[] args) {
        try {
            int userId = 0;
            // CSP提供的VM类型数量
            int vmType = 6;

            // 初始化VM类型列表
            List<VmType> vmTypes = new ArrayList<>();

            VmType vmType1 = new VmType(1, "t2.small", "AWS EC2", new ResourceVector(1.0,2.0), 0.023);

            vmTypes.add(new VmType(1, "t2.small", "AWS EC2", new ResourceVector(1.0,2.0), 0.023));
            vmTypes.add(new VmType(2, "t2.medium", "AWS EC2", new ResourceVector(2.0,4.0), 0.0464));
            vmTypes.add(new VmType(3, "m4.large", "AWS EC2", new ResourceVector(2.0,8.0), 0.1));
            vmTypes.add(new VmType(4, "m4.xlarge", "AWS EC2", new ResourceVector(4.0,16.0), 0.2));
            vmTypes.add(new VmType(5, "m5.2xlarge", "AWS EC2", new ResourceVector(8.0,32.0), 0.384));
            vmTypes.add(new VmType(6, "m5.4xlarge", "AWS EC2", new ResourceVector(16.0,64.0), 0.768));

            // 初始化VM供应器
            VmProvisoner vmProvisoner= new VmProvisoner(vmTypes);

            // 工作流数目
            int microflowNum = 3;

            List<String> daxPaths = new ArrayList<>();
            //daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/leadmm.xml");
            //daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/example_1.xml");
            daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/Montage_25.xml");
            //daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/Montage_100.xml");
            //daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/Montage_1000.xml");


            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;
            ReplicaCatalog.init(file_system);

            List<MicroFlow> microFlows = new ArrayList<MicroFlow>();

            Iterator<String> dIt = daxPaths.iterator();
            Iterator<MicroFlow> mIt = microFlows.iterator();
            int mfId = 0;

            // 将microflow与daxpath对应起来
            while (dIt.hasNext()) {
                MicroFlow microFlow = new MicroFlow();
                microFlow.setId(mfId++);
                microFlow.setDaxPath(dIt.next());
                microFlows.add(microFlow);
            }

            for (MicroFlow mf : microFlows) {
                // 解析microflow为tasklist
                MicroflowParser wfParser = new MicroflowParser(userId);

                wfParser.parse(mf.getDaxPath());
                mf.setTaskList(wfParser.getTaskList());
                for(Task task: mf.getTaskList()){
                    // 均分分布生成task的资源需求
                    Random random = new Random();
                    ResourceVector rv = new ResourceVector();
                    rv.getRes().set(0,(random.nextInt(40) + 1)/10.0);
                    rv.getRes().set(1,(random.nextInt(40) + 1)/10.0);
                    task.setResourceVector(rv);
                    // 设置任务运行时间：xml文件中runTime是以秒为单位的，taskLength是runTime*1000 以毫秒为单位，本轮为要以整数分钟为单位！！
                    // task.taskLength/6000.0 分钟
                    //task.setRunTime(Math.ceil(task.getTaskLength()/(6000.0)));
                    task.setRunTime(Math.ceil(task.getTaskLength()/1000.0));
                    //Log.printLine(task);
                    task.setMfId(mf.getId());
                }
                mf.setArrivalTime(0);
                mf.setDeadline(Math.ceil(mf.criticalPathLength()*1.3));
            }

            Parameters.ScheduleAlgorithm scheduleAlgorithm = Parameters.ScheduleAlgorithm.SMWS;
            Parameters.SortMicroFlowsRule sortingMicroFlowsRule = Parameters.SortMicroFlowsRule.SWS1;
            Parameters.SubdeadlineDivisionRule subdeadlineDivisingRule = Parameters.SubdeadlineDivisionRule.EDD;
            Parameters.LeaseVmTypeRule leasingVmTypeRule = Parameters.LeaseVmTypeRule.KLD;
            Parameters.SelectVmRule selectVmRule = Parameters.SelectVmRule.KLD;

            Parameters.init(scheduleAlgorithm,sortingMicroFlowsRule,subdeadlineDivisingRule,leasingVmTypeRule,selectVmRule);

            MicroFlowEngine microFlowEngine = new MicroFlowEngine(vmTypes,vmProvisoner);
            //对微服务工作流集合进行处理
            microFlowEngine.process(microFlows);

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }


}
