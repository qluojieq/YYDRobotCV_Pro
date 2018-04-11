package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.lib.WheelView;
import com.yongyida.yydrobotcv.R;
import com.yongyida.yydrobotcv.RegisterActivity;

import java.util.ArrayList;

/**
 * @author Brandon on 2018/3/15
 * update 18/4/10
 **/
public class RegisterVipFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = RegisterVipFragment.class.getSimpleName();
    TextView btnFinish;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_vipinfo,container,false);
//        return super.onCreateView(inflater, container, savedInstanceState);
        WheelView vipChoice = view.findViewById(R.id.vip_choice);
        btnFinish = view.findViewById(R.id.btn_finsih);
        btnFinish.setOnClickListener(this);
        ArrayList <String> arrayList = new ArrayList();
        arrayList.add("VIP0");
        arrayList.add("VIP1");
        arrayList.add("VIP2");

        vipChoice.setAdapter(new ArrayWheelAdapter(arrayList));// 设置"年"的显示数据
        vipChoice.setLabel("");// 添加文字
        vipChoice.setCurrentItem(0);// 初始化时显示的数据
        vipChoice.setGravity(Gravity.CENTER);
        vipChoice.setCyclic(false);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG,"onHiddenChanged"+hidden);
        if (hidden){

        }else {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finsih:
                long ret = ((RegisterActivity)this.getActivity()).doEnd();
                if (ret>0){
                    this.getActivity().finish();
                }else {
                }
                Log.e(TAG,"最后生成 " + ret);
                break;
        }
    }
}
