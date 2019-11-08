package com.orange.tpms.ue.frag;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.orange.tpms.R;
import com.orange.tpms.adapter.YTReadSensorAdapter;
import com.orange.tpms.app.TPMSApp;
import com.orange.tpms.bean.YTReadSensorBean;
import com.orange.tpms.bean.YTSelectModelBean;
import com.orange.tpms.lib.api.SensorHandler;
import com.orange.tpms.lib.driver.source.SensorSource;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.utils.StringUtils;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;
import com.orange.tpms.widget.YTHighCarWidget;
import com.orange.tpms.widget.YTNormalCarWidget;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.de.rocket.ue.injector.BindView;

import bean.hardware.SensorDataBean;

import static android.os.VibrationEffect.DEFAULT_AMPLITUDE;

/**
 * 读取传感器页面
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_yt_read_sensor extends Frag_base {

    public static String TAG = Frag_yt_read_sensor.class.getName();
    private static final long VIBRATE_DURATION = 200L;//震动时长
    private static final float BEEP_VOLUME = 0.10f;//铃声大小

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.rv_read_sensor)
    RecyclerView rvReadSensor;//ReadSensor
    @BindView(R.id.cw_normal_car)
    YTNormalCarWidget normalCarWidget;//NormalCar
    @BindView(R.id.cw_high_car)
    YTHighCarWidget highCarWidget;//HighCar
    @BindView(R.id.tv_content)
    TextView tvContent;//提示语言
    @BindView(R.id.bn_test)
    Button bnTest;
    @BindView(R.id.bn_test_flash)
    Button bnTestFlash;
    @BindView(R.id.bn_copy)
    Button bnTestCopy;
    @BindView(R.id.lw_loading)
    LoadingWidget lwLoading;//转圈圈

    private SensorHandler sensorHandler;
    private LinearLayoutManager layoutManager;//列表表格布局
    private YTReadSensorAdapter readSensorAdapter;//Adapter
    private YTSelectModelBean selectModelBean = new YTSelectModelBean();
    private List<YTReadSensorBean> sensorList =  new ArrayList<>();
    private MediaPlayer mediaPlayer;//播放器
    private final MediaPlayer.OnCompletionListener beepListener = (mediaPlayer) -> mediaPlayer.seekTo(0);//播放完铃声回到起点

    @Override
    public int onInflateLayout() {
        return R.layout.frag_yt_read_sensor;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initBeepSound();
        initHardware();
    }

    @Override
    public void onNexts(Object o) {
        if(o instanceof YTSelectModelBean){
            selectModelBean = (YTSelectModelBean)o;
            initSelectMode();
        }
    }

    @Override
    public boolean onBackPresss() {
        if(lwLoading.isShown()){
            lwLoading.hide();
        }else{
            back();
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // TODO: 2019/6/27 zeqiang 这里释放资源
        //hardwareApp.switchScan(false);// 关闭扫码
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * 显示图片信息
     * @param ytReadSensorBean 信息
     * @param index 位置
     */
    private void updateSensor(YTReadSensorBean ytReadSensorBean,int index){
        boolean isNormal = ytReadSensorBean.getIdNumber() != null;
        if(!isNormal){
            tvContent.setTextColor(getResources().getColor(R.color.color_red));
            tvContent.setText(String.valueOf("传感器型号错误："+ytReadSensorBean.getName()));
        }else{
            tvContent.setTextColor(getResources().getColor(R.color.color_FF101010));
            tvContent.setText("请依照轮位触发传感器");
        }
        if(selectModelBean.getType().equals(YTSelectModelBean.TYPR_NORMAL)){//普通车
            YTNormalCarWidget.STATUS status = isNormal?YTNormalCarWidget.STATUS.NORMAL:YTNormalCarWidget.STATUS.BAD;
            normalCarWidget.setCarStatus(status,index);
        }else if(selectModelBean.getType().equals(YTSelectModelBean.TYPR_HIGH)) {//高级车
            YTHighCarWidget.STATUS status = isNormal?YTHighCarWidget.STATUS.NORMAL:YTHighCarWidget.STATUS.BAD;
            highCarWidget.setCarStatus(status,index);
        }
    }

    /**
     * 重置轮子
     * @param index 位置
     */
    private void resetWheel(int index){
        if(selectModelBean.getType().equals(YTSelectModelBean.TYPR_NORMAL)){//普通车
            normalCarWidget.setCarStatus(YTNormalCarWidget.STATUS.DEFAULT,index);
        }else if(selectModelBean.getType().equals(YTSelectModelBean.TYPR_HIGH)) {//高级车
            highCarWidget.setCarStatus(YTHighCarWidget.STATUS.DEFAULT,index);
        }
    }

    /**
     * 选择显示车类型
     */
    private void initSelectMode(){
        //添加标题
        sensorList.add(new YTReadSensorBean(true,"轮位","ID号码","胎压","胎温","电池",-1));
        if(selectModelBean.getType().equals(YTSelectModelBean.TYPR_NORMAL)){
            normalCarWidget.setVisibility(View.VISIBLE);
            highCarWidget.setVisibility(View.GONE);
            sensorList.add(new YTReadSensorBean(false,YTNormalCarWidget.LOCATION.FIRST.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTNormalCarWidget.LOCATION.SECONG.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTNormalCarWidget.LOCATION.THIRD.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTNormalCarWidget.LOCATION.FORTH.toString(),"","","","",-1));
        }else if(selectModelBean.getType().equals(YTSelectModelBean.TYPR_HIGH)){
            normalCarWidget.setVisibility(View.GONE);
            highCarWidget.setVisibility(View.VISIBLE);
            sensorList.add(new YTReadSensorBean(false,YTHighCarWidget.LOCATION.FIRST.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTHighCarWidget.LOCATION.SECONG.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTHighCarWidget.LOCATION.THIRD.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTHighCarWidget.LOCATION.FORTH.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTHighCarWidget.LOCATION.FIFTH.toString(),"","","","",-1));
            sensorList.add(new YTReadSensorBean(false,YTHighCarWidget.LOCATION.SIXTH.toString(),"","","","",-1));
        }
        readSensorAdapter.setItems(sensorList);
        readSensorAdapter.notifyDataSetChanged();
    }

    /**
     * 点击某个轮子
     * @param name 轮子的名字
     * @param index 轮子对应的序号
     */
    private void clickWheel(String name,int index){
        //铃声与震动
        playBeepSoundAndVibrate();
        //重置轮子
        resetWheel(index);
        //重置传感器数据数据
        if(sensorList.size() -1 >= index){//存在就删除
            sensorList.remove(index);
            sensorList.add(index,new YTReadSensorBean(false,name,"","","","",-1));
            readSensorAdapter.setItems(sensorList);
            readSensorAdapter.notifyDataSetChanged();
        }
        //提示loading
        lwLoading.setLoadingText("读取中...");
        lwLoading.show();
        sensorHandler.readSensor(1, /*selectModelBean.getHex()*/"2C", sensorDataBean -> activity.runOnUiThread(() -> {
            activity.runOnUiThread(() -> {
                lwLoading.hide();
                YTReadSensorBean ytReadSensorBean;
                if (sensorDataBean == null || sensorDataBean.getCmdCode() != 1) {
                    toast("响应错误");
                    ytReadSensorBean = new YTReadSensorBean(false,name,null,null,null,null,-1);
                } else {
                    String idNumber = sensorDataBean.getSensor_id();
                    String tirePress = String.valueOf(sensorDataBean.getKpa());
                    String tireTemp = String.valueOf(sensorDataBean.getTemp());
                    int batLevel;
                    int batV = sensorDataBean.getV();
                    if(batV >= 29){
                        batLevel = 100;
                    }else if(batV >= 27){
                        batLevel = 50;
                    }else{
                        batLevel = 20;
                    }
                    ytReadSensorBean = new YTReadSensorBean(false,name,idNumber,tirePress,tireTemp,null,batLevel);
                }
                if(sensorList.size() -1 >= index){//存在就删除
                    sensorList.remove(index);
                }
                sensorList.add(index,ytReadSensorBean);
                final YTReadSensorBean finalSensorBean = ytReadSensorBean;
                readSensorAdapter.setItems(sensorList);
                readSensorAdapter.notifyDataSetChanged();
                updateSensor(finalSensorBean,index);
            });
        }),"00");
    }

    /**
     * 初始化硬件
     */
    private void initHardware () {
        // 打开GPIO电压
        HardwareApp.getInstance().initWithCb(activity, new HardwareApp.InitCb() {
            @Override
            public void onStart() {
                lwLoading.setLoadingText("握手中...");
                lwLoading.show();
            }

            @Override
            public void pingReceive(int ret) {
                toast("与模块联机状态:"+ret);
                lwLoading.hide();
            }
        });
        this.sensorHandler = HardwareApp.getInstance().sensorHandler;
    }

    /**
     * 播放音效与震动
     */
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

    /**
     * 初始化响铃
     */
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

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题栏
        twTitle.setTvTitle("检查传感器");
        twTitle.setBgColor(R.color.color_yt);
        twTitle.setOnBackListener((view) -> back());
        //配置RecyclerView,每行是哪个元素
        if (layoutManager == null) {
            layoutManager = new LinearLayoutManager(activity);
        }
        rvReadSensor.setLayoutManager(layoutManager);
        readSensorAdapter = new YTReadSensorAdapter(activity);
        readSensorAdapter.setOnItemClickListener((pos, readSensorBean) -> clickWheel(readSensorBean.getName(),pos));
        rvReadSensor.setAdapter(readSensorAdapter);
        //轮子的点击事件
        normalCarWidget.setOnWheelClickListener((location,index) -> clickWheel(location.toString(),index));
        highCarWidget.setOnWheelClickListener((location,index) -> clickWheel(location.toString(),index));

        // 测试按钮
        bnTest.setOnClickListener(v -> {
          // 拷贝传感器id
            /*YTReadSensorBean firstBean = sensorList.get(1);
            YTReadSensorBean secondBean = sensorList.get(2);
            if (firstBean == null || secondBean == null || firstBean.getIdNumber() == null ||
                    secondBean.getIdNumber() == null || firstBean.getIdNumber().equals("") ||
                    secondBean.getIdNumber().equals("")) {
                // 存在为空则报错
                toast("默认是将1号轮id替换2号轮id");
                return ;
            }*/
            /* sensorHandler.readSensor(1, "2C", sensorVersionBean -> {
                // 将第一个拷到第二个
                sensorHandler.copySensorId(firstBean.getIdNumber(), secondBean.getIdNumber(), ret -> {
                    // 结果
                });
            });*/
            /*String content = sensorHandler.getSensorByteFromStrStream(hardwareApp.app, "2C_V0.3.s19");
            byte[] buffer = StringUtils.hexStrToByteArray(content);
            Log.d(TAG, "buffer len: "+buffer.length);
            // 烧录传感器
            sensorHandler.writeSensorFirmwareSetting(buffer, (ret, sensorWriteInfoBean) -> {
                // 打印
                if (ret == 0) {
                    Log.d(TAG, String.format("writeSensorFirmareSetting => byteNum%s, num:%s", 2048,
                            1));
                    sensorHandler.writeSensorFirmware(buffer, 0, sensorWriteInfoBean.getByteNum());
                    return ;
               }
            });*/
            sensorHandler.writeSensorFirmwareWithProgress(1, new SensorHandler.FlashWriteProgress() {

                @Override
                public void start(int total) {
                    Log.d(TAG, String.format("total bytes %s, has finish", total));
                }

                @Override
                public void progress(int progress, int total) {
                    Log.d(TAG, String.format("total bytes %s, progress: %s", total, progress));
                }

                @Override
                public void finish(int total) {
                    Log.d(TAG, String.format("total bytes %s, has finish", total));
                }

                @Override
                public void fail(int errcode, int start, int total) {
                    Log.d(TAG, String.format("total bytes %s, fail on %s, errcode is %s", total,
                            start, errcode));
                }
            },"");
            // sensorHandler.writeSensorFirmware(buffer, 0, 200);
            /*sensorHandler.writeFlashWithReboot (new SensorHandler.FlashWriteProgress() {

                @Override
                public void start(int total) {
                    Log.d(TAG, String.format("total bytes %s, has finish", total));
                }

                @Override
                public void progress(int progress, int total) {
                    Log.d(TAG, String.format("total bytes %s, progress: %s", total, progress));
                }

                @Override
                public void finish(int total) {
                    Log.d(TAG, String.format("total bytes %s, has finish", total));
                }

                @Override
                public void fail(int errcode, int start, int total) {
                    Log.d(TAG, String.format("total bytes %s, fail on %s, errcode is %s", total,
                            start, errcode));
                }
            });*/
        });
        bnTestFlash.setOnClickListener(v -> {
            // 拷贝传感器id
            /* sensorHandler.readSensor(1, "2C", sensorVersionBean -> {
                // 将第一个拷到第二个
                sensorHandler.copySensorId(firstBean.getIdNumber(), secondBean.getIdNumber(), ret -> {
                    // 结果
                });
            });*/
            /*String content = sensorHandler.getSensorByteFromStrStream(hardwareApp.app, "2C_V0.3.s19");
            byte[] buffer = StringUtils.hexStrToByteArray(content);
            Log.d(TAG, "buffer len: "+buffer.length);
            // 烧录传感器
            sensorHandler.writeSensorFirmwareSetting(buffer, (ret, sensorWriteInfoBean) -> {
                // 打印
                if (ret == 0) {
                    Log.d(TAG, String.format("writeSensorFirmareSetting => byteNum%s, num:%s", 2048,
                            1));
                    sensorHandler.writeSensorFirmware(buffer, 0, sensorWriteInfoBean.getByteNum());
                    return ;
               }
            });*/
            // sensorHandler.writeSensorFirmware(buffer, 0, 200);
            sensorHandler.writeFlashWithReboot (new SensorHandler.FlashWriteProgress() {

                @Override
                public void start(int total) {
                    Log.d(TAG, String.format("total bytes %s, has finish", total));
                }

                @Override
                public void progress(int progress, int total) {
                    Log.d(TAG, String.format("total bytes %s, progress: %s", total, progress));
                }

                @Override
                public void finish(int total) {
                    Log.d(TAG, String.format("total bytes %s, has finish", total));
                }

                @Override
                public void fail(int errcode, int start, int total) {
                    Log.d(TAG, String.format("total bytes %s, fail on %s, errcode is %s", total,
                            start, errcode));
                }
            });
        });
        bnTestCopy.setOnClickListener(v -> {
            // 拷贝传感器id
            YTReadSensorBean firstBean = sensorList.get(1);
            YTReadSensorBean secondBean = sensorList.get(2);
            if (firstBean == null || secondBean == null || firstBean.getIdNumber() == null ||
                    secondBean.getIdNumber() == null || firstBean.getIdNumber().equals("") ||
                    secondBean.getIdNumber().equals("")) {
                // 存在为空则报错
                toast("默认是将1号轮id替换2号轮id，请保证1号轮和2号轮已经读取");
                return ;
            }
            sensorHandler.readSensor(1, "2C", sensorVersionBean -> {
                // 将第一个拷到第二个
                sensorHandler.copySensorId(firstBean.getIdNumber(), secondBean.getIdNumber(), ret -> {
                    // 结果
                });
            },"00");
        });
    }
}
