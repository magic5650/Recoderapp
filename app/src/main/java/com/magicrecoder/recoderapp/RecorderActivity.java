package com.magicrecoder.recoderapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FilenameFilter;
import java.util.Locale;


public class RecorderActivity extends AppCompatActivity {

    private static final String TAG = "Lifecycle";
    //定义数据
    private List<Recorder> mData;//定义列表数据
    private MediaRecorder mMediaRecorder;// MediaRecorder对象
    private String mRecAudioFile;//录音文件
    private File mRecAudioPath;//录音路径
    private MyAdapter adapter;//定义适配器
    private ListView mListViewArray;//定义Listview
    private Chronometer chronometer;//定义计时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        mRecAudioPath = getRecordDir();
        init_list();
        setStatusColor();
        //长按菜单弹出操作，注册列表
        registerForContextMenu(mListViewArray);
        //添加Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toolbar监听
        if (toolbar != null) {
            toolbar.setOnMenuItemClickListener(onMenuItemClick);
        }
        //UpdateManger mUpdateManger = new UpdateManger(RecorderActivity.this);
        //mUpdateManger.checkUpdateInfo();
        Log.d(TAG, "MainActivity onCreate 创建，执行");
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
        ClickListener();
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
    /*toolbar menu监听*/
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuitem) {
            switch (menuitem.getItemId()) {
                case R.id.more_action:
                    Log.d(TAG, "点击菜单");
                    break;
                case R.id.search_bar:
                    Log.d(TAG, "搜索");
                    break;
            }
            return true;
        }
    };
    /*设置状态栏颜色*/
    private void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintResource(R.color.accent_material_dark);
            tintManager.setStatusBarTintEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            Context context = getApplicationContext();
            int color = ContextCompat.getColor(context, R.color.accent_material_dark);
            window.setStatusBarColor(color);
        }
    }
    /*初始化列表*/
    private void init_list() {
        //为ListView对象赋值
        mListViewArray = (ListView) findViewById(R.id.recorder_ListView);
        if (mListViewArray != null) {
            LayoutInflater inflater = getLayoutInflater();
            //初始化数据
            initData();
            //创建自定义Adapter的对象
            adapter = new MyAdapter(inflater, mData);
            //将布局添加到ListView中
            mListViewArray.setAdapter(adapter);
            //单击监听
            mListViewArray.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TextView name = (TextView) view.findViewById(R.id.name);
                    String record_name = name.getText().toString() + ".amr";
                    File playfile = new File(mRecAudioPath.getAbsolutePath()
                            + File.separator + record_name);
                    Log.d(TAG,record_name);
                    Log.d(TAG,playfile.getPath());
                    //播放
                    playMusic(playfile);
                }
            });
        }
    }
    /*长按菜单弹出操作*/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //menu.setHeaderTitle("操作菜单");
        //添加菜单项
        menu.add(0,0,0,"删除记录");
        menu.add(0,1,0,"修改记录");
        menu.add(0,2,0,"添加备注");
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch(item.getItemId()){
            case 0:
                Log.d(TAG,Integer.toString(info.position));
                int post=(int)mListViewArray.getAdapter().getItemId(info.position);
                adapter.removeItem(post);
                return true;
            case 1:
                Log.d(TAG,"修改记录");
                return true;
            case 2:
                Log.d(TAG,"添加备注");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    /*初始化列表数据*/
    private void initData() {
        MusicFilter mFilter = new MusicFilter();
        mData = new ArrayList<>();
        File home = mRecAudioPath;
        if (home != null) {
            File[] files = home.listFiles(mFilter);
            if (files != null && files.length > 0) {
                for (File file : files) {
                    String fileName=file.getName();
                    String prefix=fileName.substring(0,fileName.lastIndexOf("."));
                    Recorder recorder = new Recorder(prefix, GetFilePlayTime(file), "录音1", "8月20号", R.drawable.ic_play_circle_filled_red_48dp);
                    mData.add(recorder);
                }
            }
        }
    }
    /*按钮监听*/
    private void ClickListener() {
        final ImageView begin_record = (ImageView) findViewById(R.id.begin_record);
        final ImageView save_record = (ImageView) findViewById(R.id.save_record);
        assert begin_record != null;
        assert save_record != null;
        begin_record.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Integer integer = (Integer) begin_record.getTag();
                integer = integer == null ? 0 : integer;
                switch (integer) {
                    default:
                    case R.mipmap.ic_toggle_radio_button_on:
                        begin_record.setImageResource(R.mipmap.ic_av_pause_circle_fill);
                        begin_record.setTag(R.mipmap.ic_av_pause_circle_fill);
                        save_record.setImageResource(0);
                        start_record();
                        Log.d(TAG, "开始录音");
                        break;
                    case R.mipmap.ic_av_pause_circle_fill:
                        begin_record.setImageResource(R.mipmap.ic_toggle_radio_button_on);
                        begin_record.setTag(R.mipmap.ic_toggle_radio_button_on);
                        save_record.setImageResource(R.drawable.ic_save_nactive_24dp);
                        Log.d(TAG, "暂停录音" + integer);
                        stop_record();
                        break;
                }
            }
        });
        save_record.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (save_record.getDrawable() == null) {
                    Log.d(TAG, "保存图片为空");
                } else {
                    Log.d(TAG, "点击保存录音");
                    begin_record.setImageResource(R.mipmap.ic_toggle_radio_button_on);
                    save_record.setImageResource(0);
                }
            }
        });
    }
    /*录音文件默认保存格式*/
    private static SimpleDateFormat time_format = new SimpleDateFormat("yyMMdd_HHmmss",Locale.getDefault());//24小时制
    /*开始录音*/
    private void start_record() {
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        File dir = getRecordDir();
        if ( dir == null ) {
            Log.d(TAG,"目录为空");
            return;
        }
        String name = time_format.format(new Date()) + ".amr";
        Log.d(TAG, name);
        File AudioFile = new File(dir, name);
        Log.d(TAG,AudioFile.getPath());
        mRecAudioFile = AudioFile.getPath();
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mMediaRecorder.setOutputFile(mRecAudioFile);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            chronometer.setFormat(null);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            Log.d(TAG,"record begin");
        } catch (Exception e) {
            Log.d(TAG,e.toString());
        }
    }
    /*结束录音*/
    private void stop_record() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            Log.d(TAG,"释放录音资源");
            mMediaRecorder = null;
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            ListView mListViewArray = (ListView) findViewById(R.id.recorder_ListView);
            if (mListViewArray != null) {
                LayoutInflater inflater = getLayoutInflater();
                //初始化数据
                initData();
                //创建自定义Adapter的对象
                adapter = new MyAdapter(inflater, mData);
                //将布局添加到ListView中
                mListViewArray.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }
    /*获取录音时常*/
    private String GetFilePlayTime(File file){
        java.util.Date date;
        SimpleDateFormat sy1;
        String dateFormat = "error";

        try {
            sy1 = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());//设置为时分秒的格式

            //使用媒体库获取播放时间
            MediaPlayer mediaPlayer;
            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(file.toString()));

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
    /*录音文件存放路径*/
    private File getRecordDir() {
        if (sdcardIsValid()) {
            String path = Environment.getExternalStorageDirectory() + "/record";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            return dir;
        } else {
            return null;
        }
    }
    /*判断是否有SD卡*/
    private boolean sdcardIsValid() {
        if (Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            Toast.makeText(getBaseContext(), "没有SD卡", Toast.LENGTH_LONG).show();
        }
        return false;
    }
    /* 播放录音文件 */
    private void playMusic(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
		/* 设置文件类型 */
        intent.setDataAndType(Uri.fromFile(file), "audio");
        startActivity(intent);
    }

    /* 过滤文件类型 */
    class MusicFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".amr"));
        }
    }
}
