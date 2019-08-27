package com.ic.Crawls.ChainExtend;

import com.alibaba.fastjson.JSONObject;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;

import java.util.*;

public class ExtendFormat {

    public static void getStas(){
        String path = "/home/benhairui/Downloads/pages/first_crawl_chain_node_result";
        List<String> list = FileRead.readDataFromFile(path);
        List<String> result = new ArrayList<>();
        Map<String,Integer> m = new HashMap<>();
        int num = 0;
        try {
            for (String s : list) {
                Map<String, Object> map = JSONObject.parseObject(s, java.util.Map.class);

                Iterator iterator = map.keySet().iterator();
                if (iterator.hasNext()) {
                    String key = iterator.next().toString();
                    List<String> kwList = (List<String>) map.get(key);
                    for (String str : kwList) {
                        if (m.containsKey(str)) {
                            int count = m.get(str);
                            m.put(str, ++count);
                        } else {
                            m.put(str, 1);
                        }
                    }
                }
                System.out.println((++num) + ":" + s + ":" + m.size());
            }
        }catch (Exception e){

        }

        List<Map.Entry<String, Integer>> hashList = new ArrayList<Map.Entry<String, Integer>>(m.entrySet());
        Collections.sort(hashList, new Comparator<Map.Entry<String, Integer>>() {
            // 降序排序
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        System.out.println(hashList.size());



        // 排序后输出
        for (Map.Entry<String, Integer> m1 : hashList) {
            System.out.println("Key=" + m1.getKey() + ", Value=" + m1.getValue());
            String str = m1.getKey();
            if(m1.getKey().contains("全产业链")){
                str = m1.getKey().replace("全","");
            }

            result.add(str+":" + m1.getValue());
        }

        FileWrite.writeDataToFile("/home/benhairui/Downloads/pages/sta4",result);

    }
    public static void main(String[] args){
        getStas();
    }
}
