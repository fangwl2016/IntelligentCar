package com.example.myapplication.manager;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.example.myapplication.bluetoothmodel.GetMessage;
import com.example.myapplication.bluetoothmodel.ToConnectBluetooth;
import com.example.myapplication.supportutils.OpenBluetooth;
import com.example.myapplication.supportutils.Vibrate;
import com.example.myapplication.util.Log;

import static com.example.myapplication.MainActivity.socket;

public class BluetoothManager {

    private Activity activity;
    private Context context;

    public BluetoothManager(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public void onClick(int id){
        Log.log("BluetoothManager onClick id:" + id);
        //震动
        Vibrate.vibrator(context);
        byte[] b = new byte[]{'0', '0', '0', '0', '0', '0', '0', '0'};
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

            case 2:
                //flag为2时 表示点击停止按钮
                if(socket != null ){
                    b[0] = 'S';
                    GetMessage.write(b);
                } else {
                    Toast.makeText(context, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;

            case 3:
                //flag为3时，表示点击前进按钮
                if(socket != null ){
                    b[0] = 'F';
                    GetMessage.write(b);
                } else {
                    Toast.makeText(context, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;

            case 4:
                //flag为4时，表示点击后退按钮
                if(socket != null ){
                    b[0] = 'B';
                    GetMessage.write(b);
                } else {
                    Toast.makeText(context, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;

            case 5:
                //flag为5时，表示点击向左按钮
                if(socket != null ){
                    b[0] = 'L';
                    GetMessage.write(b);
                } else {
                    Toast.makeText(context, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;

            case 6:
                //flag为6时，表示点击向右按钮
                if(socket != null ){
                    b[0] = 'R';
                    GetMessage.write(b);
                } else {
                    Toast.makeText(context, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;

            case 7:
                //flag为5时，表示点击提交按钮，提交指令
                if(socket != null ){
                    UiUpdate uiUpdate = new UiUpdate(activity);
                    String sendStr = uiUpdate.getSendText();
                    char[] ch = sendStr.toCharArray();
                    for (int i=0; i<ch.length; i++){
                        if(i == 8) break;
                        b[i] = (byte) ch[i];
                    }
                    Log.log("BluetoothManager onClick case 7, sendStr:" + sendStr + " str byte length:" + ch.length + " b[] length:" + b.length);
                    GetMessage.write(b);
                } else {
                    Toast.makeText(context, "蓝牙未连接", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }
}
