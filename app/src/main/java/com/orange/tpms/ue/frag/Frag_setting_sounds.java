package com.orange.tpms.ue.frag;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.view.View;
import android.widget.SeekBar;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;

import com.de.rocket.ue.injector.BindView;

/**
 * 设置详情页
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_setting_sounds extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.sb_sounds)
    SeekBar sbSounds;//Sounds

    @Override
    public int onInflateLayout() {
        return R.layout.frag_setting_sounds;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.iv_check)
    private void check(View view) {
        view.setSelected(!view.isSelected());
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_sounds_vibration);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //设置滑块颜色
        sbSounds.getThumb().setColorFilter(Color.parseColor("#324A56"), PorterDuff.Mode.SRC_ATOP);
        //设置滑块的状态
        setSeekbar(sbSounds);
        //设置滑块监听
        sbSounds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                adjustStreamVolume(seekBar.getProgress());
            }
        });
    }

    /**
     * 一个一个调节媒体音量
     */
    private void adjustStreamVolume(int progress){
        AudioManager mAudioManager =(AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        int sep = progress - current;
        for(int i = 0 ; i < Math.abs(sep);i++){
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,sep > 0 ?AudioManager.ADJUST_RAISE:AudioManager.ADJUST_LOWER,AudioManager.FLAG_SHOW_UI);
        }
    }

    /**
     * 更新滑块
     */
    private void setSeekbar(SeekBar sbSounds){
        AudioManager mAudioManager =(AudioManager)activity.getSystemService(Context.AUDIO_SERVICE);
        sbSounds.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM));
        sbSounds.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
    }
}
