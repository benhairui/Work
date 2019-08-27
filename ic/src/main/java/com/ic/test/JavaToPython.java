package com.ic.test;

import com.alibaba.fastjson.JSONObject;
import com.ic.constant.FileRead;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class JavaToPython {

    public static void exec() {
        List<String> list = FileRead.readDataFromFile("/home/benhairui/Documents/gitlab-workspace/Work/third_data/result2/splitFile");
        Process p = null;
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i).toString();
            String cmd = "python3 /home/benhairui/PycharmProjects/chain/venv/com/extend/SenTogether.py \"" + s + "\"";
            String[] cmdArray = new String[3];
            cmdArray[0] = "python3";
            cmdArray[1] = "/home/benhairui/PycharmProjects/chain/venv/com/extend/SenTogether.py";
            cmdArray[2] = s;
            System.out.println(cmd);
            try {
                p = Runtime.getRuntime().exec(cmdArray);
                InputStream fis = p.getInputStream();
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    line = line.replace("\"", "").replace("\'", "");
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public static void main(String[] args) {
        exec();

//        PythonInterpreter interpreter = new PythonInterpreter();
//        interpreter.execfile("/home/benhairui/PycharmProjects/chain/venv/com/extend/filterHtml.py");
//        PyFunction pyFunction = interpreter.get("hello",PyFunction.class);
//        PyObject pyObject = pyFunction.__call__();
//        System.out.println(pyObject);
    }



}
