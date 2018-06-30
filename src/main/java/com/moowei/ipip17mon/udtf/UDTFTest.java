package com.moowei.ipip17mon.udtf;

import com.moowei.ipip17mon.thirdparty.LocatorImplForCity;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentTypeException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by blw on 2018/4/11 0011.
 */
public class UDTFTest extends GenericUDTF {
    private PrimitiveObjectInspector stringOI = null;
    private String datxFilePathCity =  "./mydata4vipday2.datx";
    LocatorImplForCity locatorImplForCity;


    @Override
    public StructObjectInspector initialize(ObjectInspector[] argOIs)
            throws UDFArgumentException {
        if(argOIs.length != 1 ){
            throw new UDFArgumentException("ExplodeStringUDTF takes exactly one argument.");
        }
        if(argOIs[0].getCategory() != ObjectInspector.Category.PRIMITIVE
                && ((PrimitiveObjectInspector)argOIs[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING){
            throw new UDFArgumentTypeException(0, "ExplodeStringUDTF takes a string as a parameter.");
        }
        // 输入格式（inspectors）
        stringOI = (PrimitiveObjectInspector) argOIs[0];
        ArrayList<String> fieldNames = new ArrayList<String>();
        ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
        fieldNames.add("ip1");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("ip2");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        fieldNames.add("ip3");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
//        fieldNames.add("ip4");
//        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);

    }

    public ArrayList<Object[]> getCityInfoByIp(String ip){
        ArrayList<Object[]> result = new ArrayList<Object[]>();

        // 忽略null值与空值
        if (ip == null || ip.isEmpty()) {
            return result;
        }
        String[] tokens = locatorImplForCity.getLocationInfoByStrIp(ip).split("\t");
        if (tokens.length > 4){
            result.add(new Object[] { tokens[0], tokens[1], tokens[2] });
        }
        return result;
    }

    @Override
    public void process(Object[] record) throws HiveException {
        locatorImplForCity = LocatorImplForCity.getInstance(datxFilePathCity);
        if(locatorImplForCity == null ){
            throw new UDFArgumentException("ExplodeStringUDTF takes exactly one argument.");
        }
        final String name = stringOI.getPrimitiveJavaObject(record[0]).toString();
        ArrayList<Object[]> results = getCityInfoByIp(record[0].toString());
        Iterator<Object[]> it = results.iterator();
        while (it.hasNext()){
            Object[] r = it.next();
            forward(r);
        }
        // TODO Auto-generated method stub
        /*String input = record[0].toString();
        String[] name = input.split("\t");
        forward(name);*/
    }

    @Override
    public void close() throws HiveException {
        // TODO Auto-generated method stub

    }
}
