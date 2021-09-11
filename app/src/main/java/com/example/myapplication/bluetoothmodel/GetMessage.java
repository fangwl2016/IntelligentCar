package com.example.myapplication.bluetoothmodel;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.RequiresApi;

import com.example.myapplication.supportutils.ChangeFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import static com.example.myapplication.MainActivity.socket;

public class GetMessage extends Thread {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Activity activity;
    private Context context;

    private static boolean hasSaved = false;
    private double[] weightArray = {0, 0, 0};
    private int index = 0;

    /**
     * 数据保存之后ui不再变化， 数据为0时清除该标志
     */
    private boolean uiNoChange = false;
    /**
     * 第几次接收到数据
     */
    private int i = 0;

    public GetMessage(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        //单片机->手机
        InputStream input = null;
        //手机->单片机
        OutputStream output = null;

        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.inputStream = input;
        this.outputStream = output;
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

                //获取第一个字符
                char[] ch = str.toCharArray();
                int a = ChangeFormat.asciiToInt(ch[0]);

                bytes = inputStream.read(buffer);
                str = new String(buffer, StandardCharsets.ISO_8859_1);
                str = str.substring(0, bytes);

                //获取第二个字符
                ch = str.toCharArray();
                int b = ChangeFormat.asciiToInt(ch[0]);

                double c;
                if(a>b){
                    c = a % 100 + 0.01 * b;
                }else{
                    c = b % 100 + 0.01 * a;
                }

                if(c == 0){
                    hasSaved = false;
                }

                i++;

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void write(byte bytes) {
        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean getHasSaved(){
        return hasSaved;
    }

    public void setHasSaved(boolean status, boolean ui){
        hasSaved = status;
        uiNoChange = ui;
    }

}
