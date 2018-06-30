package com.moowei.ipip17mon.udf;

import com.moowei.ipip17mon.thirdparty.LocatorImplForCounty;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * Created by blw on 2018/4/11 0011.
 */
@Description(
        name = "ip2CountyString",
        value = "_FUNC_(strIP) - from the input string"
                + "返回IP对应的区县信息，字符串以制表符\\t分隔 ,eg:（\"058.206.163.0\\t058.206.163.255\\t中国\\t甘肃\\t兰州\\t榆中县\\t620123\\t17.4\\t104.1145\\t35.8441\"）",
        extended = "Example:\n"
                + " > SELECT _FUNC_(strIP) FROM src;"
)
public class Ip2CountyString extends UDF {
    //分布式缓存中的区县库路径，需要提前将HDFS中文件上传到缓存，eg：ADD file hdfs://ZBJT/user/learning_test/hive_db/udf_blw/quxian.datx;
    String datxFilePathCounty = "./quxian.datx";
    Text result = new Text();
    LocatorImplForCounty locatorImplForCounty;

    public String evaluate(String strIP){
        locatorImplForCounty = LocatorImplForCounty.getInstance(datxFilePathCounty);
        if(locatorImplForCounty != null){
            result.set(locatorImplForCounty.getLocationInfoByStrIp(strIP.trim()));
        }else{
            result.set("null");
        }
        return result.toString();
    }
}
