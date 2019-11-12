package com.orange.tpms.utils;

import com.orange.tpms.bean.SensorData;
import com.orange.tpms.lib.hardware.HardwareApp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Command {
    public static String Rx = "";

    public static void Send(String a) {
        Rx = "";
        byte[] data = GetCrc(a);
        HardwareApp.send(data);
    }

    public static String GetCrcString(String a) {
        byte[] command = StringHexToByte(a);
        int xor = 0;
        for (int i = 0; i < command.length - 2; i++) {
            xor = xor ^ command[i];
        }
        command[command.length - 2] = (byte) xor;
        return bytesToHex(command);
    }

    public static byte[] GetCrc(String a) {
        byte[] command = StringHexToByte(a);
        int xor = 0;
        for (int i = 0; i < command.length - 2; i++) {
            xor = xor ^ command[i];
        }
        command[command.length - 2] = (byte) xor;
        return command;
    }

    public static byte[] StringHexToByte(CharSequence cs) {
        byte[] bytes = new byte[cs.length() / 2];
        for (int i = 0; i < (cs.length() / 2); i++)
            bytes[i] = (byte) Integer.parseInt(cs.toString().substring(2 * i, 2 * i + 2), 16);
        return bytes;
    }

    public static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    public static int byte2ToINT(byte[] bytes) {
        int high = bytes[0];
        int low = bytes[1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }
    public static SensorData GetId(String hex, String Lf) {
        SensorData data = new SensorData();
        try {
            Send("0A 10 000E 01 00 LF HEX 00 00 00 00 00 00 00 00 39 F5".replace("HEX", hex).replace(" ", "").replace("LF", Lf));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 10 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    data.success = false;
                    return data;
                }
                if (Rx.length() == 36) {
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.id = Rx.substring(16 - idcount, 16);
                    data.bat = StringHexToByte(Rx.substring(18, 20))[0];
                    data.kpa=byte2ToINT(StringHexToByte(Rx.substring(22,26)));
                    byte[] bytes=StringHexToByte(Rx.substring(18 , 22));
                    data.c=bytes[0]-bytes[1];
                    data.vol=StringHexToByte(Rx.substring(26 , 28))[0];
                    data.success = true;
                    return data;
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            data.success = false;
            return data;
        }
    }

    public static double getDatePoor(Date endDate, Date nowDate) {
        long diff = endDate.getTime() - nowDate.getTime();
        long sec = diff / 1000;
        return sec;
    }
}
