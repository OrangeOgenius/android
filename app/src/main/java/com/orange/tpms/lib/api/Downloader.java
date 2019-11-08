package com.orange.tpms.lib.api;

import android.util.Log;

import com.orange.tpms.bean.CheckVersionCoverageReq;
import com.orange.tpms.bean.CheckVersionCoverageResp;
import com.orange.tpms.bean.CheckVersionFlashReq;
import com.orange.tpms.bean.CheckVersionFlashResp;
import com.orange.tpms.bean.CheckVersionSensorReq;
import com.orange.tpms.bean.CheckVersionSensorResp;
import com.orange.tpms.utils.FileUtil;
import com.orange.tpms.utils.HttpDownloader;

public class Downloader {
    private final String TAG = Downloader.class.getName();

    public void loadFlash (Server server, String serialNum, FileUtil.FilePrograss filePrograss) {
        CheckVersionFlashReq checkVersionFlashReq = new CheckVersionFlashReq();
        checkVersionFlashReq.setSerialNum(serialNum);
        Server.Respond<CheckVersionFlashResp> respond = server.checkVersionFlash(checkVersionFlashReq);
        int status = respond.getStatus();
        CheckVersionFlashResp resp = respond.getData();
        String fileUrl = resp.getFileUrl();
        Log.d(TAG, "onRsp Sensor: "+status+","+fileUrl);

        if (fileUrl == null) {
            Log.e(TAG, "fileUrl is null");
            filePrograss.fail("FileUrl No found.");
            return ;
        }
        // 获取文件名
        String[] splitArr = fileUrl.split("/");
        String fileName = splitArr[splitArr.length-1];

        Log.d(TAG, "filename:"+fileName);
        Log.d(TAG, "fileUrl:"+fileUrl);

        String path="flash/";
        HttpDownloader httpDownloader = new HttpDownloader();
        httpDownloader.download (fileUrl, path, fileName, filePrograss);
    }

    public void loadSensor (Server server, String serialNum, FileUtil.FilePrograss filePrograss) {
        CheckVersionSensorReq checkVersionSensorReq = new CheckVersionSensorReq();
        checkVersionSensorReq.setSerialNum(serialNum);
        Server.Respond<CheckVersionSensorResp> respond = server.checkVersionSensor(checkVersionSensorReq);
        int status = respond.getStatus();
        CheckVersionSensorResp resp = respond.getData();
        String fileUrl = resp.getFileUrl();
        Log.d(TAG, "onRsp Sensor: "+status+","+fileUrl);

        if (fileUrl == null) {
            Log.e(TAG, "fileUrl is null");
            filePrograss.fail("FileUrl No found.");
            return ;
        }
        // 获取文件名
        String[] splitArr = fileUrl.split("/");
        String fileName = splitArr[splitArr.length-1];

        Log.d(TAG, "filename:"+fileName);
        Log.d(TAG, "fileUrl:"+fileUrl);

        String path="sensor/";
        HttpDownloader httpDownloader = new HttpDownloader();
        httpDownloader.download (fileUrl, path, fileName, filePrograss);
    }

    public void loadCoverage (Server server, String serialNum, FileUtil.FilePrograss filePrograss) {
        CheckVersionCoverageReq checkVersionCoverageReq = new CheckVersionCoverageReq();
        checkVersionCoverageReq.setSerialNum(serialNum);
        Server.Respond<CheckVersionCoverageResp> respond = server.checkVersionCoverage(checkVersionCoverageReq);
        int status = respond.getStatus();
        CheckVersionCoverageResp resp = respond.getData();
        String fileUrl = resp.getFileUrl();
        Log.d(TAG, "onRsp coverage: "+status+","+fileUrl);

        if (fileUrl == null) {
            filePrograss.fail("FileUrl No found.");
            return ;
        }
        // 获取文件名
        String[] splitArr = fileUrl.split("/");
        String fileName = splitArr[splitArr.length-1];

        Log.d(TAG, "filename:"+fileName);
        Log.d(TAG, "fileUrl:"+fileUrl);

        String path="coverage/";
        HttpDownloader httpDownloader = new HttpDownloader();
        httpDownloader.download (fileUrl, path, fileName, filePrograss);
    }
}
