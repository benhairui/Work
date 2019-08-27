package com.ic;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class testThread extends Thread {

    private static Logger logger = LoggerFactory.getLogger(testThread.class);


    @Override
    public void run(){
        logger.info("已经开始执行了！！！");
    }


}
