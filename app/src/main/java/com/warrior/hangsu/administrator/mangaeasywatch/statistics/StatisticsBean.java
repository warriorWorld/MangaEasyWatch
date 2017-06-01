package com.warrior.hangsu.administrator.mangaeasywatch.statistics;

/**
 * Created by Administrator on 2016-06-16.
 */
public class StatisticsBean {
    private String date;
    private String mangaName;
    private int HPWN;
    private int page_amount;//


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMangaName() {
        return mangaName;
    }

    public void setMangaName(String mangaName) {
        this.mangaName = mangaName;
    }

    public int getHPWN() {
        return HPWN;
    }

    public void setHPWN(int HPWN) {
        this.HPWN = HPWN;
    }

    public int getPage_amount() {
        return page_amount;
    }

    public void setPage_amount(int page_amount) {
        this.page_amount = page_amount;
    }

}
