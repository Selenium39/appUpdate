package com.selenium.appUpdate.updater.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * {
 * "title": "4.5.0更新啦！",
 * "content": "1. 优化了阅读体验；\n2. 上线了 hyman 的课程；\n3. 修复了一些已知问题。",
 * "url": "http://59.110.162.30/v450_imooc_updater.apk",
 * "md5": "14480fc08932105d55b9217c6d2fb90b",
 * "versionCode": "450"
 * }
 */
public class AppInfo implements Parcelable {
    private String title;
    private String content;
    private String url;
    private String md5;
    private String versionCode;

    public AppInfo() {
    }

    public static AppInfo parse(String response) {
        JSONObject object = null;
        AppInfo appInfo = null;
        try {
            object = new JSONObject(response);
            String title = object.optString("title", "");
            String content = object.optString("content", "");
            String url = object.optString("url", "");
            String md5 = object.optString("md5", "");
            String versionCode = object.optString("versionCode", "");
            appInfo = new AppInfo(title, content, url, md5, versionCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    public AppInfo(String title, String content, String url, String md5, String versionCode) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.md5 = md5;
        this.versionCode = versionCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", url='" + url + '\'' +
                ", md5='" + md5 + '\'' +
                ", versionCode='" + versionCode + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
