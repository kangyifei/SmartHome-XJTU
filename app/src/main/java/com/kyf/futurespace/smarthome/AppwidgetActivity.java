package com.kyf.futurespace.smarthome;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import com.baidu.apistore.sdk.ApiStoreSDK;
import com.cunoraz.gifview.library.GifView;
import com.example.smarthome.R;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.kyf.futurespace.smarthome.Chat.TextToVoice;
import com.kyf.futurespace.smarthome.Chat.TuringRobot;
import com.kyf.futurespace.smarthome.Chat.VoiceToText;
import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;
import com.kyf.futurespace.smarthome.utils.ListAdapter;
import com.kyf.futurespace.smarthome.utils.ListData;
import com.kyf.futurespace.smarthome.utils.ToastSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangy on 2016/12/21.
 */

public class AppwidgetActivity extends Activity {
    List<ListData> listDatas;
    ListView listView;
    ListAdapter listAdapter;
    GifView gifView;
    private VoiceToText mVoiceToText ;
    private TuringRobot mTuringRobot ;
    private TextToVoice mTextToVoice;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HandlerMessageWhat.Chatting_FINISHED:
                    mVoiceToText.startListening();
                    gifView.setVisibility(GifView.VISIBLE);
                    break;
                case HandlerMessageWhat.VoiceToText_FINISHED:
                    mTuringRobot.startTuringChatting((String) msg.obj);
                    listDatas.add(new ListData((String)msg.obj,ListData.SEND));
                    listAdapter.notifyDataSetChanged();
                    break;
                case HandlerMessageWhat.TuringRobot_FINISHED:
                    mTextToVoice.startSpeaking((String) msg.obj);
                    listDatas.add(new ListData((String)msg.obj,ListData.RECEIVER));
                    listAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appweight_activity);
        initViews();
        try {
            SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56d5b0b0");
        } catch (Exception e) {
            ToastSimple.makeText(this, "讯飞语音功能初始化失败!", 1).show();
        }
        ToastSimple.makeText(this, "讯飞语音功能初始化成功!", 0.2).show();
        ApiStoreSDK.init(this, "66f37dcede985a2d36bf9617284e8ed3");
        mVoiceToText = new VoiceToText(AppwidgetActivity.this, mHandler);//初始化一个VoiceToText对象
        mTuringRobot = new TuringRobot(AppwidgetActivity.this, mHandler);
        mTextToVoice = new TextToVoice(AppwidgetActivity.this, mHandler);
        listView.setAdapter(listAdapter);
        listDatas.add(new ListData(this.getString(R.string.welcome),ListData.RECEIVER));
        listAdapter.notifyDataSetChanged();
        mTextToVoice.startSpeaking(this.getString(R.string.welcome));

    }
    private void initViews(){

        listView= (ListView) findViewById(R.id.lv_appwidget);
        listDatas=new ArrayList<>();
        listDatas.clear();
        listAdapter=new ListAdapter(listDatas,this);
        gifView= (GifView) findViewById(R.id.gif1);
        gifView.setVisibility(GifView.INVISIBLE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        mTextToVoice.stopSpeaking();
        mVoiceToText.stopListening();
        mTextToVoice.destroy();
        mVoiceToText.destroy();

    }
}
