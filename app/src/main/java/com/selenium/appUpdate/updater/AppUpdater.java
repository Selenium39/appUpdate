package com.selenium.appUpdate.updater;

import com.selenium.appUpdate.updater.net.INetManager;
import com.selenium.appUpdate.updater.net.netImpl.OkHttpNetManager;

public class AppUpdater {

    private static AppUpdater appUpdater = null;

    //默认为OkHttpManager
    private static INetManager netManager = new OkHttpNetManager();

    private AppUpdater() {
    }

    public static AppUpdater getInstance() {
        if (appUpdater == null) {
            appUpdater = new AppUpdater();
        }
        return appUpdater;
    }

    public INetManager getNetManager() {
        return netManager;
    }

    public void setNetManager(INetManager netManager) {
        AppUpdater.netManager = netManager;
    }
}
