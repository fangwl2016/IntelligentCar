package com.example.myapplication.supportutils;

import android.app.Activity;
import android.widget.Button;
import android.widget.TextView;
import com.example.myapplication.R;

public class CollectControl {

    private Button connectBtn;
    private TextView connectStatusText;


    /**
     * 注册控件
     */
    public void initControl(Activity activity){
        //connect button
        connectBtn = activity.findViewById(R.id.conntect_btn);
        //connect status text
        connectStatusText = activity.findViewById(R.id.bluetoothStatus_text);

    }


    public TextView getConnectStatusText(){
        return connectStatusText;
    }

    public Button getConnectBtn(){
        return connectBtn;
    }



}
