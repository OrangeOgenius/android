package com.orange.tpms.lib.test;

import android.util.Log;

import com.orange.tpms.lib.api.SensorHandler;
import com.orange.tpms.lib.driver.source.SensorSource;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.utils.StringUtils;

import bean.hardware.SensorDataBean;
import bean.hardware.SensorVersionBean;
import bean.hardware.SensorWriteInfoBean;

/**
 * Created by john on 2019/6/22.
 */

public class HardTest {

    public static String TAG = HardTest.class.getName();

    /**
     * 测试硬件
     */
    public static void Test (HardwareApp hardwareApp) {
        hardwareApp.switchScan(false);       // 是否开启SCAN键扫描功能

        // 传感器操作
        SensorHandler sensorHandler = new SensorHandler(hardwareApp);
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hardwareApp.setGpio1V(true);     // 打开GPIO电压

            // 获取软件版本
            /* sensorHandler.getSoftwareVersion(new SensorSource.SensorVersionCb() {

                @Override
                public void receive(SensorVersionBean sensorVersionBean) {
                    Log.d(TAG, "software version: ");
                    if (sensorVersionBean != null) {
                        int version = sensorVersionBean.getVersion();
                        Log.d(TAG, "version:"+version);
                        Log.d(TAG, "year:"+sensorVersionBean.getYear());
                        Log.d(TAG, "day:"+sensorVersionBean.getDay());
                        Log.d(TAG, "month:"+sensorVersionBean.getMonth());
                    }
                }
            });*/

            // 获取硬件版本
            /* sensorHandler.getHardwareVersion(new SensorSource.SensorVersionCb() {

                @Override
                public void receive(SensorVersionBean sensorVersionBean) {
                    Log.d(TAG, "hardware version: ");
                    if (sensorVersionBean != null) {
                        int version = sensorVersionBean.getVersion();
                        Log.d(TAG, "version:"+version);
                        Log.d(TAG, "year:"+sensorVersionBean.getYear());
                        Log.d(TAG, "day:"+sensorVersionBean.getDay());
                        Log.d(TAG, "month:"+sensorVersionBean.getMonth());
                    }
                }
            }); */

            // 读取传感器数据
            /*sensorHandler.readSensor(2, "70", new SensorSource.SensorDataBeansCb() {
                @Override
                public void receive(SensorDataBean sensorVersionBean) {
                    if (sensorVersionBean != null) {
                        Log.d(TAG, "sensorData: " + sensorVersionBean.getSensorDataStr());
                    }
                }
            });*/
            // 擦除Flash
            /* sensorHandler.eraseFlash(new SensorSource.SensorRespondCB() {
                @Override
                public void receive(int ret) {
                    Log.d(TAG, "eraseFlash ret:"+ret);
                }
            });*/
            // readAllHex (sensorHandler, 0);

            // 烧录Flash
            /*byte[] buffer = sensorHandler.getAssetsFileStream(hardwareApp.app, "2C.s19");
            Log.d(TAG, "buffer len:"+buffer.length);

            String tmp_msg = "";
            for (int i=0;i<buffer.length;i++) {
                // 每10个字节打印一次
                tmp_msg+=buffer[i];
                if (i % 10 == 9) {
                    Log.d(TAG, tmp_msg);
                    tmp_msg = "";
                }
            }
            if (tmp_msg != "") {
                Log.d(TAG, tmp_msg);
            }*/

            // 烧录传感器
            /*String content = sensorHandler.getSensorByteFromStrStream(hardwareApp.app, "2C_V0.3.s19");
            byte[] buffer = StringUtils.hexStrToByteArray(content);
            Log.d(TAG, "buffer len: "+buffer.length);*/
            // 烧录传感器
            /* sensorHandler.writeSensorFirmwareSetting(buffer, (ret, sensorWriteInfoBean) -> {
                // 打印
                if (ret == 0) {
                    Log.d(TAG, String.format("writeSensorFirmareSetting => byteNum%s, num:%s", 2048,
                            1));
                    sensorHandler.writeSensorFirmware(buffer, 0, sensorWriteInfoBean.getByteNum());
               }
            });*/
            // sensorHandler.writeSensorFirmware(buffer, 0, 200);

            // sensorHandler.ping(ret -> {Log.d(TAG, "reboot ret:"+ret);});
            /* sensorHandler.reboot(ret -> {
                Log.d(TAG, "reboot ret:"+ret);
                if (ret == 0) {
                    // 成功重启

                } else {
                    // 重启失败
                }
            });*/

            // 烧录Flash
            /* sensorHandler.writeFlashWithReboot (new SensorHandler.FlashWriteProgress() {

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

            // 重启
            //

            // 获取硬件信息
            /* SensorHandler.HardwareInfo info = sensorHandler.getHardwareInfo();
            Log.d(TAG, "serialNum:"+info.serianNum);
            Log.d(TAG, "model:"+info.model);
            Log.d(TAG, "hardwareVersion:"+info.hardwareVersion);*/

            // COPY ID
            //sensorHandler.copySensorId();

        }).start();

        /* hardwareApp.addDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {
                Log.d(TAG, "scanMsgReceive: "+content);
                sensorHandler.ping(new SensorSource.SensorRespondCB() {

                    @Override
                    public void receive(int ret) {
                        Log.d(TAG, "ping ret: "+ret);
                    }
                });
                SensorDataBean[] sensorDataBeans = sensorHandler.readSensor(2, "2b");

            }

            @Override
            public void uart2MsgReceive(String content) {
                Log.d(TAG, "uart2MsgReceive: "+content);
            }
        });*/

    }

    /**
     * hex String to byte array
     */
    /*public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toLowerCase();
        String[] hexStrings = hexString.split(" ");
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            char[] hexChars = hexStrings[i].toCharArray();
            bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
        }
        return bytes;
    }*/

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }
}
