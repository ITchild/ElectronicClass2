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
import com.syyk.electronicclass2.database.RCDBHelper;
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

public class AttendanceFragmnet extends Fragment{
    @BindView(R.id.attendance_date_tv)
    TextView attendance_date_tv;
    @BindView(R.id.attendance_week_tv)
    TextView attendance_week_tv;

    @BindView(R.id.attendance_rc)
    RecyclerView attendance_rc;

    private RCDBHelper db ;
    private List<AttenBean> attenDatas = new ArrayList<>();
    private List<AttenBean> postDatas = new ArrayList<>();

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
        initData();

        attendance_date_tv.setText("2016年01月01日");
        attendance_week_tv.setText("周六");

        calendarDialog.setOnDateBack(new GetCalendarDateCall() {
            @Override
            public void getDateString(String dateString) {
                dataS = dateString;
                StringUtils.showLog(dataS);
            }
        });
    }

    private void initData(){
        attenDatas = db.getAllCardId();
        attenAdapter.setData(attenDatas);
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
                //定期删除本地签到信息
                db.deleteAllCardId();
                initData();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AttenEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.POSTCARDIDMSG_BACK :
                    //签到结果的返回，主要用于更新数据库和签到界面并通知主界面
                    String data = bean.getRes();
                    StringUtils.showLog(data);
                    if(data.contains("卡号不存在")){
                        StringUtils.showCenterToast(data);
                        return;
                    }
                    AttenBean attenBean = JSON.parseObject(data,AttenBean.class);
                    if(attenBean != null) {
                        db.saveCardId(attenBean);
                        StringUtils.showCenterToast("签到成功！！！");
                        initData();
                        MessageBean msgBean = new MessageBean();
                        msgBean.setMsgCode(Catition.UPDATECARDID);
                        msgBean.setMsgs(attenBean.getName());
                        msgBean.setMsgi(attenDatas.size());
                        EventBus.getDefault().post(msgBean);
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
