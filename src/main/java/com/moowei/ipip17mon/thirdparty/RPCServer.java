package com.moowei.ipip17mon.thirdparty;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.HadoopIllegalArgumentException;
import java.io.IOException;

/**
 * Created by blw on 2018/3/19 0019.
 */
public class RPCServer implements IRPCServer {
//    static String cityPath = "/opt/blw_test/php/mydata4vipday2.datx";
//    static String  countyPath = "/opt/blw_test/php/quxian.datx";
    private static RPC.Server server;
    private volatile boolean shutdownFlag = false;
    static String  cityPath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\mydata4vipday2.datx";
    static String  countyPath = "D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\ip_datx_test\\quxian.datx";
    public static FileWatchObserver fileWatchObserver;

    static {
        try {
            fileWatchObserver = new FileWatchObserver(cityPath, 1, countyPath, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String sayHi(String name) {
        return "Hi ~ " +name ;
    }

    @Override
    public String findIPForCounty(String strIP) {
        return fileWatchObserver.getLocationInfoForCountyByIP(strIP);
    }

    @Override
    public void shutdownAll() {
        fileWatchObserver.shutdown();
        //通过设置标志字段shutdownFlag使得，当函数shutdownAll执行完成并向Client端返回调用结果后，才执行 server.stop();否则客户端在调用函数shutdownAll时,若server.stop()在函数结束前先执行会导致Client报错。
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        if(shutdownFlag){
                            server.stop();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        shutdownFlag = true;
    }

    @Override
    public String findIPForCity(String strIP) {
       return fileWatchObserver.getLocationInfoForCityByIP(strIP);
    }

//    public static void main(String[] args) throws Exception {
//        Configuration conf = new Configuration();
//        Server server = new Builder(conf).setProtocol(IRPCServer.class).setInstance(new RPCServer()).setBindAddress("192.168.8.100" ).setPort(9527).build();
//        server.start();
//    }
    public static void main(String[] args) throws HadoopIllegalArgumentException, IOException {
        RPC.Builder builder = new RPC.Builder(new Configuration());
        String bindAddress = "10.100.119.156";
        int port = 8888;
        builder.setBindAddress(bindAddress)
                .setPort(8888)
                .setProtocol(IRPCServer.class)
                .setInstance(new RPCServer());
        server = builder.build();
        server.start();
    }

}
