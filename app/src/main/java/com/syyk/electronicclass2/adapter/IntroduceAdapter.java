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
import com.syyk.electronicclass2.httpcon.NetCartion;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by fei on 2018/1/3.
 */

public class IntroduceAdapter extends RecyclerView.Adapter<IntroduceAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<String> data = new ArrayList<>();
    private int size =0;
    private Context mContext;

    public IntroduceAdapter(Context context, List<String> data) {
        mContext = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void setSize(int size){
        this.size = size;
    }

    public void setData(List<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_introduce, parent, false);
        return new IntroduceAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
            if (position == 0) {
                holder.itemintroduce_image_iv.setVisibility(View.GONE);
                holder.itemintroduce_con_tv.setVisibility(View.VISIBLE);
                holder.itemintroduce_con_tv.setText(data.get(position));
                holder.itemintroduce_con_tv.setTextSize(18 + (size * 3));
            } else {
                //搞事情
                holder.itemintroduce_image_iv.setVisibility(View.VISIBLE);
                holder.itemintroduce_con_tv.setVisibility(View.GONE);
                ImageLoader.getInstance().displayImage(NetCartion.hip + data.get(position), holder.itemintroduce_image_iv);
            }
    }

    @Override
    public int getItemCount(){
        return data.size();
//        if(data.size() == 0){
//            return data.size();
//        }else {
//            return Integer.MAX_VALUE;
//        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemintroduce_con_tv)
        TextView itemintroduce_con_tv;
        @BindView(R.id.itemintroduce_image_iv)
        ImageView itemintroduce_image_iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private NoticeAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int checkedId, int postion);

    }

    public void setOnItemClickListener(NoticeAdapter.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
}
