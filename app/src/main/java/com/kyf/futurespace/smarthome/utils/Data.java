package com.kyf.futurespace.smarthome.utils;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.baidu.apistore.sdk.ApiStoreSDK;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.kyf.futurespace.smarthome.ChatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by kyf on 2016/6/30 0030.
 */
public class Data extends Application {
    private static BluetoothSocket datasocket = null;
    private static String mac = "";
    private static String name = "";
    private static boolean conditionAndOrderConnected = false;
    private static boolean connectedToHost = false;
    private static boolean isRequestToWake = false;
    private static boolean isSongRequest = false;
    private static boolean isTemperature = false;
    private static int temperature = 0;
    private static int temperatureToAC = 100;

    private static HashMap<String, Task> taskWaitingForBluetooth = new HashMap<>();
    private static Data instance;


    @Override
    public void onCreate() {
        try {
            SpeechUtility.createUtility(this, SpeechConstant.APPID + "=56d5b0b0");
        } catch (Exception e) {
            ToastSimple.makeText(this, "讯飞语音功能初始化失败!", 1).show();
        }
        ToastSimple.makeText(this, "讯飞语音功能初始化成功!", 0.2).show();
        ApiStoreSDK.init(this, "66f37dcede985a2d36bf9617284e8ed3");
        super.onCreate();
    }

    public static int getTemperature() {
        return temperature;
    }

    public static void setTemperature(int temperature) {
        Data.temperature = temperature;
    }

    public static int getTemperatureToAC() {
        return temperatureToAC;
    }

    public static void setTemperatureToAC(int temperatureToAC) {
        Data.temperatureToAC = temperatureToAC;
    }

    public static boolean isTaskWaitingExisting(String string) {
        return taskWaitingForBluetooth.containsKey(string);
    }

    public static void addTaskWaiting(String condition, Task task) {
        taskWaitingForBluetooth.put(condition, task);
    }

    public static ArrayList<Task> getTaskWaitingList() {
        ArrayList<Task> tasks = new ArrayList<>();
        for (Iterator it = taskWaitingForBluetooth.keySet().iterator(); it.hasNext(); ) {
            tasks.add(taskWaitingForBluetooth.get(it.next()));
        }
        return tasks;
    }

    public static Task getTaskWaiting(String string) {
        return taskWaitingForBluetooth.get(string);
    }

    public static void deleteTaskWaiting(String string) {
        taskWaitingForBluetooth.remove(string);
    }

    public static void clearAllTasksWaiting() {
        taskWaitingForBluetooth.clear();
    }

    public static int getTasksWaitingNum() {
        return taskWaitingForBluetooth.size();
    }

    public static boolean isTemperature() {
        return isTemperature;
    }

    public static void setIsTemperature(boolean isTemperature) {
        Data.isTemperature = isTemperature;
    }

    public static synchronized Data getInstance() {
        if (instance == null) {
            instance = new Data();
        }
        return instance;
    }

    public static boolean isSongRequest() {
        return isSongRequest;
    }

    public static void setIsSongRequest(boolean isSongRequest) {
        Data.isSongRequest = isSongRequest;
    }

    public static boolean isRequestToWake() {
        return isRequestToWake;
    }

    public static void setIsRequestToWake(boolean isRequestToWake) {
        Data.isRequestToWake = isRequestToWake;
    }

//    public static Task getOrderWaitingForBluetooth() {
//        return orderWaitingForBluetooth;
//    }
//
//    public static void setOrderWaitingForBluetooth(Task orderWaittingForBluetooth) {
//        Data.orderWaitingForBluetooth = orderWaittingForBluetooth;
//    }

    public static boolean isConnectedToHost() {
        return connectedToHost;
    }

    public static void setConnectedToHost(boolean connectedToHost) {
        Data.connectedToHost = connectedToHost;
    }

//    public static void connectConditionToOrder(String condition, String order) {
//        conditionConnectedOrder.put(condition, order);
//        return;
//    }
//
//    public static boolean isTheConditionConnectedToOrder(String condition) {
//        return conditionConnectedOrder.containsKey(condition);
//    }
//
//    public static String getTheOrderConnectedToTheConditon(String condition) {
//        return conditionConnectedOrder.get(condition);
//    }
//
//    public static void deleteConnectionBetweenConditionAndOrder(String condition) {
//        conditionConnectedOrder.remove(condition);
//    }
//
//    public static void clearAllConnectionsBetweenConditionAndOrder() {
//        conditionConnectedOrder.clear();
//    }

    public static boolean isConditionAndOrderConnected() {
        return conditionAndOrderConnected;
    }

    public static void setConditionAndOrderConnected(boolean conditionAndOrderConnected) {
        Data.conditionAndOrderConnected = conditionAndOrderConnected;
    }

    public static BluetoothSocket getDatasocket() {
        return datasocket;
    }

    public static void setDatasocket(BluetoothSocket datasocket) {
        Data.datasocket = datasocket;
    }

    public static String getMac() {
        return mac;
    }

    public static void setMac(String mac) {
        Data.mac = mac;
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Data.name = name;
    }

    public static void resetHost() {
        mac = "";
        name = "";
        connectedToHost = false;
    }
}
