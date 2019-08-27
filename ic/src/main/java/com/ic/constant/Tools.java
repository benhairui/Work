package com.ic.constant;

import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 */
public class Tools {

    public static void simHash() {

    }

    /**
     * 去掉html标签
     * "*" 和 “？” 涉及到贪婪匹配, * 会匹配到最后一个，而加上?会匹配尽可能少的字符
     */
    public static String removeHtmlLabel(String htmlStr) {

        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式,
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        String regEx_line = "[\\s\\n]+"; //去除空白字符和回车符

        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签

        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        Pattern line_html = Pattern.compile(regEx_line, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(htmlStr);
        htmlStr = m_line.replaceAll("\n");

        return htmlStr.trim(); //返回文本字符串
    }

    /**
     * 获得P标签里的内容
     *
     * @param htmlStr
     * @return
     */
    public static String getPLabelContent(String htmlStr) {
        String regEx_p = "<p[^>]*?>[\\s\\S]*?<\\/p>";
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
//        String regEx_line = "[\\s\\n]+"; //去除空白字符和回车符
        String regEx_line = "[\\s]]+"; //去掉空白字符，保留回车符号

        Pattern p_html = Pattern.compile(regEx_p, Pattern.CASE_INSENSITIVE);
        Matcher m_p = p_html.matcher(htmlStr);
//        htmlStr = m_p.replaceAll("");
        StringBuffer buffer = new StringBuffer("");
        int i = 0;
        while (m_p.find()) {
            buffer.append(m_p.group());
        }
        htmlStr = buffer.toString();

        Pattern h_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = h_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签

        Pattern line_html = Pattern.compile(regEx_line, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(htmlStr);
        htmlStr = m_line.replaceAll("");

        return htmlStr;
    }

    /**
     * 对句子进行分词，获取相应的分词结果
     *
     * @param str
     * @return
     */
    public static List<IWord> segSentence(String str) {
        Sentence sentence = null;
        try {
            CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
            analyzer.enablePartOfSpeechTagging(true);
            sentence = analyzer.analyze(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sentence.wordList;
    }

    /**
     * 获取html的特殊符号
     *
     * @return
     */
    public static Set<String> getSpecialChara() {

        String path = Tools.class.getClassLoader().getResource("data/specialChara").getPath();
        List<String> list = FileRead.readDataFromFile(path);
        Set<String> set = new HashSet<>();
        try {
            for (String s : list) {
                String[] array = s.split("");
                set.add(array[1].trim());
                if (array.length > 2) {
                    set.add(array[2].trim());
                } else {
                    System.out.println(s);
                }
            }
        } catch (Exception e) {
            System.out.println("特殊字符文件读取失败");
            e.printStackTrace();
        }
//        System.out.println(set);
        return set;
    }

    /**
     * 得到搜狗的关键词搜索结果
     */
    public static String getStasticsOfSogou(String htmlStr) {

        String regEx_p = "(?<=搜狗已为您找到约)([0-9]{1,3})(([,][0-9]{3})*)(?=条相关结果)";
        Pattern line_html = Pattern.compile(regEx_p, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(htmlStr);

        if (!m_line.find()) {
            return null;
        }
        String str = m_line.group().replace(",", "");
        return str;
    }

    /**
     * 得到搜索的相关关键词
     *
     * @param htmlStr
     * @return
     */
    public static String getTitle(String htmlStr) {
        String regEx_p = "<title[^>]*?>[\\s\\S]*?<\\/title>";
        Pattern line_html = Pattern.compile(regEx_p, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(htmlStr);

        if (!m_line.find()) {
            return null;
        }
        String result = m_line.group().replace("&quot;", "").replace("-", "").replace("搜狗搜索", "")
                .replace("<title>", "").replace("</title>", "").trim();
        return result;
    }

    /**
     * 获取百度图片搜索框下的tag标签
     *
     * @return
     */
    public static List<String> getSearchTagOfBaidu(String htmlStr) {
        String regExp = "(?<=<a class=\"pull-rs\"[^>]{0,1000}>)[\\s\\S]*?(?=<\\/a>)";
        Pattern line_html = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(htmlStr);

        List<String> list = new ArrayList<>();
        while (m_line.find()) {
            list.add(m_line.group());
        }

        return list;
    }


    public static void dependencyParse(String sen) {
        CoNLLSentence sentence = HanLP.parseDependency(sen);
        System.out.println(sentence);
        // 可以方便地遍历它
        for (CoNLLWord word : sentence) {
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 也可以直接拿到数组，任意顺序或逆序遍历
        CoNLLWord[] wordArray = sentence.getWordArray();
        for (int i = wordArray.length - 1; i >= 0; i--) {
            CoNLLWord word = wordArray[i];
            System.out.printf("%s --(%s)--> %s\n", word.LEMMA, word.DEPREL, word.HEAD.LEMMA);
        }
        // 还可以直接遍历子树，从某棵子树的某个节点一路遍历到虚根
        CoNLLWord head = wordArray[12];
        while ((head = head.HEAD) != null) {
            if (head == CoNLLWord.ROOT) System.out.println(head.LEMMA);
            else System.out.printf("%s --(%s)--> ", head.LEMMA, head.DEPREL);
        }
    }

    public static boolean isChinese(String str) {
        Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");
        Matcher m = p_str.matcher(str);
        if (m.find() && m.group(0).equals(str)) {
            return true;
        }
        return false;
    }

    /**
     * 去掉括号里的内容，减少噪音
     *
     * @param htmlStr
     * @return
     */
    public static String removeBrack(String htmlStr) {
        String regex = "((（.*?）)|(\\(.*?\\)))";
        Pattern line_html = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(htmlStr);

        htmlStr = m_line.replaceAll("");

        return htmlStr;
    }

    public static void testRegex() {
        String str = "证券代码：000617证券简称：石油济柴公告编号：2015-019济南柴油机股份有限公司关于召开2014年年度股东大会的提示性公告本公司及董事会全体成员保证公告内容的真实、准确和完整，对公告的虚假记载、误导性陈述或者重大遗漏负连带责任。济南柴油机股份有限公司（以下简称“公司”）已于2015年3月28日在《中国证券报》、《证券时报》及巨潮资讯网（www.cninfo.com.cn）上刊登了《关于召开2014年年度股东大会的通知》，定于2015年6月12日以现场投票与网络投票相结合的表决方式召开公司2014年年度股东大会。为进一步保护投资者的合法权益，方便公司股东行使股东大会表决权，完善本次股东大会的表决机制，现将有关会议事项提示公告如下：一、召开会议基本情况（一）会议召集人：济南柴油机股份有限公司董事会（二）现场会议召开时间：2015年6月12日下午14：00时。（三）网络投票时间：1、通过深圳证券交易所交易系统投票的时间为2015年6月12日上午9:30－11:30，下午13:00－15:00；2、通过深圳证券交易所互联网投票系统(http://wltp.cninfo.com.cn)投票时间为2015年6月11日15:00至2015年6月12日15:00期间的任意时间。（四）股权登记日：2015年6月4日（五）会议的召开方式：本次股东大会采取现场投票与网络投票相结合的方式。公司将通过深圳证券交易所交易系统和互联网投票系统向股东提供网络形式的投票平台，股东可以在前述网络投票时间内通过上述系统行使表决权。股东应选择现场投票、网络投票中的一种方式进行表决，如果同一表决权出现重复投票表决的，以第一次投票表决结果为准。（六）出席对象：截至2015年6月4日下午收市时，在中国证券登记结算有限责任公司深圳分公司登记在册的持有公司股票的全体股东均有权出席本次股东大会。1、股东可以亲自出席本次会议，也可以委托代理人出席本次会议和参加表决，该代理人不必是本公司股东。2、公司董事、监事和高级管理人员。3、公司聘请的见证律师。（七）现场会议召开地点：山东省济南市经十西路11966号公司办公楼319会议室二、会议审议事项1、审议公司《2014年度董事会报告》。2、审议公司《2014年度监事会报告》。3、审议公司《2014年度财务报告》。4、审议公司《2014年度利润分配预案》。5、审议公司《2014年年度报告全文及摘要》。6、审议公司《2015年度预计日常关联交易的议案》。7、审议公司《关于2015年度预计接受关联方财务资助暨关联交易的议案》。上述1—7项审议事项披露于2015年3月28日，相关公告刊登在《证券时报》、《中国证券报》及巨潮资讯网站（http://www.cninfo.com.cn/）。注：议案六、议案七为关联交易议案，关联股东投票时应注意回避表决。三、现场股东大会登记方法（一）登记方法：出席现场会议的股东及委托代理人请于2015年6月10日（上午9:00-11:30，下午1:00-3:00）到公司证券办公室办理出席会议登记手续，异地股东可以通过信函或传真方式于上述时间登记，信函或传真以抵达公司的时间为准。1、法人股东代表应持证券账户卡、营业执照复印件、法定代表人证明、加盖公章的授权委托书及出席人身份证办理登记手续；2、自然人股东应持证券帐户卡、本人身份证；授权委托代理人持授权委托书、身份证、委托人证券帐户卡办理登记手续。（二）登记地点及联系方式1、登记地点：山东省济南市经十西路11966号证券办公室；2、邮编：250306；3、电话：0531-874233530531-874227514、传真：0531-874231775、联系人：余良刚、王云岗四、网络投票的安排在本次会议上，公司将通过深圳证券交易所交易系统和深圳证券交易所互联网投票系统向股东提供网络形式的投票平台，股东可以通过上述系统参加网络投票。有关股东参加网络投票的详细信息请登录深圳证券交易所网站（www.szse.cn）查询，网络投票的投票程序及要求详见本通知附件一。五、其他事项（一）会议费用：本次股东大会现场会议会期半天，与会人员的交通、食宿等费用自理。（二）网络投票期间，如投票系统突发重大事件影响本次会议进程，则本次会议的进程按当日通知进行。六、备查文件公司第七届董事会2015年第二次董事会决议特此公告。附件：1、网络投票程序及要求2、授权委托书格式济南柴油机股份有限公司董事会二〇一五年六月六日附件一：网络投票程序及要求一、采用深交易系统投票的投票程序1、本次临时股东大会通过深交易系统进行网络投票的时间为2015年6月12日上午9:30－11:30，下午13:00－15:00，投票程序比照深圳证券交易所新股申购业务操作。2、投票代码：360617；投票简称：济柴投票3、股东投票的具体程序为：（1）买卖方向为买入投票；（2）在“委托价格”项下填报本次临时股东大会的议案序号。100元代表总议案，1.00元代表议案1，以2.00元代表议案2，以此类推。每一议案应以相应的价格分别申报。如股东对所有议案均表示相同意见，则可以只对“总议案”进行投票。本次临时股东大会议案对应“委托价格”一览表议案序号议案名称委托价格总议案总议案统一表决100.00议案一审议公司《2014年度董事会报告》1.00议案二审议公司《2014年度监事会报告》2.00议案三审议公司《2014年度财务报告》3.00议案四审议公司《2014年度利润分配预案》4.00（3）在“委托股数”项下填报表决意见，1股代表同意，2股代表反对，3股代表弃权；（4）此次股东大会审议议案较多，如股东对所有议案均表示相同意见，可以只对“总议案”进行投票。如股东通过网络投票系统对“总议案”和单项议案进行了重复投票的,以第一次有效投票为准。即如果股东先对相关议案投票表决，再对总议案投票表决，则以已投票表决的相关议案的表决意见为准，其它未表决的议案以总议案的表决意见为准；如果股东先对总议案投票表决，再对相关议案投票表决，则以总议案的表决意见为准。（5）确认投票委托完成。（6）对同一议案的投票只能申报一次，不能撤单；（7）不符合上述规定的申报无效，深圳证券交易所交易系统作自动撤单处理。二、采用互联网投票的投票程序1、互联网投票系统开始投票的时间为2015年6月11日（现场股东大会召开前一日）下午3：00，结束时间为2015年6月12日（现场股东大会结束当日）下午3：00。2、股东通过互联网投票系统进行网络投票，需按照《深交所投资者网络服务身份认证业务实施细则》的规定办理身份认证，取得“深交所数字证书”或“深交所投资者服务密码”。3、股东根据获取的服务密码或数字证书，可登录http://wltp.cninfo.com.cn在规定时间内通过深交所互联网投票系统进行投票。三、网络投票其他注意事项网络投票系统按股东账户统计投票结果，如同一股东账户通过深交所交易系统和互联网投票系统两种方式重复投票，股东大会表决结果以第一次有效投票结果为准。附件二：授权委托书兹全权委托先生（女士）代表我单位（个人）出席济南柴油机股份有限公司2014年年度股东大会，并代为行使表决权。委托人签名：受托人签名：身份证号码：身份证号码：委托人持有股数：委托日期：2015年月日委托股东帐号：代为行使表决权范围：序号议案名称同意反对弃权12014年度董事会报告22014年度监事会报告32014年度财务报告42014年度利润分配预案52014年年度报告全文及摘要62015年预计日常关联交易的议案7关于2015年预计接受关联方财务资助暨关联交易的议案注：1、每个项目只能在同意、反对、弃权栏中选一项，并打√。2、议案六、议案七为关联交易议案，关联股东投票时应注意回避表决。3、以上委托书复印及剪报均为有效。4、本授权委托有效期：自委托书签署之日起至本次股东大会结束为止。\n";
        String regexStr = "审议((?!审议)(.|\\n))*?(摘要|摘要》|修订稿|预案|报告|的议案|调整后)(）》|）|》)";
        Pattern pattern = Pattern.compile(regexStr,Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(str);
        int i = 0;
        while(m.find()){

            System.out.println(m.group(i));
        }

    }

    public static void main(String[] args) throws IOException {

//        String s = "";
//
//        List<String> strings = getSearchTagOfBaidu(s);
//        System.out.println(JSONObject.toJSONString(strings));
//
//
//        String str = "中国人";
//        System.out.println(isChinese(str));
        testRegex();
    }
}
