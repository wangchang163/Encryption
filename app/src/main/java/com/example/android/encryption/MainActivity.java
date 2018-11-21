package com.example.android.encryption;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private File sdCard = Environment.getExternalStorageDirectory();
    private String fileName;
    private EditText etName;
    private TextView tvTime;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etName = findViewById(R.id.etName);
        tvTime = findViewById(R.id.tvTime);
        verifyStoragePermissions(this);
    }

    private void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param view
     */
    public void encrypt(View view) {
        fileName = etName.getText().toString().trim();
        if (!TextUtils.isEmpty(fileName)) {
            FileEnDecryptManager.initEncrypt(this,sdCard+"/"+fileName);
//          boolean result=AESManager.encrypt(getApplicationContext(), new File(sdCard, fileName));
        } else {
            showToast("请输入文件名称");
        }
    }

    /**
     * 解密
     *
     * @param view
     */
    public void decrypt(View view) {
        fileName = etName.getText().toString().trim();
        if (!TextUtils.isEmpty(fileName)) {
            FileEnDecryptManager.initdecrypt(this,sdCard+"/"+fileName);
//          boolean result=AESManager.decrypt(this, new File(sdCard, fileName));
        } else {
            showToast("请输入文件名称");
        }
    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

}
