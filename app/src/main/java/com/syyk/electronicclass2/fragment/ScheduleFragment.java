package com.syyk.electronicclass2.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.ScheduleAdapter;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fei on 2017/12/29.
 */

public class ScheduleFragment extends Fragment {
    @BindView(R.id.schedule_date_tv)
    TextView schedule_date_tv;
    @BindView(R.id.schedule_week_tv)
    TextView schedule_week_tv;
    @BindView(R.id.schedule_today_bt)
    Button schedule_today_bt;

    @BindView(R.id.schedule_dis_rv)
    RecyclerView schedule_dis_rv;


    private List<ScheduleBean> toDayData = new ArrayList<>();
    private List<ScheduleBean> noDayData = new ArrayList<>();
    private List<ScheduleBean> data = new ArrayList<>();
    private ScheduleAdapter scheduleAdapter;

    private CalendarDialog calendarDialog;

    private LoadingDialog loadingDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scheduleAdapter = new ScheduleAdapter(getContext(), data);
        schedule_dis_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        schedule_dis_rv.setAdapter(scheduleAdapter);

        schedule_date_tv.setText("2016-01-01");
        schedule_week_tv.setText("周六");

        calendarDialog = new CalendarDialog(getContext());

        calendarDialog.setOnDateBack(new GetCalendarDateCall() {
            @Override
            public void getDateString(String dateString) {
                schedule_date_tv.setText(dateString);
                schedule_week_tv.setText(DateTimeUtil.getCurFormatWeek("yyyy-MM-dd",dateString));
                StringUtils.showLog(dateString);
                String mac = ComUtils.getSave("mac");
                if(mac != null) {
                    Connection.getNoDaySchedule(mac,dateString, NetCartion.GETNODAYSCHEDULE_BACK);
                }
            }
        });

        loadingDialog = new LoadingDialog(getContext(),"请稍等...");
    }

    @OnClick({R.id.schedule_today_bt, R.id.schedule_dateQ_iv, R.id.schedule_dateH_iv, R.id.schedule_date_iv})
    public void scheduleOnClick(View view) {
        String mac = ComUtils.getSave("mac");
        switch (view.getId()) {
            case R.id.schedule_today_bt://回到今天
                schedule_date_tv.setText(ElectronicApplication.getmIntent().date);
                schedule_week_tv.setText(ElectronicApplication.getmIntent().week);
                data.clear();
                data.addAll(toDayData);
                scheduleAdapter.setData(data);
                break;
            case R.id.schedule_dateQ_iv://日历前
                schedule_date_tv.setText(DateTimeUtil.changeDate(schedule_date_tv.getText().toString(),-1));
                schedule_week_tv.setText(DateTimeUtil.getCurFormatWeek("yyyy-MM-dd",schedule_date_tv.getText().toString()));
                if(mac != null) {
                    loadingDialog.show();
                    Connection.getNoDaySchedule(mac,schedule_date_tv.getText().toString(), NetCartion.GETNODAYSCHEDULE_BACK);
                }
                break;
            case R.id.schedule_dateH_iv://日历后
                schedule_date_tv.setText(DateTimeUtil.changeDate(schedule_date_tv.getText().toString(),1));
                schedule_week_tv.setText(DateTimeUtil.getCurFormatWeek("yyyy-MM-dd",schedule_date_tv.getText().toString()));
                if(mac != null) {
                    loadingDialog.show();
                    Connection.getNoDaySchedule(mac,schedule_date_tv.getText().toString(), NetCartion.GETNODAYSCHEDULE_BACK);
                }
                break;
            case R.id.schedule_date_iv://日历显示
                calendarDialog.show();
                calendarDialog.setDateS(schedule_date_tv.getText().toString());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scheduleEvent(HttpEventBean bean) {
        if (bean.getResCode() == NetCartion.SUCCESS) {
            switch (bean.getBackCode()) {
                case NetCartion.GETTODAYSCHEDULE_BACK:
                case NetCartion.GETTOATTENDANCANDSCHE_BACK:
                    loadingDialog.cancel();
                    String toDayResData = bean.getRes();
                    String toDayState = JsonUtils.getJsonKey(toDayResData, "Status");
                    if (toDayState.equals("1")) {
                        toDayData = JSON.parseArray(JsonUtils.getJsonArr(toDayResData, "Model")
                                .toString(), ScheduleBean.class);
                        ElectronicApplication.getmIntent().todaySchedule = toDayData;
                        data.clear();
                        data.addAll(toDayData);
                        StringUtils.showLog("课表的节数：" + data.size());
                        if(schedule_date_tv.getText().toString().equals(ElectronicApplication.getmIntent().date)) {
                            scheduleAdapter.setData(data);
                        }
                    } else {
                        StringUtils.showToast(JsonUtils.getJsonKey(toDayResData, "Message"));
                    }
                    break;
                case NetCartion.GETNODAYSCHEDULE_BACK:
                    loadingDialog.cancel();
                    String noDayResData = bean.getRes();
                    String noDayState = JsonUtils.getJsonKey(noDayResData, "Status");
                    if (noDayState.equals("1")) {
                        noDayData = JSON.parseArray(JsonUtils.getJsonArr(noDayResData, "Model")
                                .toString(), ScheduleBean.class);
                        data.clear();
                        data.addAll(noDayData);
                        scheduleAdapter.setData(data);
                    } else {
                        StringUtils.showToast(JsonUtils.getJsonKey(noDayResData, "Message"));
                    }
                    break;
            }
        } else if (bean.getResCode() == NetCartion.FIAL) {
            loadingDialog.cancel();
            StringUtils.showToast(bean.getRes());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scheduleMsgEvent(MessageBean messageBean) {
        switch (messageBean.getMsgCode()) {
            case Catition.BINGCLASSROOM_SUCCESS://如果绑定教室成功，获取课表
            case Catition.GETTIME_SUCCESS_UPDATE://联网获取时间成功，获取课表
                schedule_date_tv.setText(ElectronicApplication.getmIntent().date);
                schedule_week_tv.setText(ElectronicApplication.getmIntent().week);
                //获取当天的课程信息
                String mac = ComUtils.getSave("mac");
                if (mac != null) {
                    Connection.getSchedule(mac, NetCartion.GETTODAYSCHEDULE_BACK);
//                    Connection.getNoDaySchedule(mac,"2018/05/09",NetCartion.GETTODAYSCHEDULE_BACK);
                }
                break;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
