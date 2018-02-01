package com.zjrb.chunzhen.entry;

import java.util.ArrayList;

/**
 * Created by blw on 2017/9/25.
 */
public class ProvinceSuffix {
    //我国地域常用到的后缀
    public static final String PROVINCE_SUFFIX = "省";
    public static final String CITY_SUFFIX = "市";
    public static final String STATE_SUFFIX = "州";
    public static final String LEAGUE_SUFFIX = "盟";
    public static final String DISTRICT_SUFFIX = "县";
    public static final String DISTRICT2_SUFFIX = "地区";
    public static final String DISTRICT3_SUFFIX = "区";


    public static Integer counterAllRight = 0;//所有记录数据
    public static Integer counterAllError = 0;//所有记录数据
    public static Integer counterGW = 0;//flage = "国外";
    public static Integer counterGN = 0;//flage = "中国"
    public static Integer counterS = 0;//flage = "省";
    public static Integer counterSS = 0;//flage = "省-市(地区,州)";
    public static Integer counterSX = 0;//flage = "省-县区旗";
    public static Integer counterSSX = 0;//flage = "省-市(地区,州)-市县区";
    public static Integer counterZ = 0;//flage = "直";
    public static Integer counterZS = 0;//flage = "直-市县区旗";
    public static Integer counterZNS = 0;//flage = "直!市";
    public static Integer counterNZ = 0;//flage = "!直";
    public static Integer counterNZS = 0;//flage = "!直-市盟";
    public static Integer counterNZSX = 0;//flage = "!直-市盟-市县区旗";
    //其他状态统计：
    public static Integer counterZAX = 0;//包含"州县"
    public static Integer counterZAS = 0;//包含"州市"
    public static Integer counterSSS = 0;//**市**市

    public final static ArrayList<String> SUFFIX_ARR = new ArrayList<String>();
    static {
        SUFFIX_ARR.add("州");
        SUFFIX_ARR.add("市");
        SUFFIX_ARR.add("地区");
        SUFFIX_ARR.add("盟");
    }
    /**
     * 获取key拼成的字符串,以逗号分隔，**,**,**
     * @return
     */
    public static String getSuffixList(){
        StringBuffer suffixBuffer = new StringBuffer();
        for(String suffix : SUFFIX_ARR){
            suffixBuffer.append(suffix+",");
        }
        return suffixBuffer.toString();
    }

    /**
     * 获取相邻的两个前缀
     */
    public static String getTowSuffix() {
        return null;
    }

    /**
     * 返回包含的字符在SUFFIX_ARR中的下标
     * 如果包含"州市"则返回为"市"
     * @param areaStr
     */
    public static int isContainsOf(String areaStr){
        String strTmp = "";
        String strTmpConnect = "";
        for(int index =0;index < ProvinceSuffix.SUFFIX_ARR.size();index++){
            strTmp = ProvinceSuffix.SUFFIX_ARR.get(index);
            strTmpConnect = index < ProvinceSuffix.SUFFIX_ARR.size()-1 ? ProvinceSuffix.SUFFIX_ARR.get(index)+ProvinceSuffix.SUFFIX_ARR.get(index+1) : "";
            if(areaStr.contains(strTmp) && !areaStr.trim().startsWith(strTmp)){
                if(strTmpConnect.length()>0 && areaStr.contains(strTmpConnect)){
                    ProvinceSuffix.counterZAS++;
                    return index+1;
                }
                return index;
            }
        }
        return -1;
    }

    /**
     * 统计
     */
    public static void getCounter(){
        System.out.println("解析数据总数： "+counterAllRight);
        System.out.println("遗漏的数据总数： "+counterAllError);
        System.out.println("国外:  "+counterGW);
        System.out.println("国内： "+counterGN);
        System.out.println("省:  "+counterS);
        System.out.println("省-市(地区,州):  "+counterSS);
        System.out.println("省-县区旗:  "+counterSX);
        System.out.println("省-市(地区,州)-市县区:  "+counterSSX);
        System.out.println("直:  "+counterZ);
        System.out.println("直-市县区旗:  "+counterZS);
        System.out.println("直!市:  "+counterZNS);
        System.out.println("!直:  "+counterNZ);
        System.out.println("!直-市盟:  "+counterNZS);
        System.out.println("!直-市盟-市县区旗:  "+counterNZSX);
        System.out.println("州县："+counterZAX);
        System.out.println("州市："+counterZAS);
        System.out.println("*市*市： "+counterSSS);
    }


    public static void main(String[] args){
        System.out.println(getSuffixList());
        System.out.println(SUFFIX_ARR.size());
    }
}
