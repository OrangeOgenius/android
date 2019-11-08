package com.orange.tpms.lib.driver.channel;

import android.util.Log;

import com.orange.tpms.lib.api.SensorHandler;
import com.orange.tpms.lib.driver.protocol.SensorProtocol;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.utils.StringUtils;

import java.util.ArrayList;

/**
 * 根据不同的命令码模拟一些硬件响应到前端
 */
public class ExampleChannel implements IChannel {

    public static String TAG = ExampleChannel.class.getName();

    byte[] in;
    int index = 0;

    @Override
    public int open() {
        return 0;
    }

    @Override
    public int close() {
        return 0;
    }

    @Override
    public int write(byte[] in) {

        this.in = in;
        int cmdCode = in[1];
        switch (cmdCode) {
            case 0x10: {
                // 包括读取传感器, 刻录传感器的接口
                String str = StringUtils.byteArrayToHexStr(in);
                index = in[5];
                Log.d(TAG, "DATA: "+str+",len:"+index);
                return in.length;
            }
            default : {
                String str = StringUtils.byteArrayToHexStr(in);
                Log.d(TAG, "DATA: "+str);
                return in.length;
            }
        }
    }

    @Override
    public byte[] read() {

        int cmdCode = in[1];
        switch (cmdCode) {
            case 0x00: {
                // HANDSHAKE
                SensorProtocol sensorProtocol = new SensorProtocol();
                sensorProtocol.setData(new byte[]{0x02});
                sensorProtocol.setCmdCode(0x00);
                sensorProtocol.setHeadCode(SensorProtocol.ENDCODE);
                sensorProtocol.setEndCode(SensorProtocol.HEADCODE);
                byte[] bytes = sensorProtocol.getBytes();
                String str = StringUtils.byteArrayToHexStr(bytes);
                Log.d(TAG, "read: "+str);
                return bytes;
            }
            case 0x01: {
                // ERASE_FLASH
                SensorProtocol sensorProtocol = new SensorProtocol();
                sensorProtocol.setData(new byte[]{0x00});
                sensorProtocol.setCmdCode(0x01);
                sensorProtocol.setHeadCode(SensorProtocol.ENDCODE);
                sensorProtocol.setEndCode(SensorProtocol.HEADCODE);
                byte[] bytes = sensorProtocol.getBytes();
                String str = StringUtils.byteArrayToHexStr(bytes);
                Log.d(TAG, "read: "+str);
                return bytes;
            }
            case 0x0A: {
                // READ_VERSION

            } break;
            case 0x10: {
                int type = in[4];
                index--;
                if (index < 0) {
                    // 返回超时
                }
                if (type == 0x01) {
                    // 包括读取传感器信息
                    String[] sensor_ids = new String[] {"A3CB03", "03AC02", "13930F", "34823C"};

                    SensorProtocol sensorProtocol = new SensorProtocol();
                    byte[] sensor_byte = StringUtils.hexStrToByteArray(sensor_ids[index%4], 4);

                    sensorProtocol.setData(new byte[]{sensor_byte[1], sensor_byte[2], sensor_byte[3], sensor_byte[0],
                            0x06, 0x32, 0x4B, 0x00, 0x03, 0x05, 0x55, 0x00});
                    sensorProtocol.setCmdCode(0x20);
                    sensorProtocol.setHeadCode(SensorProtocol.ENDCODE);
                    sensorProtocol.setEndCode(SensorProtocol.HEADCODE);
                    byte[] bytes = sensorProtocol.getBytes();
                    String str = StringUtils.byteArrayToHexStr(bytes);
                    Log.d(TAG, "read: " + str);
                    return bytes;
                } else {
                    // 刻录传感器回调
                }
            } break;
            case 0x11: {
                // 复制ID

            } break;
            case 0x12: {
                // lf调整

            } break;
            case 0x13: {
                // PDA更新程序

            } break;
            default : {
                // 其他, 响应timeout
            } break;
        }
        return new byte[0];
    }
}
