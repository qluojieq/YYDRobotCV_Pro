package com.yongyida.yydrobotcv;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.yongyida.yydrobotcv.fragment.RegisterBaseInfoFragment;
import com.yongyida.yydrobotcv.fragment.RegisterCameraFragment;
import com.yongyida.yydrobotcv.fragment.RegisterVipFragment;
import com.yongyida.yydrobotcv.useralbum.User;
import com.yongyida.yydrobotcv.useralbum.UserDataSupport;

public class RegisterActivity extends FragmentActivity {

    FrameLayout registerFrame;
    FragmentManager fm;
    FragmentTransaction ft;
    RegisterVipFragment rVipInfoFrame;
    RegisterCameraFragment rCameraInfoFrame;
    RegisterBaseInfoFragment rBaseInfoFrame ;
    User registerUser;
    int currentStep = 0;
    UserDataSupport userDataSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_main);
        registerFrame = findViewById(R.id.register_frame);
        registerFrame.removeAllViews();
        fm = getFragmentManager();
        registerUser = new User();
        registerUser.setUaerName("Brandon");//设定默认值
        registerUser.setPersonId("999");
        userDataSupport = new UserDataSupport(this);
        rVipInfoFrame = new RegisterVipFragment();
        rCameraInfoFrame = new RegisterCameraFragment();
        rBaseInfoFrame = new RegisterBaseInfoFragment();
        registerCamera(null);
    }

    public void registerBack(View view) {
        this.finish();
    }

    //跳转到录入fragment
    public void registerCamera(View view) {
        currentStep = 1;
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
    //跳转到基础信息录入fragment
    public void registerBaseInfo(View view) {
        currentStep = 2;
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

    //跳转到vip信息录入界面
    public void registerVipRate(View view) {
        currentStep = 3;
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

    public User getRegisterUser() {
        return registerUser;
    }

    //每一步完成不同的信息录入
    public void setRegisterUser(User registerUser,int step) {
        switch (step){
            case 1:
                this.registerUser.setPersonId(registerUser.getPersonId());
                break;
            case 2:
                this.registerUser.setUaerName( registerUser.getUaerName());
                this.registerUser.setGender(registerUser.getGender());
                this.registerUser.setPhoneNum(registerUser.getPhoneNum());
                this.registerUser.setBirthDay(registerUser.getBirthDay());
                break;
            case 3:
                this.registerUser.setVipRate(registerUser.getVipRate());
                break;
        }
    }


    //最后注册到数据库
    public long doEnd(){

        long ret = userDataSupport.insertUser(registerUser);

        return ret;
    }
}
