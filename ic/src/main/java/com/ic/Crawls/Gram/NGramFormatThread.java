package com.ic.Crawls.Gram;


import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import com.ic.constant.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 数据格式化线程
 */
public class NGramFormatThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(NGramFormatThread.class);

    private ArrayBlockingQueue<String> queue;
    private int index;
    private String targetBasePath;
    private Set<String> finishedPathSet;
    private String finishedPath;

    public NGramFormatThread(ArrayBlockingQueue<String> queue, int index, String targetBasePath, Set<String> finishedPathSet, String finishedPath) {
        this.queue = queue;
        this.index = index;
        this.targetBasePath = targetBasePath;
        this.finishedPathSet = finishedPathSet;
        this.finishedPath = finishedPath;
    }

    @Override
    public void run() {
        String path = "";
        List<String> resultList = new ArrayList<>();
        List<String> pathList = new ArrayList<>(1);
        while (queue.size() > 0) {
            try {
                path = queue.take();
                if (finishedPathSet.contains(path)) {
                    logger.info("线程:{},该路径已经被格式化了: {}", index, path);
                }else{
                    logger.info("当前线程: {}, 正在处理的文件: {}",index,path);
                    Thread.sleep(10000);
                }
                List<String> list = FileRead.readDataFromFile(path);

                int count = 0;
                int num = 0;
                for (String str : list) {
                    try {
                        String[] array = str.split("::");
                        if (array.length == 2 && Tools.isChinese(array[0])) {//保留全部中文字符
                            resultList.add(str);
//                            logger.info("正确的字符串: {}",str);
                        }else{
//                            logger.info("被过滤的字符串: " + str);
                            ++num;
                        }
                        logger.info("当前线程: {}, 字符串总数:{}, 正确字符串数量: {}, 错误字符串数量: {}",index,list.size(),resultList.size(),num);
                    } catch (Exception e) {
                        logger.info("线程:{}, 该字符格式化报错: {}, 路径文件: {}, 报错信息: {}", index, str, path, e.getMessage());
                    }
                }

                String targetPath = NGramThread.getTargetPath(targetBasePath, path);
                FileWrite.writeDataToFile(targetPath, resultList);
                resultList.clear();

                finishedPathSet.add(path);
                pathList.add(path);
                FileWrite.writeDataToFile(finishedPath, pathList);
                pathList.clear();
            } catch (Exception e) {
                logger.info("线程:{}, 该路径格式化报错: {}, 报错信息: {}", index, path, e.getMessage());
            }
        }
        logger.info("线程{}结束....",index);
    }
}
