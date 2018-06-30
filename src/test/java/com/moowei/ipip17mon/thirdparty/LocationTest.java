/*
package com.zjrb.ipip17mon.thirdparty;

import com.zjrb.ipip17mon.demo.IPDistrictExt;
import com.zjrb.ipip17mon.util.ReadFromFileUtil;
import com.zjrb.ipip17mon.util.WriteToFileUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

*/
/**
 * @Author BLW
 * @Time 2018-01-30
 * 用来解析datax格式文件。
 *//*

public class LocationTest {
//    private String datFileName = "D:\\USER\\BLW\\Desktop\\Data warehouse\\Cluster_Test\\IP\\ipip.net\\ipip.net-official\\c-master\\dat\\17monipdb.dat";
    private String datFileName ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180129\\mydata4vipday2.datx";
    private StringBuffer stringBuffer = new StringBuffer();
    private StringBuffer stringBufferQX = new StringBuffer();
    private StringBuffer stringBufferError = new StringBuffer();

    //小端模式解析测试
    @Test
    public void testLittleEndian() {
        byte[] x = new byte[]{1, 2, 3, 4};
        Assert.assertEquals(0x04030201, LocatorImplForCity.littleEndian(x, 0));
        byte[] x2 = new byte[]{0, 1, 2, 3, 4};
        Assert.assertEquals(0x04030201, LocatorImplForCity.littleEndian(x2, 1));
    }
    //大端模式解析测试
    @Test
    public void testBigEndian() {
        byte[] x = new byte[]{1, 2, 3, 4};
        Assert.assertEquals(0x01020304, LocatorImplForCity.bigEndian(x, 0));
        byte[] x2 = new byte[]{0, 1, 2, 3, 4};
        Assert.assertEquals(0x01020304, LocatorImplForCity.bigEndian(x2, 1));
    }

    //测试Locator.parseOctet()
    @Test
    public void testParseOctet() {
        Assert.assertEquals(1, LocatorImplForCity.parseOctet("1"));
        try {
            LocatorImplForCity.parseOctet("-1");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
        try {
            LocatorImplForCity.parseOctet("256");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
    }

    //测试将ipV4转化成byte数组的方法 strToBytes
    @Test
    public void testStrToBytes() {
        byte[] b = LocatorImplForCity.strToBytes("1.2.3.4");
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, b);

        try {
            LocatorImplForCity.strToBytes("01.2.3.4");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }

        try {
            LocatorImplForCity.strToBytes("2.3.4");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }

    }

    //测试 BuildLocationInfo 方法,将String字符串封装成 LocationInfo
    @Test
    public void testBuildLocationInfo() {
        String s = "中国\t上海\t上海\t浦东\t电信";
        LocationInfoForCity l = LocatorImplForCity.buildInfo(s.getBytes(), 0, s.getBytes().length);
        Assert.assertEquals(new LocationInfoForCity("中国", "上海", "上海", "电信"), l);
        int legth = "中国\t".getBytes().length;
        System.out.println(legth);
        LocationInfoForCity locationInfo = LocatorImplForCity.buildInfo(s.getBytes(), legth, s.getBytes().length-legth);
        Assert.assertEquals(new LocationInfoForCity("中国", "上海", "上海", "电信"), locationInfo);
    }

    //IP解析测试，
    private void testLocator(LocatorImplForCity locatorForCity) {
        Long startTime = System.currentTimeMillis();
        LocationInfoForCity info = locatorForCity.find("8.8.8.8");
        System.out.println("find ip time: "+(System.currentTimeMillis() - startTime));
        Assert.assertEquals(new LocationInfoForCity("GOOGLE", "GOOGLE", "", ""), info);
        info = locatorForCity.find("183.131.7.18");
        Assert.assertEquals(new LocationInfoForCity("中国", "浙江", "杭州", ""), info);
        info = locatorForCity.find(new byte[]{(byte) 183, (byte) 131, 7, 18});
        Assert.assertEquals(new LocationInfoForCity("中国", "浙江", "杭州", ""), info);
        info = locatorForCity.find(LocatorImplForCity.bigEndian(new byte[]{(byte) 183, (byte) 131, 7, 18}, 0));
        Assert.assertEquals(new LocationInfoForCity("中国", "浙江", "杭州", ""), info);
    }

    //将bytes解释成Int和解释成Long测试
    @Test
    public void testGetValueFromBytes(){
        Integer intTmp = new Integer(0x8fffffff);
        Long tmp = new Long(0x8fffffffL);
        System.out.println((long)(intTmp & 0xffffffffL));
        System.out.println(0x8f+"  " +0xff);
    }

    //测试从网络加载Ip库
    @Test
    public void testNetLoad() throws IOException {
        LocatorImplForCity local = Locator.loadFromNet("http://7j1xnu.com1.z0.glb.clouddn.com/17monipdb.dat");
        testLocator(local);
    }

    //测试从本地加载IP库并查询
    @Test
    public void testLocalLoad() throws IOException {
        Long loadStart = System.currentTimeMillis();
        LocatorImplForCity locatorForCity = LocatorImplForCity.loadFromLocal(datFileName);
        System.out.println("load time: "+ (System.currentTimeMillis() - loadStart));
        testLocator(locatorForCity);
    }

    //测试对比新的二分查找与老的二分查找的,单条对比
    //对于单条记录，修改后的与之前差一个数量级：老的耗时:0.921435ms  新的耗时：0.038153ms
    @Test
    public void testDiffNewFindWithOldFind() throws IOException {
//        String aimIp = "118.28.8.8";//115.236.173.94
        String aimIp = "118.28.8.8";//
        LocatorImplForCity locatorForCity = LocatorImplForCity.loadFromLocal(datFileName);
        Long starTimeForOldFun = System.nanoTime();
        LocationInfoForCity info = locatorForCity.find(aimIp);
        System.out.println(info.toString());
        System.out.println("老版本二分查找耗时(nm)："+(System.nanoTime() - starTimeForOldFun));
        Assert.assertEquals(new LocationInfoForCity("中国", "浙江", "杭州", ""), info);//1158154 1106872
        Long starTimeForNewFun = System.nanoTime();
        LocationInfoForCity infoNew = locatorForCity.findNew(aimIp);
        System.out.println(infoNew.toString());
        System.out.println("改进版本二分查找耗时(nm)："+(System.nanoTime() - starTimeForNewFun));
//        Assert.assertEquals(new LocationInfo("中国", "浙江", "杭州", ""), info);
    }


    //全部IPV4值遍历进行对比，比较两个方法是否有出入。耗时1949050ms = 32m29s72ms
    @Test
    public void testDiffNewFindWithOldFindForBach() throws IOException {
        LocatorImplForCity locatorForCity = LocatorImplForCity.loadFromLocal(datFileName);
        LocationInfoForCity infoOld = null;
        LocationInfoForCity infoNew = null;
        Long startTime = System.currentTimeMillis();
        Long number = 0xffffffffL;
        byte[] addr = new byte[4];
        for (long x = 0; x < number; x++) {
            addr[0] = (byte) ((x >> 24) & 0xff);
            addr[1] = (byte) ((x >> 16) & 0xff);
            addr[2] = (byte) ((x >> 8) & 0xff);
            addr[3] = (byte) (x & 0xff);
            infoOld = locatorForCity.find(addr);
            infoNew = locatorForCity.findNew(addr);
            Assert.assertEquals(infoOld, infoNew);
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }

    //测试从本地加载IP库并测试批量查询时间。
    @Test
    public void banch() throws IOException {
        Long number = 0x7fffffL;
        LocatorImplForCity locatorForCity = LocatorImplForCity.loadFromLocal(datFileName);
        long time1 = System.currentTimeMillis();
        locatorForCity.checkDb(number);
        long time2 = System.currentTimeMillis();
        System.out.println("banch find time " + (time2 - time1));
        System.out.println(number + "  Ops  " + (number  / (time2 - time1)));
    }

    //查询文章中所有的IP值，输出所有IP值解析之后的地域信息到文件
    @Test
    public void ipCheck() throws Exception {
        String ipFilePath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\WM_ip.txt";
        String outPutPath = "D:\\USER\\BLW\\Desktop\\output\\WM_ip_out.txt";
        String outPutPathQX = "D:\\USER\\BLW\\Desktop\\output\\WM_ip_QX_out.txt";
        int errorCount = 0;
        int count = 0,countQX = 0;
        int count2 = 0,countQX2 = 0;
        String encoding = "UTF-8";
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        String tempString = null;
        LocatorImplForCity locatorForCity = LocatorImplForCity.loadFromLocal(datFileName);
        IPDistrictExt.load("D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180201\\quxian.datx");
        LocationInfoForCity locationInfo = null;
        String strQX = null;
        try{
            inputStreamReader= new InputStreamReader(new FileInputStream(ipFilePath), encoding);
            reader = new BufferedReader(inputStreamReader);
            ReadFromFileUtil.readFileByLines(ipFilePath);
            while ((tempString = reader.readLine()) != null) {
                if(tempString.length()>4 && tempString.contains(".")) {
                    locationInfo = locatorForCity.find(tempString.trim());//地市解析
                    strQX = Arrays.toString(IPDistrictExt.find(tempString.trim()));//县区解
                    stringBufferQX.append(strQX+"\n");
                    stringBuffer.append(tempString+"\t"+locationInfo.toString()+"\n");

                    if (locationInfo.getCity() != null && locationInfo.getCity().length() > 0) count++;
                    else count2++;
                    if (strQX != "null") countQX++;
                    else countQX2++;
                }else {
                    System.out.println(tempString);
                }
            }
            reader.close();
        } catch (IOException e) {
            errorCount++;
            stringBufferError.append(tempString+"\n");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            System.out.println("县区解析数"+countQX);
            System.out.println("县区无法解析数"+countQX2);
            System.out.println("地市解析数："+count);
            System.out.println("无法解析数："+count2);
            System.out.println("错误数："+errorCount);
            WriteToFileUtil.bufferedWriteAndFileWriterTest(stringBufferQX,outPutPathQX,encoding);
            WriteToFileUtil.bufferedWriteAndFileWriterTest(stringBuffer,outPutPath,encoding);
        }
    }

}
*/
