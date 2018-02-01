package com.zjrb.chunzhen.util;


import com.zjrb.chunzhen.entry.CityDirectly;
import com.zjrb.chunzhen.entry.IPEntry;
import com.zjrb.chunzhen.entry.ProvinceSuffix;
import org.junit.Test;
import org.apache.commons.lang3.*;
/**
 * Created by blw on 2017/9/21.
 */
public class AreaSplitTest {
    private String fileName0 = "C:\\Users\\blw\\Desktop\\Data warehouse\\Cluster_Test\\IP\\IP_semple.txt";
    private String fileName = "D:\\cz88.net\\ip\\QQWry.txt";
    private String fileOutputName = "C:\\Users\\blw\\Desktop\\Data warehouse\\Cluster_Test\\IP\\output\\ipoutput.txt";


    private String str1 = "浙江省";    //flage = "省"
    private String str11 = "河北省石家庄市藁城区";    //flage = "省-市-市县区";
    private String str11e = "浙江省杭州市";   //flage = "省-市";
    private String str1e = "河北省藁城区" ;   //flage = "省-县区";
    private String str2 = "日本"; //flage = "国外";
    private String str21 = "上海市";    //flage = "直";
    private String str211 = "上海"; //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private String str22 = "北京市海淀区";    //flage = "直-市县区旗";
    private String str221 = "北京海淀区";    //flage = "直-市县区旗";
    private String str23 = "北京";   //flage = "直!市";
    private String str3 = "内蒙古锡林郭勒市苏尼特右旗";  //flage = "!直-市-市县区旗";
    private String str30 = "内蒙古通辽市霍林郭勒市"; //flage = "!直-市-市县区旗";
    private String str301 = "内蒙古锡林郭勒盟二连浩特";
    private String str31 = "内蒙古鄂尔多斯市";  //flage = "!直-市";
    private String str32 = "内蒙古锡林郭勒盟苏尼特右旗"; //flage = "!直-盟-市县区旗";
    private String str33 = "内蒙古锡林郭勒盟";  //flage = "!直-盟";
    private String str34 = "内蒙古锡林郭勒盟二连浩特市";
    private String str35 = "香港";//flage = "!直";
    private String str4 = "黑龙江省大兴安岭地区漠河县";
    private String str41 = "贵州省毕节地区威宁县";
    private String str42 = "贵州省黔南州荔波县";
    private String str44 = "黑龙江省大庆市肇州县";
    private String str45 = "河北省石家庄市赵州县";
    private String str43 = "贵州省荔波县";

    private String str5 = "宁夏吴忠市盐池县";
    private String str6 = "江苏省淮安市楚州区";
    private String str7 = "广东省广州市";//广东省惠州市//广东省梅州市丰顺县
    private String str8 = "重庆市万州区";
    private String str81 = "湖北省恩施州宣恩县";//湖北省恩施州宣恩县//湖北省荆州市松滋市
    //安徽省滁州市//安徽省亳州市蒙城县//安徽省池州市//安徽省宿州市泗县//安徽省滁州市明光市
    private String str82 = "河北省保定市定州市";

    //河北省保定市定州市
    //福建省福州市//福建省漳州市诏安县

    private AreaSplit areaSplit = new AreaSplit();

    @Test
    public void areaTest(){
        AreaSplit areaSplit = new AreaSplit();
        IPEntry ipEntry = IPEntry.getInstance();
        System.out.println(IPEntry.getIpLongFromString("1.0.0.255"));
//        Integer[] bytes = ipEntry.getIpByteArrayFromString("1.24.176.0");
//        for(int y=0;y < bytes.length;y++){
//            System.out.println(bytes[y]);
//        }
//        Long starTime = System.currentTimeMillis();
//        System.out.println(ipEntry.getIpLongFromByteArray(ipEntry.getIpByteArrayFromString("1.24.177.0")));
//        Long entTime = System.currentTimeMillis();
//        System.out.println(starTime-entTime);
//        System.out.println(ipEntry.getIpLongFromByteArray(ipEntry.getIpByteArrayFromString("1.24.177.0"))-ipEntry.getIpLongFromByteArray(ipEntry.getIpByteArrayFromString("1.24.176.0")));
    }

    /**
     * 将纯真IP库抽取省市县字段，并生成国家字段
     * @throws Exception
     */
    @Test
    public void  mainTest() throws Exception {
        AreaSplit areaSplit = new AreaSplit();
//        String fileName = "C:\\Users\\blw\\Desktop\\Data warehouse\\Cluster_Test\\IP\\IP_semple.txt";
        String fileName = "D:\\cz88.net\\ip\\QQWry.txt";
        String fileOutputName = "C:\\Users\\blw\\Desktop\\Data warehouse\\Cluster_Test\\IP\\output\\ipoutput_IP.txt";
//        StringBuffer ipStringBuffer = null;
        String encoding = areaSplit.codeString(fileName);
        areaSplit.readFileByLines(fileName,encoding,";");
        areaSplit.bufferedWriteAndFileWriterTest(areaSplit.getStringBuffer(),fileOutputName,encoding);
        ProvinceSuffix.getCounter();
    }

    @Test
    public void StringTest() throws Exception {
        String areaStrSuffix = "地区,盟,市";
        String functionStr = str33;
        System.out.println("isContains : "+areaSplit.isContains(functionStr,areaStrSuffix));
        System.out.println("indexContains :"+areaSplit.indexContains(functionStr,areaStrSuffix));
        System.out.println(ProvinceSuffix.isContainsOf(functionStr));
    }


}
