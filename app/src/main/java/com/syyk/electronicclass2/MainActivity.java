package com.syyk.electronicclass2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.syyk.electronicclass2.adapter.MainPagerAdapter;
import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
import com.syyk.electronicclass2.database.RCDBHelper;
import com.syyk.electronicclass2.dialog.EntrySettingDialog;
import com.syyk.electronicclass2.fragment.AttendanceFragmnet;
import com.syyk.electronicclass2.fragment.HomePagerFragment;
import com.syyk.electronicclass2.fragment.IntroduceFragment;
import com.syyk.electronicclass2.fragment.LiveNewFragment;
import com.syyk.electronicclass2.fragment.NoticeFragment;
import com.syyk.electronicclass2.fragment.ScheduleFragment;
import com.syyk.electronicclass2.fragment.SettingFragmnet;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.ConnectionClient;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.serialport.ISerialPortConnection;
import com.syyk.electronicclass2.serialport.SerialPortConnection;
import com.syyk.electronicclass2.service.SignalRService;
import com.syyk.electronicclass2.ui.CustomViewPager;
import com.syyk.electronicclass2.utils.Catition;
import com.syyk.electronicclass2.utils.ComUtils;
import com.syyk.electronicclass2.utils.DateTimeUtil;
import com.syyk.electronicclass2.utils.JsonUtils;
import com.syyk.electronicclass2.utils.StringUtils;
import com.syyk.electronicclass2.utils.UpdateManger;
import com.xys.libzxing.zxing.encoding.EncodingUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.tv_school_name)
    TextView tv_school_name;
    @BindView(R.id.tv_building)
    TextView tv_building;
    @BindView(R.id.tv_date)
    TextView tv_date;
    @BindView(R.id.tv_sys_time)
    TextView tv_sys_time;
    @BindView(R.id.vp_content)
    CustomViewPager vp_content;
    @BindView(R.id.rg_tabs)
    RadioGroup rg_tabs;

    @BindView(R.id.main_savePeo_tv)
    TextView main_savePeo_tv;
    @BindView(R.id.main_savePeoPho_tv)
    TextView main_savePeoPho_tv;

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.main_signNum_tv)
    TextView main_signNum_tv;
    @BindView(R.id.main_AllNum_tv)
    TextView main_AllNum_tv;

    @BindView(R.id.tv_classroom)
    TextView tv_classroom;
    @BindView(R.id.tv_course)
    TextView tv_course;
    @BindView(R.id.tv_class_time_start)
    TextView tv_class_time_start;
    @BindView(R.id.tv_class_time_end)
    TextView tv_class_time_end;
    @BindView(R.id.tv_class)
    TextView tv_class;
    @BindView(R.id.tv_teacher)
    TextView tv_teacher;
    @BindView(R.id.tv_teacherPhone)
    TextView tv_teacherPhone;

    @BindView(R.id.main_QR_iv)
    ImageView main_QR_iv;

    @BindView(R.id.mian_title_Rl)
    RelativeLayout mian_title_Rl;
    @BindView(R.id.main_right_ll)
    LinearLayout main_right_ll;

    private List<Fragment> tableData = new ArrayList<>();
    private FragmentStatePagerAdapter pagerAdapter;

    private RCDBHelper db;
    private String frontCarId = "";

    //串口的数据
    private SerialPortConnection serial;
    private String devName3 = "/dev/ttyAMA2";//串口4（与之前的不同，ttyAMA4对应物理的串口的4）
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;

    private int getNetMsg = 105;

    private int classRoomPeopleNum = 0;
    private String cleanPeopleNumFlag = "";

    //刷管理员卡是否进入设置界面标志位
    private boolean isSettingFlag = false;
    //刷管理员卡是否进入设置界面提示框
    private EntrySettingDialog entrySettingDialog;

    private SignalRService mService;
    private boolean mBound = false;

    private String cardID = "";//上报的卡号


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
//        hideBottomUIMenu();
        //串口的初始化和数据的接收
        initPort();
        //删除之前下载的安装包
        UpdateManger.removeApk();
        //走接口，获取系统的时间
        Connection.getSystemTime(NetCartion.GETSYSTEMTIMEBACK);
        //开启连接SignalR的Service
        Intent intent = new Intent();
        intent.setClass(this, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //获取一周的课表
        getWeekSchedule();

        String name = ComUtils.getSave("name");
        if (name != null) {
            tv_classroom.setText(name);
        } else {
            tv_classroom.setText("请绑定教室");
        }

        //复位HDMI
        serial.writeHex("06A00600ACFF");

    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private void initPort() {
        serial = new SerialPortConnection();
        serial.setMessageListener(new ISerialPortConnection() {
            @Override
            public void onStart(String startResult) {
                if (startResult.equals("串口启动成功")) {
                    StringUtils.showLog("串口" + devName3 + "启动成功");
//                    isConnect = true;
                } else if (startResult.equals("串口启动失败")) {
                    StringUtils.showLog("串口" + devName3 + "启动失败");
                }
            }

            @Override
            public void onMessage(String message) {
                //字符串的形式接受数据
                StringUtils.showLog("TXT的串口接受：" + message);
            }

            @Override
            public void onHexStrMessage(String hexMessage) {
                StringUtils.showLog("HEX的串口接受：" + hexMessage);
                PortReciverSendData(ComUtils.hexStringToBytes(hexMessage));
            }
        });
        serial.connect(devfd, devName3, speed, dataBits, stopBits, "N", "N");
    }

    @Override
    protected void onResume() {
        ziHandler.sendEmptyMessageDelayed(Catition.UPDATECLASS, 20000);
        super.onResume();
    }

    /**
     * 数据的初始化
     */
    private void initData() {
        tableData.add(new HomePagerFragment());
        tableData.add(new NoticeFragment());
        tableData.add(new IntroduceFragment());
        tableData.add(new ScheduleFragment());
        tableData.add(new AttendanceFragmnet());
        tableData.add(new LiveNewFragment());
        tableData.add(new SettingFragmnet());

        db = new RCDBHelper(this);

        main_savePeo_tv.setText("管理员：" + ComUtils.getSave("adminName"));
        main_savePeoPho_tv.setText("电话：" + ComUtils.getSave("adminPhone"));

    }

    /**
     * View的初始化
     */
    private void initView() {
//        tv_school_name.setText("郑州市信息技术学校");
        tv_building.setText(ComUtils.getLocalVersionName(this));
        //时钟的初始化
        mRunnable.run();
        //选项卡
        rg_tabs.setOnCheckedChangeListener(this);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), tableData);
        vp_content.setAdapter(pagerAdapter);
        vp_content.setCurrentItem(0);
        vp_content.setOffscreenPageLimit(7); // 设置viewpager的缓存界面数

        entrySettingDialog = new EntrySettingDialog(this);
        entrySettingDialog.setOnentrySettingCancleListener(new EntrySettingDialog.entrySettingCancleListener() {
            @Override
            public void cancle() {
                isSettingFlag = false;
            }
        });
    }

    /**
     * 串口的数据解析
     * @param bean
     */
    private void PortReciverSendData(byte[] bean) {
        int ret = bean.length;
        byte[] res = null;
        int resnum = 0;
        int j = 0;
        for (int i = 0; i < ret; i++) {
            if (j == 0) {
                resnum = bean[i] & 0xFF;
                res = new byte[resnum];
            }
            if (j < resnum) {
                res[j] = bean[i];
            }
            if (j == (resnum - 1)) {
                j = 0;
                String resString = ComUtils.bytesToHexString(res, res.length);
                //发送广播
                StringUtils.showLog(resString + "   服务器方法");
                //服务器的方法的解析
                netReciverContorl(resString);
            } else {
                j++;
            }
        }
    }

    private void netReciverContorl(String data) {
        //0A A0 02 04 01 02 03 04 B0 FF
        String Uid = data.substring(0, 8);
        if (Uid.equals("0AB00104")) {
            //硬件上传的卡号
            int cardIdNum = Integer.parseInt(data.substring(6, 8), 16) * 2;
            String cardId = data.substring(8, 8 + cardIdNum);
            cardID = cardId;
            StringUtils.showToast("卡号为：" + cardId);
            if (cardId != null) {
                if (getIsSuperCard(cardId)) {//判断是否为超级管理员卡
                    if (isSettingFlag) {//进入设置界面
                        isSettingFlag = false;
                        entrySettingDialog.cancel();
                        //跳到设置界面
                        vp_content.setCurrentItem(6);
                        sendClaickMsg(6);
                        mian_title_Rl.setVisibility(View.VISIBLE);
                        main_right_ll.setVisibility(View.VISIBLE);
                    } else {//告诉硬件刷卡 开锁
                        serial.writeHex("07A0030101ACFF");
                    }
                }
                if(!isSettingFlag) {
                    String SyllabusId = ElectronicApplication.getmIntent().SyllabusId;
                    if (SyllabusId != null) {
                        Connection.PostCardIdMsg(cardId, SyllabusId, NetCartion.POSTCARDIDMSG_BACK);
                        frontCarId = cardId;
                    } else {
                        StringUtils.showToast("当前没有课程");
                    }
                }
            }
        } else if (data.startsWith("07B0020101")) {
            //有人进入
            classRoomPeopleNum++;
            StringUtils.showLog("教室进入一个人，现有：" + classRoomPeopleNum + "人");
//            StringUtils.showToast("教室进入一个人");
            Connection.upDateRoomPeoNum(classRoomPeopleNum, NetCartion.UPDATEROOMPEONUM_BACK);
        } else if (data.startsWith("07B0020102")) {
            //有人出去
            classRoomPeopleNum--;
            if (classRoomPeopleNum <= 0) {
                classRoomPeopleNum = 0;
            }
            StringUtils.showLog("教室出去一个人，现有：" + classRoomPeopleNum + "人");
//            StringUtils.showToast("教室出去一个人");
            Connection.upDateRoomPeoNum(classRoomPeopleNum, NetCartion.UPDATEROOMPEONUM_BACK);
        } else if (data.startsWith("08B00302")) {//08 B0 03 02 01 00 XX FF
            //硬件信息查询的返回
            int yingGao = Integer.parseInt(data.substring(8, 10), 16);
            int yingDi = Integer.parseInt(data.substring(10, 12), 16);
            String yingVisionCode = yingGao + "." + yingDi;
//            StringUtils.showToast("硬件版本号：" + yingVisionCode);
            String androidVisionCode = ComUtils.getLocalVersionCode(this) + "";
            //获取一周的课表
            String mac = ComUtils.getSave("mac");
            if (mac != null) {
                //走接口上传安卓版本号和硬件版本号
                Connection.upDateAndroidAndHardWare(androidVisionCode,
                        yingVisionCode, mac, NetCartion.UODATEANDROIDANDHARWARE_BACK);
            }
        }else if(data.startsWith("07B00301")){//人体红外的上报
            String state= data.substring(8,10);
            if(state.equals("01")){//有人
//                StringUtils.showToast("有人来了");
            }else if(state.equals("02")){//无人
//                StringUtils.showToast("人已经走了");
                //人走了，跳到课表界面
                vp_content.setCurrentItem(3);
                sendClaickMsg(3);
                mian_title_Rl.setVisibility(View.VISIBLE);
                main_right_ll.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainHttpEvent(HttpEventBean bean) {
        if (bean.getResCode() == NetCartion.SUCCESS) {
            switch (bean.getBackCode()) {
                case NetCartion.TESTBACK:
                    StringUtils.showToast(bean.getRes());
                    StringUtils.showLog(bean.getRes());
                    break;
                case NetCartion.GETSYSTEMTIMEBACK://系统返回系统时间的接口的返回
                    //系统时间返回成功后进行时间的
                    String date = JsonUtils.getJsonKey(bean.getRes(), "Message");
                    ElectronicApplication.getmIntent().timeMulis = DateTimeUtil.getCurFormat2Millis("MM/dd/yyyy HH:mm:ss", date);
                    String time = DateTimeUtil.getCurFormatStrTime("yyyy-MM-dd HH:mm:ss E", ElectronicApplication.getmIntent().timeMulis);
                    String[] timeArr = time.split(" ");
                    if (timeArr.length != 3) {
                        return;
                    }
                    ElectronicApplication.getmIntent().date = timeArr[0];
                    ElectronicApplication.getmIntent().week = timeArr[2];
                    //获取时间成功，通知各个界面联网获取数据
                    MessageBean messageBean = new MessageBean();
                    messageBean.setMsgCode(Catition.GETTIME_SUCCESS_UPDATE);
                    EventBus.getDefault().post(messageBean);
                    break;
                case NetCartion.POSTCARDIDMSG_BACK:
                    //签到结果的返回，主要用于更新数据库和签到界面并通知主界面
                    String data = bean.getRes();
                    String state = JsonUtils.getJsonKey(data, "Status");
                    String message = JsonUtils.getJsonKey(data, "Message");
                    if (state.equals("1")) {
                        if (message != null && message.equals("Start")) {//老师刷卡，代表课程开启
                            //代表课程开启,开教室门
                            serial.writeHex("07A0030101ACFF");
                        } else {//学生刷卡
                            StringUtils.showCenterToast("签到成功");
                            String num = JsonUtils.getJsonKey(JsonUtils.getJsonObject(data, "Model"), "Attdence");
                            String name = JsonUtils.getJsonKey(JsonUtils.getJsonObject(data, "Model"), "UserName");
                            main_signNum_tv.setText(num);
                            tv_name.setText(name);
                        }
                    } else {
                        StringUtils.showToast(message);
                    }
                    break;
                case NetCartion.GETATTENDANCECOUNT:
                    String jsonString = bean.getRes();
                    String numState = JsonUtils.getJsonKey(jsonString, "Status");
                    if (numState.equals("1")) {
                        String model = JsonUtils.getJsonKey(jsonString, "Model");
                        main_signNum_tv.setText(model);
                    }
                    break;
                case NetCartion.UPDATEROOMPEONUM_BACK:
                    //上传教室人数的返回
                    String String = bean.getRes();
                    String status = JsonUtils.getJsonKey(String, "Status");
                    if (status.equals("1")) {
                        StringUtils.showLog("上报实时人数成功");
                    }
                    break;
                case NetCartion.GETWEEKSCHEDULE://获取一周课表的返回
                    List<ScheduleBean> weekSchedule = new ArrayList<>();
                    String dataString = bean.getRes();
                    String State = JsonUtils.getJsonKey(dataString, "Status");
                    if (State.equals("1")) {
                        weekSchedule = JSON.parseArray(JsonUtils.getJsonArr(dataString, "Model")
                                .toString(), ScheduleBean.class);
                        //如果不为空，开启新线程存储数据
                        if (weekSchedule != null && weekSchedule.size() != 0) {
                            //取出管理员和能开门的卡号
                            ScheduleBean bean1 = weekSchedule.get(0);
                            String AdminName = bean1.getAdminName();
                            String AdminPhone = bean1.getAdminPhone();
                            String AdminCard = bean1.getAdminCard();
                            SharedPreferences preferences = getSharedPreferences("Setting", 0);
                            SharedPreferences.Editor editor = preferences.edit();
                            if (!StringUtils.isEmpty(AdminName)) {
                                editor.putString("adminName", AdminName);
                            }
                            if (!StringUtils.isEmpty(AdminPhone)) {
                                editor.putString("adminPhone", AdminPhone);
                            }
                            if (!StringUtils.isEmpty(AdminCard)) {
                                editor.putString("adminCard", AdminCard);
                            }
                            editor.commit();
                            //进行数据的存储
                            saveWeekSchedule(weekSchedule);
                        }else if(weekSchedule != null && weekSchedule.size() == 0){
                            //如果获取到的周课表长度为0，则删除缓存的课表
                            db.deleteAllSchedule();
                        }
                    }
                    break;
                case NetCartion.UODATEANDROIDANDHARWARE_BACK:
                    //走接口上传android版本号和硬件版本号的返回
                    StringUtils.showLog(bean.getRes());
                    break;
            }
        } else if (bean.getResCode() == NetCartion.FIAL) {
            StringUtils.showToast(bean.getRes());
            StringUtils.showLog("接口错误：" + bean.getRes());
            switch (bean.getBackCode()) {
                case NetCartion.POSTCARDIDMSG_BACK://刷卡上报失败后进行本地存储
                    StringUtils.showLog("刷卡失败 卡号：" + cardID);
                    String SyllabusId = ElectronicApplication.getmIntent().SyllabusId;
                    if (SyllabusId != null) {//放到数据库中
                        //先判断时候为老师的卡
                        if(isTeacher(SyllabusId,cardID)){
                            //如果是老师的卡，开门
                            serial.writeHex("07A0030101ACFF");
                        }else {
                            AttenBean attenBean = new AttenBean();
                            attenBean.setSyllabusid(SyllabusId);
                            attenBean.setCardid(cardID);
                            db.saveCardId(attenBean);
                            StringUtils.showToast("保存成功");
                        }
                    } else {
                        StringUtils.showToast("当前没有课程");
                    }
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainMsgEvent(MessageBean bean) {
        String SyllabusId;
        switch (bean.getMsgCode()) {
            case Catition.MENUSETTING:
            case Catition.UPSETTING:
            case Catition.DOWNSETTING:
            case Catition.OKSETTING:
                serial.writeHex(bean.getMsgs());
                break;
            case Catition.UPDATECLASSUI:
                //更新主页课表信息UI
                SyllabusId = ElectronicApplication.getmIntent().SyllabusId;
                if (null == SyllabusId) {
                    tv_course.setText("");
                    tv_class_time_start.setText("");
                    tv_class_time_end.setText("");
                    tv_class.setText("");
                    tv_teacher.setText("");
                    tv_teacherPhone.setText("");
                    main_AllNum_tv.setText("0");
                    main_signNum_tv.setText("0");
                    main_QR_iv.setImageResource(R.mipmap.noclass);
                } else {
                    for (ScheduleBean scheduleBean : ElectronicApplication.getmIntent().todaySchedule) {
                        if (SyllabusId.equals("" + scheduleBean.getSyllabusId())) {
                            main_savePeo_tv.setText("管理员：" + scheduleBean.getAdminName());
                            main_savePeoPho_tv.setText("电话：" + scheduleBean.getAdminPhone());
                            tv_course.setText(scheduleBean.getCategoryName());
                            tv_class_time_start.setText(scheduleBean.getStartTime());
                            tv_class_time_end.setText(" - "+scheduleBean.getEndTime());
                            tv_class.setText(scheduleBean.getClassName());
                            tv_teacher.setText(scheduleBean.getTeacherName());
                            tv_teacherPhone.setText(scheduleBean.getTeacherPhone());
                            main_AllNum_tv.setText(scheduleBean.getStudents());
                            //动态生成二维码
                            Bitmap bitmap = EncodingUtils.createQRCode(NetCartion.hip + "/Mobile/Login?Id=" + SyllabusId, 800, 800, null);
                            // 设置图片
                            main_QR_iv.setImageBitmap(bitmap);
                        }
                    }
                }
                break;
            case Catition.UPDATECLASS://更新数据信息
                SyllabusId = ElectronicApplication.getmIntent().SyllabusId;
//                //动态生成二维码
//                Bitmap bitmap = EncodingUtils.createQRCode(NetCartion.hip+"/Mobile/Login?Id="+SyllabusId, 1300, 1300, null);
//                // 设置图片
//                main_QR_iv.setImageBitmap(bitmap);
                //测试获取考勤人数
                Connection.getAttendanceCount(SyllabusId, NetCartion.GETATTENDANCECOUNT);
                //发送消息，通知刷新考勤界面
                MessageBean bean1 = new MessageBean();
                bean1.setMsgCode(Catition.REFRESH_ATTENDANCE);
                EventBus.getDefault().post(bean1);
                break;
            case Catition.UPDATECARDID:
                //更新主界面的签到信息
                main_signNum_tv.setText(bean.getMsgi() + "");
                tv_name.setText(bean.getMsgs());
                break;
            case Catition.BINGCLASSROOM_SUCCESS://如果绑定教室成功，获取离线课表
                String mac = ComUtils.getSave("mac");
                if (mac != null) {
                    Connection.getNoNetWorkSchedule(mac, NetCartion.GETWEEKSCHEDULE);
                }
                String name = ComUtils.getSave("name");
                if (name != null) {
                    tv_classroom.setText(name);
                } else {
                    tv_classroom.setText("请绑定教室");
                }
                break;
            case Catition.SETOPENCLOSEPING://设置屏幕的开关
                if (bean.getMsgi() == 0) {//关闭
                    serial.writeHex("07A0050102AFFF");
                } else if (bean.getMsgi() == 1) {//开启
                    serial.writeHex("07A0050101AEFF");
                }
                break;
            case Catition.SETDISPALYMILL:
                //设置屏幕的开关状态以及屏幕亮的时候的延时时间
                if (bean.getMsgs().equals("0")) {//关闭
                    serial.writeHex("07A0050102AFFF");
                } else if (bean.getMsgs().equals("1")) {//长开
                    serial.writeHex("07A0050101AEFF");
                }
//                08 A0 02 02 00 0A XX FF
                int timei = bean.getMsgi();
                String time = ComUtils.changeString(Integer.toHexString(timei), 4);
                String crc = Integer.toHexString(172 + timei);
                crc = crc.substring(crc.length() - 2, crc.length());
                serial.writeByte("08A00202" + time + crc + "FF", 500);
                break;
            /*************测试*****************/
            case Catition.GETYINGVERSION ://获取硬件的版本号
                //查询硬件版本号
                serial.writeHex("06A00400AAFF");
                break;
            case Catition.SETPINGCOPEN ://设置屏幕常亮
                if(bean.getMsgs().equals("open")) {
                    serial.writeHex("07A0050101AEFF");
                }else if(bean.getMsgs().equals("close")){
                    serial.writeHex("07A0050102AFFF");
//                    08 A0 02 02 00 0A XX FF
                    int time1i = bean.getMsgi();
                    String time1 = ComUtils.changeString(Integer.toHexString(time1i), 4);
                    String crc1 = Integer.toHexString(172 + time1i);
                    crc1 = crc1.substring(crc1.length() - 2, crc1.length());
                    serial.writeByte("08A00202" + time1 + crc1 + "FF",500);
                }
                break;
        }
    }

    /**
     * 存储一周的课表
     * @param beans
     */
    private void saveWeekSchedule(final List<ScheduleBean> beans) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.deleteAllSchedule();
                for (ScheduleBean bean : beans) {
                    db.saveSchedule(bean);
                }
            }
        }).start();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home_page:
                vp_content.setCurrentItem(0);
                sendClaickMsg(0);
                mian_title_Rl.setVisibility(View.VISIBLE);
                main_right_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_notice:
                vp_content.setCurrentItem(1);
                sendClaickMsg(1);
                mian_title_Rl.setVisibility(View.GONE);
                main_right_ll.setVisibility(View.GONE);
                break;
            case R.id.rb_introduce:
                vp_content.setCurrentItem(2);
                sendClaickMsg(2);
                mian_title_Rl.setVisibility(View.GONE);
                main_right_ll.setVisibility(View.GONE);
                break;
            case R.id.rb_schedule_time:
                vp_content.setCurrentItem(3);
                sendClaickMsg(3);
                mian_title_Rl.setVisibility(View.VISIBLE);
                main_right_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_attendance:
                vp_content.setCurrentItem(4);
                sendClaickMsg(4);
                mian_title_Rl.setVisibility(View.VISIBLE);
                main_right_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_live:
                vp_content.setCurrentItem(5);
                sendClaickMsg(5);
                mian_title_Rl.setVisibility(View.VISIBLE);
                main_right_ll.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_settings:
                isSettingFlag = true;
//                展示提示刷卡的提示框
                entrySettingDialog.show();
//                vp_content.setCurrentItem(6);
//                sendClaickMsg(6);
//                mian_title_Rl.setVisibility(View.VISIBLE);
//                main_right_ll.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 告诉界面是否被点击显示
     * @param position
     */
    private void sendClaickMsg(int position){
        MessageBean bean = new MessageBean();
        bean.setMsgCode(Catition.TELLFRAGMENTCLICKED);
        bean.setMsgi(position);
        EventBus.getDefault().post(bean);
    }

    /**
     * 判断是否为管理员的卡
     * @param card
     * @return
     */
    private boolean getIsSuperCard(String card) {
        String mac = ComUtils.getSave("mac");
        if (null != mac) {//软件首次运行时。刷任意卡即可进入设置界面
            return true;
        } else {//软件已经绑定教室后再去判断是否为管理员卡
            String adminCards = ComUtils.getSave("adminCard");
            if(adminCards.endsWith(",")){
                adminCards = adminCards.substring(0,adminCards.length()-1);
            }
            if((!StringUtils.isEmpty(adminCards)) && adminCards.contains(",")){
                String [] datas = adminCards.split(",");
                for (String dataCars : datas){
                    if(dataCars.equals(card)){
                        return true;
                    }
                }
            }else if((!StringUtils.isEmpty(adminCards)) && (! adminCards.contains(","))){
                if(card.equals(adminCards)){
                    return true;
                }
            }
//            return false;
            //暂时为方便测试，先全部返回true;
            return true;
        }
    }

    private boolean isTeacher(String SyllabusId,String cardID){
        List<ScheduleBean> data = db.getAllSchedule();
        for (ScheduleBean bean : data){
            if(SyllabusId.equals(bean.getSyllabusId()+"")){
                if(bean.getTeacherCard().equals(cardID)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    //刷新首页日期和时间
    private Handler handler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            ElectronicApplication.getmIntent().timeMulis += 1000;
            String time = DateTimeUtil.getCurFormatStrTime("yyyy-MM-dd HH:mm:ss E", ElectronicApplication.getmIntent().timeMulis);
            String[] timeArr = time.split(" ");
            if (timeArr.length != 3) {
                return;
            }
            ElectronicApplication.getmIntent().date = timeArr[0];
            ElectronicApplication.getmIntent().week = timeArr[2];
            tv_sys_time.setText(timeArr[1]);
            tv_date.setText(timeArr[0] + " " + timeArr[2]);
            handler.postDelayed(mRunnable, 1000);
        }
    };

    //进行一些课程时间的查询
    private Handler ziHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Catition.UPDATECLASS) {
                startMainPor();
            }
        }
    };
    private int upDataUIFlag = 1;
    Runnable ziRunnable = new Runnable() {
        @Override
        public void run() {
            StringUtils.showLog("查询一次");
            MessageBean bean2 = new MessageBean();
            bean2.setMsgCode(Catition.SIGNALR_SEND);
            EventBus.getDefault().post(bean2);

            String time = tv_sys_time.getText().toString();
            /*****************更新课程ID的判断 开始*********************/
            List<ScheduleBean> beans = ElectronicApplication.getmIntent().todaySchedule;
            for (int i = 0; i < beans.size(); i++) {
                if (i == 0) {//先判断是否为第一节课
                    if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", "06:00:00", beans.get(0).getEndTime(), time)) {
                        ElectronicApplication.getmIntent().SyllabusId = beans.get(0).getSyllabusId() + "";
                        StringUtils.showLog("显示第" + beans.get(0).getLessonNum() + "节课的东西");
                        if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", DateTimeUtil.changeTime(beans.get(0).getStartTime(), -600000),
                                beans.get(0).getStartTime(), time)) {
                            MessageBean bean = new MessageBean();
                            bean.setMsgCode(Catition.UPDATECLASS);
                            EventBus.getDefault().post(bean);
                        } else if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", beans.get(0).getStartTime(), beans.get(0).getEndTime(), time)) {
                            if (upDataUIFlag % 4 == 0) {
                                MessageBean bean = new MessageBean();
                                bean.setMsgCode(Catition.UPDATECLASS);
                                EventBus.getDefault().post(bean);
                            }
                        }
                    }
                } else {
                    if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", beans.get(i - 1).getEndTime(),
                            beans.get(i).getEndTime(), time)) {//判断中间的课程
                        ElectronicApplication.getmIntent().SyllabusId = beans.get(i).getSyllabusId() + "";
                        StringUtils.showLog("显示第" + beans.get(i).getLessonNum() + "节课的东西");
                        if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", DateTimeUtil.changeTime(beans.get(i).getStartTime(), -600000),
                                beans.get(i).getStartTime(), time)) {
                            MessageBean bean = new MessageBean();
                            bean.setMsgCode(Catition.UPDATECLASS);
                            EventBus.getDefault().post(bean);
                        } else if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", beans.get(i).getStartTime(), beans.get(i).getEndTime(), time)) {
                            if (upDataUIFlag % 4 == 0) {
                                MessageBean bean = new MessageBean();
                                bean.setMsgCode(Catition.UPDATECLASS);
                                EventBus.getDefault().post(bean);
                            }
                        }
                    } else if (DateTimeUtil.getTimeBetewnTimes("HH:mm:ss", beans.get(i).getEndTime()
                            , "23:59:00", time) && i == beans.size() - 1) {//判断最后一节课是否结束
                        ElectronicApplication.getmIntent().SyllabusId = null;
                        StringUtils.showLog("没有课了");
                        MessageBean bean = new MessageBean();
                        bean.setMsgCode(Catition.UPDATECLASS);
                        EventBus.getDefault().post(bean);
                    }
                }
            }
            /*****************更新课程ID的判断 结束*********************/
            /****************判断人体探测人数的清空 开始*****************/
            //只要日期不同就进行清空一次
            if (!cleanPeopleNumFlag.equals(ElectronicApplication.getmIntent().date)) {
                cleanPeopleNumFlag = ElectronicApplication.getmIntent().date;
                classRoomPeopleNum = 0;
            }
            /****************判断人体探测人数的清空 结束*****************/
            upDataUIFlag++;
            if(upDataUIFlag%26 == 0) {//大概20分钟
                //获取一周的课表
                getWeekSchedule();
            }
            if (upDataUIFlag == 54) {//大概40分钟
                //重置upDataUiFlag以及发送消息获取首页，公告和介绍
                upDataUIFlag = 1;
                MessageBean bean = new MessageBean();
                bean.setMsgCode(Catition.REFRESH_FRISTTHREE);
                EventBus.getDefault().post(bean);
                //查询硬件版本号
                serial.writeHex("06A00400AAFF");
                //发送消息，通知上传离线签到信息
                bean.setMsgCode(Catition.POSTUPNONETATTEN);
                EventBus.getDefault().post(bean);
            }
            //发送消息查看SignalR是否离线
            MessageBean bean = new MessageBean();
            bean.setMsgCode(Catition.UPDATECLASSUI);
            EventBus.getDefault().post(bean);

            //发送消息进行轮询
            ziHandler.sendEmptyMessageDelayed(Catition.UPDATECLASS, 45000);
        }
    };


    private void startMainPor() {
        new Thread(ziRunnable).start();
    }

    /**
     * 获取一周的课表
     */
    private void getWeekSchedule(){
        //获取一周的课表
        String mac = ComUtils.getSave("mac");
        if (mac != null) {
            Connection.getNoNetWorkSchedule(mac, NetCartion.GETWEEKSCHEDULE);
        }
    }

    @Override
    protected void onStop() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != handler) {
            handler.removeCallbacks(mRunnable);
        }
        if (null != ziHandler) {
            ziHandler.removeMessages(Catition.UPDATECLASS);
        }
        EventBus.getDefault().unregister(this);
    }


    private boolean isExit;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void exit() {
        if (!isExit) {
            isExit = true;
            StringUtils.showToast("再按一次退出程序");
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);
            System.exit(0);
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            isExit = false;
        }
    };


}
