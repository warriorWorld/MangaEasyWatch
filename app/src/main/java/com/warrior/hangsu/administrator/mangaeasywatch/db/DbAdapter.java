package com.warrior.hangsu.administrator.mangaeasywatch.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.warrior.hangsu.administrator.mangaeasywatch.mangalist.MangaBean;
import com.warrior.hangsu.administrator.mangaeasywatch.statistics.StatisticsBean;
import com.warrior.hangsu.administrator.mangaeasywatch.utils.Globle;

import java.util.ArrayList;

public class DbAdapter {
    public static final String DB_NAME = "mangaCollect.db";
    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(Context context) {
        dbHelper = new DbHelper(context, DB_NAME, null, Globle.DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 插入一条收藏信息
     */
    public void insertCollectTb(String thumbnailPath, String mangaName) {
        db.execSQL(
                "insert into CollectBook (thumbnailPath,mangaName) values (?,?)",
                new Object[]{thumbnailPath, mangaName});
    }

    /**
     * 插入一条喜欢信息
     */
    public void insertFavoriteTb(String filePath) {
        db.execSQL(
                "insert into FavoriteImagesBook (filePath) values (?)",
                new Object[]{filePath});
    }

    /**
     * 插入一条统计信息
     */
    public void insertStatiscticsTb(String date, String mangaName, int HPWN, int pageAmount) {
        db.execSQL(
                "insert into STATISTICS (date,HPWN,page_amount,mangaName) values (?,?,?,?)",
                new Object[]{date, HPWN, pageAmount, mangaName});
    }

    /**
     * 查询所有收藏
     *
     * @return
     */
    public ArrayList<MangaBean> queryAllCollect() {
        ArrayList<MangaBean> resBeans = new ArrayList<MangaBean>();
        Cursor cursor = db
                .query("CollectBook", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String mangaName = cursor.getString(cursor.getColumnIndex("mangaName"));
            String thumbnailPath = cursor
                    .getString(cursor.getColumnIndex("thumbnailPath"));

            MangaBean mangaBean = new MangaBean();
            mangaBean.setTitle(mangaName);
            mangaBean.setWebBpPath(thumbnailPath);
            resBeans.add(mangaBean);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询所有收藏
     *
     * @return
     */
    public ArrayList<String> queryAllFavorite() {
        ArrayList<String> resBeans = new ArrayList<String>();
        Cursor cursor = db
                .query("FavoriteImagesBook", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor.getColumnIndex("filePath"));
            resBeans.add(filePath);
        }
        cursor.close();
        return resBeans;
    }

    /**
     * 查询是否已经收藏过
     */
    public boolean queryCollected(String mangaName) {
        Cursor cursor = db.rawQuery(
                "select mangaName from CollectBook where mangaName=?",
                new String[]{mangaName});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询是否已经喜欢过
     */
    public boolean queryFavorited(String filePath) {
        Cursor cursor = db.rawQuery(
                "select filePath from FavoriteImagesBook where filePath=?",
                new String[]{filePath});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查询某个漫画的所有百页单词量
     */
    public ArrayList<StatisticsBean> queryHPWN(String mangaName) {
        ArrayList<StatisticsBean> list = new ArrayList<StatisticsBean>();
        Cursor cursor = db.rawQuery(
                "select * from STATISTICS where mangaName=?",
                new String[]{mangaName});
        while (cursor.moveToNext()) {
            StatisticsBean item = new StatisticsBean();
            String date = cursor.getString(cursor
                    .getColumnIndex("date"));
            int HPWN = cursor.getInt(cursor.getColumnIndex("HPWN"));
            int page_amount = cursor.getInt(cursor.getColumnIndex("page_amount"));
            item.setMangaName(mangaName);
            item.setDate(date);
            item.setHPWN(HPWN);
            item.setPage_amount(page_amount);
            list.add(item);
        }
        cursor.close();
        return list;
    }

    /**
     * 查询所有有记录的manganame
     *
     * @return
     */
    public String[] queryAllMangaName() {
        //为避免重复添加漫画名称,先按漫画名称排序 然后如果漫画名称同上一个漫画名称相同,则不添加
        String[] res;
        Cursor cursor = db
                .query("STATISTICS", null, null, null, null, null, "mangaName");
        res = new String[cursor.getCount()];

        String lastMangaName = "";
        int i = 0;
        while (cursor.moveToNext()) {
            String mangaName = cursor.getString(cursor.getColumnIndex("mangaName"));
            if (!TextUtils.isEmpty(mangaName) && !mangaName.equals(lastMangaName)) {
                res[i] = mangaName;
                lastMangaName = mangaName;
                i++;
            }
        }
        String[] result = new String[i];
        for (int j = 0; j < i; j++) {
            result[j] = res[j];
        }
        cursor.close();
        return result;
    }

    /**
     * 根据漫画名称删除收藏
     */
    public void deleteCollectByMangaName(String mangaName) {

        db.execSQL("delete from CollectBook where mangaName=?",
                new Object[]{mangaName});

    }

    /**
     * 根据漫画名称删除收藏
     */
    public void deleteFavoriteByFilePath(String filePath) {

        db.execSQL("delete from FavoriteImagesBook where filePath=?",
                new Object[]{filePath});

    }

    /**
     * 根据漫画名称删除统计数据
     */
    public void deleteStatiscticsByMangaName(String mangaName) {
        db.execSQL("delete from STATISTICS where mangaName=?",
                new Object[]{mangaName});
    }

    public void closeDb() {
        if (null != db) {
            db.close();
        }
    }
}
