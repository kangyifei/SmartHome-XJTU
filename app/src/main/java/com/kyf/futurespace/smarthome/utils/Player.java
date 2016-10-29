package com.kyf.futurespace.smarthome.utils; /**
 * Created by kyf on 2016/7/28 0028.
 */

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public class Player implements OnCompletionListener,
        OnPreparedListener {

    private MediaPlayer mediaPlayer;// 媒体播放器
    private Handler handler;

    // 初始化播放器
    public Player(Handler handler) {
        super();
        try {
            this.handler=handler;
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
    }


    public void playUrl(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url); // 设置数据源
            Message msg=new Message();
            msg.what=HandlerMessageWhat.SongPlayer_PREPARING;
            handler.sendMessage(msg);
            mediaPlayer.prepare();// prepare自动播放
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // 播放准备
    @Override
    public void onPrepared(MediaPlayer mp) {
        Message msg=new Message();
        msg.what=HandlerMessageWhat.SongPlayer_PLAYING;
        handler.sendMessage(msg);
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        Message msg=new Message();
        msg.what=HandlerMessageWhat.SongPlayer_FINISHED;
        handler.sendMessage(msg);
        Log.e("mediaPlayer", "onCompletion");
    }
}

