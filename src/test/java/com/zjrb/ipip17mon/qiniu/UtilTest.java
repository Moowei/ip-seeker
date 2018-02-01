package com.zjrb.ipip17mon.qiniu;

import org.junit.Test;

/**
 * Created by blw on 2017/9/20.
 */
public class UtilTest {
    @Test
    public void  ipStringTets(){
//        String strIP = "163.204.172.142";
//        String[] s = strIP.split("\\.");
//        System.out.println(s[0]);
        byte b = 0x01;
        int i = (int)b;
        int b1 = (i>>24) & 0xff;
        System.out.println("b1: "+b1);

        System.out.println(i);
    }
}
