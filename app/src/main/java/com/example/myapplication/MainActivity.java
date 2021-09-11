package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.manager.BluetoothManager;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public BluetoothManager bluetoothManager = null;
    public static BluetoothAdapter bluetoothAdapter = null;
    public static BluetoothDevice bluetoothDevice = null;
    public static BluetoothSocket socket = null;

    Button connectBtn;
    /**
     * 计算两次返回时时间间隔
     */
    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //实例化manager
        bluetoothManager = new BluetoothManager(this, this);

        //连接蓝牙
        connectBtn = findViewById(R.id.conntect_btn);
        connectBtn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("MainActivity", e.toString());
            }
        }
        //退出时关闭蓝牙
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.disable();
        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            //点击连接蓝牙
            case R.id.conntect_btn:
                bluetoothManager.onClick(1);
                break;

            default:
                break;
        }

    }

    /**
     *按返回键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit(){
        final int time = 2000;
        if(System.currentTimeMillis() - exitTime > time){
            Toast.makeText(this, "再次返回退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }else{
            finish();
        }
    }

}
