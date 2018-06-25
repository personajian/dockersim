package org.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dockersim.MicroFlow;
import org.dockersim.Task;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author personajian
 * @Date 2018/5/22 0022 15:51
 */
public class UtilExcel {

    public static void createExcel(List<Task> taskList) {

        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

        //存储路径--获取桌面位置
        FileSystemView view = FileSystemView.getFileSystemView();
        File directory = view.getHomeDirectory();
        System.out.println(directory);
        //存储Excel的路径
        String path = directory + "\\" + date + ".xlsx";
        System.out.println(path);
        try {
            //定义一个Excel表格
            XSSFWorkbook wb = new XSSFWorkbook();  //创建工作薄
            XSSFSheet sheet = wb.createSheet("sheet1"); //创建工作表
            XSSFRow row = sheet.createRow(0); //行
            XSSFCell cell;  //单元格

            //添加表头数据
            for (int i = 0; i < taskList.size(); i++) {
                //从前端接受到的参数封装成list集合，然后遍历下标从而取出对应的值
                Task value = taskList.get(i);
                //将取到的值依次写到Excel的第一行的cell中
                row.createCell(i).setCellValue(0);
            }

            //输出流,下载时候的位置
//            FileWriter outputStream1 = new FileWriter(path);
            FileOutputStream outputStream = new FileOutputStream(path);
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
            System.out.println("写入成功");
        } catch (Exception e) {
            System.out.println("写入失败");
            e.printStackTrace();
        }

    }

    public static void exportMicroFlow(List<MicroFlow> microFlows) {

        //获取当前时间
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

        //存储路径--获取桌面位置
        FileSystemView view = FileSystemView.getFileSystemView();
        File directory = view.getHomeDirectory();
        System.out.println(directory);
        //存储Excel的路径
        String path = directory + "\\" + date + ".xlsx";
        System.out.println(path);
        try {
            //定义一个Excel表格
            XSSFWorkbook wb = new XSSFWorkbook();  //创建工作薄
            int count = 0;
            for(MicroFlow mf : microFlows){
                List<Task> taskList = mf.getTaskList();

                XSSFSheet sheet = wb.createSheet("sheet"+ count); //创建工作表
                XSSFCell cell;  //单元格

                XSSFRow row = sheet.createRow(0); //行

                row.createCell(0).setCellValue("$v_{i,j}$");
                row.createCell(1).setCellValue("$T^{e}_{i.j}$");
                row.createCell(2).setCellValue("$est_{i,j}$");
                row.createCell(3).setCellValue("$lft_{i,j}$");
                row.createCell(4).setCellValue("$\\ell^{B}(i,j)$");
                row.createCell(5).setCellValue("$\\ell^{F}(i,j)$");
                row.createCell(6).setCellValue("$d_{i,j}$");
                row.createCell(7).setCellValue("$\\bar{\\ell}$");
                row.createCell(8).setCellValue("$f_{ij}$");

                //添加表头数据
                for (int i = 0; i < taskList.size(); i++) {

                    row = sheet.createRow(i+1); //行

                    //从前端接受到的参数封装成list集合，然后遍历下标从而取出对应的值
                    Task task = taskList.get(i);
                    //将取到的值依次写到Excel的第一行的cell中

                    //row.createCell(0).setCellValue(task.getTaskId());
                    /*int mfId = mf.getId()+1;
                    row.createCell(0).setCellValue("$v_{"+mfId+","+task.getTaskId()+"}$");
                    row.createCell(1).setCellValue(task.getRunTime());
                    row.createCell(2).setCellValue(task.getEst());
                    row.createCell(3).setCellValue(task.getLft());
                    row.createCell(4).setCellValue(task.getLb());
                    row.createCell(5).setCellValue(task.getLf());
                    row.createCell(6).setCellValue(task.getSubDeadline());
                    row.createCell(7).setCellValue(task.getL_());
                    row.createCell(8).setCellValue(task.getFt());*/
                }

                //输出流,下载时候的位置
//            FileWriter outputStream1 = new FileWriter(path);
                FileOutputStream outputStream = new FileOutputStream(path);
                wb.write(outputStream);
                outputStream.flush();
                outputStream.close();
                System.out.println("写入成功");

                count++;
            }
        } catch (Exception e) {
            System.out.println("写入失败");
            e.printStackTrace();
        }

    }
}
