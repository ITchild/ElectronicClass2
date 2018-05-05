package com.syyk.electronicclass2;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.syyk.electronicclass2.adapter.MainPagerAdapter;
import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.bean.CardIdBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
import com.syyk.electronicclass2.database.RCDBHelper;
import com.syyk.electronicclass2.fragment.AttendanceFragmnet;
import com.syyk.electronicclass2.fragment.HomePagerFragment;
import com.syyk.electronicclass2.fragment.IntroduceFragment;
import com.syyk.electronicclass2.fragment.LivewFragment;
import com.syyk.electronicclass2.fragment.NoticeFragment;
import com.syyk.electronicclass2.fragment.ScheduleFragment;
import com.syyk.electronicclass2.fragment.SettingFragmnet;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.serialport.ISerialPortConnection;
import com.syyk.electronicclass2.serialport.SerialPortConnection;
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

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
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

    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.main_signNum_tv)
    TextView main_signNum_tv;
    @BindView(R.id.main_AllNum_tv)
    TextView main_AllNum_tv;


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

    @BindView(R.id.main_QR_iv)
    ImageView main_QR_iv;

    private List<Fragment> tableData = new ArrayList<>();
    private FragmentStatePagerAdapter pagerAdapter;

    private RCDBHelper db ;
    private String frontCarId = "";

    //串口的数据
    private SerialPortConnection serial;
    private String devName3 = "/dev/ttyAMA2";//串口4（与之前的不同，ttyAMA4对应物理的串口的4）
    private int speed = 115200;
    private int dataBits = 8;
    private int stopBits = 1;
    private int devfd = -1;

    private int getNetMsg = 105;
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

        Bitmap bitmap = EncodingUtils.createQRCode(NetCartion.hip+"/Mobile/Login?Id=59640", 1300, 1300, null);
        // 设置图片
        main_QR_iv.setImageBitmap(bitmap);
        //删除之前下载的安装包
        UpdateManger.removeApk();
        //走接口，获取系统的时间
        Connection.getSystemTime(NetCartion.GETSYSTEMTIMEBACK);
//        //测试考勤
//        Connection.PostCardIdMsg("0725825865e44ab699ddbe883a26d39e","59640",NetCartion.POSTCARDIDMSG_BACK);
//        //测试获取考勤人数
//        Connection.getAttendanceCount("59640",NetCartion.GETATTENDANCECOUNT);
    }

    private void initPort(){
        serial = new SerialPortConnection();
        serial.setMessageListener(new ISerialPortConnection() {
            @Override
            public void onStart(String startResult) {
                if (startResult.equals("串口启动成功")) {
                    StringUtils.showLog("串口"+devName3+"启动成功");
//                    isConnect = true;
                }else if(startResult.equals("串口启动失败")){
                    StringUtils.showLog("串口"+devName3+"启动失败");
                }
            }
            @Override
            public void onMessage(String message) {
                //字符串的形式接受数据
            }
            @Override
            public void onHexStrMessage(String hexMessage) {
                PortReciverSendData(ComUtils.hexStringToBytes(hexMessage));
            }
        });
        serial.connect(devfd, devName3, speed, dataBits, stopBits,"N","N");
    }

    @Override
    protected void onResume() {
        ziHandler.postDelayed(ziRunnable,20000);
        super.onResume();
    }

    /**
     * 数据的初始化
     */
    private void initData(){
        tableData.add(new HomePagerFragment());
        tableData.add(new NoticeFragment());
        tableData.add(new IntroduceFragment());
        tableData.add(new ScheduleFragment());
      //  tableData.add(new LivewFragment());
        tableData.add(new AttendanceFragmnet());
        tableData.add(new SettingFragmnet());

        db = new RCDBHelper(this);
    }

    /**
     * View的初始化
     */
    private void initView(){
        tv_school_name.setText("郑州市信息技术学校");
        tv_building.setText(ComUtils.getLocalVersionName(this));
        //时钟的初始化
        mRunnable.run();
        //选项卡
        rg_tabs.setOnCheckedChangeListener(this);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager(),tableData);
        vp_content.setAdapter(pagerAdapter);
        vp_content.setCurrentItem(0);
        vp_content.setOffscreenPageLimit(7); // 设置viewpager的缓存界面数
    }
    /**
     * 串口的数据解析
     * @param bean
     */
    private void PortReciverSendData(byte [] bean) {
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

    private void netReciverContorl(String data){
        //0A A0 02 04 01 02 03 04 XX FF
        String Uid = data.substring(0,8);
        switch (Uid){
            case "0AA00204" :
                //硬件上传的卡号
                int cardIdNum = Integer.parseInt(data.substring(6,8),16)*2;
                String cardId = data.substring(8,8+cardIdNum);
                StringUtils.showLog("卡号为："+cardId);
                if(cardId != null){
                    if(!frontCarId.equals(cardId)) {
                        String SyllabusId = ElectronicApplication.getmIntent().SyllabusId;
                        if(SyllabusId != null) {
                            Connection.PostCardIdMsg("cardId",SyllabusId,NetCartion.POSTCARDIDMSG_BACK);
                            frontCarId = cardId;
                        }else {
                            StringUtils.showToast("当前没有课程");
                        }
                    }else{
                        StringUtils.showCenterToast("你已经签到了！！！");
                    }
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainHttpEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.TESTBACK :
                    StringUtils.showToast(bean.getRes());
                    StringUtils.showLog(bean.getRes());
                    break;
                case NetCartion.GETSYSTEMTIMEBACK://系统返回系统时间的接口的返回
                    //系统时间返回成功后进行时间的
                    String date = JsonUtils.getJsonKey(bean.getRes(),"Message");
                    ElectronicApplication.getmIntent().timeMulis = DateTimeUtil.getCurFormat2Millis("MM/dd/yyyy HH:mm:ss",date);
                    break;
                case NetCartion.POSTCARDIDMSG_BACK :
                    //签到结果的返回，主要用于更新数据库和签到界面并通知主界面
                    String data = bean.getRes();
                    String state = JsonUtils.getJsonKey(data,"Status");
                    String message = JsonUtils.getJsonKey(data,"Message");
                    if(state.equals("1")){
                        if(message != null &&message.equals("Start")){//老师刷卡

                        }else{//学生刷卡

                        }
                    }else{
                        StringUtils.showToast(message);
                    }
                    break;
            }
        }else if(bean.getResCode() == NetCartion.FIAL){
            StringUtils.showToast(bean.getRes());
            StringUtils.showLog("接口错误："+bean.getRes());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void mainMsgEvent(MessageBean bean){
        switch (bean.getMsgCode()){
//            case Catition.SETTINGLIANGDU ://设置屏幕的亮度
//                serial.writeHex("060101"+bean.getMsgs()+"05FF");
//                break;
            case Catition.MENUSETTING:
            case Catition.UPSETTING:
            case Catition.DOWNSETTING:
            case Catition.OKSETTING:
                serial.writeHex(bean.getMsgs());
                break;
            case Catition.UPDATECLASS:
                //更新主页课表信息
//                ScheduleBean scheduleBean = bean.getBean();
//                tv_course.setText(scheduleBean.getCourse());
//                tv_class_time_start.setText(scheduleBean.get_starttime());
//                tv_class_time_end.setText(scheduleBean.get_endtime());
//                tv_class.setText(scheduleBean.get_c_id());
//                tv_teacher.setText(scheduleBean.getTeacherName());
//                main_AllNum_tv.setText(scheduleBean.getQuantity());
                break;
            case Catition.UPDATECARDID :
                //更新主界面的签到信息
                main_signNum_tv.setText(bean.getMsgi()+"");
                tv_name.setText(bean.getMsgs());
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home_page:
                vp_content.setCurrentItem(0);
                break;
            case R.id.rb_notice:
                vp_content.setCurrentItem(1);
                break;
            case R.id.rb_introduce:
                vp_content.setCurrentItem(2);
                break;
            case R.id.rb_schedule_time:
                vp_content.setCurrentItem(3);
                break;
            case R.id.rb_live:
                vp_content.setCurrentItem(4);
                break;
            case R.id.rb_attendance:
                vp_content.setCurrentItem(4);
                break;
            case R.id.rb_settings:
                vp_content.setCurrentItem(5);
                break;
        }
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
            String time = DateTimeUtil.getCurFormatStrTime("yyyy-MM-dd HH：mm：ss E",ElectronicApplication.getmIntent().timeMulis);
            String [] timeArr = time.split(" ");
            if(timeArr.length != 3) {
                return;
            }
            tv_sys_time.setText(timeArr[1]);
            tv_date.setText(timeArr[0] + " " + timeArr[2]);
            handler.postDelayed(mRunnable,1000);
        }
    };

    //进行一些自动的访问接口
    private Handler ziHandler = new Handler();
    Runnable ziRunnable = new Runnable() {
        @Override
        public void run() {
            StringUtils.showLog("查询一次");
            ziHandler.postDelayed(ziRunnable,35000);
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != handler) {
            handler.removeCallbacks(mRunnable);
        }
        EventBus.getDefault().unregister(this);
    }
}
