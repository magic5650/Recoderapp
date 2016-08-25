package com.magicrecoder.recoderapp;

/**
 * Created by Administrator on 2016-08-24.
 */
public class Recorder {
    /*
定义学生的构造器，创建学生对象时定义学生的信息。
 */
    public Recorder(String name, String often, String info, String create_time,int icon){
        this.name = name;
        this.often = often;
        this.info = info;
        this.create_time = create_time;
        this.icon = icon;
    }
    private int icon;  //录音图标
    private String name;//录音名字
    private String often;//录音时常
    private String info;//录音信息
    private String create_time;//录音创建时间

    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon)
    {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getOften() {
        return often;
    }
    public void setOften(String often) {
        this.often = often;
    }
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public String getCreate_time() {
        return create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}
