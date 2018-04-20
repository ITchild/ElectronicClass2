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
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.ScheduleAdapter;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.ScheduleBean;
import com.syyk.electronicclass2.dialog.CalendarDialog;
import com.syyk.electronicclass2.dialog.backcall.GetCalendarDateCall;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.utils.Catition;
import com.syyk.electronicclass2.utils.ComUtils;
import com.syyk.electronicclass2.utils.DateTimeUtil;
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


    private List<ScheduleBean> data = new ArrayList<>();
    private ScheduleAdapter scheduleAdapter;

    private CalendarDialog calendarDialog;

    private String dataS;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scheduleAdapter = new ScheduleAdapter(getContext(),data);
        schedule_dis_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        schedule_dis_rv.setAdapter(scheduleAdapter);

        schedule_date_tv.setText("2016年01月01日");
        schedule_week_tv.setText("周六");

        calendarDialog = new CalendarDialog(getContext());
        //先获取一次课表
//        Connection.getSchedule(ComUtils.getMac(), DateTimeUtil.delete0("yyyy/MM/dd"), NetCartion.GETSCHEDULE_BACK);
        Connection.getSchedule(ComUtils.getMac(),"2017/5/24", NetCartion.GETSCHEDULE_BACK);

        //开始课表的轮询
        new Thread(mRunable).start();



        calendarDialog.setOnDateBack(new GetCalendarDateCall() {
            @Override
            public void getDateString(String dateString) {
                dataS = dateString;
                StringUtils.showLog(dataS);
            }
        });
    }

    @OnClick({R.id.schedule_today_bt,R.id.schedule_dateQ_iv,R.id.schedule_dateH_iv,R.id.schedule_date_iv})
    public void scheduleOnClick(View view){
        switch (view.getId()){
            case R.id.schedule_today_bt ://回到今天
                break;
            case R.id.schedule_dateQ_iv ://日历前
                break;
            case R.id.schedule_dateH_iv ://日历后
                break;
            case R.id.schedule_date_iv ://日历显示
                calendarDialog.show();
                if(dataS != null)
                calendarDialog.setDateS(dataS);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scheduleEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch (bean.getBackCode()){
                case NetCartion.GETSCHEDULE_BACK :
                    String resData = bean.getRes();
                    StringUtils.showLog(resData);
                    data = JSON.parseArray(resData,ScheduleBean.class);
                    scheduleAdapter.setData(data);
                    break;
            }
        }else if(bean.getResCode() == NetCartion.FIAL){
            StringUtils.showToast(bean.getRes());
        }
    }

    private Handler handler = new Handler();
    private Runnable mRunable = new Runnable() {
        @Override
        public void run() {
            String date = DateTimeUtil.getCurFormatTime("HH:mm");
            long datel = DateTimeUtil.getFarmatTime("HH:mm",date);
            if(data != null){
                MessageBean bean = new MessageBean();
                for (int i=0;i<data.size();i++){
                    String startDate = data.get(i).get_starttime();
                    long startDatel = DateTimeUtil.getFarmatTime("HH:mm",startDate);
                    String endDate = data.get(i).get_endtime();
                    long endDatel = DateTimeUtil.getFarmatTime("HH:mm",endDate);
                    if(datel+300 >= startDatel && datel<startDatel){
                        //更新主界面的课程信息
                        bean.setMsgCode(Catition.UPDATECLASS);
                        bean.setBean(data.get(i));
                        EventBus.getDefault().post(bean);
                    }else if(datel>= startDatel && datel <= endDatel){
                        //更新主界面的课程信息
                        bean.setMsgCode(Catition.UPDATECLASS);
                        bean.setBean(data.get(i));
                        EventBus.getDefault().post(bean);
                    }
                    if(endDatel == datel){
                        //每节课结束删除签到信息
                        bean.setMsgCode(Catition.DELETECARDID);
                        EventBus.getDefault().post(bean);
                    }
                }
            }
            handler.postDelayed(mRunable,60000);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
