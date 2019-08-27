package com.ic.test;

import com.ic.bean.Keyword;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Crawl2 {

    public static Integer count = 0;
    public static ArrayBlockingQueue<Keyword> htmlQueue = htmlQueue = new ArrayBlockingQueue<>(20);

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/home/benhairui/Documents/java/ChromeDriver/chromedriver");
//        WebDriver driver = new ChromeDriver();
//
//        try{
////            driver.get("https://www.sogou.com/");
////
////            driver.findElement(By.id("query")).sendKeys("超导材料");
//////            driver.findElement(By.id("stb")).sendKeys(Keys.ENTER);
////            driver.findElement(By.id("stb")).click();
////
////
//            driver.manage().window().maximize();
//
//
//            driver.get("https://www.baidu.com");
//
//            driver.findElement(By.id("kw")).sendKeys("Selenium");
//
//            driver.findElement(By.id("su")).click();
//            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//        }catch (Exception e){
//            System.out.println("报错了: \n"+e.getMessage());
//        }
//            System.out.println(driver.getPageSource());
//            System.out.println(driver.getTitle());
//
//
//        driver.close();
//        driver.quit();
        WebDriver driver = new ChromeDriver(); //新建一个WebDriver 的对象，但是new 的是FirefoxDriver的驱动



        driver.get("https://www.sogou.com/");//打开指定的网站
        driver.findElement(By.id("query")).sendKeys("超导材料");//找到kw元素的id，然后输入hello
        driver.findElement(By.id("stb")).click(); //点击按扭


        System.out.println(driver.manage().getCookies());
        try {
            /**
             * WebDriver自带了一个智能等待的方法。
             dr.manage().timeouts().implicitlyWait(arg0, arg1）；
             Arg0：等待的时间长度，int 类型 ；
             Arg1：等待时间的单位 TimeUnit.SECONDS 一般用秒作为单位。
             */
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

            Thread.sleep(200);

            System.out.println(driver.getPageSource());
            System.out.println(driver.getTitle());

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * dr.quit()和dr.close()都可以退出浏览器,简单的说一下两者的区别：第一个close，
         * 如果打开了多个页面是关不干净的，它只关闭当前的一个页面。第二个quit，
         * 是退出了所有Webdriver所有的窗口，退的非常干净，所以推荐使用quit最为一个case退出的方法。
         */
        driver.quit();//退出浏览器
    }


}
