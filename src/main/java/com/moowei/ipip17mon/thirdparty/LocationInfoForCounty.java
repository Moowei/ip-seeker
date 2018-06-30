package com.moowei.ipip17mon.thirdparty;

/**
 * Created by blw on 2018/2/6 0006.
 * ipip县区库datx格式解析-官方代码
 * 样例：https://www.ipip.net/example.html
 * 058.206.163.0	058.206.163.255	中国	甘肃	兰州	榆中县	620123	17.4	104.1145	35.8441
 * 前2列分别是起始和终止IP地址，其余的从左往右依次是：
 * 第3列：国家
 * 第4列：省份/直辖市
 * 第5列：地级市/省直辖县级行政区
 * 第6列：区县
 * 第7列：中国行政区划代码
 * 第8列：覆盖范围（IP使用区域半径，单位：千米）
 * 第9列：区县中心点经度
 * 第10列：区县中心点纬度
 */
public class LocationInfoForCounty {
    public String country;  // 国家
    public String province; // 省
    public String city;     // 市
    public String county;   // 县
    public String cityCode; // 中国行政区划代码
    public String coverageArea; // 覆盖范围（IP使用区域半径，单位：千米）
    public String longitude;// 区县中心点经度
    public String latitude; // 区县中心点纬度

    public LocationInfoForCounty(){}

    public LocationInfoForCounty(String LocationInfoStr) throws Exception {
        String[] strArry = LocationInfoStr.split("\t");
        if(strArry.length == 8) {
            this.country = strArry[0];  // 国家
            this.province = strArry[1]; // 省
            this.city = strArry[2];     // 市
            this.county = strArry[3];
            this.cityCode = strArry[4];
            this.coverageArea = strArry[5];
            this.longitude = strArry[6];
            this.latitude = strArry[7];
        }else {
            throw new Exception("LocationInfoForCounty 构造参数错误"+"构造字符串LocationInfoStr："+LocationInfoStr);
//            System.out.println("IP数据字段长度错误：");
//            System.out.println("IP详细数据：" + LocationInfoStr);
//            System.out.println("字段个数："+strArry.length);
        }
    }

    public LocationInfoForCounty(String LocationInfoStr,String splitStr) throws Exception {
        String[] strArry = LocationInfoStr.split(splitStr);
        if(strArry.length == 8) {
            this.country = strArry[0];  // 国家
            this.province = strArry[1]; // 省
            this.city = strArry[2];     // 市
            this.county = strArry[3];
            this.cityCode = strArry[4];
            this.coverageArea = strArry[5];
            this.longitude = strArry[6];
            this.latitude = strArry[7];
        }else {
            throw new Exception("LocationInfoForCounty 构造参数错误：请检查IP格式是否正确"+"\nLocationInfoStr："+LocationInfoStr+"\nsplitStr："+splitStr);
//            System.out.println("IP数据字段长度错误(County)：");
//            System.out.println("IP详细数据：" + LocationInfoStr);
//            System.out.println("字段个数："+strArry.length);
        }
    }

    public String toString(String splitStr) {
        return country + splitStr + province + splitStr + city + splitStr + county + splitStr + cityCode + splitStr  + coverageArea + splitStr + longitude+ splitStr + latitude;
    }

    public String toString() {
        return country + "\t" + province + "\t" + city + "\t"+ county + "\t" + cityCode + "\t"  + coverageArea + "\t" + longitude+ "\t" + latitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCoverageArea() {
        return coverageArea;
    }

    public void setCoverageArea(String coverageArea) {
        this.coverageArea = coverageArea;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
