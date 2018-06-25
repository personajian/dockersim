package org.workflow;

import simulation.generator.util.Distribution;

import java.io.*;
import java.text.SimpleDateFormat;

/**
 * @Author personajian
 * @Date 2018/5/29 0029 21:04
 */
public class DoubleGenerator {

    public static void main(String[] args) throws IOException {

        String destDir = "D:/dockersim/data/CyberShake";
        String txtFileName = "double.txt";
        txtFileName = destDir + File.separator + txtFileName;
        File txtFile = new File(txtFileName);
        if(!txtFile.exists()){
            txtFile.createNewFile();
        }

        PrintWriter pw = new PrintWriter(new FileWriter( txtFileName ));
        for (int i = 0; i < 20; i++) {
            double runTime = Distribution.getUniformDistribution(1, 100, 1).getDouble();
            double cpu = Distribution.getUniformDistribution(0.0, 2, 1).getDouble();
            double ram = Distribution.getUniformDistribution(0.0, 2, 1).getDouble();
            pw.print(runTime +" "+ cpu + " " + ram);
            pw.println();
        }
        pw.close();

    }
}
