package com.magicrecoder.recoderapp;

import android.app.Application;

import com.magicrecoder.greendao.DaoMaster;
import com.magicrecoder.greendao.DaoSession;
import com.magicrecoder.greendao.RecorderInfoDao;

/**
 * Created by Administrator on 2016-09-05.
 */
public class Recorderapplication extends Application {
    public Session mSession;
    public DaoSession daoSession;
    public RecorderInfoDao recorderinfoDao;
    @Override
    public void onCreate(){
        mSession.init(this.getApplicationContext());
        daoSession = mSession.getInstance(this.getApplicationContext());
        recorderinfoDao = daoSession.getRecorderInfoDao();
        super.onCreate();
    }
}
