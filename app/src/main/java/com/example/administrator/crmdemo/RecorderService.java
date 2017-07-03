package com.example.administrator.crmdemo;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/19.
 */

public class RecorderService extends Service {
    private String phonenumber;
    private MediaRecorder mediaRecorder;
    private String time;
    private boolean getsd = false;
    private File file;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "开启服务", Toast.LENGTH_SHORT).show();
        //是否有sd卡
        getsd = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        //拿到电话管理器
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //监听电话状态
        telephonyManager.listen(new MyListen(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    class MyListen extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                //空闲状态
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mediaRecorder != null && file != null && file.exists()) {
                        //停止录音,释放资源
                        mediaRecorder.stop();
                        mediaRecorder.release();
                        mediaRecorder = null;
                        Toast.makeText(RecorderService.this, "电话挂断", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //摘机状态
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    long a = System.currentTimeMillis();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date b = new Date(a);
                    time = format.format(b);
                    //初始化录音机
                    if (mediaRecorder == null) {
                        mediaRecorder = new MediaRecorder();
                        //设置录音机声源是麦克风
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        //设置保存的音频文件格式,3gp
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                        //设置保存的音频文件的路径和名字
                        if (getsd) {
                            file = new File(Environment.getExternalStorageDirectory(), time + "|" + phonenumber + ".3gp");
                        } else {
                            file = new File(Environment.getDataDirectory(), time + "|" + phonenumber + ".3gp");
                        }
                        String path = file.getAbsolutePath();
                        Toast.makeText(RecorderService.this, path, Toast.LENGTH_SHORT).show();
                        mediaRecorder.setOutputFile(path);
                        //设置音频编码
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                        try {
                            //准备就绪
                            mediaRecorder.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }


                    if (mediaRecorder != null) {
                        //开始录音
                        Toast.makeText(RecorderService.this, "电话接通", Toast.LENGTH_SHORT).show();
                        mediaRecorder.start();
                    }
                    break;
                //响铃状态
                case TelephonyManager.CALL_STATE_RINGING:
                    Toast.makeText(RecorderService.this, "响铃", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        phonenumber = bundle.getString("phonenumber");
        return startId;
    }
    //判断是否拨打成功
    private boolean isLastCallSucceed() {
        String[] arrayOfString = {"number", "duration"};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Cursor localCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, arrayOfString, null, null, "date DESC");
        return (localCursor.moveToFirst()) && (localCursor.getInt(1) > 0);
    }
}
