package com.ic.Crawls.Gram;

import com.alibaba.fastjson.JSONObject;
import com.ic.constant.FileRead;
import com.ic.constant.Tools;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NGramFormat {


    public static void format() {
        String tokenSingle = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all_single";
        List<String> list = FileRead.readDataFromFile(tokenSingle);
        for (String str : list) {
            Map<String, Object> map = JSONObject.parseObject(str, Map.class);
            String sen = map.get("sen").toString();
            List<String> wList = (List<String>) map.get("value");
            List<String> cleanWList = new ArrayList<>();
            for (String token : wList) {
                if (Tools.isChinese(token.trim())) {//去掉空格
                    cleanWList.add(token);
                }
            }

            List<String> finalList = new ArrayList<>();
            List<String> gramList = new ArrayList<>();
            int start = 0;
            System.out.println("\n当前句子: " + sen);
            for (int i = 0; i < cleanWList.size() - 1; i++) {
                int preIndex = sen.indexOf(cleanWList.get(i), start);
                int endindex = sen.indexOf(cleanWList.get(i + 1), preIndex);//下一个词的下标,从当前位置开始计算

                if (endindex - preIndex == cleanWList.get(i).length()) {
                    finalList.add(cleanWList.get(i) + " :: " + cleanWList.get(i + 1));
                    System.out.println(cleanWList.get(i) + ":" + preIndex + ";" + cleanWList.get(i + 1) + ":" + endindex);
                    getTriToken(cleanWList.get(i) + " :: " + cleanWList.get(i + 1),gramList);
                }
                start = endindex;
            }

            System.out.println(JSONObject.toJSONString(finalList));
            System.out.println(JSONObject.toJSONString(gramList));
            System.out.println(finalList.size() + "\n");
        }
    }


    public static void getTriToken(String str,List<String> list) {
        System.out.println(str);
        String[] array = str.split(" :: ");
        String currentStr = str.replace(" :: ","");
        int start = array[0].length() - 1;
        int end = start + 2;

        if(end<=currentStr.length()) {
            String bigram = currentStr.substring(start, end);
            list.add(bigram);
        }

        if(start-1>=0) {
            String trigram1 = currentStr.substring(start - 1, end);
            list.add(trigram1);
        }
        if(end+1<=currentStr.length()) {
            String trigram2 = currentStr.substring(start, end + 1);
            list.add(trigram2);
        }
    }

    public void ikTest(){
        String str = "标题：全球高温超导材料行业产业链分析及市场预测全球高温超导材料行业产业链分析及市场预测一、产业链概况从产业链来看，超导主要由三部分组成；上游是矿产资源，如钇、钡、铋、锶等金属，是超导行业的基础；中游是超导材料如YBCO和BSCCO等带材，是超导行业的核心；下游是超导应用产品，如超导电缆、超导限流器、超导储能、超导发电机、超导滤波和超导变压器等，是超导行业的载体。";

    }

    public static void main(String[] args) {
        format();
        String str = "创新型 :: 孵化器";
        List<String> gramList = new ArrayList<>();

        getTriToken(str,gramList);
        System.out.println(gramList);

    }


}
