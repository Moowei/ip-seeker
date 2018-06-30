package com.moowei.ipip17mon.thirdparty;

import org.apache.commons.collections.bag.SynchronizedSortedBag;

import java.io.File;
import java.util.Observable;

/**
 * Created by blw on 2018/2/7.
 * Observer Pattern 观察者模式中继承java.util.Observable的通知者
 * 使用观察者模式监听文件是否改变
 */
final class FileWatchSubject extends Observable {
    private volatile boolean monitorFlag;  //状态

    FileWatchSubject() {
        this.monitorFlag = true;
    }

    /**
     * 开启一个线程用于监听地市文件，当文件变动时，通知观察者
     * @param filePathForCity 地市库文件路径
     * @param intervalForCity 监听频率(分钟m)，多久检测一次，当文件变动后通知观察者
     */
    public void excuteForCity(final String filePathForCity,final int intervalForCity) {
        Thread thread =  new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    monitorFile(filePathForCity,intervalForCity,"city");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("monitorForCity");
//        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 开启一个线程用于监听区县库文件，当文件变动时，通知观察者
     * @param filePathForCounty 区县库文件路径
     * @param intervalForCounty 监听频率(分钟m)，多久检测一次，当文件变动后通知观察者
     */
    public void excuteForCounty(final String filePathForCounty,final int intervalForCounty) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    monitorFile(filePathForCounty,intervalForCounty,"county");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setName("monitorForCounty");
//        thread.setDaemon(true);
        thread.start();
    }

    /**
     * 对所有文件停止监听（包括区县库和地市库）
     */
    public void shutdownAll() {
        monitorFlag = false;
    }

    /**
     * 监听指定的文件，当文件变动之后通知观察者
     * 注;monitorFlag 状态字段，故直接使用volatile关键字定义，保证线程安全。
     * @param filePath 要监听的文件路径
     * @param interval 刷新频率（m），隔多久检测一次文件状态
     * @param type 返回给观察者的状态，以此判断当前是哪个线程返回的结果
     */
    private void monitorFile(String filePath,int interval,String type) throws Exception {
        File file = new File(filePath);
        if(!file.exists()){// file not exists
            throw new Exception("Error:the function of com.zjrb.ipip17mon.thirdparty.FileWatchSubject monitorFile 输入错误，filePath文件不存在");
        }
        Long lastModified = file.lastModified();
        //delay monitor (delay 1m)
        try {
            Thread.sleep(1 * 2 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("监控开启：type = "+type);
        while (monitorFlag) {
            if (file.lastModified() != lastModified) {
                lastModified = file.lastModified();
                setChanged();       // 改变通知者的状态
                notifyObservers(type);  // 调用父类Observable方法，通知所有观察者
            }
            if(!monitorFlag){
                break;
            }
            try {
                Thread.sleep(interval * 20 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("监控结束：type = "+type );
    }
}