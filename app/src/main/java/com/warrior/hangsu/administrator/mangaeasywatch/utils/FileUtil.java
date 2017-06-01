package com.warrior.hangsu.administrator.mangaeasywatch.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
    private static final String TAG = "YanZi";
    private static final File parentPath = Environment
            .getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "reptile";
    private static String DST_FOLDER_NAME2 = "temp";

    /**
     * 初始化保存路径
     *
     * @return
     */
    private static String initPath() {
//        if (storagePath.equals("")) {
        storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME
                + "/" + DST_FOLDER_NAME2;
        Log.d("路径", storagePath);
        File f = new File(storagePath);
        if (!f.exists()) {
            // 如果不存在 就创建
            f.mkdirs();
        }
        // storagePath=storagePath+"/"+DST_FOLDER_NAME2;
        // File file=new File(storagePath);
        // if(!file.exists()){
        // file.mkdir();
        // }
//        }
        return storagePath;
    }

    public static String getPath() {
        long dataTake = System.currentTimeMillis();
        return parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME + "/"
                + dataTake + ".jpg";
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b
     * @return 图片路径
     */
    public static String saveBitmap(Bitmap b) {
        String dataTake = String.valueOf(System.currentTimeMillis());
        return saveBitmap(b, dataTake);
    }

    public static void inputStreamToFile(InputStream is) {

    }

    public static String saveBitmap(Bitmap b, String bmpName) {
        b = ImageUtil.imageZoom(b, 480);

        String path = initPath();
        String jpegName = path + "/" + bmpName;
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegName;
    }

    /**
     * 存图片
     *
     * @param b
     * @param bmpName
     * @param mangaName
     * @return
     */
    public static String saveBitmap(Bitmap b, String bmpName, String mangaName) {
        b = ImageUtil.imageZoom(b, 480);
        DST_FOLDER_NAME2 = mangaName;
        String path = initPath();
        String jpegName = path + "/" + bmpName;
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegName;
    }

    /**
     * 存图片 TODO
     *
     * @param b
     * @param bmpName
     * @param mangaName
     * @return
     * @childFolder 子文件夹名 因为漫画图片数量太大 所以在多一层子文件夹 自动建立
     */
    public static String saveBitmap(Bitmap b, String bmpName,
                                    String childFolder, String mangaName) {
        b = ImageUtil.imageZoom(b, 480);
        DST_FOLDER_NAME2 = mangaName;
        String path = initPath();
        String jpegName = path + "/" + childFolder + "/" + bmpName;
        String folderName = path + "/" + childFolder;
        File f = new File(folderName);
        if (!f.exists()) {
            // 如果不存在 就创建
            f.mkdirs();
        }
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegName;
    }

    public static String getBitmapPath(String bmpName) {
        String bppath = "";
        bppath = initPath() + "/" + bmpName;
        return bppath;
    }

    /**
     * 保存Bitmap到sdcard
     *
     * @param b
     * @return 图片路径
     */
    public static String saveBitmapPNG(Bitmap b) {
        b = ImageUtil.imageZoom(b, 480);
        String path = initPath();
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake + ".png";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jpegName;
    }

    /**
     * 递归删除文件和文件夹 因为file.delete();只能删除空文件夹或文件 所以需要这么递归循环删除
     *
     * @param file 要删除的根目�?
     */
    public static void deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

}
