package com.syyk.electronicclass2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.bean.AttenBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2018/1/18.
 */

public class AttenAdapter extends RecyclerView.Adapter<AttenAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<AttenBean> data = new ArrayList<>();
    private int size =0;

    public AttenAdapter(Context context, List<AttenBean> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setData(List<AttenBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_atten, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(position == 0){
            holder.itemAtten_first_v.setVisibility(View.VISIBLE);
            holder.itemAtten_num_tv.setText("时间");
            holder.itemAtten_name_tv.setText("课程");
            holder.itemAtten_Time_tv.setText("项目");
            holder.itemAtten_endTime_tv.setText("班级");
            holder.itemAtten_teacherName_tv.setText("应到人数");
            holder.itemAtten_stuNum_tv.setText("实到人数");
            holder.itemAtten_stuYNum_tv.setText("迟到人数");
        }else{
            holder.itemAtten_num_tv.setText("第"+position+"节课");
            holder.itemAtten_name_tv.setText("无");
            holder.itemAtten_Time_tv.setText("无");
            holder.itemAtten_endTime_tv.setText("无");
            holder.itemAtten_teacherName_tv.setText("无");
            holder.itemAtten_stuNum_tv.setText("无");
            holder.itemAtten_stuYNum_tv.setText("无");
        }

    }

    @Override
    public int getItemCount(){
        return 9;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemAtten_first_v)
        View itemAtten_first_v;
        @BindView(R.id.itemAtten_num_tv)
        TextView itemAtten_num_tv;
        @BindView(R.id.itemAtten_name_tv)
        TextView itemAtten_name_tv;
        @BindView(R.id.itemAtten_Time_tv)
        TextView itemAtten_Time_tv;
        @BindView(R.id.itemAtten_endTime_tv)
        TextView itemAtten_endTime_tv;
        @BindView(R.id.itemAtten_teacherName_tv)
        TextView itemAtten_teacherName_tv;
        @BindView(R.id.itemAtten_stuNum_tv)
        TextView itemAtten_stuNum_tv;
        @BindView(R.id.itemAtten_stuYNum_tv)
        TextView itemAtten_stuYNum_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int checkedId, int postion);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
