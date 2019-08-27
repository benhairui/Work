package com.ic;

import com.alibaba.fastjson.JSONObject;
import com.ic.bean.Keyword;
import com.ic.connect.ES;
import com.ic.constant.FileWrite;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * 插入线程
 */
public class InsertThread extends Thread {

    private static Logger logger = LoggerFactory.getLogger(InsertThread.class);
    private static final int NUM_OF_INSERT = 10;

    private static final String INDEX_NAME = "ic";
    private static final String TYPE_NAME = "html";

    private static final String storeFilePath = "/home/benhairui/Documents/gitlab-workspace/Work/haveInsert";

    private CountDownLatch countDownLatch;
    public ArrayBlockingQueue<Keyword> queue;

    public InsertThread(CountDownLatch countDownLatch, ArrayBlockingQueue queue) {
        this.countDownLatch = countDownLatch;
        this.queue = queue;
    }

    @Override
    public void run() {

        HashSet<String> set = new HashSet<>();
        List<String> list = new ArrayList<>();
        TransportClient client = null;
        try {
            countDownLatch.await();
            client = ES.getSingleClient();
        } catch (Exception e) {
            logger.error("es client对象获取失败," + e.getMessage());
        }

        logger.info("节点数量: " + client.listedNodes().size());

        BulkRequestBuilder requestBuilder = client.prepareBulk();
        BulkResponse response = null;

        int count = 0;
        System.out.println("插入线程开始执行");
        while (queue.size() > 0 || Crawl.urlQueue.size() > 0) {

            Keyword html = null;
            try {
                html = queue.take(); //唤醒其他等待中的线程,当队列为空时,会自动挂起
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            logger.info("插入线程: {},{}", Crawl.urlQueue.size(), queue.size());

            if (html == null) {
                continue;
            }

            Map<String, String> mapSource = new HashMap<>();
            mapSource.put("kw", html.getKw());
            mapSource.put("url", html.getUrl());
            mapSource.put("html", html.getHtml());
            IndexRequest request = new IndexRequest(INDEX_NAME, TYPE_NAME, html.getId()).source(mapSource); //用url的hashCode作为es的唯一主键
            requestBuilder.add(request);

            System.out.println("request数量: " + requestBuilder.numberOfActions());

            if (requestBuilder.numberOfActions() >= NUM_OF_INSERT) {
                logger.info("插入次数: {}", (++count));
                System.out.println("==============================插入次数: " + count);
                try {
                    response = requestBuilder.get();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                storeHaveInsert(response, storeFilePath);
                int total = 0;
                for (int i = 0; i < response.getItems().length; i++) {
                    if (response.getItems()[i].isFailed()) {
                        ++total;
                    }
                }
                System.out.println("ES Response: "+response.getItems().length + ", " + response.hasFailures() + ", " + total);
                requestBuilder = client.prepareBulk(); //重新申请空间，为下一次批量插入做准备
            }
        }

        System.out.println("已经跳出循环！");

        if (requestBuilder.numberOfActions() > 0) {
            logger.info("插入次数: {}", ++count);
            response = requestBuilder.get();
            storeHaveInsert(response,storeFilePath);
        }
        logger.info("重复的id: " + JSONObject.toJSONString(list));
    }

    /**
     * 存储已经插入的url
     */
    public void storeHaveInsert(BulkResponse response, String filePath) {
        if(response == null){
            return;
        }
        BulkItemResponse[] itemResponses = response.getItems();
        List<String> list = new ArrayList<>();
        for (BulkItemResponse itemResponse : itemResponses) {
            if (!itemResponse.isFailed()) {
                list.add(itemResponse.getId());
            }
        }
        FileWrite.writeDataToFile(filePath, list);
    }
}
