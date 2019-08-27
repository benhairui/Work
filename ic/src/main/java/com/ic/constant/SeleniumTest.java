package com.ic.constant;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SeleniumTest {


    public static void main(String[] args) throws Exception{

        System.setProperty("webdriver.chrome.driver", "/home/benhairui/Downloads/chromedriver_linux64/chromedriver");
        WebDriver driver = new ChromeDriver(); //新建一个WebDriver 的对象，但是new 的是FirefoxDriver的驱动
        driver.get("http://www.sogou.com");//打开指定的网站
        driver.findElement(By.id("query")).sendKeys(new String[]{"hello"});//找到kw元素的id，然后输入hello
        driver.findElement(By.id("stb")).click(); //点击按扭
        try {
            /**
             * WebDriver自带了一个智能等待的方法。
             dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             Arg0：等待的时间长度，int 类型 ；
             Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            Thread.sleep(5000);
            System.out.println(driver.manage().getCookies());

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * dr.quit()和dr.close()都可以退出浏览器,简单的说一下两者的区别：第一个close，
         * 如果打开了多个页面是关不干净的，它只关闭当前的一个页面。第二个quit，
         * 是退出了所有Webdriver所有的窗口，退的非常干净，所以推荐使用quit最为一个case退出的方法。
         */
//        driver.quit();//退出浏览器


        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CookieStore cookieStore = new BasicCookieStore();

        Set<org.openqa.selenium.Cookie> cookieSet = driver.manage().getCookies();
        Cookie cookie = null;

        org.apache.http.cookie.Cookie[] cookies = new org.apache.http.cookie.Cookie[cookieSet.size()];

        int i = 0;
        for(org.openqa.selenium.Cookie c:cookieSet){
//            org.apache.http.cookie.Cookie cookie1 = new BasicClientCookie()
//                    .setAttribute("domain",c.getDomain());
//
//
////            cookies[i++] =

            System.out.println(c);


        }





        ((BasicCookieStore) cookieStore).addCookies(cookies);

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();


        HttpGet get = new HttpGet("https://www.sogou.com/web?query=hello");


        HttpResponse response = httpClient.execute(get);

    }

}
