package com.orange.tpms.helper;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.de.rocket.Rocket;
import com.orange.tpms.bean.CheckVersionCoverageReq;
import com.orange.tpms.bean.CheckVersionCoverageResp;
import com.orange.tpms.bean.CheckVersionFlashReq;
import com.orange.tpms.bean.CheckVersionFlashResp;
import com.orange.tpms.bean.CheckVersionSensorReq;
import com.orange.tpms.bean.CheckVersionSensorResp;
import com.orange.tpms.lib.api.Downloader;
import com.orange.tpms.lib.api.MMy;
import com.orange.tpms.lib.api.SensorHandler;
import com.orange.tpms.lib.api.Server;
import com.orange.tpms.lib.db.share.SettingShare;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.utils.FileUtil;

import java.io.File;
import java.net.UnknownHostException;
import java.nio.channels.FileLock;

public class SystemUpdateHelper extends BaseHelper {

    private String TAG = "SystemUpdateHelper";
    //更新文件被移动的路径
    public static String updateFolder = "tpms";
    //flash文件的根目录名称
    public static String flashFile = "update.x2";
    //mmy文件的根目录名称
    public static String mmyFile = "update.xls";
    //sensor文件的根目录名称
    public static String sensorFile = "update.s19";
    //是否完成
    private boolean isFinish;
    //是否是检验本地，true代表本地，false代表网络
    private boolean checkSDCard = true;
    //检测升级的类型
    public enum CHECK_TYPE{
        FLASH,MMY,SENSOR
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SystemUpdateHelper(){
        File sdPath = Environment.getExternalStorageDirectory();
        if(sdPath != null){
            String tpmsPath = sdPath.getAbsolutePath()+ File.separator +updateFolder + File.separator;
            File tpmsFolder = new File(tpmsPath);
            //创建更新目录
            if(!tpmsFolder.exists()){
                tpmsFolder.mkdir();
            }
        }
    }

    /**
     * 设置自动更新
     * @param context 上下文
     * @param autoUpdate 自动更新
     */
    public void setIfSystemAutoUpdate(Context context,boolean autoUpdate){
        SettingShare.setIfSystemAutoUpdate(context,autoUpdate);
    }

    /**
     * 读取自动更新
     * @param context 上下文
     * @return 自动更新
     */
    public boolean getIfSystemAutoUpdate(Context context){
        return SettingShare.getIfSystemAutoUpdate(context);
    }

    /**
     * 恢复出厂设置(删除数据库、删除sharePrefrence)
     */
    public void systemReset(){
        SettingShare.systemReset();
    }

    /**
     * 检测有没有版本更新
     */
    public void checkUpdate(Context context){
        checkFlashUpdate(context);
    }

    /**
     * 检测下一个有没有版本更新
     */
    public void checkNextUpdate(Context context,CHECK_TYPE checkType){
        //Log.d(TAG, String.format("checkNextUpdate: %s", checkType.toString()));
        if(checkType == CHECK_TYPE.FLASH){
            checkMMYUpdate(context);
        }else if(checkType == CHECK_TYPE.MMY){
            checkSensorUpdate(context);
        }else if(checkType == CHECK_TYPE.SENSOR){
            onCheckUpdateFinishNext();
        }
    }

    /**
     * 检测更新Flash
     */
    private void checkFlashUpdate(Context context){
        Log.d(TAG, String.format("checkFlashUpdate: %s", "Flash"));
        new Thread(() -> {
            if(checkSDCard){
                File sdPath = Environment.getExternalStorageDirectory();
                if(sdPath != null){
                    File flashUpdateFile = new File(sdPath.getAbsolutePath()+ File.separator + flashFile);
                    if(flashUpdateFile.exists()){
                        onCheckUpdateNext(CHECK_TYPE.FLASH,true);
                    }else{
                        onCheckUpdateNext(CHECK_TYPE.FLASH,false);
                    }
                }
            }else{
                preRequestNext();
                Server server = new Server(context);
                CheckVersionFlashReq checkVersionFlashReq = new CheckVersionFlashReq();
                checkVersionFlashReq.setSerialNum("99");
                Server.Respond<CheckVersionFlashResp> respond_flash = server.checkVersionFlash(checkVersionFlashReq);
                int status = respond_flash.getStatus();
                finishRequestNext();
                if(status == 200){
                    CheckVersionFlashResp resp_flash = respond_flash.getData();
                    String fileUrl = resp_flash.getFileUrl();
                    onCheckUpdateNext(CHECK_TYPE.FLASH,resp_flash.getNeedUpdate());
                }else{
                    onUpdateProcessFailedNext(CHECK_TYPE.FLASH,"检测Flash失败:"+status);
                }
            }
        }).start();
    }

    /**
     * 下载更新Flash
     */
    public void downloadFlash(Context context){
        //模拟下载进度
        new Thread(() -> {
            if(checkSDCard){
                preRequestNext();
                File sdPath = Environment.getExternalStorageDirectory();
                if(sdPath != null){
                    String flashUpdatePath = sdPath.getAbsolutePath()+ File.separator + flashFile;
                    String targetFlashUpdatePath = sdPath.getAbsolutePath()+ File.separator+updateFolder+File.separator+flashFile;
                    if(FileUtil.copyFile(flashUpdatePath,targetFlashUpdatePath)){
                        onDownloadProgressNext(CHECK_TYPE.FLASH,100f);
                    }else{
                        onUpdateProcessFailedNext(CHECK_TYPE.FLASH,"移动Flash失败");
                    }
                }
                finishRequestNext();
            }else{
                preRequestNext();
                Server server = new Server(context);
                new Downloader().loadFlash(server, "99" , new FileUtil.FilePrograss() {
                    @Override
                    public void progress(int total, int progress) {
                        float finalProgress = (float) progress/total * 100;
                        if(finalProgress >= 100){
                            finish(total);
                        }else{
                            Log.d(TAG, String.format("downloadFlash -> total: %s, progress: %s", total,progress));
                            onDownloadProgressNext(CHECK_TYPE.FLASH,(float) progress/total * 100);
                        }
                    }

                    @Override
                    public void finish(int total) {
                        if(!isFinish){
                            finishRequestNext();
                            Log.d(TAG, String.format("downloadFlash:total: %s, finish", total));
                            onDownloadProgressNext(CHECK_TYPE.FLASH,100f);
                            isFinish = true;
                        }
                    }

                    @Override
                    public void start(int total) {
                        isFinish = false;
                        Log.d(TAG, String.format("downloadFlash:start: %s", total));
                    }

                    @Override
                    public void fail(String msg) {
                        Log.d(TAG, msg);
                        finishRequestNext();
                        onUpdateProcessFailedNext(CHECK_TYPE.FLASH,"下载Flash失败:"+msg);
                    }
                });
            }
        }).start();
    }

    /**
     * 刷Flash固件
     */
    public void updateFlash(){
        Log.d(TAG, "updateFlash");
        Rocket.writeOuterLog("刷Flash固件-updateFlash");
        if(!HardwareApp.getInstance().isEnableHareware()) {//关闭硬件
            onWriteFlashProgressNext(CHECK_TYPE.FLASH,(float) 100);
            return;
        }
        File sdPath = Environment.getExternalStorageDirectory();
        if(sdPath == null){
            return;
        }
        String updatePath = sdPath.getAbsolutePath()+ File.separator + updateFolder + File.separator + flashFile;
        File updateFile = new File(updatePath);
        if(!updateFile.exists()){
            return;
        }
        preRequestNext();
        Log.d(TAG, String.format("updateFlash-updatePath: %s", updatePath));
        int status = HardwareApp.getInstance().sensorHandler.writeFlashWithReboot(updatePath,new SensorHandler.FlashWriteProgress() {

            @Override
            public void start(int total) {
                Log.d(TAG, String.format("updateFlash-start:total bytes %s", total));
                isFinish = false;
                Rocket.writeOuterLog("刷Flash固件-start:"+total);
            }

            @Override
            public void progress(int progress, int total) {
                Log.d(TAG, String.format("updateFlash-progress:total bytes %s, progress: %s", total, progress));
                float finalProgress = (float) progress/total * 100;
                if(finalProgress >= 100){
                    finish(total);
                }else{
                    Rocket.writeOuterLog("刷Flash固件-progress:"+progress + " total："+total);
                    onWriteFlashProgressNext(CHECK_TYPE.FLASH,(float) progress/total * 100);
                }
            }

            @Override
            public void finish(int total) {
                Log.d(TAG, String.format("updateFlash-finish：total bytes %s, has finish", total));
                if(!isFinish){
                    updateFile.delete();
                    finishRequestNext();
                    Rocket.writeOuterLog("刷Flash固件-finish:"+total);
                    onWriteFlashProgressNext(CHECK_TYPE.FLASH,100f);
                    Log.d(TAG, String.format("updateFlash1-isFinish：total bytes %s, has finish", total));
                    isFinish = true;
                }
            }

            @Override
            public void fail(int errcode, int start, int total) {
                Log.d(TAG, String.format("updateFlash-fail：total bytes %s, fail on %s, errcode is %s", total,
                        start, errcode));
                Rocket.writeOuterLog("刷Flash固件-fail:"+String.format("total bytes %s, fail on %s, errcode is %s", total,
                        start, errcode));
                finishRequestNext();
                //暂时不管烧录失败的
                //onUpdateProcessFailedNext(CHECK_TYPE.FLASH,"烧录固件失败:"+errcode);
            }
        });
        Rocket.writeOuterLog("刷Flash固件状态码:"+status);
    }

    /**
     * 检测更新MMY
     */
    private void checkMMYUpdate(Context context){
        Log.d(TAG, String.format("checkMMYUpdate: %s", "MMY"));
        new Thread(() -> {
            if(checkSDCard){
                File sdPath = Environment.getExternalStorageDirectory();
                if(sdPath != null){
                    File mmyUpdateFile = new File(sdPath.getAbsolutePath()+ File.separator + mmyFile);
                    if(mmyUpdateFile.exists()){
                        onCheckUpdateNext(CHECK_TYPE.MMY,true);
                    }else{
                        onCheckUpdateNext(CHECK_TYPE.MMY,false);
                    }
                }
            }else{
                preRequestNext();
                Server server = new Server(context);
                CheckVersionCoverageReq checkVersionCoverageReq = new CheckVersionCoverageReq();
                checkVersionCoverageReq.setSerialNum("99");
                Server.Respond<CheckVersionCoverageResp> respond_sensor = server.checkVersionCoverage(checkVersionCoverageReq);
                int status = respond_sensor.getStatus();
                finishRequestNext();
                if(status == 200){
                    CheckVersionCoverageResp resp_sensor = respond_sensor.getData();
                    String fileUrl = resp_sensor.getFileUrl();
                    onCheckUpdateNext(CHECK_TYPE.MMY,resp_sensor.getNeedUpdate());
                }else{
                    onUpdateProcessFailedNext(CHECK_TYPE.MMY,"检测MMY失败:"+status);
                }
            }
        }).start();
    }

    /**
     * 下载MMY
     */
    public void downloadMMY(Context context){
        //模拟下载进度
        new Thread(() -> {
            if(checkSDCard){
                preRequestNext();
                File sdPath = Environment.getExternalStorageDirectory();
                if(sdPath != null){
                    String mmyUpdatePath = sdPath.getAbsolutePath()+ File.separator + mmyFile;
                    String targetMmyUpdatePath = sdPath.getAbsolutePath()+ File.separator+updateFolder+File.separator+mmyFile;
                    if(FileUtil.copyFile(mmyUpdatePath,targetMmyUpdatePath)){
                        onDownloadProgressNext(CHECK_TYPE.MMY,100f);
                    }else{
                        onUpdateProcessFailedNext(CHECK_TYPE.MMY,"移动MMY失败");
                    }
                }
                finishRequestNext();
            }else{
                preRequestNext();
                Server server = new Server(context);
                new Downloader().loadCoverage(server, "99", new FileUtil.FilePrograss() {
                    @Override
                    public void progress(int total, int progress) {
                        float finalProgress = (float) progress/total * 100;
                        if(finalProgress >= 100){
                            finish(total);
                        }else{
                            Log.d(TAG, String.format("downloadMMY -> total: %s, progress: %s", total,progress));
                            onDownloadProgressNext(CHECK_TYPE.MMY,(float) progress/total * 100);
                        }
                    }

                    @Override
                    public void finish(int total) {
                        if(!isFinish){
                            finishRequestNext();
                            Log.d(TAG, String.format("downloadMMY:total: %s, finish", total));
                            onDownloadProgressNext(CHECK_TYPE.MMY,100f);
                            isFinish = true;
                        }
                    }

                    @Override
                    public void start(int total) {
                        isFinish = false;
                        Log.d(TAG, String.format("downloadMMY:start: %s", total));
                    }

                    @Override
                    public void fail(String msg) {
                        Log.d(TAG, msg);
                        finishRequestNext();
                        onUpdateProcessFailedNext(CHECK_TYPE.MMY,"下载MMY失败:"+msg);

                    }
                });
            }
        }).start();
    }

    /**
     * 更新MMY
     */
    public void updateMMY(Context context){
        Log.d(TAG, "updateMMY");
        Rocket.writeOuterLog("更新车型资料-updateMMY");
        File sdPath = Environment.getExternalStorageDirectory();
        if(sdPath == null){
            return;
        }
        String updatePath = sdPath.getAbsolutePath()+ File.separator + updateFolder + File.separator + mmyFile;
        File updateFile = new File(updatePath);
        if(!updateFile.exists()){
            return;
        }
        //加载MMY
        preRequestNext();
        new Thread(() -> MMy.LoadMMy(context,updatePath, new MMy.MMyLoadCb() {

            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {
                finishRequestNext();
                onLoadMMYNext(CHECK_TYPE.MMY,true,0,0);
            }

            @Override
            public void onProgress(int total, int progress) {
                onLoadMMYNext(CHECK_TYPE.MMY,false,total,progress);
            }
        })).start();
    }

    /**
     * 检测更新传感器
     */
    private void checkSensorUpdate(Context context){
        Log.d(TAG, String.format("checkSensorUpdate: %s", "Sensor"));
        new Thread(() -> {
            if(checkSDCard){
                File sdPath = Environment.getExternalStorageDirectory();
                if(sdPath != null){
                    File sensorUpdateFile = new File(sdPath.getAbsolutePath()+ File.separator + sensorFile);
                    if(sensorUpdateFile.exists()){
                        onCheckUpdateNext(CHECK_TYPE.SENSOR,true);
                    }else{
                        onCheckUpdateNext(CHECK_TYPE.SENSOR,false);
                    }
                }
            }else{
                preRequestNext();
                Server server = new Server(context);
                CheckVersionSensorReq checkVersionSensorReq = new CheckVersionSensorReq();
                checkVersionSensorReq.setSerialNum("99");
                Server.Respond<CheckVersionSensorResp> respond_sensor = server.checkVersionSensor(checkVersionSensorReq);
                int status = respond_sensor.getStatus();
                finishRequestNext();
                if(status == 200){
                    CheckVersionSensorResp resp_sensor = respond_sensor.getData();
                    String fileUrl = resp_sensor.getFileUrl();
                    onCheckUpdateNext(CHECK_TYPE.SENSOR,resp_sensor.getNeedUpdate());
                }else{
                    onUpdateProcessFailedNext(CHECK_TYPE.SENSOR,"检测Sensor失败:"+status);
                }
            }
        }).start();
    }

    /**
     * 下载更新Sensor
     */
    public void downloadSensor(Context context){
        //模拟下载进度
        new Thread(() -> {
            if(checkSDCard){
                preRequestNext();
                File sdPath = Environment.getExternalStorageDirectory();
                if(sdPath != null){
                    String mmyUpdatePath = sdPath.getAbsolutePath()+ File.separator + sensorFile;
                    String targetMmyUpdatePath = sdPath.getAbsolutePath()+ File.separator+updateFolder+File.separator+sensorFile;
                    if(FileUtil.copyFile(mmyUpdatePath,targetMmyUpdatePath)){
                        onDownloadProgressNext(CHECK_TYPE.SENSOR,100f);
                    }else{
                        onUpdateProcessFailedNext(CHECK_TYPE.SENSOR,"移动Sensor失败");
                    }
                }
                finishRequestNext();
            }else{
                preRequestNext();
                Server server = new Server(context);
                new Downloader().loadSensor(server, "99" , new FileUtil.FilePrograss() {
                    @Override
                    public void progress(int total, int progress) {
                        float finalProgress = (float) progress/total * 100;
                        if(finalProgress >= 100){
                            finish(total);
                        }else{
                            Log.d(TAG, String.format("downloadSensor -> total: %s, progress: %s", total,progress));
                            onDownloadProgressNext(CHECK_TYPE.SENSOR,(float) progress/total * 100);
                        }
                    }

                    @Override
                    public void finish(int total) {
                        if(!isFinish){
                            finishRequestNext();
                            Log.d(TAG, String.format("downloadSensor:total: %s, finish", total));
                            onDownloadProgressNext(CHECK_TYPE.SENSOR,100f);
                            isFinish = true;
                        }
                    }

                    @Override
                    public void start(int total) {
                        isFinish = false;
                        Log.d(TAG, String.format("downloadSensor:start: %s", total));
                    }

                    @Override
                    public void fail(String msg) {
                        Log.d(TAG, msg);
                        finishRequestNext();
                        onUpdateProcessFailedNext(CHECK_TYPE.SENSOR,"下载Sensor失败:"+msg);
                    }
                });
            }
        }).start();
    }

    /* ***************************** WriteFlashProgress ***************************** */

    private OnWriteFlashProgressListener onWriteFlashProgressListener;

    // 接口类 -> OnWriteFlashProgressListener
    public interface OnWriteFlashProgressListener {
        void onWriteFlashProgress(CHECK_TYPE checkType,float progress);
    }

    // 对外暴露接口 -> setOnWriteFlashProgressListener
    public void setOnWriteFlashProgressListener(OnWriteFlashProgressListener onWriteFlashProgressListener) {
        this.onWriteFlashProgressListener = onWriteFlashProgressListener;
    }

    // 内部使用方法 -> WriteFlashProgressNext
    private void onWriteFlashProgressNext(CHECK_TYPE checkType,float progress) {
        if (onWriteFlashProgressListener != null) {
            runMainThread(() -> onWriteFlashProgressListener.onWriteFlashProgress(checkType,progress));
        }
    }

    /* ***************************** DownloadProgress ***************************** */

    private OnDownloadProgressListener onDownloadProgressListener;

    // 接口类 -> OnDownloadProgressListener
    public interface OnDownloadProgressListener {
        void onDownloadProgress(CHECK_TYPE checkType,float progress);
    }

    // 对外暴露接口 -> setOnDownloadProgressListener
    public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloadProgressListener) {
        this.onDownloadProgressListener = onDownloadProgressListener;
    }

    // 内部使用方法 -> DownloadProgressNext
    private void onDownloadProgressNext(CHECK_TYPE checkType,float progress) {
        if (onDownloadProgressListener != null) {
            runMainThread(() -> onDownloadProgressListener.onDownloadProgress(checkType,progress));
        }
    }

    /* ***************************** CheckUpdate ***************************** */

    private OnCheckUpdateListener onCheckUpdateListener;

    // 接口类 -> OnCheckUpdateListener
    public interface OnCheckUpdateListener {
        void onCheckUpdate(CHECK_TYPE chekType,boolean hasUpdate);
    }

    // 对外暴露接口 -> setOnCheckUpdateListener
    public void setOnCheckUpdateListener(OnCheckUpdateListener onCheckUpdateListener) {
        this.onCheckUpdateListener = onCheckUpdateListener;
    }

    // 内部使用方法 -> CheckUpdateNext
    private void onCheckUpdateNext(CHECK_TYPE chekType,boolean hasUpdate) {
        if (onCheckUpdateListener != null) {
            runMainThread(() -> onCheckUpdateListener.onCheckUpdate(chekType,hasUpdate));
        }
    }

    /* ***************************** CheckUpdateFinish ***************************** */

    private OnCheckUpdateFinishListener onCheckUpdateFinishListener;

    // 接口类 -> OnCheckUpdateListener
    public interface OnCheckUpdateFinishListener {
        void onCheckUpdateFinish();
    }

    // 对外暴露接口 -> setOnCheckUpdateListener
    public void setOnCheckUpdateFinishListener(OnCheckUpdateFinishListener onCheckUpdateFinishListener) {
        this.onCheckUpdateFinishListener = onCheckUpdateFinishListener;
    }

    // 内部使用方法 -> CheckUpdateNext
    private void onCheckUpdateFinishNext() {
        if (onCheckUpdateFinishListener != null) {
            runMainThread(() -> onCheckUpdateFinishListener.onCheckUpdateFinish());
        }
    }

    /* ***************************** UpdateProcessFailed ***************************** */

    private OnUpdateProcessFailedListener onUpdateProcessFailedListener;

    // 接口类 -> OnUpdateProcessFailedListener
    public interface OnUpdateProcessFailedListener {
        void onUpdateProcessFailed(CHECK_TYPE checkType,String msg);
    }

    // 对外暴露接口 -> setOnUpdateProcessFailedListener
    public void setOnUpdateProcessFailedListener(OnUpdateProcessFailedListener onUpdateProcessFailedListener) {
        this.onUpdateProcessFailedListener = onUpdateProcessFailedListener;
    }

    // 内部使用方法 -> UpdateProcessFailedNext
    private void onUpdateProcessFailedNext(CHECK_TYPE checkType,String msg ) {
        if (onUpdateProcessFailedListener != null) {
            runMainThread(() -> onUpdateProcessFailedListener.onUpdateProcessFailed(checkType,msg));
        }
    }

    /* ***************************** LoadMMY ***************************** */

    private OnLoadMMYListener onLoadMMYListener;

    // 接口类 -> OnLoadMMYListener
    public interface OnLoadMMYListener {
        void onLoadMMY(CHECK_TYPE checkType,boolean stop,int total, int progress);
    }

    // 对外暴露接口 -> setOnLoadMMYListener
    public void setOnLoadMMYListener(OnLoadMMYListener onLoadMMYListener) {
        this.onLoadMMYListener = onLoadMMYListener;
    }

    // 内部使用方法 -> LoadMMY
    private void onLoadMMYNext(CHECK_TYPE checkType,boolean stop,int total, int progress) {
        if (onLoadMMYListener != null) {
            runMainThread(() -> onLoadMMYListener.onLoadMMY(checkType,stop,total,progress));
        }
    }
}
