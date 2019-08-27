package com.ic;

import com.alibaba.fastjson.JSONObject;
import com.ic.Format.KwFormat;
import com.ic.connect.ES;
import com.ic.constant.FileRead;
import com.ic.constant.FileWrite;
import org.apache.xerces.impl.xpath.regex.Match;
import org.apache.xpath.operations.Bool;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.*;

public class NuoZhi {

    private static Logger logger = LoggerFactory.getLogger(NuoZhi.class);

    /**
     * 获得es的单例对象
     *
     * @return
     * @throws Exception
     */
    public static TransportClient getSingleClient() throws Exception {

        TransportClient client = null;
        Settings settings = Settings.builder()
                .put("xpack.security.user", "shuzhi:shuzhi2018")
                .put("client.transport.sniff", false)
                .put("cluster.name", "ace").build();
//       client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("elastic.finance.d2k.io"), 9300));

        client = new PreBuiltXPackTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("elastic.finance.d2k.io"), 9300));


        return client;
    }

    public static BoolQueryBuilder matchPhraze(String kw) {
        MatchPhraseQueryBuilder mpQueryBuilder = QueryBuilders.matchPhraseQuery("content.text", kw);
        MatchPhraseQueryBuilder upQueryBuilder = QueryBuilders.matchPhraseQuery("content.text", "上游");
        MatchPhraseQueryBuilder midQueryBuilder = QueryBuilders.matchPhraseQuery("content.text", "中游");
        MatchPhraseQueryBuilder downQueryBuilder = QueryBuilders.matchPhraseQuery("content.text", "下游");

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        boolQueryBuilder.should(upQueryBuilder).should(midQueryBuilder).should(downQueryBuilder);

        BoolQueryBuilder allQueryBuilder = QueryBuilders.boolQuery();
        allQueryBuilder.must(mpQueryBuilder).must(boolQueryBuilder);

        return allQueryBuilder;

    }

    /**
     * announcementTitle为资产评估报告
     * @param kw
     * @return
     */
    public static BoolQueryBuilder getMpQuery(String kw) {
        MatchQueryBuilder mQuery = QueryBuilders.matchQuery("announcementTitle", kw);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(mQuery);

        return boolQueryBuilder;

    }

    /**
     * content.text包含资产评估报告和摘要
     * @param kw
     * @return
     */
    public static BoolQueryBuilder getMQuery(String kw){
        MatchPhraseQueryBuilder mQuery = QueryBuilders.matchPhraseQuery("content.text", "资产评估报告");
        MatchPhraseQueryBuilder mQuery2 = QueryBuilders.matchPhraseQuery("content.text", "摘要");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(mQuery).must(mQuery2);
        return boolQueryBuilder;
    }

    public static BoolQueryBuilder queryById(String id){
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("_id",id);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(matchQueryBuilder);
        return boolQueryBuilder;
    }


    public static void getData() {
        boolean flag = false;
        while (true) {
//            String savePath = "/home/benhairui/Documents/es_data/have_save";
//
//            List<String> haveSaved = FileRead.readDataFromFile(savePath);
//            HashSet<String> set = new HashSet<>(haveSaved);
//            System.out.println("已经保存的数量: " + haveSaved.size());
//
//            ArrayList<String> idList = new ArrayList<>();
//            List<String> kwList = getHaveSave();
//
//            String[] sources = {"content.text"};
//            List<String> list = new ArrayList<>();
//            String filePath = "/home/benhairui/Documents/es_data/zw_es2";
//            List<String> haveSaved = FileRead.readDataFromFile(savePath);
//            HashSet<String> set = new HashSet<>(haveSaved);
//            System.out.println("已经保存的数量: " + haveSaved.size());

            String savePath = "/home/benhairui/Documents/es_data/finance/have_save_url";
            List<String> haveSaved = FileRead.readDataFromFile(savePath);
            HashSet<String> set = new HashSet<>(haveSaved);

            ArrayList<String> idList = new ArrayList<>();

            String filePath = "/home/benhairui/Documents/es_data/finance/data_abs_urlAndPage";

            List<String> list = new ArrayList<>();

            List<String> kwList = FileRead.readDataFromFile("/home/benhairui/Documents/es_data/finance/article_url");

            TimeValue timeValue = new TimeValue(30000000);
            String[] sources = {"adjunctUrl", "announcementTypeName", "announcementTime", "secName", "content.text", "announcementTitle"};

            int count = 0;
            int num = 0;
            TransportClient client = null;

            try {
                client = getSingleClient();
                String scrollId = "";
                for (String s : kwList) {
                    ++num;
                    count = 0;
//                    SearchResponse response = client.prepareSearch("disclosure_v17").setQuery(matchPhraze(s)).setSize(10).setScroll(timeValue).setFetchSource(sources, null).get();
                    SearchResponse response = client.prepareSearch("disclosure_v17").setQuery(queryById(s)).setSize(10).setScroll(timeValue).get();
                    SearchHit[] searchHits = response.getHits().getHits();
                    logger.info("当前关键词总数: {},{},{}", s, response.getHits().getTotalHits(), num);

                    while (searchHits.length > 0) {
                        logger.info("当前抓取轮次: {}, {}", s, (++count), num);
                        for (SearchHit hit : searchHits) {
                            String id = hit.getId();
                            String key = s + "_" + id;
                            if (set.contains(key)) { //由于量太大,所以先只取id,如果能检测出不包含,那么改变source为content.text,取全文字段
                                continue;
                            }
                            StringBuffer buffer = new StringBuffer();
                            Map<String, Object> sourceMap = hit.getSourceAsMap();
                            if (sourceMap.containsKey("content")) {
                                List<Map> contents = (List<Map>) sourceMap.get("content");
                                if (contents == null) {
                                    continue;
                                }
//                                for (Map cont : contents) {
//                                    buffer.append(cont.get("text").toString());
//                                }
//                                Map map = new HashMap();
//                                map.put("content", buffer.toString());
//                                map.put("kw", s);
//                                sourceMap.put("content", buffer.toString());
                                list.add(JSONObject.toJSONString(sourceMap));

                                if (list.size() >= 10) {
                                    FileWrite.writeDataToFile(filePath, list);
                                    list.clear();
                                }
                                set.add(key);
                                idList.add(key);
                                if (idList.size() >= 10) {
                                    FileWrite.writeDataToFile(savePath, idList);
                                    idList.clear();
                                }
                            }
                        }
                        scrollId = response.getScrollId();
                        response = client.prepareSearchScroll(scrollId).setScroll(timeValue).get();
                        searchHits = response.getHits().getHits();
                    }
                    if (list.size() > 0) {
                        FileWrite.writeDataToFile(filePath, list);
                        list.clear();
                    }
                    if (idList.size() > 0) {
                        FileWrite.writeDataToFile(savePath, idList);
                        idList.clear();
                    }
                }
                flag = true; //如果所有关键词抓取完毕，那么flag设置为true
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("报错了: " + e.getMessage());
            } finally {
                FileWrite.writeDataToFile(savePath, new ArrayList<String>(set));
            }
            if (flag) { //如果flag=true，那么跳出循环
                break;//跳出循环
            }
        }
    }

    public static List<String> getHaveSave() {
        String path = "/home/benhairui/Documents/es_data/have_save";
        List<String> idList = FileRead.readDataFromFile(path);

        List<String> list = KwFormat.getKeyword();

        Set<String> filterList = new HashSet<>();
        for (String key : idList) {
            String[] array = key.split("_");
            filterList.add(array[0]);
        }

        System.out.println(list.size() + "," + filterList.size());
        List<String> yuList = new ArrayList<>();
        for (String keyOrigin : list) {
            if (!filterList.contains(keyOrigin)) {
                yuList.add(keyOrigin);
            }
        }
        System.out.println(yuList);
        return yuList;
    }

    public static void main(String[] args) throws Exception {
//        getData();
//        getHaveSave();

        String envlang = System.getProperty("user.language");
        System.out.println(envlang);

    }


}














































