package com.example.myapplication.supportutils;

import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class Vibrate {
    public static void vibrator(Context context){
        Vibrator vibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffect;
        //安卓版本号限制
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //定义震动效果
            vibrationEffect = VibrationEffect.createOneShot(30, 8);
            //震动
            vibrator.vibrate(vibrationEffect);
        }
    }
}
