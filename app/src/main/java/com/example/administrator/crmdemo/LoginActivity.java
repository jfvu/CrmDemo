package com.example.administrator.crmdemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.zhy.autolayout.AutoLayoutActivity;

public class LoginActivity extends AutoLayoutActivity implements View.OnClickListener{
    private EditText etName;
    private EditText etPassword;
    private Button butLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //沉浸状态栏
        ImmersionBar.with(this).init();
        //初始化布局
        initview();

    }
private void initview(){
    //用户名
    etName = (EditText) findViewById(R.id.et_zh_login);
    //密码
    etPassword = (EditText) findViewById(R.id.et_password_login);
    //登录
    butLogin = (Button) findViewById(R.id.but_login);
    //登录监听
    butLogin.setOnClickListener(this);

}
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //页面销毁时销毁沉浸状态栏
        ImmersionBar.with(this).destroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.but_login:
                //获取用户名和密码
                String name = etName.getText().toString();
                String password = etPassword.getText().toString();
                //if (!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(password)){
                    Toast.makeText(this, "用户名："+name+"密码："+password, Toast.LENGTH_SHORT).show();
                //跳转到mainactivity
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                //}else {
                    //Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
               // }

                break;
        }
    }
}
