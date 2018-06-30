package com.moowei.ipip17mon.udf;

import com.moowei.ipip17mon.thirdparty.LocatorImplForCity;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * Created by blw on 2018/4/11 0011.
 */
public class UDFTest extends UDF {
    String pathCounty = "./quxian.datx";
    String datxFilePathCity =  "./mydata4vipday2.datx";
//    File file = new File(Thread.currentThread().getContextClassLoader().getResource("mydata4vipday2.datx").getFile());
    Text result = new Text();
    LocatorImplForCity locatorImplForCity;


    public Text evaluate(Text ip){
        locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
        if(locatorImplForCity != null){
            result.set(locatorImplForCity.getLocationInfoByStrIp(ip.toString()));
        }else{
            result.set("null");
        }
        return result;
    }


}
