package com.syyk.electronicclass2.httpcon;

import com.syyk.electronicclass2.utils.ComUtils;

/**
 * Created by fei on 2017/12/29.
 */

public class NetCartion {

    /**
     * String 类型URL部分
     */

    private static final String ip = "http://"+ComUtils.getSave("ip")+":"+ComUtils.getSave("port");

    public static final String hip = "http://"+ComUtils.getSave("hip")+":"+ComUtils.getSave("hport");

    public static final String testip = "http://192.168.1.198:9000/api/ClassCard/ClassRooms";

    //获取服务器的时间
    public static final String GET_SYSTEMTIME = ip+"/api/ClassCard/CurrentTime";

    public static final String CARDID_CHEXKED = ip+"/JavaInterface/JavaHandler.ashx";
    //介绍和公告
    public static final String GETINTROANDNOTICE = ip+"/AndroidHandler/NoticeHandler.ashx";
    //上传签到信息
    public static final String POSTCARDIDMSG = ip +"/api/ClassCard/Attdence";
    //获取考勤人数
    public static final String GETATTENDANCENUM = ip+"/api/ClassCard/GetAttdenceCount";
    //获取首页
    public static final String GETHOMEPAGER = ip+"/api/ClassCard/ContentByType";
    //获取课表
    public static final String GETSCHEDULE = ip+"/api/ClassCard/Schedule";
    //获取教室的列表
    public static final String GETCLASSROOM = ip+"/api/ClassCard/ClassRooms";
    //绑定教室
    public static final String BINDCLASSROOM = ip+"/api/ClassCard/GennerClassRoomProperty";
    //检查版本号
    public static final String CHECKVISION = ip+"/api/ClassCard/GetAndroidVersion";
    //上报教室的人数
    public static final String POSTCLASSPEONUM = ip+"/api/ClassCard/ClassRoomUsers";

    /**
     * int 类型的返回标志位置部分
     */

    public static final int SUCCESS = 1;
    public static final int FIAL = -1;

    //测试的返回接口
    public static final int TESTBACK = -1000;
    //返回系统时间的返回接口
    public static final int GETSYSTEMTIMEBACK = 999;

    public static final int CARDID_CHEXKED_BACK = 100;
    //介绍的接口的返回的标志
    public static final int GETINTRODUCE_BACK = 101;
    //公告的接口的返回的标志
    public static final int GETNOTICE_BACK = -101;
    //刷卡签到的返回
    public static final int POSTCARDIDMSG_BACK = 102;
    //获取首页的返回
    public static final int GETHOMEPAGER_BACK = 103;
    //获取当天课表的返回
    public static final int GETTODAYSCHEDULE_BACK = 104;
    //获取非当天的课表的返回
    public static final int GETNODAYSCHEDULE_BACK = -104;
    //获取教室的返回
    public static final int GETCLASSROOM_BACK = 105;
    //绑定教室的返回
    public static final int BONDCLASSROOM_BACK = 106;
    //获取考勤人数的返回
    public static final int GETATTENDANCECOUNT = 107;
    //获取当天考勤人数的返回
    public static final int GETTOATTENDANC_BACK = 108;
    //获取当天考勤和课表同事解析的返回
    public static final int GETTOATTENDANCANDSCHE_BACK = -108;
    //获取非当天的考勤人数的但会
    public static final int GETNODAYATTENDANC_BACK = 109;
    //获取软件版本的接口的返回
    public static final int CHECKVISIONCODE_BACK = 110;
    //上传教室内部人数的接口返回
    public static final int UPDATEROOMPEONUM_BACK = 111;

}
