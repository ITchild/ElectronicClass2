package com.syyk.electronicclass2.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.syyk.electronicclass2.R;

/**
 * Created by fei on 2018/5/10.
 */

public class EntrySettingDialog extends Dialog {

    private Context context;
    private String message;

    private Button entrySetting_cancle;

    public EntrySettingDialog(Context context) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;//上下文
//        this.message = message;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        setContentView(R.layout.dialog_entrysetting);
        //进行界面的调整
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()/3; // 设置dialog宽度为屏幕的4/5
        lp.height = display.getWidth()/3;
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        setCancelable(true);

        entrySetting_cancle = (Button) findViewById(R.id.entrySetting_cancle);
        entrySetting_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if(listener != null){
                    listener.cancle();
                }
            }
        });
    }

    private entrySettingCancleListener listener;

    public interface entrySettingCancleListener{
        void cancle();
    }

    public void setOnentrySettingCancleListener(entrySettingCancleListener listener){
        this.listener = listener;
    }

}
