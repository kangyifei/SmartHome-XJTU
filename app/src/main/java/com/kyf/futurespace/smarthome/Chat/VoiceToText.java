package com.kyf.futurespace.smarthome.Chat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import com.iflytek.cloud.*;
import com.kyf.futurespace.smarthome.utils.Data;
import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;
import com.kyf.futurespace.smarthome.utils.ToastSimple;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by kyf on 2016/3/4 0004.
 */
public class VoiceToText {
    private com.iflytek.cloud.SpeechRecognizer mIat = null;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    private Context mContext = null;
    private Handler mHandler;
    String lstResults = null;
    int result = 0;


    public VoiceToText(Context context, Handler myHandler) {
        mContext = context;//传入的上下文对象
        mIat = com.iflytek.cloud.SpeechRecognizer.createRecognizer(mContext, mInitListener);
        mHandler = myHandler;//传入的handler
    }

    //初始化监听器
    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d("VtT", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(mContext, "听写监听器初始化失败，错误码:" + code, Toast.LENGTH_SHORT).show();
            } else {
                setParam();
                //开始听写
            }
        }
    };
    //听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
        }

        @Override
        public void onBeginOfSpeech() {
            ToastSimple.makeText(mContext, "听写准备完成，请开始", 1).show();
        }


        @Override
        public void onEndOfSpeech() {
            ToastSimple.makeText(mContext, "正在听写", 1).show();

        }

        @Override
        public void onResult(RecognizerResult Results, boolean b) {
            getResults(Results);
            if (b) {
                //有结果了，向主线程返回消息
                if(Data.getInstance().isSongRequest()){
                    Message msg = new Message();
                    msg.what= HandlerMessageWhat.SongIdentifying_FINISHED;
                    msg.obj=lstResults;
                    mHandler.sendMessage(msg);
                }else {
                    Message msg = new Message();
                    msg.what= HandlerMessageWhat.VoiceToText_FINISHED;
                    msg.obj=lstResults;
                    mHandler.sendMessage(msg);
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.w("recognizerListener", speechError.toString());
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };

    //参数设置
    public void startListening() {
        result = mIat.startListening(mRecoListener);
        if (result != ErrorCode.SUCCESS) {
            Toast.makeText(mContext, "听写失败,错误码：" + result, Toast.LENGTH_SHORT).show();
        }
    }

    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_MIX);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "900");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
    }

    //分析返回的JSON包
    void getResults(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);
        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        lstResults = resultBuffer.toString();
    }
    public void stopListening(){
        mIat.stopListening();
    }
    public void destroy(){
        mIat.destroy();
    }
}


