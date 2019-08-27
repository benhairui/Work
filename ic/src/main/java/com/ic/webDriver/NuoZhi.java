package com.ic.webDriver;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.asm.FieldWriter;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import org.apache.xpath.operations.Bool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class NuoZhi {


    private static SimHashTest simHashTest = new SimHashTest();
    private static SimHashTest h1 = new SimHashTest();
    private static SimHashTest h2 = new SimHashTest();

    public static int compare(String str1, String str2) {

        h1.simHash(str1, 64);
        h2.simHash(str2, 64);

        int dis = simHashTest.getDistance(h1.getStrSimHash(), h2.getStrSimHash());

        return dis;
    }

    /**
     * 在一篇文章里删除simHash相似的句子，只保留一个
     *
     * @param path
     */
    public static void removeSame(String path) {

        List<String> list = new ArrayList<>();

        int count = 0;
        int total = 0;
        try (FileReader fileReader = new FileReader(path)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            List<String> all = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {

                Map m = JSONObject.parseObject(line, Map.class);

                List<String> midInfos = (List<String>) m.get("midInfo");

                List<Map<Boolean, String>> mapList = new ArrayList<>();

                for (String s : midInfos) {
                    Map<Boolean, String> map = new HashMap();
                    map.put(true, s);
                    mapList.add(map);
                }

                for (int i = 0; i < mapList.size() - 1; i++) {
                    Map<Boolean, String> m1 = mapList.get(i);
                    for (int j = i + 1; j < mapList.size(); j++) {
                        Map<Boolean, String> m2 = mapList.get(j);

                        if (m1.containsKey(true) && m2.containsKey(true)) {
                            int dis = compare(m1.get(true), m2.get(true));
                            if (dis < 3) {
                                m2.put(false, m2.remove(true));
                            }
                        }
                    }
                }

                Set<String> result = new HashSet<>();

                for (int i = 0; i < mapList.size(); i++) {
                    Map mm = mapList.get(i);
                    if (mm.containsKey(true)) {
                        result.add(mapList.get(i).get(true));
                    }
                }

                m.put("midInfo", new ArrayList<>(result));

                all.add(JSONObject.toJSONString(m));

                FileWrite.writeDataToFile(path + "_clean", all);
                all.clear();

                total += result.size();

                System.out.println((count++) + ". 当前句子总数: " + result.size() + "; 总数: " + (total));
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 建立所有句子的hash,simhash相似的句子放在一起
     */
    public static void produceSimHash(String path,String targetPath) {
        Map<String, List<String>> mapList = new HashMap<>();
        try (FileReader fileReader = new FileReader(path)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            List<String> hashList = null;
            Set<String> set = new HashSet<>();
            int count = 0;
            while ((line = bufferedReader.readLine()) != null) {
                Map m = JSONObject.parseObject(line, Map.class);
                String info = m.get("sen").toString();
                String sid = m.get("sid").toString();
//                int index = info.indexOf("(:");
//                info = info.substring(0, index);
                h1.simHash(info, 64);
                String hash = h1.getStrSimHash();
                if (mapList.containsKey(hash)) {
                    hashList = mapList.get(hash);
                    hashList.add(sid);
                } else {
                    hashList = new ArrayList<>();
                    hashList.add(sid);
                }
                mapList.put(hash, hashList);
                System.out.println((++count) + ". hash:" + hash + "; list: " + JSONObject.toJSONString(hashList));
            }

            System.out.println("map大小: "+mapList.size());
            Thread.sleep(1000);
            List<String> all = new ArrayList<>();
            int num = 0;
            for (Map.Entry<String, List<String>> entry : mapList.entrySet()) {
                Map<String, Object> m = new HashMap<>();
                String key = entry.getKey();
                List<String> value = entry.getValue();
                num = num + value.size() - 1;

                m.put("hash", key);
                m.put("value", JSONObject.toJSONString(value));
                all.add(JSONObject.toJSONString(m));

                if (all.size() >= 1000) {
                    System.out.println("总数: " + num);
                    FileWrite.writeDataToFile(targetPath, all);
                    all.clear();
                }
            }

            if (all.size() > 0) {
                FileWrite.writeDataToFile(targetPath, all);
                all.clear();
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 根据simhash比较两个simHash的距离
     */
    public static void compareSimHash(String path) {
        List<String> list = FileRead.readDataFromFile(path);
        List<String> hashList = new ArrayList<>();
        for (String s : list) {
            Map m = JSONObject.parseObject(s, Map.class);
            hashList.add(m.get("hash").toString());
        }


        Set<String> set = new HashSet<>();
        Map<String, List<String>> mapList = new HashMap<>();
        for (int i = 0; i < hashList.size() - 1; i++) {
            String str1 = hashList.get(i).toString();
            List<String> valueList = new ArrayList<>();
            for (int j = i + 1; j < hashList.size(); j++) {
                String str2 = hashList.get(j).toString();
                int dis = simHashTest.getDistance(str1, str2);
                if (dis < 3) {
                    valueList.add(str2);
                }
            }
            mapList.put(str1, valueList);
            System.out.println(i + ":" + valueList.size());
        }

        Iterator iterator = mapList.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            List<String> sonList = mapList.get(key);
            getAllSim(sonList, set, mapList);
        }


        List<String> finalList = new ArrayList<>();
        iterator = mapList.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!set.contains(key)) {
                finalList.add(key);
            }
        }

        FileWrite.writeDataToFile(path + "removeSim", finalList);

    }


    public static void getAllSim(List<String> list, Set<String> set, Map<String, List<String>> mapList) {

        if (list != null) {
            for (String s : list) {
                try {
                    set.add(s);

                    String key = mapList.get(s).toString();
                    List<String> sonList = mapList.get(key);
                    getAllSim(sonList, set, mapList);
                }catch (Exception e){
                    System.out.println(s+": " + e.getMessage());
                }
            }
        }
    }


    public static void getFinalResult(String path, String idPath) {
        List<String> list = FileRead.readDataFromFile(path);
        List<String> idList = FileRead.readDataFromFile(idPath);

        Map<String, List<String>> map = new HashMap();

        for (String id : idList) {
            Map m = JSONObject.parseObject(id, Map.class);
            String key = m.get("hash").toString();
            String value = m.get("value").toString();
            List<String> ids = JSONObject.parseObject(value, List.class);

            map.put(key, ids);
        }

        List<String> allIds = new ArrayList<>();
        for (String s : list) {
            List<String> ids = map.get(s);
            allIds.add(ids.get(0).toString());
        }

        FileWrite.writeDataToFile("/home/benhairui/Documents/es_data/depFile/hashResult_ids", allIds);

    }


    /**
     * 根据id拿到所有句子
     */
    public static void getSenWithid(String idPath,String path,String targetPath) {
        List<String> ids = FileRead.readDataFromFile(idPath);
        Set<String> set = new HashSet<>(ids);

        List<String> senList = new ArrayList<>();
        try (FileReader fileReader = new FileReader(path)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            List<String> hashList = null;
            int count = 0;
            int num = 0;
            while ((line = bufferedReader.readLine()) != null) {
                Map m = JSONObject.parseObject(line,Map.class);
                String sid = m.get("sid").toString();
                if(set.contains(sid)){
                    senList.add(line);
                    ++count;
                    System.out.println(line);
                }

                if(senList.size()>=1000){
                    FileWrite.writeDataToFile(targetPath,senList);
                    senList.clear();
                }
                ++num;

                System.out.println(num+": " + count+": ");

            }

            if(senList.size()> 0){
                FileWrite.writeDataToFile(targetPath,senList);
                senList.clear();
            }
        }catch (Exception e){
            System.out.println("no find: " + e.getMessage());
        }
    }

    /**
     * 建立唯一标识
     */
    public static void produceId(){
        String path = "/home/benhairui/Documents/es_data/es_token_single";
        List<String> list = FileRead.readDataFromFile(path);
        List<String> ids = new ArrayList<>();
        int count = 0;
        for(String s:list){
            Map m = JSONObject.parseObject(s,Map.class);
            ++count;
            m.put("sid","s"+count);
            ids.add(JSONObject.toJSONString(m));
        }

        FileWrite.writeDataToFile(path+"_id",ids);

    }

    public static void getDepFiles(){
        String path = "/home/benhairui/Documents/es_data/result2/remove/finalSen1";

        List<String> list = FileRead.readDataFromFile(path);

        List<String> depList = new ArrayList<>();

        int count = 0;
        for(String s: list){
            Map map = JSONObject.parseObject(s,Map.class);
            String midInfo = map.get("midInfo").toString();
            String kw = map.get("kw").toString();

            if(midInfo.contains(kw) && (midInfo.contains("中游")||midInfo.contains("上游")||midInfo.contains("下游"))){
                depList.add(s);
            }
        }
        FileWrite.writeDataToFile(path+"_depFile",depList);
    }


    public static void main(String[] args) {
//        String path = "/home/benhairui/Documents/es_data/result2/distributeNode2";

//        removeSame(path);

//        String path = "/home/benhairui/Documents/es_data/result2/distributeNode2_clean_split";
//        produceSimHash(path);

//        String path = "/home/benhairui/Documents/es_data/result2/remove/hashResult";
//        compareSimHash(path);
//        String idPath = "/home/benhairui/Documents/es_data/result2/remove/hashResult";
//        String path1 = "/home/benhairui/Documents/es_data/result2/remove/hashResultremoveSim";
//        getFinalResult(path1, idPath);

//        String idPath = "/home/benhairui/Documents/es_data/result2/remove/hashResult_ids";
//        String path = "/home/benhairui/Documents/es_data/result2/consumer_modify/es_splitFile_senStructure_Consumer";
//        String targetPath = "/home/benhairui/Documents/es_data/result2/remove/finalSen1";
//
//        for(int i = 0;i<=5;i++) {
//
//            getSenWithid(idPath, path+i, targetPath);
//
//        }


//        produceId();
//
//        String path = "/home/benhairui/Documents/es_data/es_token_single_id";
//        String targetPath = "/home/benhairui/Documents/es_data/depFile/hashResult";
//        produceSimHash(path,targetPath);
//        String path1 = "/home/benhairui/Documents/es_data/depFile/hashResult";
//        compareSimHash(path1);
//        String idPath = "/home/benhairui/Documents/es_data/depFile/hashResult";
//        String path2 = "/home/benhairui/Documents/es_data/depFile/hashResultremoveSim";
//        getFinalResult(path2, idPath);
//
//        String idPath = "/home/benhairui/Documents/es_data/depFile/hashResult_ids";
//        String path = "/home/benhairui/Documents/es_data/es_token_single_id";
//        String targetPath = "/home/benhairui/Documents/es_data/depFile/finalSen1";
//
//        getSenWithid(idPath,path,targetPath);
        getDepFiles();
    }
}
