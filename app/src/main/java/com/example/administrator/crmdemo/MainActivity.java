package com.example.administrator.crmdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.zhy.autolayout.AutoLayoutActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2017/6/19.
 */

public class MainActivity extends AutoLayoutActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {
    private LinearLayout linearLayout;
    private EditText etInPutNumber;
    private Button butCall;
    private final String[] permissions = {Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
            Manifest.permission.RECORD_AUDIO};
    boolean getallpermissions = false;
    private String number;
    private Intent intent;
    private RecyclerView recyclerView;
    private List<RecordEntity> recordEntityList;
    private List<String> tempData;
    private boolean sd;
    private MyRecordAdapter recordAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initview();
    }

    private void initview() {
        tempData = new ArrayList<>();

        //设置ll的高度等于系统栏高度
        linearLayout = (LinearLayout) findViewById(R.id.ll_systembar);
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = getStatusBarHeight();
        //沉浸状态栏
        ImmersionBar.with(this).init();
        //输入电话edittext
        etInPutNumber = (EditText) findViewById(R.id.et_inputnumber_main);
        //拨打电话那妞
        butCall = (Button) findViewById(R.id.but_call_main);
        //拨打电话监听
        butCall.setOnClickListener(this);
        //判断权限
        methodRequiresPermission();
        //录音记录列表
        recyclerView = (RecyclerView) findViewById(R.id.rec_main);
        //录音列表数据
        recordEntityList = new ArrayList<>();
        //是否有sd卡
        sd = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        //设置recycleview布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recordEntityList = getRecordEntity();
        recordAdapter = new MyRecordAdapter(getApplication(), recordEntityList);
        recyclerView.setAdapter(recordAdapter);

    }

    //系统栏高度
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁时销毁沉浸状态栏
        ImmersionBar.with(this).destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.but_call_main:
                //获取电话号码
                number = etInPutNumber.getText().toString();
                //判断电话号码输入是否正确
                if (isMobileNO(number)) {
                    //判断权限
                    if (getallpermissions) {
                        //开启服务
                        intent = new Intent(MainActivity.this, RecorderService.class);
                        intent.putExtra("phonenumber", number);
                        startService(intent);
                        //拨打电话
                        calling();
                    } else {
                        Toast.makeText(this, "请先在设置中开启权限才可以使用", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "确认输入的号码是否正确", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //判断电话号码
    public static boolean isMobileNO(String mobiles) {
        Pattern P = Pattern.compile("^1[0-9]{10}$");
        Matcher M = P.matcher(mobiles);
        return M.matches();
    }

    //获取权限
    @AfterPermissionGranted(300)
    private void methodRequiresPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            getallpermissions = true;

        } else {
            getallpermissions = false;
            EasyPermissions.requestPermissions(this, "此APP需要此权限", 300, permissions);
        }
    }

    //拨打电话
    private void calling() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        Uri uri = Uri.parse("tel:" + number);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (intent != null) {
            MainActivity.this.stopService(intent);
            Toast.makeText(this, "录音已关闭", Toast.LENGTH_SHORT).show();
            //数据源改变，重新
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recordEntityList = getRecordEntity();
            recordAdapter = new MyRecordAdapter(getApplication(), recordEntityList);
            recyclerView.setAdapter(recordAdapter);


        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "请先在设置中开启权限才可以使用", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Toast.makeText(this, "权限开启", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Toast.makeText(this, "权限开启失败", Toast.LENGTH_SHORT).show();
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    //拿到录音记录明细
    public ArrayList<String> getRecorddate() {

        String path;
        if (sd) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            // recordEntityList = getRecordEntity(Environment.getDataDirectory().getAbsolutePath());
            path = Environment.getDataDirectory().getAbsolutePath();
        }

        ArrayList<String> list = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                String filename = files[i].getName();
                if (filename.trim().toLowerCase().endsWith(".3gp")) {

                    list.add(filename);
                }
            }
        }
        return list;
    }

    //拿到通话记录的明细
    private List<Map<String, String>> getOutCallLog() {
        ContentResolver resolver = getContentResolver();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI,
                new String[]{CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE}, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        List<Map<String,String>> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
            long datelong = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(datelong));
            int duration = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
            int type = CallLog.Calls.OUTGOING_TYPE;
            Map<String,String> map = new HashMap<String, String>();
            map.put("number",number);
            map.put("time",date);
            map.put("duration", String.valueOf(duration));
            list.add(map);

        }
        return list;
    }
    //过滤掉拨打电话未通的情况
    private List<RecordEntity> getRecordEntity(){
        List<RecordEntity> list = new ArrayList<>();
        List<String> recordList;
        List<Map<String,String>> callList;
        callList = getOutCallLog();
        recordList = getRecorddate();
        for (int i = 0; i < recordList.size(); i++) {
            String nameAll =  recordList.get(i);
            String[] temp= nameAll.split("\\|");
            for (int j = 0; j < callList.size(); j++) {
                if (callList.get(j).get("time").equals(temp[0]) && callList.get(j).get("duration").equals("0")){
                    if (sd){
                        File file = new File(Environment.getExternalStorageDirectory(), nameAll);
                        file.delete();
                    }else {
                        File file = new File(Environment.getDataDirectory(), nameAll);
                        file.delete();
                    }
                }else if (callList.get(j).get("time").equals(temp[0]) && !callList.get(j).get("duration").equals("0")){
                    RecordEntity recordEntity = new RecordEntity(temp[0],"3gp",callList.get(j).get("duration"),false);
                    list.add(recordEntity);
                }
            }
        }

        return list;
    }
}
