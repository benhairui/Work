package com.ic.Format;


import com.ic.connect.ES;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.index.query.functionscore.ScriptScoreFunctionBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 诺之金融数据的处理
 */
public class EsDataFormat {


    public void esScore() throws Exception{

        Map<String, Object> params = new HashMap<>();
        params.put("num1", 1);
        params.put("num2", 2);
        String f = "2016-12-15";
        SimpleDateFormat dateForamt = new SimpleDateFormat("yyyy-mm-dd");
        long timeNow = 0;
//        timeNow = dateForamt.parse(f).getTime() ;
        System.out.println(timeNow);
        String inlineScript = "diff="+timeNow+"-doc['regit'].value;"
                + "return (diff/ (24 * 60 * 60 * 1000))";
        Script script = new Script(ScriptType.INLINE,Script.DEFAULT_SCRIPT_LANG,inlineScript,params);
//        script = new Script(inlineScript, ScriptType.INLINE, "groovy", params);
        ScriptScoreFunctionBuilder scriptBuilder = ScoreFunctionBuilders.scriptFunction(script);
        TransportClient client = ES.getSingleClient();
        SearchRequestBuilder requestBuilder = client.prepareSearch("dateproduct")
                .setTypes("product")
                .setQuery(QueryBuilders.functionScoreQuery(scriptBuilder));



        SearchResponse response = requestBuilder.setFrom(0).setSize(5).execute().actionGet();


    }


}
