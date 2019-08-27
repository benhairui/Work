package com.ic.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FileWrite {
    private static Logger logger = LoggerFactory.getLogger(FileWrite.class);

    /**
     * 将数据写入指定路径
     *
     * @param filePath
     */
    public static synchronized void writeDataToFile(String filePath, List<String> list) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            StringBuffer buffer = new StringBuffer();
            for (String s : list) {
                buffer.append(s).append("\n");
            }

            bw.write(buffer.toString());
            bw.close();
            fw.close();
        } catch (Exception e) {
            logger.error("文件写入错误: " + filePath);
        } finally {
            logger.info("写入结束: {}", filePath);
        }
    }

    /**
     * 将数据写入指定路径
     *
     * @param filePath
     */
    public static void writeMapDataToFile(String filePath, Map<String, Integer> map) {
        try {
            FileWriter fw = new FileWriter(filePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {

                String key = iter.next().toString();
                int value = map.get(key);
                String str = key + "::" + value + "\n";
                bw.write(str);
                System.out.println(str);
            }
            bw.close();
            fw.close();
        } catch (Exception e) {
            logger.error("文件写入错误: " + filePath);
        }
    }
}
