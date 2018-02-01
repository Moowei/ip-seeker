package com.zjrb.ipip17mon.thirdparty;

/**
 * Created by long on 2017/1/17.
 * 从IP库中查询IP的接口类
 */
public interface ILocator {
    /**
     * 根据String类型的IP查找,
     * 其实试讲String类型IP转换成byte[]，之后底层调用LocationInfo find(byte[] ipBin)
     * @param ip
     * @return
     */
    LocationInfo find(String ip);

    /**
     * 根据byte[]数组类型的IP查找
     * @param ipBin
     * @return
     */
    LocationInfo find(byte[] ipBin);

    LocationInfo find(int address);
}
