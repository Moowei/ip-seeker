//package com.zjrb.ipip17mon.qiniu;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.util.InputMismatchException;
//
//public final class Locator implements ILocator {
//    public static final String VERSION = "0.1.2";
//    private static final Charset Utf8 = Charset.forName("UTF-8");
//    private final byte[] ipData;
//    private final int textOffset; //byte形式存储的IP库中前4个byte组成的int值
//    private final int[] index;
//    private final int[] indexData1;
//    private final int[] indexData2;
//    private final byte[] indexData3;
//
//    /**
//     *
//     * @param data byte[]类型的IP库数据
//     */
//    private Locator(byte[] data) {
//        this.ipData = data;
//        //从开始位置获取4byte的数据按大端模式组成int类型的textOffset
//        this.textOffset = bigEndian(data, 0);
//
//        //从第4个byte之后按小端模式获取256个int值
//        this.index = new int[256];
//        for (int i = 0; i < 256; i++) {
//            index[i] = littleEndian(data, 4 + i * 4);
//        }
//        // byte[0] byte[1] byte[2] byte[3] byte[4    至    1027]  byte[1028.............].......  byte[textOffset-1024 ~ textOffset-1] byte[textOffset]
//        // | --- 4个byte即textOffset --- | | 1024个byte即index |
//
//        int nidx = (textOffset - 4 - 1024 - 1024) / 8; //nidx表示从第1028个byte开始到第textOffset-1024个byte中间若8个byte一组，可以存多少组。也可以看成存多少个long（8个byte）型数据。
//        //且每一组的前四个byte按大端模式组合成int存入indexData1
//        //每一组的第4,第5,第6个byte按小端模式组成int存入indexData2
//        //每一组的第7个byte存入indexData3，
//        indexData1 = new int[nidx];
//        indexData2 = new int[nidx];
//        indexData3 = new byte[nidx];
//
//        for (int i = 0, off = 0; i < nidx; i++) {
//            off = 4 + 1024 + i * 8;
//            indexData1[i] = bigEndian(ipData, off);//前四个字节按大端模式组成的int
//            indexData2[i] = ((int) ipData[off + 6] & 0xff) << 16 | ((int) ipData[off + 5] & 0xff) << 8
//                    | ((int) ipData[off + 4] & 0xff);//第6,5,4个字节按小端模式组成的int，int组成为：0000000 byte[off+6] byte[off+5] byte[off+4]
//            indexData3[i] = ipData[off + 7]; //byte[off+7]单独作为一个字节存储。
//        }
//    }
//
//    /**
//     * 大端模式：类似讲数据看成字符串来存储
//     * eg:0x12345678
//     * 低地址--------
//     * buf[0] (0x12) -- 高位
//     * buf[1] (0x34)
//     * buf[2] (0x56)
//     * buf[3] (0x78) -- 低位
//     * 高地址--------
//     * @param data
//     * @param offset
//     * @return 从offset开始的4个buyte的数据,并按大端模式转化成int类型
//     */
//    static int bigEndian(byte[] data, int offset) {
//        int a = (((int) data[offset]) & 0xff);
//        int b = (((int) data[offset + 1]) & 0xff);
//        int c = (((int) data[offset + 2]) & 0xff);
//        int d = (((int) data[offset + 3]) & 0xff);
//        return (a << 24) | (b << 16) | (c << 8) | d;
//    }
//
//    /**
//     * 小端模式：
//     * eg: 0x12345678
//     * 低地址--------
//     * buf[0] (0x78) -- 低位
//     * buf[1] (0x56)
//     * buf[2] (0x34)
//     * buf[3] (0x12) -- 高位
//     * 高地址--------
//     * @param data
//     * @param offset
//     * @return
//     */
//    static int littleEndian(byte[] data, int offset) {
//        int a = (((int) data[offset]) & 0xff);
//        int b = (((int) data[offset + 1]) & 0xff);
//        int c = (((int) data[offset + 2]) & 0xff);
//        int d = (((int) data[offset + 3]) & 0xff);
//        return (d << 24) | (c << 16) | (b << 8) | a;
//    }
//
//    /**
//     * 将ipPart转成byte类型，且ipPart必须在(0,255)之间，且除数字0外IP字段不能以0开头（如01,013等）
//     * @param ipPart IP值中的一个字段
//     * @return 返回ipPart转换成的byte
//     */
//    static byte parseOctet(String ipPart) {
//        int octet = Integer.parseInt(ipPart);
//        //IP中除了字段本身为0外一般不会以0起始（如02,012），所以若以0起始且字段长度大于1的都认为格式错误。
//        if (octet < 0 || octet > 255 || (ipPart.startsWith("0") && ipPart.length() > 1)) {
//            throw new NumberFormatException("invalid ip part");
//        }
//        return (byte) octet;
//    }
//
//    /**
//     * 验证 strIP 是否满足IP格式，且将其切分成4个字段，每个字段转换成byte类型，存入到byte[4]的数组中并返回。
//     * eg: strIP = "163.204.172.142"      b[0]=163,b[1]=204,b[2]=172,b[3]=142
//     * @param strIP 字符串类型的IP
//     * @return byte[4]的数组，里面每个元素为strIP解析之后的各个字段的值
//     */
//    static byte[] textToNumericFormatV4(String strIP) {
//        //将传入的IP值按"."切分，并检查是否满足IP格式
//        String[] s = strIP.split("\\.");
//        if (s.length != 4) {
//            throw new NumberFormatException("the ip is not v4");
//        }
//        byte[] b = new byte[4];
//        b[0] = parseOctet(s[0]);
//        b[1] = parseOctet(s[1]);
//        b[2] = parseOctet(s[2]);
//        b[3] = parseOctet(s[3]);
//        return b;
//    }
//
//    static LocationInfo buildInfo(byte[] bytes, int offset, int len) {
//        String str = new String(bytes, offset, len, Utf8);
//        String[] ss = str.split("\t", -1);
//        if (ss.length == 4) {
//            return new LocationInfo(ss[0], ss[1], ss[2], "");
//        } else if (ss.length == 5) {
//            return new LocationInfo(ss[0], ss[1], ss[2], ss[4]);
//        } else if (ss.length == 3) {
//            return new LocationInfo(ss[0], ss[1], ss[2], "");
//        } else if (ss.length == 2) {
//            return new LocationInfo(ss[0], ss[1], "", "");
//        }
//        return null;
//    }
//
//    public static Locator loadFromNet(String netPath) throws IOException {
//        URL url = new URL(netPath);
//        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
//        httpConn.setConnectTimeout(3000);
//        httpConn.setReadTimeout(30 * 1000);
//        int responseCode = httpConn.getResponseCode();
//        if (responseCode != HttpURLConnection.HTTP_OK) {
//            return null;
//        }
//
//        int length = httpConn.getContentLength();
//        if (length <= 0 || length > 20 * 1024 * 1024) {
//            throw new InputMismatchException("invalid ip data");
//        }
//        InputStream is = httpConn.getInputStream();
//        byte[] data = new byte[length];
//        int downloaded = 0;
//        int read = 0;
//        while (downloaded < length) {
//            try {
//                read = is.read(data, downloaded, length - downloaded);
//            } catch (IOException e) {
//                is.close();
//                throw new IOException("read error");
//            }
//            if (read < 0) {
//                is.close();
//                throw new IOException("read error");
//            }
//            downloaded += read;
//        }
//
//        is.close();
//
//        return loadBinary(data);
//    }
//
//    /**
//     * 将IP库加载到byte数组当中
//     * @param filePath  IP库对应的路径
//     * @return
//     * @throws IOException
//     */
//    public static Locator loadFromLocal(String filePath) throws IOException {
//        File f = new File(filePath);
//        FileInputStream fi = new FileInputStream(f);
//        byte[] b = new byte[(int) f.length()];
//        try {
//            fi.read(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//            try {
//                fi.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//            throw e;
//        }
//        fi.close();
//
//        return loadBinary(b);
//    }
//
//    public static Locator loadFromStream(InputStream in) throws Exception {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[16 * 1024];
//        int n;
//
//        while ((n = in.read(buffer)) != -1) {
//            byteArrayOutputStream.write(buffer, 0, n);
//        }
//
//        return loadBinary(byteArrayOutputStream.toByteArray());
//    }
//
//    public static Locator loadBinary(byte[] ipdb) {
//        return new Locator(ipdb);
//    }
//
//    public static void main(String[] args) {
//        if (args == null || args.length < 2) {
//            System.out.println("locator ipfile ip");
//            return;
//        }
//        try {
//            Locator l = loadFromLocal(args[0]);
//            System.out.println(l.find(args[1]));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private int findIndexOffset(long ip, int start, int end) {
//        int mid = 0;
//        while (start < end) {
//            mid = (start + end) / 2;
//            long l = 0xffffffffL & ((long) indexData1[mid]);
//            if (ip > l) {
//                start = mid + 1;
//            } else {
//                end = mid;
//            }
//        }
//        long l = ((long) indexData1[end]) & 0xffffffffL;
//        if (l >= ip) {
//            return end;
//        }
//        return start;
//    }
//
//    /**
//     * 将字符串形式IP值转化成byte[4]数组，每个元素对应为IP的4个字段
//     * @param ip
//     * @return
//     */
//    public LocationInfo find(String ip) {
//        byte[] b;
//        try {
//            b = textToNumericFormatV4(ip);
//        } catch (Exception e) {
//            return null;
//        }
//        return find(b);
//    }
//
//    public LocationInfo find(byte[] ipBin) {
//        int end = indexData1.length - 1;//8个byte一组，可以有indexData1.length组，
//        int a = 0xff & ((int) ipBin[0]);
//        if (a != 0xff) {
//            end = index[a + 1];
//        }
//        long ip = (long) bigEndian(ipBin, 0) & 0xffffffffL;
//        int idx = findIndexOffset(ip, index[a], end);
//        int off = indexData2[idx];
//        return buildInfo(ipData, textOffset - 1024 + off, 0xff & (int) indexData3[idx]);
//    }
//
//    public LocationInfo find(int address) {
//        byte[] addr = new byte[4];
//
//        addr[0] = (byte) ((address >> 24) & 0xff);
//        addr[1] = (byte) ((address >> 16) & 0xff);
//        addr[2] = (byte) ((address >> 8) & 0xff);
//        addr[3] = (byte) (address & 0xff);
//
//        return find(addr);
//    }
//
//    public void checkDb() throws IOException {
//        byte[] addr = new byte[4];
//        try {
//            for (long x = 0; x < 0xffffffffL; x++) {
//                addr[0] = (byte) ((x >> 24) & 0xff);
//                addr[1] = (byte) ((x >> 16) & 0xff);
//                addr[2] = (byte) ((x >> 8) & 0xff);
//                addr[3] = (byte) (x & 0xff);
//                find(addr);
//            }
//        } catch (Exception e) {
//            throw new IOException(e.getMessage());
//        }
//    }
//}
