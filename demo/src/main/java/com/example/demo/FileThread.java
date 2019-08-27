package com.example.demo;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 文件线程读取
 * @author: benhairui
 * @date: 2019/07/23 17:44:41
 */
public class FileThread{

    private static String path = "/home/benhairui/Documents/industry_chains_v3_table/completeCompany/company_ext2.csv";

    private BufferedReader bufferedReader = null;

    private List<String> contents = new ArrayList<>();



    /**
     * @Description: 初始化相关参数
     * @Date: 19-7-23 下午7:18
     * @param:
     * @return: void
     **/
    public void initParameter(){
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            new FileReadThread().start();
            new FileWriteThread("写入线程1").start();
            new FileWriteThread("写入线程2").start();
            new FileWriteThread("写入线程3").start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void test() {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(new File(path)));
            CSVReader csvReader = new CSVReader(new InputStreamReader(in, "utf-8"), CSVParser.DEFAULT_SEPARATOR,
                    CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, 1);
            String[] strs;
            while ((strs = csvReader.readNext()) != null) {
                System.out.println(Arrays.deepToString(strs));
            }
            csvReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 多线程读取文件
     * @Date: 19-7-23 下午7:17
     * @param: null
     * @return:
     **/
    class FileReadThread extends Thread{


        public void run(){
            synchronized (contents){
                try {
                    while (true){
                        if(contents.size()>=100){
                            System.out.println("数据已满,线程挂起!");
                            contents.wait();
                        }else{
                            contents.add(bufferedReader.readLine());
                            Thread.sleep(10);
                            System.out.println("读入线程,当前数量: "+contents.size());
                            contents.notifyAll();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        public synchronized void run2(){
            try {
              //对contents加锁,继续读取文件
                if(contents.size() >=10){//如果contents满了,那么不用再往变量里写入数据了,直接挂起该线程
                    System.out.println("开始读入数据!");
                    this.wait();
                }else { //没有满的话, 需要向变量里写入数据
                    while (contents.size() < 10) {
                        contents.add(bufferedReader.readLine());
                        Thread.sleep(500);
                        System.out.println("读入线程,当前数量: "+contents.size());
                    }
                }
                System.out.println("here");
            }catch (Exception e){
                e.printStackTrace();
            }
            this.notifyAll(); //跳出循环后唤醒写入线程
        }
    }


    class FileWriteThread extends Thread{

        private String threadName;
        public FileWriteThread(String threadName){
            this.threadName = threadName;
        }

        public void run(){

            synchronized (contents){
                try {
                    while (true) {
                        if (contents.size() == 0) {
                            System.out.println("没有数据了,挂起！" +", " + threadName);
                            contents.wait();
                            System.out.println("继续执行"+", " + threadName);
                        }else{
                            contents.remove(0);
                            Thread.sleep(10);
                            System.out.println("写入线程,当前数量: "+contents.size()+", " + threadName);
                            contents.notifyAll();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }
    }


    public static void main(String[] args) throws Exception{
//        test();
        new FileThread().initParameter();
    }

}
