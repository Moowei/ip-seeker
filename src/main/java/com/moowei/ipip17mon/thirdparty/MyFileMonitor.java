package com.moowei.ipip17mon.thirdparty;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * Created by blw on 2018/3/22 0022.
 */
public class MyFileMonitor {
    FileAlterationMonitor monitor = null;
    public MyFileMonitor(long interval) throws Exception {
        monitor = new FileAlterationMonitor(interval);
    }
    public void monitor(String path, FileAlterationListener listener) {
        FileAlterationObserver observer = new FileAlterationObserver(new File(path));
        monitor.addObserver(observer);
        observer.addListener(listener);
    }
    public void stop() throws Exception{
        monitor.stop();
    }
    public void start() throws Exception {
        monitor.start();
    }
    public static void main(String[] args) throws Exception {
        MyFileMonitor m = new MyFileMonitor(5000);
        m.monitor("D:\\USER\\BLW\\Desktop\\Data warehouse\\IPIP\\test_Observer",new MyFileListener());
        m.start();
    }
}
