package org.exmaple;

import org.dockersim.*;
import simulation.generator.util.Distribution;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 16:45
 */
public class Example3 {

    public static void main(String[] args) {
        try {
            int userId = 0;
            // CSP提供的VM类型数量
            int vmType = 6;

            // 初始化VM类型列表
            List<VmType> vmTypes = new ArrayList<>();

            vmTypes.add(new VmType(1, "t2.small", "AWS EC2", new ResourceVector(1.0, 2.0), 0.023));
            vmTypes.add(new VmType(2, "t2.medium", "AWS EC2", new ResourceVector(2.0, 4.0), 0.0464));
            vmTypes.add(new VmType(3, "m4.large", "AWS EC2", new ResourceVector(2.0, 8.0), 0.1));
            vmTypes.add(new VmType(4, "m4.xlarge", "AWS EC2", new ResourceVector(4.0, 16.0), 0.2));
            vmTypes.add(new VmType(5, "m5.2xlarge", "AWS EC2", new ResourceVector(8.0, 32.0), 0.384));
            vmTypes.add(new VmType(6, "m5.4xlarge", "AWS EC2", new ResourceVector(16.0, 64.0), 0.768));

            launch(userId, vmTypes);

        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static void launch(int userId, List<VmType> vmTypes) throws IOException {

        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;
        ReplicaCatalog.init(file_system);

        List<String> daxPaths = new ArrayList<>();
        // 工作流集合大小{ 20, 40, 60, 80, 100 }
        int[] workflowNumbers = {20, 40, 60, 80, 100};
        // 任务大小{ 50, 100, 200, 500 }
        int[] taskNumbers = {50, 100, 200, 500};

        double[] deadlineFactors = {1.2, 1.3, 1.7};

        String resulitDir = "D:/dockersim/reslut";
        new File(resulitDir).mkdirs();

        String resulitFileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".txt";
        resulitFileName = resulitDir + File.separator + resulitFileName;
        File resultFile = new File(resulitFileName);
        if(!resultFile.exists()){
            resultFile.createNewFile();
        }
        PrintWriter pw = new PrintWriter(new FileWriter(resulitFileName),true);

        String destDir = "D:/dockersim/data/CyberShake";

        for (int workflowNumber : workflowNumbers) {
            for (int taskNumber : taskNumbers) {
                for (double deadlineFactor : deadlineFactors) {
                    for (int instant = 0; instant < 10; instant++) {// 实验实例k
                        for (int w = 0; w < workflowNumber; w++) {
                            daxPaths.add(destDir + File.separator +
                                    "CyberShake" + "_" + workflowNumber + "_" + taskNumber + "_" + instant + "_" + w + ".xml");
                            List<MicroFlow> microFlows = new ArrayList<MicroFlow>();

                            Iterator<String> dIt = daxPaths.iterator();
                            Iterator<MicroFlow> mIt = microFlows.iterator();
                            int mfId = 0;
                            MicroFlow microFlow = new MicroFlow();

                            // 将microflow与daxpath对应起来
                            while (dIt.hasNext()) {
                                String daxPath = dIt.next();
                                microFlow.setId(mfId++);
                                microFlow.setDaxPath(daxPath);
                                microFlows.add(microFlow);
                            }

                            for (MicroFlow mf : microFlows) {
                                // 解析microflow为tasklist
                                MicroflowParser wfParser = new MicroflowParser(userId);
                                String daxPath = mf.getDaxPath();
                                wfParser.parse(daxPath);
                                mf.setTaskList(wfParser.getTaskList());

                                String txtPath = daxPath.substring(0, daxPath.length() - 3) + "txt";
                                BufferedReader br = new BufferedReader(new FileReader(txtPath));
                                for (Task task : mf.getTaskList()) {
                                    task.setMfId(mf.getId());

                                    String line = br.readLine();
                                    String[] config = line.split(" ");
                                    task.setRunTime(Double.parseDouble(config[0]));

                                    // 均分分布生成task的资源需求
                                    // Random random = new Random();
                                    ResourceVector rv = new ResourceVector();
                                    rv.getRes().set(0, Double.parseDouble(config[1]));
                                    rv.getRes().set(1, Double.parseDouble(config[2]));
                                    task.setResourceVector(rv);
                                    // 设置任务运行时间：xml文件中runTime是以秒为单位的，taskLength是runTime*1000 以毫秒为单位，本轮为要以整数分钟为单位！！
                                    // task.taskLength/6000.0 分钟
                                    //task.setRunTime(Math.ceil(task.getTaskLength()/(6000.0)));
                                    //Log.printLine(task);
                                }
                                br.close();
                                mf.setArrivalTime(0);
                                mf.setDeadline(mf.criticalPathLength() * deadlineFactor);
                            }

                            for (Parameters.ScheduleAlgorithm scheduleAlgorithm : Parameters.ScheduleAlgorithm.values()) {
                                for (Parameters.SortMicroFlowsRule sortingMicroFlowsRule : Parameters.SortMicroFlowsRule.values()) {
                                    for (Parameters.SubdeadlineDivisionRule subdeadlineDivisingRule : Parameters.SubdeadlineDivisionRule.values()) {
                                        for (Parameters.LeaseVmTypeRule leasingVmTypeRule : Parameters.LeaseVmTypeRule.values()) {
                                            for (Parameters.SelectVmRule selectVmRule : Parameters.SelectVmRule.values()) {
                                                Parameters.init(scheduleAlgorithm, sortingMicroFlowsRule, subdeadlineDivisingRule, leasingVmTypeRule, selectVmRule);
                                                // 初始化VM供应器
                                                VmProvisoner vmProvisoner = new VmProvisoner(vmTypes);
                                                Docker.DOCKER_ID_COUNTER = 0;
                                                MicroFlowEngine microFlowEngine = new MicroFlowEngine(vmTypes, vmProvisoner);
                                                //对微服务工作流集合进行处理
                                                microFlowEngine.process(microFlows);
                                                double cost = microFlowEngine.getTotalCost();

                                                pw.print(workflowNumber +" "+
                                                        taskNumber + " " +
                                                        deadlineFactor + " "+
                                                        scheduleAlgorithm +  " "+
                                                        sortingMicroFlowsRule + " "+
                                                        subdeadlineDivisingRule +  " "+
                                                        leasingVmTypeRule + " "+
                                                        selectVmRule + " "+
                                                        cost);
                                                pw.println();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        pw.close();
    }

}
