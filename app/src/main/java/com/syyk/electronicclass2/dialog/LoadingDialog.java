package com.syyk.electronicclass2.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.syyk.electronicclass2.R;


/**
 * Created by fei on 2017/5/10.
 */

public class LoadingDialog extends Dialog {

    private Context context;
    private String message;

    //名称的布局
    private TextView msg_tv;

    public LoadingDialog(Context context, String message) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;//上下文
        this.message = message;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        setContentView(R.layout.item_loading);
        //进行界面的调整
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()/5; // 设置dialog宽度为屏幕的4/5
        lp.height = display.getWidth()/5;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        setCancelable(true);

        msg_tv = (TextView) findViewById(R.id.loding_msg);
        msg_tv.setText(message);

    }
}
