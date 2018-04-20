package com.syyk.electronicclass2.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.alibaba.fastjson.JSON;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.bean.CardIdBean;
import com.syyk.electronicclass2.bean.HomePagerBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.service.DownConfig;
import com.syyk.electronicclass2.service.DownService;
import com.syyk.electronicclass2.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2017/12/29.
 */

public class HomePagerFragment extends Fragment {

    @BindView(R.id.homepager_videoView)
    VideoView homepager_videoView;
    @BindView(R.id.homepager_pb)
    ProgressBar homepager_pb;

    //本地的视频 需要在手机SD卡根目录DownLoad文件夹下的home.MP4视频
    private String videoUrl1 = "" ;
    //网络视频
//    private String videoUrl2 = "http://ivi.bupt.edu.cn/hls/cctv6hd.m3u8";//CCTV6
    private String videoUrl2 = "http://192.168.1.198:9512/Video/Main.mp4";//温兆宇的接口
    private Uri uri;
    private Intent intent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent  = new Intent();
        //定义并赋值Intent
        intent.setClass(getActivity(),DownService.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_homepager,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homepager_pb.setVisibility(View.VISIBLE);
        homepager_videoView.setVisibility(View.GONE);

        videoUrl1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                +"/"+DownConfig.fileName;
        uri = Uri.parse( videoUrl1 );
        MediaController mc = new MediaController(getContext());
        mc.setVisibility(View.GONE);
        //设置视频控制器
        homepager_videoView.setMediaController(mc);
        //播放完成回调
        homepager_videoView.setOnCompletionListener( new MyPlayerOnCompletionListener());
        homepager_videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //截止点击事件的传递
                return true;
            }
        });

        Connection.getHomePager(NetCartion.GETHOMEPAGER_BACK);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homePagerEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.GETHOMEPAGER_BACK :
                    String josnString = bean.getRes();
                    StringUtils.showLog(josnString);
                    HomePagerBean homePagerBean = JSON.parseObject(josnString, HomePagerBean.class);
                    SharedPreferences preferences = getContext().getSharedPreferences("home",0);
                    if(!preferences.getString("time","").equals(homePagerBean.get_datetime())){
                        //存储时间
                        DownConfig.url = homePagerBean.get_mainpageurl();
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("time",homePagerBean.get_datetime());
                        editor.commit();
                        //启动下载
                        startDown();
                    }else{
                        File file = new File(videoUrl1);
                        if(file.exists()){
                            //直接播放
                            playVideo();
                        }else{
                            //不存在就下载
                            startDown();
                        }
                    }
                    break;
            }
        }else if(bean.getResCode() == NetCartion.FIAL){
            StringUtils.showToast(bean.getRes());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homePagerMsgEvent(MessageBean bean){
        switch (bean.getMsgCode()){
            case DownConfig.DOWNSUCCESS :
                //下载成功
                playVideo();
                getActivity().stopService(intent);
                break;
            case DownConfig.DOWNFIAL:
                //下载失败,重新下载
                getActivity().stopService(intent);
                startDown();
                break;
        }
    }

    /**
     * 开启下载
     */

    private void startDown(){
//        开启下载
        getActivity().startService(intent);
    }
    /**
     * 开始播放
     */
    private void playVideo(){
        homepager_pb.setVisibility(View.GONE);
        homepager_videoView.setVisibility(View.VISIBLE);
        //设置视频路径
        homepager_videoView.setVideoURI(uri);
        //开始播放视频
        homepager_videoView.start();
    }

    /**
     * 播放完成的回调
     */
    class MyPlayerOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            //重新开始播放
            playVideo();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().stopService(intent);
    }
}
