package com.ic.data;

import com.alibaba.fastjson.JSONObject;
    import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.List;


public class HanlpTest {

    public static void example(){
        Segment segment = new CRFSegment();
        segment.enablePartOfSpeechTagging(true);
        List<Term> termList = segment.seg("本节仅针对我国有机硅上游最核心的部分，即甲基氯硅烷和中间体环节的工艺优化提出较为系统的建议");
        for(Term term:termList){
            if(term.nature == null){
                System.out.println("识别到新词： " +term.word);
            }
        }
    }

    public static void crf() throws Exception{
        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
        String[] tests = new String[]{
                "我在大搜车股份有限公司上班",
                "关于收购上海远景数字信息技术有限公司51%股权的议案",
                "北京东土科技股份有限公司关于收购上海远景数字信息技术有限公司的可行性研究报告",
                "关于使用超募资金收购上海远景数字信息技术有限公司51%股权的议案"
//                "产业链角度来看，物联网的上游主要为电子行业，包括传感器提供商、终端芯片提供商以及无线模组厂商，中游主要为通信行业，包括终端设备提供商、网络设备提供商、系统设备供应商以及电信运营商;下游主要为计算机行业，包括各大云计算平台、系统及软件开发商、智能终端提供商、系统集成供应者"
        };
        Segment s = HanLP.newSegment().enableOrganizationRecognize(true);
        List<Term> termList = s.seg(tests[0]);
        System.out.println(JSONObject.toJSONString(termList));
        for (String sentence : tests){

            System.out.println(analyzer.analyze(sentence));
        }
    }

    public static void dependency(){
//        String str = "其中，下游终端产品包括家电、照明、安防和小型智能单品等，厂商众多，市场发展得极为热闹，以智能家电为代表;上游零部件包括芯片、电路板与塑料等;中游中间品包括模块和智>能控制器等，这两大环节受到的关注度较低，发展较为薄弱。";
//        String str = "智能汽车的产业链可以描述如下：1）车联网的产业链，包括上游的元器件和芯片生产企业，中游的汽车厂商、设备厂商和软件平台开发商，以及下游的系统集成商、通信服务商、平台运\n" +
//                "营商和内容提供商等。";
        String str = "产业链角度来看，物联网的上游主要为电子行业，包括传感器提供商、终端芯片提供商以及无线模组厂商，中游主要为通信行业，包括终端设备提供商、网络设备提供商、系统设备供应商以及电信运营商;下游主要为计算机行业，包括各大云计算平台、系统及软件开发商、智能终端提供商、系统集成供应者";
        System.out.println(HanLP.parseDependency(str));
        System.out.println(HanLP.extractPhrase(str,10));

        CoNLLSentence sentences = HanLP.parseDependency(str);
        for(CoNLLWord word:sentences){
            System.out.printf("%s--(%s)-->%s\n",word.LEMMA,word.DEPREL,word.HEAD.LEMMA);
        }

    }

    public static void example_dependency(){
        CoNLLSentence sentence = HanLP.parseDependency("徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。");
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence)
        {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--)
        {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        System.out.println("===============");
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null)
        {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }
    }

    public static void main(String[] args) throws Exception{
      //  List<Term> termList = NLPTokenizer.segment("你看过穆赫兰道吗？");
     //   System.out.println(termList);

        crf();
//        dependency();

//        example_dependency();

    }


}
