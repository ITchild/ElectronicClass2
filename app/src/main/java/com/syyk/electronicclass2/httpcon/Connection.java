package com.syyk.electronicclass2.httpcon;

import android.os.Handler;

import com.syyk.electronicclass2.bean.AttenBean;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by fei on 2017/7/5.
 */

public class Connection {

    /**
     * 测试接口
     */
    public static void getTestMsg(int resCode) {
        RequestBody body = new FormBody.Builder()
//                .add("Mac", mac)
//                .add("SerachData", date)
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.testip, resCode);
    }

    /**
     * 获取公告和介绍
     *
     * @param resCode
     */
    public static void getIntrAndNotice(int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("Notice", "1")
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.GETINTROANDNOTICE, resCode);
    }

    /**
     * 上传读卡信息
     *
     * @param resCode
     */
    public static void PostCardIdMsg(String cardId, int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("Card_Id", cardId)
                .add("DateTime", "11:11")
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.POSTCARDIDMSG, resCode);
    }

    /**
     * 获取首页信息
     *
     * @param resCode
     */
    public static void getHomePager(int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("MainPage", "1")
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.GETHOMEPAGER, resCode);
    }

    /**
     * 获取课表
     *
     * @param mac
     * @param date
     * @param resCode http://192.168.1.198:9512/AndroidHandler/CourseHandler.ashx?Mac=123456&SerachData=2017/5/24
     */

    public static void getSchedule(String mac, String date, int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("Mac", mac)
                .add("SerachData", date)
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.GETSCHEDULE, resCode);
    }

    /**
     * 获取教室的列表
     *
     * @param resCode
     */
    public static void getClassRoom(int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("ClassRoom", "1")
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.GETCLASSROOM, resCode);
    }

    /**
     * 绑定教室信息
     *
     * @param mac
     * @param classId
     * @param resCode
     */
    public static void bondClassRoom(String mac, String classId, int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("Mac", mac)
                .add("CR_Id", classId)
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.BINDCLASSROOM, resCode);
    }


}
