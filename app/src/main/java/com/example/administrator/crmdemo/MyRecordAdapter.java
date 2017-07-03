package com.example.administrator.crmdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */

public class MyRecordAdapter extends RecyclerView.Adapter<MyRecordAdapter.MyViewHolder> {
    private Context mContext;
    private List<RecordEntity> datas;//数据

    //自定义监听事件
    /*public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view);
        void onItemLongClick(View view);
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }*/

    public MyRecordAdapter(Context mContext, List<RecordEntity> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public MyRecordAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle_main,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyRecordAdapter.MyViewHolder holder, int position) {
        String a = datas.get(position).getTime();
        //String[] b = a.split(" ");
        a = a.replace("2017-","");
        //holder.tvTime.setText(b[0]+"\n"+b[1]);
        holder.tvTime.setText(a.trim());
        holder.tvFormat.setText("    "+datas.get(position).getFormat());
        holder.tvDuration.setText(datas.get(position).getDuration()+"秒");
        if (datas.get(position).isUpload()){
            holder.tvUpload.setText("已上传");
        }else {
            holder.tvUpload.setText("未上传");
            holder.tvUpload.setTextColor(Color.parseColor("#1f8bcc"));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /*@Override
    public void onClick(View v) {

    }*/
    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTime;
        private TextView tvFormat;
        private TextView tvDuration;
        private TextView tvUpload;
        public MyViewHolder(View view){
            super(view);
            //分辨率匹配
            AutoUtils.autoSize(view);
            tvTime = (TextView) view.findViewById(R.id.tv_item_time);
            tvFormat = (TextView) view.findViewById(R.id.tv_item_format);
            tvDuration = (TextView) view.findViewById(R.id.tv_item_duration);
            tvUpload = (TextView) view.findViewById(R.id.tv_item_upload);
        }
    }
}
