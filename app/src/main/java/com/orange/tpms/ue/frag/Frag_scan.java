/*package com.orange.tpms.ue.frag;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.BindView;
import com.orange.tpms.R;
import com.orange.tpms.widget.TitleWidget;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.camera.CameraManager;
import com.uuzuche.lib_zxing.decoding.CaptureActivityHandler;
import com.uuzuche.lib_zxing.decoding.InactivityTimer;
import com.uuzuche.lib_zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.regex.Pattern;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

*//**
 * 通用扫码界面
 * 目前用不到扫码的逻辑，行去掉，如果需要用上只需要屏蔽注释，导入zxing.aar即可
 * 需要在Application初始化 ZXingLibrary.initDisplayOpinion(this);
 * Created by haide.yin() on 2019/1/23 15:38.
 *//*
public class Frag_scan extends RoFragment implements SurfaceHolder.Callback {

    public static final int SCAN_CAMERA_SUCCESS = 1;//摄像头扫码成功
    public static final int SCAN_CAMERA_FAILED = -1;//摄像头扫码失败
    public static final int SCAN_ALBEM_SUCCESS = 2;//相册扫码成功
    public static final int CAMERA_INIT_FAILED = -3;//摄像头初始化失败
    private static final long VIBRATE_DURATION = 200L;//震动时长
    private static final float BEEP_VOLUME = 0.10f;//铃声大小
    private static final int DELAY_SCAN = 1000;//延迟重新扫描时间

    @BindView(R.id.viewfinder_view)
    ViewfinderView viewfinderView;
    @BindView(R.id.preview_view)
    SurfaceView surfaceView;
    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title

    private CaptureActivityHandler handler;//处理流数据的Handler
    private boolean hasSurface = false;//Surface是否成功创建，没有之前不能初始化摄像头
    private MediaPlayer mediaPlayer;//播放器
    private SurfaceHolder surfaceHolder;//视频容器
    private Camera camera;//摄像头
    private final MediaPlayer.OnCompletionListener beepListener = (mediaPlayer) -> mediaPlayer.seekTo(0);//播放完铃声回到起点
    private Handler delayHandler = new Handler();
    private InactivityTimer inactivityTimer;

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    *//**
     * 初始化View
     *//*
    private void initView() {
        //初始化SurfaceHolder
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(Frag_scan.this);
        inactivityTimer = new InactivityTimer(this.getActivity());
        //初始化摄像头
        CameraManager.init(activity.getApplication());
        //初始化响铃与震动
        initBeepSound();
        //返回
        twTitle.setOnBackListener((view) -> back());
    }

    @Override
    public int onInflateLayout() {
        return R.layout.frag_scan;
    }

    @Override
    public void onNexts(Object o) {
        //有权限才能开启扫码
        askPermissison(new String[]{Manifest.permission.CAMERA}, (i, b) -> {
            if(!b){
                back();
            }else{
                delayHandler.postDelayed(() -> {
                    //必须要等sufaceHolder回调surfaceCreated() 才去initCamera(),因为camera的初始化和holder的
                    if (hasSurface) {
                        beginScan();
                    }
                }, 20);//这个延迟是为了防止部分手机没初始化完毕的时候启动摄像头
            }
        });
    }

    @Override
    public boolean isReloadData() {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();//页面Pause的时候需要停止扫描Handler
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (inactivityTimer != null) {
            inactivityTimer.shutdown();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopScan();//页面Pause的时候需要停止扫描Handler
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            //页面消失的时候清除队列，防止内存泄漏
            delayHandler.removeMessages(0);
            stopScan();//切换fragment的时候需要停止扫描Handler
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceHolder = holder;
        if (!hasSurface) {
            hasSurface = true;
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                beginScan();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
        stopScan();
    }

    *//**
     * 初始化摄像头,开启扫码
     *//*
    private void beginScan() {
        try {
            if (surfaceHolder != null) {
                CameraManager.get().openDriver(surfaceHolder);
                camera = CameraManager.get().getCamera();
                if (handler != null) {
                    handler.quitSynchronously();
                    handler = null;
                }
                handler = new CaptureActivityHandler(null, null, viewfinderView, new CodeUtils.AnalyzeCallback() {
                    @Override
                    public void onAnalyzeSuccess(Bitmap mBitmap, String result) {
                        if (inactivityTimer != null) {
                            inactivityTimer.onActivity();
                        }
                        Pattern pattern = Pattern.compile("[0-9]*");
                        if (pattern.matcher(result).matches()) {//有可能扫描出来的是一串数字
                            toast("扫出一串数字", 2000);
                            beginScanDelay();//重新开始扫描
                        } else {
                            onScanResult(true, SCAN_CAMERA_SUCCESS, result);
                        }
                    }

                    @Override
                    public void onAnalyzeFailed() {
                        //解码失败
                        onScanResult(false, SCAN_CAMERA_FAILED, null);
                    }
                });
            }
        } catch (Exception e) {
            onScanResult(false, CAMERA_INIT_FAILED, null);
        }
    }

    *//**
     * 延迟初始化摄像头,开启扫码
     *//*
    private void beginScanDelay() {
        delayHandler.postDelayed(this::beginScan, DELAY_SCAN);
    }

    *//**
     * 停止扫码
     *//*
    private void stopScan() {
        if (camera != null) {
            if (CameraManager.get().isPreviewing()) {
                if (!CameraManager.get().isUseOneShotPreviewCallback()) {
                    camera.setPreviewCallback(null);
                }
                camera.stopPreview();
                CameraManager.get().getPreviewCallback().setHandler(null, 0);
                CameraManager.get().getAutoFocusCallback().setHandler(null, 0);
                CameraManager.get().setPreviewing(false);
            }
        }
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    *//**
     * 扫描结果统一处理
     *
     * @param resultcode 状态码
     * @param result     扫描数据
     *//*
    public void onScanResult(boolean success, int resultcode, String result) {
        //处理二维码
        if (success && (resultcode == Frag_scan.SCAN_CAMERA_SUCCESS || resultcode == Frag_scan.SCAN_ALBEM_SUCCESS)) {
            //播放铃声以及震动
            playBeepSoundAndVibrate();
            back(false,result);
        } else {
            toast("扫码失败");
        }
    }

    *//**
     * 初始化响铃
     *//*
    private void initBeepSound() {
        AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        //支持铃声才初始化
        if (audioService.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && mediaPlayer == null && getActivity() != null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
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
            mediaPlayer.setOnCompletionListener(beepListener);
            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    *//**
     * 播放音效与震动
     *//*
    private void playBeepSoundAndVibrate() {
        AudioManager audioService = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioService.getRingerMode() == AudioManager.RINGER_MODE_NORMAL && mediaPlayer != null) {
            mediaPlayer.start();
        }
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
}*/
