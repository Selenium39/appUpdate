package com.selenium.appUpdate.updater.net.netImpl;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.selenium.appUpdate.updater.net.INetCallBack;
import com.selenium.appUpdate.updater.net.INetDownloadCallBack;
import com.selenium.appUpdate.updater.net.INetManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求具体实现类:使用OKHttpClient框架
 */
public class OkHttpNetManager implements INetManager {
    private static final String TAG = "Selenium";
    private OkHttpClient okHttpClient;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public OkHttpNetManager() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        okHttpClient = builder.connectTimeout(15, TimeUnit.SECONDS)
                .build();
    }


    @Override
    public void get(String url, final INetCallBack callBack, Object tag) {
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.url(url).tag(tag).get().build();
        Call call = okHttpClient.newCall(request);
        //同步请求
        // Response response = call.execute();
        //异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        e.printStackTrace();
                        callBack.failed(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String result;
                try {
                    result = response.body().string();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(result);
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                    callBack.failed(e);
                }
            }
        });

    }

    @Override
    public void download(String url, final File targetFile, final INetDownloadCallBack downloadCallBack, Object tag) {
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder.url(url).tag(tag).get().build();
        Call call = okHttpClient.newCall(request);
        //同步请求
        // Response response = call.execute();
        //异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) { //任务取消掉后直接return，防止空指针异常
                    return;
                }
                e.printStackTrace();
                downloadCallBack.failed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                OutputStream os = null;

                try {
                    is = response.body().byteStream();
                    //总文件大小
                    final long totalSize = response.body().contentLength();
                    if (targetFile != null) {
                    }

                    os = new FileOutputStream(targetFile);
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    //已下载文件大小
                    long downloadSize = 0;
                    //读取网络字节流并写入文件
                    while ((len = is.read(buffer)) != -1) {
                        os.write(buffer, 0, len);
                        os.flush();
                        downloadSize += len;
                        final long finalDownloadSize = downloadSize;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //乘以1.0f防止小数整除大数为0
                                downloadCallBack.progress((int) (((finalDownloadSize * 1.0f) / totalSize) * 100))
                                ;
                            }
                        });
                    }

                    //保险起见为文件设置可读可写可执行
                    targetFile.setExecutable(true, false);
                    targetFile.setReadable(true, false);
                    targetFile.setWritable(true, false);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadCallBack.success(targetFile);
                        }
                    });
                } catch (Exception e) {
                    if (call.isCanceled()) {//任务取消掉后直接return，防止空指针异常
                        return;
                    }
                    e.printStackTrace();
                    downloadCallBack.failed(e);
                } finally {
                    //不要忘了关闭流
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                }
            }
        });
    }

    @Override
    public void cancel(Object tag) {
        List<Call> queuedCalls = okHttpClient.dispatcher().queuedCalls();
        List<Call> runningCalls = okHttpClient.dispatcher().runningCalls();

        //取消正在执行中的任务
        if (runningCalls != null) {
            for (Call call : runningCalls) {
                call.cancel();
            }
        }
        //取消排队中的任务
        if (queuedCalls != null) {
            for (Call call : queuedCalls) {
                call.cancel();
            }
        }
    }
}
