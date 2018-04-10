package com.yongyida.yydrobotcv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BaseInfoShowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_info_show);
    }

    public void baseinfoBace(View view) {
        finish();
    }

    public void baseinfoDelete(View view) {
    }
}
