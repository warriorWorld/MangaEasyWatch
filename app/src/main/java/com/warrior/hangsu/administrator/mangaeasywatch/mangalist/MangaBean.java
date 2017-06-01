package com.warrior.hangsu.administrator.mangaeasywatch.mangalist;

/**
 * Created by Administrator on 2016/4/3.
 */
public class MangaBean {
    private String path;
    private String bpPath;
    private String title;
    private String webBpPath;

    public String getBpPath() {
        if (!bpPath.contains("file://")) {
            return "file://" + bpPath;
        } else {
            return bpPath;
        }
    }

    public void setBpPath(String bpPath) {
        this.bpPath = bpPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWebBpPath() {
        return webBpPath;
    }

    public void setWebBpPath(String webBpPath) {
        this.webBpPath = webBpPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
