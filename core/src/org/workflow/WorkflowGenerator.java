package org.workflow;

import org.dockersim.Parameters;
import simulation.generator.app.Application;
import simulation.generator.app.CyberShake;
import simulation.generator.app.SIPHT;
import simulation.generator.util.Distribution;

import java.io.*;
import java.util.Arrays;

public class WorkflowGenerator {

    public enum WorkflowName {CyberShake, SIPHT}

    public static void main(String[] arg) throws Exception {
        // 工作流集合大小{ 20, 40, 60, 80, 100 }
        int[] workflowNumbers = Parameters.workflowNumbers;
        // 任务大小{ 50, 100, 200, 500 }
        int[] taskNumbers = Parameters.taskNumbers;
        //int[] workflowNumbers = { 20};
        //int[] taskNumbers = { 50 };
        for (WorkflowName workflowName : WorkflowGenerator.WorkflowName.values()) {
            for (int i = 0; i < workflowNumbers.length; i++) {
                for (int j = 0; j < taskNumbers.length; j++) {
                    for (int wf = 0; wf < workflowNumbers[i]; wf++) {
                        generateWorkflows(workflowName, workflowNumbers[i], taskNumbers[j], wf);
                        for (int instant = 0; instant < 10; instant++){
                            // 每个工作流集合大小+任务大小 生成10个实验实例
                            generateInstance(workflowName, workflowNumbers[i], taskNumbers[j], wf, instant);
                        }
                    }
                }
            }
        }
    }

    private static void generateInstance(WorkflowName workflowName, int workflowNumber, int taskNumber, int wf, int instant) throws IOException {

        String destDir = "D:/dockersim/data/" + workflowName.name();
        new File(destDir).mkdirs();
        // String[] args = { "-a", "Montage", "-n", "40", "-f",
        // String[] args = { "-a", "Montage", "-n", "40" };
        String[] args = {"-a", workflowName.name(), "-n", String.valueOf(taskNumber)};
        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);

        // 生成所有工作流实例的任务runtime,cpu,ram配置
        String txtFileName = args[1] + "_" + workflowNumber + "_" + taskNumber + "_" + wf + "_" + instant + ".txt";
        txtFileName = destDir + File.separator + txtFileName;
        File txtFile = new File(txtFileName);
        if (!txtFile.exists()) {
            txtFile.createNewFile();
        }
        printTaskConfig(new PrintWriter(new FileWriter(txtFileName)), taskNumber);


    }

    private static void generateWorkflows(WorkflowName workflowName, int workflowNumber, int taskNumber, int wf)
            throws Exception, FileNotFoundException {
        String destDir = "D:/dockersim/data/" + workflowName.name();
        new File(destDir).mkdirs();
        // String[] args = { "-a", "Montage", "-n", "40", "-f",
        // String[] args = { "-a", "Montage", "-n", "40" };
        String[] args = {"-a", workflowName.name(), "-n", String.valueOf(taskNumber)};
        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);

        Application app;
        switch (workflowName) {
            case CyberShake:
                app = new CyberShake();
                break;
            case SIPHT:
                app = new SIPHT();
                break;
            default:
                app = new CyberShake();
                break;
        }

        app.generateWorkflow(newArgs);

        String xmlFileName = args[1] + "_" + workflowNumber + "_" + taskNumber + "_" + wf + ".xml";
        xmlFileName = destDir + File.separator + xmlFileName;
        File xmlFile = new File(xmlFileName);
        if (!xmlFile.exists()) {
            xmlFile.createNewFile();
        }
        app.printWorkflow(new FileOutputStream(xmlFile));

        //replace(xmlFileName);

    }

    private static void printTaskConfig(PrintWriter pw, int taskNumber) {

        for (int i = 0; i < taskNumber; i++) {
            double runTime = Distribution.getUniformDistribution(1, 100, 1).getDouble();
            double cpu = Distribution.getUniformDistribution(0.0, 2, 1).getDouble();
            double ram = Distribution.getUniformDistribution(0.0, 2, 1).getDouble();
            pw.print(runTime + " " + cpu + " " + ram);
            pw.println();
        }
        pw.close();
    }

    private static void replace(String fileName) throws IOException {

        File srcFile = new File(fileName);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(srcFile)));

        File destFile = new File(fileName + ".tmp");

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