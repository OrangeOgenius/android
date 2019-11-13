package com.orange.tpms.utils;

import android.app.Activity;
import com.orange.tpms.bean.SensorData;
import com.orange.tpms.lib.hardware.HardwareApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static String getBit(byte by){
        StringBuffer sb = new StringBuffer();
        sb.append((by>>7)&0x1);
        sb.append((by>>6)&0x1);
        sb.append((by>>5)&0x1);
        sb.append((by>>4)&0x1);
        sb.append((by>>3)&0x1);
        sb.append((by>>2)&0x1);
        sb.append((by>>1)&0x1);
        sb.append((by>>0)&0x1);
        return sb.toString();
    }
    public static int byte2ToINT(byte[] bytes) {
        int high = bytes[0];
        int low = bytes[1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
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
                if (time > 10 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    data.success = false;
                    return data;
                }
                if (Rx.length() == 36) {
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.id = Rx.substring(16 - idcount, 16);
                    data.bat = getBit(StringHexToByte(Rx.substring(28,30))[0]).substring(3,4);
                    data.kpa=byte2ToINT(StringHexToByte(Rx.substring(22,26)));
                    byte[] bytes=StringHexToByte(Rx.substring(18 , 22));
                    data.c=bytes[1]-bytes[0];
                    data.vol=22+(StringHexToByte(Rx.substring(26 , 28))[0]&0x0F);
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
public static boolean ProgramFirst(String Lf, String Hex, String count, String s19, Activity activity){
        try{
            String spilt="";
            while(count.length()<2){count="0"+count;}
            while(Hex.length()<2){Hex="0"+Hex;}
//            InputStream is =new InputStream(new FileInputStream("s19"));
            FileInputStream fo=new FileInputStream(activity.getApplicationContext().getFilesDir().getPath()+"/"+s19+".s19");
            InputStreamReader fr = new InputStreamReader(fo);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String s=br.readLine();
                if(s!=null&&!s.equals("null")){sb.append(s);}
            }
            String B8=sb.toString().substring(16,18);
            String B9=sb.toString().substring(18,20);
            String B12=sb.toString().substring(24,26);
            String B13=sb.toString().substring(26,28);
            Send("0A 10 00 0E  02 CT  Lf Hex 8b 9b 12b 13b 00 00 00 00 ff f5".replace("CT",count).replace("Lf",Lf).replace("Hex",Hex)
                    .replace("8b",B8).replace("9b",B9).replace("12b",B12).replace("13b",B13).replace(" ",""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 10 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    return false;
                }
                if(Rx.length()==36){
                    spilt=(Rx.substring(10, 12).equals("04")) ? sb.substring(0, 2048*2) : sb.substring(0, 6144*2);
                    return WriteFlash(spilt);
                }
                Thread.currentThread().sleep(100);
            }
        }catch (Exception e){e.printStackTrace(); return false;}

}
public static boolean WriteFlash(String data){
        try{
            int count=(data.length()%400==0) ? data.length()/400 : data.length()/400+1;
            for(int i=0;i<count;i++){
                if(i==count-1){
                   if(!CheckData(data.substring(400*i),Integer.toHexString(i))){return false;} ;
                }else{
                    if(!CheckData(data.substring(400*i,400*i+400),Integer.toHexString(i))){return false;};
                }
            }
            return true;
        }catch (Exception e){e.printStackTrace();
        return false;}

}
public static boolean CheckData(String data,String place){
        try{
            String Long=(data.length()==400) ? "00CB":"00"+Integer.toHexString(data.length()/2+1);
            String command="0A 13 LONG DATA PLACE FF F5".replace(" ", "").
                    replace("LONG", Long).replace("DATA",data).replace("PLACE", place);
            Send(command);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while(true){
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 10 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    return false;
                }
                if(Rx.length()==36){return true;}
                Thread.currentThread().sleep(100);
            }
        }catch (Exception e){e.printStackTrace();return false;}
}
public static boolean ProgramCheck(){
        try{
            Send("0A 14 00 0E 00 00 00 00 00 00 00 00 00 00 00 00 ff f5".replace(" ", ""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while(true){
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 10 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    return false;
                }
                if(Rx.length()==36){
                    String check=Rx.substring(12, 20);
                    if(check.equals("7FFFFFFF")||check.equals("000007FF")){return true;}else{

                    }
                }
            }
        }catch (Exception e){e.printStackTrace();return false;}
}
    public static double getDatePoor(Date endDate, Date nowDate) {
        long diff = endDate.getTime() - nowDate.getTime();
        long sec = diff / 1000;
        return sec;
    }
}
