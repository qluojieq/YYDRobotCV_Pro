package com.yongyida.yydrobotcv.useralbum;
/**
 * @author Brandon on 2018/3/13
 **/
public class User {

    public static final String C_UID = "u_id";
    public static final String C_UN = "u_name";
    public static final String C_UBD = "u_birthday";
    public static final String C_UGD = "u_gender";
    public static final String C_UPN = "u_phone_num";
    public static final String C_UPR = "u_vip_rate";
    public static final String C_UIC = "u_identify_count";
    public static final String C_TAG = "u_tag";
    public static final String TAG = User.class.getSimpleName();
    int userId;
    String uaerName;
    String birthDay;
    String gender;
    String phoneNum;
    String vipRate;
    int identifyCount;
    String tag;

    public static String getTAG() {
        return TAG;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUaerName() {
        return uaerName;
    }

    public void setUaerName(String uaerName) {
        this.uaerName = uaerName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getVipRate() {
        return vipRate;
    }

    public void setVipRate(String vipRate) {
        this.vipRate = vipRate;
    }

    public int getIdentifyCount() {
        return identifyCount;
    }

    public void setIdentifyCount(int identifyCount) {
        this.identifyCount = identifyCount;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
