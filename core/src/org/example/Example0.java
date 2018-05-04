package org.example;

import org.dockersim.Log;
import org.dockersim.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 16:45
 */
public class Example0 {

    public static void main(String[] args) {
        try {
            int userId = 0;
            // CSP提供的VM类型数量
            int vmType = 6;

            // 初始化VM类型列表
            List<VmType> vmTypes = new ArrayList<>();

            vmTypes.add(new VmType(1, "t2.small", "AWS EC2", 1, 1, 2, 0, 0.023));
            vmTypes.add(new VmType(2, "t2.medium", "AWS EC2", 1, 2, 4, 0, 0.0464));
            vmTypes.add(new VmType(3, "m4.large", "AWS EC2", 1, 2, 8, 0, 0.1));
            vmTypes.add(new VmType(4, "m4.xlarge", "AWS EC2", 1, 4, 16, 0, 0.2));
            vmTypes.add(new VmType(5, "m5.2xlarge", "AWS EC2", 1, 8, 32, 0, 0.384));
            vmTypes.add(new VmType(6, "m5.4xlarge", "AWS EC2", 1, 16, 64, 0, 0.768));

            // 工作流数目
            int microflowNum = 3;

            List<String> daxPaths = new ArrayList<>();
            daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/Montage_25.xml");
            daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/Montage_50.xml");
            daxPaths.add("D:/dev/workspace/idea/dockersim/core/config/dax/Montage_100.xml");


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
                MicroflowParser wfParser = new MicroflowParser(userId);

                wfParser.parse(mf.getDaxPath());
                mf.setTaskList(wfParser.getTaskList());
                for(Task task:mf.getTaskList()){
                    Log.printLine(task);
                }

            }

            //MicroFlowEngine microFlowEngine = new MicroFlowEngine();
            // 对微服务工作流集合进行处理
            //microFlowEngine.process(microFlows);

        } catch (Exception e) {
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }


}
