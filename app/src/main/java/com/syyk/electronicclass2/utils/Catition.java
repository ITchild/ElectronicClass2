package com.syyk.electronicclass2.utils;

/**
 * Created by fei on 2018/1/2.
 */

public class Catition {

    public static final String UpDateApkUrl = "/sdcard/电子班牌/";
    public static final String UpDateApkName = UpDateApkUrl+"电子班牌.apk";


    //设置屏幕亮度
    public static final int SETTINGLIANGDU = 1000;
    //跟新主界面的签到信息
    public static final int UPDATECARDID = 1001;
    //删除昨天的签到信息
    public static final int DELETECARDID = 1003;
    //更新主页的课表
    public static final int UPDATECLASS = 1004;
    //更新主页的课表
    public static final int UPDATECLASSUI = -1004;
    //屏幕的菜单
    public static final int MENUSETTING = 1005;
    //屏幕的上翻
    public static final int UPSETTING = 1006;
    //屏幕的下翻
    public static final int DOWNSETTING = 1007;
    //屏幕的确认按键
    public static final int OKSETTING = 1008;
    //绑定教室成功
    public static final int BINGCLASSROOM_SUCCESS = 1009;
    //系统时间获取成功，通知个界面联网获取数据
    public static final int GETTIME_SUCCESS_UPDATE = 1010;
    //刷新考勤界面
    public static final int REFRESH_ATTENDANCE = 1011;
    //刷新首页公告和介绍
    public static final int REFRESH_FRISTTHREE = 1012;
    //通知SignalR发送消息
    public static final int SIGNALR_SEND = 1013;
    //开始上传离线签到的信息
    public static final int POSTUPNONETATTEN = 1014;
    //刷新课表
    public static final int REFRESHSCHEDULE = 1015;

    //设置屏幕开关的标志
    public static final int SETOPENCLOSEPING = 1016;
    //设置屏幕开启的延时的时间
    public static final int SETDISPALYMILL = 1017;


    //通知界面被点击的事件标志
    public static final int TELLFRAGMENTCLICKED = 1018;



    /**************测试的时候使用的********************/
    //查询硬件版本号
    public static final int GETYINGVERSION = 30001;
    //设置屏幕常亮
    public static final int SETPINGCOPEN = 30002;

}
