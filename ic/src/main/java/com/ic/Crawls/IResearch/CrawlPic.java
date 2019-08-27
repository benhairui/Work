package com.ic.Crawls.IResearch;

import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 艾瑞咨询图片爬取
 */
public class CrawlPic extends Thread {

    private static Logger logger = LoggerFactory.getLogger(CrawlPic.class);

    private static String picBasePath = "http://report.iresearch.cn/rimgs/";

    private static String titleBasePath = "https://www.iresearch.com.cn/Detail/report?isfree=0&id=";

    private static String suffer = ".jpg";

    private static String basePath = "/home/benhairui/Pictures/airui2/";

    private Set<String> set ;

    public void run() {

        ArrayList<String> finishedList = new ArrayList<>();

        List<String> list = FileRead.readDataFromFile(basePath+"finished");

        set = new HashSet<>(list);

        try {
            for (int num = 2999; num >= 2400; num--) { //3160   3315

//                String titlePath = titleBasePath + num;
//                String title = "";

//                String html = getPage(titlePath, 0, "");
//                title = getTitle(html);
//                logger.info("当前title是: {}", title);

                String targetPath = basePath + num;
                File f = new File(targetPath);
                if (!f.exists()) {
                    f.mkdirs();
                } else if (!f.isDirectory()) {
                    f.delete();
                    f.mkdirs();
                }

                int count = 1;
                while (count<=60) {
                    String picPath = picBasePath + num + "/" + count + suffer;
                    if(set.contains(picPath)){
                        continue;
                    }
                    String msg = getPage(picPath, 1, targetPath + "/" + count + suffer);
                    if (msg.equals("error")) {//如果是404,那么直接跳出循环，进行抓取下一个报告
                        break;
                    }

                    if (msg.equals("success")) {
                        set.add(picPath);
                        finishedList.add(picPath);
                    }
                    ++count;
                }
                FileWrite.writeDataToFile(basePath + "finished", finishedList);
                finishedList.clear();

                Thread.sleep(500); //休眠500ms, 防止ip被封
            }

        } catch (Exception e) {
            logger.info("抓取出错: {}", e.getMessage());
            e.printStackTrace();

            FileWrite.writeDataToFile(basePath + "finished", finishedList);
        }
    }


    /**
     * 根据url来抓取页面
     *
     * @param url
     * @throws Exception
     */
    public static String getPage(String url, int type, String filePath) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)//一、连接超时：connectionTimeout-->指的是连接一个url的连接等待时间
                .setSocketTimeout(2000)// 二、读取数据超时：SocketTimeout-->指的是连接上一个url，获取response的返回等待时间
                .setConnectionRequestTimeout(5000)
                .build();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setConfig(requestConfig); //设置get请求的超时时间

        HttpResponse response = client.execute(get);


        String message = "";
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String html = "";
//            String html = EntityUtils.toString(entity);
            if (type == 1) { //实际抓图片
                if (!html.contains("404.jpg")) {
                    InputStream in = entity.getContent();
                    message = saveFileToDisk(in, filePath);
//                    byte[] bytes = readInputStream(in);
//                    //创建输出流
//                    FileOutputStream outStream = new FileOutputStream("/home/benhairui/Pictures/airui/3160/3.jpg");
//                    //写入数据
//                    outStream.write(bytes);

                } else {
                    message = "error";
                }
            }
            if (type == 0) {
                message = html;
            }
        }
        client.close();
        return message;
    }

    public static String getTitle(String doc) {
        Document document = Jsoup.parse(doc);
        Elements elems = document.getElementsByClass("m-report-content");

        String title = elems.get(0).text();

        return title;
    }

    public static String saveFileToDisk(InputStream in, String filePath) {

        String state = "fail";
        try {
            byte[] bytes = readInputStream(in);
            File f = new File(filePath);
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes);
            state = "success";
            fos.close();
        } catch (Exception e) {
            logger.info("存储报错: {}", e.getMessage());
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                logger.info("inputstream关闭失败: {}", e.getMessage());
            }
        }
        logger.info("存储成功: {}", filePath);

        return state;
    }



    public static byte[] readInputStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while ((len = inStream.read(buffer)) != -1) {
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }

    public static void main(String[] args) {
        CrawlPic c = new CrawlPic();
        c.start();
    }
}
