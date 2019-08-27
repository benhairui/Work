package com.ic.Format;

import com.alibaba.fastjson.JSONObject;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import com.ic.constant.Tools;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.net.URLEncoder;
import java.util.*;

public class NodeChainFormat {


    public static List<String> readChainNodeFromFile() {
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
        List<Map> nodeList = new ArrayList<>();
        //取出所有节点的中上下游map
        for (Map map : mapList) {
            Map icBody = (Map) map.get("industryChainBody");
            if (icBody.containsKey("leftRegion")) {
                Map leftRegion = (Map) icBody.get("leftRegion");
                if (leftRegion.containsKey("nodeList")) {
                    List<Map> leftRegionList = (List<Map>) leftRegion.get("nodeList");
                    nodeList.addAll(leftRegionList);
                }
            }
            if (icBody.containsKey("upRegion")) {
                Map leftRegion = (Map) icBody.get("upRegion");
                if (leftRegion.containsKey("nodeList")) {
                    List<Map> leftRegionList = (List<Map>) leftRegion.get("nodeList");
                    nodeList.addAll(leftRegionList);
                }
            }
            if (icBody.containsKey("downRegion")) {
                Map leftRegion = (Map) icBody.get("downRegion");
                if (leftRegion.containsKey("nodeList")) {
                    List<Map> leftRegionList = (List<Map>) leftRegion.get("nodeList");
                    nodeList.addAll(leftRegionList);
                }
            }
        }
        for (Map map : nodeList) {
            String name = map.get("name").toString();
            String uuid = map.get("uuid").toString();
            kwList.add(uuid + "::" + name);
            getNode(map, kwList);
        }
        System.out.println("节点总数: " + kwList.size());


        FileWrite.writeDataToFile("/home/benhairui/Documents/gitlab-workspace/Work/chain/kw", kwList);

        return kwList;
    }

    public static void getNode(Map<String, Object> map, List<String> kwList) {

        if (map.containsKey("nextNodeList")) {
            List<Map> list = (List<Map>) map.get("nextNodeList");
            for (Map node : list) {
                String name = map.get("name").toString();
                String uuid = map.get("uuid").toString();
                kwList.add(uuid + "::" + name);
                getNode(node, kwList);
            }
        }
    }


    public static void getDataFromHtml(List<String> kwList) throws Exception {

        CookieStore cookieStore = new BasicCookieStore();
        DefaultHttpClient client = new DefaultHttpClient();
        client.setCookieStore(cookieStore);

        HttpGet get = new HttpGet("https://www.sogou.com/web?query=hello");

        HttpResponse response = client.execute(get);

        DefaultHttpClient client1 = new DefaultHttpClient();
        client1.setCookieStore(client.getCookieStore());

        HttpGet get1 = null;

        HttpResponse response1 = null;

        int count = 0;
        ArrayList<String> htmlList = new ArrayList<>();
        ArrayList<String> haveSaved = new ArrayList<>();
        while (count < kwList.size()) {
            String kw = kwList.get(count).toString();
            String[] array = kw.split("_");
            StringBuffer url = new StringBuffer("https://www.sogou.com/web?query=");
            StringBuffer finalKw = new StringBuffer("");
            finalKw.append("\"").append(array[1]).append("\" ").append("\"区块链\"");

            StringBuffer result = url.append(URLEncoder.encode(finalKw.toString()));

            get1 = new HttpGet(result.toString());
            System.out.println(get1.getURI());
            response1 = client1.execute(get1);

            Map<String, String> map = new HashMap();
            map.put("kw", kw);
            map.put("html", EntityUtils.toString(response1.getEntity()));

            htmlList.add(JSONObject.toJSONString(map));
            System.out.println(kw + ": " + count);
            ++count;


            CookieStore c = client.getCookieStore();

            for (Cookie cookie : c.getCookies()) {
                System.out.println(cookie.getName() + ":" + cookie.getValue());
            }

            if (htmlList.size() >= 100) {
                FileWrite.writeDataToFile("/home/benhairui/Documents/gitlab-workspace/Work/chain/html", htmlList);
                htmlList.clear();
            }

            haveSaved.add(kw);
        }

        if (htmlList.size() >= 0) {
            FileWrite.writeDataToFile("/home/benhairui/Documents/gitlab-workspace/Work/chain/html", htmlList);
            htmlList.clear();
        }
    }


    public static void getSearchNumOfKw(String path) {

        File file = new File(path);
        File[] listDirs = file.listFiles();

        Map<String, Integer> map = new HashMap<>();

        List<String> errorList = new ArrayList<>();

        int count = 0;
        for (File f : listDirs) {
            String absolutePath = f.getAbsolutePath(); //拿到文件路径
            List<String> list = FileRead.readDataFromFile(absolutePath);


            StringBuffer buffer = new StringBuffer();
            for (String s : list) {
                buffer.append(s).append("\t");
            }
            String str = buffer.toString();

            String title = Tools.getTitle(str);

            String num = Tools.getStasticsOfSogou(str);
            System.out.println(num);
            if (num == null || title == null) {
                System.out.println(absolutePath);
                errorList.add(str);
            } else {
                map.put(title, Integer.parseInt(num));
            }

            System.out.println(++count + ". " + title + ":" + num);
        }


        List<Map.Entry<String, Integer>> hashList = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
        Collections.sort(hashList, new Comparator<Map.Entry<String, Integer>>() {
            // 降序排序
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });

        System.out.println(hashList.size());

        List<String> result = new ArrayList<>();

        // 排序后输出
        for (Map.Entry<String, Integer> m : hashList) {
            System.out.println("Key=" + m.getKey() + ", Value=" + m.getValue());

            result.add(m.getKey()+":" + m.getValue());
        }

        System.out.println(errorList.size());

        FileWrite.writeDataToFile("/home/benhairui/Downloads/pages/result",result);
    }

    /**
     * 过滤掉中心节点
     */
    public static void filterCenterNode(){
        List<String> kwWords = KwFormat.getKeyword();
        HashSet<String> set = new HashSet<>(kwWords);
        List<String> sta = FileRead.readDataFromFile("/home/benhairui/Downloads/pages/result");

        List<String> stringArrayList = new ArrayList<>();
        System.out.println(sta.size());
        int i = 0;
        for(String s : sta){
            String[] array = s.split(":");
            String str = array[0].substring(0,array[0].length()-4);
            if(!set.contains(str)){
                stringArrayList.add(s);
                System.out.println(stringArrayList.size() + ": " + (++i));
            }
        }


        System.out.println(stringArrayList.size());

        FileWrite.writeDataToFile("/home/benhairui/Downloads/pages/result_filter",stringArrayList);
    }


    /**
     * 扩展产业链中心节点，首先获得种子节点
     */
    public static void getChainWord(){
        List<String> originList = KwFormat.getKeyword();
        List<String> NodeList = FileRead.readDataFromFile("/home/benhairui/Downloads/pages/result_filter");

        List<String> list = new ArrayList<>();
        for(String str : NodeList){
            String[] array = str.split(":");
            if(array.length>1 && Integer.parseInt(array[1])>0){
                list.add(array[0]);
            }
        }

        for(String str : originList){
            list.add(str+" 产业链");
        }


        FileWrite.writeDataToFile("/home/benhairui/Downloads/pages/chain_node_result",list);

    }

    public static void filterChainNode(){
        List<String> allChainNode = FileRead.readDataFromFile("/home/benhairui/Downloads/pages/all_chain_node_clean");
        List<String> kwList = KwFormat.getKeyword();
        List<String> result = new ArrayList<>();

        for(String s : allChainNode){
            if(!kwList.contains(s)){
                result.add(s);
            }
        }

        FileWrite.writeDataToFile("/home/benhairui/Downloads/pages/leftNodeExceptCenter",result);
        System.out.println("写入结束!");

    }


    public static void main(String[] args) throws Exception {
        String path = "/home/benhairui/Downloads/pages/page";
//        getSearchNumOfKw(path);

//        filterCenterNode();
//        getChainWord();
        filterChainNode();
    }
}
