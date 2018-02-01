package com.zjrb.chunzhen.entry;

/**
 * Created by blw on 2017/9/19.
 */

import com.zjrb.chunzhen.util.LogFactory;
import org.apache.log4j.Level;

import java.util.StringTokenizer;

/**
 * 一条IP范围记录，不仅包括国家和区域，也包括起始IP和结束IP
 */
public class IPEntry {

    //类属性
    private String originBeginIp;//起始IP
    private String originEndIp;//终止IP
    private String parseCountry;//国家
    private String parseProvince;//省
    private String parseCity;//市（州，盟，地区）
    private String parseDistrict;//县（区）
    private String parseOperator;//运营商
    private String originArea;//原始的地址字段
    private String originOperatorDESC;//运营商原始字段
    private Long parseLongBeginIp;//转成long型的startIp
    private Long parseLongEndIp;//转成Long型的endIp
    private Long parseCounterIp;//该区间的ip数
    private String flage;//地址字段的类型

    private static IPEntry ipEntryInstance = new IPEntry();

    /**
     * 构造函数
     */
    private IPEntry(){}

    public static IPEntry getInstance(){
        return ipEntryInstance;
    }

    /**
     * 初始化Instance
     */
    public void initIPEntryInstance(){
        ipEntryInstance.originBeginIp ="";
        ipEntryInstance.originEndIp = "";
        ipEntryInstance.parseCountry = "";
        ipEntryInstance.parseProvince = "";
        ipEntryInstance.parseCity = "";
        ipEntryInstance.parseDistrict = "";
        ipEntryInstance.parseOperator = "";
        ipEntryInstance.originArea = "";
        ipEntryInstance.originOperatorDESC = "";
        ipEntryInstance.parseLongBeginIp = 0l;
        ipEntryInstance.parseLongEndIp = 0l;
        ipEntryInstance.parseCounterIp = 0l;
        ipEntryInstance.flage = "";
    }

    public IPEntry getInstanceByParam(String country, String province, String city, String parseDistrict, String flage, String originArea) {
        ipEntryInstance.parseCountry = country;
        ipEntryInstance.parseProvince = province;
        ipEntryInstance.parseCity = city;
        ipEntryInstance.parseDistrict = parseDistrict;
        ipEntryInstance.originArea = originArea;
        ipEntryInstance.flage = flage;
        return ipEntryInstance;
    }

    public String getParseCountry() {
        return parseCountry;
    }

    public String getParseCity() {
        return parseCity;
    }

    public String getOriginArea() {
        return originArea;
    }

    public String getOriginBeginIp() {
        return originBeginIp;
    }

    public String getParseOperator() {
        return parseOperator;
    }

    public String getOriginOperatorDESC() {
        return originOperatorDESC;
    }

    public String getParseDistrict() {
        return parseDistrict;
    }

    public String getOriginEndIp() {
        return originEndIp;
    }

    public String getFlage() {
        return flage;
    }

    public String getParseProvince() {
        return parseProvince;
    }

    public Long getParseLongBeginIp() {
        return parseLongBeginIp;
    }

    public Long getParseLongEndIp() {
        return parseLongEndIp;
    }

    public void setParseLongBeginIp(Long parseLongBeginIp) {
        this.parseLongBeginIp = parseLongBeginIp;
    }

    public void setParseLongEndIp(Long parseLongEndIp) {
        this.parseLongEndIp = parseLongEndIp;
    }

    public void setParseCounterIp(Long parseCounterIp) {
        this.parseCounterIp = parseCounterIp;
    }

    public void setParseOperator(String parseOperator) {
        this.parseOperator = parseOperator;
    }

    public void setOriginOperatorDESC(String originOperatorDESC) {
        this.originOperatorDESC = originOperatorDESC;
    }

    public void setOriginArea(String originArea) {
        this.originArea = originArea;
    }

    public void setOriginBeginIp(String originBeginIp) {
        this.originBeginIp = originBeginIp;
    }

    public void setParseCity(String parseCity) {
        this.parseCity = parseCity;
    }

    public void setParseCountry(String parseCountry) {
        this.parseCountry = parseCountry;
    }

    public void setParseDistrict(String parseDistrict) {
        this.parseDistrict = parseDistrict;
    }

    public void setOriginEndIp(String originEndIp) {
        this.originEndIp = originEndIp;
    }

    public void setFlage(String flage) {
        this.flage = flage;
    }

    public void setParseProvince(String parseProvince) {
        this.parseProvince = parseProvince;
    }

    /**
     * 默认显示方式是以“|”分隔
     *
     * @return
     */
    public String getAreaSplited() {
        return parseCountry + "|" + parseProvince + "|" + parseCity + "|" + parseDistrict;
    }

    /**
     * 显示省市县字段拼接成的字符串，并指定分隔符
     *
     * @param splitStr 指定分隔符
     * @return 返回由省市县字段拼接成的字符串，eg：中国|河北|保定|定州
     */
    public String getAreaSplited(String splitStr) {
        return parseCountry + splitStr + parseProvince + splitStr + parseCity + splitStr + parseDistrict;
    }

    /**
     * 全部字段输出格式
     * @param splitStr
     * @return
     */
    public String getIPLibrarySplited(String splitStr) {
        return originBeginIp + splitStr + originEndIp + splitStr + parseCountry + splitStr + parseProvince + splitStr + parseCity + splitStr + parseDistrict + splitStr + parseOperator + splitStr + originArea + splitStr + originOperatorDESC + splitStr + parseLongBeginIp + splitStr + parseLongEndIp + splitStr+ parseCounterIp;
    }

    /**
     * 从ip的字符串形式得到字节数组形式
     * @param ip 字符串形式的ip
     * @return Long形式的ip,若返回为0，说明输入的字符串IP格式错误。
     */
    public static long getIpLongFromString(String ip) {
        Integer[] ret = new Integer[4];
        StringTokenizer st = new StringTokenizer(ip, ".");
        try {
            ret[0] = (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            LogFactory.log("从ip的字符串到int数组报错", Level.ERROR, e);
//            e.printStackTrace();
            return 0;
        }
        return (((long)ret[0] << 24) | (ret[1] << 16) |
                (ret[2] << 8) | ret[3]);
    }

}