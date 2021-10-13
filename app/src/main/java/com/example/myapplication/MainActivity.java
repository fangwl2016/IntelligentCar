package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.manager.BluetoothManager;
import com.example.myapplication.supportutils.Vibrate;
import com.giftedcat.cameratakelib.CameraTakeManager;
import com.giftedcat.cameratakelib.listener.CameraTakeListener;
import com.giftedcat.cameratakelib.utils.LogUtil;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.myapplication.Utils.assetFilePath;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //拍照
    Unbinder unbinder;
    private Context mContext;
    /** 权限相关*/
    private static final int GETPERMS = 100;
    private String[] perms;
    private Handler permissionsHandler = new Handler();
    CameraTakeManager manager;

    public BluetoothManager bluetoothManager = null;
    public static BluetoothAdapter bluetoothAdapter = null;
    public static BluetoothDevice bluetoothDevice = null;
    public static BluetoothSocket socket = null;
    private static String positionURL = "http://10.220.57.142:8080/drive/position";
    private static String actionURL = "http://10.220.57.142:8080/drive/action?x=3&y=0&dir=N";
    private static ScheduledExecutorService scheduledService = Executors.newSingleThreadScheduledExecutor();

    public static void action() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(positionURL).build();
                Call call = client.newCall(request);
                try {
                    Response response = call.execute();
                    response.body().byteStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                scheduledService.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request req = new Request.Builder().url(actionURL).build();
                            Call call1 = client.newCall(req);
                            Response res = call1.execute();
                            byte[] buf = new byte[1024];
                            int n = res.body().byteStream().read(buf);
                            System.out.println(new String(buf, 0, n));
                            res.body().byteStream().close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 0, 1, TimeUnit.SECONDS);
            }
        }).start();
    }

    Button connectBtn;
    Button stopBtn;
    Button frontBtn;
    Button backBtn;
    Button leftBtn;
    Button rightBtn;
    EditText sendMessageText;
    Button sendBtn;
    SurfaceView previewView;
    ImageView imgPic;
    TextView tvPicDir;
    Button takePhotoBtn;

    /**
     * 计算两次返回时时间间隔
     */
    private long exitTime = 0;

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        action();

        //实例化manager
        bluetoothManager = new BluetoothManager(this, this);

        //监听事件
        connectBtn = findViewById(R.id.conntect_btn);
        connectBtn.setOnClickListener(this);
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
        previewView = findViewById(R.id.surfaceview);
        imgPic = findViewById(R.id.img_pic);
        tvPicDir = findViewById(R.id.tv_pic_dir);
        takePhotoBtn = findViewById(R.id.btn_take_photo);
        takePhotoBtn.setOnClickListener(this);

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

        //发送按钮
        sendBtn = findViewById(R.id.send_butten);
        sendBtn.setOnClickListener(this);
        //加载模型
        loadModel();

        unbinder = ButterKnife.bind(this);
        mContext = this;
        perms = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        checkPermission();
        startAutoTakePhotoThread();

    }

    public void checkPermission() {
        //判断是否有相关权限，并申请权限
        if (EasyPermissions.hasPermissions(mContext, perms)) {
            permissionsHandler.post(new Runnable() {
                @Override
                public void run() {
                    init();
                }
            });
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, perms, GETPERMS);
        }
    }

    public void startAutoTakePhotoThread(){
        new Thread(){
            public void run(){
                LogUtil.d("启动拍照线程");
                while(true){
                    try {
                        sleep(5000);
                        manager.takePhoto();
                    }catch (Exception e){
                        LogUtil.e("发生错误",e);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        init();
    }

    private void init(){
        manager = new CameraTakeManager(this, previewView, new CameraTakeListener() {
            @Override
            public void onSuccess(File bitmapFile, Bitmap mBitmap) {
                imgPic.setImageBitmap(mBitmap);
                tvPicDir.setText("图片路径：" + bitmapFile.getPath());
            }

            @Override
            public void onFail(String error) {
                LogUtil.e(error);
            }
        });
    }

    private void loadModel() {
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        Bitmap bitmap= null;
        Module module = null;
        try {

            // 1. 获取图片
            bitmap = BitmapFactory.decodeStream(getAssets().open("image.jpg"));
            imageView.setImageBitmap(bitmap);
            // 2. 加载模型
            module =Module.load(assetFilePath(this, "trafficLights.pt"));
        } catch (IOException e) {

            e.printStackTrace();
            finish();
        }
        // 3. bitmap -> Tensor
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

        // 4. 运行模型
        Tensor resultTensor = module.forward(IValue.from(inputTensor)).toTensor();

        // 5. 解析结果
        final float[] scores = resultTensor.getDataAsFloatArray();
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {

            if (scores[i] > maxScore) {

                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        String className = Constants.IMAGENET_CLASSES[maxScoreIdx];
        Log.i("className", className);
        textView.setText("红绿灯类别为" + className);


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
//        //退出时关闭蓝牙
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if(bluetoothAdapter.isEnabled()){
//            bluetoothAdapter.disable();
//        }
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            //点击连接蓝牙
            case R.id.conntect_btn:
                bluetoothManager.onClick(1);
                break;

            //点击停止按钮
            case R.id.stop_button:
                bluetoothManager.onClick(2);
                break;

            //点击前进按钮
            case R.id.front_button:
                bluetoothManager.onClick(3);
                break;

            //点击后退按钮
            case R.id.back_button:
                bluetoothManager.onClick(4);
                break;

            //点击左转按钮
            case R.id.left_button:
                bluetoothManager.onClick(5);
                break;

            //点击右转按钮
            case R.id.right_button:
                bluetoothManager.onClick(6);
                break;

            //点击发送消息按钮
            case R.id.send_butten:
                bluetoothManager.onClick(7);
                break;

            case R.id.btn_take_photo:
                /** 点击拍照获取照片*/
                manager.takePhoto();
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
