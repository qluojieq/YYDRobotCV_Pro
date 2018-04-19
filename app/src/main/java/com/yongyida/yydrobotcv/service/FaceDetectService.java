package com.yongyida.yydrobotcv.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.yongyida.yydrobotcv.camera.Camera2Track;
import com.yongyida.yydrobotcv.camera.CameraBase;
import com.yongyida.yydrobotcv.camera.PreviewListener;
import com.yongyida.yydrobotcv.utils.CommonUtils;
import com.yongyida.yydrobotcv.utils.DrawUtil;

import java.util.List;

import dou.utils.DLog;
import dou.utils.ToastUtil;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

public class FaceDetectService extends Service implements PreviewListener{

    private static final String TAG = FaceDetectService.class.getSimpleName();
    CameraBase mCamera2Track;
    YMFaceTrack faceTrack;
    Context mContext;


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
        trackingMap = new SimpleArrayMap<>();
        DrawUtil.updateDataSource(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"startCommand");
        switch (intent.getStringExtra("startType")){
            case "start":
                mCamera2Track.start();
                startTrack();
                break;
            case "stop":
                stopTrack();
                mCamera2Track.stop();
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");
        super.onDestroy();
    }
    private final Object lock = new Object();
    protected boolean stop = false;
    @Override
    public void preview(byte[] bytes) {
        if (!stop) {
            synchronized (lock) {
                runTrack(bytes);
            }
        }
    }

    public void stopTrack() {

        if (faceTrack == null) {
            DLog.d("already release track");
            return;
        }
        stop = true;
        faceTrack.onRelease();
        faceTrack = null;
        DLog.d("release track success");
    }

    public void startTrack() {
        if (faceTrack != null) {
            DLog.d("already init track");
            return;
        }

        stop = false;
        mContext = this;
        faceTrack = new YMFaceTrack();

        faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_NEAR);

        int result = faceTrack.initTrack(this, YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640);
        DLog.d("getAlbumSize1: " + faceTrack.getEnrolledPersonIds().size());
        boolean needUpdateFaceFeature = faceTrack.isNeedUpdateFaceFeature();
        if (needUpdateFaceFeature) {
            DLog.d("update result: " + faceTrack.updateFaceFeature());
        }

        if (result == 0) {
            faceTrack.setRecognitionConfidence(70);
            new ToastUtil(this).showSingletonToast("初始化检测器成功");
        } else {
            new ToastUtil(this).showSingletonToast("初始化检测器失败");
        }

        DLog.d("getAlbumSize2 :" + faceTrack.getEnrolledPersonIds().size());
    }
    private void runTrack(byte[] data) {
            final List<YMFace> faces = analyse(data, 1280, 720);
            if (faces.size()>0){
                String name = DrawUtil.getNameFromPersonId(faces.get(0).getPersonId());
//                Log.e(TAG,"人脸识别的 id号码 " + faces.get(0).getPersonId() + "可信度 " + faces.get(0).getConfidence() +  "获取到的人名 " + name);
                if (!TextUtils.isEmpty(name)){
                    CommonUtils.serviceToast(this,name);
                }
            }
    }


    private SimpleArrayMap<Integer, YMFace> trackingMap;
    private Thread thread;
    boolean threadBusy = false;
    protected List<YMFace> analyse(final byte[] bytes, final int iw, final int ih) {

        if (faceTrack == null) return null;
        final List<YMFace> faces = faceTrack.trackMulti(bytes, iw, ih);


        if (faces != null && faces.size() > 0) {
            if ( !stop) {
                if (trackingMap.size() > 50) trackingMap.clear();
                //只对最大人脸框进行识别
                int maxIndex = 0;
                for (int i = 1; i < faces.size(); i++) {
                    if (faces.get(maxIndex).getRect()[2] <= faces.get(i).getRect()[2]) {
                        maxIndex = i;
                    }
                }

                final YMFace ymFace = faces.get(maxIndex);
                final int anaIndex = maxIndex;
                final int trackId = ymFace.getTrackId();
                final float[] rect = ymFace.getRect();
                final float[] headposes = ymFace.getHeadpose();

                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            threadBusy = true;
                            final byte[] yuvData = new byte[bytes.length];
                            System.arraycopy(bytes, 0, yuvData, 0, bytes.length);
                            boolean next = true;
                            if ((Math.abs(headposes[0]) > 30
                                    || Math.abs(headposes[1]) > 30
                                    || Math.abs(headposes[2]) > 30)) {
                                //角度不佳不再识别
                                next = false;
                            }
                            int faceQuality = faceTrack.getFaceQuality(anaIndex);
                            if (faceQuality < 85) {
                                //人脸质量不佳，不再识别
                                next = false;
                            }
                            long time = System.currentTimeMillis();
                            int identifyPerson = -11;
                            if (next) {
                                final int trackId = ymFace.getTrackId();
                                if (!trackingMap.containsKey(trackId) ||
                                        trackingMap.get(trackId).getPersonId() <= 0) {


                                    identifyPerson = faceTrack.identifyPerson(anaIndex);
                                    int confidence = faceTrack.getRecognitionConfidence();

                                    ymFace.setIdentifiedPerson(identifyPerson, confidence);
                                    trackingMap.put(trackId, ymFace);
                                }
                                next = false;
                                //使用本地就不再使用云端,可直接删除云端部分
                            }

                            DLog.d("identify end " + identifyPerson + " time :" + (System.currentTimeMillis() - time)
                                    + "  faceQuality: " + faceQuality);

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            threadBusy = false;
                        }
                    }
                });
                thread.start();
            }

            for (int i = 0; i < faces.size(); i++) {
                final YMFace ymFace = faces.get(i);
                final int trackId = ymFace.getTrackId();
                if (trackingMap.containsKey(trackId)) {
                    YMFace face = trackingMap.get(trackId);
                    ymFace.setIdentifiedPerson(face.getPersonId(), face.getConfidence());
                }
            }
        }
        return faces;
    }

}
