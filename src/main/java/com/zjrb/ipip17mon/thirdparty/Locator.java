package com.zjrb.ipip17mon.thirdparty;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.InputMismatchException;

public final class Locator implements ILocator {
    public static final String VERSION = "0.1.2";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    //IP库加载到内存后的引用
    private final byte[] ipData;
    //首4个字节按大端模式存储的数据；与IP库中记录地域数据与运营商信息的索引有关。,
    private final int areaDataIndex;
    //第4个byte之后按小端模式获取256个int值，存放数字0~255（IP库中endIp的第一个字段）的索引
    private final int[] ipIndex;
    //存放Ip库中每条记录的Long型IP值的数组
    private final int[] indexData1;
    //存放IP库中每条记录地域和运营商（\t分隔的字符串）信息索引的数组。
    private final int[] indexData2;
    //存放IP库中每条记录地域和运营商（\t分隔的字符串）信息长度的byte数组
    private final byte[] indexData3;

    /**
     * 带参数的构造，只能内部调
     * 为ipData、areaDataIndex、ipIndex、indexData1、indexData2、indexData3六个字段赋值，即初始化
     * @param data byte[]形式的Ip库
     */
    private Locator(byte[] data) {
        this.ipData = data;
        this.areaDataIndex = bigEndian(data, 0);
        this.ipIndex = new int[256];
        for (int i = 0; i < 256; i++) {
            ipIndex[i] = littleEndian(data, 4 + i * 4);
        }
        //IP库中包含的记录的条数ipDataCount，每条记录的索引对应8个byte，由三个部分组成，byte[0]~byte[3]组成indexData1[]；byte[4]~byte[6]组成indexData2[]，byte[7]组成indexData3[]
        int ipDataCount = (areaDataIndex - 4 - 1024 - 1024) / 8;
        indexData1 = new int[ipDataCount];
        indexData2 = new int[ipDataCount];
        indexData3 = new byte[ipDataCount];
        for (int i = 0, off = 0; i < ipDataCount; i++) {
            //每条记录的索引从ipData中第4 + 1024个字节之后开始，
            off = 4 + 1024 + i * 8;
            indexData1[i] = bigEndian(ipData, off);
            indexData2[i] = ((int) ipData[off + 6] & 0xff) << 16 | ((int) ipData[off + 5] & 0xff) << 8
                    | ((int) ipData[off + 4] & 0xff);
            indexData3[i] = ipData[off + 7];
        }
    }

    /**
     * 大端模式：类似讲数据看成字符串来存储
     * eg:0x12345678
     * 低地址--------
     * buf[0] (0x12) -- 高位
     * buf[1] (0x34)
     * buf[2] (0x56)
     * buf[3] (0x78) -- 低位
     * 高地址--------
     * @param data 要转换的数据
     * @param offset 下标
     * @return 从offset开始的4个buyte的数据,并按大端模式转化成int类型
     */
    static int bigEndian(byte[] data, int offset) {
        int a = (((int) data[offset]) & 0xff);
        int b = (((int) data[offset + 1]) & 0xff);
        int c = (((int) data[offset + 2]) & 0xff);
        int d = (((int) data[offset + 3]) & 0xff);
        return (a << 24) | (b << 16) | (c << 8) | d;
    }

    /**
     * 小端模式：
     * eg: 0x12345678
     * 低地址--------
     * buf[0] (0x78) -- 低位
     * buf[1] (0x56)
     * buf[2] (0x34)
     * buf[3] (0x12) -- 高位
     * 高地址--------
     * @param data 要按照小端模式转换的byte[]形式的数据
     * @param offset 开始的下标
     * @return 拼成的int类型的数据
     */
    static int littleEndian(byte[] data, int offset) {
        int d = (((int) data[offset + 3]) & 0xff);
        int c = (((int) data[offset + 2]) & 0xff);
        int b = (((int) data[offset + 1]) & 0xff);
        int a = (((int) data[offset]) & 0xff);
        return (d << 24) | (c << 16) | (b << 8) | a;
    }

    /**
     * 将ipPart转成byte类型，且ipPart必须在(0,255)之间，且除数字0外IP字段不能以0开头（如01,013等），不满足要求时抛出异常NumberFormatException
     * @param ipPart IP值中的一个字段
     * @return 返回ipPart转换成的byte
     */
    static byte parseOctet(String ipPart) {
        int octet = Integer.parseInt(ipPart);
        //IP中除了字段本身为0外一般不会以0起始（如02,012），所以若以0起始且字段长度大于1的都认为格式错误。
        if (octet < 0 || octet > 255 || (ipPart.startsWith("0") && ipPart.length() > 1)) {
            throw new NumberFormatException("invalid ip part");
        }
        return (byte) octet;
    }

    /**
     * 验证 strIP 是否满足IP格式，且将其切分成4个字段，每个字段转换成byte类型，组成一个byte[4]的数组并返回。
     * eg: strIP = "163.204.172.142"      b[0]=163,b[1]=204,b[2]=172,b[3]=142
     * @param str 字符串类型的IP（如："163.204.172.142"）
     * @return byte[4]的数组，里面元素依次为strIP解析之后对应各个字段的值
     */
    static byte[] textToNumericFormatV4(String str) {
        String[] ipSplitedArr = str.split("\\.");
        if (ipSplitedArr.length != 4) {
            throw new NumberFormatException("the ip is not v4");
        }
        byte[] ipByteArr = new byte[4];
        ipByteArr[0] = parseOctet(ipSplitedArr[0]);
        ipByteArr[1] = parseOctet(ipSplitedArr[1]);
        ipByteArr[2] = parseOctet(ipSplitedArr[2]);
        ipByteArr[3] = parseOctet(ipSplitedArr[3]);
        return ipByteArr;
    }

    /**
     * 将输入的字节数组按UTF_8解析，组成String，并对得到的String按\t分隔，封装成LocationInfo
     * 构造方法：LocationInfo(String country, String state, String city, String isp)
     * @param bytes byte[]形式的字符串（eg: "中国\t上海".getBytes()）
     * @param offset byte[]中从哪个下标开始获取byte
     * @param len 要获取的字节数
     * @return 封装的存有地址信息的LocationInfo
     */
    static LocationInfo buildInfo(byte[] bytes, int offset, int len) {
        String str = new String(bytes, offset, len, UTF_8);
        String[] ss = str.split("\t", -1);
//        System.out.println(str  + "" + ss.length);
        if (ss.length == 4) {
            return new LocationInfo(ss[0], ss[1], ss[2], "");
        } else if (ss.length == 5) {
            return new LocationInfo(ss[0], ss[1], ss[2], ss[4]);
        } else if (ss.length == 3) {
            return new LocationInfo(ss[0], ss[1], ss[2], "");
        } else if (ss.length == 2) {
            return new LocationInfo(ss[0], ss[1], "", "");
        }
        return null;
    }

    public static Locator loadFromNet(String netPath) throws IOException {
        URL url = new URL(netPath);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setConnectTimeout(3000);
        httpConn.setReadTimeout(30 * 1000);
        int responseCode = httpConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            return null;
        }

        int length = httpConn.getContentLength();
        if (length <= 0 || length > 20 * 1024 * 1024) {
            throw new InputMismatchException("invalid ip data");
        }
        InputStream is = httpConn.getInputStream();
        byte[] data = new byte[length];
        int downloaded = 0;
        int read = 0;
        while (downloaded < length) {
            try {
                read = is.read(data, downloaded, length - downloaded);
            } catch (IOException e) {
                is.close();
                throw new IOException("read error");
            }
            if (read < 0) {
                is.close();
                throw new IOException("read error");
            }
            downloaded += read;
        }

        is.close();

        return loadBinary(data);
    }

    /**
     * 从本地目录加载iP库
     * @param filePath IP的本机地址
     * @return
     * @throws IOException
     */
    public static Locator loadFromLocal(String filePath) throws IOException {
        File f = new File(filePath);
        FileInputStream fi = new FileInputStream(f);
        byte[] b = new byte[(int) f.length()];
        try {
            fi.read(b);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                fi.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
        fi.close();

        return loadBinary(b);
    }

    public static Locator loadFromStream(InputStream in) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[16 * 1024];
        int n;

        while ((n = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, n);
        }

        return loadBinary(byteArrayOutputStream.toByteArray());
    }

    public static Locator loadBinary(byte[] ipdb) {
        return new Locator(ipdb);
    }

    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("locator ipfile ip");
            return;
        }
        try {
            Locator locator = loadFromLocal(args[0]);
            System.out.println(locator.find(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 在indexData1[]中二分查找指定的IP,
     * @param ip 要查找的IP
     * @param start indexData1[]起始位置下标
     * @param end indexData1[]终止位置下标
     * @return 终止位置的下标
     */
    private int findIndexOffset(long ip, int start, int end) {
        int midIndex = 0;
        while (start < end) {
            midIndex = (start + end) / 2;
            long midIndexIpLong =  ((long) indexData1[midIndex]) & 0xffffffffL;
            if (ip > midIndexIpLong) {
                start = midIndex + 1;
            } else {
                end = midIndex;
            }
        }
        long endIndexIpLong = ((long) indexData1[end]) & 0xffffffffL;
        if (endIndexIpLong >= ip) {
            return end;
        }
        return start;
    }

    /**
     * 修正后的二分查找，避免了很多多余的查找
     * 在indexData1[]中二分查找指定的IP,
     * @param ip 要查找的IP，Long型
     * @param start 存储数据的数组起始位置下标
     * @param end 存储数据的数组终止位置下标
     * @return 终止位置的下标
     */
    private int findIndexOffsetNew(long ip, int start, int end) {
        int midIndex = (start + end) / 2;
        while (start < end) {
            long midIndexIpLong = 0xffffffffL & ((long) indexData1[midIndex]);
            if (ip > midIndexIpLong) {
                if(ip <= (0xffffffffL & ((long) indexData1[midIndex+1]))){
                    return midIndex + 1;
                }
                start = midIndex + 1;
            } else {
                if(midIndex!= 0 && (0xffffffffL & ((long) indexData1[midIndex-1]))<ip && (ip <= (0xffffffffL & ((long) indexData1[midIndex])))){
                    return midIndex;
                }
                end = midIndex;
            }
            midIndex = (start + end) / 2;
        }
        return end;
    }

    @Override
    public LocationInfo find(String ip) {
        byte[] b;
        try {
            b = textToNumericFormatV4(ip);
        } catch (Exception e) {
            return null;
        }
        return find(b);
    }

    /**
     * 对应新的二分查找算法
     * @param ip String类型的Ip值
     * @return 返回封装好的LocationInfo
     */
    public LocationInfo findNew(String ip) {
        byte[] b;
        try {
            b = textToNumericFormatV4(ip);
        } catch (Exception e) {
            return null;
        }
        return findNew(b);
    }

    /**
     * 从IP库中查询指定的IP
     * @param ipBin byte数组指定的iP 192.168.1.1 -》 [192][168][1][1]
     * @return 返回LocationInfo
     */
    @Override
    public LocationInfo find(byte[] ipBin) {
        //另end等于indexData1中元素的个数-1
        int end = indexData1.length - 1;
        //获取第一个IP字段
        int a = 0xff & ((int) ipBin[0]);
        //第一个IP字段不等于255，则end =
        if (a != 0xff) {
            end = ipIndex[a + 1];
        }
        //将ipBini转化成Long型的数字
        long ip = (long) bigEndian(ipBin, 0) & 0xffffffffL;

        int idx = findIndexOffset(ip, ipIndex[a], end);
        int off = indexData2[idx];
        //ipData即以bytes[]存储的库
        return buildInfo(ipData, areaDataIndex - 1024 + off, 0xff & (int) indexData3[idx]);
    }

    /**
     * 调用新的二分查找算法
     * 从IP库中查询指定的IP,
     * @param ipBin byte数组指定的iP 192.168.1.1 -》 [192][168][1][1]
     * @return 返回LocationInfo
     */
    public LocationInfo findNew(byte[] ipBin) {
        //另end等于indexData1中元素的个数-1
        int end = indexData1.length - 1;
        //获取第一个IP字段
        int a = 0xff & ((int) ipBin[0]);
        //第一个IP字段不等于255，则end =
        if (a != 0xff) {
            end = ipIndex[a + 1];
        }
        //将ipBini转化成Long型的数字
        long ip = (long) bigEndian(ipBin, 0) & 0xffffffffL;
        int idx = findIndexOffsetNew(ip, ipIndex[a], end);
        int off = indexData2[idx];
        //ipData即以bytes[]存储的库
        return buildInfo(ipData, areaDataIndex - 1024 + off, 0xff & (int) indexData3[idx]);
    }

    @Override
    public LocationInfo find(int address) {
        byte[] addr = new byte[4];
        addr[0] = (byte) ((address >> 24) & 0xff);
        addr[1] = (byte) ((address >> 16) & 0xff);
        addr[2] = (byte) ((address >> 8) & 0xff);
        addr[3] = (byte) (address & 0xff);
        return find(addr);
    }

    /**
     * 测试批处理耗时
     * @param mmber 批处理数量
     * @throws IOException
     */
    public void checkDb(Long mmber) throws IOException {
        byte[] addr = new byte[4];
        try {
            for (long x = 0; x < mmber; x++) {
                addr[0] = (byte) ((x >> 24) & 0xff);
                addr[1] = (byte) ((x >> 16) & 0xff);
                addr[2] = (byte) ((x >> 8) & 0xff);
                addr[3] = (byte) (x & 0xff);
                find(addr);
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
