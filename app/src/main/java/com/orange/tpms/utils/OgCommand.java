package com.orange.tpms.utils;

import android.app.Activity;
import android.util.Log;
import com.orange.blelibrary.blelibrary.BleActivity;
import com.orange.tpms.Callback.*;
import com.orange.tpms.bean.PublicBean;
import com.orange.tpms.bean.SensorData;
import com.orange.tpms.lib.hardware.HardwareApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OgCommand {
    public static String Rx = "";
    public static String NowTag = "";
    public static String SendTag = "";

    public static void Send(String a) {
        Rx = "";
        SendTag = NowTag;
        byte[] data = GetCrc(a.toUpperCase());
        Log.d("DATA:", "TX:" + bytesToHex(data));
        HardwareApp.send(new byte[]{0x1B, 0x23, 0x23, 0x55, 0x54, 0x54, 0x32});
        HardwareApp.send(new byte[]{(byte) data.length});
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

    public static String getBit(String a) {
        byte data[] = StringHexToByte(a);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append((data[i] >> 7) & 0x1);
            sb.append((data[i] >> 6) & 0x1);
            sb.append((data[i] >> 5) & 0x1);
            sb.append((data[i] >> 4) & 0x1);
            sb.append((data[i] >> 3) & 0x1);
            sb.append((data[i] >> 2) & 0x1);
            sb.append((data[i] >> 1) & 0x1);
            sb.append((data[i] >> 0) & 0x1);
        }

        return sb.toString();
    }

    public static String getBit(byte by) {
        StringBuffer sb = new StringBuffer();
        sb.append((by >> 7) & 0x1);
        sb.append((by >> 6) & 0x1);
        sb.append((by >> 5) & 0x1);
        sb.append((by >> 4) & 0x1);
        sb.append((by >> 3) & 0x1);
        sb.append((by >> 2) & 0x1);
        sb.append((by >> 1) & 0x1);
        sb.append((by >> 0) & 0x1);
        return sb.toString();
    }

    public static int byte2ToINT(byte[] bytes) {
        int high = bytes[0];
        int low = bytes[1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }

    //    public static Read
    public static ArrayList<SensorData> GetPrId(String hex, String Lf) {
        ArrayList<SensorData> array = new ArrayList<>();

        try {
            Send("0A 10 000E 01 02 LF HEX 00 00 00 00 00 00 00 00 39 F5".replace("HEX", hex).replace(" ", "").replace("LF", Lf));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 20 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 20) {
                        ReOpen();
                    }
                    return array;
                }
                if (Rx.length() >= 36) {
                    SensorData data = new SensorData();
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.id = Rx.substring(8, 16);
                    data.idcount = idcount;
                    data.bat = getBit(StringHexToByte(Rx.substring(28, 30))[0]).substring(3, 4);
                    data.kpa = byte2ToINT(StringHexToByte(Rx.substring(22, 26)));
                    byte[] bytes = StringHexToByte(Rx.substring(18, 22));
                    data.c = bytes[1] - bytes[0];
                    data.vol = 22 + (StringHexToByte(Rx.substring(26, 28))[0] & 0x0F);
                    data.success = true;
                    array.add(data);
                    if (array.size() == PublicBean.ProgramNumber) {
                        return array;
                    } else {
                        if (Rx.length() > 36) {
                            Rx = Rx.substring(36);
                        } else {
                            Rx = "";
                        }
                    }
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return array;
        }
    }

    //    public static Read
    public static ArrayList<SensorData> GetPr(String Lf, int count, String hex) {
        ArrayList<SensorData> response = new ArrayList<SensorData>();
        try {
            String co = Integer.toHexString(count);
            while (co.length() < 2) {
                co = "0" + co;
            }
            Send("0A 10 000E 01 00 LF hex 00 00 00 00 count 00 00 00 39 F5".replace(" ", "").replace("LF", Lf).replace("count", co).replace("hex", hex));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 20 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 20) {
                        ReOpen();
                    }
                    return response;
                }
                if (Rx.length() >= 36) {
                    SensorData data = new SensorData();
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.idcount = idcount;
                    data.id = Rx.substring(8, 16);
                    data.bat = getBit(StringHexToByte(Rx.substring(28, 30))[0]).substring(3, 4);
                    data.kpa = byte2ToINT(StringHexToByte(Rx.substring(22, 26)));
                    byte[] bytes = StringHexToByte(Rx.substring(18, 22));
                    data.c = bytes[1] - bytes[0];
                    data.vol = 22 + (StringHexToByte(Rx.substring(26, 28))[0] & 0x0F);
                    data.success = true;
                    response.add(data);
                    Rx = Rx.substring(36);
                    if (response.size() == count) {
                        return response;
                    }
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return response;
        }
    }

    //    public static Read
    public static SensorData GetId(String hex, String Lf) {
        SensorData data = new SensorData();
        try {
            Send("0A 10 000E 01 00 LF HEX 00 00 00 00 00 00 00 00 39 F5".replace("HEX", hex).replace(" ", "").replace("LF", Lf));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    data.success = false;
                    if (time > 15) {
                        ReOpen();
                    }
                    return data;
                }
                if (Rx.length() >= 36) {
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.idcount = idcount;
                    data.id = Rx.substring(16 - idcount, 16);
//                    data.id=Rx.substring(8, 16);
                    data.bat = getBit(StringHexToByte(Rx.substring(28, 30))[0]).substring(3, 4);
                    data.kpa = byte2ToINT(StringHexToByte(Rx.substring(22, 26)));
                    byte[] bytes = StringHexToByte(Rx.substring(18, 22));
                    data.c = bytes[1] - bytes[0];
                    data.vol = 22 + (StringHexToByte(Rx.substring(26, 28))[0] & 0x0F);
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

    public static Program_C P_Callback;

    public static void Program(String Lf, String Hex, String count, String s19, Activity activity, Program_C caller, ArrayList<String> sensor) {
        try {
            P_Callback = caller;
            FileInputStream fo = new FileInputStream("/sdcard/files19/" + s19 + ".s19");
            InputStreamReader fr = new InputStreamReader(fo);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String s = br.readLine();
                if (s != null && !s.equals("null")) {
                    sb.append(s);
                }
            }
            if (SendTrigerInfo(sensor) && ProgramFirst(Lf, Hex, count, sb.toString())) {
                caller.Program_Finish(ProgramCheck(spilt));
            } else {
                caller.Program_Finish(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            caller.Program_Finish(false);
        }
    }

    public static boolean SendTrigerInfo(ArrayList<String> sensor) {
        try {
            for (int i = 0; i < sensor.size(); i++) {
                String position = Integer.toHexString(i + 1);
                while (position.length() < 2) {
                    position = "0" + position;
                }
                String id = (sensor.get(i));
                while (id.length() < 8) {
                    id = "0" + id;
                }
                Send("0A 15 00 0E position ID 00 00 00 00 00 00 00 18 F5".replace("position", position).replace("ID", id).replace(" ", ""));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                Date past = sdf.parse(sdf.format(new Date()));
                while (true) {
                    Date now = sdf.parse(sdf.format(new Date()));
                    double time = getDatePoor(now, past);
                    if (time > 20 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                        if (time > 20) {
                            ReOpen();
                        }
                        return false;
                    }
                    if (Rx.length() == 36) {
                        break;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    static String spilt;

    public static boolean ProgramFirst(String Lf, String Hex, String count, String data) {
        try {

            while (count.length() < 2) {
                count = "0" + count;
            }
            while (Hex.length() < 2) {
                Hex = "0" + Hex;
            }
            String B8 = data.substring(14, 16);
            String B9 = data.substring(16, 18);
            String B12 = data.substring(22, 24);
            String B13 = data.substring(24, 26);
            Send("0A 10 00 0E  02 CT  Lf Hex 8b 9b 12b 13b 00 00 00 00 ff f5".replace("CT", count).replace("Lf", Lf).replace("Hex", Hex)
                    .replace("8b", B8).replace("9b", B9).replace("12b", B12).replace("13b", B13).replace(" ", ""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 20 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 20) {
                        ReOpen();
                    }
                    return false;
                }
                if (Rx.length() >= 36) {
                    spilt = (Rx.substring(10, 12).equals("04")) ? data.substring(0, 2048 * 2) : data.substring(0, 6144 * 2);
                    return WriteFlash(spilt);
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void ReOpen() {
        try {
            Log.e("DATA:", "重新上電");
            HardwareApp.getInstance().open5V(false);
            Thread.sleep(500);
            HardwareApp.getInstance().open9V(false);
            Thread.sleep(500);
            HardwareApp.getInstance().openPB5(false);
            Thread.sleep(500);
            HardwareApp.getInstance().setGpio1V(false);
            Thread.sleep(500);
            HardwareApp.getInstance().open5V(true);
            Thread.sleep(500);
            HardwareApp.getInstance().open9V(true);
            Thread.sleep(500);
            HardwareApp.getInstance().openPB5(true);
            Thread.sleep(500);
            HardwareApp.getInstance().setGpio1V(true);
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean WriteFlash(String data) {
        try {
            int count = (data.length() % 400 == 0) ? data.length() / 400 : data.length() / 400 + 1;
            for (int i = 0; i < count; i++) {
                if (i == count - 1) {
                    P_Callback.Program_Progress(100);
                    if (!CheckData(data.substring(400 * i), Integer.toHexString(i + 1))) {
                        return false;
                    }
                    ;
                } else {
                    P_Callback.Program_Progress(i * 100 / count);
                    if (!CheckData(data.substring(400 * i, 400 * i + 400), Integer.toHexString(i + 1))) {
                        return false;
                    }
                    ;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean CheckData(String data, String place) {
        try {
            while (place.length() < 2) {
                place = "0" + place;
            }
            String Long = (data.length() == 400) ? "00CB" : "00" + Integer.toHexString(data.length() / 2 + 3);
            String command = "0A 13 LONG DATA PLACE FF F5".replace(" ", "").
                    replace("LONG", Long).replace("DATA", data).replace("PLACE", place);
            Send(command);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal = 0;
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 2 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    return false;
                }
                if (Rx.length() >= 36) {
                    return true;
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean ProgramCheck(String data) {
        try {
            Send("0A 14 00 0E 00 00 00 00 00 00 00 00 00 00 00 00 ff f5".replace(" ", ""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            int fal = 0;
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A")) || fal == 10) {
                    if (time > 15) {
                        ReOpen();
                    }
                    return false;
                }
                if (Rx.length() >= 36 && Rx.contains("F513000E00")) {
                    String check = Rx.substring(12, 20);
                    if (check.equals("7FFFFFFF") || check.equals("000007FF")) {
                        return true;
                    } else {
                        if (!RePr(getBit(check).substring(1), data)) {
                            return false;
                        }
                        ;
                        past = sdf.parse(sdf.format(new Date()));
                        fal++;
                    }
                }
                Thread.currentThread().sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean RePr(String b, String data) {
        b = (reverseBySort(b));
        Log.d("DATA::", "失敗" + b);
        int count = (data.length() % 400 == 0) ? data.length() / 400 : data.length() / 400 + 1;
        for (int i = 0; i < count; i++) {
            P_Callback.Program_Progress(i * 100 / count);
            if (!String.valueOf(b.charAt(i)).equals("1")) {
                if (i == count - 1) {
                    if (!CheckData(data.substring(400 * i), Integer.toHexString(i + 1))) {
                        return false;
                    }
                    ;
                } else {
                    if (!CheckData(data.substring(400 * i, 400 * i + 400), Integer.toHexString(i + 1))) {
                        return false;
                    }
                    ;
                }
            }
        }
        Send("0A 14 00 0E 00 00 00 00 00 00 00 00 00 00 00 00 ff f5".replace(" ", ""));
        return true;
    }

    public static boolean reboot() {
        try {
            String data = "0A0D00030000F5";
            Send(data);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 20 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 20) {
                        ReOpen();
                    }
                    return false;
                }
                if (Rx.length() == 14) {
                    return true;
                }
                Thread.currentThread().sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void GetVerion(Version_C caller) {
        try {
            String data = "0A0A000EFFFFFFFFFFFFFFFFFFFFFFFF00F5";
            Send(data);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 15) {
                        ReOpen();
                    }
                    caller.version("", false);
                    return;
                }
                if (Rx.length() >= 36) {
                    caller.version(Rx.substring(8, 16), true);
                    return;
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void WriteBootloader(BleActivity act, int Ind, String filename, Update_C caller) {
        try {
//            FileInputStream fo=new FileInputStream(context.getApplicationContext().getFilesDir().getPath()+"/"+filename+".s2");
            InputStreamReader fr = new InputStreamReader((filename.equals("no")) ? act.getAssets().open("update.x2") : new FileInputStream(act.getApplicationContext().getFilesDir().getPath() + "/update.x2"));
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String s = br.readLine();
                s = s.replace("null", "");
                sb.append(s);
            }
            int Long = 0;
            if (sb.length() % Ind == 0) {
                Long = sb.length() / Ind;
            } else {
                Long = sb.length() / Ind + 1;
            }
            Log.d("總行數", "" + Long);
            for (int i = 0; i < Long; i++) {
                if (i == Long - 1) {
                    Log.d("行數", "" + i);
                    String data = bytesToHex(sb.substring(i * Ind, sb.length()).getBytes());
                    int length = Ind + 2;
                    check(Convvvert(data, Integer.toHexString(length)));
                    caller.Updateing(100);
                    caller.Finish(true);
                } else {
                    String data = bytesToHex(sb.substring(i * Ind, i * Ind + Ind).getBytes());
                    Log.d("行數", "" + i);
                    int length = Ind + 2;
                    caller.Updateing(i * 100 / Long);
                    if (!check(Convvvert(data, Integer.toHexString(length)))) {
                        caller.Finish(false);
                    }
                }
            }
            fr.close();
            caller.Finish(true);
        } catch (Exception e) {
            e.printStackTrace();
            caller.Finish(false);
        }
    }

    public static String Convvvert(String data, String length) {
        String command = "0A02LX00F5";
        while (length.length() < 4) {
            length = "0" + length;
        }
        command = (command.replace("L", length).replace("X", data));
        return command;
    }

    public static boolean check(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal = 0;
            Send(data);
            while (fal < 5) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 2) {
                    past = sdf.parse(sdf.format(new Date()));
                    Send(data);
                    fal++;
                }
                if (Rx.length() >= 14 && Rx.equals(GetCrcString("F502000300F40A")) || Rx.equals(GetCrcString("F50B000301F70A"))) {
                    return true;
                }
                Thread.currentThread().sleep(100);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void GetHard() {
        try {
            String data = "0A0C000EFFFFFFFFFFFFFFFFFFFFFFFF00F5";
            Send(data);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 15) {
                        ReOpen();
                    }
                    return;
                }
                if (Rx.length() >= 14) {
//                    if(Rx.contains(GetCrcString("F500000302F40A"))){caller.result(2);}
//                    if(Rx.contains(GetCrcString("F500000301F40A"))){caller.result(1);}
                    return;
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
//        caller.result(-1);
        }
    }

    public static void HandShake(Hanshake_C caller) {
        try {
            String data = "0A0000030000F5";
            Send(data);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if (time > 15) {
                        ReOpen();
                    }
                    caller.result(-1);
                    return;
                }
                if (Rx.length() >= 14) {
                    if (Rx.contains(GetCrcString("F500000302F40A"))) {
                        caller.result(2);
                    }
                    if (Rx.contains(GetCrcString("F500000301F40A"))) {
                        caller.result(1);
                    }
                    if (Rx.contains(GetCrcString("F501000300F70A"))) {
                        caller.result(1);
                    }
                    return;
                }
                Thread.currentThread().sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            caller.result(-1);
        }
    }

    public static void IdCopy(Copy_C caller, String hex) {
        try {
            PublicBean.CopySuccess = new ArrayList<Boolean>();
            PublicBean.CopySuccess.add(false);
            PublicBean.CopySuccess.add(false);
            PublicBean.CopySuccess.add(false);
            PublicBean.CopySuccess.add(false);
            while (hex.length() < 2) {
                hex = "0" + hex;
            }
            for (int i = 0; i < PublicBean.SensorList.size(); i++) {
                int Original_Long = PublicBean.SensorList.get(i).length();
                int New_Long = PublicBean.NewSensorList.get(i).length();
                String Original_ID = PublicBean.SensorList.get(i);
                while (Original_ID.length() < 8) {
                    Original_ID = "0" + Original_ID;
                }
                String New_ID = PublicBean.NewSensorList.get(i);
                while (New_ID.length() < 8) {
                    New_ID = "0" + New_ID;
                }
                String data = "0A 11 00 0E Original_ID Original_Long New_ID New_Long hex 00 ff f5".replace(" ", "").replace("Original_Long", "0" + Original_Long)
                        .replace("New_Long", "0" + New_Long).replace("Original_ID", Original_ID).replace("New_ID", New_ID).replace("hex", hex);
                Log.e("DATA:", "Prepare:" + data);
                Send(data);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                int fal = 0;
                Date past = sdf.parse(sdf.format(new Date()));
                while (true) {
                    Date now = sdf.parse(sdf.format(new Date()));
                    double time = getDatePoor(now, past);
                    if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                        if (time > 15) {
                            ReOpen();
                            caller.Copy_Finish(false);
                            return;
                        }
                        if (SendTag.equals(NowTag)) {
                            caller.Copy_Next(false, i);
                        }
                        break;
                    }
                    if (Rx.length() >= 36) {
                        int idcount=Integer.parseInt(Rx.substring(17,18));
                        if (Rx.contains(PublicBean.SensorList.get(i).substring(8-idcount))) {
                            if (SendTag.equals(NowTag)) {
                                PublicBean.CopySuccess.set(i, true);
                                caller.Copy_Next(true, i);
                            }
                        } else {
                            if (SendTag.equals(NowTag)) {
                                PublicBean.CopySuccess.set(i, false);
                                caller.Copy_Next(false, i);
                            }
                        }
                        break;
                    }
                    Thread.currentThread().sleep(100);
                }
                Thread.currentThread().sleep(1000);
            }
            if (SendTag.equals(NowTag)) {
                caller.Copy_Finish(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            caller.Copy_Finish(false);
        }
    }

    public static String reverseBySort(String str) {
        if (str == null || str.length() == 1) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = str.length() - 1; i >= 0; i--) {
            sb.append(str.charAt(i));//使用StringBuffer從右往左拼接字元
        }
        return sb.toString();
    }

    public static double getDatePoor(Date endDate, Date nowDate) {
        long diff = endDate.getTime() - nowDate.getTime();
        long sec = diff / 1000;
        return (SendTag.equals(NowTag)) ? sec : 16;
    }
}
