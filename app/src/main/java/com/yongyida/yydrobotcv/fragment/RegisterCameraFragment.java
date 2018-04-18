package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.yydrobotcv.R;
import com.yongyida.yydrobotcv.RegisterActivity;
import com.yongyida.yydrobotcv.camera.CameraBase;
import com.yongyida.yydrobotcv.camera.ImageUtils;
import com.yongyida.yydrobotcv.useralbum.User;

import java.util.ArrayList;
import java.util.List;

import dou.helper.CameraHelper;
import dou.helper.CameraParams;
import dou.utils.BitmapUtil;
import dou.utils.DLog;
import dou.utils.DeviceUtil;
import dou.utils.DisplayUtil;
import dou.utils.ToastUtil;
import mobile.ReadFace.YMFace;
import mobile.ReadFace.YMFaceTrack;

import static dou.utils.HandleUtil.runOnUiThread;

/**
 * @author Brandon on 2018/4/10
 **/
public class RegisterCameraFragment extends Fragment implements CameraHelper.PreviewFrameListener {

    private final String TAG = RegisterCameraFragment.class.getSimpleName();

    public SurfaceView preview_surface;
    public SurfaceView draw_surface;
    TextView hintFaceView;
    protected CameraHelper mCameraHelper;
    protected YMFaceTrack faceTrack;
    Context mContext;

    protected int iw = 0, ih;
    private float scale_bit;
    private boolean showFps = false;
    private List<Float> timeList = new ArrayList<>();
    protected boolean stop = false;
    //camera_max_width值为-1时, 找大于640分辨率为屏幕宽高等比
    private int camera_max_width = -1;

    int camera_fps;
    int camera_count;
    long camera_long = 0;

    int sw;
    int sh;

    public static Handler mHandler;

    //对于每种情况预览帧数进程测试
    int TOTAL_STEP = 5;
    int currentStep = 0;
    int viewCountStep1 = 0;
    int viewCountStep2 = 0;
    int viewCountStep3 = 0;
    int viewCountStep4 = 0;
    //注册的Id;
    int personId;
    User registerUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        initCamera(view);
        registerUser = new User();
        faceFrame = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_face_frame);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        hintFaceView.setText("侧脸");
                        break;
                    case 2:
                        hintFaceView.setText("抬头");
                        break;
                    case 3:
                        hintFaceView.setText("完成，跳转");
                        break;
                    case 4://停止，跳转
                        ((RegisterActivity)RegisterCameraFragment.this.getActivity()).registerBaseInfo(null);
                        ((RegisterActivity)RegisterCameraFragment.this.getActivity()).setRegisterUser(registerUser,1);
                        break;
                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTrack();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            stopTrack();
        } else {
            viewCountStep4 = 0;//点回来，转过去
            startTrack();
        }

    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (camera_long == 0) camera_long = System.currentTimeMillis();
        camera_count++;
        if (System.currentTimeMillis() - camera_long > 1000) {
            camera_fps = camera_count;
            camera_count = 0;
            camera_long = 0;
        }
        initCameraMsg();
        if (!stop) {
            runTrack(bytes);
        }
    }

    private void runTrack(byte[] data) {
        try {

            long time = System.currentTimeMillis();
            final List<YMFace> faces = analyse(data, iw, ih);

            String str = "";
            StringBuilder fps = new StringBuilder();
            if (showFps) {
                fps.append("fps = ");
                long now = System.currentTimeMillis();
                float than = now - time;
                timeList.add(than);
                if (timeList.size() >= 20) {
                    float sum = 0;
                    for (int i = 0; i < timeList.size(); i++) {
                        sum += timeList.get(i);
                    }
                    fps.append(String.valueOf((int) (1000f * timeList.size() / sum)))
                            .append(" camera ")
                            .append(camera_fps);
                    timeList.remove(0);
                }
            }
//            final String fps1 = fps.toString() + str;
            final String fps1 = "";

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    drawAnim(faces, draw_surface, scale_bit, getCameraId(), fps1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initCamera(View view) {
        mContext = this.getActivity();
        sw = DisplayUtil.getScreenWidthPixels(mContext);
        sh = DisplayUtil.getScreenHeightPixels(mContext);
        preview_surface = view.findViewById(R.id.camera_preview);
        draw_surface = view.findViewById(R.id.draw_view);
        hintFaceView = view.findViewById(R.id.hint_face_side);
        draw_surface.setZOrderOnTop(true);
        draw_surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        //预设Camera参数，方便扩充
        CameraParams params = new CameraParams();
        //优先使用的camera Id,
        params.firstCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        params.surfaceView = preview_surface;
        params.preview_width = camera_max_width;
        params.preview_width = 1920;
        params.preview_height = 1080;

        params.camera_ori = 0;
        params.camera_ori_front = 0;

        if (DeviceUtil.getModel().equals("Nexus 6")) {
            params.camera_ori_front = 180;

        }

        params.previewFrameListener = this;
        mCameraHelper = new CameraHelper(this.getActivity(), params);
    }

    public synchronized void stopTrack() {
        clearDrawSurface();
        if (faceTrack == null) {
            DLog.d("already release track");
            return;
        }
        stop = true;
        faceTrack.onRelease();
        faceTrack = null;
        DLog.d("release track success");
    }

    public synchronized void startTrack() {
        if (faceTrack != null) {
            DLog.d("already init track");
            return;
        }

        stop = false;

        iw = 0;//重新调用initCameraMsg的开关
        faceTrack = new YMFaceTrack();

        //设置人脸检测距离，默认近距离，需要在initTrack之前调用
        faceTrack.setDistanceType(YMFaceTrack.DISTANCE_TYPE_NEAR);

//        普通有效期版本初始化
        int result = faceTrack.initTrack(this.getActivity(), YMFaceTrack.FACE_0, YMFaceTrack.RESIZE_WIDTH_640);

        //设置人脸识别置信度，设置75，不允许修改

        if (result == 0) {
            DLog.d("初始化成功");
//            new ToastUtil(mContext).showSingletonToast("初始化检测器成功");

        } else {
            DLog.d("初始化失败" );
//            new ToastUtil(mContext).showSingletonToast("初始化检测器失败");
        }
        DLog.d("getAlbumSize: " + faceTrack.getAlbumSize());
    }

    private void initCameraMsg() {
        if (iw == 0) {

            int surface_w = preview_surface.getLayoutParams().width;
            int surface_h = preview_surface.getLayoutParams().height;



            iw = mCameraHelper.getPreviewSize().width;
            ih = mCameraHelper.getPreviewSize().height;


            int orientation = 0;
            ////注意横屏竖屏问题
            DLog.d(getResources().getConfiguration().orientation + " : " + Configuration.ORIENTATION_PORTRAIT);
            if (sw < sh) {
                scale_bit = surface_w / (float) ih;
                if (mCameraHelper.getCameraId() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    orientation = YMFaceTrack.FACE_270;
                } else {
                    orientation = YMFaceTrack.FACE_90;
                }
            } else {
                scale_bit = surface_h / (float) ih;
                orientation = YMFaceTrack.FACE_0;
            }
            if (faceTrack == null) {
                iw = 0;
                return;
            }

            faceTrack.setOrientation(orientation);
            Log.e(TAG,"orientation " + orientation);
            ViewGroup.LayoutParams params = draw_surface.getLayoutParams();
            params.width = surface_w;
            params.height = surface_h;
            draw_surface.requestLayout();
            Log.e(TAG, "跟踪时使用的 iw " + iw + " ih" + ih + "surface_w" + surface_w + "surface_h" + surface_h);
        }
    }

    public int getCameraId() {
        return mCameraHelper.getCameraId();
    }

    //动画处理
    protected void drawAnim(List<YMFace> faces, SurfaceView outputView, float scale_bit, int cameraId, String fps) {
        Log.e(TAG,"drawAnim");

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = outputView.getHolder().lockCanvas();

        if (canvas == null) return;
        try {

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            int viewW = outputView.getLayoutParams().width;
            int viewH = outputView.getLayoutParams().height;
            if (faces == null || faces.size() == 0) return;

            for (int i = 0; i < faces.size(); i++) {
                int size = DisplayUtil.dip2px(mContext, 2);
                paint.setStrokeWidth(size);
                paint.setStyle(Paint.Style.STROKE);
                YMFace ymFace = faces.get(i);
                float[] rect = ymFace.getRect();
                float[] pose = ymFace.getHeadpose();
                float x1  = rect[0] * scale_bit-150;
                float y1 = rect[1] * scale_bit;
                float rect_width = rect[2] * scale_bit;

                //draw rect
                RectF rectf = new RectF(x1, y1, x1 + rect_width, y1 + rect_width);
//                canvas.drawRect(rectf, paint);
                surfaceDraw(rectf,canvas);

            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            outputView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    //数据处理
    protected List<YMFace> analyse(byte[] bytes, int iw, int ih) {
        if (faceTrack == null) return null;
        final List<YMFace> faces = faceTrack.trackMulti(bytes, iw, ih);
        if (faces!=null&&faces.size()>0){
            YMFace ymFace = faces.get(0);

            if (currentStep ==0 && isFrontFace(ymFace)){
                viewCountStep1++;
                Log.e(TAG,"脸的位置测试： 正脸");
                if (viewCountStep1== TOTAL_STEP){
                    currentStep++;
                    mHandler.sendEmptyMessage(1);
                    viewCountStep2 = 0;
                    addFace1(bytes,ymFace.getRect());
                }
            }else if (currentStep ==1 && isSideFace(ymFace)){
                viewCountStep2++;
                if (viewCountStep2== TOTAL_STEP){
                    viewCountStep3 = 0;
                    currentStep++;
                    mHandler.sendEmptyMessage(2);
                }
                Log.e(TAG,"脸的位置测试： 侧脸");
            }else if (currentStep ==2 && isRiseFace(ymFace)){
                viewCountStep3++;
                if (viewCountStep3== TOTAL_STEP){
                    currentStep++;
                    viewCountStep4 = 0;
                    mHandler.sendEmptyMessage(3);

                }
                Log.e(TAG,"脸的位置测试： 抬头");
            }

        }else {
            viewCountStep1 = 0;
            viewCountStep2 = 0;
            viewCountStep3 = 0;
            viewCountStep4 = 0;
        }
       if (currentStep ==3){
            viewCountStep4++;
            Log.e(TAG,"注册完成");
            if (viewCountStep4 == TOTAL_STEP){
                mHandler.sendEmptyMessage(4);
            }
        }
        return faces;
    }

    //判断正脸
    private boolean isFrontFace(YMFace ymFace){
        boolean ret = false;
        float facePose[] = ymFace.getHeadpose();
        float x = facePose[0];
        float y = facePose[1];
        float z = facePose[2];
        if (Math.abs(x)<10&&Math.abs(y)<10&&Math.abs(z)<10)
            ret = true;
        return  ret;
    }

    //判断侧脸
    private boolean isSideFace(YMFace ymFace){
        boolean ret = false;
        float facePose[] = ymFace.getHeadpose();
        float z = facePose[2];
        if (Math.abs(z)>15)
            ret = true;
        return  ret;
    }

    //抬头脸
    private boolean isRiseFace(YMFace ymFace){
        boolean ret = false;
        float facePose[] = ymFace.getHeadpose();
        float y = facePose[1];
        if (y<-15)ret = true;
        return ret;
    }

    //添加人脸

    void addFace1(byte[] bytes, float[] rect) {

        personId = faceTrack.addPerson(0);//添加人脸
        registerUser.setAge(faceTrack.getAge(0)+"");
        registerUser.setGender(faceTrack.getGender(0)+"");

        Log.e(TAG,"起始 年龄" + faceTrack.getAge(0)+"性别 "+faceTrack.getGender(0));


        if (personId > 0) {
            registerUser.setPersonId(personId+"");
            Bitmap image = BitmapUtil.getBitmapFromYuvByte(bytes, iw, ih);
            Bitmap head = Bitmap.createBitmap(image, (int) rect[0], (int) rect[1],
                    (int) rect[2], (int) rect[3], null, true);
            ImageUtils.saveBitmap(mContext.getCacheDir() + "/" + personId + ".jpg",head);

        } else {
            DLog.d("添加人脸失败！");
            Toast.makeText(mContext, "添加人脸失败！请重新添加", Toast.LENGTH_SHORT).show();
        }

    }

    public void removePersonId(String personId) {
        if (faceTrack==null){
            startTrack();
        }
        faceTrack.deletePerson(Integer.parseInt(personId));
        Log.e(TAG,"移除已经注册，成功");
        stopTrack();
    }

    @Override
    public void onDestroy() {//释放
        faceFrame.recycle();
        super.onDestroy();
    }
    Bitmap faceFrame;
    public void surfaceDraw(RectF rect,Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2f);
        mPaint.setTextSize(40f);

        if (canvas != null) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(faceFrame, null, rect, mPaint);
        }
    }
    public void clearDrawSurface() {
        Canvas canvas = draw_surface.getHolder().lockCanvas();
        Paint paint = new Paint(); paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint); paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        draw_surface.invalidate();
        draw_surface.getHolder().unlockCanvasAndPost(canvas);
    }

    public void reInit(){
         currentStep = 0;
         viewCountStep1 = 0;
         viewCountStep2 = 0;
         viewCountStep3 = 0;
         viewCountStep4 = 0;
    }

}
