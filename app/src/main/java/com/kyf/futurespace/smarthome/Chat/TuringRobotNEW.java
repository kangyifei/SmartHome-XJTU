//package com.kyf.futurespace.smarthome.Chat;
//
//import android.content.Context;
//import android.os.Handler;
//import android.util.Log;
//
//import com.turing.androidsdk.*;
//
///**
// * Created by kyf on 2016/4/24 0024.
// */
//public class TuringRobotNEW {
//    private SDKInitBuilder sdkInitBuilder = null;
//    private TuringApiManager mTuringApiManager = null;
//    private MyHttpRequestWatcher myHttpRequestWatcher = null;
//    private String TAG = "TuringRobotNEW";
//    private boolean turingApiManagerInit = false;
//
//    public TuringRobotNEW(final Context mContext, Handler mHandler) {
//        sdkInitBuilder = new SDKInitBuilder(mContext);
//        sdkInitBuilder.setTuringKey("c8ba2f7d673701d21a87da8c9cd565ee");
//        sdkInitBuilder.setSecret("2344734c3974248b");
//        myHttpRequestWatcher = new MyHttpRequestWatcher(mHandler);
//        SDKInit.init(sdkInitBuilder, new InitListener() {
//            @Override
//            public void onComplete() {
//                initTuringApiManager(mContext);
//            }
//
//            @Override
//            public void onFail(String s) {
//
//            }
//        });
//    }
//
//    private void initTuringApiManager(final Context context) {
//        mTuringApiManager = new TuringApiManager(context);
//        mTuringApiManager.setHttpListener(myHttpRequestWatcher);
//        turingApiManagerInit = true;
//    }
//
//    public void startTuringChatting(String mString) {
//        if (turingApiManagerInit) {
//            mTuringApiManager.requestTuringAPI(mString);
//            Log.w("requestTuring",mString);
//        } else {
//            Log.w(TAG + "requestTuringAPI", "TuringApiManager hasn't been initiated");
//        }
//
//    }
//}
//
