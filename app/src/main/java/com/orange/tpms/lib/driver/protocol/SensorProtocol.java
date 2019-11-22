package com.orange.tpms.lib.driver.protocol;

import com.orange.tpms.lib.utils.StringUtils;

/**
 * sensor传感器传输协议
 */
public class SensorProtocol {

    public static String TAG = SensorProtocol.class.getName();

    public static int HEADCODE = 0x0A;     // 标头码
    public static int ENDCODE = 0xF5;      // 结束码

    /**
     * 定义命令码
     */
    public static class CMDCODE {
        public static final int HANDSHAKE = 0x00;      // 确认传输两端是否存在
        public static final int ERASE_FLASH = 0x01;        // 擦除Flash
        public static final int WRITE_FLASH = 0x02;        // 烧写Flash
        public static final int READ_VERSION = 0x0A;       // 取得软件版本
        public static final int READ_HARDWARE_VERSION = 0x0C;       // 取得硬件版本
        public static final int REBOOT = 0x0D;             // 重启
        public static final int READ_SETTING = 0x10;       // 读取传感器设定
        public static final int WRITE_SENSOR = 0x13;       // 烧录传感器
        public static final int PROGRAM_CHECK = 0x14;       // program_check
        public static final int GET_SENSOR_INFO = 0x20;        // 传感器信息
        public static final int SEND_SENSOR_ID = 0x30;         // 传送传感器ID
        public static final int GET_WRITE_SENSOR_RESULT = 0x31;        // 传感器刻录结果
        public static final int COPY_SENSOR_ID = 0x11;             // 复制传感器ID
        public static final int LF_ADJUST = 0x12;              // LF功率调整
        public static final int SENSOR_SOFTWARE_UPDATE = 0x13;     // PDA传送传感器更新程序
        public static final int TIMEOUT_OR_ERR = 0x1C;         // Timeout或命令格式错误

        public static final int READ_WRITE_SENSOR = 0x30;       // 读取烧录传感器信息
    }

    protected int headCode;           // 标头码
    protected int cmdCode;            // 命令码
    protected int lenght;             // 长度码
    protected byte[] data;            // 数据
    protected int checkCode;          // 检查码
    protected int endCode;            // 结束码

    public int getHeadCode () {
        return headCode;
    }

    public int getCmdCode () {
        return cmdCode;
    }

    public int getLenght () {
        return lenght;
    }

    public void setLenght (int len) {
        this.lenght = len;
    }

    public byte[] getData () {
        return this.data;
    }

    public int getCheckCode () {
        return this.checkCode;
    }

    public int getEndCode () {
        return this.endCode;
    }

    public void setHeadCode (int headCode) {
        this.headCode = headCode;
    }

    public void setCmdCode (int cmdCode) {
        this.cmdCode = cmdCode;
    }

    public void setData (byte[] data) {
        this.data = data;
    }

    private void setCheckCode (int checkCode) {
        this.checkCode = checkCode;
    }

    public void setEndCode (int endCode) {
        this.endCode = endCode;
    }

    public SensorProtocol () {
        this.headCode = HEADCODE;
        this.endCode = ENDCODE;
    }

    /**
     * 初始化
     * @param cmdCode
     * @param data
     */
    public SensorProtocol (int cmdCode, byte[] data) {
        this.headCode = HEADCODE;
        this.endCode = ENDCODE;
        this.cmdCode = cmdCode;
        this.data = data;
    }

    /**
     * 重新初始化
     */
    public void reset () {

    }

    /**
     * 将二进制流转成对象
     * @param bytes
     */
    public static SensorProtocol toObject (byte[] bytes) {
        if (bytes == null || bytes.length < 7) {
            return null;
        }
        int headCode = bytes[0]&0xFF;
        int cmdCode = bytes[1]&0xFF;
        int len = bytes[2]*256+bytes[3];

        // 获取data块
        int data_len = len - 2;
        if (data_len > (bytes.length-6) || data_len < 0) {
            // 不符合条件
            return null;
        }
        byte[] tmp_data = new byte[data_len];
        for (int i=0;i<data_len;i++) {
            tmp_data[i] = bytes[i+4];
        }

        int endCode = bytes[1+1+2+data_len+1];

        // 检查码 (xor标头码 to 资料码)
        int checkCode = bytes[0];
        for (int i=1;i<4+data_len;i++) {
            checkCode = checkCode^bytes[i];
        }
        SensorProtocol sensorProtocol = new SensorProtocol();
        sensorProtocol.setHeadCode(headCode);
        sensorProtocol.setCmdCode(cmdCode);
        sensorProtocol.setCheckCode(checkCode);
        sensorProtocol.setData(tmp_data);
        sensorProtocol.setEndCode(endCode);
        sensorProtocol.setLenght(len);
        return sensorProtocol;
    }

    /**
     * 将对象转成二进制
     */
    public byte[] getBytes () {
        int data_len = this.data.length;
        int len = 4+this.data.length+2;
        byte[] bytes = new byte[len];
        bytes[0] = (byte) (this.headCode&0xFF);
        bytes[1] = (byte) (this.cmdCode&0xFF);

        String s_len = StringUtils.numToHex16(this.data.length+2);
        byte[] tmp_len_bytes = StringUtils.hexStrToByteArray(s_len, 2);
        bytes[2] = tmp_len_bytes[0];
        bytes[3] = tmp_len_bytes[1];

        for (int i=0;i<data_len;i++) {
            bytes[i+4] = this.data[i];
        }

        // 检查码 (xor标头码 to 资料码)
        int checkCode = bytes[0];
        for (int i=1;i<4+data_len;i++) {
            checkCode = checkCode^bytes[i];
        }

        // 检查码+结束码
        bytes[4+data_len] = (byte) (checkCode&0xFF);
        bytes[5+data_len] = (byte) (endCode&0xFF);
        return bytes;
    }
}
