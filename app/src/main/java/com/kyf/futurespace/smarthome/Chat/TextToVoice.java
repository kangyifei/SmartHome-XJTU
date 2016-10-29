package com.kyf.futurespace.smarthome.Chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import com.iflytek.cloud.*;
import com.kyf.futurespace.smarthome.utils.Data;
import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;
import com.kyf.futurespace.smarthome.utils.ToastSimple;

/**
 * Created by kyf on 2016/4/7 0007.
 */
public class TextToVoice {
    private SpeechSynthesizer mTts;
    private Context mContext = null;
    private Handler mHandler;
    int result = 0;
    String lstResult = null;

    public TextToVoice(Context context, Handler handler) {
        mContext = context;
        mHandler=handler;
        mTts = SpeechSynthesizer.createSynthesizer(mContext, mTtoVInitListener);
    }

    //初始化合成监听器
    private InitListener mTtoVInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("Ttslistener", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "合成监听器初始化失败，错误码:" + code, Toast.LENGTH_SHORT).show();
            } else {
                setParam();
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    //语音合成实例
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            ToastSimple.makeText(mContext, "开始播放", 1).show();
        }

        @Override
        public void onSpeakPaused() {
            Toast.makeText(mContext, "暂停播放", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onSpeakResumed() {
            Toast.makeText(mContext, "继续播放", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                ToastSimple.makeText(mContext, "播放完成", 1).show();
//                if(Data.getInstance().isRequestToWake()){
//                    mHandler.obtainMessage(HandlerMessageWhat.Chatting_FINISHED)
//                            .sendToTarget();
//                    Data.getInstance().setIsRequestToWake(false);
//                }else
//            if(Data.getInstance().isSongRequest()){
//                    mHandler.obtainMessage(HandlerMessageWhat.Song_answering_FINISHED)
//                            .sendToTarget();
//                }
//                else {
                mHandler.obtainMessage(HandlerMessageWhat.Chatting_FINISHED)
                        .sendToTarget();
//                }

            } else if (error != null) {
                Toast.makeText(mContext, error.getPlainDescription(true), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public void setParam() {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
        mTts.setParameter(SpeechConstant.VOICE_NAME, "");
    }

    public void startSpeaking(String mstring) {
        lstResult = mstring;
        result = mTts.startSpeaking(lstResult, mTtsListener);
        if (result != ErrorCode.SUCCESS) {
            Toast.makeText(mContext, "合成失败,错误码：" + result, Toast.LENGTH_SHORT).show();
        }
    }

    public void stopSpeaking() {
        mTts.stopSpeaking();
    }

    public void destroy() {
        mTts.destroy();
    }
}
