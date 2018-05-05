package com.syyk.electronicclass2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;


import com.syyk.electronicclass2.ElectronicApplication;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.utils.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fei on 2017/12/5.
 */

public class MainSettingDialog extends Dialog {

    @BindView(R.id.mainSetting_Contorlp_et)
    EditText mainSetting_Contorlp_et;
    @BindView(R.id.mainSetting_ContorlPort_et)
    EditText mainSetting_ContorlPort_et;

    @BindView(R.id.mainSetting_HIp_et)
    EditText mainSetting_HIp_et;
    @BindView(R.id.mainSetting_HPort_et)
    EditText mainSetting_HPort_et;
    @BindView(R.id.mainSetting_VoideUser_et)
    EditText mainSetting_VoideUser_et;
    @BindView(R.id.mainSetting_VoidePass_et)
    EditText mainSetting_VoidePass_et;

    private SharedPreferences preferences;



    public MainSettingDialog(Context context) {
        super(context, R.style.dialog_custom); //dialog的样式
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        setContentView(R.layout.dialog_mainsetting);
        ButterKnife.bind(this);
        //进行界面的调整
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        setCancelable(true);

        preferences = ElectronicApplication.getmIntent().getSharedPreferences("Setting",0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(preferences == null){
            preferences = ElectronicApplication.getmIntent().getSharedPreferences("Setting",0);
        }
        mainSetting_Contorlp_et.setText(preferences.getString("ip","192.168.1.198"));
        mainSetting_ContorlPort_et.setText(preferences.getString("port","9000"));
        mainSetting_HIp_et.setText(preferences.getString("hIp","192.168.1.198"));
        mainSetting_HPort_et.setText(preferences.getString("hPort","1192"));
//        mainSetting_VoideUser_et.setText(preferences.getString("vUser","admin"));
//        mainSetting_VoidePass_et.setText(preferences.getString("vPass","admin"));
    }

    @OnClick({R.id.mainSetting_cancle_bt,R.id.mainSetting_ok_bt})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.mainSetting_cancle_bt ://设置的取消按钮
                cancel();
                break;
            case R.id.mainSetting_ok_bt ://设置的确认按钮
                save();
                cancel();
                break;
        }
    }

    private void save(){
        String ip = mainSetting_Contorlp_et.getText().toString();
        if(StringUtils.isEmpty(ip)){
            StringUtils.showToast("请输入IP地址");
            return;
        }
        String port = mainSetting_ContorlPort_et.getText().toString();
        if(StringUtils.isEmpty(port)){
            StringUtils.showToast("请输入端口号");
            return;
        }
        String hip = mainSetting_HIp_et.getText().toString();
        if(StringUtils.isEmpty(hip)){
            StringUtils.showToast("请输入后台的IP地址");
            return;
        }
        String hport = mainSetting_HPort_et.getText().toString();
        if(StringUtils.isEmpty(hport)){
            StringUtils.showToast("请输入后台的端口");
            return;
        }
//        String vuser = mainSetting_VoideUser_et.getText().toString();
//        if(StringUtils.isEmpty(vuser)){
//            StringUtils.showToast("请输入摄像机的用户名");
//            return;
//        }
//        String vpass = mainSetting_VoidePass_et.getText().toString();
//        if(StringUtils.isEmpty(vpass)){
//            StringUtils.showToast("请输入摄像机的用户密码");
//            return;
//        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ip",ip);
        editor.putString("port",port);
        editor.putString("hIp",hip);
        editor.putString("hPort",hport);
//        editor.putString("vUser",vuser);
//        editor.putString("vPass",vpass);
        editor.commit();
    }

}