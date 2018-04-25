package com.yongyida.yydrobotcv.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orbbec.astrakernel.AstraContext;
import com.orbbec.astrakernel.PermissionCallbacks;
import com.orbbec.astrastartlibs.DepthData;
import com.orbbec.astrastartlibs.UserTracker;
import com.yongyida.yydrobotcv.utils.CommonUtils;

import org.openni.IObservable;
import org.openni.IObserver;
import org.openni.Point3D;
import org.openni.UserEventArgs;

public class PersonDetectService extends Service {
    private static final String TAG = PersonDetectService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG,"onCreate");
        super.onCreate();
        mContext = new AstraContext(this,permissionCallbacks);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"onDestroy");
        super.onDestroy();
    }
    AstraContext mContext;
    DepthData mDepthData;
    UserTracker mUserTracker;
    private Thread m_analyzeThread;
    boolean mExit = false;
    boolean mInitOk = false;


    PermissionCallbacks permissionCallbacks = new PermissionCallbacks() {
        @Override
        public void onDevicePermissionGranted() {
            Log.e(TAG,"permission granted");
            mInitOk = true;
            mDepthData = new DepthData(mContext);
            mDepthData.setMapOutputMode(320,240,30);
            mUserTracker = new UserTracker(mContext);
            mUserTracker.addUserDetectObserver(new NewUserObserver());
            mContext.start();
            if (m_analyzeThread==null){
                m_analyzeThread = new Thread(new AnalyzeRunable());
                m_analyzeThread.start();
            }
        }

        @Override
        public void onDevicePermissionDenied() {
            Log.e(TAG,"permission denied");

        }
    };
    class NewUserObserver implements IObserver<UserEventArgs> {

        @Override
        public void update(IObservable<UserEventArgs> iObservable, UserEventArgs userEventArgs) {
            Log.e(TAG,"newUserObserver update"+ userEventArgs.getId());
        }
    }

    boolean isPerson = true;
    long [] oneTime = {0,0,0,0,0,0,0,0,0,0};
    long [] oneTimeGone = {0,0,0,0,0,0,0,0,0,0};
    class AnalyzeRunable implements Runnable{
        @Override
        public void run() {
            while (!mExit){

//                Log.e(TAG,"人体检测中");
                mContext.waitAnyUpdateAll();
                int [] data  = mUserTracker.getUsers();
                if (data.length<=0){
                    continue;
                }

                for (int i=0;i<data.length;i++){
                    Point3D head = mDepthData
                            .convertRealWorldToProjective(mUserTracker
                                    .getCoM(data[i]));
                    if (head.getZ()>0&&head.getZ()<2000){
                        isPerson = true;
                        oneTime[i]++;
                        oneTimeGone[i] = 0;
                        if (isPerson&&oneTime[i]==1){
                            startFaceDetect("start");
                            Log.e(TAG,"人来 " + i);
                            CommonUtils.serviceToast(PersonDetectService.this,"有人");
                        }
                    }else {
                        isPerson = false;
                        oneTime[i] = 0;
                        oneTimeGone[i]++;
                        if (oneTimeGone[i]==1){
//                         startFaceDetect("stop");
                            CommonUtils.serviceToast(PersonDetectService.this,"离开");
                         Log.e(TAG,"人离开" + i);
                        }
                    }

                }

            }
        }
    }

    //获取当前人数
    public int getCurrentPersonCount(){
        int ret = 0;
        for (int i = 0;i<oneTime.length;i++){
            if (oneTime[i]>0){
                ret++;
            }
        }
        return  ret;
    }

    //启动人脸检测服务
    public void startFaceDetect(String type){
        if (true){
            Log.e(TAG,"当前拥有的人数 " + getCurrentPersonCount());
            if (getCurrentPersonCount()==1){
                Intent intent = new Intent(this,FaceDetectService.class);
                intent.putExtra("startType",type);
                startService(intent);
            }
        }
    }

}
