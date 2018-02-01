package com.zjrb.ipip17mon.demo;

/**
 * Created by blw on 2018/1/29 0029.
 * IPIP.net 收费版，datax解析代码
 */
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class IPExt {
    public static void main(String[] args) {
        String filePath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180129\\mydata4vipday2.datx";

        IPExt.load(filePath);
        System.out.println(Arrays.toString(IPExt.find("8.8.8.8")));
        System.out.println("122222222222222222222222222");
        System.out.println(Arrays.toString(IPExt.find("118.28.8.8")));
        System.out.println(Arrays.toString(IPExt.find("255.255.255.255")));
    }

    @Test
    public void IPTest(){
        String filePath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180129\\mydata4vipday2.datx";

        IPExt.load(filePath);
        System.out.println(Arrays.toString(IPExt.find("118.28.8.8")));
        System.out.println(Arrays.toString(IPExt.find("255.255.255.255")));
        System.out.println("122222222222222222222222222");
    }



    //是否自动更新
    public static boolean enableFileWatch = false;

    private static int offset;//dataBuffer的下标
    private static int[] index = new int[65536];
    private static ByteBuffer dataBuffer;//IP库
    private static ByteBuffer indexBuffer;//
    private static Long lastModifyTime = 0L;
    private static File ipFile ;//IP文件的路径生成的file
    private static ReentrantLock lock = new ReentrantLock();

    public static void load(String filename) {
        ipFile = new File(filename);
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static void load(String filename, boolean strict) throws Exception {
        ipFile = new File(filename);
        if (strict) {
            int contentLength = Long.valueOf(ipFile.length()).intValue();
            if (contentLength < 512 * 1024) {
                throw new Exception("ip data file error.");
            }
        }
        load();
        if (enableFileWatch) {
            watch();
        }
    }

    public static String[] find(String ip) {//118.28.8.8
        String[] ips = ip.split("\\.");
        System.out.println(Integer.valueOf(ips[0]));//118
        //将前两个IP成分（118,28）组成Int值，作为索引
        int prefix_value = (Integer.valueOf(ips[0]) * 256 + Integer.valueOf(ips[1]));
        //将String类型IP转化成Long
        long ip2long_value = ip2long(ip);
        //index区域中查找下标为prefix_value的值，
        int start = index[prefix_value];
        //offset是dataBuffer的下标,max_comp_len为区域的相对长度
        int max_comp_len = offset - 262144 - 4;//1024*256 = 262144 =256 * 256 *4
        long tmpInt;
        long index_offset = -1;
        int index_length = -1;
        byte b = 0;
        //start 是相对 indexBuffer 中的下标，而不是 dataBuffer
        //这里使用的是遍历，而不是二分查找，效率很低！！！！！！！！！！！差评
        for (start = start * 9 + 262144; start < max_comp_len; start += 9) {
            tmpInt = int2long(indexBuffer.getInt(start));
            if (tmpInt >= ip2long_value) {
                //index_offset
                index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
                index_length = ((0xFF & indexBuffer.get(start + 7)) << 8) + (0xFF & indexBuffer.get(start + 8));
                break;
            }
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
//        System.out.println(new String(areaBytes, Charset.forName("UTF-8")).toString());
        return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
    }

    private static void watch() {
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long time = ipFile.lastModified();
                if (time > lastModifyTime) {
                    load();
                }
            }
        }, 1000L, 5000L, TimeUnit.MILLISECONDS);
    }

    /**
     * dataBuffer  加载datax的IP数据库到内存
     * indexBuffer: dataBuffer中读取indexLength-4个字节。dataBuffer[0-3]组成的Int值= indexLength
     */
    private static void load() {
        lastModifyTime = ipFile.lastModified();
        lock.lock();
        try {
            dataBuffer = ByteBuffer.wrap(getBytesByFile(ipFile));
            dataBuffer.position(0);
            //读取四个字节组成一个INT返回，其数值就是indexLength（即索引结束的下标），即数组长度
            offset = dataBuffer.getInt(); // indexLength
            //创建byte数组：indexBytes，用于存放索引
            byte[] indexBytes = new byte[offset];
            //从IP数据库dataBuffer中读取offset - 4个字节，存放到indexBytes下标为0至offset - 4
            dataBuffer.get(indexBytes, 0, offset - 4);
            //将数组indexBytes再次封装成ByteBuffer
            indexBuffer = ByteBuffer.wrap(indexBytes);
            //使用小端模式，byte[3]byte[2]byte[1]byte[0]，大端模式：byte[0]byte[1]byte[2]byte[3]
            indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 256; j++) {
                    index[i * 256 + j] = indexBuffer.getInt();
                }
            }
            indexBuffer.order(ByteOrder.BIG_ENDIAN);
        } finally {
            lock.unlock();
        }
    }

    private static byte[] getBytesByFile(File file) {
        FileInputStream fin = null;
        byte[] bs = new byte[new Long(file.length()).intValue()];
        try {
            fin = new FileInputStream(file);
            int readBytesLength = 0;
            int i;
            while ((i = fin.available()) > 0) {
                fin.read(bs, readBytesLength, i);
                readBytesLength += i;
            }
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
        }

        return bs;
    }

    private static long bytesToLong(byte a, byte b, byte c, byte d) {
        return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
    }

    /**
     * String类型的IP转成Long型
     * @param ip
     * @return
     */
    private static long ip2long(String ip)  {
        return int2long(str2Ip(ip));
    }

    /**
     * String 类型的IP转成Int类型值
     * @param ip
     * @return
     */
    private static int str2Ip(String ip)  {
        String[] ss = ip.split("\\.");
        int a, b, c, d;
        a = Integer.parseInt(ss[0]);
        b = Integer.parseInt(ss[1]);
        c = Integer.parseInt(ss[2]);
        d = Integer.parseInt(ss[3]);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    /**
     * Int类型转成Long型
     * @param i
     * @return
     */
    private static long int2long(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }
}

