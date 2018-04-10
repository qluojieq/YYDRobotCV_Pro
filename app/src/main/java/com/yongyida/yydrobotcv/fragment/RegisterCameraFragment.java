package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yongyida.yydrobotcv.R;

import java.io.IOException;

import dou.helper.CameraHelper;

/**
 * @author Brandon on 2018/3/15
 **/
public class RegisterCameraFragment extends Fragment implements SurfaceHolder.Callback ,Camera.PreviewCallback{

    private final String TAG = RegisterCameraFragment.class.getSimpleName();

    public byte gBuffer[];
    private SurfaceView surfaceView;
    private Camera camera; //这个是hardare的Camera对象
    SurfaceHolder holder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_camera,container,false);
        surfaceView = view.findViewById(R.id.camera_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
        if (camera!=null)
        camera.stopPreview();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG,"onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG,"onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG,"onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG,"onDetach");
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(gBuffer);
        Log.e(TAG, "预览帧frame");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 当Surface被创建的时候，该方法被调用，可以在这里实例化Camera对象
        //同时可以对Camera进行定制
        camera = Camera.open(); //获取Camera实例
        camera.setPreviewCallback(this);
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Camera.Parameters e = this.camera.getParameters();

        Camera.Size s = e.getPreviewSize();
        Log.e(TAG, s.width + " w  ;  h  " + s.height);
        camera.setParameters(e);
        int bufferSize  = 640*480 * ImageFormat.getBitsPerPixel(e.getPreviewFormat()) / 8;
        gBuffer = new byte[bufferSize];
        camera.addCallbackBuffer(gBuffer);
        camera.setPreviewCallbackWithBuffer(this);
        camera.startPreview();
        }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG,(camera!=null)+"onHiddenChanged"+hidden);
        if (camera!=null)
        if (hidden){
           camera.stopPreview();
        }else {
            camera.setPreviewCallbackWithBuffer(this);
            camera.startPreview();
        }
    }


}
