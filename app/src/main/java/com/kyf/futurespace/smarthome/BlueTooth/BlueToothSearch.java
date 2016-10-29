package com.kyf.futurespace.smarthome.BlueTooth;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import com.example.smarthome.R;
import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;
import com.kyf.futurespace.smarthome.utils.ToastSimple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyf on 2016/4/11 0011.
 */
public class BlueToothSearch extends Activity {
    ListView mListView = null;
    Button mSearchButton = null;

    BlueToothDeviceListAdapter blueToothDeviceListAdapter=null;
    BlueTooth mBlueTooth;
    BlueToothDevicesListData blueToothDevicesListData;
    SharedPreferences preferences;
    String name=null;
    String mac=null;
    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HandlerMessageWhat.SetHostDevice_FINISHED:
                    int i= (int) msg.obj;
                    Log.e("BlueToothSearch","主控信息传递成功");
                    listDevices.get(i).setConnectingDevices(BlueToothDevicesListData.Host);
                    Log.e("BlueToothSearch",listDevices.get(i).getConnectingDevicesState());
                    blueToothDeviceListAdapter.notifyDataSetChanged();
                    Intent intent = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };

    List<BlueToothDevicesListData> listDevices =new ArrayList<BlueToothDevicesListData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetoothsearch);
        mListView = (ListView) findViewById(R.id.lv_bluetooth_search);
        mSearchButton = (Button) findViewById(R.id.btn_buletooth_search);

        blueToothDeviceListAdapter=new BlueToothDeviceListAdapter(listDevices,mHandler,this);
        mListView.setAdapter(blueToothDeviceListAdapter);
        mBlueTooth = new BlueTooth();
        preferences = getSharedPreferences("host", Context.MODE_PRIVATE);
        name = preferences.getString("name","");
        mac = preferences.getString("mac","");
        IntentFilter intent = new IntentFilter();
        intent.addAction(BluetoothDevice.ACTION_FOUND);
        intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(searchDevices, intent);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View view) {
                                                 listDevices.clear();
                                                 setTitle("本机蓝牙地址：" + mBlueTooth.getmBluetoothAdapter().getAddress());
                                                 if (mBlueTooth.getmBluetoothAdapter().isDiscovering()) {
                                                     mBlueTooth.getmBluetoothAdapter().cancelDiscovery();
                                                 }
                                                 mBlueTooth.getmBluetoothAdapter().startDiscovery();
                                                 ToastSimple.makeText(BlueToothSearch.this,"开始搜索",1).show();
                                             }
                                         }
        );
    }

    //接收搜索到的蓝牙设备广播
    private BroadcastReceiver searchDevices = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Object[] lstName = b.keySet().toArray();

            // 显示所有收到的消息及其细节
            for (int i = 0; i < lstName.length; i++) {
                String keyName = lstName[i].toString();
                Log.e(keyName, String.valueOf(b.get(keyName)));
            }
            // 搜索设备时，取得设备的MAC地址
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    if(name.equals(device.getName())){
                        if(mac.equals(device.getAddress())){
                            blueToothDevicesListData=new BlueToothDevicesListData
                                    ("未配对",device.getAddress(),device.getName(),BlueToothDevicesListData.Host);
                            Log.w("blueToothSearch",
                                    "未配对"+" 连接到主控 "+device.getAddress()+" "+device.getName());
                            listDevices.add(blueToothDevicesListData); // 获取设备名称和mac地址
                            blueToothDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                    else {
                    blueToothDevicesListData=new BlueToothDevicesListData
                            ("未配对",device.getAddress(),device.getName(),BlueToothDevicesListData.NULL);
                    Log.w("blueToothSearch",
                            "未配对"+" 未连接到设备"+device.getAddress()+" "+device.getName());
                    listDevices.add(blueToothDevicesListData); // 获取设备名称和mac地址
                    blueToothDeviceListAdapter.notifyDataSetChanged();
                    }
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    if(name.equals(device.getName())){
                        if(mac.equals(device.getAddress())){
                            blueToothDevicesListData=new BlueToothDevicesListData
                                    ("已配对",device.getAddress(),device.getName(),BlueToothDevicesListData.Host);
                            Log.w("blueToothSearch",
                                    "已配对"+" 连接到主控 "+device.getAddress()+" "+device.getName());
                            listDevices.add(blueToothDevicesListData); // 获取设备名称和mac地址
                            blueToothDeviceListAdapter.notifyDataSetChanged();
                        }
                    }
                    else {
                        blueToothDevicesListData = new BlueToothDevicesListData
                                ("已配对", device.getAddress(), device.getName(), BlueToothDevicesListData.NULL);
                        Log.w("blueToothSearch",
                                "已配对" + "未连接到设备" + device.getAddress() + device.getName());
                        listDevices.add(blueToothDevicesListData); // 获取设备名称和mac地址
                        blueToothDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(searchDevices);
        mBlueTooth.getmBluetoothAdapter().cancelDiscovery();
        super.onDestroy();
    }
}
