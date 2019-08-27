package com.ic.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileRead {

    private static Logger logger = LoggerFactory.getLogger(FileRead.class);

    /**
     * 从文件中读取数据
     */
    public static List<String> readDataFromFile(String filePath) {

        List<String> list = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath)) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
//                list.add(new String(line.getBytes("latin1"),"UTF-8"));
                list.add(line);
            }
            bufferedReader.close();
            fileReader.close();
        } catch (Exception e) {
            logger.info("文件读取失败: {}, 失败原因: {}", filePath, e.getMessage());
        }
        return list;
    }

    public static void main(String[] args){
        List<String> list = FileRead.readDataFromFile("/home/benhairui/Documents/test");

    }
}
