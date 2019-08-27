package com.ic.test;

import com.alibaba.fastjson.JSONObject;
import com.ic.connect.ES;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


public class ES_Chain {

    private static Integer start = -1;

    private static Integer preStart = 0;

    private static Map<String, Integer> map = new HashMap<>();

    private static Map<String, Object> userMap = new HashMap<>();

    public static String category = "punc";

    private Logger logger = LoggerFactory.getLogger(ES_Chain.class);

    /**
     * 判断用户信息是否存在
     *
     * @param userName
     * @param passWord
     * @return
     */
    public boolean judgeUserInfo(String userName, String passWord) {

        try {
            TransportClient client = ES.getSingleClient();

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            MatchPhraseQueryBuilder mpuQuery = QueryBuilders.matchPhraseQuery("un", userName);
            MatchPhraseQueryBuilder mppQuery = QueryBuilders.matchPhraseQuery("ps", passWord);
            boolQueryBuilder.must(mppQuery).must(mpuQuery);


            SearchResponse response = client.prepareSearch(ES_Enum.USER_INDEX.getName()).setQuery(boolQueryBuilder).setSize(1).get();
            if (response.getHits().getHits().length > 0) {

                userMap.put(userName, "false");

                return true;
            }

        } catch (Exception e) {
            logger.info("查询出错: {}", e.getMessage());
        }
        return false;
    }

    public synchronized SenInfo getNextInfo(String userName, String id) {

        SenInfo senInfo = null;

        logger.info("开始查下一条: ");

        try {
            if (userMap.containsKey(userName)) {
                String value = userMap.get(userName).toString();
                if (value == "false") {
                    senInfo = getNextInfoWithSize(userName);
                    // userMap.put(userName, "false" + "_" + id);//更改值
                } else if (value != null) {
                    String[] array = value.split("_");
                    if (array.length == 2) {
                        if (array[0].equals("true")) { //直接获取下一个,并记录下一个的id
                            senInfo = getNextInfoWithSize(userName);
                        } else {//还是展示当前id
                            senInfo = getSenInfoWithId(id);
                        }
                    } else {
                        logger.info("用户信息出错: {}", userName);
                    }
                } else {
                    logger.info("用户信息出错: {}", userName);
                }
            }

        } catch (Exception e) {
            logger.info("查询下一个出错: {}",e.getMessage());
        }

        return senInfo;
    }

    public SenInfo getSenInfoWithId(String id) {
        try {
            TransportClient client = ES.getSingleClient();

            MatchQueryBuilder queryBuilder = QueryBuilders.matchQuery("id", id);
            SearchResponse response = null;
            response = client.prepareSearch(ES_Enum.SEARCH_INDEX.getName()).setQuery(queryBuilder).setSize(1).get();

            if (response.getHits().getHits().length > 0) {
                SearchHit hit = response.getHits().getHits()[0];
                Map map = hit.getSourceAsMap();
                SenInfo senInfo = new SenInfo();
                senInfo.setCand_word(getCandWord(map));
                senInfo.setCand_word_str(JSONObject.toJSONString(getCandWord(map)));
                senInfo.setCategory(map.get("category") != null ? map.get("category").toString() : null);
                senInfo.setId(id);
                senInfo.setKw(map.get("kw").toString());
                senInfo.setMidInfo(map.get("midInfo").toString());
                senInfo.setLevel(map.get("level").toString());
                senInfo.setUrl(map.get("url").toString());
                senInfo.setIsFinStr(map.get("isFinStr").toString());
                System.out.println("id结果: "+JSONObject.toJSONString(senInfo));
                return senInfo;
            }
        } catch (Exception e) {
            logger.info("获取当前id出错: {}, 信息: {}", id, e.getMessage());
        }
        return null;
    }

    public SenInfo getNextInfoWithSize(String author) {
        ++start;
        try {
            TransportClient client = ES.getSingleClient();

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            MatchPhraseQueryBuilder mpuQuery = QueryBuilders.matchPhraseQuery("isFinStr", "false");
            MatchPhraseQueryBuilder mppQuery = QueryBuilders.matchPhraseQuery("category", category);
            boolQueryBuilder.must(mpuQuery).must(mppQuery);

            SearchResponse response = null;
            response = client.prepareSearch(ES_Enum.SEARCH_INDEX.getName()).setQuery(boolQueryBuilder).setFrom(start).setSize(1).get();
            if (category.equals("punc")) {
                if (start == 3) {
                    category = "dep";
                    start = -1;
                }
            } else if (category.equals("dep")) {
                if (start == 2) {
                    start = -1;
                    category = "nonDep";
                }
            }

            if (response.getHits().getHits().length > 0) {
                SearchHit hit = response.getHits().getHits()[0];
                Map map = hit.getSourceAsMap();
                String id = hit.getId();
                SenInfo senInfo = new SenInfo();
                senInfo.setCand_word(getCandWord(map));
                senInfo.setCand_word_str(JSONObject.toJSONString(getCandWord(map)));
                senInfo.setCategory(map.get("category") != null ? map.get("category").toString() : null);
                senInfo.setId(id);
                senInfo.setKw(map.get("kw").toString());
                senInfo.setMidInfo(map.get("midInfo").toString());
                senInfo.setLevel(map.get("level").toString());
                senInfo.setUrl(map.get("url").toString());
                senInfo.setIsFinStr(map.get("isFinStr").toString());

                System.out.println(JSONObject.toJSONString(senInfo));


                String info = "false_" + senInfo.getId();
                userMap.put(author, info);


                return senInfo;
            }
        } catch (Exception e) {
            logger.info("查询出错: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获得候选词的集合
     *
     * @param map
     * @return
     */
    public List<String> getCandWord(Map map) {
        List<String> all = new ArrayList<>();
        StringBuffer str = new StringBuffer("");
        List<String> list = null;
        List<String> list_new = null;
        if (map.containsKey("cand_word")) {
            list = (List<String>) map.get("cand_word");
        }
        if (map.containsKey("new")) {
            list_new = (List<String>) map.get("new");
        }
        if (list != null) {

            all.addAll(list);
        }
        if (list_new != null) {
            all.addAll(list_new);
        }
        return all;
    }

    /**
     * 暂时不做这个功能
     *
     * @param userName
     * @return
     */
    @Deprecated
    public SenInfo getPreInfo(String userName) {
        int preStart = map.get(userName);
        ++preStart;
        try {
            TransportClient client = ES.getSingleClient();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            MatchPhraseQueryBuilder mpfQuery = QueryBuilders.matchPhraseQuery("isFinStr", "true");
            MatchPhraseQueryBuilder mpuQuery = QueryBuilders.matchPhraseQuery("author", userName);

            boolQueryBuilder.must(mpfQuery).must(mpuQuery);
            /**
             * 按日期降序，拿到从from开始
             */
            SearchResponse response = client.prepareSearch(ES_Enum.SEARCH_INDEX.getName()).setQuery(boolQueryBuilder).addSort("submit_time", SortOrder.DESC).setFrom(preStart).setSize(1).get();

            if (response.getHits().getHits().length > 0) {
                map.put(userName, preStart);

                Map map = response.getHits().getHits()[0].getSourceAsMap();
                SenInfo senInfo = new SenInfo();
                senInfo.setCand_word((List<String>) map.get("cand_word"));
                senInfo.setCategory(map.get("category") != null ? map.get("category").toString() : null);
                senInfo.setId(map.get("_id").toString());
                senInfo.setKw(map.get("kw").toString());
                senInfo.setMidInfo(map.get("midInfo").toString());
                senInfo.setLevel(map.get("level").toString());
                senInfo.setUrl(map.get("url").toString());
                senInfo.setSubmit_time(map.get("submit_time").toString());
                senInfo.setFilter_word_str(map.get("filter_word").toString());
                return senInfo;
            }
        } catch (Exception e) {
            logger.info("查询出错: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 更新字段
     *
     * @param senInfo
     * @return
     */
    public boolean submit(SenInfo senInfo) {
        boolean flag = false;
        String id = senInfo.getId();
        try {
            TransportClient client = ES.getSingleClient();
            Map map = new HashMap();
            map.put("isFinStr", "true");
            map.put("author", senInfo.getAuthor());
            map.put("submit_time", senInfo.getSubmit_time());
            map.put("filter_word",senInfo.getFilter_word_str());
            System.out.println(map);
            UpdateResponse updateResponse = client.prepareUpdate(ES_Enum.SEARCH_INDEX.getName(), ES_Enum.SEARCH_TYPE.getName(), id).setDoc(map).get();
//            logger.info("id:{}, 更新状态: {}",id,updateResponse.forcedRefresh());

            String author = senInfo.getAuthor();
            String info = userMap.get(author).toString();
            logger.info("当前数据:{}",JSONObject.toJSONString(userMap));
            info = info.replace("false", "true");
            userMap.put(author, info);
            flag = true;
            logger.info("当前数据:{}",JSONObject.toJSONString(userMap));
        } catch (Exception e) {
            flag = false;
            logger.info("id:{}, 强制更新出错: {}", id, e.getMessage());
        }
        return flag;
    }


    /**
     * 日期格式转换
     */
    public static String format(Date date) {
        SimpleDateFormat myFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = myFmt2.format(date);
        return d;
    }


    public static void testNext() {
        ES_Chain es_chain = new ES_Chain();
        for (int i = 0; i < 10; i++) {
            SenInfo s = es_chain.getNextInfo("", "");
            System.out.println(JSONObject.toJSONString(s));
        }
    }


    public static void main(String[] args) throws Exception {
        ES_Chain es_chain = new ES_Chain();
//        boolean flag = es_chain.judgeUserInfo("admin","admin");
//
//        System.out.println(flag);

//        testNext();
//        TransportClient client = ES.getSingleClient();
//        Map map = new HashMap();
//        map.put("isFinStr", "false");
//        map.put("author", "admin_admin");
//        map.put("submit_time", "2019-01-04 21:41:47");
//        String id = "1";
//        UpdateResponse updateResponse = client.prepareUpdate(ES_Enum.SEARCH_INDEX.getName(), ES_Enum.SEARCH_TYPE.getName(), id).setDoc(map).get();
        es_chain.getSenInfoWithId("121");
    }

}
