package com.ic;

import com.alibaba.fastjson.JSONObject;
import com.ic.bean.Keyword;
import com.ic.bean.UrlBean;
import com.ic.constant.FileWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

/**
 * 爬虫抓取线程, 在ArrayBlockingQueue为空时, 队列会自动挂起，直到有新的数据进来,所以当最后真的队列为空时,需要有个机制来终止线程
 * <p>
 * 进一步改进: 在线程连续执行n秒后,会自动休眠m秒,释放一些资源, 以便插入线程可以得到执行
 */
public class CrawlThread extends Thread {
    private static Logger logger = LoggerFactory.getLogger(CrawlThread.class);

    private static int num = 0;

    private int index;
    public CyclicBarrier cyclicBarrier;
    public CountDownLatch countDownLatch;
    public ArrayBlockingQueue<Keyword> queue;


    public CrawlThread() {

    }

    public CrawlThread(int index, CyclicBarrier cyclicBarrier) {
        this.index = index;
        this.cyclicBarrier = cyclicBarrier;
    }

    public CrawlThread(int index, CountDownLatch countDownLatch,ArrayBlockingQueue<Keyword> queue) {
        this.index = index;
        this.countDownLatch = countDownLatch;
        this.queue = queue;
    }

    @Override
    public void run() {

        String[] array = null;
        boolean flag = false;
        long start = System.currentTimeMillis();

        List<String> list = new ArrayList<>();
        String tempFilePath = "/home/benhairui/Documents/gitlab-workspace/Work/tempHtmlData_down";

        while (!Crawl.urlQueue.isEmpty()) {
            flag = false;
            try {
                //如果urlQueue为空，那么改队列需要挂起，直到有新的数据进队列
                System.out.println("爬虫线程开始执行: " + index);
                UrlBean url = Crawl.urlQueue.take();
                logger.info("线程: {}, 取后: {},{}", index, Crawl.urlQueue.size(), queue.size());
                String htmlPage = "";
                htmlPage = Crawl.getPage(url.getUrl());
                if (url.getLevel() == 1) {
                    Crawl.parseAndGetUrl(htmlPage, url.getUrl()); //将第一层的url作为第二层的关键词
                } else {

                    array = getKw(url.getKw());
//                    for (String s : array) {
//                        if (htmlPage.contains(s)) {//如果包含关键词,那么flag设为true
//                            flag = true;
//                            break;
//                        }
//                    }
                    if(htmlPage.contains(array[0]) && (htmlPage.contains("上游")||htmlPage.contains("中游") || htmlPage.contains("下游"))){ //如果有包含关键词,那么设置flag为true
                        flag = true;
                    }
                    if (flag) {
                        Keyword kw = new Keyword();
                        kw.setHtml(htmlPage);
                        kw.setKw(url.getKw());
                        kw.setUrl(url.getUrl());
                        String id = url.getUrl().substring("http://www.baidu.com/link?url=".length()); //生成数据的id
                        kw.setId(id);

                    //    queue.put(kw); //将抓取的页面插入到队列中,队列满时,让其自动挂起

                        list.add(JSONObject.toJSONString(kw));
                        System.out.println("数组大小: " + list.size());
                        if (list.size() >= 10) {
                            FileWrite.writeDataToFile(tempFilePath, list);
                            System.out.println("插入数据成功");
                            list.clear();
                        }
                    }
                }
                //日志记录各个url的抓取结果
                if (StringUtils.isEmpty(htmlPage)) {
                    logger.info(url.getUrl() + "  ---------  页面抓取为空");
                } else {
                    logger.info(url.getUrl() + "  ---------  页面抓取成功");
                }


                if (Crawl.isWakeUp && queue.size() >= 10) {
                    countDownLatch.countDown();
                    synchronized (Crawl.isWakeUp) {
                        Crawl.isWakeUp = false;
                    }
                }

                long end = System.currentTimeMillis();
                if (end - start >= 60 * 1000) {
                    start = end; //重新设置当前时间
                    Thread.sleep(10000);//线程休眠10s
                }

            } catch (Exception e) {
                logger.info("页面抓取失败,{}", e.getMessage());
            }
            logger.info("线程:{}, 一次抓取后: {},{}", index, Crawl.urlQueue.size(), queue.size());
        }

        if (list.size() > 0) {
            FileWrite.writeDataToFile(tempFilePath, list);
            list.clear();
        }

        logger.info("线程结束: {}" ,index);
//
//        try {
//            this.cyclicBarrier.await();
//        } catch (Exception e) {
//            System.out.println("await失败: " + e.getMessage());
//        }
    }

    public String[] getKw(String str) {
        String s = str.substring(26);
        String[] array = s.split("_");
        return array;
    }

    public static void main(String[] args) {
        System.out.println("http://www.baidu.com/link?url=".length());
        String str = "http://www.baidu.com/s?wd=hello";
        String[] array = str.split("http://www.baidu.com/s?wd=");
        String s = str.substring(26);
        System.out.println(Crawl.baseUrl.length() + ", " + s);
    }
}
