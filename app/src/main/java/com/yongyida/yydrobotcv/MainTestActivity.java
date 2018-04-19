package com.yongyida.yydrobotcv;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yongyida.yydrobotcv.service.FaceDetectService;
import com.yongyida.yydrobotcv.service.PersonDetectService;

public class MainTestActivity extends AppCompatActivity {
    private static final String TAG = MainTestActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);
    }

    public void startPersonDetect(View view) {
        Log.e(TAG,"开始人体检测");
        Intent intent = new Intent(MainTestActivity.this,PersonDetectService.class);
        startService(intent);
    }

    public void startPersonRegist(View view) {
        Log.e(TAG,"启动注册人脸");
        Intent intent = new Intent(this,PersonListActivity.class);
        startActivity(intent);
    }

    public void closePersonDetect(View view) {
        Log.e(TAG,"关闭人体检测");
    }

    public void startFaceDetect(View view) {
        Log.e(TAG,"开始人脸检测服务");
        Intent intent = new Intent(this, FaceDetectService.class);
        intent.putExtra("startType","start");
        startService(intent);

    }

    public void stopFaceDetect(View view) {
        Log.e(TAG,"关闭人脸检测服务");
        Intent intent = new Intent(this, FaceDetectService.class);
        intent.putExtra("startType","stop");
        startService(intent);
    }
}
