package com.yongyida.yydrobotcv.useralbum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brandon on 2018/3/13
 * update 18/4/10
 **/
public class UserDataSupport {
    public static final String TAG = UserDataSupport.class.getSimpleName();
    UserDataHelper userHelper;
    SQLiteDatabase database;
    private String[] allColumns = {
            UserDataHelper.C_ID,
            UserDataHelper.C_ID_PERSON,
            UserDataHelper.C_UN,
            UserDataHelper.C_UBD,
            UserDataHelper.C_UGD,
            UserDataHelper.C_UPN,
            UserDataHelper.C_UPR,
            UserDataHelper.C_HEAD,
            UserDataHelper.C_UIC,
            UserDataHelper.C_TAG
    };

    public UserDataSupport(Context context) {
        userHelper = new UserDataHelper(context);
    }

    private void open() {
        database = userHelper.getWritableDatabase();
    }
    private void close() {
        userHelper.close();
    }


    //获取全部用户
    public List<User> getAllUsers() {
        open();
        List<User> allUsers = new ArrayList<>();
        Cursor cursor = database.query(UserDataHelper.DATABASE_TABLE, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            User user = new User();
            user.setUserId(cursor.getString(0));
            user.setPersonId(cursor.getString(1));
            user.setUaerName(cursor.getString(2));
            user.setBirthDay(cursor.getString(3));
            user.setGender(cursor.getString(4));
            user.setPhoneNum(cursor.getString(5));
            user.setVipRate(cursor.getString(6));
            user.setHeadPortrait(cursor.getString(7));
            user.setIdentifyCount(cursor.getString(8));
            user.setTag(cursor.getString(9));
            allUsers.add(user);
            cursor.moveToNext();
        }
        Log.e(TAG,allUsers.size() + " get all user success " + cursor.getCount());
        cursor.close();
        close();
        return allUsers;
    }

    //插入一个用户
    public long insertUser(User user) {
        open();
        ContentValues values = new ContentValues();
        values.put(allColumns[0], user.getUserId());
        values.put(allColumns[1], user.getPersonId());
        values.put(allColumns[2], user.getUaerName());
        values.put(allColumns[3], user.getBirthDay());
        values.put(allColumns[4], user.getGender());
        values.put(allColumns[5], user.getPhoneNum());
        values.put(allColumns[6], user.getVipRate());
        values.put(allColumns[7], user.getHeadPortrait());
        values.put(allColumns[8], user.getIdentifyCount());
        values.put(allColumns[9], user.getTag());
        Long insertCount = database.insert(UserDataHelper.DATABASE_TABLE, null, values);
        close();
        return insertCount;
    }

    //更新访问次数
    public long updateIdentifyCount(String personId) {
        open();
        Cursor lastCursor = database.query(UserDataHelper.DATABASE_TABLE, new String[]{UserDataHelper.C_UIC}, UserDataHelper.C_ID_PERSON + "= ?", new String[] {personId}, null, null, null);
        int lastCount = Integer.getInteger(lastCursor.getString(0));
        lastCount++;
        ContentValues contentValues = new ContentValues();
        contentValues.put(allColumns[1], lastCount);
        long ret = database.update(UserDataHelper.DATABASE_TABLE, contentValues, UserDataHelper.C_ID_PERSON + " = ?" , new String[]{personId});
        close();
        return ret;
    }

    //删除用户
    public long deleteUser(String personId) {
        long ret = -1;
        open();
        ret = database.delete(UserDataHelper.DATABASE_TABLE, UserDataHelper.C_ID_PERSON + "= ?" + personId, new String [] { personId});
        close();
        return ret;
    }

    //获取单个用户
    public User getUser(String personId){
        User user = new User();
        Cursor cursor = database.query(UserDataHelper.DATABASE_TABLE, allColumns, UserDataHelper.C_ID_PERSON + "= ?", new String[] {personId}, null, null, null);
       if (cursor.getCount()==1){
           cursor.moveToFirst();
           user.setUserId(cursor.getString(0));
           user.setPersonId(cursor.getString(1));
           user.setUaerName(cursor.getString(2));
           user.setBirthDay(cursor.getString(3));
           user.setGender(cursor.getString(4));
           user.setPhoneNum(cursor.getString(5));
           user.setVipRate(cursor.getString(6));
           user.setHeadPortrait(cursor.getString(7));
           user.setIdentifyCount(cursor.getString(8));
           user.setTag(cursor.getString(9));
       }
        return  user;
    }

}
