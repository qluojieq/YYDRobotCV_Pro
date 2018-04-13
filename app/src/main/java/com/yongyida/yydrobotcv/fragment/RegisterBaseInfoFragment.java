package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;

import com.bigkoo.pickerview.lib.WheelView;
import com.yongyida.yydrobotcv.R;
import com.yongyida.yydrobotcv.RegisterActivity;
import com.yongyida.yydrobotcv.useralbum.User;

import java.util.ArrayList;

/**
 * @author Brandon on 2018/3/15
 *
 **/
public class RegisterBaseInfoFragment extends Fragment implements View.OnClickListener{

    private final String TAG = RegisterBaseInfoFragment.class.getSimpleName();
    int currentStep = 1;//一共会有四部分，分别注册对应的信息；
    TextView nextStepBtn;
    ImageView stepHintView;
    User registerUser2;
    WheelView genderWheelView;
    LinearLayout genderTableView;
    LinearLayout nameTableView;
    LinearLayout phoneTableView;
    FrameLayout birthdayTableView;

    FragmentManager fm;
    FragmentTransaction ft;

    BirthDayChoiceFragment birthDayChoiceFragment;

    EditText phoneNumView;
    EditText nameView;
    ArrayList<String> genderList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baseinfo,container,false);
        initView(view);
        fm = getFragmentManager();
        ft = fm.beginTransaction();

        birthDayChoiceFragment = new BirthDayChoiceFragment();
        ft.add(R.id.insert_birthday_tap, birthDayChoiceFragment).commit();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG,"onHiddenChanged"+hidden);
        if (hidden){
            currentStep = 1;
        }else {
            currentStep = 1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_info_next:
                Log.e(TAG,"next step is been pressed 当前步数"+currentStep);
                currentStep++;
                switchTable(currentStep);
                break;
        }

    }
    public void initView(View view){
        nextStepBtn = view.findViewById(R.id.btn_info_next);
        stepHintView = view.findViewById(R.id.info_step_hint);
        nextStepBtn.setOnClickListener(this);
        genderWheelView = view.findViewById(R.id.gender_choice);

         genderTableView = view.findViewById(R.id.insert_gender_tap);
         nameTableView = view.findViewById(R.id.insert_name_tap);
         phoneTableView = view.findViewById(R.id.insert_phone_tap);
         birthdayTableView = view.findViewById(R.id.insert_birthday_tap);

        phoneNumView = view.findViewById(R.id.edit_phone);
        nameView = view.findViewById(R.id.edit_name);


        //初始化显示数据
        genderList = new ArrayList();
        genderList.add("男");
        genderList.add("女");
        genderList.add("保密");
        genderWheelView.setAdapter(new ArrayWheelAdapter(genderList));// 设置"年"的显示数据
        genderWheelView.setLabel("");// 添加文字
        genderWheelView.setCurrentItem(0);// 初始化时显示的数据
        genderWheelView.setGravity(Gravity.CENTER);
        genderWheelView.setTextColorCenter(getResources().getColor(R.color.colorTextWrite));
        genderWheelView.setTextSize(16);
        genderWheelView.setCyclic(true);

        registerUser2 = new User();
        registerUser2.setUaerName("Brandon");
    }

    public void switchTable(int position){
        switch (position){
            case 1:
                stepHintView.setImageResource(R.mipmap.info_step_1);
                phoneTableView.setVisibility(View.VISIBLE);
                nameTableView.setVisibility(View.INVISIBLE);
                genderTableView.setVisibility(View.INVISIBLE);
                birthdayTableView.setVisibility(View.INVISIBLE);
                break;
            case 2:
                stepHintView.setImageResource(R.mipmap.info_step_2);
                phoneTableView.setVisibility(View.INVISIBLE);
                nameTableView.setVisibility(View.VISIBLE);
                genderTableView.setVisibility(View.INVISIBLE);
                birthdayTableView.setVisibility(View.INVISIBLE);
                break;
            case 3:
                stepHintView.setImageResource(R.mipmap.info_step_3);
                phoneTableView.setVisibility(View.INVISIBLE);
                nameTableView.setVisibility(View.INVISIBLE);
                genderTableView.setVisibility(View.VISIBLE);
                birthdayTableView.setVisibility(View.INVISIBLE);

                break;
            case 4:
                stepHintView.setImageResource(R.mipmap.info_step_4);
                phoneTableView.setVisibility(View.INVISIBLE);
                nameTableView.setVisibility(View.INVISIBLE);
                genderTableView.setVisibility(View.INVISIBLE);
                birthdayTableView.setVisibility(View.VISIBLE);
                break;
            case 5:
                ((RegisterActivity)this.getActivity()).registerVipRate(null);
                String phoneNum = phoneNumView.getText().toString();
                String nameString = nameView.getText().toString();
                String genderString = genderList.get(genderWheelView.getCurrentItem());
                String birthdayString = birthDayChoiceFragment.getBirthday();
                Log.e(TAG,"电话号码 "+ phoneNum + "名字 "+nameString);
                registerUser2.setPhoneNum(phoneNum);
                registerUser2.setUaerName(nameString);
                registerUser2.setGender(genderString);
                registerUser2.setBirthDay(birthdayString);

                ((RegisterActivity)this.getActivity()).setRegisterUser(registerUser2,2);

                break;
        }
    }

    public void initBirthDayChoice(){

    }
}
