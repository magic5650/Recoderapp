package com.magicrecoder.recoderapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RecorderActivity extends AppCompatActivity {

    private static final String TAG = "Lifecycle";
    //定义数据
    private List<Recorder> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
/*            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);*/
            //透明状态栏
            //setTranslucentStatus(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintResource(R.color.accent_material_dark);
            tintManager.setStatusBarTintEnabled(true);
/*            View view = findViewById(R.id.status_bar_holder);
            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) view.getLayoutParams();
            params.height = CommonUtils.getStatusbarHeight(getBaseContext());
            view.setLayoutParams(params);
            view.setVisibility(View.VISIBLE);*/
            //透明导航栏
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.TRANSPARENT);
            Context context = getApplicationContext();
            int color = ContextCompat.getColor(context,R.color.accent_material_dark);
            window.setStatusBarColor(color);
        }
        //添加Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toolbar监听
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(onMenuItemClick);
        }
        Log.d(TAG, "MainActivity onCreate 创建，执行");
    }
    @Override
    public void onStart() {
        super.onStart();
        //为ListView对象赋值
        final ListView mListViewArray = (ListView) findViewById(R.id.recorder_ListView);
        if (mListViewArray != null) {
            mListViewArray.post(new Runnable() {
                @Override
                public void run() {
                    int list_height = mListViewArray.getMeasuredHeight();
                    ImageView icon = (ImageView) findViewById(R.id.icon);
                    if (icon != null) {
/*                        ViewGroup.LayoutParams icon_height = icon.getLayoutParams();
                        icon_height.height = list_height / 3;
                        icon.setLayoutParams(icon_height);*/
                    }
                }
            });
            LayoutInflater inflater = getLayoutInflater();
            //初始化数据
            initData();
            //创建自定义Adapter的对象
            MyAdapter adapter = new MyAdapter(inflater, mData);
            //将布局添加到ListView中
            mListViewArray.setAdapter(adapter);
        }
        Log.d(TAG, "MainActivity onStart 可见 执行");
    }
    /*初始化数据*/
    private void initData() {
        mData = new ArrayList <>();
        Recorder recorder_one  = new Recorder("20160820", "30s", "录音1", "8月20号",R.mipmap.appicon );
        Recorder recorder_two  = new Recorder("20160720", "01:30", "录音2", "7月20号",R.mipmap.appicon );
        Recorder recorder_three  = new Recorder("20160620", "11:01", "录音3", "6月20号",R.mipmap.appicon );
        mData.add(recorder_one);
        mData.add(recorder_two);
        mData.add(recorder_three);
    }
    //获取状态栏的高度@return
    private int getStatusBarHeight(){
        try
        {
            Class<?> c=Class.forName("com.android.internal.R$dimen");
            Object obj=c.newInstance();
            Field field=c.getField("status_bar_height");
            int x=Integer.parseInt(field.get(obj).toString());
            return  getResources().getDimensionPixelSize(x);
        }catch(Exception e){
            e.printStackTrace();
        }
        return 0;
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
