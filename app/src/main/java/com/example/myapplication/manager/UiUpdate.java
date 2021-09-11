package com.example.myapplication.manager;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.supportutils.CollectControl;

import java.text.DecimalFormat;

public class UiUpdate {
    private Activity activity;

    public UiUpdate(Activity activity){
        this.activity = activity;
    }

    public void showReceiveText(final String s){
        //must in the main thread to update ui
        //change to main thread
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                CollectControl collectControl = new CollectControl();
                collectControl.initControl(activity);
                //get receiveText control
                EditText textReceive = collectControl.getTextReceive();
                textReceive.setText(s);

            }
        });
    }

    public String getSendText(){
        CollectControl collectControl = new CollectControl();
        EditText textSend = collectControl.getTextSend();
        return textSend.getText().toString();
    }

}
