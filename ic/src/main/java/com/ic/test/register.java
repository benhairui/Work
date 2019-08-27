package com.ic.test;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/hello")
public class register {

    private Logger logger = LoggerFactory.getLogger(register.class);

    @Autowired
    private ES_Chain es_chain;

    @RequestMapping(value = "/startTest")
//    @ResponseBody
    public String myTest() {
        return "editInfo";
    }


//    @RequestMapping(value = "/registerUser2", method = RequestMethod.POST)
//    public ModelAndView registerUser(String username, String password) {
//
//
//        logger.info("hello");
//
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        ModelAndView mv = null;
//        if (username.equals("1") && password.equals("1")) {
//            mv = new ModelAndView("redirect:/hello/startTest");
//        } else {
//            mv = new ModelAndView("redirect:/hello/registerUser");
//        }
//
//        return mv;
//    }

    @RequestMapping(value = "/registerUser",method = RequestMethod.POST)
    public @ResponseBody String registerUser2(String username,String password){
        String current = "success";
//        logger.info("登录成功");
//        User user = new User();
//        user.setUsername(username);
//        user.setPassword(password);
//        logger.info(username+":"+password);


        boolean flag = es_chain.judgeUserInfo(username,password);

        if(flag){
            current = "success";
        }else{
            current = "fail";
        }
        logger.info("是否匹配: {}",flag);

        return current;
    }

    @RequestMapping(value = "pre",method = RequestMethod.POST)
    public @ResponseBody String preSen(){

        logger.info("测试click()操作");

        return "hello world";
    }

    @RequestMapping(value = "/next",method = RequestMethod.POST)
    @ResponseBody
    public SenInfo nextSen(String userName,String id){
        logger.info("进入函数");
        logger.info("nextInfo信息: {},{}",userName,id);
        SenInfo senInfo = es_chain.getNextInfo(userName,id);
        logger.info("结果: {}", JSONObject.toJSONString(senInfo));


        return senInfo;
    }

    @RequestMapping(value = "/skip", method = RequestMethod.GET)
    public ModelAndView skipAnother() {
        logger.info("页面跳转");

        ModelAndView mv = new ModelAndView("redirect:/hello/startTest");

        logger.info("执行玩了");

        return mv;
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(String id, String filterWord,String author) {
        SenInfo senInfo = new SenInfo();
        if(filterWord.isEmpty()){
            return "empty";
        }else {
            senInfo.setId(id);
            senInfo.setAuthor(author);
            senInfo.setFin(true);
            senInfo.setSubmit_time(ES_Chain.format(new Date()));
            if(filterWord.trim().equals("none")){
                senInfo.setFilter_word_str(filterWord);
            }else{
                senInfo.setFilter_word_str(filterWord);
            }
        }

        boolean flag = es_chain.submit(senInfo);


        return String.valueOf(flag);

    }

    public static void main(String[] args){


        System.out.println(new Date());
    }

}
