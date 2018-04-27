package com.syyk.electronicclass2.fragment;

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
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.bean.ClassRoomBean;
import com.syyk.electronicclass2.bean.IntroAndNoticeBean;
import com.syyk.electronicclass2.bean.MessageBean;
import com.syyk.electronicclass2.dialog.BondClassRoomDialog;
import com.syyk.electronicclass2.dialog.MainSettingDialog;
import com.syyk.electronicclass2.httpcon.Connection;
import com.syyk.electronicclass2.httpcon.HttpEventBean;
import com.syyk.electronicclass2.httpcon.NetCartion;
import com.syyk.electronicclass2.utils.Catition;
import com.syyk.electronicclass2.utils.ComUtils;
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

public class SettingFragmnet extends Fragment {
    @BindView(R.id.setting_change_sb)
    SeekBar setting_change_sb;
    @BindView(R.id.setting_change_tv)
    TextView setting_change_tv;

    @BindView(R.id.setting_appName_tv)
    TextView setting_appName_tv;
    @BindView(R.id.setting_version_tv)
    TextView setting_version_tv;
    @BindView(R.id.setting_Copyright_tv)
    TextView setting_Copyright_tv;

    private MainSettingDialog mainSettingDialog;

    private BondClassRoomDialog bondClassRoomDialog;
    private List<ClassRoomBean> classData = new ArrayList<>();

    UpdateManger updateManger ;

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
        setting_change_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setting_change_tv.setText(progress+"");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String light = setting_change_tv.getText().toString();
                if(!StringUtils.isEmpty(light)) {
                    String hexlight = Integer.toHexString(Integer.parseInt(light));
                    if(hexlight.length() == 1){
                        hexlight = "0"+hexlight;
                    }
                    StringUtils.showLog(hexlight);
                    MessageBean bean = new MessageBean();
                    bean.setMsgCode(Catition.SETTINGLIANGDU);
                    bean.setMsgs(hexlight);
                    EventBus.getDefault().post(bean);
                }else{

                }
            }
        });
        setting_appName_tv.setText("软件名称："+getContext().getResources().getString(R.string.app_name));
        setting_version_tv.setText("版本号："+ComUtils.getLocalVersionName(getActivity()));
        setting_Copyright_tv.setText(getContext().getResources().getString(R.string.copyRight));

        updateManger = new UpdateManger(getContext(),"http://172.26.106.1/ws.yingyonghui.com/34932c8a2438008b7024de8b64dd9d35/5ae2a3bf/apk/5789178/a05a158aa2eb484afe20f779c2edc45d");
    }

    @OnClick({R.id.setting_ip_bt,R.id.setting_ClassRoom_bt,R.id.setting_UpDate_bt})
    public void settingOnClick(View view){
        switch (view.getId()){
            case R.id.setting_ip_bt :
                mainSettingDialog.show();
                break;
            case R.id.setting_ClassRoom_bt ://绑定教室
                //先获取教室的列表
                Connection.getClassRoom(NetCartion.GETCLASSROOM_BACK);
                break;
            case R.id.setting_UpDate_bt ://检查软甲更新
                updateManger.showNoticeDialog();
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
                    final String classRes = bean.getRes();
                    classData = JSON.parseArray(classRes,ClassRoomBean.class);
                    if(classData != null){
                        bondClassRoomDialog = new BondClassRoomDialog(getContext(),classData);
                        bondClassRoomDialog.show();
                        bondClassRoomDialog.setOnItemClickListener(new BondClassRoomDialog.OnItemClickListener() {
                            @Override
                            public void onItemClick(int postion) {
                                //进行教室的绑定
                                Connection.bondClassRoom(ComUtils.getMac(),
                                        classData.get(postion).get_id()+"",NetCartion.BONDCLASSROOM_BACK);
                            }
                        });
                    }
                    break;
                case NetCartion.BONDCLASSROOM_BACK:
                    String bondString = bean.getRes();
                    if(bondString != null && bondString.equals("Success!")){
                        StringUtils.showCenterToast("绑定教室成功");
                    }else{
                        StringUtils.showCenterToast("绑定教室失败");
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
