package com.zjrb.chunzhen.util;

import com.zjrb.chunzhen.entry.CityDirectly;
import com.zjrb.chunzhen.entry.IPEntry;
import com.zjrb.chunzhen.entry.Province;
import com.zjrb.chunzhen.entry.ProvinceSuffix;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Set;

/**
 * Created by blw on 2017/9/21.
 * @author blw
 * 根据给定的字符转切分出省市县字段
 */
public class AreaSplit {
    private StringBuffer stringBuffer = new StringBuffer();
    private String fileName = "./QQWry.txt";
    private String fileOutputName = "./ipoutput.txt";
    private static final String PROVINCE_SUFFIX = "省";
    private static final String CITY_SUFFIX = "市";

    public StringBuffer getStringBuffer() {
        return stringBuffer;
    }

    public void setStringBuffer(StringBuffer stringBuffer) {
        this.stringBuffer = stringBuffer;
    }


    /**
     * 按照指定格式（encoding）读取文件
     * @param fileName 要读取的文件地址
     * @param encoding  按照指定编码格式读取文件避免乱码
     */
    public void readFileByLines(String fileName,String encoding,String splitStr) {
        IPEntry ipEntryInstance = IPEntry.getInstance();
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        try {
            splitStr = "\\t".equals(splitStr) ? "\t" : splitStr;
            inputStreamReader= new InputStreamReader(new FileInputStream(fileName), encoding);
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            Long beginLongIP = 0L;
            Long endLongIP = 0L;
            // 一次读入一行，直到读入null为文件结束
            //1.0.8.0         1.0.15.255      广东省 电信
            while ((tempString = reader.readLine()) != null) {
                //将文件中的连续的多个空格替换成分号。
                tempString = tempString.replaceAll("[ ][ ]*",";");
                //以分号切分获取原始的四个字段
                String[] lines = tempString.split(";");
                ipEntryInstance.initIPEntryInstance();
                if(lines.length>3){
                    //获取countryStr,provinceStr,cityStr,districtStr,flage,areaStr等六个字段
                    ipEntryInstance = getArea(lines[2],ipEntryInstance,false);
                    //补充，剩余的originBeginIp，originEndIp，parseOperator，originOperatorDESC 四个字段
                    ipEntryInstance.setOriginBeginIp(lines[0]);
                    ipEntryInstance.setOriginEndIp(lines[1]);
                    ipEntryInstance.setParseOperator(lines[3]);
                    ipEntryInstance.setOriginOperatorDESC(lines[3]);
                    beginLongIP = IPEntry.getIpLongFromString(lines[0]);
                    ipEntryInstance.setParseLongBeginIp(beginLongIP);
                    endLongIP = IPEntry.getIpLongFromString(lines[1]);
                    ipEntryInstance.setParseLongEndIp(endLongIP);
                    ipEntryInstance.setParseCounterIp(endLongIP-beginLongIP + 1);
                    ProvinceSuffix.counterAllRight++;
                    stringBuffer.append(ipEntryInstance.getIPLibrarySplited(splitStr)+"\n");
                }else if(lines.length == 3){ //有些数据没有运营商字段。
                    //获取countryStr,provinceStr,cityStr,districtStr,flage,areaStr等六个字段
                    ipEntryInstance = getArea(lines[2],ipEntryInstance,false);
                    //补充，剩余的originBeginIp，originEndIp，parseOperator，originOperatorDESC 四个字段
                    ipEntryInstance.setOriginBeginIp(lines[0]);
                    ipEntryInstance.setOriginEndIp(lines[1]);
                    beginLongIP = IPEntry.getIpLongFromString(lines[0]);
                    ipEntryInstance.setParseLongBeginIp(beginLongIP);
                    endLongIP = IPEntry.getIpLongFromString(lines[1]);
                    ipEntryInstance.setParseLongEndIp(endLongIP);
                    ipEntryInstance.setParseCounterIp(endLongIP-beginLongIP + 1);
                    ProvinceSuffix.counterAllRight++;
                    stringBuffer.append(ipEntryInstance.getIPLibrarySplited(splitStr)+"\n");
                }else{
                    //数据基本无用，直接舍弃
                    ProvinceSuffix.counterAllError ++;
                }
            }
            reader.close();
        } catch (IOException e) {
            ProvinceSuffix.counterAllError ++;
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
//            return stringBuffer;
        }
    }

    /**
     * 字符缓冲写入
     * @param stringBuffer 要写入的数据
     * @param filePath 要写入的文件地址
     * @param encoding 写文件时的文件编码
     */
    public void bufferedWriteAndFileWriterTest(StringBuffer stringBuffer,String filePath,String encoding) {
        File file = new File(filePath);
//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
//            bufferedWriter = new BufferedWriter( new FileWriter(file));
            bufferedWriter.write(stringBuffer.toString());
            bufferedWriter.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 判断文件的编码格式
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public String codeString(String fileName) throws Exception{
        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }



    /**
     * 将字符串切分，获取省，市，县字段，三个字段，没有值的使用""填充
     * 0、为了显示准确，市级字段除了后缀"市"被过滤，其他的：“州”“地区”"盟"等后缀被保留。
     * 1、对于**市**市 的情况，例如："河北省保定市定州市" 和 "内蒙古**市**市"。在获取市子弹，因为使用"市"对字段切分，所以会导致最后的一个县区（如果有县区字段）字段丢失最后的后缀"市"：
     *      例如"河北省保定市定州市" --》中国|河北|保定|定州 ；为避免误导，对最后的县区字段手动添加后缀"市"使结果为：中国|河北|保定|定州市
     *      若获取市字段时使用的是其他后缀如：州,盟,区域,则无需另行为县区字段添加后缀"市"
     * 2、对于直辖市：
     *      北京  -> 中国|北京|北京|
     *      北京市 -> 中国|北京|北京|
     *      北京市海淀区 -> 中国|北京|北京|海淀区
     *      北京海淀区 -> 中国|北京|北京|海淀区
     *
     * @param areaStr 要切分的地域字段的信息
     * @param isTest 是否显示调试信息
     * @return
     */
    public IPEntry getArea(String areaStr,IPEntry ipEntryInstance,Boolean isTest){
        Boolean isChina = Boolean.FALSE;
        String countryStr = "";    //国家
        String provinceStr = "";// 省
        String cityStr = "";    // 市
        String districtStr = "";// 县
        String flage = "";//记录类型

        String[] tempProvinceArr = null;
        String tempProvinceStr = null;
        String[] tempCityArr = null;
        String tempCityStr = null;
        if(isContains(areaStr,PROVINCE_SUFFIX)){//包含“省”,且不以“省”开头
            tempProvinceArr = areaStr.split(PROVINCE_SUFFIX);
            isChina = Boolean.TRUE;
            countryStr = "中国";
            ProvinceSuffix.counterGN++;
            if(isTest) {
                System.out.println(countryStr + ",,,,,,,,,,,,,,,,,,,1-36,,,省");
            }
            provinceStr = tempProvinceArr[0];
            if(isTest) {
                System.out.println(provinceStr + ",,,,,,,,,,,,,,,,,,,1-38,,,省");
            }
            if(tempProvinceArr.length >1 && !StringUtils.isEmpty(tempProvinceArr[1]) && !StringUtils.isBlank(tempProvinceArr[1])){//"省"之后仍有内容
//                if (isContains(tempProvinceArr[1],"地区,市,州")) {//包含“市”，且不以“市”开头：如“**省**市**县（区，市）”
                int indexSuffix = ProvinceSuffix.isContainsOf(tempProvinceArr[1]);//tempProvinceArr[1]为截取省字段之后的字符串
                if(indexSuffix >= 0 ){
                    tempCityArr = tempProvinceArr[1].split(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix));//判断之后的字段是：州,市,地区,盟中的哪个，并按照该字段切分tempProvinceArr[1]
                    //因为存在"黑龙江省大庆市肇州县"和"河北省石家庄市赵州县"这种情况（会导致：黑龙江|大庆市肇州|县  中国|河北|石家庄市赵州|县），导致切分错误。
                    Boolean isZX = "州".equals(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix)) && tempProvinceArr[1].endsWith("州县") && tempProvinceArr[1].contains("市");
                    Boolean isZQ = "州".equals(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix)) && tempProvinceArr[1].endsWith("州区") && tempProvinceArr[1].contains("市");
                    if(isZX | isZQ){
                        ProvinceSuffix.counterZAX++;
                        tempCityArr = tempProvinceArr[1].split("市");
                    }
                    //为避免误解，若后缀为地区或州（如：贵州省黔南州荔波县），后缀不去除（结果：贵州;黔南州;荔波县）
                    cityStr = "地区".equals(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix)) | ("州".equals(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix)) && !isZX && !isZQ)  ? tempCityArr[0]+ProvinceSuffix.SUFFIX_ARR.get(indexSuffix) : tempCityArr[0];

                    if(isTest) {
                        System.out.println(cityStr + ",,,,,,,,,,,,,,,,,,,1-43,,,省-市(地区,州)");
                    }
                    if(tempCityArr.length>1 && !StringUtils.isEmpty(tempCityArr[1]) && !StringUtils.isBlank(tempCityArr[1])){//市之后有内容
//                        districtStr = tempProvinceArr[1].endsWith("市") || tempCityArr[1].endsWith("县") || tempCityArr[1].endsWith("区") || tempCityArr[1].endsWith("旗") ? tempCityArr[1] : "";
                        //如果切分字段为"市"且类型为：**市**市，例如："河北省保定市定州市" ;切分后最后的"定州市"为"定州",为其补充"市"最为后缀
                        districtStr = (CITY_SUFFIX.equals(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix)) && tempProvinceArr[1].endsWith(CITY_SUFFIX)) ? tempCityArr[1]+ProvinceSuffix.SUFFIX_ARR.get(indexSuffix) : tempCityArr[1];
                        if((CITY_SUFFIX.equals(ProvinceSuffix.SUFFIX_ARR.get(indexSuffix)) && tempProvinceArr[1].endsWith(CITY_SUFFIX))) {
                            ProvinceSuffix.counterSSS++;
                        }
                        if(isTest) {
                            System.out.println(districtStr + ",,,,,,,,,,,,,,,,,,,1-46,,,省-市(地区,州)-市县区旗");
                        }
                        flage = "省-市(地区,州)-市县区";
                        ProvinceSuffix.counterSSX++;
                    }else{
                        flage = "省-市(地区,州)";
                        ProvinceSuffix.counterSS++;
                    }
                }else {
                    districtStr = tempProvinceArr[1].endsWith("县") || tempProvinceArr[1].endsWith("区") || tempProvinceArr[1].endsWith("旗") ? tempProvinceArr[1] : "";
                    if(isTest) {
                        System.out.println(districtStr + ",,,,,,,,,,,,,,,,,,,2-57,,,省-县区旗");
                    }
                    //输出到文件。查看
                    flage = "省-县区旗";
                    ProvinceSuffix.counterSX++;
                }
            }else{
                //省之后没有内容，eg:浙江省。什么也不做
                flage = "省";
                ProvinceSuffix.counterS++;
            }
        }else{
            //不包含“省”，1、国外，2、内蒙古，3、北京，上海等直辖市，4、港澳台地区。
            //1、遍历所有省份，
            tempProvinceStr = getInSet(Province.getProvinceSet(),areaStr);//
            if(!StringUtils.isEmpty(tempProvinceStr)){//字符串中包含省名，说明是国内的地址
                isChina = true;
                countryStr = "中国";
                ProvinceSuffix.counterGN++;
                if(isTest) {
                    System.out.println(countryStr + ",,,,,,,,,,,,,,,,,,,3-70,,,直");
                }
                provinceStr = tempProvinceStr;
                if(isTest) {
                    System.out.println(provinceStr + ",,,,,,,,,,,,,,,,,,,3-72,,,直");
                }

                String areaStrSuffix = "地区,盟,市";
                if(CityDirectly.getCitySet().contains(tempProvinceStr)){//是否是直辖市：“北京市西城区”
                    if(isContains(areaStr,areaStrSuffix)){//直辖市之后跟地区,盟,市
                        int indexSuffix = indexContains(areaStr,areaStrSuffix);
                        tempCityArr = areaStr.split(areaStrSuffix.split(",")[indexSuffix]);
                        cityStr = "地区".equals(areaStrSuffix.split(",")[indexSuffix]) | "州".equals(areaStrSuffix.split(",")[indexSuffix]) |"盟".equals(areaStrSuffix.split(",")[indexSuffix]) ? tempCityArr[0]+areaStrSuffix.split(",")[indexSuffix] : tempCityArr[0];
//                        cityStr = tempCityArr[0];
                        if(tempCityArr.length>1 && !StringUtils.isEmpty(tempCityArr[1]) && !StringUtils.isBlank(tempCityArr[1])){//市之后有内容
                            //一般市之后的所有内容都无论以什么结尾，都统一作为districtStr的内容
                            districtStr = tempCityArr[1];
                            if(isTest) {
                                System.out.println(districtStr + ",,,,,,,,,,,,,,,,,,,3-79,,,直辖市");
                            }
                            flage = "直-市县区旗";
                            ProvinceSuffix.counterZS ++;
                        }else{
                            //是直辖市，但仅仅只有市：“北京市”
                            flage = "直";
                            ProvinceSuffix.counterZ ++;
                        }
                    }else{
                        //是直辖市，但是却没有包含“市”字，暂时简单处理，需统计类似数据量
                        //自动将市字段=直辖市
                        cityStr = provinceStr;
                        districtStr = areaStr.replaceAll("北京市|北京","").trim().length()>0 ? areaStr.replaceAll("北京市|北京","") : "";
                        flage = "直!市";
                        ProvinceSuffix.counterZNS++;
                    }

                } else{//不是直辖市，说明就是内蒙古，港澳台等
                    tempCityStr = areaStr.replaceAll(tempProvinceStr,"");//把省从地址字符串中去除掉
                    if(isContains(tempCityStr,areaStrSuffix)){//省之后为地区,盟,市：内蒙古乌兰察布市,内蒙古通辽市霍林郭勒市
                        int indexSuffix = indexContains(tempCityStr,areaStrSuffix);
                        tempCityArr = tempCityStr.split(areaStrSuffix.split(",")[indexSuffix]);
                        cityStr = "地区".equals(areaStrSuffix.split(",")[indexSuffix]) | "州".equals(areaStrSuffix.split(",")[indexSuffix]) |"盟".equals(areaStrSuffix.split(",")[indexSuffix]) ? tempCityArr[0]+areaStrSuffix.split(",")[indexSuffix] : tempCityArr[0];
                        if(tempCityArr.length>1 && !StringUtils.isEmpty(tempCityArr[1]) && !StringUtils.isBlank(tempCityArr[1])){//获取县（区，旗）
                            //一般市之后的所有内容都无论以什么结尾，都统一作为districtStr的内容
                            districtStr = tempCityArr[1];
                            flage = "!直-市盟-市县区旗";
                            ProvinceSuffix.counterNZSX++;
                        }else{//只到市没有县区旗
                            flage = "!直-市盟";
                            ProvinceSuffix.counterNZS ++;
                        }
                    }else {//港澳台，单独没有市县
                        flage = "!直";
                        ProvinceSuffix.counterNZ++;
                    }
                }
            }else {
                //不包含中国的省，说明是国外地址
                countryStr = areaStr;
                flage = "国外";
                ProvinceSuffix.counterGW ++;
            }
        }
        ipEntryInstance.setParseCountry(countryStr);
        ipEntryInstance.setParseProvince(provinceStr);
        ipEntryInstance.setParseCity(cityStr);
        ipEntryInstance.setParseDistrict(districtStr);
        ipEntryInstance.setFlage(flage);
        ipEntryInstance.setOriginArea(areaStr);
//        IPEntry ipEntry = new IPEntry(countryStr,provinceStr,cityStr,districtStr,flage,areaStr);
        return ipEntryInstance;
    }



    /**
     * areaStr包含provinceSuffix中字符串,但不能以其开头
     * provinceSuffix中字符元素顺序固定
     * @param areaStr
     * @return
     */
    public int indexContains(String areaStr,String str) {
        String[] strTempArr = str.contains(",") ? str.split(",") : new String[]{str};
        String strTmp = "";
        for(int index =0;index<strTempArr.length;index++){
            strTmp = strTempArr[index];
            if(areaStr.contains(strTmp) && !areaStr.trim().startsWith(strTmp)){
                return index;
            }
        }
        return -1;
    }

    /**
     * areaStr中是否含有str中的字符
     * str中字符以逗号分隔：
     * @param areaStr
     * @param str
     * @return
     */
    public Boolean isContains(String areaStr, String str) {
        String[] strTempArr = str.contains(",") ? str.split(",") : new String[]{str};
        for(String strTmp : strTempArr){
            if(areaStr.contains(strTmp) && !areaStr.trim().startsWith(strTmp)){
                return true;
            }
        }
        return false;
    }

    /**
     * 字符串str中是否包含set集合中的元素，包含则返回该元素，否则为""
     * @param set
     * @param str
     * @return
     */
    public String getInSet(Set<String> set, String str){
        String aimStr = "";
        for(String tmpStr : set ){
           if(str.contains(tmpStr) && str.startsWith(tmpStr)) {
               aimStr = tmpStr;
               break;
           }
        }
        return aimStr;
    }

    /**
     * 字符串str中是否包含set集合中的元素，包含则返回true，否则为false
     * @param set
     * @param str
     * @return
     */
    public Boolean isInSet(Set<String> set, String str){
        Boolean result = false;
        for(String tmpStr : set ){
            if(str.contains(tmpStr) && str.startsWith(tmpStr)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 调用时的入口函数
     * encoding ：解析之后文件的编码格式
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Integer inputSize = 4;
        if(args.length>0 && ("help".equals(args[0]) || "--help".equals(args[0]) || "-help".equals(args[0]))){
            System.out.println("com.zjrb.chunzhen.util.AreaSplit inputFilePath outputFilePath encoding splitStr");
            System.out.println("inputFilePath: 原始的纯真库地址（文本格式）,默认:./QQWry.txt");
            System.out.println("outputFilePath: 解析之后数据写入文件的路径（若文件不存在自动创建）默认：./ipoutput.txt");
            System.out.println("encoding: 输出时采用的编码格式，因为之后要导入到数据库建议UTF-8，默认与输入文本的格式相同");
            System.out.println("splitStr: 解析后的输出文件中字段间的分隔符");
            System.out.println("输出数据格式如下：originBeginIp + originEndIp + parseCountry  + parseProvince + parseCity + parseDistrict + parseOperator + originArea + originOperatorDESC;");
        }
        if(args.length!=inputSize){
            System.out.println("输入错误！！！必须要有参数");
            System.out.println("eg: com.zjrb.chunzhen.util.AreaSplit inputFilePath outputFilePath encoding splitStr");
            System.out.println("详细输入：com.zjrb.chunzhen.util.AreaSplit help");
        }else{
            AreaSplit areaSplit = new AreaSplit();
            String inputFilePath = !StringUtils.isAnyEmpty(args[0]) && !StringUtils.isBlank(args[0]) ? args[0]: "./QQWry.txt";
            String outputFilePath = !StringUtils.isAnyEmpty(args[1]) && !StringUtils.isBlank(args[1]) ? args[1]: "./ipoutput.txt";
            String inputEncoding = areaSplit.codeString(inputFilePath);
            String encoding = !StringUtils.isAnyEmpty(args[2]) && !StringUtils.isBlank(args[2]) ? args[2]: inputEncoding;
            String splitStr = !StringUtils.isAnyEmpty(args[3]) && !StringUtils.isBlank(args[3]) ? args[3]: ";";//不指定的话morning使用分号
            System.out.println("输入文件inputFilePath: "+inputFilePath);
            System.out.println("输出文件outputFilePath: "+outputFilePath);
            System.out.println("输出文件编码encoding: "+encoding);
            System.out.println("输入文件编码inputEncoding: "+inputEncoding);
            System.out.println("输出文件中字段分隔符："+splitStr);
            areaSplit.readFileByLines(inputFilePath,inputEncoding,splitStr);
            areaSplit.bufferedWriteAndFileWriterTest(areaSplit.getStringBuffer(),outputFilePath,encoding);
            System.out.println("数据统计如下：");
            ProvinceSuffix.getCounter();
        }

    }
}
