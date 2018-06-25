package org.exmaple;

import org.dockersim.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 16:45
 */
public class Example7 {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(new File("D:/dockersim/reslut/SIPHT20180614221046.txt")));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D:/dockersim/reslut/SIPHT20180614221046_rdp.txt")));


        // 工作流集合大小{ 20, 40, 60, 80, 100 }
        int[] workflowNumbers = Parameters.workflowNumbers;
        // 任务大小{ 50, 100, 200, 500 }
        int[] taskNumbers = Parameters.taskNumbers;


        lable:
        for (int workflowNumber : workflowNumbers) {
            for (int taskNumber : taskNumbers) {
                for (int instant = 0; instant < 10; instant++) {// 实验实例k
                    for (Parameters.ScheduleAlgorithm scheduleAlgorithm : Parameters.ScheduleAlgorithm.values()) {

                        List<Result> results = new LinkedList<>();
                        Double minCost = Double.MAX_VALUE;

                        for (Parameters.SortMicroFlowsRule sortingMicroFlowsRule : Parameters.SortMicroFlowsRule.values()) {
                            for (Parameters.SubdeadlineDivisionRule subdeadlineDivisingRule : Parameters.SubdeadlineDivisionRule.values()) {
                                for (Parameters.LeaseVmTypeRule leasingVmTypeRule : Parameters.LeaseVmTypeRule.values()) {
                                    for (Parameters.SelectVmRule selectVmRule : Parameters.SelectVmRule.values()) {

                                        String str = reader.readLine();
                                        if (str == null) {
                                            break lable;
                                        }

                                        String[] arr = str.split(" ");
                                        Result result = new Result();
                                        result.workflowNumber = Integer.parseInt(arr[0]);
                                        result.taskNumber = Integer.parseInt(arr[1]);
                                        result.deadlineFactor = Double.parseDouble(arr[2]);
                                        result.instant = Integer.parseInt(arr[3]);
                                        result.scheduleAlgorithm = arr[4];
                                        result.sortingMicroFlowsRule = arr[5];
                                        result.subdeadlineDivisingRule = arr[6];
                                        result.leasingVmTypeRule = arr[7];
                                        result.selectVmRule = arr[8];
                                        result.cost = Double.parseDouble(arr[9]);

                                        results.add(result);
                                        minCost = Double.min(minCost, result.cost);
                                    }
                                }
                            }
                        }

                        for (Result r : results) {
                            r.rdp = (r.cost - minCost) / minCost * 100;
                            writer.write(r.workflowNumber + " ");
                            writer.write(r.taskNumber + " ");
                            writer.write(r.deadlineFactor + " ");
                            writer.write(r.instant + " ");
                            writer.write(r.scheduleAlgorithm + " ");
                            writer.write(r.sortingMicroFlowsRule + " ");
                            writer.write(r.subdeadlineDivisingRule + " ");
                            writer.write(r.leasingVmTypeRule + " ");
                            writer.write(r.selectVmRule + " ");
                            writer.write(r.cost + " ");
                            writer.write(r.rdp + " ");
                            writer.write(System.lineSeparator());
                        }

                    }
                }
            }
        }

        writer.flush();
        writer.close();
        reader.close();

    }
}
    class Result {

        public Result() {
        }

        public int workflowNumber;
        public int taskNumber;
        public double deadlineFactor;
        public int instant;
        public String scheduleAlgorithm;
        public String sortingMicroFlowsRule;
        public String subdeadlineDivisingRule;
        public String leasingVmTypeRule;
        public String selectVmRule;
        public double cost;
        public double rdp;
    }

