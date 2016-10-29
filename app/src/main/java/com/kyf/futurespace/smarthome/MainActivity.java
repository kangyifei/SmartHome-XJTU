package com.kyf.futurespace.smarthome;

import android.app.TabActivity;
import android.content.*;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.kyf.futurespace.smarthome.brain.ConditionTaskActivity;
import com.kyf.futurespace.smarthome.utils.Data;
import com.example.smarthome.R;

/**
 * Created by kyf on 2016/7/24 0024.
 */

public class MainActivity extends TabActivity {
    private TabHost tabhost;
    private RadioGroup main_radiogroup;
    private RadioButton tab_icon_weixin, tab_icon_setting;
    private ImageView brainwave_signal;
    private ImageView imgbtn_conditionTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        SharedPreferences sharedPreferences = getSharedPreferences("host", MODE_PRIVATE);
        Data data = Data.getInstance();
        data.setName(sharedPreferences.getString("name", ""));
        data.setMac(sharedPreferences.getString("mac", ""));
        //获取按钮
        main_radiogroup = (RadioGroup) findViewById(R.id.main_radiogroup);

        tab_icon_weixin = (RadioButton) findViewById(R.id.tab_icon_weixin);
        tab_icon_setting = (RadioButton) findViewById(R.id.tab_icon_setting);

        brainwave_signal = (ImageView) findViewById(R.id.title_BrainWaveSignal);
        brainwave_signal.setImageDrawable(getResources()
                .getDrawable(R.drawable.brainwave_signal00));
        imgbtn_conditionTask= (ImageView) findViewById(R.id.title_conditionTasks);
        imgbtn_conditionTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, ConditionTaskActivity.class);
                startActivity(intent);
            }
        });

        //往TabWidget添加Tab
        tabhost = getTabHost();
        tabhost.addTab(tabhost.newTabSpec("tag1").setIndicator("0").setContent(new Intent(this, ChatActivity.class)));
        tabhost.addTab(tabhost.newTabSpec("tag2").setIndicator("1").setContent(new Intent(this, SettingActivity.class)));

        //设置监听事件
        checkListener checkradio = new checkListener();
        main_radiogroup.setOnCheckedChangeListener(checkradio);


    }

    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("chat.brainwave.signal");
        registerReceiver(brainWaveSignalReceiver, intentFilter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(brainWaveSignalReceiver);
        super.onStop();
    }


    //监听类
    public class checkListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            //setCurrentTab 通过标签索引设置当前显示的内容
            //setCurrentTabByTag 通过标签名设置当前显示的内容
            switch (checkedId) {
                case R.id.tab_icon_weixin:
                    tabhost.setCurrentTab(0);
                    //或
                    //tabhost.setCurrentTabByTag("tag1");
                    break;
                case R.id.tab_icon_setting:
                    tabhost.setCurrentTab(1);
                    break;
            }


        }
    }

    private BroadcastReceiver brainWaveSignalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("chat.brainwave.signal")) {
                int signal = intent.getExtras().getInt("signal");
                if (signal >= 200) {
                    brainwave_signal.setImageDrawable(getResources()
                            .getDrawable(R.drawable.brainwave_signal00));
                } else if (signal >= 160) {
                    brainwave_signal.setImageDrawable(getResources()
                            .getDrawable(R.drawable.brainwave_signal01));
                } else if (signal >= 120) {
                    brainwave_signal.setImageDrawable(getResources()
                            .getDrawable(R.drawable.brainwave_signal02));
                } else if (signal >= 80) {
                    brainwave_signal.setImageDrawable(getResources()
                            .getDrawable(R.drawable.brainwave_signal03));
                } else if (signal > 0) {
                    brainwave_signal.setImageDrawable(getResources()
                            .getDrawable(R.drawable.brainwave_signal04));
                } else {
                    brainwave_signal.setImageDrawable(getResources()
                            .getDrawable(R.drawable.brainwave_signal05));

                }
            }
        }
    };
}



