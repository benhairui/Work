package com.ic.Crawls.ChainExtend;

import com.ic.constant.FileRead;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

public class TagStart {

    public void start(){

        List<String> finishedList = FileRead.readDataFromFile("/home/benhairui/Downloads/pages/have_finished");
        Set<String> set = new HashSet<>(finishedList);

        List<String> list = FileRead.readDataFromFile("/home/benhairui/Downloads/pages/chain_node_result");

        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(300000);
        blockingQueue.addAll(list);

        int threadNum = 8;

        CrawlTagThread[] crawlTagThreads = new CrawlTagThread[threadNum];
        for(int i = 0;i<threadNum;i++){

            DefaultHttpClient client = new DefaultHttpClient();

            crawlTagThreads[i] = new CrawlTagThread(client,blockingQueue,set,i);
        }

        for(int i = 0;i<threadNum;i++){
            crawlTagThreads[i].start();
        }

        for(int i = 0;i<threadNum;i++){
            try {
                crawlTagThreads[i].join();
            }catch (Exception e){
                System.out.println("join失败: " + e.getMessage());
            }
        }

        System.out.println("程序结束! ");
    }


    public static void main(String[] args){


    }
}
