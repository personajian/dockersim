package org.workflow;

import org.dockersim.Parameters;

import java.io.*;

/**
 * @Author personajian
 * @Date 2018/5/31 0031 18:53
 */
public class WorkflowDelete {

    public static void main(String[] args) throws IOException {

        String daxPath = "D:/dockersim/data";

        //String fileName = daxPath + File.separator+"CyberShake_30.xml";

        // 工作流集合大小{ 20, 40, 60, 80, 100 }
        int[] workflowNumbers = Parameters.workflowNumbers;
        // 任务大小{ 50, 100, 200, 500 }
        int[] taskNumbers = Parameters.taskNumbers;

        //int[] workflowNumbers = { 20};
        //int[] taskNumbers = { 50 };
        for (WorkflowGenerator.WorkflowName workflowName : WorkflowGenerator.WorkflowName.values()) {
            for (int i = 0; i < workflowNumbers.length; i++) {
                for (int j = 0; j < taskNumbers.length; j++) {
                    for (int wf = 0; wf < workflowNumbers[i]; wf++) {
                        // 每个工作流集合大小+任务大小 生成10个实验实例
                        String fileName = daxPath + File.separator +
                                workflowName + File.separator +
                                workflowName + "_" +
                                workflowNumbers[i] + "_" +
                                taskNumbers[j] + "_" +
                                wf + ".xml";
                        replace(fileName);
                    }
                }
            }
        }
    }

    private static void replace(String fileName) throws IOException {

        File srcFile = new File(fileName);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(srcFile)));

        File destFile = new File(fileName + "tmp");

        BufferedWriter writer = new BufferedWriter(new FileWriter(destFile));


        String str = null;
        while (true) {
            str = reader.readLine();

            if (str == null)
                break;

            if (!str.startsWith("    <uses")) {
                writer.write(str);
                writer.write("\n");
            }
        }
        writer.flush();
        writer.close();
        reader.close();

        srcFile.delete();
        destFile.renameTo(srcFile);
    }

}
