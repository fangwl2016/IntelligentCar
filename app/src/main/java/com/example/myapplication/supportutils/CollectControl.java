package com.example.myapplication.supportutils;

import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.myapplication.R;

public class CollectControl {

    private Button connectBtn;
    private TextView connectStatusText;
    private TextView receiveLabel;
    private EditText textReceive;
    private Button stopButton;
    private Button backButton;
    private Button frontButton;
    private Button leftButton;
    private Button rightButton;
    private Button sendButten;
    private EditText textSend;


    /**
     * 注册控件
     */
    public void initControl(Activity activity){

        connectBtn = activity.findViewById(R.id.conntect_btn);
        connectStatusText = activity.findViewById(R.id.bluetoothStatus_text);
        receiveLabel = activity.findViewById(R.id.receive_label);
        textReceive = activity.findViewById(R.id.text_receive);
        stopButton = activity.findViewById(R.id.stop_button);
        backButton = activity.findViewById(R.id.back_button);
        frontButton = activity.findViewById(R.id.front_button);
        leftButton = activity.findViewById(R.id.left_button);
        rightButton = activity.findViewById(R.id.right_button);
        sendButten = activity.findViewById(R.id.send_butten);
        textSend = activity.findViewById(R.id.text_send);

    }


    public TextView getConnectStatusText(){
        return connectStatusText;
    }

    public Button getConnectBtn(){
        return connectBtn;
    }

    public TextView getReceiveLabel() { return receiveLabel; }

    public EditText getTextReceive() { return textReceive; }

    public Button getStopButton() { return stopButton; }

    public Button getBackButton() { return backButton; }

    public Button getFrontButton() { return frontButton; }

    public Button getLeftButton() { return leftButton; }

    public Button getRightButton() { return rightButton; }

    public Button getSendButten() { return sendButten; }

    public EditText getTextSend() { return textSend; }



}
