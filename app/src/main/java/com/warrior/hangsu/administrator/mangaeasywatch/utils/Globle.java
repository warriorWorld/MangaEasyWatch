package com.warrior.hangsu.administrator.mangaeasywatch.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.warrior.hangsu.administrator.mangaeasywatch.R;

public class Globle {
    //数据库版本号
    public static final int DB_VERSION = 2;
    //版本号
    public static String versionName;
    // 某具体漫画地址
    public static String mangaPathThumbnail;
    // 某具体漫画名称
    public static String mangaTitle;
    //某具体漫画第一或第二级地址
    public static String mangaPath;
    public static boolean reptileing = false;
    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .showImageOnLoading(R.drawable.on_loading)
            .showImageOnFail(R.drawable.loading_fail)
            .build();
    public static String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=mangaeasywa" +
            "tch&key=986400551&type=data&doctype=json&version=1.1&q=";
    public static int scrollGap = 200;
    //并不是真正的日期 而是用来比大小的
    public static String currentData = "";
    //用在保存单张图片上的章节
    public static String currentChapter = "";

    /**
     * 友好页面下载漫画
     */
    public static boolean friendlyDownload = false;
    public static int startPoint, endPoint;
    public static String mangaName;

    /**
     * 分类
     */
    public static String mangaTypeName;//漫画类型名称
    public static String mangaTypeCode;//漫画类型链接

    public enum Source {
        //本地资源
        local,
        //网络资源
        web,
        //喜欢的
        favorite
    }

    public static Source sourceFrom;


}
