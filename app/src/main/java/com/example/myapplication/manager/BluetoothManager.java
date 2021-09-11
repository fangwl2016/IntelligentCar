package com.example.myapplication.manager;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.bluetoothmodel.GetMessage;
import com.example.myapplication.bluetoothmodel.ToConnectBluetooth;
import com.example.myapplication.supportutils.OpenBluetooth;
import com.example.myapplication.supportutils.Vibrate;

import static com.example.myapplication.MainActivity.socket;
public class BluetoothManager {

    private Activity activity;
    private Context context;

    public BluetoothManager(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public void onClick(int id){
        //震动
        Vibrate.vibrator(context);
        switch (id){
            case 1:
                //flag为1时 表示打开蓝牙成功
                OpenBluetooth openBluetooth = new OpenBluetooth(activity);
                int flag = openBluetooth.openTheBluetooth();
                if(flag == 1){
                    //open bluetooth success
                    ToConnectBluetooth toConnectBluetooth = new ToConnectBluetooth(activity, context);
                    toConnectBluetooth.connectToTheBluetooth();
                }else{
                    //open bluetooth fail
                    Toast.makeText(context, "蓝牙打开失败", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}
