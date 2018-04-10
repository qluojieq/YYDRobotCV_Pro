package com.yongyida.yydrobotcv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yongyida.yydrobotcv.customview.SiderBar;
import com.yongyida.yydrobotcv.useralbum.User;

import java.util.ArrayList;

/**
 * @author Brandon on 2018/3/13
 **/
public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    // Used to load the 'native-lib' library on application startup.
    ArrayList<User> usersData;
    static {
        System.loadLibrary("native-lib");
    }
    RecyclerView userRecycleView;
    SiderBar mSiderBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRecycleView = (RecyclerView)findViewById(R.id.user_recycle);
        mSiderBar = findViewById(R.id.side_bar);
//        LinearLayoutManager
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2,GridLayoutManager.HORIZONTAL,false);
        userRecycleView.setLayoutManager(gridLayoutManager);
        UsersAdapter userDataAdapter = new UsersAdapter(null,this);

        userDataAdapter.setOnItemClickListener(new UsersAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Log.e(TAG,"这个位置白点击了 "+position);
                Intent intent = new Intent(MainActivity.this,BaseInfoShowActivity.class);
                startActivity(intent);
            }
        });
        mSiderBar.setRecycleView(userRecycleView);
        userRecycleView.setAdapter(userDataAdapter);
    }

    public void addNewUser(View view) {
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    public static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder>implements View.OnClickListener{

        public interface OnItemClickListener{
            void onClick(View view,int position);
        }

        OnItemClickListener mOnItemClickListener = null;
        public void setOnItemClickListener(OnItemClickListener listener){
            mOnItemClickListener = listener;
        }
        Context mContext;
        ArrayList<User> usersData;
        public UsersAdapter(ArrayList<User> usersData, Context context) {
            this.usersData = usersData;
            mContext = context;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_user_item,parent,false);
            view.setOnClickListener(this);
            MyViewHolder holder =   new MyViewHolder(view);
            return holder ;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TextView textView = holder.itemView.findViewById(R.id.item_name);
            if (position/2==0){
                char c = (char) (position/2+65);
                textView.setText(c+"Brandon"+ position);
            }else {
                char c = (char) (position/2+65);
                textView.setText(c+"Brandon"+ position);
            }

            holder.itemView.setTag(position);
        }



        @Override
        public int getItemCount() {
            return 100;
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener!=null){
                mOnItemClickListener.onClick(v,(int)v.getTag());
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
}
