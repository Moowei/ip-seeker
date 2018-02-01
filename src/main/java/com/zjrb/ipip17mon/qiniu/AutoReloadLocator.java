//package com.zjrb.ipip17mon.qiniu;
//
//import java.io.IOException;
//import java.util.Observable;
//import java.util.Observer;
//
///**
// * Created by long on 2017/1/17.
// */
//public final class AutoReloadLocator implements ILocator, Observer {
//
////    private final SimpleFileWatchService watchService;
//    private final String filePath;
//    private Locator locator;
//
//    public AutoReloadLocator(String filePath, int intervalSeconds) throws IOException {
//        this.filePath = filePath;
//        locator = Locator.loadFromLocal(filePath);
////        watchService = new SimpleFileWatchService(filePath, intervalSeconds);//监控文件是否有变动的服务
////        watchService.addObserver(this);
////        watchService.excute();
//    }
//
//    // only for test
//    public static void main(String[] args) {
////        String filePath = "17monipdb.dat";
//        Long startTime = System.currentTimeMillis();
//        String filePath = "C:\\Users\\blw\\Desktop\\Data warehouse\\Cluster_Test\\IP\\ipip.net\\17monipdb\\17monipdb.dat";
//        if (args != null && args.length > 0) {
//            filePath = args[0];
//        }
//        AutoReloadLocator autoReloadLocator;
//        try {
//            autoReloadLocator = new AutoReloadLocator(filePath, 10);
//            LocationInfo locationInfo =  autoReloadLocator.find("1.57.212.150");
////            System.out.println(autoReloadLocator.find("163.204.172.142"));
//            System.out.println("parseCity: "+locationInfo.getCity()+"parseCountry: "+locationInfo.getCountry()+"ISP: "+ locationInfo.getIsp()+"state"+locationInfo.getState());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Long endTime = System.currentTimeMillis();
//        System.out.println("耗时："+ (endTime-startTime));
//        try {
//            Thread.sleep(60 * 60 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public LocationInfo find(String ip) {
//        return locator.find(ip);
//    }
//
//    @Override
//    public LocationInfo find(byte[] ipBin) {
//        return locator.find(ipBin);
//    }
//
//    @Override
//    public LocationInfo find(int address) {
//        return locator.find(address);
//    }
//
//    @Override
//    public void update(Observable o, Object arg) {
//        try {
//            locator = Locator.loadFromLocal(filePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
////    public void shutdown() {
////        watchService.deleteObserver(this);
////        watchService.shutdown();
////    }
//}
