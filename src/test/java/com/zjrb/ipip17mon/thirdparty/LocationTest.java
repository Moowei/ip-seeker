package com.zjrb.ipip17mon.thirdparty;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
/**
 * Created by long on 2017/1/16.
 */
public class LocationTest {
    private String datFileName = "D:\\USER\\BLW\\Desktop\\Data warehouse\\Cluster_Test\\IP\\ipip.net\\ipip.net-official\\c-master\\dat\\17monipdb.dat";

    //小端模式解析测试
    @Test
    public void testLittleEndian() {
        byte[] x = new byte[]{1, 2, 3, 4};
        Assert.assertEquals(0x04030201, Locator.littleEndian(x, 0));
        byte[] x2 = new byte[]{0, 1, 2, 3, 4};
        Assert.assertEquals(0x04030201, Locator.littleEndian(x2, 1));
    }
    //大端模式解析测试
    @Test
    public void testBigEndian() {
        byte[] x = new byte[]{1, 2, 3, 4};
        Assert.assertEquals(0x01020304, Locator.bigEndian(x, 0));
        byte[] x2 = new byte[]{0, 1, 2, 3, 4};
        Assert.assertEquals(0x01020304, Locator.bigEndian(x2, 1));
    }

    //测试Locator.parseOctet()
    @Test
    public void testParseOctet() {
        Assert.assertEquals(1, Locator.parseOctet("1"));
        try {
            Locator.parseOctet("-1");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
        try {
            Locator.parseOctet("256");
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }
    }
    //测试将ipV4转化成byte数组的方法textToNumericFormatV4
    @Test
    public void testTextToNumericFormatV4() {
        byte[] b = Locator.textToNumericFormatV4("1.2.3.4");
        Assert.assertArrayEquals(new byte[]{1, 2, 3, 4}, b);

        try {
            Locator.textToNumericFormatV4("01.2.3.4");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }

        try {
            Locator.textToNumericFormatV4("2.3.4");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof NumberFormatException);
        }

    }

    //测试buildInfo方法
    @Test
    public void testBuildLocationInfo() {
        String s = "中国\t上海\t上海\t浦东\t电信";
        LocationInfo l = Locator.buildInfo(s.getBytes(), 0, s.getBytes().length);
        Assert.assertEquals(new LocationInfo("中国", "上海", "上海", "电信"), l);
        int legth = "中国\t".getBytes().length;
        System.out.println(legth);
        LocationInfo locationInfo = Locator.buildInfo(s.getBytes(), legth, s.getBytes().length-legth);
        Assert.assertEquals(new LocationInfo("中国", "上海", "上海", "电信"), locationInfo);
    }

    private void testLocator(Locator locator) {
        Long startTime = System.currentTimeMillis();
        LocationInfo info = locator.find("8.8.8.8");
        System.out.println("find ip time: "+(System.currentTimeMillis() - startTime));
        Assert.assertEquals(new LocationInfo("GOOGLE", "GOOGLE", "", ""), info);
        info = locator.find("183.131.7.18");
        Assert.assertEquals(new LocationInfo("中国", "浙江", "杭州", ""), info);
        info = locator.find(new byte[]{(byte) 183, (byte) 131, 7, 18});
        Assert.assertEquals(new LocationInfo("中国", "浙江", "杭州", ""), info);
        info = locator.find(Locator.bigEndian(new byte[]{(byte) 183, (byte) 131, 7, 18}, 0));
        Assert.assertEquals(new LocationInfo("中国", "浙江", "杭州", ""), info);

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
        Locator l = Locator.loadFromNet("http://7j1xnu.com1.z0.glb.clouddn.com/17monipdb.dat");
        testLocator(l);
    }

    //测试从本地加载IP库并查询
    @Test
    public void testLocalLoad() throws IOException {
        Long loadStart = System.currentTimeMillis();
        Locator locator = Locator.loadFromLocal(datFileName);
        System.out.println("load time: "+ (System.currentTimeMillis() - loadStart));
        testLocator(locator);
    }

    //测试对比新的二分查找与老的二分查找的,单条对比
    //对于单条记录，修改后的与之前差一个数量级：老的耗时:0.921435ms  新的耗时：0.038153ms
    @Test
    public void testDiffNewFindWithOldFind() throws IOException {
        String aimIp = "115.236.173.94";
        Locator locator = Locator.loadFromLocal(datFileName);
        Long starTimeForOldFun = System.nanoTime();
        LocationInfo info = locator.find(aimIp);
        System.out.println("老版本二分查找耗时(nm)："+(System.nanoTime() - starTimeForOldFun));
        Assert.assertEquals(new LocationInfo("中国", "浙江", "杭州", ""), info);
        Long starTimeForNewFun = System.nanoTime();
        info = locator.findNew(aimIp);
        System.out.println("改进版本二分查找耗时(nm)："+(System.nanoTime() - starTimeForNewFun));
        Assert.assertEquals(new LocationInfo("中国", "浙江", "杭州", ""), info);
    }


    //全部IPV4值遍历进行对比，比较两个方法是否有出入。耗时1949050ms = 32m29s72ms
    @Test
    public void testDiffNewFindWithOldFindForBach() throws IOException {
        Locator locator = Locator.loadFromLocal(datFileName);
        LocationInfo infoOld = null;
        LocationInfo infoNew = null;
        Long startTime = System.currentTimeMillis();
        Long number = 0xffffffffL;
        byte[] addr = new byte[4];
        for (long x = 0; x < number; x++) {
            addr[0] = (byte) ((x >> 24) & 0xff);
            addr[1] = (byte) ((x >> 16) & 0xff);
            addr[2] = (byte) ((x >> 8) & 0xff);
            addr[3] = (byte) (x & 0xff);
            infoOld = locator.find(addr);
            infoNew = locator.findNew(addr);
            Assert.assertEquals(infoOld, infoNew);
        }
        System.out.println(System.currentTimeMillis() - startTime);
    }

    //测试从本地加载IP库并测试批量查询时间。
    @Test
    public void banch() throws IOException {
        Long number = 0xffffffL;
        Locator locator = Locator.loadFromLocal(datFileName);
        long time1 = System.currentTimeMillis();
        locator.checkDb(number);
        long time2 = System.currentTimeMillis();
        System.out.println("banch find time " + (time2 - time1));
        System.out.println(number + "  Ops  " + (number  / (time2 - time1)));
    }
}
