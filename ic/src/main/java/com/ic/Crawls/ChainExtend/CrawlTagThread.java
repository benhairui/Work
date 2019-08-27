package com.ic.Crawls.ChainExtend;

import com.alibaba.fastjson.JSONObject;
import com.ic.constant.FileWrite;
import com.ic.constant.Tools;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * 产业链扩展，通过抓取百度图片的suggest tag
 */
public class CrawlTagThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(CrawlTagThread.class);
    private static final String baseUrl = "http://image.baidu.com/search/index?tn=baiduimage&word=";
    private static final Integer numOfInsert = 10;
    private static RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(2000)//一、连接超时：connectionTimeout-->指的是连接一个url的连接等待时间
            .setSocketTimeout(2000)// 二、读取数据超时：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
            .setConnectionRequestTimeout(5000)
            .build();

    private DefaultHttpClient client;
    private ArrayBlockingQueue<String> queue;
    private Set<String> set;
    private int index;


    public CrawlTagThread(DefaultHttpClient client, ArrayBlockingQueue<String> queue, Set<String> set, int index) {
        this.client = client;
        this.queue = queue;
        this.set = set;
        this.index = index;
    }

    @Override
    public void run(){
        ArrayList<String> resultList = new ArrayList<>();
        ArrayList<String> nowFinished = new ArrayList<>();
        Set<String> hasExists = new HashSet<>(set);
        String finishedPath = "/home/benhairui/Downloads/pages/have_finished2";
        String resultPath = "/home/benhairui/Downloads/pages/first_crawl_chain_node_result2";

        while (queue.size()>0){

            try {
                String str = queue.take();
                if(set.contains(str)){
                    continue;
                }
                String url = baseUrl + URLEncoder.encode(str);
                HttpGet httpGet = new HttpGet(url);
                httpGet.setConfig(requestConfig);

                HttpResponse response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                String htmlStr = EntityUtils.toString(entity,"UTF-8");
                List<String> list = Tools.getSearchTagOfBaidu(htmlStr);
                List<String> kwList = new ArrayList<>();
                for (String s : list) {
                    if (s.endsWith("产业链")) {
                        kwList.add(s);
                        if(!hasExists.contains(s)) {
                            queue.put(s);  //将抓取到的节点重新放入队列中，做新的抓取输入参数

                        }
                        hasExists.add(s);
                    }
                }

                Map<String,Object> map = new HashMap<>();
                map.put(str,kwList);

                resultList.add(JSONObject.toJSONString(map));


                set.add(str);
                nowFinished.add(str);

                if(resultList.size()>=numOfInsert){
                    FileWrite.writeDataToFile(finishedPath,nowFinished);
                    nowFinished.clear();

                    FileWrite.writeDataToFile(resultPath, resultList);
                    resultList.clear();
                }

                logger.info("当前线程: {},resultList大小:{}, set大小: {}, 当前队列数: {},当前输入关键词: {}, 抓取结果: {}",index,resultList.size(),set.size(),queue.size(),str,kwList);

            }catch (Exception e){
                FileWrite.writeDataToFile(finishedPath,nowFinished);
                nowFinished.clear();

                FileWrite.writeDataToFile(resultPath, resultList);
                resultList.clear();
            }
        }

        if(resultList.size()>0) {
            FileWrite.writeDataToFile(finishedPath, nowFinished);
            nowFinished.clear();

            FileWrite.writeDataToFile(resultPath, resultList);
            resultList.clear();
        }


        client.close();

    }
}
