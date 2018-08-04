package com.yongyida.yydrobotcv.motion;

import android.content.ContentResolver;
import android.content.Context;

import com.yongyida.robot.communicate.app.common.send.SendClient;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.HeadControl;
import com.yongyida.robot.communicate.app.hardware.motion.send.data.SteeringControl;

/**
 * @author Brandon on 2018/8/2
 * 控制头部运动
 **/
public class HeadHelper {

    private static HeadControl mHeadControl = new HeadControl() ;
    private static SteeringControl mHeadLeftRight = mHeadControl.getHeadLeftRightControl();
    private static SteeringControl mHeadUpDown = mHeadControl.getHeadUpDownControl() ;

    public static int stepLR = 10;
    public static int stepUD = 10;


    public static void headLeft(Context context){
        mHeadControl.setAction(HeadControl.Action.LEFT_RIGHT);
        mHeadLeftRight.setMode(SteeringControl.Mode.DISTANCE_SPEED);
        mHeadLeftRight.getDistance().setType(SteeringControl.Distance.Type.BY);
        mHeadLeftRight.setNegative(true);
        mHeadLeftRight.getDistance().setValue(10*stepLR);

        SendClient.getInstance(context).send(null, mHeadControl, null );
    }

    public static void headRight(Context context){

        mHeadControl.setAction(HeadControl.Action.LEFT_RIGHT);
        mHeadLeftRight.setMode(SteeringControl.Mode.DISTANCE_SPEED);
        mHeadLeftRight.getDistance().setType(SteeringControl.Distance.Type.BY);
        mHeadLeftRight.setNegative(false);
        mHeadLeftRight.getDistance().setValue(10 * stepLR);
        SendClient.getInstance(context).send(null, mHeadControl, null );
    }


    public static void headUp(Context context){

        mHeadControl.setAction(HeadControl.Action.UP_DOWN);
        mHeadUpDown.setMode(SteeringControl.Mode.DISTANCE_SPEED);
        mHeadUpDown.getDistance().setType(SteeringControl.Distance.Type.BY);
        mHeadUpDown.setNegative(true);
        mHeadUpDown.getDistance().setValue(10*stepUD);
        SendClient.getInstance(context).send(null, mHeadControl, null );
    }

    public static void headDown(Context context){

        mHeadControl.setAction(HeadControl.Action.UP_DOWN);
        mHeadUpDown.setMode(SteeringControl.Mode.DISTANCE_SPEED);
        mHeadUpDown.getDistance().setType(SteeringControl.Distance.Type.BY);
        mHeadUpDown.setNegative(false);
        mHeadUpDown.getDistance().setValue(10*stepUD);

        SendClient.getInstance(context).send(null, mHeadControl, null );
    }


    public static  void stopMotin(Context context){
        mHeadControl.setAction(HeadControl.Action.UP_DOWN);
        mHeadUpDown.setMode(SteeringControl.Mode.STOP);

        SendClient.getInstance(context).send(null, mHeadControl, null );

    }
}
