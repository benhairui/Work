package com.ic.test;

import java.util.List;

public class SenInfo {

    private String id;
    private String midInfo;
    private String kw;
    private String level;
    private String url;
    private List<String> cand_word;
//    private List<String> filter_word;
    private String category;
    private String submit_time;
    private String author;
    private boolean isFin;

    private String cand_word_str;

    private String isFinStr;

    private String filter_word_str;


    public String getCand_word_str() {
        return cand_word_str;
    }

    public void setCand_word_str(String cand_word_str) {
        this.cand_word_str = cand_word_str;
    }

    public String getIsFinStr() {
        return isFinStr;
    }

    public void setIsFinStr(String isFinStr) {
        this.isFinStr = isFinStr;
    }

    public String getFilter_word_str() {
        return filter_word_str;
    }

    public void setFilter_word_str(String filter_word_str) {
        this.filter_word_str = filter_word_str;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isFin() {
        return isFin;
    }

    public void setFin(boolean fin) {
        isFin = fin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMidInfo() {
        return midInfo;
    }

    public void setMidInfo(String midInfo) {
        this.midInfo = midInfo;
    }

    public String getKw() {
        return kw;
    }

    public void setKw(String kw) {
        this.kw = kw;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getCand_word() {
        return cand_word;
    }

    public void setCand_word(List<String> cand_word) {
        this.cand_word = cand_word;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(String submit_time) {
        this.submit_time = submit_time;
    }
}
