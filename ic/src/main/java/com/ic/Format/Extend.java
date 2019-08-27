package com.ic.Format;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import com.ic.constant.Tools;

import java.util.*;

public class Extend {

    public static void senStas() {
        String filePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/result/clean_sentence2";
        int length = "http://www.baidu.com/s?wd=".length();

        List<String> list = FileRead.readDataFromFile(filePath);

        int count = 0;
        for (String s : list) {
            Map<String, Object> map = JSONObject.parseObject(s, Map.class);
            String sen = map.get("sen").toString();
            String key = map.get("key").toString();
            key = key.substring(length, key.length());
            String[] array = key.split("_");

            List<IWord> tokenList = Tools.segSentence(sen);
            System.out.println((++count) + ". " + key + ": " + tokenList);
        }
    }

    /**
     * 去重
     */
    public static void removeRepeat(){
        String filePath = "/home/benhairui/Documents/es_data/es_sentence_clean";
        List<String> list = FileRead.readDataFromFile(filePath);
        Set<String> set = new HashSet<>(list);
        List<String> setList = new ArrayList<>(set);

        FileWrite.writeDataToFile(filePath+"_clean",setList);

    }

    /**
     * 随机选择n个词观察
     */
    public static void randomPick(int n){
        String filePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/result/clean_sentence2";
        List<String> list = FileRead.readDataFromFile(filePath);
        List<String> randomList = new ArrayList<>();
        Random random = new Random();

        List<Integer> intlist = new ArrayList<>();
        for(int i = 0;i<n;i++){
            int num = random.nextInt(list.size());
            randomList.add(list.get(num));
            System.out.println(list.get(num));
        }
        randomList.add("\n==============\n");

        FileWrite.writeDataToFile(filePath+"_randomfile",randomList);

    }




    public static void main(String[] args) {
//        senStas();
//        removeRepeat();
        randomPick(50);
    }


}
