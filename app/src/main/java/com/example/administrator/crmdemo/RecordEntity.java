package com.example.administrator.crmdemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/6/19.
 * 录音实体类，记录拨打电话时间，储存类型，通话时长,是否上传
 */

public class RecordEntity implements Parcelable{
    private String time;
    private String format;
    private String duration;
    private boolean upload;

    protected RecordEntity(Parcel in) {
        time = in.readString();
        format = in.readString();
        duration = in.readString();
        upload = in.readByte() != 0;
    }

    public static final Creator<RecordEntity> CREATOR = new Creator<RecordEntity>() {
        @Override
        public RecordEntity createFromParcel(Parcel in) {
            return new RecordEntity(in);
        }

        @Override
        public RecordEntity[] newArray(int size) {
            return new RecordEntity[size];
        }
    };

    public String getTime() {
        return time;
    }

    public String getFormat() {
        return format;
    }

    public String getDuration() {
        return duration;
    }

    public boolean isUpload() {
        return upload;
    }

    public RecordEntity(String time, String format, String duration, boolean upload) {
        this.time = time;
        this.format = format;
        this.duration = duration;
        this.upload = upload;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
dest.writeStringArray(new String[]{this.time,this.format,this.duration, String.valueOf(this.isUpload())});
    }
}
