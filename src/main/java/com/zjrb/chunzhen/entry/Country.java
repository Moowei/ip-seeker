package com.zjrb.chunzhen.entry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by blw on 2017/9/21.
 * @author blw
 */
public class Country {
    public final static Map<String, String> COUNTRY_MAP = new HashMap<String, String>();
    static {
        COUNTRY_MAP.put("004", "阿富汗");
        COUNTRY_MAP.put("008", "阿尔巴尼亚");
        COUNTRY_MAP.put("012", "阿尔及利亚");
        COUNTRY_MAP.put("020", "安道尔");
        COUNTRY_MAP.put("036", "澳大利亚");
        COUNTRY_MAP.put("040", "奥地利");
        COUNTRY_MAP.put("056", "比利时");
        COUNTRY_MAP.put("060", "百慕大");
        COUNTRY_MAP.put("090", "所罗门群岛");
        COUNTRY_MAP.put("100", "保加利亚");
        COUNTRY_MAP.put("124", "加拿大");
        COUNTRY_MAP.put("152", "智利");
        COUNTRY_MAP.put("156", "中国");
        COUNTRY_MAP.put("170", "哥伦比亚");
        COUNTRY_MAP.put("208", "丹麦");
        COUNTRY_MAP.put("222", "萨尔瓦多");
        COUNTRY_MAP.put("246", "芬兰");
        COUNTRY_MAP.put("250", "法国");
        COUNTRY_MAP.put("300", "希腊");
        COUNTRY_MAP.put("344", "香港");
        COUNTRY_MAP.put("380", "意大利");
        COUNTRY_MAP.put("392", "日本");
        COUNTRY_MAP.put("410", "韩国");
        COUNTRY_MAP.put("446", "澳门");
        COUNTRY_MAP.put("458", "马来西亚");
        COUNTRY_MAP.put("554", "新西兰");
        COUNTRY_MAP.put("578", "挪威");
        COUNTRY_MAP.put("702", "新加坡");
        COUNTRY_MAP.put("752", "瑞典");
        COUNTRY_MAP.put("756", "瑞士");
        COUNTRY_MAP.put("826", "英国");
        COUNTRY_MAP.put("840", "美国");
        COUNTRY_MAP.put("901", "台湾");
    }
    public static String get(String code) {
        String cur = COUNTRY_MAP.get(code);
        return cur == null ? "" : cur;
    }
}
