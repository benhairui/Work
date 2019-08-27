package com.ic;

import com.ic.Format.KwFormat;
import com.ic.bean.Keyword;
import com.ic.bean.UrlBean;
import com.ic.constant.FileRead;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/**
 * 爬虫抓取,
 * 这里不用set来去重，默认同一个关键词抓出来的页面都是不同的，
 * 但是不同关键词即使抓出来的关键词是相同的，但是因为关键词不同，默认是两个页面
 */
public class Crawl {

    public Crawl() {
        System.out.println("Crawl初始化");
    }

    private static Logger logger = LoggerFactory.getLogger(Crawl.class);


    public static int insertThreadSize = 1;

    public static Boolean flag = false;

    public static Boolean isWakeUp = true;

    public static final String baseUrl = "http://www.baidu.com/s?wd=";

    public static final String SOGOU_URL = "https://www.sogou.com/web?query=";

    public static int URL_QUEUE_SIZE = 1; //url队列大小,同时也代表抓取的页面大小

    public static int KW_QUEUE_SIZE = 1; //关键词队列大小

    public static int THREADPOOL_SIZE = 1;// 线程池大小

    public static int PAGE_SIZE = 0; //抓取的页数

    public static ExecutorService executorServices = null; //线程池定义

    public static ArrayBlockingQueue<UrlBean> urlQueue = null;

    public static ArrayBlockingQueue<String> kwQueue = null;

    public static Set<String> set = new HashSet<>();

    private static CookieStore cookieStore = new BasicCookieStore();

    /**
     * 存放cookie
     */
    public static Set<CookieStore> cookieStores = new HashSet<>();

    /**
     * 初始化各种参数，这里主要是初始化线程池和队列大小
     */
    public static void init() {

        URL_QUEUE_SIZE = 100000;
        KW_QUEUE_SIZE = 10000;
        THREADPOOL_SIZE = 12;
        PAGE_SIZE = 1;
        executorServices = Executors.newFixedThreadPool(THREADPOOL_SIZE);

        urlQueue = new ArrayBlockingQueue<>(URL_QUEUE_SIZE); //100000的队列，用来存储所有相关的url，为了满足多线程抓取页面
        kwQueue = new ArrayBlockingQueue<>(KW_QUEUE_SIZE);

    }


    /**
     * 拼接url
     *
     * @param prikeyKw 主关键词
     * @param assKw    副关键词
     */
    public static String jointUrl(String prikeyKw, String assKw) {
        StringBuffer url = new StringBuffer(baseUrl);
        url.append(prikeyKw).append("_");
        url.append(assKw);
        return url.toString();
    }

    /**
     * 根据url来抓取页面
     *
     * @param url
     * @throws Exception
     */
    public static String getPage(String url) throws Exception {
        SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(3000).build();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)//一、连接超时：connectionTimeout-->指的是连接一个url的连接等待时间
                .setSocketTimeout(2000)// 二、读取数据超时：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
                .setConnectionRequestTimeout(5000)
                .build();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig); //设置get请求的超时时间

        HttpResponse response = client.execute(get);

        CookieStore cookieStore = new BasicCookieStore();

        String message = "";
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            message = EntityUtils.toString(entity, "UTF-8");
        } else {
            client.setCookieStore(cookieStore);
        }
        ((CloseableHttpResponse) response).close();
        client.close();
        return message;
    }


    /**
     * 解析页面拿到相应的url,并插入到队列中
     */
    public static void parseAndGetUrl(String bodyMsg, String kw) throws Exception {

        Document doc = Jsoup.parse(bodyMsg);
        Element bodyElem = doc.body();

        Element contentLeftElem = bodyElem.getElementById("content_left");

        Elements elements = contentLeftElem.children();

        for (Element elem : elements) {
            Elements h3Elem = elem.getElementsByTag("h3");
            Elements aElem = h3Elem.get(0).getElementsByTag("a");
            aElem.get(0).attributes();

            String linkHref = aElem.get(0).attr("href");
            if (!set.contains(linkHref)) { //如果已经保存了,那么不再插入队列中
                UrlBean urlBean = new UrlBean();
                urlBean.setKw(kw); //设置第二层链接的关键词
                urlBean.setLevel(2);
                urlBean.setUrl(linkHref);
                urlQueue.put(urlBean);
            }
        }
    }

    public static void getHaveInsert(String filePath) {

        try {
            List<String> urlList = FileRead.readDataFromFile(filePath);
            for (String s : urlList) {
                set.add(s);
            }
        } catch (Exception e) {
            logger.info("读取失败: {}", filePath);
        }
    }


    public void produceUrl(String s) {
//        kwQueue.offer(jointUrl(s, "上游"));
        kwQueue.offer(jointUrl(s, "下游"));
//        kwQueue.offer(jointUrl(s, "下游"));

//        UrlBean urlBeanUp = new UrlBean();
//        urlBeanUp.setLevel(1);
//        urlBeanUp.setKw(jointUrl(s, "上游"));
//        urlBeanUp.setUrl(jointUrl(s, "上游"));
//        urlQueue.offer(urlBeanUp);

//
//        UrlBean urlBeanDown = new UrlBean();
//        urlBeanDown.setLevel(1);
//        urlBeanDown.setKw(jointUrl(s, "中游"));
//        urlBeanDown.setUrl(jointUrl(s, "中游"));
//        urlQueue.offer(urlBeanDown);

        UrlBean urlBeanMid = new UrlBean();
        urlBeanMid.setLevel(1);
        urlBeanMid.setKw(jointUrl(s, "下游"));
        urlBeanMid.setUrl(jointUrl(s, "下游"));
        urlQueue.offer(urlBeanMid);
    }


    public void start() {

        String haveInsertPath = "/home/benhairui/Documents/gitlab-workspace/Work/haveInsert";
        getHaveInsert(haveInsertPath);

        init();

        List<String> list = KwFormat.getKeyword();
        System.out.println(kwQueue.size() + "," + list.size());
        for (String s : list) {
            produceUrl(s);
        }

        System.out.println(kwQueue.size());
        System.out.println(urlQueue.size());

        final CountDownLatch countDownLatch = new CountDownLatch(1);


        ArrayBlockingQueue<Keyword> queue = new ArrayBlockingQueue(100); //100的字符串队列,减小内存压力
        InsertThread it = new InsertThread(countDownLatch, queue);
        it.start();
        CyclicBarrier cyclicBarrier = new CyclicBarrier(1, it); //有一个线程执行结束，那么就开始执行插入操作

        for (int i = 1; i <= THREADPOOL_SIZE; i++) {
            CrawlThread ct = new CrawlThread(i, countDownLatch, queue);
            executorServices.execute(ct);
        }
        executorServices.shutdown();
    }

    public static String sendHttpPost(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/json");

        List<NameValuePair> list = new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("sentence","我是中国人"));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(list,"utf-8");

        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(httpPost);
        System.out.println(response.getStatusLine().getStatusCode() + "\n");
        HttpEntity entity2 = response.getEntity();
        String responseContent = EntityUtils.toString(entity2, "UTF-8");
        System.out.println(responseContent);

        response.close();
        httpClient.close();
        return responseContent;
    }

    public static void main(String[] args)throws Exception {
        sendHttpPost("http://localhost:9090");
    }
}
