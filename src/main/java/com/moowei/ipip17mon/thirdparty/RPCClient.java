package com.moowei.ipip17mon.thirdparty;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * Created by blw on 2018/3/19 0019.
 */
public class RPCClient {
    public static void main(String[] args) throws Exception {
        Boolean flag = true;
        //得到server的代理对象，必须传入服务端集成的接口，且versionID必须与服务端接口中一致才能通信
        IRPCServer proxy = RPC.getProxy(IRPCServer.class, 10010, new InetSocketAddress("10.100.119.156" , 8888), new Configuration());
        //调用代理对象的方法，
        String ip = "118.28.8.8";
        while(flag){
            System.out.println("请输入：");
            Scanner sc = new Scanner(System.in);
            switch(Integer.valueOf(sc.nextLine())){
                case 1 : System.out.println(proxy.sayHi( "lili"));
                    break;
                case 2 : System.out.println(proxy.findIPForCity(ip));
                    break;
                case 3 :System.out.println(proxy.findIPForCounty("118.28.8.8"));
                    break;
                case 4 :System.out.println("结束监控");
                    flag = false;
                    proxy.shutdownAll();
                    break;
                default:System.out.println("其他操作，忽略");
            }
         }
        //关掉RPC
        RPC. stopProxy(proxy);
    }

}
