package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.manager.BluetoothManager;
import com.example.myapplication.supportutils.Vibrate;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public BluetoothManager bluetoothManager = null;
    public static BluetoothAdapter bluetoothAdapter = null;
    public static BluetoothDevice bluetoothDevice = null;
    public static BluetoothSocket socket = null;
    private static String positionURL = "http://10.220.57.142:8080/drive/position";
    private static String actionURL = "http://10.220.57.142:8080/drive/action?x=3&y=0&dir=N";
    private static ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();

    public static void action() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(positionURL).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            response.body().byteStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduledService.scheduleAtFixedRate(()-> {
            try {
                Request req = new Request.Builder().url(actionURL).build();
                Call call1 = client.newCall(req);
                Response res = call1.execute();
                byte[] buf = new byte[1024];
                res.body().byteStream().read(buf);
                System.out.println(new String(buf));
                res.body().byteStream().close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    Button connectBtn;
    Button stopBtn;
    Button frontBtn;
    Button backBtn;
    Button leftBtn;
    Button rightBtn;
    EditText sendMessageText;
    Button sendBtn;

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

        //监听指令
        stopBtn = findViewById(R.id.stop_button);
        stopBtn.setOnClickListener(this);
        frontBtn = findViewById(R.id.front_button);
        frontBtn.setOnClickListener(this);
        backBtn = findViewById(R.id.back_button);
        backBtn.setOnClickListener(this);
        leftBtn = findViewById(R.id.left_button);
        leftBtn.setOnClickListener(this);
        rightBtn = findViewById(R.id.right_button);
        rightBtn.setOnClickListener(this);

        //发送消息编辑框
        sendMessageText = findViewById(R.id.text_send);
        sendMessageText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager manager = ((InputMethodManager)getSystemService
                        (Context.INPUT_METHOD_SERVICE));

                if(hasFocus){//获得焦点
                    //震动
                    Vibrate.vibrator(MainActivity.this);
                    //文本清空
                    sendMessageText.setText("");
                    //开软键盘
                    if(manager != null){
                        manager.showSoftInput(v,0);
                    }
                }
                else{//失去焦点
                    if(manager != null){
                        //收起键盘
                        manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                }
            }
        });

        //发送确定
        sendBtn = findViewById(R.id.send_butten);
        sendBtn.setOnClickListener(this);
        action();
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

            case R.id.stop_button:
                bluetoothManager.onClick(2);
                break;

            case R.id.front_button:
                bluetoothManager.onClick(3);
                break;

            case R.id.back_button:
                bluetoothManager.onClick(4);
                break;

            case R.id.left_button:
                bluetoothManager.onClick(5);
                break;

            case R.id.right_button:
                bluetoothManager.onClick(6);
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
