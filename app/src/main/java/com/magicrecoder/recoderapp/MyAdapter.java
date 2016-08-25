package com.magicrecoder.recoderapp;

/**
 * Created by Administrator on 2016-08-19.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

/** 自定义适配器 */
//ViewHolder静态类
public class MyAdapter extends BaseAdapter {
    private List<Recorder> mData;//定义数据
    private LayoutInflater mInflater;//定义Inflater,加载自定义布局
    public MyAdapter(LayoutInflater inflater,List<Recorder> data){
        mInflater = inflater;
        mData = data;
    }
    @Override
    public int getCount(){
        return mData.size();
    }
    @Override
    public  Object getItem(int position){
        return position;
    }
    @Override
    public  long getItemId(int position){
        return position;
    }
    //在外面先定义，ViewHolder静态类
    static class ViewHolder
    {
        public ImageView icon;
        public TextView name;
        public TextView often;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup){
        ViewHolder holder = null;
        //如果缓存convertView为空，则需要创建View
        if(convertView == null) {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.recorder_list_layout, null);
            //获取自定义布局中每一个控件的对象
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.often = (TextView) convertView.findViewById(R.id.often);
/*            ImageView icon_view = (ImageView) convertView.findViewById(R.id.icon);
            ViewGroup.LayoutParams icon_height = icon_view.getLayoutParams();
            icon_height.height = 250;
            holder.icon.setLayoutParams(icon_height);*/
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        Recorder recorder = mData.get(position);
        //将数据一一添加到自定义的布局中
        holder.icon.setImageResource(recorder.getIcon());
        holder.name.setText(recorder.getName());
        holder.often.setText(recorder.getOften());
        return convertView;
    }
}

