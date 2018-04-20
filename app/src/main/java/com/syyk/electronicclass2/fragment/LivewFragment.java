package com.syyk.electronicclass2.fragment;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.dahuatech.netsdk.common.DialogProgress;
import com.dahuatech.netsdk.common.PrefsConstants;
import com.dahuatech.netsdk.common.ToolKits;
import com.dahuatech.netsdk.module.IPLoginModule;
import com.dahuatech.netsdk.module.LivePreviewModule;
import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.utils.ComUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by fei on 2017/12/29.
 */

public class LivewFragment extends Fragment implements SurfaceHolder.Callback,
        AdapterView.OnItemSelectedListener{
    @BindView(R.id.video_realview_sv)
    SurfaceView video_realview_sv;
    @BindView(R.id.select_channel)
    Spinner select_channel;
    @BindView(R.id.select_stream_type)
    Spinner select_stream_type;

    private SharedPreferences mSharedPrefs;
    private IPLoginModule mLoginModule;
    private ElectronicApplication app;
    private DialogProgress mDialogProgress;
    Resources res;

    private String IP = "192.168.1.208";
    private String port = "37777";
    private String user = "admin";
    private String passWord = "admin";

    LivePreviewModule mLiveModule;
    private int count = 0;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /// get global data
        app = (ElectronicApplication) getActivity().getApplication();
        res = getResources();
        mDialogProgress = new DialogProgress(getContext());
        mLoginModule = new IPLoginModule();
        mSharedPrefs = getActivity().getPreferences(MODE_PRIVATE);

        LoginTask loginTask = new LoginTask();
        loginTask.execute();

        IP = ComUtils.getSave("vip");
        port = ComUtils.getSave("vport");
        user = ComUtils.getSave("vuser");
        passWord = ComUtils.getSave("vpass");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void liveEvent(HttpEventBean bean){

    }

    /// LoginTask
    private class LoginTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            mDialogProgress.setMessage(res.getString(R.string.logining));
            mDialogProgress.setSpinnerType(DialogProgress.FADED_ROUND_SPINNER);
            mDialogProgress.setCancelable(false);
            mDialogProgress.show();
        }
        @Override
        protected Boolean doInBackground(String... params) {
            return mLoginModule.login(IP, port, user, passWord);
        }
        @Override
        protected void onPostExecute(Boolean result){
            mDialogProgress.dismiss();
            if (result) {
                putSharePrefs();
                app.setLoginHandle(mLoginModule.getLoginHandle());
                app.setmDeviceInfo(mLoginModule.getDeviceInfo());
//                startActivity(new Intent(getContext(), LivePreviewActivity.class));
                play();
            } else {
                ToolKits.showMessage(getContext(), getErrorCode(getResources(), mLoginModule.errorCode()));
            }
        }
    }
    private void putSharePrefs() {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putString(PrefsConstants.LOGIN_IP, IP);
        editor.putString(PrefsConstants.LOGIN_PORT, port);
        editor.putString(PrefsConstants.LOGIN_USERNAME, user);
        editor.putString(PrefsConstants.LOGIN_PASSWORD, passWord);
        editor.putBoolean(PrefsConstants.LOGIN_CHECK, true);
        editor.apply();
    }

    private void play(){
        mLiveModule = new LivePreviewModule(getContext());
        video_realview_sv.getHolder().addCallback(this);
        initializeSpinner(select_channel,(ArrayList)mLiveModule.getChannelList()).setSelection(0);
        initializeSpinner(select_stream_type,(ArrayList)mLiveModule.getStreamTypeList(select_channel.getSelectedItemPosition())).setSelection(1);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mLiveModule.initSurfaceView(video_realview_sv);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int parentID = parent.getId();
        boolean isMain = isMainStream();

        ///Close the listener event when not triggered.
        ///关闭未触发时的监听事件.
        if((count == 0)&&(position == 0)) {
            count ++;
            return;
        }

        switch (parentID){
            case R.id.select_channel:
                onChannelChanged(position);
                break;
            case R.id.select_stream_type:
                onStreamTypeChanged(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void onChannelChanged(int pos){
        if (mLiveModule == null)
            return;
        mLiveModule.stopRealPlay();
        mLiveModule.startPlay(pos,select_stream_type.getSelectedItemPosition(),video_realview_sv);
    }

    private void onStreamTypeChanged(int position){
        if (mLiveModule == null)
            return;
        mLiveModule.stopRealPlay();
        mLiveModule.startPlay(select_channel.getSelectedItemPosition(),position,video_realview_sv);
    }

    private boolean isMainStream(){
        return select_stream_type.getSelectedItemPosition() == 0 ? true : false;
    }

    private Spinner initializeSpinner(final Spinner spinner, ArrayList array){
        spinner.setSelection(0,true);
        spinner.setOnItemSelectedListener(this);
        spinner.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_dropdown_item,array));
        return spinner;
    }

    public static String getErrorCode(Resources res, int errorCode) {
        switch(errorCode) {
            case IPLoginModule.NET_USER_FLASEPWD_TRYTIME:
                return res.getString(R.string.NET_USER_FLASEPWD_TRYTIME);
            case IPLoginModule.NET_LOGIN_ERROR_PASSWORD:
                return res.getString(R.string.NET_LOGIN_ERROR_PASSWORD);
            case IPLoginModule.NET_LOGIN_ERROR_USER:
                return res.getString(R.string.NET_LOGIN_ERROR_USER);
            case IPLoginModule.NET_LOGIN_ERROR_TIMEOUT:
                return res.getString(R.string.NET_LOGIN_ERROR_TIMEOUT);
            case IPLoginModule.NET_LOGIN_ERROR_RELOGGIN:
                return res.getString(R.string.NET_LOGIN_ERROR_RELOGGIN);
            case IPLoginModule.NET_LOGIN_ERROR_LOCKED:
                return res.getString(R.string.NET_LOGIN_ERROR_LOCKED);
            case IPLoginModule.NET_LOGIN_ERROR_BLACKLIST:
                return res.getString(R.string.NET_LOGIN_ERROR_BLACKLIST);
            case IPLoginModule.NET_LOGIN_ERROR_BUSY:
                return res.getString(R.string.NET_LOGIN_ERROR_BUSY);
            case IPLoginModule.NET_LOGIN_ERROR_CONNECT:
                return res.getString(R.string.NET_LOGIN_ERROR_CONNECT);
            case IPLoginModule.NET_LOGIN_ERROR_NETWORK:
                return res.getString(R.string.NET_LOGIN_ERROR_NETWORK);
            default:
                return res.getString(R.string.NET_ERROR);
        }
    }


    @Override
    public void onResume() {
        // while onResume we should logout the device.
        mLoginModule.logout();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if(null != mLoginModule) {
            mLoginModule.logout();
            mLoginModule = null;
        }
        EventBus.getDefault().unregister(this);
        if(mLiveModule != null) {
            //先判断是否为空
            mLiveModule.stopRealPlay();
        }
        mLiveModule = null;
        video_realview_sv = null;
        super.onDestroy();
    }
}
