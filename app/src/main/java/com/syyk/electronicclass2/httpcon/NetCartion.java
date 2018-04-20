package com.syyk.electronicclass2.httpcon;

import com.syyk.electronicclass2.utils.ComUtils;

/**
 * Created by fei on 2017/12/29.
 */

public class NetCartion {

    /**
     * String 类型URL部分
     */

    private static final String ip = "http://"+ComUtils.getSave("ip")+":9512";

    public static final String testip = "http://192.168.1.198:1192/Home/GetUserListPost";

    public static final String CARDID_CHEXKED = ip+"/JavaInterface/JavaHandler.ashx";
    //介绍和公告
    public static final String GETINTROANDNOTICE = ip+"/AndroidHandler/NoticeHandler.ashx";
    //上传签到信息
    public static final String POSTCARDIDMSG = ip +"/AndroidHandler/CardHandler.ashx";
    //获取首页
    public static final String GETHOMEPAGER = ip+"/AndroidHandler/NoticeHandler.ashx";
    //获取课表
    public static final String GETSCHEDULE = ip+"/AndroidHandler/CourseHandler.ashx";
    //获取教室的列表
    public static final String GETCLASSROOM = ip+"/AndroidHandler/AskHandler.ashx";
    //绑定教室
    public static final String BINDCLASSROOM = ip+"/AndroidHandler/EquipHandler.ashx";



    /**
     * int 类型的返回标志位置部分
     */


    public static final int SUCCESS = 1;
    public static final int FIAL = -1;

    public static final int CARDID_CHEXKED_BACK = 100;
    public static final int GETINTROANDNOTICE_BACK = 101;
    public static final int POSTCARDIDMSG_BACK = 102;
    //获取首页的返回
    public static final int GETHOMEPAGER_BACK = 103;
    //获取课表的返回
    public static final int GETSCHEDULE_BACK = 104;
    //获取教室的返回
    public static final int GETCLASSROOM_BACK = 105;
    //绑定教室的返回
    public static final int BONDCLASSROOM_BACK = 106;

}
