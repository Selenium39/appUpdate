package com.selenium.appUpdate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.selenium.appUpdate.updater.AppUpdater;
import com.selenium.appUpdate.updater.bean.AppInfo;
import com.selenium.appUpdate.updater.net.INetCallBack;
import com.selenium.appUpdate.updater.net.INetDownloadCallBack;
import com.selenium.appUpdate.updater.ui.ShowAppInfoDialog;
import com.selenium.appUpdate.updater.util.AppUtil;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    public static final String APP_VERSION_URL = "http://59.110.162.30/app_updater_version.json";
    private Button mBtnUpdate;
    private static final String TAG = "selenium";
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnUpdate = findViewById(R.id.btn_update);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android 6.0以上
            //进入程序，申请读写权限
            int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUpdater.getInstance().getNetManager().get(APP_VERSION_URL, new INetCallBack() {
                    @Override
                    public void success(String response) {
                        //1.解析json;http://59.110.162.30/app_updater_version.json
                        final AppInfo appInfo = AppInfo.parse(response);
                        if (appInfo == null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "服务器返回版本信息错误", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        //2.做版本匹配
                        long version = Integer.valueOf(appInfo.getVersionCode());
                        if (version <= AppUtil.getVersionCode(MainActivity.this)) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "已经是最新版本了!!!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //弹框显示版本信息
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ShowAppInfoDialog.show(MainActivity.this, appInfo);
                                }
                            });
                        }
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "网络错误，更新版本失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }, MainActivity.this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        AppUpdater.getInstance().getNetManager().cancel(MainActivity.this);
        super.onDestroy();
    }
}
