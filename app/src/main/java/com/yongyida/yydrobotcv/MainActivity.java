package com.yongyida.yydrobotcv;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yongyida.yydrobotcv.customview.SiderBar;
import com.yongyida.yydrobotcv.customview.SiderBar.OnChooseLetterChangedListener;
import com.yongyida.yydrobotcv.service.PersonDetectService;
import com.yongyida.yydrobotcv.useralbum.User;
import com.yongyida.yydrobotcv.useralbum.UserDataSupport;
import com.yongyida.yydrobotcv.utils.ChineseCharacterUtil;

import java.io.File;
import java.util.List;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_SETTLING;

/**
 * @author Brandon on 2018/3/13
 **/
public class MainActivity extends AppCompatActivity implements OnRequestPermissionsResultCallback{

    private static final int BASE_INFO_REQUEST = 10;
    private static final int NEW_ADD_REQUEST = 11;
    public static final String TAG = MainActivity.class.getSimpleName();
    OnChooseLetterChangedListener onChooseLetterChangedListener;
    UserDataSupport dataSupport;
    // Used to load the 'native-lib' library on application startup.
    public static List<User> usersData;

    static {
        System.loadLibrary("native-lib");
    }

    public static Handler mHandler;

    RecyclerView userRecycleView;
    UsersAdapter userDataAdapter;
    SiderBar mSiderBar;
    final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                mSiderBar.setLetters(ChineseCharacterUtil.getFirstChar(usersData.get(msg.what).getUserName()));
//                gridLayoutManager.scrollToPositionWithOffset(scrollString(usersData.get(msg.what).getUaerName()),0);
                return false;
            }
        });
        setContentView(R.layout.activity_main);
        dataSupport =  UserDataSupport.getInstance(this);
        userRecycleView = (RecyclerView) findViewById(R.id.user_recycle);
        mSiderBar = findViewById(R.id.side_bar);
        onChooseLetterChangedListener = new OnChooseLetterChangedListener() {
            @Override
            public void onChooseLetter(String s) {
                gridLayoutManager.scrollToPositionWithOffset(scrollString(s), 0);
            }
        };
        mSiderBar.setOnTouchingLetterChangedListener(onChooseLetterChangedListener);
//        LinearLayoutManager

        userRecycleView.setLayoutManager(gridLayoutManager);
        usersData = dataSupport.getAllUsers("list");
        userDataAdapter = new UsersAdapter(this);
        userDataAdapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e(TAG, "这个位置白点击了 " + position);
                if (position == 0) {
                    addNewUser(null);

                } else {
                    Intent intent = new Intent(MainActivity.this, BaseInfoShowActivity.class);
                    intent.putExtra("one_user", usersData.get(position));
                    startActivityForResult(intent, 10);
                }

            }
        });
        mSiderBar.setRecycleView(userRecycleView);
        userRecycleView.setAdapter(userDataAdapter);
        userRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        Log.e(TAG, "静止");
                        break;
                    case SCROLL_STATE_DRAGGING:
                        Log.e(TAG, "拖动");
                        break;
                    case SCROLL_STATE_SETTLING:
                        Log.e(TAG, "设置");
                        break;


                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition() + 1;//可见范围内的第一项的位置
//                int lastVisibleItemPosition=gridLayoutManager.findLastVisibleItemPosition();//可见范围内的第一项的位置
                if (usersData.size()>9){
                    mSiderBar.setLetters(ChineseCharacterUtil.getFirstChar(usersData.get(firstVisibleItemPosition).getUserName()));
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestCameraPermission();
    }


    //添加按钮
    public void addNewUser(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivityForResult(intent, NEW_ADD_REQUEST);
    }

    public void mainBack(View view) {
        this.finish();
    }

    public static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> implements View.OnClickListener {

        public interface OnItemClickListener {
            void onClick(View view, int position);
        }

        OnItemClickListener mOnItemClickListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
        }

        Context mContext;


        public UsersAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_item, parent, false);
            view.setOnClickListener(this);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TextView textView = holder.itemView.findViewById(R.id.item_name);
            ImageView portraitView = holder.itemView.findViewById(R.id.item_portrait);
            String name = usersData.get(position).getUserName();
            Bitmap bigMap = null;

            if (position == 0) {
                bigMap = BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_add);
                portraitView.setImageBitmap(bigMap);
            } else {

                try {
                    File avaterFile = new File(mContext.getCacheDir() + "/" + usersData.get(position).getPersonId() + ".jpg");
                    if (avaterFile.exists()) {
                        bigMap = BitmapFactory.decodeFile(avaterFile.getAbsolutePath());
                        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(), bigMap);
                        roundedBitmapDrawable.setCircular(true);
                        portraitView.setImageDrawable(roundedBitmapDrawable);
                    }

                } catch (Exception e) {
                }
//            if (position / 2 == 0) {
//                char c = (char) (position / 2 + 65);
//                textView.setText(c + name + position);
//            } else {
//                char c = (char) (position / 2 + 65);
//                textView.setText(c + name + position);
//            }

            }
            textView.setText(name);
            holder.itemView.setTag(position);
        }


        @Override
        public int getItemCount() {
            int size = usersData.size();
            Log.e(TAG, "拥有登记数目 " + size);
            return size;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(v, (int) v.getTag());
            }
        }

        static class MyViewHolder extends RecyclerView.ViewHolder {
            View itemView;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode " + requestCode + "  resultCode" + resultCode);
        switch (requestCode) {
            case BASE_INFO_REQUEST:

                if (resultCode == BaseInfoShowActivity.DELETE_SUCCESS_RESULT_CODE) {
                    Log.e(TAG, "删除成功，更新一下数据" + usersData.size());
                    usersData.clear();
                    usersData = dataSupport.getAllUsers("list");
                    userDataAdapter.notifyDataSetChanged();
                    makeText(this);
                }
                break;
            case NEW_ADD_REQUEST:
                if (resultCode == RegisterActivity.ADD_SUCCESS_RESULT_CODE) {
                    usersData.clear();
                    usersData = dataSupport.getAllUsers("list");
                    userDataAdapter.notifyDataSetChanged();
                    Log.e(TAG, "添加成功，更新一下数据" + usersData.size());
                }
                break;

        }

    }

    //删除成功的自定义Toast
    public void makeText(Context context) {
        Toast customToast = new Toast(context);
        //获得view的布局
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_toast, null);

        //设置textView中的文字
        //设置toast的View,Duration,Gravity最后显示
        customToast.setView(customView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER, 0, 0);
        customToast.show();
    }

    public void testClosePerson() {
        Intent intent = new Intent(this, PersonDetectService.class);
        startService(intent);
    }

    public int scrollString(String targetChar) {
        int ret = 0;
        int i = 0;
        for (; i < usersData.size(); i++) {
            if (targetChar.equals(ChineseCharacterUtil.getFirstChar(usersData.get(i).getUserName()))) {
                break;
            }
        }
        ret = i;
        Log.e(TAG, "点击到啊字母 " + targetChar + "首次出现该字母的位置 " + ret);
        if (usersData.size()>9&&i > usersData.size() - 10) {
            mHandler.sendEmptyMessage(usersData.size() - 10);
            Log.e(TAG, "超出滑动范围 " + usersData.get(usersData.size() - 10).getUserName());
            if (usersData.size() / 2 == 0) {
                ret = usersData.size() - 10;
            } else {
                ret = usersData.size() - 9;
            }

        }
        return ret;
    }

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getSupportFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(getString(R.string.request_permission))
                        .show(getSupportFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }

    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage("权限")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }
}
