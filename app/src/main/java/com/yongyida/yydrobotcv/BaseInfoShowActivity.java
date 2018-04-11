package com.yongyida.yydrobotcv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yongyida.yydrobotcv.useralbum.User;
import com.yongyida.yydrobotcv.useralbum.UserDataSupport;

public class BaseInfoShowActivity extends AppCompatActivity {

    ImageView portraitImageView;
    TextView vipRateView;
    TextView nameView;
    TextView birthdayView;
    TextView genderView;
    TextView visitedCountView;
    TextView phoneNumView;
    User user;
    UserDataSupport dataSupport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_info_show);
        user = (User) getIntent().getSerializableExtra("one_user");
        dataSupport = new UserDataSupport(this);
        initView();
        initData();

    }

    public void initData(){
        Bitmap bigMap;

        //圆形的头像
        if (user.getHeadPortrait()==null){
               bigMap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_success);
        }else {
            bigMap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_success);
        }
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bigMap);
        roundedBitmapDrawable.setCircular(true);
        portraitImageView.setImageDrawable(roundedBitmapDrawable);
        nameView.setText("姓名："+user.getUaerName());
        genderView.setText("性别："+user.getGender());
        phoneNumView.setText("电话："+user.getPhoneNum());
        birthdayView.setText("生日："+user.getBirthDay());
        visitedCountView.setText("被访问次数："+user.getIdentifyCount());
    }

    public void baseinfoBace(View view) {
        finish();
    }

    public void baseinfoDelete(View view) {
        dataSupport.deleteUser(user.getPersonId());
    }

    public void initView(){
        portraitImageView = findViewById(R.id.base_show_head_img);
        vipRateView = findViewById(R.id.base_show_vip_rate);
        nameView = findViewById(R.id.base_show_name);
        birthdayView = findViewById(R.id.base_show_birthday);
        genderView = findViewById(R.id.base_show_gender);
        visitedCountView = findViewById(R.id.base_show_recognizer_count);
        phoneNumView = findViewById(R.id.base_show_phone);
    }
}
