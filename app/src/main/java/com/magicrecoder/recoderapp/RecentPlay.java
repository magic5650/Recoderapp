package com.magicrecoder.recoderapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2016-10-17.
 */
public class RecentPlay implements Parcelable {
    private String filepath;//录音文件路径
    private String name;//录音名字
    private String tag;//录音标签
    private String create_user;//创建者

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filepath);
        dest.writeString(this.name);
        dest.writeString(this.tag);
        dest.writeString(this.create_user);
    }

    public RecentPlay(String filepath,String name,String tag,String create_user) {
        this.filepath = filepath;
        this.name = name;
        this.tag = tag;
        this.create_user = create_user;
    }

    protected RecentPlay(Parcel in) {
        this.filepath = in.readString();
        this.name = in.readString();
        this.tag = in.readString();
        this.create_user = in.readString();
    }

    public static final Parcelable.Creator<RecentPlay> CREATOR = new Parcelable.Creator<RecentPlay>() {
        @Override
        public RecentPlay createFromParcel(Parcel source) {
            return new RecentPlay(source);
        }

        @Override
        public RecentPlay[] newArray(int size) {
            return new RecentPlay[size];
        }
    };
}
