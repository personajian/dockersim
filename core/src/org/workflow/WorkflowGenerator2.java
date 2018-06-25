package org.workflow;

import simulation.generator.app.Application;
import simulation.generator.app.CyberShake;
import simulation.generator.util.Distribution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Arrays;

public class WorkflowGenerator2 {

	public static void main(String[] arg) throws Exception {

		for (int i = 0; i < 100; i++) {
			// 均匀分布
			double uniform = (double)Distribution.getUniformDistribution(1,100,1).getInt();
			System.out.println(uniform);
		}

		/*for (int i = 0; i < 100; i++) {
			// 常数分布
			double uniform = Distribution.getConstantDistribution(100,1).getDouble();
			System.out.println(uniform);
		}
		for (int i = 0; i < 10000; i++) {
			// 正态分布
			double uniform = Distribution.getTruncatedNormalDistribution(50	,50,1).getDouble();
			System.out.println(uniform);
		}*/

        /*// 工作流集合大小{ 20, 40, 60, 80, 100 }
		int[] workflowNumbers = { 20, 40, 60, 80, 100 };
		// 任务大小{ 50, 100, 200, 500 }
		int[] taskNumbers = { 50, 100, 200, 500 };
		for (int i = 0; i < workflowNumbers.length; i++) {
			for (int s = 0; s < workflowNumbers[i]; s++)
				for (int j = 0; j < taskNumbers.length; j++) {
                    // 每个工作流集合大小+任务大小 生成10个实验实例
					for (int k = 0; k < 10; k++) {
						generateWorkflows(workflowNumbers[i], taskNumbers[j], s, k);
					}
				}
		}*/

	}

	private static void generateWorkflows(int workflowNumber, int taskNumber, int s, int index)
			throws Exception, FileNotFoundException {
		// Random random = new Random();
		// double runtime = 3 + random.nextInt(15000) / 1000;
		// String[] args = { "-a", "Montage", "-n", "40", "-f",
		// String.valueOf(runtime) };
		// String[] args = { "-a", "Montage", "-n", "40" };
		String[] args = { "-a", "CyberShake", "-n", String.valueOf(taskNumber) };
		String[] newArgs = Arrays.copyOfRange(args, 2, args.length);

		Application app = new CyberShake();
		app.generateWorkflow(newArgs);
		String fileName = args[1] + "_" + workflowNumber + "_" + taskNumber + "_" + index + "_" + s + ".xml";

		String destDir = "D:/dockersim/data/CyberShake";
		new File(destDir).mkdirs();

		fileName = destDir + File.separator + fileName;
		File file = new File(fileName);

		if(!file.exists()){
			file.createNewFile();
		}
		app.printWorkflow(new FileOutputStream(file));
	}
}