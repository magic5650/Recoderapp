package com.magicrecoder.recoderapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecorderActivity extends AppCompatActivity {

    private static final String TAG = "Lifecycle";
    //定义数据
    private List<Recorder> mData;
    //定义ListView对象
    private ListView mListViewArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        setContentView(R.layout.activity_recorder);
        //为ListView对象赋值
        mListViewArray = (ListView) findViewById(R.id.recorder_ListView);
        LayoutInflater inflater = getLayoutInflater();
        //初始化数据
        initData();
        //创建自定义Adapter的对象
        MyAdapter adapter =new MyAdapter(inflater,mData);
        //将布局添加到ListView中
        mListViewArray.setAdapter(adapter);
        //添加Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toolbar监听
        toolbar.setOnMenuItemClickListener(onMenuItemClick);
        Log.d(TAG, "MainActivity onCreate 创建，执行");
    }
    /*
    初始化数据
     */
    private void initData() {
        mData = new ArrayList <>();
        Recorder recorder_one  = new Recorder("20160820", "30s", "录音1", "8月20号",R.mipmap.appicon );
        Recorder recorder_two  = new Recorder("20160720", "01:30", "录音2", "7月20号",R.mipmap.appicon );
        Recorder recorder_three  = new Recorder("20160620", "11:01", "录音3", "6月20号",R.mipmap.appicon );
        mData.add(recorder_one);
        mData.add(recorder_two);
        mData.add(recorder_three);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity onStart 可见 执行");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume 获取焦点 执行");

        final ImageView begin_record = (ImageView) findViewById(R.id.begin_record);
        final ImageView save_record = (ImageView) findViewById(R.id.save_record);
        assert begin_record != null;
        assert save_record != null;
        begin_record.setOnClickListener(new ImageView.OnClickListener() {;
            @Override
            public void onClick(View arg0) {
                Integer  integer  = (Integer ) begin_record.getTag();
                integer = integer == null ? 0 : integer;
                switch (integer){
                    case R.mipmap.ic_toggle_radio_button_on:
                    default:
                        begin_record.setImageResource(R.mipmap.ic_av_pause_circle_fill);
                        begin_record.setTag(R.mipmap.ic_av_pause_circle_fill);
                        save_record.setImageResource(0);
                        Log.d(TAG,"开始录音" + integer);
                        break;
                    case R.mipmap.ic_av_pause_circle_fill:
                        begin_record.setImageResource(R.mipmap.ic_toggle_radio_button_on);
                        begin_record.setTag(R.mipmap.ic_toggle_radio_button_on);
                        save_record.setImageResource(R.drawable.ic_save_nactive_24dp);
                        Log.d(TAG,"暂停录音" + integer);
                        break;
                }
            }
        });
        save_record.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(save_record.getDrawable() == null){
                    Log.d(TAG, "保存图片为空");
                }
                else {
                    Log.d(TAG, "点击保存录音");
                    begin_record.setImageResource(R.mipmap.ic_toggle_radio_button_on);
                    save_record.setImageResource(0);
                }
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity onPause 失去焦点 执行");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop 不可见 执行");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity onDestroy 销毁 执行");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "MainActivity onRestart 重新打开 执行");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "MainActivity onSaveInstanceState 保存数据");
    }

    @Override
    public void onRestoreInstanceState(Bundle saveInstanceState) {
        super.onRestoreInstanceState(saveInstanceState);
        Log.d(TAG, "MainActivity onRestoreInstanceState 保存数据");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener(){
        @Override
        public boolean onMenuItemClick(MenuItem menuitem) {
            switch (menuitem.getItemId()){
                case R.id.more_action:
                    Log.d(TAG,"点击菜单");
                    break;
                case R.id.search_bar:
                    Log.d(TAG,"搜索");
                    break;
            }
            return true;
        }
    };
}
