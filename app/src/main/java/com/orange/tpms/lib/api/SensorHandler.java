package com.orange.tpms.lib.api;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.orange.tpms.helper.SystemUpdateHelper;
import com.orange.tpms.lib.driver.channel.UARTChannel;
import com.orange.tpms.lib.driver.source.SensorSource;
import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.utils.StringUtils;
import com.orange.tpms.utils.HttpDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import bean.hardware.SensorDataBean;
import bean.hardware.SensorVersionBean;
import cz.msebera.android.httpclient.util.EncodingUtils;
import cz.msebera.android.httpclient.util.TextUtils;

import static android.media.AudioRecord.MetricsConstants.ENCODING;

/**
 * 传感器控制类
 */
public class SensorHandler extends BApi {

    public static String TAG = SensorHandler.class.getName();

    private static SensorHandler instance = null;

    SensorSource sensorSource;
    HardwareApp hardwareApp;

    public SensorHandler (HardwareApp hardwareApp) {
        this.sensorSource = new SensorSource(hardwareApp);
        this.hardwareApp = hardwareApp;
        this.sensorSource.setIChannel(new UARTChannel(hardwareApp));
    }

    public SensorHandler () {
    }

    public void init (HardwareApp hardwareApp) {
        this.sensorSource = new SensorSource(hardwareApp);
        this.sensorSource.setIChannel(new UARTChannel(hardwareApp));
    }

    public static SensorHandler getInstance (HardwareApp hardwareApp) {
        if (hardwareApp == null) {
            return instance;
        }
        if (instance == null) {
            instance = new SensorHandler(hardwareApp);
        } else {
        }
        return instance;
    }

    public static SensorHandler getInstance () {
        if (instance == null) {
            instance = new SensorHandler();
        } else {
        }
        return instance;
    }

    /**
     * 确认与模块正常联机 (用来做心跳, 发现异常则重新连接)
     * @return
     */
    @Deprecated
    public int ping () {
        int ret = this.sensorSource.handShake(new SensorSource.SensorRespondCB () {

            @Override
            public void receive(int ret) {

            }
        });
        return ret;
    }

    /**
     * 确认与模块正常联机 (用来做心跳, 发现异常则重新连接)
     * @return
     */
    public int ping (SensorSource.SensorRespondCB sensorRespondCB) {
        int ret = this.sensorSource.handShake(sensorRespondCB);
        return ret;
    }

    /**
     * 擦除PDA Flash
     * @return
     */
    @Deprecated
    public int eraseFlash () {
        int ret = this.sensorSource.eraseFlash(new SensorSource.SensorRespondCB () {

            @Override
            public void receive(int ret) {

            }
        });
        return ret;
    }

    public int eraseFlash (SensorSource.SensorRespondCB sensorRespondCB) {
        int ret = this.sensorSource.eraseFlash(sensorRespondCB);
        return ret;
    }

    /**
     * 读取所有传感器信息
     * num 目前只支持1
     * @return
     */
    public SensorDataBean[] readSensor (int num, String hex, SensorSource.SensorDataBeansCb sensorDataBeansCb,String a) {
        return this.sensorSource.readSensor(num, hex, sensorDataBeansCb,a);
    }

    @Deprecated
    public SensorDataBean[] readSensor (int num, String hex) {
        return null;
    }

    /**
     * 读取软件版本
     * @return
     */
    public SensorVersionBean getSoftwareVersion (SensorSource.SensorVersionCb sensorVersionCb) {
        return this.sensorSource.getVersion(sensorVersionCb);
    }

    /**
     * 读取硬件版本
     * @return
     */
    public SensorVersionBean getHardwareVersion (SensorSource.SensorVersionCb sensorVersionCb) {
        return this.sensorSource.getHardwareVersion(sensorVersionCb);
    }

    /**
     * 写入Flash
     */
    private int writeFlash (byte[] inputData, SensorSource.SensorRespondCB sensorRespondCB) {
        return this.sensorSource.writeFlash (inputData, sensorRespondCB);
    }



    /**
     * 复制传感器id
     * @return
     */
    public int copySensorId (String srcId, String descId, SensorSource.SensorRespondCB sensorRespondCB) {
        Log.d(TAG, "srcId:"+srcId+",descId:"+descId);
        byte[] srcId_bytes = StringUtils.hexStrToByteArray(srcId);
        byte[] descId_bytes = StringUtils.hexStrToByteArray(descId);

        if (srcId.length() < 6 || srcId.length() > 8) {
            // 返回错误
            return -1;
        }
        if (srcId.length() < 6 || srcId.length() > 8) {
            return -1;
        }
        byte[] inputData = new byte[12];
        int j=0;
        for (int i=0;i<srcId_bytes.length;i++) {
            inputData[j++] = srcId_bytes[i];
        }
        inputData[j++] = (byte)srcId.length();
        for (int i=0;i<descId_bytes.length;i++) {
            inputData[j++] = descId_bytes[i];
        }
        inputData[j++] = (byte)descId.length();
        inputData[j++] = 0x00;
        inputData[j++] = 0x00;
        Log.d(TAG, "copySensorId: "+StringUtils.byteArrayToHexStr(inputData));
        return this.sensorSource.copySensorId(inputData, sensorRespondCB);
    }

    //通过反射获取ro.serialno
    private String getSerialNumber(){
        String serial = "unknown";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            String barcode = (String) get.invoke(c, "gsm.serial");
            String[] code = barcode.split(" ");
            serial = code[0];
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return serial;
    }

    /**
     * 获取硬件信息
     * @return
     */
    public HardwareInfo getHardwareInfo () {
        HardwareInfo info = new HardwareInfo();
        info.serianNum = getSerialNumber();
        info.model = Build.MODEL;
        info.hardwareVersion = android.os.Build.VERSION.RELEASE;
        return info;
    }

    /**
     * 烧录Flash, 自动reboot
     * @return
     */
    public int writeFlashWithReboot (FlashWriteProgress flashWriteProgress) {
        SensorHandler sensorHandler = this;
        byte[] buffer = sensorHandler.getAssetsFileStream(hardwareApp.app, "OGPDA_191024_APP.x2");

        sensorHandler.ping(ret -> {
            if (ret == 1) {
                // bootloader模式, 直接烧录Flash
                sensorHandler.writeFlash (buffer, 0, flashWriteProgress);
            } else {
                sensorHandler.reboot(tmp_ret -> {
                    if (tmp_ret == 0x01) {
                        // 成功清理Flash, 准备烧录
                        sensorHandler.writeFlash(buffer, 0, flashWriteProgress);
                    } else {
                        // flashWriteProgress.fail(tmp_ret, 0, 0);
                        sensorHandler.ping(tmp2_ret -> {
                            if (tmp2_ret == 1) {
                                // bootloader模式, 直接烧录Flash
                                sensorHandler.writeFlash (buffer, 0, flashWriteProgress);
                            }
                        });
                    }
                });
            }
        });
        return 0;
    }

    /**
     * 烧录Flash, 自动reboot
     * @return
     */
    public int writeFlashWithReboot (String filePath,FlashWriteProgress flashWriteProgress) {
        SensorHandler sensorHandler = this;
        byte[] buffer = sensorHandler.getExternalFileStream(hardwareApp.app, filePath);

        sensorHandler.ping(ret -> {
            if (ret == 1) {
                // bootloader模式, 直接烧录Flash
                sensorHandler.writeFlash (buffer, 0, flashWriteProgress);
            } else {
                sensorHandler.reboot(tmp_ret -> {
                    if (tmp_ret == 0x00) {
                        // 成功清理Flash, 准备烧录
                        sensorHandler.writeFlash(buffer, 0, flashWriteProgress);
                    } else {
                        flashWriteProgress.fail(tmp_ret, 0, 0);
                    }
                });
            }
        });
        return 0;
    }

    /**
     * 写入Flash bytes
     * 可加入线程同步把递归改成非递归方式
     * @return
     */
    private int write_buf_len = 0;
    private int writeFlash (byte[] buffer, int start, FlashWriteProgress flashWriteProgress) {
        int j_data = 0;      // 输入数据下标
        int max_write_buf_len = 132;
        byte[] data = new byte[1];

        // 重启reboot
        write_buf_len = max_write_buf_len;
        for (int i=start;i<buffer.length;i++) {
            if (i%max_write_buf_len==0) {
                // 创建新data buf
                int i_lack_byte = buffer.length-i;  // 剩余buf字节
                if ((i_lack_byte) >= max_write_buf_len) {
                    write_buf_len = max_write_buf_len;
                    data = new byte[write_buf_len];
                } else {
                    write_buf_len = i_lack_byte;
                    data = new byte[write_buf_len];
                }
                j_data = 0;
                Log.d(TAG, String.format("write %s", data.length));
                Log.d(TAG, String.format("write from %s", start));

                if (start == 0) {
                    flashWriteProgress.start(buffer.length);
                }
                // 写入
                /* try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            }
            data[j_data++] = buffer[i];
            if (j_data == write_buf_len) {
                // 存储到二维数组内
                sensorSource.writeFlash(data, ret -> {
                    // 收到成功的回调后继续下一条消息
                    Log.d(TAG, String.format("ret:%s", ret));
                    if (ret == 0) {
                        // 得到烧录成功的回调
                        flashWriteProgress.progress(start+write_buf_len, buffer.length);
                        writeFlash (buffer, start+write_buf_len, flashWriteProgress);
                    } else if (ret == 0x0b) {
                        // 烧录完成的响应
                        flashWriteProgress.finish(buffer.length);
                        return ;
                    } else {
                        flashWriteProgress.fail(ret, start, buffer.length);
                        return ;
                    }
                });
                break;
            }
        }
        return 0;
        // 二维数组逐行发送
        // sensorHandler.ping(ret -> Log.d(TAG, "ret:"+ret));
        // sensorHandler.writeFlash(data, ret -> Log.d(TAG, String.format("ret:%s", ret)));
    }

    /**
     * 重启
     * @return
     */
    public int reboot (SensorSource.SensorRespondCB sensorRespondCB) {
        return this.sensorSource.reboot(sensorRespondCB);
    }

    public int programCheck (SensorSource.SensorRespondProgramCheck sensorRespondCB) {
        return this.sensorSource.programCheck(sensorRespondCB);
    }

    /**
     * 获取烧录传感器配置
     * @包括: 需要烧录多少颗传感器、需要烧录的总大小
     */
    public int writeSensorFirmwareSetting (int number, byte[] buffer, SensorSource.SensorWriteInfo sensorWriteInfo) {
        // 烧录传感器
        byte[] data = new byte[12];
        data[0] = 0x02;
        data[1] = (byte)number;
        data[2] = 0x00;
        data[3] = 0x2C;
        data[4] = buffer[7];
        data[5] = buffer[8];
        data[6] = buffer[11];
        data[7] = buffer[12];
        data[8] = 0x00;
        data[9] = 0x00;
        data[10] = 0x00;
        data[11] = 0x00;
        return this.sensorSource.writeSensorFirmwareSetting (sensorWriteInfo, data);
    }

    /**
     * 烧录传感器返回进度
     * @return
     */
    public int writeSensorFirmwareWithProgress (int number, FlashWriteProgress flashWriteProgress,String filename) {
        String content = getSensorByteFromStrStream(hardwareApp.app, filename);
        byte[] buffer = StringUtils.hexStrToByteArray(content);
        Log.d(TAG, "buffer len: "+buffer.length);

        retry_num = 0;      // 重置重试次数
        this.tmp_data = null;       // 初始化
        flashWriteProgress.start(buffer.length);
        // 烧录传感器
        writeSensorFirmwareSetting(number, buffer, (ret, sensorWriteInfoBean) -> {
            // 打印
            if (ret == 0) {
                Log.d(TAG, String.format("writeSensorFirmareSetting => byteNum%s, num:%s", sensorWriteInfoBean.getByteNum(),
                        sensorWriteInfoBean.getNum()));
                if (number != sensorWriteInfoBean.getNum()) {
                    flashWriteProgress.fail(-4, 0, buffer.length);
                } else {
                    writeSensorFirmware(buffer, 0, sensorWriteInfoBean.getByteNum(), sensorWriteInfoBean.getByteNum(), flashWriteProgress);
                }
                return ;
            }
        });
        return 0;
    }

    /**
     * 烧录传感器返回进度
     * @return
     */
    public int writeSensorFirmwareWithProgress (int number, String filePath,FlashWriteProgress flashWriteProgress) {
        String content = null;
        if(TextUtils.isEmpty(filePath)){
            content = getSensorByteFromStrStream(hardwareApp.app, "2C_V0.3.s19");
        }else{
            content = getSensorByteFromStrStreamWithPath(hardwareApp.app, filePath);
        }
        byte[] buffer = StringUtils.hexStrToByteArray(content);
        Log.d(TAG, "buffer len: "+buffer.length);

        flashWriteProgress.start(buffer.length);
        // 烧录传感器
        writeSensorFirmwareSetting(number, buffer, (ret, sensorWriteInfoBean) -> {
            // 打印
            if (ret == 0) {
                Log.d(TAG, String.format("writeSensorFirmareSetting => byteNum%s, num:%s", 2048,
                        number));
                writeSensorFirmware(buffer, 0, sensorWriteInfoBean.getByteNum(), sensorWriteInfoBean.getByteNum(), flashWriteProgress);
                return ;
            }
        });
        return 0;
    }

    /**
     * 烧录传感器
     */
    int retry_num = 0;
    public static final int RETRY_NUM = 12;
    private int writeSensorFirmware (byte[] buffer, int start, int end, int byteNum, /** 1-2048,2-6144 */ FlashWriteProgress flashWriteProgress) {
        int j_data = 0;      // 输入数据下标
        int max_write_buf_len = 200;
        byte[] data = new byte[1];

        Log.d(TAG, String.format("begin1 start:%s, end:%s", start, end));

        // 获取第几个200byte
        int index = (int) (Math.ceil((double)(start/max_write_buf_len))+1);

        // 重启reboot
        if ((end-start) >= max_write_buf_len) {
            write_buf_len = max_write_buf_len;
        } else {
            write_buf_len = end-start;
        }

        boolean ifSuc = this.IfNeedProgramCheck(index, byteNum);
        Log.d(TAG, "IfNeedProgramCheck: "+ifSuc+",index:"+index+",byteNum:"+byteNum);

        // 写入进度
        flashWriteProgress.progress(start, end);

        if (!ifSuc && start < (end-write_buf_len)) {
            // 不需要烧录, 烧录下一个
            writeSensorFirmware(buffer, write_buf_len+start, end, byteNum, flashWriteProgress);
            return 1;
        } else if (!ifSuc && (start+write_buf_len)==end) {
            // 最后一个且不需要重传则进行program check
            this.sensorSource.programCheck((tmp_ret,tmp_data) -> {
                // 确认烧录完成
                if (tmp_ret == 0) {
                    // 响应
                    int data0 = tmp_data[0];
                    int data1 = tmp_data[1];

                    if (data1 == 0x03) {
                        // 烧录完成
                        flashWriteProgress.finish(end);       // 完成
                        return ;
                    } else if (data1 == 0x00) {
                        // 烧录中间过程
                        int lack_index = programCheckSensor (tmp_data, byteNum);
                        Log.d(TAG, String.format("lack_index: %s, byteNum:%s, retry_num:%s", lack_index, byteNum, retry_num));
                        if (lack_index >= 0 && retry_num < 11) {
                            // 存在需要重新烧录
                            retry_num++;
                            writeSensorFirmware(buffer, max_write_buf_len*lack_index, end, byteNum, flashWriteProgress);
                            return ;
                        } else {
                            // 报错
                            flashWriteProgress.fail(-2, 0, end);        // 报错
                            return ;
                        }
                    } else {
                        flashWriteProgress.fail(data1, 0, end);       // 完成
                        return ;
                    }
                }
            });
            return 1;
        }

        Log.d(TAG, String.format("begin2 start:%s, end:%s, index:%s", start, end, index));

        // 完成
        if (start >= end) {
            flashWriteProgress.finish(end);
            return 1;
        }

        Log.d(TAG, String.format("begin start:%s, end:%s, index:%s", start, end, index));
        for (int i=start;i<end;i++) {
            if (i==start) {
                // 创建新data buf
                int i_lack_byte = end-i;  // 剩余buf字节
                if ((i_lack_byte) >= max_write_buf_len) {
                    write_buf_len = max_write_buf_len;
                } else {
                    write_buf_len = i_lack_byte;
                }
                data = new byte[write_buf_len];
                j_data = 0;
                Log.d(TAG, String.format("write %s", data.length));
            }
            data[j_data++] = buffer[i];
            if (j_data == write_buf_len) {
                // 存储到二维数组内
                sensorSource.writeSensorFireware(ret -> {
                    Log.d(TAG, String.format("ret:%s,start:%s,wirte_buf_len:%s,end:%s, index:%s", ret, start, write_buf_len, end, index));
                    if ((start+write_buf_len)==end) {
                        // program check
                        // 重新校验下是否还有需要重新传的
                        this.sensorRetryProgramCheck (buffer, start, end, max_write_buf_len, byteNum, flashWriteProgress);
                    } else {
                        if (ret >= 0) {
                            // 格式符合
                            writeSensorFirmware(buffer, start + write_buf_len, end, byteNum, flashWriteProgress);
                            return;
                        } else {
                            Log.d(TAG, String.format("ret error:%s", ret));
                        }
                    }
                }, data, index);
                break;
            }
        }
        return 0;
    }

    /**
     * 进行校验
     * @param buffer
     * @param start
     * @param end
     * @param max_write_buf_len
     * @param flashWriteProgress
     */
    private void sensorRetryProgramCheck (byte[] buffer, int start, int end, int max_write_buf_len, int byteNum, FlashWriteProgress flashWriteProgress) {
        // 最后一个且不需要重传则进行program check
        this.sensorSource.programCheck((tmp_ret,tmp_data) -> {
            // 确认烧录完成
            if (tmp_ret == 0) {
                // 响应
                int data0 = tmp_data[0];
                int data1 = tmp_data[1];

                if (data1 == 0x03) {
                    // 烧录完成
                    flashWriteProgress.finish(end);       // 完成
                } else if (data1 == 0x00) {
                    // 烧录中间过程
                    int lack_index = programCheckSensor (tmp_data, byteNum);
                    if (lack_index >= 0 && retry_num < RETRY_NUM) {
                        // 存在需要重新烧录
                        retry_num++;
                        writeSensorFirmware(buffer, max_write_buf_len*lack_index, end, byteNum, flashWriteProgress);
                        return ;
                    } else if (lack_index < 0) {
                        retry_num++;
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        flashWriteProgress.finish(end);
                        return ;
                    } else {
                        flashWriteProgress.fail(-3, 0, end);       // 完成
                    }
                } else {
                    flashWriteProgress.fail(data1, 0, end);       // 完成
                }
            } else {
                // 非0表示失败, 需要重新校验
                if (retry_num < 11) {
                    // 存在需要重新烧录
                    retry_num++;
                    this.sensorRetryProgramCheck(buffer, start, end, max_write_buf_len, byteNum, flashWriteProgress);
                    return ;
                } else {
                    flashWriteProgress.fail(-1, 0, end);       // 传感器坏了, 需要人工处理
                    return ;
                }
            }
        });
    }

    /**
     * 补烧录
     */
    byte[] tmp_data = null;
    private int programCheckSensor (byte[] data, int byteNum) {
        tmp_data = data;

        // 判断是否缺漏
        if (byteNum == 2048) {
            int data4 = data[4] & 0xFF;
            int data5 = data[5] & 0xFF;

            // Log.d(TAG, String.format("check data:%s,%s", data4, data5));
            // 判断是否缺漏
            int totalSign = data5 + data4 * 16 * 16;
            int i = 0;      // 第i位
            int m_byte = 11; // 总共11位

            for (i = 0; i < m_byte; i++) {
                int map = 1 << i;
                if ((totalSign & map) == 0) {
                    // 缺失, 补烧录
                    return i;
                } else {
                    // 不缺失
                    continue;
                }
            }
        } else if (byteNum == 6144) {
            // 6144
            int data2 = data[2] & 0xFF;
            int data3 = data[3] & 0xFF;
            int data4 = data[4] & 0xFF;
            int data5 = data[5] & 0xFF;

            Log.d(TAG, String.format("check data:%s,%s,%s,%s", data2, data3, data4, data5));
            // 判断是否缺漏
            long totalSign = data5+data4*256+data3*256*256+data2*256*256*256;
            int i = 0;      // 第i位
            int m_byte = 31; // 总共31位

            for (i = 0; i < m_byte; i++) {
                long map = 1 << i;
                // Log.d(TAG, "map:"+map+",totalSign:"+totalSign);

                if ((totalSign & map) == 0) {
                    // 缺失, 补烧录
                    return i;
                } else {
                    // 不缺失
                    continue;
                }
            }
        }
        return -1;      // 不缺失
    }

    /**
     * 补烧录
     * @param byteNum 2048, 6144
     */
    private boolean IfNeedProgramCheck (int index, int byteNum) {
        byte[] data = this.tmp_data;

        if (data == null) {
            return true;
        }
        if (byteNum == 2048) {
            int data4 = data[4] & 0xFF;
            int data5 = data[5] & 0xFF;

            // 判断是否缺漏
            int totalSign = data5 + data4 * 16 * 16;
            int i = 0;      // 第i位
            int m_byte = 11; // 总共11位

            for (i = 0; i < m_byte; i++) {
                int map = 1 << i;
                if ((totalSign & map) == 0) {
                    // 缺失, 补烧录
                    if ((i + 1) == index) {
                        return true;
                    }
                } else {
                    // 不缺失
                    continue;
                }
            }
        } else if (byteNum == 6144) {
            // 6144
            int data2 = data[2] & 0xFF;
            int data3 = data[3] & 0xFF;
            int data4 = data[4] & 0xFF;
            int data5 = data[5] & 0xFF;

            Log.d(TAG, String.format("check data:%s,%s,%s,%s", data2, data3, data4, data5));
            // 判断是否缺漏
            long totalSign = data5+data4*256+data3*256*256+data2*256*256*256;
            int i = 0;      // 第i位
            int m_byte = 31; // 总共31位

            for (i = 0; i < m_byte; i++) {
                long map = 1 << i;
                // Log.d(TAG, "map:"+map+",totalSign:"+totalSign);

                if ((totalSign & map) == 0) {
                    // 缺失, 补烧录
                    if ((i+1) == index) {
                        return true;
                    }
                } else {
                    // 不缺失
                    continue;
                }
            }
        }
        return false;      // 不缺失
    }

    /**
     * 更新模组固件
     */
    public void updatePDAFirmware () {

    }

    /**
     * 获取Flash固件字节
     * @return
     */
    public byte[] getAssetsFileStream (Context context, String fileName) {
        try {
            InputStream in = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int length = in.available();
            //创建byte数组
            byte[] buffer = new byte[length];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Flash固件字节
     * @return
     */
    public byte[] getExternalFileStream (Context context, String fileName) {
        try {
            InputStream in = new FileInputStream(new File(fileName));
            // InputStream in = context.getResources().getAssets().open(fileName);
            //获取文件的字节数
            int length = in.available();
            //创建byte数组
            byte[] buffer = new byte[length];
            //将文件中的数据读到byte数组中
            in.read(buffer);
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符字节文件
     * @return
     */
    public String getSensorByteFromAssetsStrStream (Context context, String fileName) {
        byte[] buffer = this.getAssetsFileStream(context, fileName);
        String result = EncodingUtils.getString(buffer, ENCODING);
        result = StringUtils.replaceBlank(result);
        return result;
    }

    /**
     * 字符字节文件
     * @return
     */
    private String getSensorByteFromStrStream (Context context, String fileName) {
        // 本地如果存在sensor固件则使用本地
        String filePath = context.getApplicationContext().getFilesDir().getPath()+"/"+fileName+".s19";
        File sensorFd = new File (filePath);
        byte[] buffer = null;
        if (sensorFd.exists()) {
            // 如果文件存在则烧录
            Log.d(TAG, "getSensorByteFromStrStream: "+filePath);
            buffer = this.getExternalFileStream(context, filePath);
        } else {
            buffer = this.getAssetsFileStream(context, fileName);
        }
        String result = EncodingUtils.getString(buffer, ENCODING);
        result = StringUtils.replaceBlank(result);
        return result;
    }

    /**
     * 字符字节文件
     * @return
     */
    private String getSensorByteFromStrStreamWithPath (Context context, String filePath) {
        byte[] buffer = this.getExternalFileStream(context, filePath);
        String result = EncodingUtils.getString(buffer, ENCODING);
        result = StringUtils.replaceBlank(result);
        return result;
    }

    public static class HardwareInfo {

        public String serianNum = "";       // 序列号
        public String model = "";           // 机型型号
        public String hardwareVersion = "";     // 硬件版本

    }

    /**
     * Flash烧录进度
     */
    public static interface FlashWriteProgress {

        public void start(int total);        // 开始烧录
        public void progress (int progress, int total);     // 烧录进度
        public void finish (int total);      // 烧录完成
        public void fail (int errcode, int start, int total);     // 烧录失败, start:失败的烧录起始点
    }
}
