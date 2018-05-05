package com.syyk.electronicclass2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.syyk.electronicclass2.R;
import com.syyk.electronicclass2.httpcon.NetCartion;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2018/1/2.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<String> data = new ArrayList<>();
    private int size = 0;

    public NoticeAdapter(Context context, List<String> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_notice, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (position == 0) {
            holder.itemnotice_image_iv.setVisibility(View.GONE);
            holder.itemnotice_con_tv.setVisibility(View.VISIBLE);
            holder.itemnotice_con_tv.setText(data.get(position));
            holder.itemnotice_con_tv.setTextSize(18 + (size * 3));
        } else {
            //搞事情
            holder.itemnotice_image_iv.setVisibility(View.VISIBLE);
            holder.itemnotice_con_tv.setVisibility(View.GONE);
            ImageLoader.getInstance().displayImage(NetCartion.hip + data.get(position), holder.itemnotice_image_iv);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemnotice_con_tv)
        TextView itemnotice_con_tv;
        @BindView(R.id.itemnotice_image_iv)
        ImageView itemnotice_image_iv;

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