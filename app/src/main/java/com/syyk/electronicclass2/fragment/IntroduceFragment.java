package com.syyk.electronicclass2.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.IntroduceAdapter;
import com.syyk.electronicclass2.adapter.NoticeAdapter;
import com.syyk.electronicclass2.bean.IntroAndNoticeBean;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
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

/**
 * Created by fei on 2017/12/29.
 */

public class IntroduceFragment extends Fragment {

    @BindView(R.id.introduce_title_tv)
    TextView introduce_title_tv;
    @BindView(R.id.introduce_dis_rv)
    RecyclerView introduce_dis_rv;

    @BindView(R.id.introduce_load_pb)
    ProgressBar introduce_load_pb;

    private IntroduceAdapter introduceAdapter;
    private List<String> imageData = new ArrayList<>();

    private String isUpDate ="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introduce,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        introduceAdapter = new IntroduceAdapter(getContext(),imageData);
        introduce_dis_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        introduce_dis_rv.setAdapter(introduceAdapter);
    }

    @Override
    public void onResume() {
        //连接网络获取介绍
        String mac = ComUtils.getSave("mac");
        if(null != mac){
            Connection.getIntroduce(mac,NetCartion.GETINTRODUCE_BACK);
        }
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void introduceEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.GETINTRODUCE_BACK :
                    String josnString =  bean.getRes();
                    String state = JsonUtils.getJsonKey(josnString,"Status");
                    if(state.equals("1")) {
                        josnString = JsonUtils.getJsonObject(josnString,"Model");
                        String introduce = JsonUtils.getJsonKey(josnString, "Introduce");
                        List<IntroAndNoticeBean> introAndNotices = JSON.parseArray(JsonUtils.getJsonArr(josnString, "Files")
                                .toString(), IntroAndNoticeBean.class);
                        ElectronicApplication.getmIntent().timeMulis = DateTimeUtil.
                                getCurFormat2Millis("MM/dd/yyyy HH:mm:ss", JsonUtils.getJsonKey(josnString, "Time"));
                        imageData.clear();
                        addList(introduce);
                        for(IntroAndNoticeBean bean1 : introAndNotices){
                            addList(bean1.getPath());
                        }
                        introduceAdapter.setData(imageData);
                        introduce_load_pb.setVisibility(View.GONE);
                        introduce_dis_rv.setVisibility(View.VISIBLE);
                    }else{
                        StringUtils.showToast(JsonUtils.getJsonKey(josnString,"Message"));
                    }
                    break;
            }
        }else if(bean.getResCode() == NetCartion.FIAL){
            StringUtils.showToast(bean.getRes());
        }
    }


    private void addList(String image){
        if(!StringUtils.isEmpty(image)){
            imageData.add(image);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
