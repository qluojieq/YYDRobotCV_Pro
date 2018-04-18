package com.yongyida.yydrobotcv.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.yongyida.yydrobotcv.camera.Camera2Track;
import com.yongyida.yydrobotcv.camera.CameraBase;
import com.yongyida.yydrobotcv.camera.PreviewListener;

public class FaceDetectService extends Service implements PreviewListener{

    private static final String TAG = FaceDetectService.class.getSimpleName();
    CameraBase mCamera2Track;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate");
        mCamera2Track = Camera2Track.getCameraInstance(this);
        mCamera2Track.setListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"startCommand");
        switch (intent.getStringExtra("startType")){
            case "start":
                mCamera2Track.start();
                break;
            case "stop":
                mCamera2Track.stop();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");
        super.onDestroy();
    }

    @Override
    public void preview(byte[] bytes) {
        Log.e(TAG,"preview");
    }
}
