package com.moowei.ipip17mon.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
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
public class IPDistrictExt {
    private static int offset;//datx文件的前四个字节存储的Int值，索引区的长度(indexLength)
    private static int[] index = new int[65536];//256*256个Int值（256*256*4个字节），每个Int值含义：Ip的前两个字段组成的Int值(IP[0]*256+IP[1])
    private static ByteBuffer dataBuffer;
    private static ByteBuffer indexBuffer;
    private static File ipFile ;
    private static ReentrantLock lock;

    static {
        lock = new ReentrantLock();
    }

    public static void main(String[] args) {
        load("D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180201\\quxian.datx");
        System.out.println(Arrays.toString(find("112.16.76.131")));
        Long st = System.nanoTime();
//        for (int i = 0; i < 10000; i++) {
//            find("183.129.129.129");
//        }
//        System.out.println((System.nanoTime() - st) / 1000 / 1000);
    }

    /**
     * 对外提供的方法，加载datxIP库并初始化
     * @param ipFilePath datxIp库的本地路径
     */
    public static void load(String ipFilePath) {
        ipFile = new File(ipFilePath);
        load();
    }

    /**
     * 内部方法，为类变量赋值:
     * static File ipFile;              //datx文件路劲构建的File类；
     * static ByteBuffer dataBuffer;    //datx文件读入内存封装成ByteBuffer类方便之后使用；
     * static ByteBuffer indexBuffer;   //从datx中抽取出索引区数据封装成ByteBuffer类，方便之后使用；
     * static int offset;               //datx文件的前四个字节存储的Int值，索引区的长度(indexLength)
     * static int[] index = new int[65536]; //IP前两个字段的索引。256*256个Int值（256*256*4个字节），每个Int值含义：Ip的前两个字段组成的Int值(IP[0]*256+IP[1])
     */
    private static void load() {
        FileInputStream fin = null;
        lock.lock();
        try {
            dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
            fin = new FileInputStream(ipFile);
            int readBytesLength;
            byte[] chunk = new byte[4096];
            while (fin.available() > 0) {
                readBytesLength = fin.read(chunk);
                dataBuffer.put(chunk, 0, readBytesLength);
            }
            dataBuffer.position(0);
            int indexLength = dataBuffer.getInt();//前四个字节存储的Int值，为indexLength
            byte[] indexBytes = new byte[indexLength];
            dataBuffer.get(indexBytes, 0, indexLength - 4);//indexBytes:dataBuffer[4]~dataBuffer[indexLength-4]
            indexBuffer = ByteBuffer.wrap(indexBytes);
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);//小端模式获取IpIndex
            offset = indexLength;

            for (int i = 0; i < 256; i++) {//前256*256*4个字节按小端模式，4个一组成Int，共256*256个Int值，index[256*256]
                for (int j = 0; j < 256; j++) {
                    index[i * 256 + j] = indexBuffer.getInt();
                }
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);//获取index之后将数据获取模式切换回大端模式
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
            lock.unlock();
        }
    }

    public static String[] find(String ip) {
        String[] ips = ip.split("\\.");//IP转成String数组
        int prefix_value = (Integer.valueOf(ips[0]) * 256 + Integer.valueOf(ips[1]));//前两个IP值组成Int
        long ip2long_value  = ip2long(ip);//将要查找的IP转成Long
        int start = index[prefix_value];//确定该IP在areaData的下标起始位置（避免每次从areaData区下标0开始查找）
        int max_comp_len = offset - 262148;//areaData对比查找的最大字节个数（实际可以在减小为offset-262144-4-(start*9)）
        long index_offset = -1;
        int index_length = -1;
        for (start = start * 13 + 262144; start < max_comp_len; start += 13) {
            if (int2long(indexBuffer.getInt(start)) <= ip2long_value) {
                if (int2long(indexBuffer.getInt(start + 4)) >= ip2long_value) {
                    index_offset = bytesToLong(indexBuffer.get(start + 11), indexBuffer.get(start + 10), indexBuffer.get(start + 9), indexBuffer.get(start + 8));
                    index_length = 0xFF & indexBuffer.get(start + 12);
                    break;
                }
            } else {
                break;
            }
        }

        if (index_offset == -1 && index_length == -1) {
            return null;
        }

        byte[] areaBytes;
        lock.lock();
        try {
            dataBuffer.position(offset + (int) index_offset - 262144);
            areaBytes = new byte[index_length];
            dataBuffer.get(areaBytes, 0, index_length);
        } finally {
            lock.unlock();
        }

        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    private static int str2Ip(String ip)  {
        String[] ss = ip.split("\\.");
        int a, b, c, d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    private static long ip2long(String ip)  {
        return int2long(str2Ip(ip));
    }

    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
}