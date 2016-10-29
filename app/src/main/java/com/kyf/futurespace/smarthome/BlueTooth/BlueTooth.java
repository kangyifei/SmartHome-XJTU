package com.kyf.futurespace.smarthome.BlueTooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.kyf.futurespace.smarthome.utils.Data;
import com.kyf.futurespace.smarthome.utils.HandlerMessageWhat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by kyf on 2016/4/7 0007.
 */
public class BlueTooth {
    final static private String TAG = "bluetooth";
    private BluetoothAdapter mBluetoothAdapter = null;
    private boolean connected = false;
    private BluetoothSocket socket = null;
    private BluetoothDevice mBluetoothDevice = null;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Handler mHandler;


    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public BluetoothDevice getmBluetoothDevice() {
        return mBluetoothDevice;
    }

    public void setmBluetoothDevice(BluetoothDevice mBluetoothDevice) {
        this.mBluetoothDevice = mBluetoothDevice;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public BlueTooth() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    public BlueTooth(Handler handler) {
        this.mHandler = handler;
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }

    //连接到主机函数

    public BluetoothSocket startConnectToServerSocketAndReturnSocket(String macAddress) {
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(macAddress);
        mBluetoothAdapter.cancelDiscovery();
        try {
            this.socket = mBluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            Thread connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    // Block until server connection accepted.
                    try {
                        Log.e("BlueTooth,连接到主机函数", "连接中");
                        socket.connect();
                        connected = true;
                        mHandler.obtainMessage(HandlerMessageWhat.HostConnection_FINISHED).
                                sendToTarget();
                        Log.w("BlueTooth,连接到主机函数", "连接成功");

                    } catch (IOException e) {
                        mHandler.obtainMessage(HandlerMessageWhat.HostConnection_FAILED).
                                sendToTarget();
                        e.printStackTrace();
                    }
                }
            });
            connectThread.start();
            return socket;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Bluetooth client I/O Exception", e);
            return socket;
        }
    }


    public void sendString(String mString) throws IOException {
        byte[] msgBuffer;
        String message;
        OutputStream mOutStream = null;
        message = mString;
        Log.w(TAG, "字符串发送函数开始工作");
        Data data = Data.getInstance();
        String mac = data.getMac();
        BluetoothSocket socket = data.getDatasocket();
        Log.w(TAG, "字符串发送函数Mac:" + mac);
        if ((socket == null) || (!socket.isConnected())) {
            Log.w(TAG, "字符串发送函数开始重新获取SOCKET");
            if (mac.length() != 0) {
                Log.w(TAG, "字符串发送函数存在主控MAC：" + mac);
                socket = startConnectToServerSocketAndReturnSocket(mac);
                Log.w(TAG, "字符串发送函数重新获取SOCKET成功");
            } else {
                Log.e(TAG, "字符串发送函数不存在Socket");
                return;
            }
        }
        Log.w(TAG, "字符串发送函数SOCKET获取完成");
        try {
            mOutStream = socket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Output stream creation failed.", e);
        }
        msgBuffer = message.getBytes();
        try {
            mOutStream.write(msgBuffer);
            Log.w("发送字符串函数", "发送完成");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception during write.", e);
        }
    }


    //接收信息

    public String getString() throws IOException {
        InputStream inputStream;
        String order = "";
        String mac = Data.getMac();
        BluetoothSocket socket = Data.getDatasocket();
        //判断SOCKET是否过期需要重连
        if ((socket == null) || (!socket.isConnected())) {
            if (mac.length() != 0) {
                socket = startConnectToServerSocketAndReturnSocket(mac);
            } else {
                return "";
            }
        }
        try {
            //获取输入流
            inputStream = socket.getInputStream();
            //解析输入流
            if (inputStream.available() > 0) {
                order = new BufferedReader(new InputStreamReader(inputStream)).readLine();
                return order;
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Message receiving failed", e);
            return "";
        }
    }


    public void cancel() {
        try {
            socket.close();
            socket = null;
            mBluetoothAdapter.disable();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connected = false;
        }
    }
}
