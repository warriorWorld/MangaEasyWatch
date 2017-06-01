package com.warrior.hangsu.administrator.mangaeasywatch.statistics;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-06-16.
 */
public class StatisticsListViewBean {
    //listview
    private String mangaName;
    private int pageAmount;//总量
    private int wordAmount;
    private ArrayList<StatisticsBean> list;

    public int getPageAmount() {
        return pageAmount;
    }

    public void setPageAmount(int pageAmount) {
        this.pageAmount = pageAmount;
    }

    public int getWordAmount() {
        return wordAmount;
    }

    public void setWordAmount(int wordAmount) {
        this.wordAmount = wordAmount;
    }

    public String getMangaName() {
        return mangaName;
    }

    public void setMangaName(String mangaName) {
        this.mangaName = mangaName;
    }

    public ArrayList<StatisticsBean> getList() {
        return list;
    }

    public void setList(ArrayList<StatisticsBean> list) {
        this.list = list;
    }
}
