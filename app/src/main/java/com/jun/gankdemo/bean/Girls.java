package com.jun.gankdemo.bean;

import java.util.List;

public class Girls {
    public int page;
    public int page_count;
    public int status;
    public int total_counts;
    public List<PrettyGirl> data;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage_count() {
        return page_count;
    }

    public void setPage_count(int page_count) {
        this.page_count = page_count;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTotal_counts() {
        return total_counts;
    }

    public void setTotal_counts(int total_counts) {
        this.total_counts = total_counts;
    }

    public List<PrettyGirl> getData() {
        return data;
    }

    public void setData(List<PrettyGirl> data) {
        this.data = data;
    }
}
