package com.warrior.hangsu.administrator.mangaeasywatch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private Context xcontext;

    public static final String COLLECT_BOOK = "create table if not exists CollectBook ("
            + "id integer primary key autoincrement,"
            + "thumbnailPath text,"
            + "mangaName text)";
    public static final String DATA_STATISTICS = "create table if not exists STATISTICS ("
            + "id integer primary key autoincrement,"
            + "date text," + "HPWN integer," + "page_amount integer,"
            + "mangaName text)";
    //这个数据库保存的是本地收藏的图片
    public static final String  FAVORITE_IMAGES_BOOK = "create table if not exists FavoriteImagesBook ("
            + "id integer primary key autoincrement,"
            + "filePath text)";
    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        xcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COLLECT_BOOK);
        db.execSQL(DATA_STATISTICS);
        db.execSQL(FAVORITE_IMAGES_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(COLLECT_BOOK);
        db.execSQL(DATA_STATISTICS);
    }

}
