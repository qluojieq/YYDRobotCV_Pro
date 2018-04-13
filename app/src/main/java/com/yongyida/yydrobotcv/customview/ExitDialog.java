package com.yongyida.yydrobotcv.customview;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.yongyida.yydrobotcv.R;

import org.w3c.dom.Text;

/**
 * @author Brandon on 2018/4/12
 **/
public class ExitDialog extends Dialog implements View.OnClickListener {
    TextView confirmBtn;
    TextView cancelBtn;

    public ExitDialog(@NonNull Context context) {
        super(context);
    }

    OnCloseListener listener;

    public ExitDialog(@NonNull Context context, int themeResId, OnCloseListener listener) {
        super(context, themeResId);
        this.listener = listener;
    }

    public ExitDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_item);
        setCanceledOnTouchOutside(false);
        cancelBtn = findViewById(R.id.cancel_close);
        confirmBtn = findViewById(R.id.cancel_confirm);
        confirmBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_close:
                listener.clickCancel();
                break;
            case R.id.cancel_confirm:
                listener.clickConfirm();
                break;

        }

    }

    public interface OnCloseListener {
        void clickConfirm();

        void clickCancel();

    }
}
