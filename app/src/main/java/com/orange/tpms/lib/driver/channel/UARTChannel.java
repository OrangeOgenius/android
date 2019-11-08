package com.orange.tpms.lib.driver.channel;

import android.util.Log;

import com.orange.tpms.lib.hardware.HardwareApp;
import com.orange.tpms.lib.test.HardTest;
import com.orange.tpms.lib.utils.StringUtils;

/**
 * 串口通道
 */
public class UARTChannel implements IChannel {

    public static String TAG = UARTChannel.class.getName();

    HardwareApp hardwareApp;

    public UARTChannel (HardwareApp hardwareApp) {
        this.hardwareApp = hardwareApp;
    }

    @Override
    public int open() {
        return 0;
    }

    @Override
    public int close() {
        return 0;
    }

    WaitThread t1;

    @Override
    public int write(byte[] in) {
        // 硬件发送, 经过中转
        new Thread(() -> {
            String str = StringUtils.byteArrayToHexStr(in);
            str = str_tobyte(str);
            Log.d("DATA:", "TX: " + str);
            Log.d(TAG, "write: " + str);
            byte[] byte_send = hexStringToBytes(str);

            /*int split_num = 0;
            int MAX_NUM = 50;
            while (split_num*MAX_NUM < byte_send.length) {
                int remain_len = byte_send.length-(split_num*MAX_NUM);
                int len = (remain_len)>MAX_NUM?MAX_NUM:remain_len;

                byte[] wait_send_byte = new byte[len];      //  待发送字节
                for (int j=0;j<wait_send_byte.length;j++) {
                    wait_send_byte[j] = byte_send[split_num*MAX_NUM+j];
                }

                String tmp_str = StringUtils.byteArrayToHexStr(wait_send_byte);
                Log.d(TAG,String.format("第 %s %s byte: %s", split_num, wait_send_byte.length, tmp_str));
                HardwareApp.send(new byte[]{0x1B, 0x23, 0x23, 0x55, 0x54, 0x54, 0x32});
                HardwareApp.send(new byte[]{(byte) wait_send_byte.length});
                HardwareApp.send(wait_send_byte);
                split_num++;
            }*/
            HardwareApp.send(new byte[]{0x1B, 0x23, 0x23, 0x55, 0x54, 0x54, 0x32});
            HardwareApp.send(new byte[]{(byte) byte_send.length});
            HardwareApp.send(byte_send);
        }).start();

        // t1 = new WaitThread("t1");
        // 每次访问都会重置
        /*hardwareApp.setMsgDataReceiver(new HardwareApp.DataReceiver() {
            @Override
            public void scanMsgReceive(String content) {
            }

            @Override
            public void uart2MsgReceive(String content) {
                t1.msg = content;
                Log.d(TAG, "uart2MsgReceive: " + content);
                synchronized (t1) {
                    t1.notify();
                }
            }
        });*/
        if (in != null) {
            return in.length;
        }
        return 0;
    }

    public static String str_tobyte(String str){

        StringBuffer s1 = new StringBuffer(str);
        StringBuffer s2 = new StringBuffer();
        int index;
        for (index = 2; index < s1.length(); index += 3) {
            s1.insert(index, ',');
        }
        String[] array = s1.toString().split(",");
        for (String string : array) {
            s2.append(string+" ");
        }

        return s2.toString();
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789abcdef".indexOf(c);
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

    @Override
    public byte[] read() {

        /*Log.d(TAG, "begin read...");
        if (t1 == null) {
            return new byte[0];
        }

        synchronized (t1) {
            //启动“线程1”
            Log.d(TAG, Thread.currentThread().getName() + " start t1");
            t1.start();
            //主线程等待t1通过notify() 唤醒
            Log.d(TAG, Thread.currentThread().getName() + " wait");
            try {
                t1.wait(4000);     // 4s超时
                Log.d(TAG, Thread.currentThread().getName() + " continue");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        byte[] bytes = new byte[0];
        Log.d(TAG, "read: "+ t1.msg);
        return bytes;*/
        return new byte[0];
    }

    class WaitThread extends Thread {
        public String msg;

        public WaitThread(String name){
            super(name);
            msg = "";
        }

        public void run(){
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
