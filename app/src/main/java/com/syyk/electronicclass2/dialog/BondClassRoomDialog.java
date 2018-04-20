package com.syyk.electronicclass2.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.adapter.BondClassAdapter;
import com.syyk.electronicclass2.bean.ClassRoomBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2018/1/25.
 */

public class BondClassRoomDialog extends Dialog {
    @BindView(R.id.itemBondClassRoom_dis_rv)
    RecyclerView itemBondClassRoom_dis_rv;

    private Context context;
    private BondClassAdapter bondClassAdapter;
    private List<ClassRoomBean> data = new ArrayList<>();


    public BondClassRoomDialog(Context context,List<ClassRoomBean> data) {
        super(context, R.style.dialog_custom); //dialog的样式
        this.context = context;//上下文
        this.data = data;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        setContentView(R.layout.item_bondclassroom);
        ButterKnife.bind(this);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失
        setCancelable(true);

        bondClassAdapter = new BondClassAdapter(context,data);
        itemBondClassRoom_dis_rv.setLayoutManager(new LinearLayoutManager(context));
        itemBondClassRoom_dis_rv.setAdapter(bondClassAdapter);

        bondClassAdapter.setOnItemClickListener(new BondClassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion) {
                if(mOnItemClickListener!= null){
                    mOnItemClickListener.onItemClick(postion);
                    cancel();
                }
            }
        });
    }


    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int postion);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

}
