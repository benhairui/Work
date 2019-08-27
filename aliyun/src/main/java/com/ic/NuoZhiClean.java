package com.ic;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NuoZhiClean {


    public static void removeSame(){
        String path = "";
    }


    public static void main(String[] args){

        String regExp = "(.*作为.*评估(结论|报告)(.*?,))";
        String str = "明细详见下表： 资产评估结果汇总表被评估单位：安徽鼎世金额单位：人民币万元 项目 账面价值评估价值增减额增值率（%）A  B  C = B – A  D=C/A×100%流动资产 1 486.24486.24非流动资产 2 6.116.00  -0.11  -1.88其中：可供出售金融资产3固定资产 4 6.116.00  -0.11  -1.88递延所得税资产5资产总计 6 492.35492.24  -0.11  -0.02流动负债 7 201.45201.45非流动负债 8负债总计 负债总计负债总计 负债总计   9 201.45201.45净 净净 净资 资资 资产 产产 产（ （（ （所有者权益 所有者权益所有者权益 所有者权益） ）） ）10 290.90290.79  -0.11  -0.04评估结论详细情况详见资产基础法评估明细表,";
        Pattern line_html = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        Matcher m_line = line_html.matcher(str);

        List<String> list = new ArrayList<>();

        while(m_line.find()){
            list.add(m_line.group());
        }

        System.out.println("hello");




    }



}
