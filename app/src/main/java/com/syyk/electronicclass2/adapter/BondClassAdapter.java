package com.syyk.electronicclass2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.bean.ClassRoomBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2018/1/25.
 */

public class BondClassAdapter extends RecyclerView.Adapter<BondClassAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<ClassRoomBean> data = new ArrayList<>();
    private int size =0;

    public BondClassAdapter(Context context, List<ClassRoomBean> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setData(List<ClassRoomBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_bondclass, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.itemclass_name_tv.setText(data.get(position).get_classroomname());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount(){
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemclass_name_tv)
        TextView itemclass_name_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int postion);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}