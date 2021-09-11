package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.KeyEvent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.manager.BluetoothManager;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.IOException;

import static com.example.myapplication.Utils.assetFilePath;


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

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //实例化manager
        bluetoothManager = new BluetoothManager(this, this);

        //连接蓝牙
        connectBtn = findViewById(R.id.conntect_btn);
        connectBtn.setOnClickListener(this);

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
