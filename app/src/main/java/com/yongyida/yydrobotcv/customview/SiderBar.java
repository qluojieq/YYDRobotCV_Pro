package com.yongyida.yydrobotcv.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * @author Brandon on 2018/3/14
 **/
public class SiderBar extends View {

    public static final String TAG = SiderBar.class.getSimpleName();

    private Paint paint = new Paint();

    private int choose = -1;

    private boolean showBackground;

    public void setRecycleView(RecyclerView mRecycleView) {
        this.recycleView = mRecycleView;
    }

    RecyclerView recycleView;

    public static String[] letters = {"#", "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private OnChooseLetterChangedListener onChooseLetterChangedListener;

    public SiderBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SiderBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SiderBar(Context context) {
        super(context);
    }

    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (showBackground) {
            canvas.drawColor(Color.parseColor("#fff7fff8"));
        }
        int height = getHeight()/2+12;
        int width = getWidth();
        //平均每个字母占的高度
        int singleWidth = width / letters.length;
        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setTextSize(25);
            int y = height;
            int x = singleWidth * i + singleWidth/2;
            if (i == choose) {
                //画text所占的区域
                Paint.FontMetricsInt fm = paint.getFontMetricsInt();
                int top = y + fm.top;
                int bottom = y + fm.bottom;
                int width1 = (int)paint.measureText(letters[i]);
                Rect rect = new Rect(x,top,x+width1,bottom);
                paint.setColor(Color.BLUE);
                canvas.drawCircle(x+width1/2,y-width1/2,20,paint);
                paint.setColor(Color.parseColor("#ff07fff8"));
                recycleView.scrollToPosition(i);
                paint.setFakeBoldText(true);
            }

            canvas.drawText(letters[i], x, y, paint);

            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        int oldChoose = choose;
        int c = (int) (x / getWidth() * letters.length);
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                showBackground = true;
                if (oldChoose != c ) {
                    if (c > -1 && c < letters.length) {
                        Log.e(TAG,"ACTION_DOWN"+c);
                        if (onChooseLetterChangedListener != null)
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                    }

                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (oldChoose != c) {
                    if (c > -1 && c < letters.length) {
                        Log.e(TAG,"ACTION_MOVE"+c);
                        if (onChooseLetterChangedListener != null)
                        onChooseLetterChangedListener.onChooseLetter(letters[c]);
                        choose = c;
                        invalidate();
                    }

                }
                break;
//            case MotionEvent.ACTION_UP:
//                Log.e(TAG,"ACTION_UP"+c);
//                showBackground = false;
//                choose = -1;
//                if (onChooseLetterChangedListener != null) {
//                    onChooseLetterChangedListener.onNoChooseLetter();
//                }
//                invalidate();
//                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(OnChooseLetterChangedListener onChooseLetterChangedListener) {
        this.onChooseLetterChangedListener = onChooseLetterChangedListener;
    }

    public interface OnChooseLetterChangedListener {

        void onChooseLetter(String s);

    }

    public void setLetters(String s){
        for (int i = 0;i<letters.length;i++){
            if (s.equals(letters[i])){
                choose=i;
                break;
            }
        }
        invalidate();
    }

}


