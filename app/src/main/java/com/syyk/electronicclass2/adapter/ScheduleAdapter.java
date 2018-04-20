package com.syyk.electronicclass2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.bean.AttenBean;
import com.syyk.electronicclass2.bean.ScheduleBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2018/1/25.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<ScheduleBean> data = new ArrayList<>();
    private int size =0;

    public ScheduleAdapter(Context context, List<ScheduleBean> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setData(List<ScheduleBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_schedule, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if(position == 0) {
            holder.itemSchedule_first_v.setVisibility(View.VISIBLE);
            holder.itemSchedule_num_tv.setText("时间");
            holder.itemSchedule_name_tv.setText("课程名称");
            holder.itemSchedule_Time_tv.setText("项目");
            holder.itemSchedule_teacherName_tv.setText("教师名字");
            holder.itemSchedule_stuName_tv.setText("上课班级");
        }else{
            holder.itemSchedule_num_tv.setText("第"+position+"节课");
            if(data != null) {
                for (int i = 0; i < data.size(); i++) {
                    ScheduleBean bean = data.get(i);
                    if ((position + "").equals(bean.get_number())) {
                        holder.itemSchedule_name_tv.setText(bean.getCourse());
                        holder.itemSchedule_Time_tv.setText(bean.get_starttime()+"-"+bean.get_endtime());
                        holder.itemSchedule_teacherName_tv.setText(bean.getTeacherName());
                        holder.itemSchedule_stuName_tv.setText(bean.get_c_id());
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount(){
        //每天都是8节课，+1为课表的表头
        return 9;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.itemSchedule_first_v)
        View itemSchedule_first_v;
        @BindView(R.id.itemSchedule_num_tv)
        TextView itemSchedule_num_tv;
        @BindView(R.id.itemSchedule_name_tv)
        TextView itemSchedule_name_tv;
        @BindView(R.id.itemSchedule_Time_tv)
        TextView itemSchedule_Time_tv;
        @BindView(R.id.itemSchedule_teacherName_tv)
        TextView itemSchedule_teacherName_tv;
        @BindView(R.id.itemSchedule_stuName_tv)
        TextView itemSchedule_stuName_tv;

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