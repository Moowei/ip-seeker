package com.moowei.ipip17mon.thirdparty;

/**
 * Created by blw on 2018/02/06.
 * 用于封装IP详细信息的类
 * 第1列：国家
 * 第2列：省份（不能标注到省一级的则显示国家；外国的部分首都不属于任何州省的，显示城市名）
 * 第3列：地级市/省直辖县级行政区
 * 第4列：所有者（学校/公司）
 * 第5列：运营商/线路（每日标准版不含运营商信息）
 * 第6列：城市/省份/国家中心点纬度
 * 第7列：城市/省份/国家中心点经度
 * 第8列：所在时区代表城市
 * 第9列：所在时区
 * 第10列：中国行政区划代码
 * 第11列：国际区号
 * 第12列：国家代码
 * 第13列：洲代码
 */
public final class LocationInfoForCity {
    public String country;  //国家
    public String province; //省
    public String city;     //市
    public String owner;    //所有者（学校/公司）
    public String isp;      //运营商
    public String longitude;//经度
    public String latitude; //纬度
    public String timeZone; //时区：UTC+8
    public String timeZoneCity;     //时区对应的位置，如：Asia/Shanghai
    public String cityCode;         //中国行政区划代码
    public String internationalCode;//国际区号
    public String countryCode;  //国家代码
    public String stateCode;    //州代码

    public LocationInfoForCity(){
    }

    public LocationInfoForCity(String LocationInfoStr, String splitStr) throws Exception {
        String[] strArry = LocationInfoStr.split(splitStr);
        if(strArry.length == 13){
            this.country = strArry[0];//国家
            this.province = strArry[1];  //省
            this.city = strArry[2];   //市
            this.owner = strArry[3];    //所有者（学校/公司）
            this.isp = strArry[4];    //运营商
            this.longitude = strArry[5];  //经度
            this.latitude = strArry[6];   //纬度
            this.timeZone = strArry[7];   //时区：UTC+8
            this.timeZoneCity = strArry[8];   //时区对应的位置，如：Asia/Shanghai
            this.cityCode = strArry[9];   //中国行政区划代码
            this.internationalCode = strArry[10]; //国际区号
            this.countryCode = strArry[11];  //国家代码
            this.stateCode = strArry[12];    //州代码
        } else {
            throw new Exception("LocationInfoForCity 构造参数错误:请检查IP格式是否正确"+"\nLocationInfoStr="+LocationInfoStr + "\nsplitStr="+splitStr);
//            System.out.println("IP数据字段长度错误（City）：");
//            System.out.println("IP详细数据：" + LocationInfoStr);
//            System.out.println("字段个数：" + strArry.length);
        }
    }

    public LocationInfoForCity(String country, String province, String city, String isp) {
        this.country = country;//国家
        this.province = province;  //省
        this.city = city;   //市
        this.isp = isp;    //运营商
    }

    public String toString(String splitStr) {
        return country + splitStr + province + splitStr + city + splitStr+ owner + splitStr + isp+ splitStr + longitude+ splitStr + latitude+ splitStr +timeZone + splitStr + timeZoneCity+ splitStr +cityCode + splitStr + internationalCode+ splitStr + countryCode+ splitStr + stateCode ;
    }

    public String toString() {
        return country + "\t" + province + "\t" + city + "\t"+ owner + "\t" + isp+ "\t" + longitude+ "\t" + latitude+ "\t" +timeZone + "\t" + timeZoneCity+ "\t" +cityCode + "\t" + internationalCode+ "\t" + countryCode+ "\t" + stateCode ;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof LocationInfoForCity)) {
            return false;
        }
        LocationInfoForCity l = (LocationInfoForCity) o;
        return country.equals(l.country) && province.equals(l.province) && city.equals(l.city) && isp.equals(l.isp);
    }

    public int hashCode() {
        return country.hashCode() * 31 * 31 * 31 + province.hashCode() * 31 * 31 + city.hashCode() * 31 + isp.hashCode();
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

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getTimeZoneCity() {
        return timeZoneCity;
    }

    public void setTimeZoneCity(String timeZoneCity) {
        this.timeZoneCity = timeZoneCity;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getInternationalCode() {
        return internationalCode;
    }

    public void setInternationalCode(String internationalCode) {
        this.internationalCode = internationalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
}
