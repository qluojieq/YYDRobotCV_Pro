package com.yongyida.yydrobotcv.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yongyida.yydrobotcv.R;

/**
 * @author Brandon on 2018/3/15
 **/
public class RegisterBaseInfoFragment extends Fragment {
    private final String TAG = RegisterBaseInfoFragment.class.getSimpleName();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_baseinfo,container,false);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e(TAG,"onHiddenChanged"+hidden);
    }
}
