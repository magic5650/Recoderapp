package com.magicrecoder.recoderapp;

/**
 * Created by Administrator on 2016-08-19.
 */

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/** 自定义适配器 */
//ViewHolder静态类
public class MyAdapter extends BaseAdapter {
    private static final String TAG = "Lifecycle";
    private String path = Environment.getExternalStorageDirectory() + "/record";
    private Context mContext;
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
    public long getItemId(int position){
        return position;
    }

    public void removeItem(int position) {
        String Filename= mData.get(position).getName() + ".amr";
        Log.d(TAG,"文件名为" + Filename + ",位置为" + position);
        if (removeAudioFile(Filename)) {
            mData.remove(position);
            this.notifyDataSetChanged();
        }
        else {
            Toast.makeText(mContext,"文件删除失败", Toast.LENGTH_LONG).show();
        }
    }
    public void changeItem(int position){
        String Filename= mData.get(position).getName() + ".amr";
        String newFilename= mData.get(position).getName() + ".amr";
        Recorder recorder=modifyAudioFile(Filename,newFilename);
        if(recorder != null) {
            mData.remove(position);
            mData.add(position, recorder);
            this.notifyDataSetChanged();
        }
    }
    private boolean removeAudioFile(String Filename) {
        File dir = new File(path);
        if (dir.exists()) {
            File AudioFile = new File(dir, Filename);
            return AudioFile.delete();
        }
        else {
            return false;
        }
    };
    private Recorder modifyAudioFile (String Filename,String newFilename) {
        File dir = new File(path);
        File AudioFile = new File(dir, Filename);
        File newAudioFile = new File(dir, newFilename);
        if( AudioFile.exists() ) {
            if (newAudioFile.exists()){
                Toast.makeText(mContext,"有重名文件", Toast.LENGTH_LONG).show();
                return null;
            }
            else {
                AudioFile.renameTo(newAudioFile);
                String fileName = newAudioFile.getName();
                String prefix = fileName.substring(0, fileName.lastIndexOf("."));
                Recorder recorder = new Recorder(prefix, GetFilePlayTime(newAudioFile), "录音1", "8月20号", R.drawable.ic_play_circle_filled_red_48dp);
                return recorder;
            }
        }
        else {
            return null;
        }
    };
    private String GetFilePlayTime(File file){
        java.util.Date date;
        SimpleDateFormat sy1;
        String dateFormat = "error";

        try {
            sy1 = new SimpleDateFormat("HH:mm:ss");//设置为时分秒的格式

            //使用媒体库获取播放时间
            MediaPlayer mediaPlayer;
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse(file.toString()));

            //使用Date格式化播放时间mediaPlayer.getDuration()
            date = sy1.parse("00:00:00");
            date.setTime(mediaPlayer.getDuration() + date.getTime());//用消除date.getTime()时区差
            dateFormat = sy1.format(date);

            mediaPlayer.release();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat;
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
        Recorder recorder = mData.get(position);
        //如果缓存convertView为空，则需要创建View
        if(convertView == null) {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.recorder_list_layout, null);
            //获取自定义布局中每一个控件的对象
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.often = (TextView) convertView.findViewById(R.id.often);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        //将数据一一添加到自定义的布局中
        holder.icon.setImageResource(recorder.getIcon());
        holder.name.setText(recorder.getName());
        holder.often.setText(recorder.getOften());
        return convertView;
    }
}

