package com.ic.Format;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.SegToken;
import com.ic.bean.Keyword;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import com.ic.constant.Tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class KwFormat {

    private static Set<String> charaSet = new HashSet<String>();
    private static Set<Character> puncSet = new HashSet<>();

    static {
        charaSet.add("un");
        charaSet.add("g");
        charaSet.add("gb");
        charaSet.add("gbc");
        charaSet.add("gc");
        charaSet.add("gi");
        charaSet.add("gg");
        charaSet.add("n");
        charaSet.add("nb");
        charaSet.add("nba");
        charaSet.add("nbc");
        charaSet.add("nz");
        charaSet.add("nn");
        charaSet.add("nmc");
        charaSet.add("nhm");
        charaSet.add("nbd");
        charaSet.add("nb");
        charaSet.add("nrf");

        puncSet.add('.');
        puncSet.add('。');
        puncSet.add('!');
        puncSet.add('！');
        puncSet.add('?');
        puncSet.add('？');
    }

    public static List<String> getKeyword() {
        String baseUrl = "/home/benhairui/Downloads/nodejsons/nodejsons/";
        String[] files = new String[]{"ic_infos_ant_fortune_mongo-local.json", "ic_infos_sw_mongo-local.json", "ic_infos_xmind_mongo-local.json"};
        List<String> list = new ArrayList<>();
        for (String s : files) {
            list.addAll(FileRead.readDataFromFile(baseUrl + s));
        }

        List<Map> mapList = new ArrayList<>();
        for (String s : list) {
            List<Map> subList = JSONObject.parseArray(s, Map.class);
            mapList.addAll(subList);
        }
        System.out.println(list.size());
        System.out.println(mapList.size());
        System.out.println(mapList.get(0));

        List<String> kwList = new ArrayList<>();
        for (Map map : mapList) {
            kwList.add(map.get("name").toString());
        }

        FileWrite.writeDataToFile("/home/benhairui/Desktop/xmind/haveSaved",kwList);
        System.out.println(kwList.get(0));

        return kwList;
    }

    /**
     * 移除数据的html标签
     */
    public static void removeHtmlLabelOfData() {
        String path = "/home/benhairui/Documents/gitlab-workspace/Work/";
        List<String> preList = FileRead.readDataFromFile(path + "tempHtmlData_down");
        List<String> resultList = new ArrayList<>();
        int count = 0;
        for (String s : preList) {
            Keyword kw = JSONObject.parseObject(s, Keyword.class);
            String parseStr = Tools.getPLabelContent(kw.getHtml());
            System.out.println(parseStr);
            kw.setHtml(parseStr);
            resultList.add(JSONObject.toJSONString(kw));

            if (resultList.size() >= 10) {
                FileWrite.writeDataToFile(path + "fouth_data/dataAfterProcess_down", resultList);
                System.out.println(++count);
                resultList.clear();
            }
        }
        FileWrite.writeDataToFile(path + "fouth_data/dataAfterProcess_down", resultList);
        System.out.println(++count);
    }

    /**
     * 获取P标签的里的内容
     */
    public static void getPLabelData() {

    }

    /**
     * 根据关键词拿到它的前后n个句子
     *
     * @param str
     * @param n   前后n句
     */
    public static List<String> getNSentenceOfWord(String str, int n, String kw, String level) {
        List<String> list = new ArrayList<>();
        int start = 0;
        int indexStart = 0;
        int indexEnd = 0;
        int count = 0;
        while (start < str.length()) {
            count = 0;
            int index = str.indexOf(kw, start);
            System.out.println("当前下标: " + index);
            if (index == -1) {//-1说明已经到字符串结尾了，那么直接跳出循环
                break;
            }

            for (int i = index; i < str.length(); ++i) {
                if (puncSet.contains(str.charAt(i))) {
                    ++count;
                }
                if (count == n || i == str.length() - 1) {
                    System.out.println("hello");
                    indexEnd = i;
                    break;
                }
            }

            count = 0;
            for (int i = index; i >= start; --i) {
                if (puncSet.contains(str.charAt(i))) {
                    ++count;
                }
                if (count == n || i == 0) {
                    indexStart = i;
                    break;
                }
            }
            System.out.println(indexStart + "," + indexEnd + "," + count);
            String subStr = str.substring(indexStart + 1, indexEnd + 1).replace("&#12288;", "");
            System.out.println("结果: " + kw + ", " + subStr);
            start = indexEnd + 1;
            indexStart = start;
            if (subStr.contains(level)) {//既包含关键词也包含上游
                list.add(subStr);
            }
        }
        return list;
    }

    /**
     * html搜索结果
     *
     * @param filePath
     * @param resultPath
     * @param n
     */
    public static void getNSentence(String filePath, String resultPath, int n) {
        System.out.println("正在获取前后n句");
        List<String> list = FileRead.readDataFromFile(filePath);
        List<String> mapList = new ArrayList<>();
        for (String str : list) {
            Keyword kw = JSONObject.parseObject(str, Keyword.class);
            String k = kw.getKw();
            String[] array = k.substring(26).split("_");
            String html = kw.getHtml();
            String url = kw.getUrl();

            Map<String, Object> map = new HashMap<>();
            List<String> allSent = new ArrayList<>();
            for (int i = 1; i < array.length; i++) {
                List<String> relativeList = getNSentenceOfWord(html, n, array[0], array[i]);
                allSent.addAll(relativeList);
            }
            map.put("key", k);
            map.put("value", allSent);
            map.put("url", url); //该句子所在的页面
            mapList.add(JSONObject.toJSONString(map));
        }
        FileWrite.writeDataToFile(resultPath, mapList);
    }

    /**
     * 从金融数据文本中获得相关的句子
     */
    public static void getNSentenceOfESText(String filePath, String resultPath, int n) {
        int count = 0;
        try {
            FileReader fr = new FileReader(filePath);
            BufferedReader br = new BufferedReader(fr);
            String line = "";
            List<String> list = new ArrayList<>();
            list.add("上游");
            list.add("中游");
            list.add("下游");
            List<String> mapList = new ArrayList<>();

            int num = 0;
            while ((line = br.readLine()) != null) {
                if ((num++) < count) {
                    continue;
                }
                Map map = JSONObject.parseObject(line, Map.class);
                String kw = map.get("kw").toString();
                String content = map.get("content").toString();

                Map<String, Object> resultMap = new HashMap<>();
                List<String> allSent = new ArrayList<>();
                for (String level : list) {
                    List<String> relativeList = getNSentenceOfWord(content, n, kw, level);
                    allSent.addAll(relativeList);
                    resultMap.put("key", kw + "_" + level);
                    resultMap.put("value", allSent);
                    resultMap.put("url", "None");
                    if (allSent.size() > 0) {
                        mapList.add(JSONObject.toJSONString(resultMap));
                    }
                }

                if (mapList.size() >= 100) {
                    FileWrite.writeDataToFile(resultPath, mapList);
                    System.out.println("写入次数: " + (++count));
                    mapList.clear();
                }
            }
            if (mapList.size() > 0) {
                FileWrite.writeDataToFile(resultPath, mapList);
                System.out.println("最后写入: " + (++count));
                mapList.clear();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("当前断开节点行: " + count);
        }
    }


    /**
     * 对字符串进行分词
     *
     * @return
     */
    public static void getResult(String filepath, String resultPath) {
        List<String> list = FileRead.readDataFromFile(filepath);
        List<String> resultList = new ArrayList<>();
        System.out.println("数量: " + list.size());
        int count = 0;
        int num = 0;
        for (String str : list) {
            Map<String, Object> resultMap = new HashMap();
            Map map = JSONObject.parseObject(str, Map.class);
            List<String> sList = (List<String>) map.get("value");
            List<IWord> wordList = new ArrayList<>();
            for (String ss : sList) {
                List<IWord> iWords = Tools.segSentence(ss);

                resultMap.put("key", map.get("key").toString());
                resultMap.put("value", iWords);
                resultMap.put("url", map.get("url").toString());
                resultMap.put("sen", ss);
                System.out.println((++num) + ", " + JSONObject.toJSONString(resultMap));
                resultList.add(JSONObject.toJSONString(resultMap));
                if (resultList.size() >= 100) {
                    FileWrite.writeDataToFile(resultPath, resultList);
                    System.out.println(++count);
                    resultList.clear();
                }
            }
        }

        FileWrite.writeDataToFile(resultPath, resultList);
        System.out.println(++count);
    }

    public static HashSet<String> getStopWords() {
        String stopWordsPath = "/home/benhairui/Documents/data/dictionary/stopwords.txt";
        List<String> list = FileRead.readDataFromFile(stopWordsPath);

        HashSet<String> set = new HashSet(list);
        return set;
    }


    /**
     * 对句子进行分词处理
     * @param filepath
     * @param resultPath
     */
    public static void getResult2(String filepath, String resultPath) {
        HashSet<String> stopWordSet = getStopWords();
        List<String> list = FileRead.readDataFromFile(filepath);
        List<String> resultList = new ArrayList<>();
        List<String> singleList = new ArrayList<>();
        HashSet<String> set = new HashSet<>();
        System.out.println("数量: " + list.size());
        int count = 0;
        int num = 0;
        for (String str : list) {
            Map<String, Object> resultMap = new HashMap();
            Map<String, Object> singleMap = new HashMap<>();
            Map map = JSONObject.parseObject(str, Map.class);
            List<String> sList = (List<String>) map.get("value");

            for (String ss : sList) {
                List<IWord> iWords = Tools.segSentence(ss);
                ss = ss.replace("\n", "");
                ss = ss.replace("\\n", "");
                List<IWord> wordList = new ArrayList<>();
                List<String> sinList = new ArrayList<>();
                for (IWord word : iWords) {
                    if (charaSet.contains(word.getLabel()) && !stopWordSet.contains(word.getValue())) { //过滤停止词
                        wordList.add(word);
                        sinList.add(word.getValue());
                        set.add(word.getValue());
                    }
                }

                resultMap.put("key", map.get("key").toString());
                resultMap.put("value", wordList);
                resultMap.put("url", map.get("url").toString());
                resultMap.put("sen", ss);
                singleMap.putAll(resultMap);
                singleMap.put("value", sinList);


                System.out.println((++num) + ", " + JSONObject.toJSONString(resultMap));
                resultList.add(JSONObject.toJSONString(resultMap));
                singleList.add(JSONObject.toJSONString(singleMap));
                if (resultList.size() >= 100) {
                    FileWrite.writeDataToFile(resultPath, resultList);
                    FileWrite.writeDataToFile(resultPath + "_single", singleList);
                    System.out.println(++count);
                    resultList.clear();
                    singleList.clear();
                }
            }
        }

        FileWrite.writeDataToFile(resultPath, resultList);
        FileWrite.writeDataToFile(resultPath + "_single", singleList);
        System.out.println(++count);
    }

    /**
     * 删除已经存在的节点
     */
    public static void removeHasExists() {
        List<String> list = getKeyword();
        for (String s : list) {
            Map m = JSONObject.parseObject(s, Map.class);
        }
    }

    /**
     * 根据字符距离取
     */
    public static List<String> getSenOfWordsWithDis(String str, int n, String kw, String level) {

        List<String> list = new ArrayList<>();

        int start = 0;
        int end = 0;
        int current = 0;
        while (current <= str.length()) {

            current = str.indexOf(kw, current);
            start = current - n;

            String subStr = str.substring(start, current);
            if (subStr.contains(level)) {
                for (int i = current; i > 0; i--) {
                    if (puncSet.contains(str.charAt(i))) {
                        start = i;
                        break;
                    }
                }

                for (int i = 0; i < str.length(); i++) {
                    if (puncSet.contains(str.charAt(i))) {
                        end = i;
                        break;
                    }
                }
                list.add(str.substring(start, end));
            }
            current = end;
        }

        return list;
    }

    /**
     * 包括，是，处于，位于
     * 　有
     *
     * @param originPath
     * @param targetPath
     */
    public static void getSenAndKw(String originPath, String targetPath) {
        List<String> list = FileRead.readDataFromFile(originPath);
        List<String> resultList = new ArrayList<>();
        for (String s : list) {
            Map<String, Object> map = JSONObject.parseObject(s, Map.class);
            String key = map.get("key").toString();
            String sen = map.get("sen").toString();
            key = key.substring("http://www.baidu.com/s?wd=".length()).split("_")[0];
            if (sen.contains("是") || sen.contains("包括")) {
                resultList.add(s);
            } else if (sen.contains("位于")) {
                int index = sen.indexOf("处于");
                int endIndex = sen.indexOf(key);
                if (index > 0 && endIndex > 0 && endIndex > index && !(sen.substring(index, endIndex).contains(",") || sen.substring(index, endIndex).contains("，"))) {
                    resultList.add(s);
                }
            } else if (sen.contains("处于")) {
                int index = sen.indexOf("处于");
                int endIndex = sen.indexOf(key);
                if (index > 0 && endIndex > 0 && endIndex > index && !(sen.substring(index, endIndex).contains(",") || sen.substring(index, endIndex).contains("，"))) {
                    resultList.add(s);
                }
            }
            if (resultList.size() >= 100) {
                FileWrite.writeDataToFile(targetPath, resultList);
                resultList.clear();
            }
        }
        if (resultList.size() > 0) {
            FileWrite.writeDataToFile(targetPath, resultList);
            resultList.clear();
        }
    }

    /**
     * 使用jieba重新分词来进行处理
     *
     * @param originPath
     * @param targetPath
     */
    public static void reToken(String originPath, String targetPath) {
        List<String> list = FileRead.readDataFromFile(originPath);
        ArrayList<String> resultList = new ArrayList<>();
        for (String s : list) {
            List<String> wordList = new ArrayList<>();
            Map<String, Object> map = JSONObject.parseObject(s, Map.class);
            String sen = map.get("sen").toString();
//            List<SegToken> tokenList = segmenter.process(sen, JiebaSegmenter.SegMode.SEARCH);
            List<IWord> tokenList = Tools.segSentence(sen);
            tokenList.forEach(token -> wordList.add(token.getValue()));
            map.put("value", wordList);
            resultList.add(JSONObject.toJSONString(map));
            if (resultList.size() >= 100) {
                FileWrite.writeDataToFile(targetPath, resultList);
                resultList.clear();
            }
        }
        if (resultList.size() > 0) {
            FileWrite.writeDataToFile(targetPath, resultList);
            resultList.clear();
        }
    }


    public static void reFormat(String originPath, String targetPath) {
        List<String> list = FileRead.readDataFromFile(originPath);
        List<String> resultList = new ArrayList<>();
        Map<String, StringBuffer> resultMap = new HashMap<>();
        int count = 0;
        for (String s : list) {
            System.out.println(++count);
            Map<String, Object> map = JSONObject.parseObject(s, Map.class);
            String key = map.get("key").toString();
            String sen = map.get("sen").toString();
            String words = map.get("value").toString();
            String url = map.get("url").toString();
            if (resultMap.containsKey(key)) {
                StringBuffer buffer = resultMap.get(key);
                buffer.append("      ").append(sen).append("\n      ").append(words).append("\n\n");
                resultMap.put(key, buffer);
            } else {
                StringBuffer buffer = new StringBuffer();
                buffer.append(key).append(" : ").append(url).append("\n      ").append(sen).append("\n      ").append(words).append("\n\n");
                resultMap.put(key, buffer);
            }
        }
        Iterator iterator = resultMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            resultList.add(resultMap.get(key).toString());
            System.out.println(resultMap.get(key).toString());
        }

        FileWrite.writeDataToFile(targetPath, resultList);
    }

    /**
     * 得到所有文本的单个词
     *
     * @param originPath
     * @param targetPath
     */
    public static void getAllSingleChara(String originPath, String targetPath) {
        List<String> list = FileRead.readDataFromFile(originPath);
        List<String> resultList = null;

        HashSet<String> set = new HashSet<>();
        int count = 0;
        for (String s : list) {
            System.out.println(++count);
            Map<String, Object> map = JSONObject.parseObject(s, Map.class);
            Object obj = map.get("value");
            List<String> words = (List<String>) obj;
            for (String word : words) {
                for (int i = 0; i < word.length(); i++) {
                    String str = String.valueOf(word.charAt(i));
                    if (Tools.isChinese(str)) {
                        set.add(str);
                    }
                }
            }
        }
        resultList = new ArrayList<>(set);

        FileWrite.writeDataToFile(targetPath, resultList);
    }


    /**
     * 去掉相关噪音数据，比如括号, 句子很长的
     */
    public static void cleanData(String originPath, String targetPath) {
        List<String> list = FileRead.readDataFromFile(originPath);
        List<String> resultList = new ArrayList<>();
        List<String> beyondLimit = new ArrayList<>();
        int count = 0;
        for (String s : list) {
            System.out.println(++count);
            Map<String, Object> map = JSONObject.parseObject(s, Map.class);
//            String key = map.get("key").toString();
            String sen = map.get("sen").toString();
            String words = map.get("value").toString();
            String url = map.get("url").toString();

            sen = Tools.removeBrack(sen);
            sen = sen.replace(" ","").replace("&nbsp;","").replace("&gt;","").replace("&quot;","")
                    .replace("&ldquo;","").replace("&ensp","")
                .replace("&ensp;","");
            map.put("sen",sen);
            map.put("length", sen.length());
            map.remove(words);
            map.remove(url);

            if(sen.length()>=150 && sen.length()<200){
                beyondLimit.add(JSONObject.toJSONString(map));
            }else if(sen.length()<150) {

                resultList.add(JSONObject.toJSONString(map));
            }
        }

        FileWrite.writeDataToFile(targetPath, resultList);
        FileWrite.writeDataToFile(targetPath+"_beyondLimt_150", beyondLimit);
    }

    /**
     * 得到P标签里的内容，并保留回车符
     */
    public static void getPLabelContent(){
        String path = "/home/benhairui/Documents/gitlab-workspace/Work/fouth_data/dataAfterProcess_mid";
        List<String> list = FileRead.readDataFromFile(path);
        List<String> targetList = new ArrayList<>();
        int count = 0;
        for(String s: list){
            Map map = JSONObject.parseObject(s,Map.class);

            String html = map.get("html").toString();
            String[] array = html.split("\n");
            for(String str:array){
                if(str.contains("上游")){
                    Map<String,Object> resultMap = new HashMap<>();
                    resultMap.put("para",str);
                    resultMap.put("id",map.get("id"));
                    resultMap.put("kw",map.get("kw"));
                    resultMap.put("url",map.get("url"));
                    targetList.add(JSONObject.toJSONString(resultMap));
                }
            }
            System.out.println(++count);
        }
        String tPath = path+"_para";
        FileWrite.writeDataToFile(tPath,targetList);
        System.out.println("文件写入结束!");
    }


    public static void removeSpecialChara(){
        Set<String> set = Tools.getSpecialChara();
        String path = "/home/benhairui/Documents/gitlab-workspace/Work/fouth_data/dataAfterProcess_All_para";
        List<String> list = FileRead.readDataFromFile(path);
        List<String> resultList = new ArrayList<>();
        int count = 0;
        for(String s: list){
            for(String chara:set){
                if(s.contains(chara)){
                   s = s.replace(chara,"");
                }
            }
            resultList.add(s);
            System.out.println(++count);
        }

        String tPath = path+"_clean";
        FileWrite.writeDataToFile(tPath,resultList);
    }





    public static void main(String[] args) {


//        removeHtmlLabelOfData();
//        String htmlStr = Tools.removeHtmlLabel(str);
////        System.out.println(htmlStr);
//
//        List<String> list = getNSentenceOfWord(htmlStr, 2, "房地产");
//
//
//        System.out.println(list.size());
////
//        String filePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/dataAfterProcess_all";
//        String resultPath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/sentence_1_all";
//        getNSentence(filePath, resultPath, 1);
//
//        String filePath = "/home/benhairui/Documents/es_data/zw_es2";
//        String resultPath = "/home/benhairui/Documents/es_data/es_sentence";
//        getNSentenceOfESText(filePath, resultPath, 1);


//        String segResultPath = "/home/benhairui/Documents/gitlab-workspace/Work/seg_sentence";
//
//        getResult(resultPath, segResultPath);


//        html文本数据
//        String filePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/sentence_1_all";
//        String tokenFilterNoun = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all";
//
//        getResult2(filePath, tokenFilterNoun);
//
//
//        String filePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all";
//        String targetPath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all_filter2";
////        getSenAndKw(filePath, targetPath);
////        String reTokenPath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all_filter_retoken";
////        reToken(targetPath,reTokenPath);
////        System.out.println(htmlStr);
//
//        String tokenSingle = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all_single";
//        String singlePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/result/tokenNoun_all_single_format2";
//
//        reFormat(tokenSingle, singlePath);


//        String tokenSingle = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all_single";
//        String singlePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/result/all_chara";
//
//
//        getAllSingleChara(tokenSingle,singlePath);

//        String filePath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/tokenNoun_all_single";
//        String targetPath = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/result/clean_sentence2";
//
//        cleanData(filePath, targetPath);


//        String filePath = "/home/benhairui/Documents/es_data/es_sentence_clean";
//        String tokenFilterNoun = "/home/benhairui/Documents/es_data/es_token333";
//        getResult2(filePath, tokenFilterNoun);


//        removeHtmlLabelOfData();
//        getPLabelContent();
//        removeSpecialChara();
    }


}