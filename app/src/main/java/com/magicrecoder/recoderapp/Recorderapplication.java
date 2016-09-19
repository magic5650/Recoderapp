package com.magicrecoder.recoderapp;

import android.app.Application;
import android.content.Context;

import com.github.yoojia.anyversion.AnyVersion;
import com.github.yoojia.anyversion.Version;
import com.github.yoojia.anyversion.VersionParser;
import com.magicrecoder.greendao.DaoMaster;
import com.magicrecoder.greendao.DaoSession;
import com.magicrecoder.greendao.RecorderInfoDao;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Administrator on 2016-09-05.
 */
public class Recorderapplication extends Application {
    public Session mSession;
    public DaoSession daoSession;
    public RecorderInfoDao recorderinfoDao;
    @Override
    public void onCreate(){
        super.onCreate();
        mSession.init(this.getApplicationContext());
        daoSession = mSession.getInstance(this.getApplicationContext());
        recorderinfoDao = daoSession.getRecorderInfoDao();
        AnyVersion.init(this, new VersionParser() {
            @Override
            public Version onParse(String response) {
                final JSONTokener tokener = new JSONTokener(response);
                try {
                    JSONObject json = (JSONObject) tokener.nextValue();
                    return new Version(
                            json.getString("name"),
                            json.getString("note"),
                            json.getString("url"),
                            json.getInt("code")
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
