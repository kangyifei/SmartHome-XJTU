package com.kyf.futurespace.smarthome.BlueTooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.smarthome.R;
import com.kyf.futurespace.smarthome.utils.Data;
import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;
import com.kyf.futurespace.smarthome.utils.ToastSimple;

import java.util.List;

/**
 * Created by kyf on 2016/4/25 0025.
 */
public class BlueToothDeviceListAdapter extends BaseAdapter {
    private List<BlueToothDevicesListData> lists;
    private Activity mContext;
    private Handler mHandler;

    public BlueToothDeviceListAdapter(List<BlueToothDevicesListData> lists, Handler handler, Activity Context) {
        this.lists = lists;
        this.mContext = Context;
        this.mHandler = handler;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_bluetoothdevice,
                    viewGroup, false);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.blueToothDevices_name);
            holder.mac = (TextView) view.findViewById(R.id.blueToothDevices_mac);
            holder.connectingDevices = (TextView) view.findViewById(R.id.blueToothDevices_connectingDevices);
            holder.state = (TextView) view.findViewById(R.id.blueToothDevices_bond);
            holder.btnConnect = (Button) view.findViewById(R.id.blueToothDevices_connectBtn);
            holder.mobileDevice = (Spinner) view.findViewById(R.id.blueToothDevices_deviceSpinner);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText((CharSequence) lists.get(i).getName());
        holder.mac.setText((CharSequence) lists.get(i).getMac());
        holder.connectingDevices.setText((CharSequence) lists.get(i).getConnectingDevicesState());
        holder.state.setText((CharSequence) lists.get(i).getState());
        holder.btnConnect.setOnClickListener(new MyClickListener(i));
        return view;
    }

    private static class ViewHolder {
        TextView name;
        TextView mac;
        TextView connectingDevices;
        TextView state;
        Button btnConnect;
        Spinner mobileDevice;
    }

    private class MyClickListener implements View.OnClickListener {
        int mPosition;

        public MyClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(mContext).setTitle("设置主控")
                    .setMessage("警告，系统只能存在一个主控，以前可能存在的主控将被删除")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences preferences = mContext
                                    .getSharedPreferences("host", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            String name = lists.get(mPosition).getName();
                            String mac = lists.get(mPosition).getMac();
                            editor.putString("name", name);
                            editor.putString("mac", mac);
                            editor.commit();
                            Data data = Data.getInstance();
                            data.setName(name);
                            data.setMac(mac);
                            Log.e("hostname", name);
                            Log.e("hostMac", mac);
                            Message msg = new Message();
                            msg.what = HandlerMessageWhat.SetHostDevice_FINISHED;
                            msg.obj = mPosition;
                            mHandler.sendMessage(msg);
                            ToastSimple.makeText(mContext, "设置成功", 1).show();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

        }
    }

}
