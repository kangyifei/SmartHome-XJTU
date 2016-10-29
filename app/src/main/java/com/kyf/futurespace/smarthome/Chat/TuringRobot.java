package com.kyf.futurespace.smarthome.Chat;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.turing.androidsdk.InitListener;
import com.turing.androidsdk.TuringApiConfig;
import com.turing.androidsdk.TuringApiManager;

/**
 * Created by kyf on 2016/8/17 0017.
 */
    public class TuringRobot {
        private TuringApiConfig turingApiConfig=null;
        private TuringApiManager mTuringApiManager=null;
        private MyHttpRequestWatcher myHttpRequestWatcher=null;
        private String TAG="TuringRobot";
        private String result=null;
        private Handler handler=null;
        private boolean turingApiManagerInit=false;
        public TuringRobot(Context mContext, Handler mHandler) {
            this.handler=mHandler;
            initTuringApiManager(mContext);
            addListener();
        }
        private void initTuringApiManager(final Context context) {
            turingApiConfig = new TuringApiConfig(context, "c8ba2f7d673701d21a87da8c9cd565ee");
            turingApiConfig.setInitListener(new InitListener() {
                @Override
                public void onComplete() {
                    Log.w(TAG, "TuringApiManager initiation succeed");
                    mTuringApiManager = new TuringApiManager(turingApiConfig, context);
                    turingApiManagerInit=true;
                }
                @Override
                public void onFail() {
                    Log.w(TAG, "TuringApiManager initiation falied");
                    turingApiManagerInit=false;
                }
            });
            turingApiConfig.init(context);
        }
        private void addListener() {
            myHttpRequestWatcher=new MyHttpRequestWatcher(handler);
            if(turingApiManagerInit) {
                mTuringApiManager.setRequestWatcher(myHttpRequestWatcher);
            }
            else {
                Log.w(TAG+"setRequestWatcher","TuringApiManager hasn't been initiated");
            }
        }
        public void startTuringChatting(String mString){
            if(turingApiManagerInit) {
                mTuringApiManager.requestTuringAPI(mString);
            }
            else{
                Log.w(TAG+"requestTuringAPI","TuringApiManager hasn't been initiated");
            }
        }
    }
