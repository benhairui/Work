package com.ic.bean;

public class UrlBean {
    private String kw;

    private String url;

    private int level; //该url是第几层


    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
