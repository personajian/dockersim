package org.exmaple;

import org.dockersim.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author personajian
 * @Date 2018/4/24 0024 16:45
 */
public class Example7SiphtCom {

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(new File("D:/dockersim/reslut/CYBERSHAKE_COM20180625152222.txt")));
        BufferedWriter writer = new BufferedWriter(new FileWriter(new File("D:/dockersim/reslut/CYBERSHAKE_COM20180625152222_rdp.txt")));


        // 工作流集合大小{ 20, 40, 60, 80, 100 }
        int[] workflowNumbers = Parameters.workflowNumbers;
        // 任务大小{ 50, 100, 200, 500 }
        int[] taskNumbers = Parameters.taskNumbers;


        lable:
        for (int workflowNumber : workflowNumbers) {
            for (int taskNumber : taskNumbers) {
                for (int instant = 0; instant < 10; instant++) {// 实验实例k
                    List<Result1> results = new LinkedList<>();
                    Double minCost = Double.MAX_VALUE;

                    for (double deadlineFactor : Parameters.DEADLINWFACTORS) {
                        for (Parameters.ScheduleAlgorithm scheduleAlgorithm : Parameters.ScheduleAlgorithm.values()) {

                            String str = reader.readLine();
                            if (str == null) {
                                break lable;
                            }

                            String[] arr = str.split(" ");
                            Result1 result = new Result1();
                            result.workflowNumber = Integer.parseInt(arr[0]);
                            result.taskNumber = Integer.parseInt(arr[1]);
                            result.instant = Integer.parseInt(arr[2]);
                            result.deadlineFactor = Double.parseDouble(arr[3]);
                            result.scheduleAlgorithm = arr[4];
                            result.cost = Double.parseDouble(arr[5]);

                            results.add(result);
                            minCost = Double.min(minCost, result.cost);
                        }

                    }


                    for (Result1 r : results) {
                        r.rdp = (r.cost - minCost) / minCost * 100;
                        writer.write(r.workflowNumber + " ");
                        writer.write(r.taskNumber + " ");
                        writer.write(r.instant + " ");
                        writer.write(r.deadlineFactor + " ");
                        writer.write(r.scheduleAlgorithm + " ");
                        writer.write(r.cost + " ");
                        writer.write(r.rdp + " ");
                        writer.write(System.lineSeparator());
                    }

                }
            }
        }
        writer.flush();
        writer.close();
        reader.close();

    }
}

class Result1 {

    public Result1() {
    }

    public int workflowNumber;
    public int taskNumber;
    public int instant;
    public double deadlineFactor;
    public String scheduleAlgorithm;
    public double cost;
    public double rdp;
}

