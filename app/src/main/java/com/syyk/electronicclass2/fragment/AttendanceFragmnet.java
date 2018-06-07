package com.syyk.electronicclass2.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.AttenAdapter;
import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.bean.IntroAndNoticeBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
import com.syyk.electronicclass2.database.RCDBHelper;
import com.syyk.electronicclass2.dialog.CalendarDialog;
import com.syyk.electronicclass2.dialog.LoadingDialog;
import com.syyk.electronicclass2.dialog.backcall.GetCalendarDateCall;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.utils.Catition;
import com.syyk.electronicclass2.utils.ComUtils;
import com.syyk.electronicclass2.utils.DateTimeUtil;
import com.syyk.electronicclass2.utils.JsonUtils;
import com.syyk.electronicclass2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fei on 2017/12/29.
 */

public class AttendanceFragmnet extends Fragment{
    @BindView(R.id.attendance_date_tv)
    TextView attendance_date_tv;
    @BindView(R.id.attendance_week_tv)
    TextView attendance_week_tv;

    @BindView(R.id.attendance_rc)
    RecyclerView attendance_rc;

    private RCDBHelper db ;
    private List<ScheduleBean> attenDatas = new ArrayList<>();
    private List<ScheduleBean> todayAttenDatas = new ArrayList<>();

    private AttenAdapter attenAdapter;

    private CalendarDialog calendarDialog;

    private LoadingDialog loadingDialog;

    //离线的刷卡数据
    private List<AttenBean> noNetAttens = new ArrayList<>();
    //离线刷卡数据的标志
    private int position = 0;
    //离线刷卡的最大值
    private int maxPosition ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new RCDBHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attenAdapter = new AttenAdapter(getContext(),attenDatas);
        attendance_rc.setLayoutManager(new LinearLayoutManager(getContext()));
        attendance_rc.setAdapter(attenAdapter);

        calendarDialog = new CalendarDialog(getContext());
//        initData();
        attendance_date_tv.setText("2016-01-01");
        attendance_week_tv.setText("周六");

        calendarDialog.setOnDateBack(new GetCalendarDateCall() {
            @Override
            public void getDateString(String dateString) {
                attendance_date_tv.setText(dateString);
                attendance_week_tv.setText(DateTimeUtil.getCurFormatWeek("yyyy-MM-dd",dateString));
                StringUtils.showLog(dateString);
                String mac = ComUtils.getSave("mac");
                if(mac != null) {
                    Connection.getNoDaySchedule(mac,dateString, NetCartion.GETTOATTENDANC_BACK);
                }
            }
        });
        loadingDialog = new LoadingDialog(getContext(),"请稍等...");
    }

    private void initData(){
//        attenDatas = db.getAllCardId();
//        attenAdapter.setData(attenDatas);
    }

    @OnClick({R.id.attendance_today_bt,R.id.attendance_dateQ_iv,R.id.attendance_dateH_iv,R.id.attendance_date_iv})
    public void scheduleOnClick(View view){
        String mac = ComUtils.getSave("mac");
        switch (view.getId()){
            case R.id.attendance_today_bt ://回到今天
                attendance_date_tv.setText(ElectronicApplication.getmIntent().date);
                attendance_week_tv.setText(ElectronicApplication.getmIntent().week);
                attenDatas.clear();
                attenDatas.addAll(todayAttenDatas);
                attenAdapter.setData(attenDatas);
                break;
            case R.id.attendance_dateQ_iv ://日历前
                attendance_date_tv.setText(DateTimeUtil.changeDate(attendance_date_tv.getText().toString(),-1));
                attendance_week_tv.setText(DateTimeUtil.getCurFormatWeek("yyyy-MM-dd",attendance_date_tv.getText().toString()));
                if(mac != null) {
                    loadingDialog.show();
                    Connection.getNoDaySchedule(mac,attendance_date_tv.getText().toString(), NetCartion.GETNODAYATTENDANC_BACK);
                }
                break;
            case R.id.attendance_dateH_iv ://日历后
                attendance_date_tv.setText(DateTimeUtil.changeDate(attendance_date_tv.getText().toString(),1));
                attendance_week_tv.setText(DateTimeUtil.getCurFormatWeek("yyyy-MM-dd",attendance_date_tv.getText().toString()));
                if(mac != null) {
                    loadingDialog.show();
                    Connection.getNoDaySchedule(mac,attendance_date_tv.getText().toString(), NetCartion.GETNODAYATTENDANC_BACK);
                }
                break;
            case R.id.attendance_date_iv ://日历显示
                calendarDialog.show();
                calendarDialog.setDateS(attendance_date_tv.getText().toString());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attendanceEvnt(MessageBean bean){
        switch (bean.getMsgCode()){
            case Catition.BINGCLASSROOM_SUCCESS://如果绑定教室成功，获取课表
            case Catition.GETTIME_SUCCESS_UPDATE://联网获取时间成功，获取课表
                loadingDialog.cancel();
                attendance_date_tv.setText(ElectronicApplication.getmIntent().date);
                attendance_week_tv.setText(ElectronicApplication.getmIntent().week);
                //获取当天的课程信息
                String mac = ComUtils.getSave("mac");
                if(mac != null) {
                    //课程和考勤都在一起
                    Connection.getSchedule(mac, NetCartion.GETTOATTENDANC_BACK);
//                    Connection.getNoDaySchedule(mac,"2018/05/09",NetCartion.GETTOATTENDANC_BACK);
                }
                break;
            case Catition.REFRESH_ATTENDANCE:
                //获取当天的课程信息
                String mac1 = ComUtils.getSave("mac");
                if(mac1 != null) {
                    //课程和考勤都在一起
                    Connection.getSchedule(mac1, NetCartion.GETTOATTENDANCANDSCHE_BACK);
                }
                break;
            case Catition.POSTUPNONETATTEN :
                //上传离线的签到的信息
                noNetAttens = db.getAllCardId();
                position = 0;
                maxPosition = noNetAttens.size();
                if(noNetAttens.size() != 0){
                    Connection.PostCardIdMsg(noNetAttens.get(0).getCardid(),
                            noNetAttens.get(0).getSyllabusid(), Catition.POSTUPNONETATTEN);
                }
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AttenEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.GETTOATTENDANC_BACK://获取当天的考勤信息
                case NetCartion.GETTOATTENDANCANDSCHE_BACK:
                    loadingDialog.cancel();
                    String toDayResData = bean.getRes();
                    String toDayState = JsonUtils.getJsonKey(toDayResData,"Status");
                    if(toDayState.equals("1")){
                        todayAttenDatas = JSON.parseArray(JsonUtils.getJsonArr(toDayResData,"Model")
                                .toString(),ScheduleBean.class);
                        attenDatas.clear();
                        attenDatas.addAll(todayAttenDatas);
                        if(attendance_date_tv.getText().toString().equals(ElectronicApplication.getmIntent().date)) {
                            attenAdapter.setData(attenDatas);
                        }
                    }else{
                        StringUtils.showToast(JsonUtils.getJsonKey(toDayResData,"Message"));
                    }
                    break;
                case NetCartion.GETNODAYATTENDANC_BACK ://获取非当天的考勤信息
                    loadingDialog.cancel();
                    String noDayResData = bean.getRes();
                    String noDayState = JsonUtils.getJsonKey(noDayResData,"Status");
                    if(noDayState.equals("1")){
                        attenDatas = JSON.parseArray(JsonUtils.getJsonArr(noDayResData,"Model")
                                .toString(),ScheduleBean.class);
                        attenAdapter.setData(attenDatas);
                    }else{
                        StringUtils.showToast(JsonUtils.getJsonKey(noDayResData,"Message"));
                    }
                    break;
                case Catition.POSTUPNONETATTEN ://离线的考勤上传的返回
                    String data = bean.getRes();
                    String state = JsonUtils.getJsonKey(data, "Status");
                    if (state.equals("1")) {
                        //签到成功后删除数据库中的信息
                        db.deleteCardId(noNetAttens.get(position).getCardid()
                                ,noNetAttens.get(position).getSyllabusid());
                        position ++;
                        if(position<maxPosition){
                            Connection.PostCardIdMsg(noNetAttens.get(position).getCardid(),
                                    noNetAttens.get(position).getSyllabusid(), Catition.POSTUPNONETATTEN);
                        }
                    }
                    break;
            }
        }else if(bean.getResCode() == NetCartion.FIAL){
            loadingDialog.cancel();
            StringUtils.showToast(bean.getRes());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
