package com.moowei.ipip17mon.thirdparty;

/**
 * Created by blw on 2018/3/19 0019.
 */
public interface IRPCServer {
    public static final long versionID =10010;
    public String sayHi(String name );

    /**
     * 关闭服务，包括后台监听服务以及RPC服务
     */
    public void shutdownAll();

    /**
     * 从地市IP库查找当前IP对应的信息
     * @param strIP 要查找的IP
     * @return 地市信息的字符串
     */
    public String findIPForCity(String strIP);

    /**
     * 从县区库中查找当前IP对应的县区信息
     * @param strIP 要查找的IP
     * @return 区县信息的字符串
     */
    public String findIPForCounty(String strIP);
}
