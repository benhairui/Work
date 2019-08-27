package com.ic.Crawls.Gram;

import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import com.ic.constant.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class MergeAllFileThread extends Thread {

    private static Logger logger = LoggerFactory.getLogger(MergeAllFileThread.class);

    private ArrayBlockingQueue<String> queue; //存储第二层文件夹的路径，然后每个文件夹洗存储相应的合并结果
    private int index;
    private String targetBasePath;
    private Set<String> finishedPathSet;
    private String finishedPath;

    public MergeAllFileThread(ArrayBlockingQueue<String> queue, int index, String targetBasePath, Set<String> finishedPathSet, String finishedPath) {
        this.queue = queue;
        this.index = index;
        this.targetBasePath = targetBasePath;
        this.finishedPathSet = finishedPathSet;
        this.finishedPath = finishedPath;
    }

    @Override
    public void run() {
        String path = "";
        List<String> pathList = new ArrayList<>(1);

        while (queue.size() > 0) {

            try {
                path = queue.take();
                Map<String, Integer> map = new HashMap<>();



                if (finishedPathSet.contains(path)) {
                    logger.info("线程:{},该路径已经被格式化了: {}", index, path);
                } else {
                    logger.info("当前线程: {}, 正在处理的文件: {}", index, path);
                }

                String targetPath = getTargetPath(targetBasePath, path);
                logger.info("最终写入路径: {}",targetPath);
                Thread.sleep(10000);


                File dirFile = new File(path);
                File[] listFiles = dirFile.listFiles();
                int fileNum = 0;
                for (File f : listFiles) {
                    List<String> list = FileRead.readDataFromFile(f.getAbsolutePath());
                    int num = 0;
                    ++fileNum;
                    int total = 0;
                    for (String str : list) {
                        try {
                            String[] array = str.split("::");
                            if (map.containsKey(array[0])) {
                                int count = map.get(array[0]);
                                count += Integer.parseInt(array[1]);
                                map.put(array[0], count);
                            } else {
                                map.put(array[0], Integer.parseInt(array[1]));
                            }
                            if((++total)%50000 == 0 && total>=50000) { //每50000次打印一次日志
                                logger.info("当前第{}个文件, 当前文件名: {},总行数: {}, 当前行数：{},当前文件路径: {}", fileNum, f.getName(), list.size(), ++num, f.getAbsolutePath());
                            }
                        }catch (Exception e){
                            logger.info("字符串出错了: {}",str);
                        }
                    }
                }

                logger.info("map大小: {}, 写入文件: {}",map.size(),targetPath);
                FileWrite.writeMapDataToFile(targetPath, map);
                map.clear();

                finishedPathSet.add(path);
                pathList.add(path);
                FileWrite.writeDataToFile(finishedPath, pathList);
                pathList.clear();
            } catch (Exception e) {
                logger.info("线程:{}, 该路径格式化报错: {}, 报错信息: {}", index, path, e.getMessage());
            }
        }
        logger.info("线程{}结束....", index);
    }

    /**
     * 用文件夹的名字来做最后统计的文件名
     * @param target
     * @param path
     * @return
     */
    public static String getTargetPath(String target, String path) {
        File f = new File(path);
        String parentdir = f.getName();

        target += parentdir;

        File file = new File(target);
        if(file.exists()){//如果文件存在，说明是中间文件，那么删除掉，重新进行写入
            file.delete();
        }
        return target;
    }
}
