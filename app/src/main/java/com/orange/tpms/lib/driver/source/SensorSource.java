package com.orange.tpms.lib.driver.source;

import android.util.Log;

import com.orange.tpms.lib.driver.channel.ExampleChannel;
import com.orange.tpms.lib.driver.channel.IChannel;
import com.orange.tpms.lib.driver.channel.UARTChannel;
import com.orange.tpms.lib.driver.protocol.SensorProtocol;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.utils.StringUtils;

import java.util.Timer;
import java.util.TimerTask;

import bean.hardware.SensorDataBean;
import bean.hardware.SensorVersionBean;
import bean.hardware.SensorWriteInfoBean;
import com.orange.tpms.utils.Command;

import static com.orange.tpms.lib.utils.StringUtils.byteArrayToHexStr;
import static com.orange.tpms.utils.Command.StringHexToByte;
import static com.orange.tpms.utils.Command.bytesToHex;

/**
 * 传感器抽象类
 */
public class SensorSource implements ISource {

    public static String TAG = SensorSource.class.getName();

    IChannel iChannel;
    HardwareApp hardwareApp;

    public SensorSource (HardwareApp hardwareApp) {
        this.hardwareApp = hardwareApp;
        this.iChannel = new UARTChannel(hardwareApp);
    }

    /**
     * 设置通道
     * @param iChannel
     */
    @Override
    public void setIChannel (IChannel iChannel) {
        this.iChannel = iChannel;
    }

    public void wait (int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * hex String to byte array
     */
    public static byte[] hexStringToBytes(String hexString) {
        hexString = hexString.toLowerCase();
        String[] hexStrings = hexString.split(" ");
        byte[] bytes = new byte[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            char[] hexChars = hexStrings[i].toCharArray();
            bytes[i] = (byte) (charToByte(hexChars[0]) << 4 | charToByte(hexChars[1]));
        }
        return bytes;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
    }

    public interface SensorRespondCB {
        public void receive (int ret);
    }

    public interface SensorRespondProgramCheck {
        public void receive (int ret, byte[] data);
    }

    public interface SensorWriteInfo {
        public void receive (int ret, SensorWriteInfoBean sensorWriteInfoBean);
    }

    private int getRespond (int cmdcode, byte code, int filter_cmdcode, SensorRespondCB sensorRespondCB) {
        SensorProtocol sensorProtocol = new SensorProtocol(cmdcode,
                new byte[] {code});
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                // 获取到消息
                byte[] out = hexStringToBytes (content);
                int ret = -2;
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            ret = -3;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode == filter_cmdcode &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            ret = data[0];
                            break;
                        }
                        if (cmdCode == SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            ret = SensorProtocol.CMDCODE.TIMEOUT_OR_ERR;
                            break;
                        }
                        ret = -2;
                    } break;
                }
                sensorRespondCB.receive(ret);
            }
        });
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        Log.d(TAG, "inBytes: "+StringUtils.byteArrayToHexStr(inBytes));
        Log.d(TAG, "len: "+len);

        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        wait (10);      // 延迟等待10ms

        // byte[] out = iChannel.read();
        return 0;
    }

    /**
     * 开机后PDA发送此命令, 确认与模块正常联机
     * @return 1:模块在Boot loader流程
     *         2:模块在主程序流程
     *         <0:发生错误
     */
    public int handShake (SensorRespondCB sensorRespondCB) {
        Log.d(TAG, "action: handShake");
        return getRespond(SensorProtocol.CMDCODE.HANDSHAKE, (byte)0x00,
                SensorProtocol.CMDCODE.HANDSHAKE, sensorRespondCB);
    }

    /**
     * 擦除Flash
     * @return 0:Success
     *         1:StartAddress isn't align with page size
     *         2:NVM/Flash status fail
     */
    public int eraseFlash (SensorRespondCB sensorRespondCB) {
        Log.d(TAG, "action: eraseFlash");
        return getRespond(SensorProtocol.CMDCODE.ERASE_FLASH, (byte)0x00,
                SensorProtocol.CMDCODE.ERASE_FLASH, sensorRespondCB);
    }

    public int writeSensorFirmwareSetting (SensorWriteInfo sensorWriteInfo, byte[] data) {
        Log.d(TAG, "action: writeSensorFirmwareSetting");
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.READ_SETTING,
                data);
        byte[] inBytes = sensorProtocol.getBytes();

        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                // 获取到消息
                byte[] out = hexStringToBytes (content);
                int ret = -2;
                int num = 0;
                int byteNum = 0;
                SensorWriteInfoBean sensorWriteInfoBean = new SensorWriteInfoBean();

                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            ret = -3;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode == SensorProtocol.CMDCODE.READ_WRITE_SENSOR &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            ret = 0;
                            num = data[0];
                            byteNum = ((data[1]==0x04)?2048:6144);
                            sensorWriteInfoBean.setNum(num);
                            sensorWriteInfoBean.setByteNum(byteNum);
                            break;
                        }
                        if (cmdCode == SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            ret = SensorProtocol.CMDCODE.TIMEOUT_OR_ERR;
                            break;
                        }
                        ret = -2;
                    } break;
                }
                sensorWriteInfo.receive(ret, sensorWriteInfoBean);
            }
        });

        int len = iChannel.write(inBytes);

        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        wait (10);      // 延迟等待10ms

        // byte[] out = iChannel.read();
        return 0;
    }

    /**
     * 复制id
     * @return
     */
    public int copySensorId (byte[] inputData, SensorRespondCB sensorRespondCB) {
        Log.d(TAG, "action: copySensorId");
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.COPY_SENSOR_ID,
                inputData);
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                byte[] out = hexStringToBytes (content);
                int ret = -2;
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            ret = -3;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode==SensorProtocol.CMDCODE.COPY_SENSOR_ID &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            // byte[] data = outProtocol.getData();
                            ret = 0;
                            break;
                        }
                        if (cmdCode==SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            ret = SensorProtocol.CMDCODE.TIMEOUT_OR_ERR;
                            break;
                        }
                        ret = -2;
                    } break;
                }
                sensorRespondCB.receive(ret);
            }
        });
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        wait (10);      // 延迟等待10ms
        return 0;
    }

    /**
     * 写入Flash
     * @param inputData 最大长度132
     * @return 0:pass
     *         2:program flash fail
     *         3:after flash programmed and verify fail
     */
    public int writeFlash (byte[] inputData, SensorRespondCB sensorRespondCB) {
        Log.d(TAG, "action: writeFlash");
        if (inputData.length > 132) {
            // 返回错误
            return -3;
        }
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.WRITE_FLASH,
                inputData);
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                byte[] out = hexStringToBytes (content);
                int ret = -2;
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            ret = -3;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode==SensorProtocol.CMDCODE.WRITE_FLASH &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            ret = data[0];
                            break;
                        }
                        if (cmdCode==SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            ret = SensorProtocol.CMDCODE.TIMEOUT_OR_ERR;
                            break;
                        }
                        if (cmdCode==0x0b &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 烧录完成
                            // byte[] data = outProtocol.getData();
                            ret = 0x0b;
                            break;
                        }
                        ret = -2;
                    } break;
                }
                if (ret != -2) {
                    sensorRespondCB.receive(ret);
                }
            }
        });
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        // wait (10);      // 延迟等待10ms

        // byte[] out = iChannel.read();
        return 0;
    }

    public interface SensorVersionCb {
        public void receive (SensorVersionBean sensorVersionBean);
    }

    public interface SensorDataBeansCb {
        public void receive (SensorDataBean sensorVersionBean);
    }

    /**
     * 取得软件版本
     * @return
     * d[1]: 年,18表2018年
     * d[2]: 月,01表1月
     * d[3]: 日,02表2月
     * d[4]: 版本号
     * d[5]-d[12]: reserved
     */
    public SensorVersionBean getVersion (SensorVersionCb sensorVersionCb) {
        Log.d(TAG, "action: getVersion");
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.READ_VERSION,
                new byte[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1});
        byte[] inBytes = sensorProtocol.getBytes();
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                SensorVersionBean sensorVersionBean = null;
                byte[] out = hexStringToBytes (content);
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            sensorVersionBean = null;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode==SensorProtocol.CMDCODE.READ_VERSION &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            SensorVersionBean versionBean = new SensorVersionBean();
                            int year = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[1]}));
                            int month = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[2]}));
                            int day = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[3]}));
                            int version = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[4]}));

                            versionBean.setYear(year);           // year
                            versionBean.setMonth(month);          // month
                            versionBean.setDay(day);            // day
                            versionBean.setVersion(version);        // version
                            sensorVersionBean = versionBean;
                            break;
                        }
                        if (cmdCode==SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            sensorVersionBean = null;
                            break;
                        }
                        sensorVersionBean = null;
                    } break;
                }
                sensorVersionCb.receive(sensorVersionBean);
            }
        });
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return null;
        }
        Log.d(TAG, "inBytes: "+StringUtils.byteArrayToHexStr(inBytes));
        Log.d(TAG, "len: "+len);
        wait (10);      // 延迟等待10ms

        // byte[] out = iChannel.read();

        return null;
    }

    /**
     * 获取硬件版本
     * @param sensorVersionCb
     * @return
     */
    public SensorVersionBean getHardwareVersion (SensorVersionCb sensorVersionCb) {
        Log.d(TAG, "action: getHardwareVersion");
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.READ_HARDWARE_VERSION,
                new byte[] {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1});
        byte[] inBytes = sensorProtocol.getBytes();
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                SensorVersionBean sensorVersionBean = null;
                byte[] out = hexStringToBytes (content);
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            sensorVersionBean = null;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode==SensorProtocol.CMDCODE.READ_HARDWARE_VERSION &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            SensorVersionBean versionBean = new SensorVersionBean();
                            int year = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[1]}));
                            int month = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[2]}));
                            int day = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[3]}));
                            int version = Integer.parseInt(StringUtils.byteArrayToHexStr(new byte[]{data[4]}));

                            versionBean.setYear(year);           // year
                            versionBean.setMonth(month);          // month
                            versionBean.setDay(day);            // day
                            versionBean.setVersion(version);        // version
                            sensorVersionBean = versionBean;
                            break;
                        }
                        if (cmdCode==SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            sensorVersionBean = null;
                            break;
                        }
                        sensorVersionBean = null;
                    } break;
                }
                sensorVersionCb.receive(sensorVersionBean);
            }
        });
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return null;
        }
        Log.d(TAG, "inBytes: "+StringUtils.byteArrayToHexStr(inBytes));
        Log.d(TAG, "len: "+len);
        wait (10);      // 延迟等待10ms

        // byte[] out = iChannel.read();

        return null;
    }

    /**
     * 重启
     * @return
     */
    public int reboot (SensorRespondCB sensorRespondCB) {
        Log.d(TAG, "action: reboot");
        return getRespond(SensorProtocol.CMDCODE.REBOOT, (byte)0x00,
                SensorProtocol.CMDCODE.ERASE_FLASH, sensorRespondCB);
    }

    public int programCheck (SensorRespondProgramCheck sensorRespondCB) {
        Log.d(TAG, "action: programCheck");
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.PROGRAM_CHECK,
                new byte[] {
                        0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00
                });
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                // 获取到消息
                byte[] out = hexStringToBytes (content);
                int ret = -2;
                byte[] data = null;

                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            ret = -3;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode == SensorProtocol.CMDCODE.WRITE_SENSOR &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            data = outProtocol.getData();
                            ret = data[0];
                            break;
                        }
                        if (cmdCode == SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            ret = SensorProtocol.CMDCODE.TIMEOUT_OR_ERR;
                            break;
                        }
                        ret = -2;
                    } break;
                }
                sensorRespondCB.receive(ret, data);
            }
        });
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        Log.d(TAG, "inBytes: "+StringUtils.byteArrayToHexStr(inBytes));
        Log.d(TAG, "len: "+len);

        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        wait (10);      // 延迟等待10ms

        // byte[] out = iChannel.read();
        return 0;
    }

    private String getSensorIdFromBytes (byte[] data) {
        int id_len = data[4]&0xFF;
        byte[] tempIdByte = null;

        if (id_len==6) {
            // 6码
            tempIdByte = new byte[3];
            tempIdByte[0] = data[1];
            tempIdByte[1] = data[2];
            tempIdByte[2] = data[3];
        } else {
            // 7,8码id
            tempIdByte = new byte[4];
            tempIdByte[0] = data[0];
            tempIdByte[1] = data[1];
            tempIdByte[2] = data[2];
            tempIdByte[3] = data[3];
        }
        return byteArrayToHexStr(tempIdByte);
    }

    /**
     * 读取多个传感器信息
     * @param num
     * @param hex
     * @return
     */
    protected long lastReadTime = 0;
    public static final long TIMEOUT = 5000;

    public SensorDataBean[] readSensor (int num, String hex, SensorSource.SensorDataBeansCb sensorDataBeansCb,String lf) {
        Log.d(TAG, "action: readSensor");
        byte[] hexByte = StringUtils.hexStrToByteArray(hex, 2);     // 转成2bytes

        byte data2 = 0x00;
        Log.d(TAG, "readSensor num:"+num);
        if (num > 1) {
            data2 = 0x02;
        }
        Log.d(TAG, "readSensor data2:"+data2);
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.READ_SETTING,
                new byte[] {0x01, data2, StringHexToByte(lf)[0], hexByte[1], 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return null;
        }
        wait (10);      // 延迟等待10ms
        lastReadTime = System.currentTimeMillis();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 定时
                if ((System.currentTimeMillis()-lastReadTime)>(TIMEOUT-100)) {
                    SensorDataBean dataBean = new SensorDataBean();
                    dataBean.setCmdCode(-3);     // 失败
                    sensorDataBeansCb.receive(dataBean);
                }
            }
        }, 5000);
        /* SensorDataBean[] dataBeans = new SensorDataBean[num];
        for (int i=0;i<num;i++) { */
        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                SensorDataBean dataBean = null;
                byte[] out = hexStringToBytes (content);
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            dataBean = null;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode == SensorProtocol.CMDCODE.GET_SENSOR_INFO &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            dataBean = new SensorDataBean();
                            dataBean.setId_len(data[4]);
                            // 设置sensor_id
                            String sensor_id = getSensorIdFromBytes(data);
                            dataBean.setSensor_id(sensor_id);
                            Log.e("count:",bytesToHex(data));
                            dataBean.setB_temp(data[5]);
                            dataBean.setH_temp(data[6]);
                            Log.d(TAG, "readSensor: "+data[7]+","+data[8]);
                            dataBean.setKpa(data[7]*256+data[8]);
                            dataBean.setV(data[9]);
                            byte data_10 = data[10];
                            int ifHasTemp = data_10&0x40;       // bit7
                            int ifHasV = data_10&0x20;          // bit6
                            int batteryStatus = data_10&0x10;   // bit5
                            int batteryLevel = data_10&0x08;        // bit4
                            dataBean.setHasBatteryV(ifHasV);
                            dataBean.setHasTemp(ifHasTemp);
                            dataBean.setBatteryStatus(batteryStatus);
                            dataBean.setBatteryLevel(batteryLevel);
                            dataBean.setCmdCode(1);         // 成功获取
                        }
                        if (cmdCode==SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            dataBean = new SensorDataBean();
                            dataBean.setCmdCode(-3);     // 失败
                        }
                    } break;
                }
                sensorDataBeansCb.receive(dataBean);
                lastReadTime = System.currentTimeMillis();
            }
        });
        return null;
    }

    /**
     * 每隔1s读取一次
     * @param num
     * @param hex
     * @return
     */
    public int writeSensor (int num, int hex, WriteSensorProgress writeSensorProgress) {

        // 进入刻录烧写流程
        writeSensorProgress.begin();

        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.READ_SETTING,
                new byte[] {0x02, (byte)num, 0x00, (byte)hex});
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        wait (10);      // 延迟等待10ms

        int MAX_DELAY_COUNT = 20;       // 刻录最大等待超时次数

        byte[] out = iChannel.read();
        SensorProtocol outProtocol = SensorProtocol.toObject(out);
        if (outProtocol == null) {
            return -3;
        }
        int cmdCode = outProtocol.getCmdCode();
        if (cmdCode==SensorProtocol.CMDCODE.SEND_SENSOR_ID &&
                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
            // 开始准备刻录
            byte[] data = outProtocol.getData();
            String sensor_id = byteArrayToHexStr(data);
            int sensor_num = data[5];

            // 回调说明已经收到刻录准备了
            writeSensorProgress.prepare(sensor_id, sensor_num);
        }

        int delay_count = 0;        // 超时计数
        while (delay_count<MAX_DELAY_COUNT) {
            out = iChannel.read();
            outProtocol = SensorProtocol.toObject(out);
            cmdCode = outProtocol.getCmdCode();
            if (cmdCode==SensorProtocol.CMDCODE.GET_WRITE_SENSOR_RESULT &&
                    outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                // 开始准备刻录
                byte[] data = outProtocol.getData();
                String sensor_id = byteArrayToHexStr(data);
                int ret = data[5];      // 1-刻录成功, 2-刻录失败

                // 返回刻录成功或者刻录失败
                writeSensorProgress.finish(ret, sensor_id);
                break;
            }
            delay_count++;
        }

        // 退出刻录烧写流程
        writeSensorProgress.exit();
        return 0;
    }

    /**
     * 替换传感器id (replacedSensorId被替换成factorySensorId)
     * @param replacedSensorId
     * @param factorySensorId
     * @return
     */
    public String copySensor (String replacedSensorId, String factorySensorId) {
        int replaced_len = replacedSensorId.length();
        int factory_len = factorySensorId.length();
        byte[] replacedSensorBytes = StringUtils.hexStrToByteArray(replacedSensorId);
        byte[] factorySensorBytes = StringUtils.hexStrToByteArray(factorySensorId);

        // 组合获取输入流
        byte[] input_stream = new byte[10];
        int m_index = 0;
        for (int i=0;i<replacedSensorBytes.length;i++) {
            input_stream[m_index++] = replacedSensorBytes[i];
        }
        // 其余位数补充为0x00
        for (int i=m_index;i<=3;i++) {
            input_stream[m_index] = 0x00;
        }
        input_stream[m_index++] = (byte) (replaced_len&0xFF);

        for (int i=0;i<factorySensorBytes.length;i++) {
            input_stream[m_index++] = factorySensorBytes[i];
        }
        // 其余位数补充为0x00
        for (int i=m_index;i<=8;i++) {
            input_stream[m_index] = 0x00;
        }
        input_stream[m_index++] = (byte) (factory_len&0xFF);

        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.COPY_SENSOR_ID,
                input_stream);
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return null;
        }
        wait (10);      // 延迟等待10ms
        byte[] out = iChannel.read();
        SensorProtocol outProtocol = SensorProtocol.toObject(out);
        if (outProtocol == null) {
            return null;
        }
        int cmdCode = outProtocol.getCmdCode();
        if (cmdCode==SensorProtocol.CMDCODE.COPY_SENSOR_ID &&
                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
            byte[] data = outProtocol.getData();
            return byteArrayToHexStr(data);
        }
        return null;
    }

    /**
     * 功率调整
     * @param level, 1~10
     * @return 0: success
     *         1: fail
     */
    public int lfAdjust (int level, SensorRespondCB sensorRespondCB) {
        return getRespond(SensorProtocol.CMDCODE.LF_ADJUST, (byte)level,
                SensorProtocol.CMDCODE.LF_ADJUST, sensorRespondCB);
    }

    /**
     * 烧录传感器
     * @return
     */
    public int writeSensorFireware (SensorRespondCB sensorRespondCB, byte[] inputData, int index) {
        Log.d(TAG, "action: writeSensorFireware");

        // 写入指定byte
        byte[] newData = new byte[inputData.length+1];
        for (int i=0;i<inputData.length;i++) {
            newData[i] = inputData[i];
        }
        newData[newData.length-1] = (byte)(index);        // 第几个200byte

        hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanReceive() {

            }

            @Override
            public void scanMsgReceive(String content) {

            }

            @Override
            public void uart2MsgReceive(String content) {
                byte[] out = hexStringToBytes (content);
                int ret = -2;
                switch (1) {
                    case 1: {
                        SensorProtocol outProtocol = SensorProtocol.toObject(out);
                        if (outProtocol == null) {
                            ret = -3;
                            break;
                        }
                        int cmdCode = outProtocol.getCmdCode();
                        if (cmdCode==SensorProtocol.CMDCODE.WRITE_SENSOR &&
                                outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                            // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                            byte[] data = outProtocol.getData();
                            ret = data[0];
                            break;
                        }
                        if (cmdCode==SensorProtocol.CMDCODE.TIMEOUT_OR_ERR) {
                            ret = -1;
                            break;
                        }
                        ret = -2;
                    } break;
                }
                sensorRespondCB.receive(ret);
            }
        });
        SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.WRITE_SENSOR,
                newData);
        byte[] inBytes = sensorProtocol.getBytes();
        int len = iChannel.write(inBytes);
        if (len != inBytes.length) {
            // 发错有错误
            return -1;
        }
        wait (10);      // 延迟等待10ms

        return 0;
    }

    /**
     * 将固件写入烧录缓存
     * @return
     */
    private int pdaUpdateRam (byte[] input) {
        int MAX_NUM = 200;
        int total_page = (int) Math.ceil(input.length/MAX_NUM);

        for (int i=1;i<=total_page;i++) {
            // 获取需要烧录的节点块
            int pre_write_num = 0;
            if (i<total_page) {
                pre_write_num = MAX_NUM;
            } else {
                pre_write_num = input.length-(i-1)*MAX_NUM;
            }
            int new_num = pre_write_num+1;
            byte[] tempByte = new byte[new_num];
            for (int j=0;j<pre_write_num;j++) {
                tempByte[j] = input[(i-1)*MAX_NUM+j];
            }
            tempByte[pre_write_num] = (byte)(pre_write_num&0xFF);

            SensorProtocol sensorProtocol = new SensorProtocol(SensorProtocol.CMDCODE.SENSOR_SOFTWARE_UPDATE,
                    tempByte);
            byte[] inBytes = sensorProtocol.getBytes();
            int len = iChannel.write(inBytes);
            if (len != inBytes.length) {
                // 发错有错误
                return -2;
            }
            wait(10);      // 延迟等待10ms
            byte[] out = iChannel.read();
            SensorProtocol outProtocol = SensorProtocol.toObject(out);
            if (outProtocol == null) {
                return -3;
            }
            int cmdCode = outProtocol.getCmdCode();
            if (cmdCode == SensorProtocol.CMDCODE.SENSOR_SOFTWARE_UPDATE &&
                    outProtocol.getHeadCode() == SensorProtocol.ENDCODE) {
                // 响应体标头码为请求体的结束码, 成功回应, 返回结果为data[1]
                byte[] data = outProtocol.getData();
                int if_success = data[0];
                if (if_success == 0) {
                    continue;
                } else {
                    return -1;
                }
            }
        }
        return -3;
    }

    public interface WriteSensorProgress {
        public void begin ();
        public void prepare (String sensor_id, int prepare_num);
        public void finish (int status, String sensor_id);
        public void exit();
    }

    /**
     * 接收到消息
     * @param <T>
     */
    public interface IMsgRespond<T> {
        public void receive (int ret, T t);
    }
}
