package com.moowei.ipip17mon.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by blw on 2018/2/1 0001.
 */
public class ReadAndWriteFileUtil {

    private static Properties properties = new Properties();
    /**
     * 读取配置文件
     * @param fileName
     */
    public static void readProperties(String fileName){
        try {
            InputStream in = ReadAndWriteFileUtil.class.getResourceAsStream("/"+fileName);
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            properties.load(bf);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /**
     * 根据key读取对应的value
     * @param key
     * @return
     */
    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
