package com.kyf.futurespace.smarthome;

import android.app.Activity;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.iflytek.cloud.SpeechUtility;
import com.kyf.futurespace.smarthome.BlueTooth.BlueTooth;
import com.kyf.futurespace.smarthome.BlueTooth.BlueToothSearch;
import com.kyf.futurespace.smarthome.utils.Data;
import com.kyf.futurespace.smarthome.utils.ToastSimple;
import com.example.smarthome.R;

/**
 * Created by kyf on 2016/7/24 0024.
 */
public class SettingActivity extends Activity {
    private Button btnYuji = null;
    private Button btnBluetooth = null;
    private Button btnClearHostData = null;
    private Button btnClearConditionConnectedData = null;
    private Button btnConnectToBrainWave = null;
    private TextView tvYujiState = null;
    private TextView tvBluetoothState = null;
    private TextView tvBrainWaveState = null;
    private BlueTooth mBlueTooth = null;
    private String name = "";
    final Data data = (Data) getApplication();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_setting);
        Log.e("SettingActivity", "Create");

        initView();

        mBlueTooth = new BlueTooth();

        name = data.getName();

        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            tvYujiState.setText("语记未安装");
            btnYuji.setClickable(true);
            btnYuji.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = SpeechUtility.getUtility().getComponentUrl();
                    Uri uri = Uri.parse(url);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                }
            });
        } else {
            tvYujiState.setText("语记已安装");
            btnYuji.setClickable(false);
        }

        //蓝牙开启检测
        if (mBlueTooth.getmBluetoothAdapter().isEnabled()) {
            tvBluetoothState.setText("蓝牙已打开");
        } else {
            boolean res = mBlueTooth.getmBluetoothAdapter().enable();
            if (res) {
                tvBluetoothState.setText("蓝牙已打开");
            } else {
                tvBluetoothState.setText("蓝牙未打开");
            }

        }
        btnConnectToBrainWave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("setting.brainwave.begin");
                sendBroadcast(intent);
            }
        });
        //蓝牙搜索BTN
        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, BlueToothSearch.class);
                startActivity(intent);
            }

        });
        //主控数据清除按钮
        btnClearHostData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.resetHost();
                SharedPreferences sharedPreferences=getSharedPreferences("host",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.clear().commit();
                ToastSimple.makeText(SettingActivity.this, "程序即将重启", 1).show();
                Intent intent = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        //清空条件
        btnClearConditionConnectedData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setConditionAndOrderConnected(false);
                data.clearAllTasksWaiting();
                ToastSimple.makeText(SettingActivity.this, "用户数据已清除", 1).show();
                Log.w("SettingActivity", "用户数据已清除");
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        name = data.getName();
        if (!"".equals(name)) {
            tvBluetoothState.append(name+"已连接");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        name = data.getName();
        if (!"".equals(name)) {
            tvBluetoothState.append(name+"已连接");
        }
    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("chat.brainwave.state");
        intentFilter.addAction("chat.host.connected");
        registerReceiver(brainWaveStateReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        data.setConnectedToHost(false);
        unregisterReceiver(brainWaveStateReceiver);
        mBlueTooth.cancel();
        Log.e("SettingActivity", "Destroy");
        super.onDestroy();
    }

    private void initView() {
        btnYuji = (Button) findViewById(R.id.btn_yuji);
        btnBluetooth = (Button) findViewById(R.id.btn_bluetooth);
        btnClearHostData = (Button) findViewById(R.id.btn_buletooth_search_clear);
        btnClearConditionConnectedData = (Button) findViewById(R.id.btn_condition_clear);
        btnConnectToBrainWave = (Button) findViewById(R.id.btn_brainwave);
        tvBluetoothState = (TextView) findViewById(R.id.tv_bluetooth_state);
        tvYujiState = (TextView) findViewById(R.id.tv_yuji_state);
        tvBrainWaveState = (TextView) findViewById(R.id.tv_brainwave_state);
    }

    private BroadcastReceiver brainWaveStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("chat.brainwave.state")) {
                String state = intent.getExtras().getString("state");
                tvBrainWaveState.setText(state);
            }
            if (action.equals("chat.host.connected")) {
                tvBluetoothState.append("\n主控" + name + "已连接");
            }
        }
    };
}
