package com.moowei.ipip17mon.thirdparty;

import java.io.File;

/**
 * Created by blw on 2018/1/31 14:10:00
 * IPIP.net 收费版，针对县区的IP库datx解析代码
 * 内部枚举类实现的伪单利模式：当文件被修改后会重新加载文件并创建新的实例，若采用的文件没有改动，则保持单例。
 */

public final class LocatorImplForCounty extends Locator {
    private final byte[] ipData;      //IP库加载到内存后的引用
    private final int areaDataIndex;  //首4个字节按大端模式存储的数据；与IP库中记录地域数据与运营商信息的索引有关。,
    private final int[] ipIndex;      //第4个byte之后按小端模式获取256个int值，存放数字0~255（IP库中endIp的第一个字段）的索引
    private final int[] indexData1;   //存放Ip库中每条记录的Long型END_IP值的数组
    private final int[] indexData2;   //存放IP库中每条记录地域和运营商（\t分隔的字符串）信息索引的数组。即当前IP段对应的地域和运营商信息在indexData3区域中的下标。
    private final int[] indexData3;   //存放IP库中每条记录地域和运营商（\t分隔的字符串）信息长度的byte数组

    /**
     * 内部枚举类实现的伪单利模式：当文件被修改后会重新加载文件并创建新的实例，若采用的文件没有改动，则保持单例。
     */
    private enum SingleHolder{
        INSTANCE;
        private LocatorImplForCounty locatorImplForCounty = null;
        private Long lastModified;
        private LocatorImplForCounty getLocatorImplForCountyInstance(String datxFilePath){
            Long modified = new File(datxFilePath).lastModified();
            if((locatorImplForCounty == null) || (!lastModified.equals(modified))){
                System.out.println("创建新对象\tIP库路径："+datxFilePath);
                locatorImplForCounty = new LocatorImplForCounty(datxFilePath);
                this.lastModified = modified;
            }
            return locatorImplForCounty;
        }
    }

    public static LocatorImplForCounty getInstance(String ipFilePathForDatx){
        return LocatorImplForCounty.SingleHolder.INSTANCE.getLocatorImplForCountyInstance(ipFilePathForDatx);
    }

    private LocatorImplForCounty(String ipFilePathForDatx) {
        byte[] ipDataArr = load(ipFilePathForDatx);
        this.ipData = ipDataArr;
        this.areaDataIndex = bigEndian(ipData, 0);
        this.ipIndex = new int[65536];
        for (int i = 0; i < 65536; i++) {
            ipIndex[i] = littleEndian(ipData, 4 + i * 4);
        }
        int ipDataCount = (areaDataIndex - 4 - 262144 - 262144) / 13;//IP库中包含的记录的条数 ipDataCount ，每条记录的索引对应13个 byte，由三个部分组成，byte[0]~byte[3] 组成startIP ,byte[4]~byte[7]组成endIP即indexData1[]；byte[8]~byte[11]组成indexData2[]，byte[13]组成indexData3[]
        indexData1 = new int[ipDataCount];
        indexData2 = new int[ipDataCount];
        indexData3 = new int[ipDataCount];
        for (int i = 0, off = 0; i < ipDataCount; i++) {
            off = 4 + 262144 + i * 13;
            indexData1[i] = bigEndian(this.ipData, off+4); //四个字节组成Int,END_IP,取的时候需要转成Long型使用，因为Int会有负数
            indexData2[i] = ((int) this.ipData[off + 11] & 0xff) << 24 | ((int) this.ipData[off + 10] & 0xff) << 16 | ((int) this.ipData[off + 9] & 0xff) << 8
                    | ((int) this.ipData[off + 8] & 0xff);   // 8,9,10,11字节按小端模式组成int，area区的指针
            indexData3[i] = ((int) this.ipData[off + 12] & 0xff);    //长度
        }
    }

    @Override
    public byte[] load(String filePath) {
        return super.loadFromLocal(filePath);
    }

    @Override
    public Object findLocationInfoByStrIp(String ipStr) {
        LocationInfoForCounty locationInfoForCounty = null;
        try {
            locationInfoForCounty = new LocationInfoForCounty(getLocationInfoByStrIp(ipStr),"\t");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return locationInfoForCounty;
    }


    @Override
    public String getLocationInfoByStrIp(String ipStr) {
        if(isIpStr(ipStr)){
            byte[] ipByteArr = super.strToBytes(ipStr);
            int end = indexData1.length - 1;    //另end等于indexData1中元素的个数-1
            int ipPrefixValue = (0xff & ((int) ipByteArr[0])) << 8 | (0xff & ((int) ipByteArr[1])); //IP的前两个字节组成的int,永远为正
            if (ipPrefixValue != 0xffff) {  //第一个IP字段不等于255，end为ipIndex中下标为(ipPrefixValue+1)的值，即areaIndex区的下标
                end = ipIndex[ipPrefixValue + 1];
            }
            long ipLong = super.intToLong(bigEndian(ipByteArr, 0));//Ip值按大端模式转化成Long型
            int idx = super.binarySearch(ipLong, ipIndex[ipPrefixValue], end,indexData1);
            int off = indexData2[idx];//目标IP值对应的位置信息在areaData区的下标
            int length = indexData3[idx];//目标IP对应的位置信息的长度（字节个数）
            return new String(ipData, areaDataIndex - 262144 + off, length, super.UTF_8);
        }else {
            return "IP字符格式错误";
        }

    }
}
