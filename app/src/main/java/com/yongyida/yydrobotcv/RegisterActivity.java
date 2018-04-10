package com.yongyida.yydrobotcv;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.yongyida.yydrobotcv.fragment.RegisterBaseInfoFragment;
import com.yongyida.yydrobotcv.fragment.RegisterCameraFragment;
import com.yongyida.yydrobotcv.fragment.RegisterVipFragment;

public class RegisterActivity extends AppCompatActivity {
    FrameLayout registerFrame;
    FragmentManager fm;
    FragmentTransaction ft;
    RegisterVipFragment rVipInfoFrame;
    RegisterCameraFragment rCameraInfoFrame;
    RegisterBaseInfoFragment rBaseInfoFrame ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        registerFrame = findViewById(R.id.register_frame);

        registerFrame.removeAllViews();
        fm = getFragmentManager();

        rVipInfoFrame = new RegisterVipFragment();
        rCameraInfoFrame = new RegisterCameraFragment();
        rBaseInfoFrame = new RegisterBaseInfoFragment();

        registerCamera(null);
    }

    public void registerVipRate(View view) {
        ft = fm.beginTransaction();
        if (!rCameraInfoFrame.isHidden()){
            ft.hide(rCameraInfoFrame);
        }
        if (!rBaseInfoFrame.isHidden()){
            ft.hide(rBaseInfoFrame);
        }

        if (rVipInfoFrame.isAdded()){
            ft.show(rVipInfoFrame).commit();
        }else {
            ft.add(R.id.register_frame,rVipInfoFrame).show(rVipInfoFrame).commit();
        }
    }

    public void registerBack(View view) {
        this.finish();
    }

    public void registerCamera(View view) {
        ft = fm.beginTransaction();
        if (!rBaseInfoFrame.isHidden()){
            ft.hide(rBaseInfoFrame);
        }
        if (!rVipInfoFrame.isHidden()){
            ft.hide(rVipInfoFrame);
        }

        if (rCameraInfoFrame.isAdded()){
            ft.show(rCameraInfoFrame).commit();
        }else {
            ft.add(R.id.register_frame,rCameraInfoFrame).show(rCameraInfoFrame).commit();
        }
    }

    public void registerBaseInfo(View view) {
        ft = fm.beginTransaction();

        if (!rCameraInfoFrame.isHidden()){
            ft.hide(rCameraInfoFrame);
        }
        if (!rVipInfoFrame.isHidden()){
            ft.hide(rVipInfoFrame);
        }

        if (rBaseInfoFrame.isAdded()){
            ft.show(rBaseInfoFrame).commit();
        }else {
            ft.add(R.id.register_frame,rBaseInfoFrame).show(rBaseInfoFrame).commit();
        }
    }

}
