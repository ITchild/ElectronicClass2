package com.syyk.electronicclass2.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.bean.ClassRoomBean;
import com.syyk.electronicclass2.bean.IntroAndNoticeBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.bean.VerSionCodeBean;
import com.syyk.electronicclass2.dialog.BondClassRoomDialog;
import com.syyk.electronicclass2.dialog.LoadingDialog;
import com.syyk.electronicclass2.dialog.MainSettingDialog;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.ConnectionClient;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.ui.LongTouchBtn;
import com.syyk.electronicclass2.utils.Catition;
import com.syyk.electronicclass2.utils.ComUtils;
import com.syyk.electronicclass2.utils.JsonUtils;
import com.syyk.electronicclass2.utils.StringUtils;
import com.syyk.electronicclass2.utils.UpdateManger;

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

public class SettingFragmnet extends Fragment{
//    @BindView(R.id.setting_change_sb)
//    SeekBar setting_change_sb;
//    @BindView(R.id.setting_change_tv)
//    TextView setting_change_tv;

    @BindView(R.id.setting_appName_tv)
    TextView setting_appName_tv;
    @BindView(R.id.setting_version_tv)
    TextView setting_version_tv;
    @BindView(R.id.setting_Copyright_tv)
    TextView setting_Copyright_tv;

    @BindView(R.id.setting_menu_bt)
    LongTouchBtn setting_menu_bt;
    @BindView(R.id.setting_up_bt)
    LongTouchBtn setting_up_bt;

    @BindView(R.id.setting_Ok_bt)
    LongTouchBtn setting_Ok_bt;
    @BindView(R.id.setting_down_bt)
    LongTouchBtn setting_down_bt;


    private MainSettingDialog mainSettingDialog;

    private BondClassRoomDialog bondClassRoomDialog;
    private List<ClassRoomBean> classData = new ArrayList<>();

    UpdateManger updateManger ;

    private String macAddr ;

    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        ButterKnife.bind(this,view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainSettingDialog = new MainSettingDialog(getContext());
//        setting_change_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                setting_change_tv.setText(progress+"");
//            }
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                String light = setting_change_tv.getText().toString();
//                if(!StringUtils.isEmpty(light)) {
//                    String hexlight = Integer.toHexString(Integer.parseInt(light));
//                    if(hexlight.length() == 1){
//                        hexlight = "0"+hexlight;
//                    }
//                    StringUtils.showLog(hexlight);
//                    MessageBean bean = new MessageBean();
//                    bean.setMsgCode(Catition.SETTINGLIANGDU);
//                    bean.setMsgs(hexlight);
//                    EventBus.getDefault().post(bean);
//                }else{
//
//                }
//            }
//        });
        setting_appName_tv.setText("软件名称："+getContext().getResources().getString(R.string.app_name));
        setting_version_tv.setText("版本号："+ComUtils.getLocalVersionName(getActivity()));
        setting_Copyright_tv.setText(getContext().getResources().getString(R.string.copyRight));

        loadingDialog = new LoadingDialog(getContext(),"请稍等...");

        initListener();
    }

    private void initListener(){
        //    菜单/确认
        setting_menu_bt.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
            @Override
            public void onLongTouch() {
                MessageBean bean = new MessageBean();
                bean.setMsgCode(Catition.MENUSETTING);
                bean.setMsgs("07A0010105AEFF");
                EventBus.getDefault().post(bean);
            }
            @Override
            public void onLongUp(boolean isLong) {
                MessageBean bean = new MessageBean();
                if(isLong){
                    bean.setMsgCode(Catition.MENUSETTING);
                    bean.setMsgs("07A0010109B2FF");
                    EventBus.getDefault().post(bean);
                }else{//点击事件
                    bean.setMsgCode(Catition.MENUSETTING);
                    bean.setMsgs("07A0010101AAFF");
                    EventBus.getDefault().post(bean);
                }
            }
        });
        //上翻
        setting_up_bt.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
            @Override
            public void onLongTouch() {
                MessageBean bean = new MessageBean();
                bean.setMsgCode(Catition.UPSETTING);
                bean.setMsgs("07A0010107B0FF");
                EventBus.getDefault().post(bean);
            }

            @Override
            public void onLongUp(boolean isLong) {
                MessageBean bean = new MessageBean();
                if(isLong){
                    bean.setMsgCode(Catition.UPSETTING);
                    bean.setMsgs("07A001010BB4FF");
                    EventBus.getDefault().post(bean);
                }else{//点击事件
                    bean.setMsgCode(Catition.UPSETTING);
                    bean.setMsgs("07A0010103ACFF");
                    EventBus.getDefault().post(bean);
                }
            }
        });
        //下翻
        setting_down_bt.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
            @Override
            public void onLongTouch() {
                MessageBean bean = new MessageBean();
                bean.setMsgCode(Catition.DOWNSETTING);
                bean.setMsgs("07A0010108B1FF");
                EventBus.getDefault().post(bean);
            }

            @Override
            public void onLongUp(boolean isLong) {
                MessageBean bean = new MessageBean();
                if(isLong){
                    bean.setMsgCode(Catition.DOWNSETTING);
                    bean.setMsgs("07A001010CB5FF");
                    EventBus.getDefault().post(bean);
                }else{//点击事件
                    bean.setMsgCode(Catition.DOWNSETTING);
                    bean.setMsgs("07A0010104ADFF");
                    EventBus.getDefault().post(bean);
                }
            }
        });
        //返回
        setting_Ok_bt.setOnLongTouchListener(new LongTouchBtn.LongTouchListener() {
            @Override
            public void onLongTouch() {
                MessageBean bean = new MessageBean();
                bean.setMsgCode(Catition.OKSETTING);
                bean.setMsgs("07A0010106AFFF");
                EventBus.getDefault().post(bean);
            }

            @Override
            public void onLongUp(boolean isLong) {
                MessageBean bean = new MessageBean();
                if(isLong){
                    bean.setMsgCode(Catition.OKSETTING);
                    bean.setMsgs("07A001010AB3FF");
                    EventBus.getDefault().post(bean);
                }else{//点击事件
                    bean.setMsgCode(Catition.OKSETTING);
                    bean.setMsgs("07A0010102ABFF");
                    EventBus.getDefault().post(bean);
                }
            }
        });
    }

    @OnClick({R.id.setting_ip_bt,R.id.setting_ClassRoom_bt,R.id.setting_UpDate_bt,R.id.setting_exit_bt})
    public void settingOnClick(View view){
        switch (view.getId()){
            case R.id.setting_ip_bt :
                mainSettingDialog.show();
                break;
            case R.id.setting_ClassRoom_bt ://绑定教室
                //先获取教室的列表
                Connection.getClassRoom(NetCartion.GETCLASSROOM_BACK);
                loadingDialog.show();
                break;
            case R.id.setting_UpDate_bt ://检查软甲更新
                Connection.checkVisionCode(NetCartion.CHECKVISIONCODE_BACK);
                loadingDialog.show();
                break;
            case R.id.setting_exit_bt :
                System.exit(0);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void settingEvent(MessageBean bean) {
        switch (bean.getMsgCode()) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void settingHttpEvent(HttpEventBean bean){
        if(bean.getResCode() == NetCartion.SUCCESS){
            switch(bean.getBackCode()){
                case NetCartion.GETCLASSROOM_BACK :
                    loadingDialog.cancel();
                    final String classRes = bean.getRes();
                    classData = JSON.parseArray(JsonUtils.getJsonArr(classRes,"Model").toString(),ClassRoomBean.class);
                    if(classData != null){
                        bondClassRoomDialog = new BondClassRoomDialog(getContext(),classData);
                        bondClassRoomDialog.show();
                        bondClassRoomDialog.setOnItemClickListener(new BondClassRoomDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int postion) {
                                //进行教室的绑定
                                macAddr = ComUtils.getMac();
                                loadingDialog.show();
                                Connection.bondClassRoom(macAddr,classData.get(postion).getId()+"",NetCartion.BONDCLASSROOM_BACK);
                            }
                        });
                    }
                    break;
                case NetCartion.BONDCLASSROOM_BACK:
                    loadingDialog.cancel();
                    String bondString = bean.getRes();
                    String state = JsonUtils.getJsonKey(bondString,"Status");
                    if(state.equals("1")){
                        StringUtils.showCenterToast("绑定教室成功");
                        SharedPreferences preferences = ElectronicApplication.getmIntent().getSharedPreferences("Setting",0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("mac",macAddr);
                        editor.commit();
                        //绑定教室成功，发送消息
                        MessageBean messageBean = new MessageBean();
                        messageBean.setMsgCode(Catition.BINGCLASSROOM_SUCCESS);
                        EventBus.getDefault().post(messageBean);
                    }else{
                        StringUtils.showCenterToast("绑定教室失败");
                    }
                    break;
                case NetCartion.CHECKVISIONCODE_BACK :
                    loadingDialog.cancel();
                    String jsonString = bean.getRes();
                    String checkState = JsonUtils.getJsonKey(jsonString,"Status");
                    if(checkState.equals("1")){
                        VerSionCodeBean bean1 = JSON.parseObject(JsonUtils.getJsonObject(jsonString,"Model")
                                ,VerSionCodeBean.class);
                        if(Integer.parseInt(bean1.getVersion()) > ComUtils.getLocalVersionCode(getActivity())){
                            updateManger = new UpdateManger(getContext(),NetCartion.hip+bean1.getPath());
                            updateManger.showNoticeDialog();
                        }else{
                            StringUtils.showCenterToast("安装包已经是最新了");
                        }
                    }else{
                        StringUtils.showToast(JsonUtils.getJsonKey(jsonString,"Message"));
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
