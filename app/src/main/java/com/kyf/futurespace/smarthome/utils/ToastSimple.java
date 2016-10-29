package com.kyf.futurespace.smarthome.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kyf on 2016/4/12 0012.
 */
public class ToastSimple {
    private double time;
    private static Handler handler;
    private Timer showTimer;
    private Timer cancelTimer;

    private Toast toast;

    private ToastSimple(){
        showTimer = new Timer();
        cancelTimer = new Timer();
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setToast(Toast toast){
        this.toast = toast;
    }

    public static ToastSimple makeText(Context context, String text, double time){
        ToastSimple toast1= new ToastSimple();
        toast1.setTime(time);
        toast1.setToast(Toast.makeText(context, text, Toast.LENGTH_SHORT));
        handler = new Handler(context.getMainLooper());
        return toast1;
    }

    public void show(){
        toast.show();
        if(time > 2){
            showTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new ShowRunnable());
                }
            }, 0, 1900);
        }
        cancelTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new CancelRunnable());
            }
        }, (long)(time * 1000));
    }

    private class CancelRunnable implements Runnable {
        @Override
        public void run() {
            showTimer.cancel();
            toast.cancel();
        }
    }

    private class ShowRunnable implements Runnable {
        @Override
        public void run() {
            toast.show();
        }
    }
}
