package com.magicrecoder.recoderapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magicrecoder.greendao.DaoSession;
import com.magicrecoder.greendao.RecorderInfoDao;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FilenameFilter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class RecorderActivity extends AppCompatActivity {

    private static final String TAG = "Lifecycle";
    //定义数据
    private List<RecorderInfo> recorderData;//listView数据对象
    private RecorderAdapter recorderAdapter;//适配器对象
    private ListView recorderListView;//列表对象
    private MediaRecorder mMediaRecorder;// MediaRecorder对象
    private String Filename;//录音文件绝对路径
    private Chronometer chronometer;//定义计时器
    RecorderInfo backObject = null;
    String backFileName;
    private RecorderInfoDao recorderInfoDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        recorderListView = (ListView) findViewById(R.id.recorder_ListView) ;
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        recorderInfoDao = ((Recorderapplication) this.getApplicationContext()).recorderinfoDao;
        init_recorder_list();
        try {
            Intent intent3 = getIntent();
            backFileName = intent3.getStringExtra("fileName");
            if ( backFileName != null) {
                backObject = recorderInfoDao.queryBuilder().where(RecorderInfoDao.Properties.Filepath.eq(backFileName)).unique();
                if( backObject != null ) {
                    recorderData.remove(backObject);
                    recorderAdapter.notifyDataSetChanged();
                    Log.d(TAG, "通知适配器");
                }
                else {
                    Log.d(TAG,"根据返回的ID查的无此对象");
                }
                recorderInfoDao.deleteByKey(backObject.getId());//删除数据库对象
                Log.d(TAG, "删除返回的object");
            }
            else {
                Log.d(TAG, "返回的对象为空");
            }
        }catch (Exception e){
            e.toString();
        }
        setStatusColor();
        //长按菜单弹出操作，注册列表
        registerForContextMenu(recorderListView);
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
        Log.d(TAG, "MainActivity onStart 可见 执行");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume 获取焦点 执行");
        ClickListener();
/*        Intent intent3 = getIntent();
        backObject = intent3.getParcelableExtra("recorder");
        if (backObject != null) {
            getDao().deleteByKey(backObject.getId());//删除数据库对象
            recorderData.remove(backObject);
            Log.d(TAG, "删除返回的object");
            recorderAdapter.notifyDataSetChanged();
            Log.d(TAG, "通知适配器");
        }
        else {
            Log.d(TAG, "返回的对象为空");
        }*/
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
/*        Intent intent2 = getIntent();
        backObject = intent2.getParcelableExtra("recorder");
        getDao().deleteByKey(backObject.getId());//删除数据库对象
        recorderData.remove(backObject);
        Log.d(TAG,"删除返回的object");
        recorderAdapter.notifyDataSetChanged();
        Log.d(TAG,"通知适配器");*/
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

/*        MenuInflater actionInflater = getMenuInflater();
        actionInflater.inflate(R.menu.recorder_menu,menu);*/
        return super.onCreateOptionsMenu(menu);
    }

    /*toolbar menu监听*/
    private Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuitem) {
            switch (menuitem.getItemId()) {
                case R.id.more_action:
                    Log.d(TAG, "点击菜单");
                    return true;
                case R.id.search_bar:
                    Log.d(TAG, "搜索");
                    return true;
                case R.id.menu_help:
                    Log.d(TAG, "帮助");
                    final AlertDialog helpDialog = new AlertDialog.Builder(RecorderActivity.this).create();
                    final View helpView = View.inflate(RecorderActivity.this, R.layout.help_dialog, null);
                    helpDialog.setView(helpView);
                    final TextView back = (TextView) helpView.findViewById(R.id.back);
                    final ImageView image = (ImageView) helpView.findViewById(R.id.loveImage);
                    image.setImageResource(R.drawable.help_pic);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            helpDialog.dismiss();
                        }
                    });
                    helpDialog.show();
                    return true;
            }
            return false;
        }
    };
    /*设置状态栏颜色*/
    private void setStatusColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintResource(R.color.color_toolbar);
            tintManager.setStatusBarTintEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            Context context = getApplicationContext();
            int color = ContextCompat.getColor(context, R.color.color_toolbar);
            window.setStatusBarColor(color);
        }
    }

    /*初始化列表数据*/
    private void initRecorderData() {
        recorderData = new ArrayList<>();
        //按照插入时间倒序排序，也就是说时间晚的会在前面显示
        recorderData = recorderInfoDao.queryBuilder().where(RecorderInfoDao.Properties.Id.notEq(-1)).orderDesc(RecorderInfoDao.Properties.Id).build().list();
    }
    //初始化列表
    private void init_recorder_list() {
        if (recorderListView != null) {
            LayoutInflater inflater = getLayoutInflater();
            initRecorderData();
            recorderAdapter = new RecorderAdapter(inflater, recorderData);
            recorderListView.setAdapter(recorderAdapter);

            //单击监听
            recorderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RecorderInfo recorderObject = recorderData.get(position);
                    File audioFile = new File(recorderObject.getFilepath());
                    if (audioFile.exists()) {
                        //playMusic(audioFile);
                        Log.d(TAG,"要开始播放了");
                        //playAudio(recorderObject.getFilepath(),recorderObject.getOften());
                        playAudio(recorderObject);
                    } else {
                        Toast.makeText(getBaseContext(), "录音文件不存在", Toast.LENGTH_SHORT).show();
                        if (recorderObject.getId() != null) {
                            recorderInfoDao.deleteByKey(recorderObject.getId());//删除数据库对象
                            recorderData.remove(recorderObject);
                            recorderAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
            //5.点击更多图标监听，实现接口setIconClickListener
            recorderAdapter.setIconClickListener(new RecorderAdapter.OnIconClickListener() {
                @Override
                public void onIconClick(final int position, View v) {
                    Log.d(TAG, "点击位置为" + position + "图标为");
                    final AlertDialog dialog = new AlertDialog.Builder(RecorderActivity.this).create();
                    final View dialogView = View.inflate(RecorderActivity.this, R.layout.custom_dialog, null);
                    final EditText editText = (EditText) dialogView.findViewById(R.id.fileName);
                    final TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialogTitle);
                    final TextView mBtnOK = (TextView) dialogView.findViewById(R.id.btnOK);
                    final TextView mBtnNO = (TextView) dialogView.findViewById(R.id.btnNO);
                    PopupMenu actionMenu = new PopupMenu(RecorderActivity.this, v);
                    getMenuInflater().inflate(R.menu.recorder_menu, actionMenu.getMenu());
                    actionMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        RecorderInfo recorderObject = recorderData.get(position);
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delRecorder:
                                    if (recorderObject.getId() != null) {
                                        Log.d(TAG, "" + recorderObject.getId());
                                        recorderInfoDao.deleteByKey(recorderObject.getId());//删除数据库对象
                                        recorderData.remove(recorderObject);
                                        recorderAdapter.notifyDataSetChanged();
                                    }
                                    if (removeAudioFile(recorderObject.getFilepath()))//删除文件
                                    {
                                        Toast.makeText(getApplicationContext(), "删除文件成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "删除文件失败", Toast.LENGTH_SHORT).show();
                                    }
                                    return true;
                                case R.id.modifyName:
                                    dialogTitle.setText("修改名称");
                                    editText.setText(recorderObject.getName());
                                    editText.selectAll();
                                    editText.setFocusable(true);
                                    editText.setFocusableInTouchMode(true);
                                    editText.requestFocus();
                                    editText.selectAll();
                                    Timer timer = new Timer();
                                    timer.schedule(new TimerTask() {
                                        public void run() {
                                            InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputManager.showSoftInput(editText, 0);
                                        }
                                    }, 500);
                                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String newName = editText.getText().toString();
                                            Log.d(TAG, newName);
                                            Log.d(TAG, "索引为" + position);
                                            recorderObject.setName(newName);
                                            recorderInfoDao.update(recorderObject);
                                            recorderData.get(position).setName(newName);
                                            updateView(position);//
                                            //recorderAdapter.notifyDataSetChanged();
                                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                    mBtnNO.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.setView(dialogView);
                                    dialog.show();
                                    Log.d(TAG, "修改记录");
                                    return true;
                                case R.id.modifyInfo:
                                    dialogTitle.setText("修改备注");
                                    editText.setText(recorderObject.getInfo());
                                    editText.setFocusable(true);
                                    editText.setFocusableInTouchMode(true);
                                    editText.requestFocus();
                                    editText.selectAll();
                                    Timer timer2 = new Timer();
                                    timer2.schedule(new TimerTask() {
                                        public void run() {
                                            InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            inputManager.showSoftInput(editText, 0);
                                        }
                                    }, 500);
                                    mBtnOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String newInfo = editText.getText().toString();
                                            Log.d(TAG, newInfo);
                                            recorderObject.setInfo(newInfo);
                                            recorderInfoDao.update(recorderObject);
                                            recorderData.get(position).setInfo(newInfo);
                                            updateView(position);//只刷新一条
                                            //recorderAdapter.notifyDataSetChanged();//会全部刷新
                                            Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                                    mBtnNO.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.setView(dialogView);
                                    dialog.show();
                                    Log.d(TAG, "添加备注");
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    actionMenu.show();
                }
            });
        }
        else{
            Log.d(TAG,"ArrayList非空");
        }
    }

    //长按列表弹出操作
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        //menu.setHeaderTitle("操作菜单");
        //添加菜单项
        menu.add(0, 0, 0, "删除记录");
        menu.add(0, 1, 0, "修改名称");
        menu.add(0, 2, 0, "添加备注");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int position = (int) recorderListView.getAdapter().getItemId(info.position);//获取点击listView的索引位置
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        final View dialogView = View.inflate(this, R.layout.custom_dialog, null);
        final EditText editText = (EditText) dialogView.findViewById(R.id.fileName);
        final TextView dialogTitle=(TextView) dialogView.findViewById(R.id.dialogTitle);
        final TextView mBtnOK = (TextView) dialogView.findViewById(R.id.btnOK);
        final TextView mBtnNO = (TextView) dialogView.findViewById(R.id.btnNO);
        RecorderInfo recorderObject= recorderData.get(position);
        switch (item.getItemId()) {
            case 0:
                if (recorderObject.getId() != null) {
                    Log.d(TAG,""+recorderObject.getId());
                    recorderInfoDao.deleteByKey(recorderObject.getId());//删除数据库对象
                    recorderData.remove(recorderObject);
                    recorderAdapter.notifyDataSetChanged();
                }
                if(removeAudioFile(recorderObject.getFilepath()))//删除文件
                {
                    Toast.makeText(getApplicationContext(),"删除文件成功",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"删除文件失败",Toast.LENGTH_SHORT).show();
                }
                return true;
            case 1:
                dialogTitle.setText("修改名称");
                editText.setText(recorderObject.getName());
                editText.selectAll();
                mBtnOK.setOnClickListener(new View.OnClickListener() {
                    RecorderInfo recorder= recorderData.get(position);
                    @Override
                    public void onClick(View v) {
                        String newName = editText.getText().toString();
                        Log.d(TAG, newName);
                        Log.d(TAG,"索引为"+position);
                        recorder.setName(newName);
                        recorderInfoDao.update(recorder);
                        recorderData.get(position).setName(newName);
                        updateView(position);
                        //recorderAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                mBtnNO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(dialogView);
                dialog.show();
                Log.d(TAG, "修改记录");
                return true;
            case 2:
                dialogTitle.setText("修改备注");
                editText.setText(recorderObject.getInfo());
                editText.selectAll();
                mBtnOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecorderInfo recorderTwo=recorderData.get(position);
                        String newInfo = editText.getText().toString();
                        Log.d(TAG, newInfo);
                        recorderTwo.setInfo(newInfo);
                        recorderInfoDao.update(recorderTwo);
                        recorderData.get(position).setInfo(newInfo);
                        updateView(position);
                        //recorderAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "修改成功", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                mBtnNO.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(dialogView);
                dialog.show();
                Log.d(TAG, "添加备注");
                return true;
            default:
                return super.onContextItemSelected(item);
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
                    case R.drawable.ic_radio_button_checked_red_24dp:
                        begin_record.setImageResource(R.drawable.ic_pause_circle_filled_red_24dp);
                        begin_record.setTag(R.drawable.ic_pause_circle_filled_red_24dp);
                        //save_record.setImageResource(0);
                        int color = ContextCompat.getColor(getBaseContext(), R.color.color_black);
                        chronometer.setTextColor(color);

                        start_record();
                        Log.d(TAG, "开始录音");
                        break;
                    case R.drawable.ic_pause_circle_filled_red_24dp:
                        begin_record.setImageResource(R.drawable.ic_radio_button_checked_red_24dp);
                        begin_record.setTag(R.drawable.ic_radio_button_checked_red_24dp);
                        //save_record.setImageResource(R.drawable.ic_save_nactive_24dp);
                        Log.d(TAG, "暂停录音" + integer);
                        int color2 = ContextCompat.getColor(getBaseContext(), R.color.color_grey);
                        chronometer.setTextColor(color2);
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
                    begin_record.setImageResource(R.drawable.ic_radio_button_checked_red_24dp);
                    save_record.setImageResource(0);
                }
            }
        });
    }

    /*录音文件默认保存格式*/
    private static SimpleDateFormat time_format = new SimpleDateFormat("HHmmss", Locale.getDefault());//24小时制

    /*开始录音*/
    private void start_record() {
        File dir = getRecordDir();
        if (dir == null) {
            Log.d(TAG, "目录为空");
            return;
        }
        Filename = time_format.format(new Date()) + ".amr";
        Log.d(TAG, Filename);
        File AudioFile = new File(dir, Filename);
        Log.d(TAG, AudioFile.getPath());
        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mMediaRecorder.setOutputFile(AudioFile.getPath());
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            chronometer.setFormat(null);
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            Log.d(TAG, "record begin");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    /*结束录音*/
    private void stop_record() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            Log.d(TAG, "释放录音资源");
            mMediaRecorder = null;
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            if (recorderListView != null) {
                File dir = getRecordDir();
                File File = new File(dir, Filename);
                String filepath=File.getPath();
                Log.d(TAG, "文件绝对路径"+filepath);
                int icon=R.drawable.ic_play_circle_filled_red_48dp;
                String name="录音"+Filename.substring(0,Filename.lastIndexOf("."));
                String often=GetFilePlayTime(File);
                String info="添加备注";
                Date create_date=new Date(File.lastModified());
                DateFormat format = new SimpleDateFormat("MM月dd日",Locale.getDefault());
                String create_time=format.format(create_date);
                String create_user="诗宁";
                String tag="默认";
                int action=R.drawable.ic_more_vert_grey_24dp;
                RecorderInfo recorderInfo=new RecorderInfo(null,filepath,icon,name,often,info,create_time,create_user,tag,action);
                recorderInfoDao.insert(recorderInfo);
                recorderData.add(0,recorderInfo);//永远在第一个，列表第一个显示
                recorderAdapter.notifyDataSetChanged();
            }
        }
    }

    /*获取录音时常*/
    private String GetFilePlayTime(File file) {
        Date date;
        Date oneHour;
        SimpleDateFormat sy1,sy2;
        String dateFormat = "error";

        try {
            sy1 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());//设置为时分秒的格式
            sy2 = new SimpleDateFormat("mm:ss", Locale.getDefault());//设置为时分秒的格式

            //使用媒体库获取播放时间
            MediaPlayer mediaPlayer;
            mediaPlayer = MediaPlayer.create(getBaseContext(), Uri.parse(file.toString()));

            //使用Date格式化播放时间mediaPlayer.getDuration()
            date = sy1.parse("00:00:00");
            oneHour = sy1.parse("01:00:00");
            date.setTime(mediaPlayer.getDuration() + date.getTime());//用消除date.getTime()时区差
            if (date.getTime()<oneHour.getTime()) {
                dateFormat = sy2.format(date);
            }
            else {
                dateFormat = sy1.format(date);
            }
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
                Environment.MEDIA_MOUNTED)) {
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
        intent.setAction(Intent.ACTION_VIEW);
        /* 设置文件类型 */
        intent.setDataAndType(Uri.fromFile(file), "audio");
        startActivity(intent);
    }

    //private void playAudio(String filePath,String durationTime) {
    private void playAudio(RecorderInfo recorderObject) {
        Intent intent = new Intent(this, MediaPlayActivity.class);
        Log.d(TAG,"放入的文件名为"+recorderObject.getFilepath());
        intent.putExtra("recorder",recorderObject);
        startActivity(intent);
    }

    /*通过 Recorderapplication 类提供的 getDaoSession() 获取具体 Dao*/
    private RecorderInfoDao getDao() {
        return ((Recorderapplication) this.getApplicationContext()).recorderinfoDao;
    }

    private void updateView(int itemIndex) {
        //得到第一个可显示控件的位置，
        int visiblePosition = recorderListView.getFirstVisiblePosition();
        //只有当要更新的view在可见的位置时才更新，不可见时，跳过不更新
        if (itemIndex - visiblePosition >= 0) {
            //得到要更新的item的view
            View view = recorderListView.getChildAt(itemIndex - visiblePosition);
            //调用adapter更新界面
            recorderAdapter.updateView(view, itemIndex);
        }
    }
    //删除文件
    private boolean removeAudioFile(String Filename) {
        File AudioFile = new File(Filename);
        return AudioFile.delete();
    };
    /* 过滤文件类型 */
    class MusicFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".amr"));
        }
    }
}
