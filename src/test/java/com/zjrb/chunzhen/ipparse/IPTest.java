package com.zjrb.chunzhen.ipparse;

/**
 * Created by blw on 2017/9/19.
 */
import junit.framework.TestCase;

public class IPTest extends TestCase {

    public void testIp(){
        //指定纯真数据库的文件名，所在文件夹
        long getIPStartTime=System.currentTimeMillis();
        String ipStr = "1.24.176.0";
        IPSeeker ip=new IPSeeker("qqwry.dat","D:\\cz88.net\\ip");
        long getIPEndTime=System.currentTimeMillis();
        //测试IP 58.20.43.13
        String country = ip.getIPLocation(ipStr).getCountry();
        long getCountryStarTime = System.currentTimeMillis();
        String areas = ip.getIPLocation(ipStr).getArea();
        long getAreaStarTime = System.currentTimeMillis();
        System.out.println("getIPStartTime"+getIPStartTime);
        System.out.println("getIPEndTime"+getIPEndTime);

        System.out.println("获取IP时间：" + (getIPEndTime-getIPStartTime));
        System.out.println("获取Country时间：" + (getCountryStarTime-getIPEndTime));
        System.out.println("获取Area时间：" + (getAreaStarTime-getCountryStarTime));
        System.out.println("国家： "+country);
        System.out.println("地区： "+areas);
    }
}
