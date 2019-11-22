package com.orange.tpms.ue.kt_frag


import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.orange.blelibrary.blelibrary.RootFragement
import com.orange.tpms.R
import kotlinx.android.synthetic.main.fragment_frag__sounds.view.*

class Frag_Sounds : RootFragement() {
    lateinit var sbSounds: SeekBar//Sounds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      rootview=inflater.inflate(R.layout.fragment_frag__sounds, container, false)
        sbSounds=rootview.findViewById(R.id.sb_sounds)
        rootview.iv_check.setOnClickListener {
            it.setSelected(!it.isSelected())
        }
        initView()
        return rootview
    }
    /**
     * 初始化页面
     */
    private fun initView() {
        //设置滑块颜色
        sbSounds.getThumb().setColorFilter(Color.parseColor("#324A56"), PorterDuff.Mode.SRC_ATOP)
        //设置滑块的状态
        setSeekbar(sbSounds)
        //设置滑块监听
        sbSounds.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                adjustStreamVolume(seekBar.progress)
            }
        })
    }

    /**
     * 一个一个调节媒体音量
     */
    private fun adjustStreamVolume(progress: Int) {
        val mAudioManager = activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val current = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
        val sep = progress - current
        for (i in 0 until Math.abs(sep)) {
            mAudioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                if (sep > 0) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI
            )
        }
    }

    /**
     * 更新滑块
     */
    private fun setSeekbar(sbSounds: SeekBar) {
        val mAudioManager = activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        sbSounds.max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
        sbSounds.progress = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
    }

}
