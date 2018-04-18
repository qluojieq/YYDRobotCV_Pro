package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;

import com.bigkoo.pickerview.lib.WheelView;
import com.yongyida.yydrobotcv.R;
import com.yongyida.yydrobotcv.RegisterActivity;
import com.yongyida.yydrobotcv.useralbum.User;
import com.yongyida.yydrobotcv.utils.CommonUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dou.utils.ToastUtil;

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

    TextView stepHint1;
    TextView stepHint2;
    TextView stepHint3;
    TextView stepHint4;

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
                switch (msg.what) {
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
            saveData();//隐藏的时候保存一下信息
        } else {
//            currentStep = 1;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_info_next:
                Log.e(TAG, "next step is been pressed 当前步数" + currentStep);
                if (currentStep == 2) {
                    if (TextUtils.isEmpty(nameView.getText())){
                        new ToastUtil(this.getActivity()).showSingletonToast("名字不能空缺 ");
                        return;
                    }else if (!CommonUtils.isMatch(nameView.getText().toString(),regName)){
                        new ToastUtil(this.getActivity()).showSingletonToast("名字不符合规则");
                        return;
                    }
                }
                if (currentStep<5){
                    currentStep++;
                }
                switchTable(currentStep);
                break;
            case R.id.hint_info1:
                currentStep = 1;
                switchTable(currentStep);
                break;
            case R.id.hint_info2:
                currentStep = 2;
                switchTable(currentStep);
                break;
            case R.id.hint_info3:
                currentStep = 3;
                switchTable(currentStep);
                break;
            case R.id.hint_info4:
                currentStep = 4;
                switchTable(currentStep);
                break;
        }

    }

    public void initView(View view) {

        stepHint1 = view.findViewById(R.id.hint_info1);
        stepHint1.setOnClickListener(this);
        stepHint2 = view.findViewById(R.id.hint_info2);
        stepHint2.setOnClickListener(this);
        stepHint3 = view.findViewById(R.id.hint_info3);
        stepHint3.setOnClickListener(this);
        stepHint4 = view.findViewById(R.id.hint_info4);
        stepHint4.setOnClickListener(this);

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
                boolean isMatch = CommonUtils.isMatch(s.toString(), regPhonNum);
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
        phoneNumView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    Log.e(TAG, "actionId " + actionId + "event " + EditorInfo.IME_ACTION_NEXT);
                    nextStepBtn.performClick();
                }

                return true;
            }
        });
        nameView = view.findViewById(R.id.edit_name);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isMatch = CommonUtils.isMatch(s.toString(), regName);
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
        nameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null
                        && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {
                    Log.e(TAG, "actionId " + actionId + "event " + EditorInfo.IME_ACTION_NEXT);
                    nextStepBtn.performClick();
                    if (!TextUtils.isEmpty(nameView.getText())&&CommonUtils.isMatch(nameView.getText().toString(),regName)){
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInputFromWindow(v.getWindowToken(), 0, 0);
                    }
                }
                return true;
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
        Log.e(TAG, "从activity中获取的 年龄" + registerUser2.getAge() + " 年龄" + registerUser2.getGender());
        if (TextUtils.isEmpty(registerUser2.getGender())) {//初始化男女
            genderWheelView.setCurrentItem(0);//
        } else {
            if (registerUser2.getGender().equals("-1")) {
                genderWheelView.setCurrentItem(2);//
            } else {
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
                phoneNumView.setFocusable(true);
                phoneNumView.setFocusableInTouchMode(true);
                phoneNumView.requestFocus();
                nameTableView.setVisibility(View.INVISIBLE);
                genderTableView.setVisibility(View.INVISIBLE);
                birthdayTableView.setVisibility(View.INVISIBLE);
                break;
            case 2:
                stepHintView.setImageResource(R.mipmap.info_step_2);
                phoneTableView.setVisibility(View.INVISIBLE);
                nameTableView.setVisibility(View.VISIBLE);
                genderTableView.setVisibility(View.INVISIBLE);
                nameView.setFocusable(true);
                nameView.setFocusableInTouchMode(true);
                nameView.requestFocus();
                birthdayTableView.setVisibility(View.INVISIBLE);
                break;
            case 3:
                stepHintView.setImageResource(R.mipmap.info_step_3);
                phoneTableView.setVisibility(View.INVISIBLE);
                nameTableView.setVisibility(View.INVISIBLE);
                genderTableView.setVisibility(View.VISIBLE);
                birthdayTableView.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(registerUser2.getAge())) {
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

                break;
        }
    }

    public void saveData(){
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
    }


}
