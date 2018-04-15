package com.yongyida.yydrobotcv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.yongyida.yydrobotcv.customview.ExitDialog;
import com.yongyida.yydrobotcv.customview.SiderBar;
import com.yongyida.yydrobotcv.service.PersonDetectService;
import com.yongyida.yydrobotcv.useralbum.User;
import com.yongyida.yydrobotcv.useralbum.UserDataSupport;

import java.io.File;
import java.util.List;

/**
 * @author Brandon on 2018/3/13
 **/
public class MainActivity extends AppCompatActivity {

    private static final int BASE_INFO_REQUEST = 10;
    private static final int NEW_ADD_REQUEST = 11;
    public static final String TAG = MainActivity.class.getSimpleName();
    UserDataSupport dataSupport;
    // Used to load the 'native-lib' library on application startup.
   public static List<User> usersData;

    static {
        System.loadLibrary("native-lib");
    }

    RecyclerView userRecycleView;
    UsersAdapter userDataAdapter;
    SiderBar mSiderBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSupport = new UserDataSupport(this);
        userRecycleView = (RecyclerView) findViewById(R.id.user_recycle);
        mSiderBar = findViewById(R.id.side_bar);
//        LinearLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        userRecycleView.setLayoutManager(gridLayoutManager);
        usersData = dataSupport.getAllUsers();
        userDataAdapter = new UsersAdapter(this);
        userDataAdapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e(TAG, "这个位置白点击了 " + position);
                Intent intent = new Intent(MainActivity.this, BaseInfoShowActivity.class);
                intent.putExtra("one_user",usersData.get(position));
                startActivityForResult(intent,10);
            }
        });
        mSiderBar.setRecycleView(userRecycleView);
        userRecycleView.setAdapter(userDataAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //添加按钮
    public void addNewUser(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivityForResult(intent,NEW_ADD_REQUEST);
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


        public UsersAdapter( Context context) {
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
            String name = usersData.get(position).getUaerName();
            Bitmap bigMap = null;
            try{
                File avaterFile = new File(mContext.getCacheDir() + "/" + usersData.get(position).getPersonId() + ".jpg");
                if(avaterFile.exists()) {
                    bigMap = BitmapFactory.decodeFile(avaterFile.getAbsolutePath());
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(mContext.getResources(),bigMap);
                    roundedBitmapDrawable.setCircular(true);
                    portraitView.setImageDrawable(roundedBitmapDrawable);
                }

            } catch (Exception e) {}
            if (position / 2 == 0) {
                char c = (char) (position / 2 + 65);
                textView.setText(c + name + position);
            } else {
                char c = (char) (position / 2 + 65);
                textView.setText(c + name + position);
            }

            holder.itemView.setTag(position);
        }


        @Override
        public int getItemCount() {
            int size = usersData.size();
            Log.e(TAG,"拥有登记数目 "+size);
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
        Log.e(TAG,"requestCode " + requestCode + "  resultCode" + resultCode);
        switch(requestCode){
            case BASE_INFO_REQUEST:

                if (resultCode==BaseInfoShowActivity.DELETE_SUCCESS_RESULT_CODE){
                    Log.e(TAG,"删除成功，更新一下数据"+usersData.size());
                    usersData.clear();
                    usersData = dataSupport.getAllUsers();
                    userDataAdapter.notifyDataSetChanged();
                    makeText(this);
                }
                break;
            case NEW_ADD_REQUEST:
                if (resultCode==RegisterActivity.ADD_SUCCESS_RESULT_CODE){
                    usersData.clear();
                    usersData = dataSupport.getAllUsers();
                    userDataAdapter.notifyDataSetChanged();
                    Log.e(TAG,"添加成功，更新一下数据"+usersData.size());
                }
                break;

        }

    }

    public  void makeText(Context context) {
        Toast customToast = new Toast(context);
        //获得view的布局
        View customView = LayoutInflater.from(context).inflate(R.layout.custom_toast,null);

        //设置textView中的文字
        //设置toast的View,Duration,Gravity最后显示
        customToast.setView(customView);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.CENTER,0,0);
        customToast.show();
    }

    public void testClosePerson() {
        Intent intent = new Intent(this,PersonDetectService.class);
        startService(intent);
    }
}
