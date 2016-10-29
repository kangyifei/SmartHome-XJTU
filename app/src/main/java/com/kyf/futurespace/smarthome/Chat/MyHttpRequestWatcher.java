package com.kyf.futurespace.smarthome.Chat;

import android.os.Handler;
import android.util.Log;

import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;
import com.turing.androidsdk.HttpRequestWatcher;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by kyf on 2016/4/25 0025.
 */
public class MyHttpRequestWatcher implements HttpRequestWatcher {
    Handler handler = null;
    public MyHttpRequestWatcher(Handler mHandler) {
        this.handler = mHandler;
    }
    @Override
    public void onSuceess(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            if(jsonObject.has("code")){
                int code=jsonObject.getInt("code");
                if(code<200000){
                    if (jsonObject.has("text")) {
                        handler.obtainMessage
                                (HandlerMessageWhat.TuringRobot_FINISHED, jsonObject.get("text"))
                                .sendToTarget();
                        Log.w("turingres",(String) jsonObject.get("text"));
                    }
                }else {
                    String[] res=new String[2];
                    res[0]= (String) jsonObject.get("text");
                    res[1]=jsonObject.getString("url");
                        handler.obtainMessage
                                (HandlerMessageWhat.TuringRobot_FINISHEDWITHURL, res)
                                .sendToTarget();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onError(String s) {
    }
}


