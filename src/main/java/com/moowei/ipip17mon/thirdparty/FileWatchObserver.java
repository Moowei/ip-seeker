package com.moowei.ipip17mon.thirdparty;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by blw on 2018/2/17.
 * Observer Pattern 观察者模式中实现java.util.Observer接口的观察者
 * 使用观察者模式监听文件是否改变，实现自动加载变化后的Ip库
 * */


public final class FileWatchObserver implements Observer {

    private final FileWatchSubject watchService;
    private String filePathForCity = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\mydata4vipday2.datx";
    private String filePathForCounty = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\quxian.datx";
    private LocatorImplForCity locatorForCity;
    private LocatorImplForCounty locatorForCounty;
    private Object objForCity,objForCounty;


    public FileWatchObserver(String iPFilePathForCity, int intervalForCity,String ipFilePathForCounty, int intervalForCounty) throws Exception {
        //加载IP库
        if (iPFilePathForCity != null){
            this.filePathForCity = iPFilePathForCity;
        }
        if (ipFilePathForCounty != null){
            this.filePathForCounty = ipFilePathForCounty;
        }
        objForCity = LocatorImplForCity.getInstance(filePathForCity);
        if(objForCity instanceof LocatorImplForCity){ //初始化地市IP数据库
            locatorForCity = (LocatorImplForCity)objForCity;
        }else {
            throw new Exception("地市IP库初始化失败！");
        }
        objForCounty = LocatorImplForCounty.getInstance(filePathForCounty);
        if(objForCounty instanceof LocatorImplForCounty){ //初始化县区IP数据库
            locatorForCounty = (LocatorImplForCounty)objForCounty;
        }else {
            throw new Exception("区县IP库初始化失败！");
        }
        //创建通知这，并将当前的监听类加入其通知列表，并启动线程进行监听
        watchService = new FileWatchSubject();
        watchService.addObserver(this);
        watchService.excuteForCity(iPFilePathForCity,intervalForCity);
        watchService.excuteForCounty(ipFilePathForCounty,intervalForCounty);
    }

    //接受到通知，之后重新加载IP库
    @Override
    public void update(Observable o, Object arg) {
        if((arg instanceof String) && String.valueOf(arg).equals("city")){
            System.out.println("地市IP库更新了!!重新装载中......");
            objForCity = LocatorImplForCity.getInstance(filePathForCity);
            if(objForCity instanceof LocatorImplForCity){
                locatorForCity = (LocatorImplForCity)objForCity;
            }
        }
        if((arg instanceof String) && String.valueOf(arg).equals("county")){
            System.out.println("县区IP库更新了!!重新装载中......");
            objForCounty = LocatorImplForCounty.getInstance(filePathForCounty);
            if(objForCounty instanceof LocatorImplForCounty){
                locatorForCounty = (LocatorImplForCounty)objForCounty;
            }
        }
    }

    /**
     * 关闭对地市IP库和区县IP库的监听
     */
    public void shutdown() {
        watchService.deleteObserver(this);
        watchService.shutdownAll();
    }

    /**
     * 获取内存中加载的地市IP库对象 LocatorImplForCity
     * @return LocatorImplForCity的对象实例
     */
    public LocatorImplForCity getLocatorForCity() {
        return locatorForCity;
    }

    /**
     * 在地市IP库中查询指定ip
     * @param ip 要查询的String类型的IP
     * @return 该IP对应的地市信息吗，字符串形式
     */
    public String getLocationInfoForCityByIP(String ip) {
        if((locatorForCity != null) && (locatorForCity.findLocationInfoByStrIp(ip) instanceof LocationInfoForCity)){
            return ((LocationInfoForCity)locatorForCity.findLocationInfoByStrIp(ip)).toString();
        }else {
            return null;
        }
    }

    /**
     * 获取内存中加载的区县IP库对象
     * @return LocatorImplForCounty的对象实例
     */
    public LocatorImplForCounty getLocatorForCounty() {
        return locatorForCounty;
    }

    /**
     * 在区县IP库中查找指定的IP值
     * @param ip 要查找的字符串形式的Ip
     * @return IP对应的区县的信息
     */
    public String getLocationInfoForCountyByIP(String ip) {
        if((locatorForCounty != null) && (locatorForCounty.findLocationInfoByStrIp(ip) instanceof LocationInfoForCounty)){
            return  ((LocationInfoForCounty)locatorForCounty.findLocationInfoByStrIp(ip)).toString();
        }else {
            return null;
        }
    }
}
