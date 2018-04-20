package com.syyk.electronicclass2.bean;

/**
 * Created by fei on 2018/1/2.
 */

public class MessageBean {
    private int msgCode;
    private String msgs;
    private int msgi;
    private ScheduleBean bean;

    public int getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(int msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgs() {
        return msgs;
    }

    public void setMsgs(String msgs) {
        this.msgs = msgs;
    }

    public int getMsgi() {
        return msgi;
    }

    public void setMsgi(int msgi) {
        this.msgi = msgi;
    }


    public ScheduleBean getBean() {
        return bean;
    }

    public void setBean(ScheduleBean bean) {
        this.bean = bean;
    }
}
