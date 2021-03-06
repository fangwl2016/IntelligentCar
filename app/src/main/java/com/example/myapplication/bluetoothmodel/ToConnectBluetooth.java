package com.example.myapplication.bluetoothmodel;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.Toast;

import java.util.Set;

import static com.example.myapplication.MainActivity.bluetoothAdapter;
import static com.example.myapplication.MainActivity.bluetoothDevice;

public class ToConnectBluetooth {

    private Activity activity;
    private Context context;

    public ToConnectBluetooth(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public void connectToTheBluetooth(){
        //查询配对设备
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        //whether find the device
        boolean findIt = false;

        for(BluetoothDevice device : bondedDevices){
            if(device.getName().contains("BT04-A")){
                //find my device
                findIt = true;
                bluetoothDevice = device;
                Toast.makeText(activity,"找到蓝牙设备，准备连接", Toast.LENGTH_SHORT).show();

                ConnectThread connectThread = new ConnectThread(activity, context);
                connectThread.start();
                break;
            }
        }

        if(!findIt){
            //can not find the device
            Toast.makeText(activity,"未找到蓝牙设备", Toast.LENGTH_SHORT).show();
        }

    }
}
