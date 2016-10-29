package com.kyf.futurespace.smarthome.Chat;

import android.content.Context;
import android.os.Handler;
import com.iflytek.cloud.TextUnderstander;

/**
 * Created by kyf on 2016/8/13 0013.
 */
public class XfyunUnderstanding {
    private Context mContext = null;
    private Handler mHandler;
    private String inpString="";
    private TextUnderstander textUnderstander=TextUnderstander.createTextUnderstander(mContext,null);

}
