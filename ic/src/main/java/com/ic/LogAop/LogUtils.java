package com.ic.LogAop;

import com.alibaba.fastjson.JSONObject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 对日志做切面管理,条件是必须是被spring管理的bean,不然没法做切面管理
 */
@Aspect
@Component
public class LogUtils {

    private static Logger logger = LoggerFactory.getLogger(LogUtils.class);

    /**
     * 插入后做切面管理
     */
    @Pointcut("execution(* org.elasticsearch.action.bulk.BulkRequest.*(..))")
    private void afterBulkResponse() {
    }

    @AfterReturning(pointcut = "execution(* com.ic.test.SpringTest.myTest())",returning="response")
    public void responseAopTest(JoinPoint point,Object response){
        logger.info("报错了: "+response.toString());
    }

    @AfterReturning(pointcut = "execution(* org.elasticsearch.client.transport.TransportClient.prepareBulk())", returning="response")
    public void responseAop(JoinPoint point,  Object response) {

        logger.info("插入日志测试!");

        BulkResponse responses = (BulkResponse) response;

        String methodName = point.getSignature().getName();

        List<String> list = new ArrayList<>();
        Iterator<BulkItemResponse> iter = responses.iterator();
        while(iter.hasNext()){
            BulkItemResponse itemResponse = iter.next();
            if(itemResponse.isFailed()){
                list.add(itemResponse.getId());
            }
        }

        if(list.size()>0) {
            logger.info("当前方法名: {}, 失败的数据id有: ",methodName, JSONObject.toJSONString(list));

        }else{
            logger.info("当前方法名: {}, 全部插入成功!", methodName);
        }
    }
}
