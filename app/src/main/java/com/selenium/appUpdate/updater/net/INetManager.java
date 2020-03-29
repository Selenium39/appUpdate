package com.selenium.appUpdate.updater.net;

import java.io.File;

/**
 * 提供网络接口进行调用:
 * 1.接口隔离具体的实现，解耦合
 * 2.方便多个开发者并行开发
 */
public interface INetManager {
    void get(String url, INetCallBack callBack,Object tag);

    void download(String url, File targetFile, INetDownloadCallBack downloadCallBack,Object tag);

    void cancel(Object tag);
}
