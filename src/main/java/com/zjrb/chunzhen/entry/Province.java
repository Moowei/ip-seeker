package com.zjrb.chunzhen.entry;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by blw on 2017/9/21.
 * @author blw
 */
public class Province {

    public final static Map<String, String> PROVINCE_MAP = new HashMap<String, String>();
    static {
        PROVINCE_MAP.put("北京","华北地区");
        PROVINCE_MAP.put("天津","华北地区");
        PROVINCE_MAP.put("河北","华北地区");
        PROVINCE_MAP.put("山西","华北地区");
        PROVINCE_MAP.put("内蒙古","华北地区");
        PROVINCE_MAP.put("辽宁","华北地区");
        PROVINCE_MAP.put("吉林","华北地区");
        PROVINCE_MAP.put("黑龙江","华北地区");
        PROVINCE_MAP.put("上海","华东地区");
        PROVINCE_MAP.put("江苏","华东地区");
        PROVINCE_MAP.put("浙江","华东地区");
        PROVINCE_MAP.put("安徽","华东地区");
        PROVINCE_MAP.put("福建","华东地区");
        PROVINCE_MAP.put("江西","华东地区");
        PROVINCE_MAP.put("山东","华东地区");
        PROVINCE_MAP.put("河南","华中地区");
        PROVINCE_MAP.put("湖北","华中地区");
        PROVINCE_MAP.put("湖南","华中地区");
        PROVINCE_MAP.put("广东","华南地区");
        PROVINCE_MAP.put("广西","华南地区");
        PROVINCE_MAP.put("海南","华南地区");
        PROVINCE_MAP.put("重庆","西南地区");
        PROVINCE_MAP.put("四川","西南地区");
        PROVINCE_MAP.put("贵州","西南地区");
        PROVINCE_MAP.put("云南","西南地区");
        PROVINCE_MAP.put("西藏","西南地区");
        PROVINCE_MAP.put("陕西","西北地区");
        PROVINCE_MAP.put("甘肃","西北地区");
        PROVINCE_MAP.put("青海","西北地区");
        PROVINCE_MAP.put("宁夏","西北地区");
        PROVINCE_MAP.put("新疆","西北地区");
        PROVINCE_MAP.put("香港","港澳台地区");
        PROVINCE_MAP.put("澳门","港澳台地区");
        PROVINCE_MAP.put("台湾","港澳台地区");
    }


    public static String get(String code) {
        String cur = PROVINCE_MAP.get(code);
        return cur == null ? "" : cur;
    }

    public static Set<String> getProvinceSet (){
        return PROVINCE_MAP.keySet();
    }

    public static void main(String[] args){
       for (String str : Province.getProvinceSet()){
            System.out.println(str);
       }
//        System.out.println( Province.get("台湾1"));
    }
}
