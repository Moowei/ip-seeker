package com.moowei.ipip17mon.udf;

import com.moowei.ipip17mon.thirdparty.LocatorImplForCity;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * Created by blw on 2018/4/11 0011.
 */
@Description(
        name = "ip2CityString",
        value = "_FUNC_(strIP) - from the input string"
                + "返回IP对应的地市信息，字符串以制表符\\t分隔（共13个字段） ",
        extended = "Example:\n"
                + " > SELECT _FUNC_(strIP) FROM src;"
)
public class Ip2CityString extends UDF {
    //分布式缓存中的地市库路径，需要提前将HDFS中文件上传到缓存，eg：ADD file hdfs://ZBJT/user/learning_test/hive_db/udf_blw/mydata4vipday2.datx;
    String datxFilePathCity =  "./mydata4vipday2.datx";
    Text result = new Text();
    LocatorImplForCity locatorImplForCity;

    public String evaluate(String strIP){
        locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
        if(locatorImplForCity != null){
            result.set(locatorImplForCity.getLocationInfoByStrIp(strIP.trim()));
        }else{
            result.set("null");
        }
        return result.toString();
    }
}
