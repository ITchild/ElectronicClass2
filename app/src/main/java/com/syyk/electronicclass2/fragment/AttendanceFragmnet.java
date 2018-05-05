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
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.AttenAdapter;
import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.bean.IntroAndNoticeBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
import com.syyk.electronicclass2.database.RCDBHelper;
import com.syyk.electronicclass2.dialog.CalendarDialog;
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
    private List<ScheduleBean> postDatas = new ArrayList<>();

    private AttenAdapter attenAdapter;

    private CalendarDialog calendarDialog;
    private String dataS;

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
        dataS = DateTimeUtil.getCurDate("yyyy-MM-dd");
//        initData();

        attendance_date_tv.setText("2016年01月01日");
        attendance_week_tv.setText("周六");

        calendarDialog.setOnDateBack(new GetCalendarDateCall() {
            @Override
            public void getDateString(String dateString) {
                dataS = dateString;
                StringUtils.showLog(dataS);
            }
        });

        //获取当天的课程信息
        String mac = ComUtils.getSave("mac");
        if(mac != null) {
            //课程和考勤都在一起
            Connection.getSchedule(mac, NetCartion.GETTOATTENDANC_BACK);
        }
    }

    private void initData(){
//        attenDatas = db.getAllCardId();
//        attenAdapter.setData(attenDatas);
    }

    @OnClick({R.id.attendance_today_bt,R.id.attendance_dateQ_iv,R.id.attendance_dateH_iv,R.id.attendance_date_iv})
    public void scheduleOnClick(View view){
        switch (view.getId()){
            case R.id.attendance_today_bt ://回到今天
                break;
            case R.id.attendance_dateQ_iv ://日历前
                break;
            case R.id.attendance_dateH_iv ://日历后
                break;
            case R.id.attendance_date_iv ://日历显示
                calendarDialog.show();
                calendarDialog.setDateS(dataS);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void attendanceEvnt(MessageBean bean){
        switch (bean.getMsgCode()){
            case Catition.DELETECARDID :
//                //定期删除本地签到信息
//                db.deleteAllCardId();
//                initData();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AttenEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.GETTOATTENDANC_BACK://获取当天的考勤信息
                    String toDayResData = bean.getRes();
                    String toDayState = JsonUtils.getJsonKey(toDayResData,"Status");
                    if(toDayState.equals("1")){
                        attenDatas = JSON.parseArray(JsonUtils.getJsonArr(toDayResData,"Model")
                                .toString(),ScheduleBean.class);
                        attenAdapter.setData(attenDatas);
                    }else{
                        StringUtils.showToast(JsonUtils.getJsonKey(toDayResData,"Message"));
                    }
                    break;
                case NetCartion.GETNODAYATTENDANC_BACK ://获取非当天的考勤信息
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
            }
        }else if(bean.getResCode() == NetCartion.FIAL){
            StringUtils.showToast(bean.getRes());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
