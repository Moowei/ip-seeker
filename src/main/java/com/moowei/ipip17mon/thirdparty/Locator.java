package com.moowei.ipip17mon.thirdparty;

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.InputMismatchException;

/**
 * Created by blw on 2018/2/6 0006.
 * 将区县IP库和地市IP库的通用方法抽取到封装成父类
 */
public abstract class Locator {
    protected final String VERSION = "0.0.1";   //版本
    protected String filePath;
    protected final Charset UTF_8 = Charset.forName("UTF-8");   //数据编码格式

    /**
     * 通过制定本地路径加载IP数据
     * @param filePath IP库文件路径（URL 或者本地路径，具体由子类实现）
     */
    protected abstract byte[] load(String filePath);

    /**
     * 通过URL获取IP数据
     * @param URL
     * @return
    protected abstract byte[] loadByURL(String URL);*/

    /**
     * 根据IP值获取该IP对应的详细地域和运营商信息,返回封装好的类
     * @param ipStr 要查找的IP
     * @return 返回封装的类
     */
    public abstract Object findLocationInfoByStrIp(String ipStr);

    /**
     * 根据IP值获取该IP对应的详细地域和运营商信息,返回String
     * @param ipStr 要查找的IP
     * @return 返回字符串信息"\t"分割
     */
    public abstract String getLocationInfoByStrIp(String ipStr);

    protected Locator(){}

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
    protected int bigEndian(byte[] data, int offset) {
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
    protected int littleEndian(byte[] data, int offset) {
        int d = (((int) data[offset + 3]) & 0xff);
        int c = (((int) data[offset + 2]) & 0xff);
        int b = (((int) data[offset + 1]) & 0xff);
        int a = (((int) data[offset]) & 0xff);
        return (d << 24) | (c << 16) | (b << 8) | a;
    }


    /**
     * 将String类型的IP转成byte[]
     * 详细：验证 strIP 是否满足IP格式，且将其切分成4个字段，每个字段转换成byte类型，组成一个byte[4]的数组并返回。
     * eg: strIP = "163.204.172.142"      b[0]=163,b[1]=204,b[2]=172,b[3]=142
     * @param ipStr 字符串类型的IP（如："163.204.172.142"）
     * @return byte[4]的数组，里面元素依次为strIP解析之后对应各个字段的值
     */
    protected byte[] strToBytes(String ipStr) {
        String[] ipSplitedArr = ipStr.split("\\.");
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
     * 将字符串形式的数值ipPart转成byte类型，
     * 注意：ipPart必须在(0,255)之间，且除数字0外IP字段不能以0开头（如01,013等），不满足要求时抛出异常NumberFormatException
     * @param ipPart IP值中的一个字段
     * @return 返回ipPart转换成的byte
     */
    private byte parseOctet(String ipPart) {
        int octet = Integer.parseInt(ipPart);
        if (octet < 0 || octet > 255 ) {  //IP中除了字段本身为0外一般不会以0起始（如02,012），所以若以0起始且字段长度大于1的都认为格式错误。
            throw new NumberFormatException("invalid ip part");
        }
        return (byte) octet;
    }

    /**
     * 通过URL链接加载IP库
     * @param netPath
     * @return
     * @throws IOException
     */
    public static byte[] loadFromNet(String netPath) throws IOException {
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
        return data;
    }

    /**
     * 从本地目录加载iP库
     * @param filePath IP的本机地址
     * @return
     * @throws IOException
     */
    protected byte[] loadFromLocal(String filePath){
        File ipFile = new File(filePath);
        FileInputStream fi = null;
        byte[] ipByteArr = new byte[(int) ipFile.length()];
        try {
            fi = new FileInputStream(ipFile);
            fi.read(ipByteArr);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            IOUtils.closeQuietly(fi);
        }
        return ipByteArr;
    }

    /**
     * 从InputStream流加载IP库
     * @param in
     * @return
     * @throws Exception
     */
    protected byte[] loadFromStream(InputStream in) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[16 * 1024];
        int n;
        while ((n = in.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, n);
        }
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * 在indexData1[]中二分查找指定的IP,
     * @param ip 要查找的IP
     * @param start endIpIndex[]起始位置下标，即要查找的IP值所对应的位置信息在areaData区的下标
     * @param end endIpIndex[]终止位置下标，
     * @return 终止位置的下标
     */
    protected int binarySearch(long ip, int start, int end,int[] endIpIndex) {
        int midIndex = 0;
        while (start < end) {
            midIndex = (start + end) / 2;
            long midIndexIpLong = intToLong(endIpIndex[midIndex]);
            if (ip > midIndexIpLong) {
                start = midIndex + 1;
            } else {
                end = midIndex;
            }
        }
        long endIndexIpLong = intToLong(endIpIndex[end]);
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
    protected int binarySearchNew(long ip, int start, int end,int[] endIpIndex) {
        int midIndex = (start + end) / 2;
        while (start < end) {
            long midIndexIpLong = 0xffffffffL & ((long) endIpIndex[midIndex]);
            if (ip > midIndexIpLong) {
                if(ip <= (0xffffffffL & ((long) endIpIndex[midIndex+1]))){
                    return midIndex + 1;
                }
                start = midIndex + 1;
            } else {
                if(midIndex!= 0 && (0xffffffffL & ((long) endIpIndex[midIndex-1]))<ip && (ip <= (0xffffffffL & ((long) endIpIndex[midIndex])))){
                    return midIndex;
                }
                end = midIndex;
            }
            midIndex = (start + end) / 2;
        }
        return end;
    }


    /**
     * Int类型转成Long型
     * @param i
     * @return
     */
    protected long intToLong(int i) {
        long l = i & 0x7fffffffL;
        if (i < 0) {
            l |= 0x080000000L;
        }
        return l;
    }

    /**
     * 将Int类型的IP值转化成Byte数组类型的IP值,每个Byte存储Ip中的一个字段
     * @param ipInt Int类型的IP值
     * @return byte数组,其中每个byte存储一个IP字段
     */
    protected byte[] intToByteArr(int ipInt) {
        byte[] addr = new byte[4];
        addr[0] = (byte) ((ipInt >> 24) & 0xff);
        addr[1] = (byte) ((ipInt >> 16) & 0xff);
        addr[2] = (byte) ((ipInt >> 8) & 0xff);
        addr[3] = (byte) (ipInt & 0xff);
        return addr;
    }

    /**
     * 正则表达式校验是否是合法的IP格式
     * @param ipStr String类型的ip值
     * @return 是否是IP格式
     */
    protected boolean isIpStr(String ipStr){
        if (ipStr != null && !ipStr.isEmpty() && ipStr.contains(".")) {
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
            "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
           return ipStr.matches(regex);
        }
        return false;
    }


}
