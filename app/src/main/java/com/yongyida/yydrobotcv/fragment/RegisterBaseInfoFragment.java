package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brandon on 2018/3/15
 **/
public class RegisterBaseInfoFragment extends Fragment implements View.OnClickListener {

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

    TextView warnTextName;
    TextView warnTextPhone;

    FragmentManager fm;
    FragmentTransaction ft;

    BirthDayChoiceFragment birthDayChoiceFragment;

    EditText phoneNumView;
    EditText nameView;
    ArrayList<String> genderList;

    public static Handler mHandler;

    //    private  String regPhonNum = "^1[3|4|5|7|8][0-9]\\d{4,8}$";
    private String regPhonNum = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";

    private String regName = "^[(a-zA-Z0-9\\u4e00-\\u9fa5)]{1,8}$";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baseinfo, container, false);
        initView(view);
        fm = getFragmentManager();
        ft = fm.beginTransaction();

        birthDayChoiceFragment = new BirthDayChoiceFragment();
        ft.add(R.id.insert_birthday_tap, birthDayChoiceFragment).commit();
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        warnTextPhone.setVisibility(View.GONE);
                        break;
                    case 2:
                        warnTextPhone.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        Log.e(TAG, "符合条件");
                        warnTextName.setVisibility(View.GONE);
                        break;
                    case 4:
                        Log.e(TAG, "不符合条件");
                        warnTextName.setVisibility(View.VISIBLE);
                        break;

                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG, "onHiddenChanged" + hidden);
        if (hidden) {
            currentStep = 1;
        } else {
            currentStep = 1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_info_next:
                Log.e(TAG, "next step is been pressed 当前步数" + currentStep);
                currentStep++;
                switchTable(currentStep);
                break;
        }

    }

    public void initView(View view) {
        nextStepBtn = view.findViewById(R.id.btn_info_next);
        stepHintView = view.findViewById(R.id.info_step_hint);
        nextStepBtn.setOnClickListener(this);
        genderWheelView = view.findViewById(R.id.gender_choice);
        warnTextName = view.findViewById(R.id.input_warn_name);
        warnTextPhone = view.findViewById(R.id.input_warn_phone);

        genderTableView = view.findViewById(R.id.insert_gender_tap);
        nameTableView = view.findViewById(R.id.insert_name_tap);
        phoneTableView = view.findViewById(R.id.insert_phone_tap);
        birthdayTableView = view.findViewById(R.id.insert_birthday_tap);

        phoneNumView = view.findViewById(R.id.edit_phone);
        phoneNumView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = Pattern.compile(regPhonNum);
                Matcher m = p.matcher(s);
                boolean isMatch = m.matches();
                if (isMatch) {
                    mHandler.sendEmptyMessage(1);
                } else {

                    mHandler.sendEmptyMessage(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        nameView = view.findViewById(R.id.edit_name);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Pattern p = Pattern.compile(regName);
                Matcher m = p.matcher(s);
                boolean isMatch = m.matches();
                if (isMatch) {
                    mHandler.sendEmptyMessage(3);
                } else {
                    mHandler.sendEmptyMessage(4);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerUser2 = ((RegisterActivity) this.getActivity()).getRegisterUser();
        //初始化显示数据
        genderList = new ArrayList();
        genderList.add("保密");
        genderList.add("男");
        genderList.add("女");
        genderWheelView.setAdapter(new ArrayWheelAdapter(genderList));// 设置"年"的显示数据
        genderWheelView.setLabel("");// 添加文字
        Log.e(TAG,"从activity中获取的 年龄" + registerUser2.getAge() + " 年龄" + registerUser2.getGender() );
        if (TextUtils.isEmpty(registerUser2.getGender())){//初始化男女
            genderWheelView.setCurrentItem(0);//
        }else {
            if (registerUser2.getGender().equals("-1")){
                genderWheelView.setCurrentItem(2);//
            }else {
                genderWheelView.setCurrentItem(1);//
            }
        }
        genderWheelView.setGravity(Gravity.CENTER);
        genderWheelView.setTextColorCenter(getResources().getColor(R.color.colorTextWrite));
        genderWheelView.setTextSize(16);
        genderWheelView.setCyclic(true);


        registerUser2.setUaerName("Brandon");
    }

    public void switchTable(int position) {
        switch (position) {
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
                if (!TextUtils.isEmpty(registerUser2.getAge())){
                    birthDayChoiceFragment.setCurrentDate(registerUser2.getAge());
                }

                break;
            case 4:
                stepHintView.setImageResource(R.mipmap.info_step_4);
                phoneTableView.setVisibility(View.INVISIBLE);
                nameTableView.setVisibility(View.INVISIBLE);
                genderTableView.setVisibility(View.INVISIBLE);
                birthdayTableView.setVisibility(View.VISIBLE);
                break;
            case 5:
                ((RegisterActivity) this.getActivity()).registerVipRate(null);
                String phoneNum = phoneNumView.getText().toString();
                String nameString = nameView.getText().toString();
                String genderString = genderList.get(genderWheelView.getCurrentItem());
                String birthdayString = birthDayChoiceFragment.getBirthday();
                Log.e(TAG, "电话号码 " + phoneNum + "名字 " + nameString);
                registerUser2.setPhoneNum(phoneNum);
                registerUser2.setUaerName(nameString);
                registerUser2.setGender(genderString);
                registerUser2.setBirthDay(birthdayString);

                ((RegisterActivity) this.getActivity()).setRegisterUser(registerUser2, 2);

                break;
        }
    }

    public void initBirthDayChoice() {

    }
}
