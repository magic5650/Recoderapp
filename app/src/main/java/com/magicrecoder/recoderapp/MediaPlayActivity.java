package com.magicrecoder.recoderapp;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.magicrecoder.greendao.RecorderInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MediaPlayActivity extends Activity {
    private static final String TAG = "Lifecycle";
    private static SeekBar seekBar;//属性在对象生成的时候才有，所以在静态代码中要变成静态。
    private AudioService.MusicInterface mi;
    private MyServiceConn conn;
    private Intent intent;
    private RecorderInfo recorderInfo;
    private String filePath;
    String durationTime;
    private static ImageView playIcon;
    private static ImageView pauseIcon;
    private static TextView tx_currentTime;
    TextView tx_maxTime;
    private ImageView backToRecorder;
    private ImageView delAudio;
    private LinearLayout.LayoutParams miss = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,0);
    private LinearLayout.LayoutParams show = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1);
    private List<RecorderInfo> recentPlayData;//listView数据对象
    private PlayListAdapter playListAdapter;//适配器对象
    private ListView recentPlayListView;//最近播放列表对象

    static Handler handler = new Handler(){//handler是谷歌说明的定义成静态的，
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            boolean isPlay = bundle.getBoolean("isPlaying");
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            //刷新进度条的进度，设置SeekBar的Max和Progress就能够时时更新SeekBar的长度，
            if ( isPlay ) {
                //Log.d(TAG,"正在播放,总时长为"+duration+",当前位置为"+currentPosition);
                seekBar.setMax(duration);
                seekBar.setProgress(currentPosition);
            }
            try {
                int time= currentPosition/1000;
                if (time >= 3600) {
                    int hour = time/3600;
                    int minute = (time/60) % 60;
                    int second = time % 60;
                    String currentTime = String.format(Locale.getDefault(),"%02d:%02d:%02d", hour, minute, second);
                    tx_currentTime.setText(currentTime);
                }
                else{
                    int minute = time/60;
                    int second = time % 60;
                    String currentTime = String.format(Locale.getDefault(),"%02d:%02d", minute, second);
                    tx_currentTime.setText(currentTime);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //根据当前位置判断录音是否结束，结束后更新UI，getCurrentPosition与duration偏移量200以内
            if ( !isPlay ){
                Log.d(TAG,"播放结束,当前位置为"+currentPosition+"seekBar的长度为"+duration);
                seekBar.setProgress(duration);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,0);
                pauseIcon.setLayoutParams(lp);
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT,1);
                playIcon.setLayoutParams(lp2);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MediaPlayActivity onCreate 创建 执行");
        setContentView(R.layout.activity_media_play);
        recentPlayListView = (ListView) findViewById(R.id.recent_play_list) ;
        setStatusColor();
        tx_currentTime = (TextView) findViewById(R.id.tx_currentTime);
        tx_currentTime.setText("00:00");
        seekBar = (SeekBar) findViewById(R.id.seedBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //拖动SeedBar的进度改变录音播放进度
                int process = seekBar.getProgress();
                mi.seekTo(process);
                try {
                    int time= process/1000;
                    if (time >= 3600) {
                        int hour = time/3600;
                        int minute = (time/60) % 60;
                        int second = time % 60;
                        String currentTime = String.format(Locale.getDefault(),"%02d:%02d:%02d", hour, minute, second);
                        tx_currentTime.setText(currentTime);
                    }
                    else{
                        int minute = time/60;
                        int second = time % 60;
                        String currentTime = String.format(Locale.getDefault(),"%02d:%02d", minute, second);
                        tx_currentTime.setText(currentTime);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        try {
            Intent intent2 = getIntent();
            recorderInfo = intent2.getParcelableExtra("recorder");
            if (recorderInfo == null){
                Intent intent = new Intent(MediaPlayActivity.this, RecorderActivity.class);
                Log.d(TAG,"录音对象为空,返回录音界面");
                startActivity(intent);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        filePath = recorderInfo.getFilepath();
        durationTime = recorderInfo.getOften();
        Log.d(TAG,"启动播放界面,录音文件路径为"+filePath+"时常为"+durationTime);

        intent= new Intent(this,AudioService.class);
        startService(intent);
        playIcon = (ImageView) findViewById(R.id.play);
        pauseIcon = (ImageView) findViewById(R.id.pause);
        backToRecorder = (ImageView) findViewById(R.id.backToRecorder);
        delAudio = (ImageView) findViewById(R.id.delAudio);
        tx_maxTime = (TextView) findViewById(R.id.tx_maxTime);
        tx_maxTime.setText(durationTime);
        conn= new MyServiceConn();
        bindService(intent,conn,BIND_AUTO_CREATE);
    }

    @Override
    public  void onResume() {
        super.onResume();
        init_play_list();
        Log.d(TAG, "MediaPlayActivity onResume 获取焦点 执行");
        playIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playIcon.setLayoutParams(miss);
                pauseIcon.setLayoutParams(show);
                //根据seekBar位置判断是继续还是重新开始
                if ( mi.getCurrentPosition()>0 && !(seekBar.getProgress()==seekBar.getMax()) ) {
                    Log.d(TAG,"当前播放位置为"+ mi.getCurrentPosition() +",继续播放");
                    continuePlay();
                }
                else {
                    Log.d(TAG,"重新开始播放");
                    seekBar.setProgress(0);
                    play();
                }
            }
        });
        pauseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"暂停播放录音");
                pauseIcon.setLayoutParams(miss);
                playIcon.setLayoutParams(show);
                pause();
            }
        });
        backToRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayActivity.this.finish();//这个是关键
            }
        });
        delAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"询问删除文件,暂停播放录音");
                if(mi.isPlaying()) {
                    pauseIcon.setLayoutParams(miss);
                    playIcon.setLayoutParams(show);
                    pause();
                    Log.d(TAG,"已暂停暂停播放录音");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MediaPlayActivity.this,R.style.MyDialogAlert);
                //设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
                //builder.setIcon(android.R.drawable.ic_dialog_alert);
                //设置对话框标题
                builder.setTitle("删除录音");
                //设置对话框内的文本
                builder.setMessage("删除录音文件"+recorderInfo.getName());
                //设置确定按钮，并给按钮设置一个点击侦听，注意这个OnClickListener使用的是DialogInterface类里的一个内部接口
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 执行点击确定按钮的业务逻辑
                        if(recorderInfo != null) {
                            try {
                                dialog.dismiss();
                                //传递RecorderInfo对象给RecorderActivity用于删除数据对象以便更新列表
                                Intent intent = new Intent(MediaPlayActivity.this, RecorderActivity.class);
                                Log.d(TAG,"放入的文件名为"+recorderInfo.getFilepath());
                                intent.putExtra("recorder",recorderInfo);
                                startActivity(intent);
                                MediaPlayActivity.this.finish();
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });
                //设置取消按钮
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 执行点击取消按钮的业务逻辑
                        dialog.dismiss();
                    }
                });
                //使用builder创建出对话框对象
                AlertDialog dialog = builder.create();
                //显示对话框
                dialog.show();
            }
        });
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "MediaPlayActivity onStop 不可见 执行");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "MediaPlayActivity onPause 失去焦点 执行");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MediaPlayActivity onDestroy 销毁 执行");
        exit();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "MediaPlayActivity onRestart 重新打开 执行");
    }
    private void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // 设置透明状态栏
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
    //初始化最近播放列表
    private void init_play_list() {
    //列表非空，且适配器为空才初始化
        if (recentPlayListView != null && playListAdapter == null){
            LayoutInflater inflater = getLayoutInflater();
            //init_playRecent_data();
            Log.d(TAG,"对象的名称为"+recorderInfo.getName());
            if (recentPlayData == null) {
                recentPlayData = new ArrayList<>();
                Log.d(TAG, "初始化列表数据");
                //按照插入时间倒序排序，也就是说时间晚的会在前面显示
                recentPlayData = getDao().queryBuilder().where(RecorderInfoDao.Properties.Id.notEq(-1)).orderDesc(RecorderInfoDao.Properties.Id).build().list();
                //recentPlayData.add(0,recorderInfo);
            }
            Log.d(TAG,"绑定适配器数据");
            playListAdapter = new PlayListAdapter(inflater, recentPlayData);
            Log.d(TAG,"列表绑定适配器");
            recentPlayListView.setAdapter(playListAdapter);
            //单击监听
            recentPlayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RecorderInfo recorderObject = recentPlayData.get(position);
                    File audioFile = new File(recorderObject.getFilepath());
                    if (audioFile.exists()) {
                        //playMusic(audioFile);
                        Log.d(TAG,"要开始播放了");
                        filePath = recorderObject.getFilepath();
                        mi.play(filePath);
                        pauseIcon.setLayoutParams(show);
                        playIcon.setLayoutParams(miss);
                        tx_maxTime.setText(recorderObject.getOften());
                    } else {
                        Toast.makeText(getBaseContext(), "录音文件不存在", Toast.LENGTH_SHORT).show();
                        if (recorderObject.getId() != null) {
                            getDao().deleteByKey(recorderObject.getId());//删除数据库对象
                            recentPlayData.remove(recorderObject);
                            playListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }

    /*通过 Recorderapplication 类提供的 getDaoSession() 获取具体 Dao*/
    private RecorderInfoDao getDao() {
        return ((Recorderapplication) this.getApplicationContext()).recorderinfoDao;
    }
    class MyServiceConn implements ServiceConnection{
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mi = (AudioService.MusicInterface) service;//中间人
            Log.d(TAG,"获取到mi对象,立即播放");
            play();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
    public void play(){
        Log.d(TAG,"调用MediaPlayActivity里的方法play");
        mi.play(filePath);
    }
    public void continuePlay (){
        mi.continuePlay();
    }
    public void pause(){
        mi.pause();
    }
    public void exit() {
        Log.d(TAG,"退出服务");
        unbindService(conn);  //解绑
        stopService(intent);  //停止
    }
}
