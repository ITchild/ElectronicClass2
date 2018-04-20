package com.syyk.electronicclass2.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.dialog.backcall.GetCalendarDateCall;
import com.syyk.electronicclass2.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fei on 2018/4/8.
 */

public class CalendarDialog extends Dialog {

    private Context context;
    private String dateS;

    //名称的布局
    private MaterialCalendarView calendar_calendar_mcv;
    private Button calendar_ok_bt;
    private Button calendar_cancel_bt ;

    private GetCalendarDateCall getCalendarDateCall;

    public CalendarDialog(Context context) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;//上下文
    }

    public void setDateS(String dateS){
        this.dateS = dateS;
        setDate();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        setContentView(R.layout.dialog_calendar);
        //进行界面的调整
//        WindowManager windowManager = ((Activity) context).getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.width = display.getWidth()/2; // 设置dialog宽度为屏幕的4/5
//        lp.height = display.getWidth()*2/3;
//        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        setCancelable(true);

        calendar_calendar_mcv = (MaterialCalendarView) findViewById(R.id.calendar_calendar_mcv);
        calendar_ok_bt = (Button) findViewById(R.id.calendar_ok_bt);
        calendar_cancel_bt = (Button) findViewById(R.id.calendar_cancel_bt);

        calendar_calendar_mcv.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                dateS = date.getYear()+"-"+(date.getMonth()+1)+"-"+date.getDay();
            }
        });
        calendar_ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dateS == null){
                    StringUtils.showToast("请先选择日期");
                    return;
                }
                if(getCalendarDateCall != null) {
                    getCalendarDateCall.getDateString(dateS);
                }
                cancel();
            }
        });
        calendar_cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    private void setDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟
        try {
            Date date=sdf.parse(dateS);
            calendar_calendar_mcv.setCurrentDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setOnDateBack(GetCalendarDateCall getCalendarDateCall){
        this.getCalendarDateCall = getCalendarDateCall;
    }

}
