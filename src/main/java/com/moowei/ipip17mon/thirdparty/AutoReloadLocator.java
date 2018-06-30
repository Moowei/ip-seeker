package com.moowei.ipip17mon.thirdparty;

/**
 * Created by blw on 2018/2/8 0008.
 */
public class AutoReloadLocator {
    static String  cityPath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\mydata4vipday2.datx";
    static String  countyPath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\quxian.datx";
//    static String path2 = "/opt/blw_test/php/mydata4vipday2.datx";
    public static FileWatchObserver fileWatchObserver;
//    only for test java -jar com.zjrb.ipip17mon.thirdparty.AutoReloadLocator 118.28.8.8
    public static void main(String[] args) {
        try {
            fileWatchObserver = new FileWatchObserver(cityPath, 1,countyPath,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String ip = "118.24.8.8";
        System.out.println(fileWatchObserver.getLocatorForCity().findLocationInfoByStrIp(ip));
        System.out.println(fileWatchObserver.getLocationInfoForCityByIP(ip));
        System.out.println(fileWatchObserver.getLocationInfoForCountyByIP(ip));
        System.out.println(fileWatchObserver.getLocatorForCounty().findLocationInfoByStrIp(ip));
        if (args != null && args.length > 0) {

        }
    }
}
