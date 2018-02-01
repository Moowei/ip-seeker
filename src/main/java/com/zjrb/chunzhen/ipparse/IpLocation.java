package com.zjrb.chunzhen.ipparse;

/**
 * Created by blw on 2017/9/19.
 * 用来封装ip相关信息，目前只有两个字段，ip所在的国家和地区
 */
public class IpLocation {
    public final String LOCAL_NET = "CZ88.NET";
    private String country;
    private String area;

    public IpLocation() {
        country = area = "";
    }

    public IpLocation getCopy() {
        IpLocation ret = new IpLocation();
        ret.country = country;
        ret.area = area;
        return ret;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        //如果为局域网，纯真IP地址库的地区会显示CZ88.NET,这里把它去掉
        if(LOCAL_NET.equals(area.trim())){
            this.area="本机或本网络";
        }else{
            this.area = area;
        }
    }
}