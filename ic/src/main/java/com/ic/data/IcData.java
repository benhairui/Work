package com.ic.data;

import com.alibaba.fastjson.JSONObject;
import com.ic.connect.ES;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.*;

public class IcData {

    public static void formatSentence(String path){
        List<String> list = FileRead.readDataFromFile(path);
        int count = 0;
        List<String> depList = new ArrayList<>();
        List<String> allList = new ArrayList<>();
        for(int i = 0;i<list.size();i++){
            String str = list.get(i).toString();
            Map map = JSONObject.parseObject(str,Map.class);
            map.put("id",String.valueOf(i));

            String kw = map.get("kw").toString().substring(26);
            String[] array = kw.split("_");

            map.put("kw",array[0]);
            map.put("level",array[1]);

            String midInfo = map.get("midInfo").toString();
            if(midInfo.contains(array[0]) && midInfo.contains(array[1])){
                map.put("category","dep");
                depList.add(JSONObject.toJSONString(map));
            }else if(midInfo.contains("、")){
                map.put("category","punc");
            }else{
                map.put("category","nonDep");
            }
            map.put("isFinStr","false");

            allList.add(JSONObject.toJSONString(map));
        }
        FileWrite.writeDataToFile("/home/benhairui/Documents/gitlab-workspace/Work/third_data/result2/splitFile_dep",depList);

        FileWrite.writeDataToFile("/home/benhairui/Documents/gitlab-workspace/Work/third_data/result2/splitFile_all",allList);

    }

    /**
     * 对句子进行格式化处理
     * @param client
     * @param path
     */
    public static void insertDataToEs(TransportClient client,String path){

        List<String> list = FileRead.readDataFromFile(path);

        BulkRequestBuilder requestBuilder = client.prepareBulk();
        BulkResponse response = null;

        int count = 0;
        int num = 0;
        System.out.println(list.size());
        Set<String> set = new HashSet<>();
        for(int i = 0;i<list.size();i++){
            String str = list.get(i).toString();
            Map map = JSONObject.parseObject(str,Map.class);

            if(!map.containsKey("new")){
                System.out.println(++num);
            }

            String midInfo = map.get("midInfo").toString();
            String kw = map.get("kw").toString();
            if(midInfo.contains("、")){
                map.put("category","punc");
            }else if (midInfo.contains(kw) && (midInfo.contains("中游")||midInfo.contains("上游")||midInfo.contains("下游"))){
                map.put("category", "dep");
            }else{
                map.put("category", "nonDep");
            }

            IndexRequest request = new IndexRequest("nuozhi", "sen", map.get("sid").toString()).source(map); //用url的hashCode作为es的唯一主键
            requestBuilder.add(request);

            if(requestBuilder.numberOfActions()>=10){
                response = requestBuilder.get();
                System.out.println(response.hasFailures() + " 次数: " + (++count));
                requestBuilder = client.prepareBulk();
            }
        }
        if(requestBuilder.numberOfActions()> 0){
            response = requestBuilder.get();
            System.out.println(response.hasFailures());
        }
        System.out.println("插入结束!");
    }

    /**
     * 插入依存句法分析的句子
     * @param client
     * @param path
     */
    public static void updateDepDataToEs(  TransportClient client,String path){

        List<String> list = FileRead.readDataFromFile(path);

        BulkRequestBuilder requestBuilder = client.prepareBulk();
        BulkResponse response = null;

        int count = 0;
        List<String> depList = new ArrayList<>();
        for(int i = 0;i<list.size();i++){
            String str = list.get(i).toString();
            Map map = JSONObject.parseObject(str,Map.class);


            map.put("cand_word",map.remove("node"));
            map.put("isFinStr","false");
            map.put("category","dep");


            UpdateRequest updateRequest = new UpdateRequest("nuozhi","sen",map.get("sid").toString()).doc(map);
            requestBuilder.add(updateRequest);
//            IndexRequest request = new IndexRequest("ic", "sen", map.get("id").toString()).source(map); //用url的hashCode作为es的唯一主键
//            requestBuilder.add(request);

            if(requestBuilder.numberOfActions()>=10){
                response = requestBuilder.get();
                System.out.println(response.hasFailures() + " 次数: " + (++count));
                requestBuilder = client.prepareBulk();
            }
        }
        if(requestBuilder.numberOfActions()> 0){
            response = requestBuilder.get();
            System.out.println(response.hasFailures());
        }

//        FileWrite.writeDataToFile("/home/benhairui/Documents/gitlab-workspace/Work/third_data/result2/splitFile_dep",depList);

    }

    public static void getDataFromES(TransportClient client,String path){
        List<String> list = new ArrayList<>();
        try{
            MatchAllQueryBuilder maQuery = QueryBuilders.matchAllQuery();
            SearchResponse response = client.prepareSearch("ic","sen").setScroll(new TimeValue(10000)).setSize(100).setQuery(maQuery).get();

            SearchHit[] searchHits = response.getHits().getHits();

            while(searchHits.length>0) {
                for (SearchHit hit : searchHits) {
                    Map m = hit.getSourceAsMap();
                    list.add(JSONObject.toJSONString(m));
                }

                response = client.prepareSearchScroll(response.getScrollId()).get();
            }
            FileWrite.writeDataToFile(path,list);
        }catch (Exception e){
            System.out.println("报错了: " + e.getMessage());
        }
    }


    public static void main(String[] args) throws Exception{
        TransportClient client = ES.getSingleClient();
//        String path = "/home/benhairui/Documents/gitlab-workspace/Work/third_data/result2/splitFile_result"; //splitFile_result_senStructure

//        String path = "/home/benhairui/Documents/es_data/result2/remove/finalSen1";
//        insertDataToEs(client,path);

        String path = "/home/benhairui/Documents/es_data/result2/remove/depFileNode";
        updateDepDataToEs(client,path);


    }

}
