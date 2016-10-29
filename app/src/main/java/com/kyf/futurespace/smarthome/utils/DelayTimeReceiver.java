package com.kyf.futurespace.smarthome.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.kyf.futurespace.smarthome.BlueTooth.BlueTooth;
import com.kyf.futurespace.smarthome.Chat.TextToVoice;
import com.kyf.futurespace.smarthome.Chat.VoiceToText;
import com.kyf.futurespace.smarthome.ChatActivity;

import java.io.IOException;

/**
 * Created by kyf on 2016/6/30 0030.
 */
public class DelayTimeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String answer=intent.getStringExtra("answer");
        String order = intent.getStringExtra("order");
        Handler handler=new Handler();
        Log.w("DelayTimeReceiver", order);
        BlueTooth blueTooth=new BlueTooth();
        try {
            blueTooth.sendString(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            blueTooth.sendString(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!("".equals(answer))){
            Log.w("DelayTimeReceiver", answer);
            TextToVoice textToVoice=new TextToVoice(context,handler);
            VoiceToText voiceToText=new VoiceToText(context,handler);
            textToVoice.stopSpeaking();
            voiceToText.stopListening();
            textToVoice.startSpeaking(answer);
        }
    }
}
