package com.ic.Crawls.Gram;

import com.ic.constant.FileRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * ngram. 统计每个词的bigram和trigram
 */
public class NGram {

    private static Logger logger = LoggerFactory.getLogger(NGram.class);

    public static void getAllFile(String dirPath, ArrayBlockingQueue<String> queue, Set<String> finishedSet) {
        File file = new File(dirPath);
        File[] files = file.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                getAllFile(f.getAbsolutePath(), queue, finishedSet);
            } else {
                if (!finishedSet.contains(f.getAbsolutePath())) {
                    queue.add(f.getAbsolutePath());
                }
            }
        }

    }

    public static void start() {
//        String targetPath = "/home/benhairui/Documents/baike_data/dataSta2/";
//        String filePath = "/home/benhairui/Documents/baike_data/result/";
//        String kwPath = "/home/benhairui/Documents/baike_data/all_chara";
//        String pPath = "/home/benhairui/Documents/baike_data/finishedPath2";
//        String num = "5";


//        String targetPath = "/home/lxy/mahairui/dataSta/";
//        String filePath = "/zfs/lixiaoyun/source_data_industry/";
//        String kwPath = "/home/lxy/mahairui/all_chara";
//        String pPath = "/home/lxy/mahairui/finishedPath";
//
        String paraPath = "/home/lxy/mahairui/parameter";
        List<String> paraList = FileRead.readDataFromFile(paraPath);
        String targetPath = paraList.get(0);
        String filePath = paraList.get(1);
        String kwPath = paraList.get(2);
        String pPath = paraList.get(3);
        String num = paraList.get(4);

        List<String> kwList = FileRead.readDataFromFile(kwPath);
        Set<String> set = new HashSet<>(kwList);


        List<String> pathList = FileRead.readDataFromFile(pPath);
        Set<String> pathSet = new HashSet<>(pathList);

        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10000);

        getAllFile(filePath, blockingQueue,pathSet);

        logger.info("所有路径的队列大小: {}, 已完成的路径大小: {}, 关键词的大小: {}", blockingQueue.size(), pathSet.size(), set.size());


        int threadNum = Integer.parseInt(num);

        NGramThread[] threads = new NGramThread[threadNum];

        for (int i = 0; i < threadNum; i++) {
            threads[i] = new NGramThread(targetPath, set, blockingQueue, pathSet, i, pPath);
        }
        for (int i = 0; i < threadNum; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                System.out.println("join失败: " + e.getMessage());
            }
        }
        System.out.println("线程结束!!!");
    }


    /**
     * 统计结果格式化
     */
    public static void formatStart() {
//        String targetPath = "/home/benhairui/Documents/baike_data/dataClean/";
//        String filePath = "/home/benhairui/Documents/baike_data/dataSta2/";
//        String pPath = "/home/benhairui/Documents/baike_data/finishedPathClean";
//        String num = "5";


//        String targetPath = "/home/lxy/mahairui/dataSta/";
//        String filePath = "/zfs/lixiaoyun/source_data_industry/";
//        String kwPath = "/home/lxy/mahairui/all_chara";
//        String pPath = "/home/lxy/mahairui/finishedPath";
//
        String paraPath = "/home/lxy/mahairui/parameter";
        List<String> paraList = FileRead.readDataFromFile(paraPath);
        String targetPath = paraList.get(0);
        String filePath = paraList.get(1);
        String kwPath = paraList.get(2);
        String pPath = paraList.get(3);
        String num = paraList.get(4);


        List<String> pathList = FileRead.readDataFromFile(pPath);
        Set<String> pathSet = new HashSet<>(pathList);

        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10000);

        getAllFile(filePath, blockingQueue,pathSet);

        int threadNum = Integer.parseInt(num);

        NGramFormatThread[] threads = new NGramFormatThread[threadNum];

        for (int i = 0; i < threadNum; i++) {

            threads[i] = new NGramFormatThread(blockingQueue, i, targetPath, pathSet, pPath);
        }
        for (int i = 0; i < threadNum; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                System.out.println("join失败: " + e.getMessage());
            }
        }
        logger.info("线程结束!");
    }

    public static void getMergeAllFile(String dirPath, ArrayBlockingQueue<String> queue) {
        File file = new File(dirPath);
        File[] files = file.listFiles();

        for (File f : files) {
            queue.add(f.getAbsolutePath());
        }
    }

    /**
     * 合并结果文件
     */
    public static void mergeStart() {
//        String targetPath = "/home/benhairui/Documents/baike_data/mergeDir/";
//        String filePath = "/home/benhairui/Documents/baike_data/dataClean/";
//        String pPath = "/home/benhairui/Documents/baike_data/mergeFinished";
//        String num = "1";


//        String targetPath = "/home/lxy/mahairui/dataSta/";
//        String filePath = "/zfs/lixiaoyun/source_data_industry/";
//        String kwPath = "/home/lxy/mahairui/all_chara";
//        String pPath = "/home/lxy/mahairui/finishedPath";
//
        String paraPath = "/home/lxy/mahairui/parameter";
        List<String> paraList = FileRead.readDataFromFile(paraPath);
        String targetPath = paraList.get(0);
        String filePath = paraList.get(1);
        String kwPath = paraList.get(2);
        String pPath = paraList.get(3);
        String num = paraList.get(4);


        List<String> pathList = FileRead.readDataFromFile(pPath);
        Set<String> pathSet = new HashSet<>(pathList);

        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(10000);

        getMergeAllFile(filePath, blockingQueue);

        int threadNum = Integer.parseInt(num);

        MergeAllFileThread[] threads = new MergeAllFileThread[threadNum];

        for (int i = 0; i < threadNum; i++) {

            threads[i] = new MergeAllFileThread(blockingQueue, i, targetPath, pathSet, pPath);
        }
        for (int i = 0; i < threadNum; i++) {
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {
                System.out.println("join失败: " + e.getMessage());
            }
        }
        logger.info("线程结束!");
    }

    public static void main(String[] args) {
        mergeStart();
    }

}
