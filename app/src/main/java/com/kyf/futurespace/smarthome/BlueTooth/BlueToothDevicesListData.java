package com.kyf.futurespace.smarthome.BlueTooth;

/**
 * Created by kyf on 2016/4/25 0025.
 */
class BlueToothDevicesListData {
    static final int NULL = 0;
    static final int Host = 1;
    private String state;
    private String connectingDevicesState;
    private String Mac;
    private String name;
    private int connectingDevices;

    public BlueToothDevicesListData(String state, String Mac, String name, int connectingDevices) {
        setState(state);
        setMac(Mac);
        setName(name);
        setConnectingDevices(connectingDevices);
        switch (connectingDevices) {
            case 0:
                setConnectingDevicesState("没有连接到设备");
                break;
            case 1:
                setConnectingDevicesState("连接到主控");
                break;
        }
    }

    public int getConnectingDevices() {
        return connectingDevices;
    }

    public void setConnectingDevices(int mConnectingDevices) {
        connectingDevices = mConnectingDevices;
        switch (connectingDevices) {
            case 0:
                setConnectingDevicesState("没有连接到设备");
                break;
            case 1:
                setConnectingDevicesState("连接到主控");
                break;}

    }

    public String getConnectingDevicesState() {
        return connectingDevicesState;
    }

    public void setConnectingDevicesState(String connectingDevicesState) {
        this.connectingDevicesState = connectingDevicesState;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMac() {
        return Mac;
    }

    public void setMac(String mac) {
        Mac = mac;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
