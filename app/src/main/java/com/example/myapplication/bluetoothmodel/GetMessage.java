package com.example.myapplication.bluetoothmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.example.myapplication.manager.UiUpdate;
import com.example.myapplication.supportutils.ChangeFormat;
import com.example.myapplication.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import static com.example.myapplication.MainActivity.socket;

public class GetMessage extends Thread {
    //单片机->手机
    private static InputStream inputStream = null;
    //手机->单片机
    private static OutputStream outputStream = null;
    private Activity activity;
    private Context context;

    /**
     * 获取消息流
     */
    public GetMessage(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;
        while (true) {
            try {
                //从输入流读取一定数量的字节存在buff数组中，返回读取的字节数
                bytes = inputStream.read(buffer);
                String str = new String(buffer, StandardCharsets.ISO_8859_1);
                str = str.substring(0, bytes);
                //将输入流展示到接收的数据文本框中
                UiUpdate uiUpdate = new UiUpdate(activity);
                uiUpdate.showReceiveText(str);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * 发送字节数组
     * @param b
     */
    public static void write(byte[] b) {
        try {
            Log.log("GetMessage write start, b:" + new String(b));
            outputStream.write(b);
            outputStream.flush();
        } catch (IOException e) {
            Log.log("GetMessage write throw exception:" + e.getMessage());
        }
    }
}
