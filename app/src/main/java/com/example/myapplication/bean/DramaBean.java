package com.example.myapplication.bean;

public class DramaBean {
    public int DramaImgId;
    public String DramaName;
    public boolean isPending;
    public boolean isStop;

    public DramaBean(int dramaImgId, String dramaName, boolean isPending, boolean isStop) {
        DramaImgId = dramaImgId;
        DramaName = dramaName;
        this.isPending = isPending;
        this.isStop = isStop;
    }

    public DramaBean() {
    }
}
