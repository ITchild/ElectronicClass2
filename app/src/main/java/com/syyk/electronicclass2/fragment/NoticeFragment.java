package com.syyk.electronicclass2.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.IntroduceAdapter;
import com.syyk.electronicclass2.adapter.NoticeAdapter;
import com.syyk.electronicclass2.bean.CardIdBean;
import com.syyk.electronicclass2.bean.IntroAndNoticeBean;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
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

public class NoticeFragment extends Fragment {

    @BindView(R.id.notice_title_tv)
    TextView notice_title_tv;
    @BindView(R.id.notice_dis_rv)
    RecyclerView notice_dis_rv;
    @BindView(R.id.notice_load_pb)
    ProgressBar notice_load_pb;

    private NoticeAdapter noticeAdapter;
    private List<String> imageData = new ArrayList<>();

    private String isUpDate = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        noticeAdapter = new NoticeAdapter(getContext(),imageData);
        notice_dis_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        notice_dis_rv.setAdapter(noticeAdapter);

        //连接网络获取公告和介绍
//        Connection.getIntrAndNotice(NetCartion.GETINTROANDNOTICE_BACK);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void noticeEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.GETINTROANDNOTICE_BACK :
                    String josnString =  bean.getRes();
                    StringUtils.showLog(josnString);
                    IntroAndNoticeBean introAndNoticeBean = JSON.parseObject(josnString, IntroAndNoticeBean.class);
                    if(!isUpDate.equals(introAndNoticeBean.getDatetime())){
                        isUpDate = introAndNoticeBean.getDatetime();
                        imageData.clear();
                        noticeAdapter.setSize(introAndNoticeBean.getFontType());
                        addList(introAndNoticeBean.getNoticeMessage());
                        addList(introAndNoticeBean.getNoticeImg1());
                        addList(introAndNoticeBean.getNoticeImg2());
                        addList(introAndNoticeBean.getNoticeImg3());
                        noticeAdapter.setData(imageData);
                        notice_load_pb.setVisibility(View.GONE);
                        notice_dis_rv.setVisibility(View.VISIBLE);
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
