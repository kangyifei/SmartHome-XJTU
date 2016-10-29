package com.kyf.futurespace.smarthome;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.apistore.sdk.ApiCallBack;
import com.baidu.apistore.sdk.ApiStoreSDK;
import com.baidu.apistore.sdk.network.Parameters;
import com.example.smarthome.R;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.kyf.futurespace.smarthome.Chat.TuringRobot;
import com.neurosky.thinkgear.TGDevice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kyf.futurespace.smarthome.BlueTooth.BlueTooth;
import com.kyf.futurespace.smarthome.Chat.TextToVoice;
import com.kyf.futurespace.smarthome.Chat.VoiceToText;
import com.kyf.futurespace.smarthome.brain.TextAnalyzer;
import com.kyf.futurespace.smarthome.brain.TimeAnalyzer;
import com.kyf.futurespace.smarthome.utils.*;
import com.neurosky.thinkgear.TGEegPower;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kyf on 2016/7/24 0024.
 */
public class ChatActivity extends Activity {

    private Button btnChat = null;
    private TextView tvRes = null;
    private TextView tvBrain = null;
    private String lstResults = null;

    private boolean chatting = false;
    private VoiceToText mVoiceToText = null;
    private TuringRobot mTuringRobot = null;
    private TextToVoice mTextToVoice = null;
    private BlueTooth mBlueTooth = null;
    private TimeAnalyzer mTar = null;
    private TextAnalyzer mTextAnalyzer = null;
    private BluetoothSocket socket = null;
    private String name = null;
    private String mac = null;
    private String order = null;
    private int blinkLower_cnt = 0;
    private boolean sleepedAndStopBlinkTesting = false;
    private Data data = Data.getInstance();
    private boolean isWeatherRequest = false;
    private Player player = null;
    private AsyncTask playingSong = null;
    private String songFileName = "";
    private Task task = null;
    private static String condition = "";
    private boolean isBrainAppear = false;

    private Boolean isreceiveBluetoothData = true;

    private TGDevice tgDevice = null;

    private Thread receiveBluetoothData = new Thread(new Runnable() {
        @Override
        public void run() {
            //控制循环
            while (isreceiveBluetoothData) {
                String data = "";
                try {
                    //获取数据
                    data = mBlueTooth.getString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!("".equals(data))) {
                    //发送到UI进程
                    Message msg = new Message();
                    msg.obj = data;
                    receiveBluetoothDataHandler.sendMessage(msg);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    });

    private Handler mHandler = new Handler() {//处理语音识别的结果
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerMessageWhat.VoiceToText_FINISHED:
                    lstResults = (String) msg.obj;
                    Log.w("VoiceToText_FINISHED", lstResults);
                    tvRes.setText(lstResults);
                    mVoiceToText.stopListening();
                    if (lstResults.contains("天气")) {
                        isWeatherRequest = true;
                    }
                    if (!lstResults.contains("天气")) {
                        isWeatherRequest = false;
                    }
                    if ((lstResults.contains("歌")) && (lstResults.contains("放"))) {
                        Data.getInstance().setIsSongRequest(true);
                        chatting = true;
                        mTextToVoice.startSpeaking("您要听什么歌");
                        tvRes.setText("您要听什么歌");
                    } else {
                        Data.getInstance().setIsSongRequest(false);
                        mTextAnalyzer = new TextAnalyzer(mHandler);
                        mTextAnalyzer.update(lstResults);
                        task = mTextAnalyzer.getTask();
                        order = task.getOrder().get(0);
                        Log.w("ChatActivity", "开始语义理解");
                        if (mTextAnalyzer.isObjectExisting() && mTextAnalyzer.isOrderExisting()) {
                            Log.w("ChatActivity", "存在命令词与对象");
                            if (!mTextAnalyzer.isConditionExisting()) {
                                mTar = new TimeAnalyzer();
                                mTar.update(lstResults);
                                mTar.date = mTar.getTimeCondition();
                                if (mTar.isChangeTime()) {
                                    if (lstResults.contains("提醒我")) {
                                        Log.w("ChatActivity", "找到");
                                        String[] lst = lstResults.split("提醒我", 2);
                                        setDelayTimeAndOrder(mTar.date, "", "时间到了，该" + lst[1]);
                                        mTextToVoice.startSpeaking("好的,提醒任务已设置");
                                        tvRes.setText("好的,提醒任务已设置");
                                    } else {
                                        Log.w("ChatActivity", "找到时间");
                                        setDelayTimeAndOrder(mTar.date, order, "");
                                        Log.w("ChatActivity", "Task:" + order);
                                        String answer = lstResults.replace("给我", "");
                                        mTextToVoice.startSpeaking("好的" + answer);
                                        tvRes.setText("好的" + answer);
                                    }

                                } else {
                                    Log.w("ChatActivity", "未找到时间");
                                    for (Iterator it = task.getOrder().iterator(); it.hasNext(); ) {
                                        order = (String) it.next();
                                        try {
                                            mBlueTooth.sendString(order);
                                            Log.w("ChatActivity", "Task:" + order);
                                            Thread.currentThread().sleep(100);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    String answer = "";
                                    for (Iterator it = task.getAnswer().iterator(); it.hasNext(); ) {
                                        answer = answer + "," + it.next();
                                    }
                                    mTextToVoice.startSpeaking("好的，" + answer);
                                    tvRes.setText("好的，" + answer);
                                }
                            } else {
//                                condition = task.getCondition();
                                tvRes.setText("好的,条件任务已设置");
                                mTextToVoice.startSpeaking("好的，条件任务已设置");
                            }
                        } else {
                            Log.w("ChatActivity", "未找到命令，开始图灵识别");
                            mTuringRobot.startTuringChatting(lstResults);
                        }
                    }
                    break;
                case HandlerMessageWhat.TuringRobot_FINISHED:
                    lstResults = (String) msg.obj;
                    if (isWeatherRequest) {
                        int c = lstResults.indexOf(";");
                        if (c > 0) {
                            lstResults = lstResults.substring(0, c);
                        }
                        lstResults = lstResults.replace("/", "月");
                        lstResults = lstResults.replace(" ", ",");
                        lstResults = lstResults.replaceAll("°", "度");
                        Log.w("answerOFweather", lstResults);
                    }
                    tvRes.setText(lstResults);
                    mTextToVoice.startSpeaking(lstResults);
                    break;
                case HandlerMessageWhat.TuringRobot_FINISHEDWITHURL:
                    final String[] res = (String[]) msg.obj;
                    lstResults = res[0];
                    Link link = new Link("请点击查看结果")
                            .setUnderlined(false)
                            .setOnClickListener(new Link.OnClickListener() {
                                @Override
                                public void onClick(String clickedText) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(res[1]));
                                    startActivity(intent);
                                }
                            });
                    List<Link> links = new ArrayList<>();
                    links.add(link);
                    tvRes.setText(lstResults + "。请点击查看结果");
                    LinkBuilder.on(tvRes)
                            .addLinks(links)
                            .build();
                    mTextToVoice.startSpeaking(lstResults + "。请点击查看结果");
                    break;
                case HandlerMessageWhat.Chatting_FINISHED:
                    if (data.isRequestToWake()) {
                        try {
                            mBlueTooth.sendString("!BRPH0000");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        data.setIsRequestToWake(false);
                    }
                    mVoiceToText.startListening();
                    chatting = true;
//                    btnChat.setText("开始");
                    break;
                case HandlerMessageWhat.HostConnection_FINISHED:
                    Log.w("ChatActivity", "主控已连接");
                    ToastSimple.makeText(ChatActivity.this, "主控已连接", 1).show();
                    Intent intent = new Intent("chat.host.connected");
                    sendBroadcast(intent);
                    Data.setConnectedToHost(true);
                    receiveBluetoothData.start();
                    break;
                case HandlerMessageWhat.HostConnection_FAILED:
                    ToastSimple.makeText(ChatActivity.this,
                            "主控连接失败，请重试", 1).show();
                    break;
//                case HandlerMessageWhat.answering_FINISHED:
//                    mVoiceToText.startListening();
//                    chatting = true;
//                    break;
//                case HandlerMessageWhat.Song_answering_FINISHED:
//                    mVoiceToText.startListening();
//                    chatting = true;
//                    break;
                case HandlerMessageWhat.SongIdentifying_FINISHED:
                    data.setIsSongRequest(false);
                    lstResults = (String) msg.obj;
                    String[] songs = lstResults.split("的");
                    tvRes.setText("歌曲信息获取中");
                    Parameters para = new Parameters();
                    String songName = "";
                    for (int i = 1; i < songs.length; i++) {
                        songName = songName + songs[i];
                    }
                    para.put("s", songs[0] + " " + songName);
                    para.put("size", "1");
                    para.put("page", "1");
                    ApiStoreSDK.execute("http://apis.baidu.com/geekery/music/query", ApiStoreSDK.GET, para,
                            new ApiCallBack() {
                                @Override
                                public void onSuccess(int i, String s) {
                                    String hash = "";
                                    try {
                                        JSONObject json1 = new JSONObject(s);
                                        JSONObject json2 = json1.getJSONObject("data");
                                        JSONArray json3 = json2.getJSONArray("data");
                                        JSONObject json4 = json3.getJSONObject(0);
                                        hash = (String) json4.get("hash");
                                        songFileName = json4.getString("filename");
                                        Log.e("歌曲信息", songFileName);
                                    } catch (JSONException e) {
                                        tvRes.setText("未搜索到歌曲");
                                        chatting = false;
                                        btnChat.setText("开始");
                                        e.printStackTrace();
                                    }
                                    Parameters para = new Parameters();
                                    para.put("hash", hash);
                                    tvRes.setText("歌曲:" + songFileName + "联网获取中");
                                    ApiStoreSDK.execute("http://apis.baidu.com/geekery/music/playinfo", ApiStoreSDK.GET, para,
                                            new ApiCallBack() {
                                                @Override
                                                public void onSuccess(int i, String s) {
                                                    String url = "";
                                                    try {
                                                        JSONObject json1 = new JSONObject(s);
                                                        JSONObject json2 = json1.getJSONObject("data");
                                                        url = json2.getString("url");
                                                        Log.e("歌曲网址：", url);
                                                    } catch (JSONException e) {
                                                        tvRes.setText("歌曲联网获取失败，请重试");
                                                        chatting = false;
                                                        btnChat.setText("开始");
                                                        e.printStackTrace();
                                                    }
                                                    if (url != "") {
                                                        AsyncTask playingSong = new AsyncTask() {
                                                            @Override
                                                            protected Object doInBackground(Object[] params) {
                                                                player = new Player(mHandler);
                                                                player.playUrl((String) params[0]);
                                                                return null;
                                                            }
                                                        };
                                                        playingSong.execute(url);
                                                    } else {
                                                        tvRes.setText("未获取到" + songFileName + "的在线版本");
                                                        chatting = false;
                                                        btnChat.setText("开始");
                                                    }

                                                    super.onSuccess(i, s);
                                                }

                                                @Override
                                                public void onError(int i, String s, Exception e) {
                                                    tvRes.setText("歌曲联网获取出错,请重试");
                                                    mTextToVoice.startSpeaking("歌曲联网获取出错,请重试");
                                                    chatting = false;
                                                    btnChat.setText("开始");
                                                    super.onError(i, s, e);
                                                }

                                                @Override
                                                public void onComplete() {
                                                    super.onComplete();
                                                }
                                            });
                                    super.onSuccess(i, s);

                                }

                                @Override
                                public void onError(int i, String s, Exception e) {
                                    tvRes.setText("歌曲信息获取失败请重试");
                                    super.onError(i, s, e);
                                }

                                @Override
                                public void onComplete() {
                                    super.onComplete();
                                }
                            });
                    break;
                case HandlerMessageWhat.SongPlayer_PREPARING:
                    tvRes.setText("歌曲：" + songFileName + "缓冲中");
                    break;
                case HandlerMessageWhat.SongPlayer_PLAYING:
                    tvRes.setText("播放中：" + songFileName);
                    break;
                case HandlerMessageWhat.SongPlayer_FINISHED:
                    tvRes.setText("播放完成");
                    mVoiceToText.startListening();
                    break;
                case HandlerMessageWhat.OverTemperature_WakeAC:
                    try {
                        mBlueTooth.sendString("!BRAC0000");
                        Log.w("ChatActivity", "Task:" + "!BRAC0000");
                        Thread.currentThread().sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };
    Handler thinkGearHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TGDevice.MSG_STATE_CHANGE:
                    switch (msg.arg1) {
                        case TGDevice.STATE_IDLE:
                            break;
                        case TGDevice.STATE_CONNECTING:
                            Intent intent = new Intent("chat.brainwave.state");
                            intent.putExtra("state", "Connecting...\n");
                            sendBroadcast(intent);
                            break;
                        case TGDevice.STATE_CONNECTED:
                            Intent intent2 = new Intent("chat.brainwave.state");
                            intent2.putExtra("state", "Connected.\n");
                            sendBroadcast(intent2);
                            tgDevice.start();
                            break;
                        case TGDevice.STATE_NOT_FOUND:
                            Intent intent3 = new Intent("chat.brainwave.state");
                            intent3.putExtra("state", "Can't find\n");
                            sendBroadcast(intent3);
                            break;
                        case TGDevice.STATE_NOT_PAIRED:
                            Intent intent4 = new Intent("chat.brainwave.state");
                            intent4.putExtra("state", "not paired\n");
                            sendBroadcast(intent4);
                            break;
                        case TGDevice.STATE_DISCONNECTED:
                            Intent intent5 = new Intent("chat.brainwave.state");
                            intent5.putExtra("state", "Disconnected mang\n");
                            sendBroadcast(intent5);
                    }
                    break;
                case TGDevice.MSG_POOR_SIGNAL:
                    Intent intent = new Intent("chat.brainwave.signal");
                    intent.putExtra("signal", msg.arg1);
                    sendBroadcast(intent);
                    Log.e("SignalQuality: ", "" + msg.arg1);
                    if (msg.arg1 == 0) {
                        if (!sleepedAndStopBlinkTesting) {
                            blinkLower_cnt++;
                            Log.w("blinkLower_cnt", "" + blinkLower_cnt);
                            if (blinkLower_cnt >= 6) {
                                try {
                                    mBlueTooth.sendString("!BRLI0001");
//                                    sleepedAndStopBlinkTesting = true;
                                    blinkLower_cnt = 0;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    sleepedAndStopBlinkTesting = false;
                                }
                            }
                        }
                    }
                    break;
                case TGDevice.MSG_ATTENTION:
                    if (isBrainAppear) {
                        tvBrain.setText("attention:" + msg.arg1);
                    }
                    break;
                case TGDevice.MSG_MEDITATION:
                    if (isBrainAppear) {
                        tvBrain.append("meditation:" + msg.arg1);
                    }
                    break;
                case TGDevice.MSG_EEG_POWER:
                    TGEegPower ep=(TGEegPower)msg.obj;
                    Log.v("HelloEEG", "Delta: " + ep.delta);
                    String brainres="Delta: " + ep.delta+","
                            +"theta: " +ep.theta;
                    if (isBrainAppear) {
                        ToastSimple.makeText(ChatActivity.this,brainres,0.3);
                    }
                    break;
                case TGDevice.MSG_BLINK:
                    Log.e("BLINK: ", "" + msg.arg1);
                    blinkLower_cnt = 0;
                    break;
                default:
                    break;
            } /* end switch on message type */
        } /* end handleMessage() */
    };
    Handler receiveBluetoothDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String order = (String) msg.obj;
            if (order.contains("!BRPH0001")) {
                if (chatting) {
                    mVoiceToText.stopListening();
                    mTextToVoice.stopSpeaking();
                    if (player != null) {
                        player.stop();
                    }
                    if (playingSong != null) {
                        playingSong.cancel(true);
                    }
                    chatting = false;
                }
                mTextToVoice.startSpeaking("主人请吩咐");
                tvRes.setText("主人请吩咐");
                data.setIsRequestToWake(true);
                chatting = true;
                btnChat.setText("关闭");
            }
//            if (!"".equals(condition)) {
//                if (order.indexOf(condition) >= 0) {
            if (data.isTaskWaitingExisting(order)) {
                Log.w("ChatActivity", "条件已触发");
//                    if (!data.getOrderWaitingForBluetooth().getOrder().get(0).equals("")) {
                Task task = data.getTaskWaiting(order);
                Log.w("ChatActivity", "条件已存在指令");
                for (Iterator it = task.getOrder().iterator(); it.hasNext(); ) {
                    order = (String) it.next();
                    try {
                        mBlueTooth.sendString(order);
                        Log.w("ChatActivity", "Task:" + order);
                        Thread.currentThread().sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                        data.setOrderWaitingForBluetooth(null);
//                    }
//                data.deleteTaskWaiting(order);
            }
            if (order.contains("!BCTP")) {
                String temp = order.substring(5);
                int tem = Integer.valueOf(temp);
                data.setTemperature(tem);
                ToastSimple.makeText(ChatActivity.this, "当前温度是：" + (tem / 10.0), 0.5).show();
            }
//            }
            Log.e("Myacty_BluetoothData", order);
        }
    };

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("setting.brainwave.begin");
        registerReceiver(brainWaveBeginReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_chat);
        Log.e("ChatActivity", "Create");


        //讯飞语音功能初始化


        mVoiceToText = new VoiceToText(ChatActivity.this, mHandler);//初始化一个VoiceToText对象
        mTuringRobot = new TuringRobot(ChatActivity.this, mHandler);
        mTextToVoice = new TextToVoice(ChatActivity.this, mHandler);
        mBlueTooth = new BlueTooth(mHandler);
        name = data.getName();
        mac = data.getMac();
        initView();
        //蓝牙自动连接

        if (name.length() != 0) {
            if (mac.length() != 0) {
                socket = mBlueTooth.startConnectToServerSocketAndReturnSocket(mac);
                data.setDatasocket(socket);
            }
        } else {
            Log.e("ChatActivity", "未获取到主控");
        }

        if (order != null) {
            try {
                mBlueTooth.sendString(order);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        tvRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBrainAppear) {
                    tvBrain.setText(" ");
                    isBrainAppear = false;
                } else {
                    isBrainAppear = true;
                }
            }
        });


        btnChat.setText("开始");
        //设置属性
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (!chatting) {
//                    mVoiceToText.startListening();
//                    chatting = true;
//                    btnChat.setText("关闭");
//                } else {
                    mVoiceToText.stopListening();
                    mTextToVoice.stopSpeaking();
                    if (player != null) {
                        player.stop();
                    }
                    if (playingSong != null) {
                        playingSong.cancel(true);
                    }
                    tvRes.setText("");
                    chatting = false;
                    btnChat.setText("关闭");
//                }
            }
        });
    }

    @Override
    protected void onStop() {
        Log.e("ChatActivity", "Stop");
        mVoiceToText.stopListening();
        mTextToVoice.stopSpeaking();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e("ChatActivity", "Destroy");
        isreceiveBluetoothData = false;
        unregisterReceiver(brainWaveBeginReceiver);
        if (!(tgDevice == null)) {
            tgDevice.close();
        }
        if (!(socket == null)) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (player != null) {
            player.stop();
        }
        if (playingSong != null) {
            playingSong.cancel(true);
        }
        mVoiceToText.stopListening();
        mVoiceToText.destroy();
        mTextToVoice.stopSpeaking();
        mTextToVoice.destroy();
        mBlueTooth.cancel();
        socket = null;
        super.onDestroy();
    }


    //    private void setDelayTimeAndOrder(Calendar calendar, String orderString) {
//        AlarmManager am;
//        am = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent intent = new Intent(ChatActivity.this, DelayTimeReceiver.class);
//        intent.putExtra("order", orderString);
//        Log.w("setDelayTimeAndOrder", orderString);
//        PendingIntent sender = PendingIntent.getBroadcast(ChatActivity.this, 0, intent, 0);
//        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//        Log.w("setDelayTimeAndOrder", "" + calendar.getTime());
//        Log.w("setDelayTimeAndOrder", "" + calendar.getTimeInMillis());
//    }
    private void setDelayTimeAndOrder(Calendar calendar, String orderString, String answer) {
        AlarmManager am;
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(ChatActivity.this, DelayTimeReceiver.class);
        intent.putExtra("order", orderString);
        intent.putExtra("answer", answer);
        Log.w("setDelayTimeAndOrder", orderString);
        PendingIntent sender = PendingIntent.getBroadcast(ChatActivity.this, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        Log.w("setDelayTimeAndOrder", "" + calendar.getTime());
        Log.w("setDelayTimeAndOrder", "" + calendar.getTimeInMillis());
    }

    private void initView() {
        btnChat = (Button) findViewById(R.id.btn);
        tvRes = (TextView) findViewById(R.id.textret);
        tvBrain = (TextView) findViewById(R.id.tvBrainData);
        tvRes.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private BroadcastReceiver brainWaveBeginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("setting.brainwave.begin")) {
                //连接脑电波模块
                BluetoothAdapter bluetoothAdapter = mBlueTooth.getmBluetoothAdapter();
                tgDevice = new TGDevice(bluetoothAdapter, thinkGearHandler);
                BluetoothDevice bwDevice = bluetoothAdapter.getRemoteDevice("00:BA:55:57:00:7F");
                tgDevice.connect(bwDevice, true);
            }
        }
    };
}
