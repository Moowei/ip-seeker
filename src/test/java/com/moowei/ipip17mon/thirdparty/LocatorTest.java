package com.moowei.ipip17mon.thirdparty;

import com.moowei.ipip17mon.demo.IPExt;
import com.moowei.ipip17mon.util.ReadFromFileUtil;
import com.moowei.ipip17mon.util.WriteToFileUtil;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

/**
 * Created by blw on 2018/2/7 0007.
 */
public class LocatorTest {

    private String datxFilePathCounty ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180201\\quxian.datx";
    private String datxFilePathCounty2 ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\quxian.datx";
    private String datxFilePathCity ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180129\\mydata4vipday2.datx";
    private String datxFilePathCity2 ="D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\20180208\\mydata4vipday2.datx";

    @Test
    public void countyDatxIPTest(){
        String aimIp = "118.24.8.8";//
        Locator locator = LocatorImplForCounty.getInstance(datxFilePathCounty2);
        Object obj = locator.findLocationInfoByStrIp(aimIp);
        if (obj instanceof LocationInfoForCounty){
            System.out.println(((LocationInfoForCounty)obj).toString());
        }
    }

    @Test
    public void cityDatxIPTest(){
        String aimIp = "118.24.8.8";//96152008 96515904
        LocatorImplForCity locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
        IPExt.load(datxFilePathCity);

        Long starTimeForOldFun = System.nanoTime();//4295786
        System.out.println(Arrays.toString(IPExt.find(aimIp)));
        System.out.println("官方版本耗时："+(System.nanoTime()-starTimeForOldFun));

        Long startTime = System.nanoTime();//1254972 1047792
        Object obj = locatorImplForCity.findLocationInfoByStrIp(aimIp);
        System.out.println("耗时: "+(System.nanoTime()-startTime));

        if (obj instanceof LocationInfoForCity){
            System.out.println(((LocationInfoForCity)obj).toString());
        }
    }

    @Test
    public void longValueTest(){
        long longValue1 = new File(datxFilePathCity).lastModified();
        long longValue2 = new File(datxFilePathCity).lastModified();
        Long longValue3 = new File(datxFilePathCity).lastModified();
        Long longValue4 = new File(datxFilePathCity).lastModified();
        Assert.assertEquals((longValue1 == longValue2),true);
        Assert.assertEquals((longValue3 == longValue4),false);
        Assert.assertEquals((longValue3.equals(longValue4)),true);
    }

    @Test
    public void enumSingleTest(){
        String aimIp = "115.236.173.94";//
        LocatorImplForCity locatorImplForCity1 = LocatorImplForCity.getInstance(datxFilePathCity);
        LocatorImplForCity locatorImplForCity2 = LocatorImplForCity.getInstance(datxFilePathCity);
        LocatorImplForCity locatorImplForCity3 = LocatorImplForCity.getInstance(datxFilePathCity);
        LocatorImplForCity locatorImplForCity11 = LocatorImplForCity.getInstance(datxFilePathCity2);
        LocatorImplForCity locatorImplForCity12 = LocatorImplForCity.getInstance(datxFilePathCity2);
        LocatorImplForCity locatorImplForCity13 = LocatorImplForCity.getInstance(datxFilePathCity2);
        Object obj = locatorImplForCity1.findLocationInfoByStrIp(aimIp);
        if (obj instanceof LocationInfoForCity){
            Assert.assertEquals((((LocationInfoForCity)obj).toString()),"中国\t浙江\t杭州\t\t电信\t30.287459\t120.153576\tAsia/Shanghai\tUTC+8\t330100\t86\tCN\tAP");
        }
        Assert.assertEquals((locatorImplForCity1 == locatorImplForCity2),true);
        Assert.assertEquals((locatorImplForCity1 == locatorImplForCity3),true);
        Assert.assertEquals((locatorImplForCity1 == locatorImplForCity11),false);
        Assert.assertEquals((locatorImplForCity11 == locatorImplForCity12),true);
        Assert.assertEquals((locatorImplForCity11 == locatorImplForCity13),true);
    }

    //查看IP数据库在内存中的大小
    @Test
    public void checkMemorySize(){
        System.gc();
        long total = Runtime.getRuntime().totalMemory();    // Java 虚拟机中的内存总量(byte)
        long m1 = Runtime.getRuntime().freeMemory();        // Java 虚拟机中的空闲内存(byte)
        System.out.println("before:" + (total - m1));
//        LocatorImplForCity locatorImplForCity1 = LocatorImplForCity.getInstance(datxFilePathCity);
        LocatorImplForCity locatorImplForCity11 = LocatorImplForCity.getInstance(datxFilePathCity2);
        long total1 = Runtime.getRuntime().totalMemory();
        long m2 = Runtime.getRuntime().freeMemory();
        System.out.println("after:" + (total1 - m2));
    }

    //查询文章中所有的IP值，输出所有IP值解析之后的地域信息到文件
//    @Test
    public void ipCheck() throws Exception {
        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBufferQX = new StringBuffer();
        StringBuffer stringBufferError = new StringBuffer();
        String ipFilePath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\WM_ip.txt";
        String outPutPath = "D:\\USER\\BLW\\Desktop\\output\\WM_ip_out.txt";
        String outPutPathQX = "D:\\USER\\BLW\\Desktop\\output\\WM_ip_QX_out.txt";
        int errorCount = 0;
        int allCount=0,count = 0,countQX = 0;
        int count2 = 0,countQX2 = 0;
        String encoding = "UTF-8";
        BufferedReader reader = null;
        InputStreamReader inputStreamReader = null;
        String tempString = null;
        Object object = null;
        LocatorImplForCity locatorForCity = LocatorImplForCity.getInstance(datxFilePathCity2);
        LocatorImplForCounty locatorImplForCounty = LocatorImplForCounty.getInstance(datxFilePathCounty);
//        IPDistrictExt.load(datxFilePathCounty);
        LocationInfoForCity locationInfoForCity = null;
        LocationInfoForCounty locationInfoForCounty = null;
        String strQX = null;
        try{
            inputStreamReader= new InputStreamReader(new FileInputStream(ipFilePath), encoding);
            reader = new BufferedReader(inputStreamReader);
            ReadFromFileUtil.readFileByLines(ipFilePath);
            while ((tempString = reader.readLine()) != null) {
                allCount++;
                if(tempString.length()>4 && tempString.contains(".")) {
                    object = locatorForCity.findLocationInfoByStrIp(tempString.trim());//地市解析
                    if(object instanceof LocationInfoForCity ){
                        locationInfoForCity = (LocationInfoForCity)object;
                        stringBuffer.append(tempString+"\t"+ locationInfoForCity.toString()+"\n");
                        if(locationInfoForCity.getCity() != null  && locationInfoForCity!=null && !"null".equals(locationInfoForCity.getCity()) && locationInfoForCity.getCity().length() > 0)
                            count++;
                    }
                    object = locatorImplForCounty.findLocationInfoByStrIp(tempString.trim());//地市解析
                    if(object instanceof LocationInfoForCounty ){
                        locationInfoForCounty = (LocationInfoForCounty)object;
                        stringBufferQX.append(tempString+"\t"+ locationInfoForCounty.toString()+"\n");
                        if(locationInfoForCounty.getCity() != null && locationInfoForCounty!=null && !"null".equals(locationInfoForCounty.getCity()) && locationInfoForCounty.getCity().length() > 0)
                            countQX++;
                    }
//                    stringBufferQX.append(strQX+"\n");
//                    strQX = Arrays.toString(IPDistrictExt.find(tempString.trim()));//县区解
//                    stringBufferQX.append(strQX+"\n");
                }else {
                    System.out.println(tempString);
                }
            }
            reader.close();
        } catch (IOException e) {
            errorCount++;
//            stringBufferError.append(tempString+"\n");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
            System.out.println("IP数："+ allCount);
            System.out.println("县区解析数"+ countQX);
            System.out.println("县区无法解析数"+ countQX2);
            System.out.println("地市解析数："+ count);
            System.out.println("无法解析数："+ count2);
            System.out.println("异常错误数："+ errorCount);
            WriteToFileUtil.bufferedWriteAndFileWriterTest(stringBufferQX,outPutPathQX,encoding);
            WriteToFileUtil.bufferedWriteAndFileWriterTest(stringBuffer,outPutPath,encoding);
        }
    }

   @Test
    public void isIPStr() {
       String aimIp = "118.24";//
       Locator locator = LocatorImplForCounty.getInstance(datxFilePathCounty2);
       LocatorImplForCity locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
       System.out.println(locatorImplForCity.getLocationInfoByStrIp(aimIp));
       System.out.println(locator.getLocationInfoByStrIp(aimIp));
    }

    @Test
    public void findLocationInfoByStrIpTest() {
        String aimIp = "60.191.70.89";//
        Locator locator = LocatorImplForCounty.getInstance(datxFilePathCounty2);
        LocatorImplForCity locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
        System.out.println(locatorImplForCity.findLocationInfoByStrIp(aimIp).toString());
        System.out.println(locator.findLocationInfoByStrIp(aimIp).toString());
    }

    @Test
    public void errorIpStringTest(){
        String strIP = "218.94.230.4";
        LocatorImplForCounty locatorImplForCounty = LocatorImplForCounty.getInstance(datxFilePathCounty);
        String result = locatorImplForCounty.getLocationInfoByStrIp(strIP.trim());
        System.out.println(result);

    }

}
