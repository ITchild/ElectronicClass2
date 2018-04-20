package com.syyk.electronicclass2.httpcon;

/**
 * Created by fei on 2018/1/2.
 */

public class HttpEventBean {
    private int resCode;
    private String res;
    private int backCode;

    public int getBackCode() {
        return backCode;
    }

    public void setBackCode(int backCode) {
        this.backCode = backCode;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getRes() {
        return res;
    }

    public void setRes(String res) {
        this.res = res;
    }
}
