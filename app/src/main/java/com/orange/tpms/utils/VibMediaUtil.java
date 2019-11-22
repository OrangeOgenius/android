package com.orange.tpms.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.orange.tpms.R;

import java.io.IOException;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

/**
 * 媒体与音效,不做单例，因为持有activity的应用会造成内存泄漏
 * Created by haide.yin() on 2019/2/25 10:20.
 */

public class VibMediaUtil {

    private static final long VIBRATE_DURATION = 200L;//震动时长
    private static final float BEEP_VOLUME = 0.10f;//铃声大小

    private Activity activity;
    private MediaPlayer mediaPlayer;//播放器

    public VibMediaUtil(Activity activity){
        this.activity = activity;
    }

    /**
     * 播放音效与震动
     */
    public void playBeepSoundAndVibrate() {
        playBeep();
        playVibrate();
    }

    /**
     * 播放震动
     */
    public void playVibrate() {
        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        //创建一次性振动
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //milliseconds 震动时长（ms）
            //amplitude 振动强度。这必须是1到255之间的值，或者DEFAULT_AMPLITUDE
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(VIBRATE_DURATION, DEFAULT_AMPLITUDE);
            vibrator.vibrate(vibrationEffect);
        } else {
            //低版本支持
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * 释放媒体
     */
    public void release(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 响铃
     */
    public void playBeep() {
        release();
        AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        //支持铃声才初始化
        if (audioService.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && mediaPlayer == null && activity != null) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // 使用系统的媒体音量控制
                AudioAttributes attributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build();
                mediaPlayer.setAudioAttributes(attributes);
            } else {
                //低版本支持
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            //播放结束
            mediaPlayer.setOnCompletionListener(mp -> {

            });
            AssetFileDescriptor file = activity.getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }
}
