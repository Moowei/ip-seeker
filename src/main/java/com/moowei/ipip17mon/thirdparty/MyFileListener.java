package com.moowei.ipip17mon.thirdparty;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * Created by blw on 2018/3/22 0022.
 */
public class MyFileListener implements FileAlterationListener {

    @Override
    public void onStart(FileAlterationObserver observer) {
        System.out.println("monitor start scan files..");
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println(directory.getName()+" director created.");
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println(directory.getName()+" director changed.");
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println(directory.getName()+" director deleted.");
    }

    @Override
    public void onFileCreate(File file) {
        System.out.println(file.getName()+" created.");
    }

    @Override
    public void onFileChange(File file) {
        System.out.println(file.getName()+" changed.");
    }

    @Override
    public void onFileDelete(File file) {
        System.out.println(file.getName()+" deleted.");
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        System.out.println("monitor stop scanning..");
    }
}
