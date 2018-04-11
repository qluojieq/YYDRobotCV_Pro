package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yongyida.yydrobotcv.R;
import com.yongyida.yydrobotcv.RegisterActivity;
import com.yongyida.yydrobotcv.useralbum.User;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baseinfo,container,false);
        registerUser2 = new User();
        registerUser2.setUaerName("Brandon");
        nextStepBtn = view.findViewById(R.id.btn_info_next);
        stepHintView = view.findViewById(R.id.info_step_hint);
        nextStepBtn.setOnClickListener(this);
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

                switch (currentStep){
                    case 1:
                        currentStep++;
                        stepHintView.setImageResource(R.mipmap.info_step_2);
                        break;
                    case 2:
                        currentStep++;
                        stepHintView.setImageResource(R.mipmap.info_step_3);
                        break;
                    case 3:
                        currentStep++;
                        stepHintView.setImageResource(R.mipmap.info_step_4);
                        break;
                    case 4:
                        ((RegisterActivity)RegisterBaseInfoFragment.this.getActivity()).registerVipRate(null);
                        ((RegisterActivity)RegisterBaseInfoFragment.this.getActivity()).setRegisterUser(registerUser2,2);
                        break;
                }

                break;
        }

    }
}
