package com.ic.relation.main;

import com.ic.relation.util.RelationUtil;

import java.util.Set;

/**
 * Created by 刘绪光 on 2018/6/6.
 */
public class RelaExtract {
    //拿到相应的谓词，统计有哪些谓词,抽样查看

    public static void main(String[] args) {
        String[] testArr = {
//                "刘小绪非常喜欢跑步",
//                "刘小绪和李华是朋友",
//                "刘小绪生于四川",
//                "刘小绪洗干净了衣服",
//                "海洋由水组成",
//                "父亲是来自肯尼亚的留学生",
//                "刘小绪就职于学校",
//                "中国的首都是北京",
//                "产业链角度来看，物联网的上游主要为电子行业，包括传感器提供商、终端芯片提供商以及无线模组厂商，中游主要为通信行业，包括终端设备提供商、网络设备提供商、系统设备供应商以及电信运营商;下游主要为计算机行业，包括各大云计算平台、系统及软件开发商、智能终端提供商、系统集成供应者",
                "10%上游IP是体育产业核心，带动下游90%体育消费洞察体育力量，把握产业脉搏！",
//                " 上游行业中的医疗设备行业主要提供口腔治疗设备、口腔基础检查设备及其他设备，包括牙钳、牙科电钻、牙科椅等；口腔材料行业主要提供义齿材料、种植材料和正畸材料；医用药品行业主要包括麻醉剂和抗感染药物等，占比较小。"
        };

        Set<String> result = RelationUtil.entityRelation(testArr);

        for (String relation :
                result) {
            System.out.println(relation);
        }
    }

}
