package com.ic.Crawls.Gram;

import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 每个文件都存储它的统计结果
 */
public class NGramThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(NGramThread.class);

    private String targetPath; //文件的写入路径
    private Set<String> kwSet; //存储已经完成的文件路径
    private ArrayBlockingQueue<String> queue; //所有文件的路径全部存入该队列中
    private Set<String> pathSet;
    private int index;
    private String finishedPath;

    public NGramThread(String targetPath, Set<String> kwSet, ArrayBlockingQueue<String> queue, Set<String> pathSet, int index, String finishedPath) {
        this.targetPath = targetPath;
        this.kwSet = kwSet;
        this.queue = queue;
        this.pathSet = pathSet;
        this.index = index;
        this.finishedPath = finishedPath;
    }

    @Override
    public void run() {
        String path = "";
        List<String> hasFinished = new ArrayList<>();
        Map<String, Integer> map = null;
        while (queue.size() > 0) {
            try {
                map = new HashMap<>();
                path = queue.take();  //文件的读取路径
                String tPath = getTargetPath(targetPath, path);
                if (pathSet.contains(path)) {
                    logger.info("线程: {}, 该文件已经完成: {}", index, path);
                    continue;
                }
                logger.info("线程: {}, 当前gram提取文件: {}, 路径数量: {}", index, path, queue.size());
                List<String> sentences = FileRead.readDataFromFile(path); //百科的所有文本段句子
                int count = 0;
                for (String sentence : sentences) {
                    ++count;
                    getGram(index, sentence, map);
                    logger.info("句子总数: {}, 当前句子下标: {}, 线程下标: {}", sentences.size(), count, index);
                }
                logger.info("线程: {},该文件正在排序: ", index, path);
                List<Map.Entry<String, Integer>> list = SortMap(map);

                WriteDataToFile(index, tPath, list); //每个文件写入一次

                hasFinished.add(path);
                pathSet.add(path);
                FileWrite.writeDataToFile(finishedPath, hasFinished);

                //释放相关变量的空间
                map.clear();
                sentences.clear();
                list.clear();

            } catch (Exception e) {
                logger.error("线程:{}, gram 提取出错: {}, 报错信息: {}", index, path, e.getMessage());
            }
        }
    }


    public static void getGram(int tIndex, String sentence, Map<String, Integer> map) {
        String result = "";
        for (int i = 0; i < sentence.length() - 2; i++) {
            StringBuffer buffer = new StringBuffer("");
            if (i + 1 < sentence.length()) {
                buffer.append(sentence.charAt(i)).append(sentence.charAt(i + 1));
                result = buffer.toString();
                Integer count = 0;
                if (map.containsKey(result)) {
                    count = map.get(result);
                    map.put(result, ++count);
                } else {
                    map.put(result, 1);
                }
//                logger.info("线程:{}, 字符串:{}, 数量: {}, 字符数", tIndex, result, count,2);
            }
            if (i + 2 < sentence.length()) {
                buffer.append(sentence.charAt(i + 2));
                result = buffer.toString();
                Integer count = 0;
                if (map.containsKey(result)) {
                    count = map.get(result);
                    map.put(result, ++count);
                } else {
                    map.put(result, 1);
                }
//                logger.info("线程:{}, 字符串:{}, 数量: {}, 字符数", tIndex, result, 3);
            }
        }
    }


    public static List<Map.Entry<String, Integer>> SortMap(Map<String, Integer> m) {
        List<Map.Entry<String, Integer>> hashList = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
        Collections.sort(hashList, new Comparator<Map.Entry<String, Integer>>() {
            // 降序排序
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });
        return hashList;
    }

    public static void WriteDataToFile(int index, String filePath, List<Map.Entry<String, Integer>> list) {

        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(filePath);
            bw = new BufferedWriter(fw);
            for (Map.Entry<String, Integer> m : list) {
                bw.write(m.getKey() + "::" + m.getValue() + "\n");
            }
            logger.info("线程:{}, 文件写入成功: {}", index, filePath);
        } catch (Exception e) {
            logger.error("线程:{}, 文件写入错误: {}, 报错信息: {}", index, filePath, e.getMessage());
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (Exception e) {
                logger.error("线程:{},文件关闭出错: {},报错信息: {}", index, filePath, e.getMessage());
            }
        }
    }

    public static String getTargetPath(String target, String path) {
        File f = new File(path);
        String parentdir = f.getParentFile().getName();
        String name = f.getName();

        target += parentdir;

        File parent = new File(target);
        if (!parent.exists()) {//如果文件夹不存在，那么就创建文件夹
            parent.mkdirs();
        }

        return target + "/" + name;
    }

    public static void main(String[] args) {
        String sentence = "我是一个中国人";
        String str = "人";
        Map<String, Integer> map = new HashMap<>();
        int n = 2;

        String path = "/home/benhairui/Documents/baike_data/result/00000/split_file00";
        String tpath = "/home/benhairui/Documents/baike_data/dataSta/";

        System.out.println(getTargetPath(tpath, path));


//        getGram(sentence,str,map,n);

    }
}
