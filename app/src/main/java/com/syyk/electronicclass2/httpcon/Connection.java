package com.syyk.electronicclass2.httpcon;

import android.os.Handler;

import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.utils.ComUtils;

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
     * 获取服务器的时间
     */
    public static void getSystemTime(int resCode){
//        RequestBody body = new FormBody.Builder()
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simpleCon(body, NetCartion.GET_SYSTEMTIME, resCode);
        ConnectionClient.simpleCon(NetCartion.GET_SYSTEMTIME+"?makeSureId="+ComUtils.getRandom(),resCode);
    }

    /**
     * 获取介绍
     * @param resCode
     * @param mac
     */
    public static void getIntroduce(String mac,int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("Notice", "1")
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.GETINTROANDNOTICE, resCode);

        ConnectionClient.simpleCon(NetCartion.GETHOMEPAGER+"?type=3&mac="+mac+"&makeSureId="+ComUtils.getRandom()
                ,resCode);
    }

    /**
     * 获取公告
     * @param resCode
     * @param mac
     */
    public static void getNotice(String mac,int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("Notice", "1")
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.GETINTROANDNOTICE, resCode);

        ConnectionClient.simpleCon(NetCartion.GETHOMEPAGER+"?type=2&mac="+mac+"&makeSureId="+ComUtils.getRandom()
                ,resCode);
    }

    /**
     * 上传读卡信息
     * @param resCode
     */
    public static void PostCardIdMsg(String cardId,String syllabusId, int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("Card_Id", cardId)
//                .add("DateTime", "11:11")
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.POSTCARDIDMSG, resCode);

        ConnectionClient.simpleCon(NetCartion.POSTCARDIDMSG+"?Card="+cardId+"&syllabusId="+syllabusId,resCode);
    }

    //获取考勤人数
    public static void getAttendanceCount(String syllabusId,int resCode){
        ConnectionClient.simpleCon(NetCartion.GETATTENDANCENUM+"?syllabusId="+syllabusId,resCode);
    }

    /**
     * 获取首页信息
     *
     * @param resCode
     */
    public static void getHomePager(String mac,int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("MainPage", "1")
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.GETHOMEPAGER, resCode);
        ConnectionClient.simpleCon(NetCartion.GETHOMEPAGER+"?type=1&mac="+mac+"&makeSureId="+ComUtils.getRandom()
                ,resCode);
    }

    /**
     * 获取课表(当天的课表)
     *
     * @param mac
     * @param resCode
     */

    public static void getSchedule(String mac, int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("Mac", mac)
//                .add("SerachData", date)
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.GETSCHEDULE, resCode);

        ConnectionClient.simpleCon(NetCartion.GETSCHEDULE+"?Mac="+mac+"&makeSureId="+ComUtils.getRandom()//+"&dt="
                ,resCode);
    }

    /**
     * 获取某天课表
     *
     * @param mac
     * @param resCode
     */

    public static void getNoDaySchedule(String mac,String date, int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("Mac", mac)
//                .add("SerachData", date)
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.GETSCHEDULE, resCode);

        ConnectionClient.simpleCon(NetCartion.GETSCHEDULE+"?Mac="+mac+"&makeSureId="+ComUtils.getRandom()+"&dt="+date
                ,resCode);
    }

    /**
     * 获取教室的列表
     *
     * @param resCode
     */
    public static void getClassRoom(int resCode) {
        RequestBody body = new FormBody.Builder()
                .add("makeSureId", ComUtils.getRandom())
                .build();
        ConnectionClient.simplePostCon(body, NetCartion.GETCLASSROOM, resCode);
    }

    /**
     * 绑定教室信息
     * @param mac
     * @param classId
     * @param resCode
     */
    public static void bondClassRoom(String mac, String classId, int resCode) {
//        RequestBody body = new FormBody.Builder()
//                .add("mac", mac)
//                .add("classRoomId", classId)
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.BINDCLASSROOM, resCode);
        ConnectionClient.simpleCon(NetCartion.BINDCLASSROOM+"?classRoomId="+classId+"&mac="+mac+
                "&makeSureId="+ComUtils.getRandom(),resCode);

    }

    /**
     * 检查版本号
     * @param resCode
     */
    public static void checkVisionCode(int resCode){
        ConnectionClient.simpleCon(NetCartion.CHECKVISION,resCode);
    }

    /**
     * 更新教室内人员数量
     * @param num
     * @param resCode
     */
    public static void upDateRoomPeoNum(int num,int resCode){
        ConnectionClient.simpleCon(NetCartion.POSTCLASSPEONUM+"?count="+num+
                "&makeSureId="+ComUtils.getRandom(),resCode);
//        RequestBody body = new FormBody.Builder()
//                .add("count", num+"")
//                .add("makeSureId", ComUtils.getRandom())
//                .build();
//        ConnectionClient.simplePostCon(body, NetCartion.POSTCLASSPEONUM, resCode);
    }

    /**
     * 获取离线一周课表
     * @param mac
     * @param resCode
     */

    public static void getNoNetWorkSchedule(String mac,int resCode){
        ConnectionClient.simpleCon(NetCartion.WEEKSCHEDULE+"?mac="
                +mac+"&makeSureId="+ComUtils.getRandom(),resCode);
    }

    /**
     * 上传android版本号和硬件的版本号
     * @param androidVision
     * @param hardVision
     * @param mac
     * @param resCode
     */
    public static void upDateAndroidAndHardWare(String androidVision,String hardVision,String mac,int resCode){
        ConnectionClient.simpleCon(NetCartion.UPDATEANDROIDANDHARDWARE+"?android="+androidVision+
        "&firmware="+hardVision+"&mac="+mac+"&makeSureId="+ComUtils.getRandom(),resCode);
    }

}
