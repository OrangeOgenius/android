package com.orange.tpms.utils;

import android.app.Activity;
import android.util.Log;
import com.orange.tpms.Callback.Copy_C;
import com.orange.tpms.Callback.Program_C;
import com.orange.tpms.bean.PublicBean;
import com.orange.tpms.bean.SensorData;
import com.orange.tpms.lib.hardware.HardwareApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Command {
    public static String Rx = "";
    public static String NowTag = "";
    public static String SendTag="";
    public static void Send(String a) {
        Rx = "";
        SendTag=NowTag;
        byte[] data = GetCrc(a.toUpperCase());
        Log.d("DATA:","TX:"+bytesToHex(data));
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
    public static String getBit(String a){
        byte data[]=StringHexToByte(a);
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<data.length;i++){
            sb.append((data[i]>>7)&0x1);
            sb.append((data[i]>>6)&0x1);
            sb.append((data[i]>>5)&0x1);
            sb.append((data[i]>>4)&0x1);
            sb.append((data[i]>>3)&0x1);
            sb.append((data[i]>>2)&0x1);
            sb.append((data[i]>>1)&0x1);
            sb.append((data[i]>>0)&0x1);
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
    public static ArrayList<SensorData> GetPrId(String hex, String Lf) {
        ArrayList<SensorData> array=new ArrayList<>();

        try {
            Send("0A 10 000E 01 02 LF HEX 00 00 00 00 00 00 00 00 39 F5".replace("HEX", hex).replace(" ", "").replace("LF", Lf));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if(time > 15){ReOpen();}
                    return array;
                }
                if (Rx.length() >= 36) {
                    SensorData data = new SensorData();
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.id = Rx.substring(8, 16);
                    data.idcount=idcount;
                    data.bat = getBit(StringHexToByte(Rx.substring(28,30))[0]).substring(3,4);
                    data.kpa=byte2ToINT(StringHexToByte(Rx.substring(22,26)));
                    byte[] bytes=StringHexToByte(Rx.substring(18 , 22));
                    data.c=bytes[1]-bytes[0];
                    data.vol=22+(StringHexToByte(Rx.substring(26 , 28))[0]&0x0F);
                    data.success = true;
                    array.add(data);
                    if(array.size()==PublicBean.ProgramNumber){
                        return array;
                    }else{
                        if(Rx.length()>36){Rx=Rx.substring(36);}else{Rx="";}
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
                    if(time > 15){ReOpen();}
                    return data;
                }
                if (Rx.length() >= 36) {
                    int idcount = Integer.parseInt(Rx.substring(17, 18));
                    data.idcount=idcount;
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
    public static Program_C P_Callback;
    public static void Program(String Lf, String Hex, String count, String s19, Activity activity, Program_C caller){
        try{
            P_Callback=caller;
            FileInputStream fo=new FileInputStream(activity.getApplicationContext().getFilesDir().getPath()+"/"+s19+".s19");
            InputStreamReader fr = new InputStreamReader(fo);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String s=br.readLine();
                if(s!=null&&!s.equals("null")){sb.append(s);}
            }
            if(ProgramFirst( Lf,  Hex,  count,sb.toString())){
                caller.Program_Finish(ProgramCheck(spilt));
            }else{caller.Program_Finish(false);}
        }catch (Exception e){e.printStackTrace();caller.Program_Finish(false);}
    }
    static String spilt;
public static boolean ProgramFirst(String Lf, String Hex, String count, String data){
        try{

            while(count.length()<2){count="0"+count;}
            while(Hex.length()<2){Hex="0"+Hex;}
            String B8=data.substring(14,16);
            String B9=data.substring(16,18);
            String B12=data.substring(22,24);
            String B13=data.substring(24,26);
            Send("0A 10 00 0E  02 CT  Lf Hex 8b 9b 12b 13b 00 00 00 00 ff f5".replace("CT",count).replace("Lf",Lf).replace("Hex",Hex)
                    .replace("8b",B8).replace("9b",B9).replace("12b",B12).replace("13b",B13).replace(" ",""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if(time > 15){ReOpen();}
                    return false;
                }
                if(Rx.length()>=36){
                    spilt=(Rx.substring(10, 12).equals("04")) ? data.substring(0, 2048*2) : data.substring(0, 6144*2);
                    return WriteFlash(spilt);
                }
                Thread.currentThread().sleep(100);
            }
        }catch (Exception e){e.printStackTrace(); return false;}

}
public static void ReOpen(){
    try{
        Log.e("DATA:","重新上電");
        HardwareApp.getInstance().open5V(false);
        Thread.sleep(500);
        HardwareApp.getInstance().open5V(false);
        Thread.sleep(500);
        HardwareApp.getInstance().open5V(false);
        Thread.sleep(500);
        HardwareApp.getInstance().open5V(true);
        Thread.sleep(500);
        HardwareApp.getInstance().open5V(true);
        Thread.sleep(500);
        HardwareApp.getInstance().open5V(true);
        Thread.sleep(500);
    }catch (Exception e){e.printStackTrace();}
}
public static boolean WriteFlash(String data){
        try{
            int count=(data.length()%400==0) ? data.length()/400 : data.length()/400+1;
            for(int i=0;i<count;i++){
                if(i==count-1){
                    P_Callback.Program_Progress(100);
                   if(!CheckData(data.substring(400*i),Integer.toHexString(i+1))){return false;} ;
                }else{
                    P_Callback.Program_Progress(i*100/count);
                    if(!CheckData(data.substring(400*i,400*i+400),Integer.toHexString(i+1))){return false;};
                }
            }
            return true;
        }catch (Exception e){e.printStackTrace();
        return false;}
}
public static boolean CheckData(String data,String place){
        try{
            while(place.length()<2){place="0"+place;}
            String Long=(data.length()==400) ? "00CB":"00"+Integer.toHexString(data.length()/2+3);
            String command="0A 13 LONG DATA PLACE FF F5".replace(" ", "").
                    replace("LONG", Long).replace("DATA",data).replace("PLACE", place);
            Send(command);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal=0;
            while(true){
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 2 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    return false;
                }
                if(Rx.length()>=36){return true;}
                Thread.currentThread().sleep(100);
            }
        }catch (Exception e){e.printStackTrace();return false;}
}
public static boolean ProgramCheck(String data){
        try{
            Send("0A 14 00 0E 00 00 00 00 00 00 00 00 00 00 00 00 ff f5".replace(" ", ""));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            int fal=0;
            Date past = sdf.parse(sdf.format(new Date()));
            while(true){
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))||fal==10) {
                    if(time > 15){ReOpen();}
                    return false;
                }
                if(Rx.length()>=36&&Rx.contains("F513000E00")){
                    String check=Rx.substring(12, 20);
                    if(check.equals("7FFFFFFF")||check.equals("000007FF")){
                        Thread.currentThread().sleep(1000);
                        return true;}else{
                       if(!RePr(getBit(check).substring(1),data)){return false;} ;
                        past = sdf.parse(sdf.format(new Date()));
                        fal++;
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();return false;}
}
    public static boolean RePr(String b,String data){
        b= (reverseBySort(b));
        Log.d("DATA::","失敗"+b);
        int count=(data.length()%400==0) ? data.length()/400 : data.length()/400+1;
        for(int i=0;i<count;i++){
            P_Callback.Program_Progress(i*100/count);
            if(!String.valueOf(b.charAt(i)).equals("1")){
                if(i==count-1){
                    if(!CheckData(data.substring(400*i),Integer.toHexString(i+1))){return false;} ;
                }else{
                    if(!CheckData(data.substring(400*i,400*i+400),Integer.toHexString(i+1))){return false;};
                }
            }
        }
        Send("0A 14 00 0E 00 00 00 00 00 00 00 00 00 00 00 00 ff f5".replace(" ", ""));
        return true;
    }
//    0a 11 00 0e 12 34 56 78 08 34 dd c0 8b 08 00 00 bf f5
    public static void IdCopy(Copy_C caller){
    try{
        for(int i=0;i<PublicBean.SensorList.size();i++){
            int Original_Long=PublicBean.SensorList.get(i).length();
            int New_Long=PublicBean.NewSensorList.get(i).length();
            String Original_ID=PublicBean.SensorList.get(i);while(Original_ID.length()<8){Original_ID="0"+Original_ID;}
            String New_ID=PublicBean.NewSensorList.get(i) ; while(New_ID.length()<8){New_ID="0"+New_ID;}
            String data="0A 11 00 0E Original_ID Original_Long New_ID New_Long 00 00 ff f5".replace(" ", "").replace("Original_Long","0"+Original_Long)
                    .replace("New_Long","0"+New_Long).replace("Original_ID",Original_ID).replace("New_ID",New_ID);
            Log.e("DATA:","Prepare:"+data);
            Send(data);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            int fal=0;
            Date past = sdf.parse(sdf.format(new Date()));
            while(true){
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 15 || Rx.equals(GetCrcString("F51C000301000A")) || Rx.equals(GetCrcString("F51C000302000A"))) {
                    if(time > 15){ReOpen();return;}
                   if(SendTag.equals(NowTag)){caller.Copy_Next(false,i);}
                    break;
                }
                if(Rx.length()>=36){
                    if(Rx.contains(PublicBean.SensorList.get(i))){
                        if(SendTag.equals(NowTag)){caller.Copy_Next(true,i);}
                    }else{ if(SendTag.equals(NowTag)){caller.Copy_Next(false,i);} }
                    break;
                }
            }
        }
        if(SendTag.equals(NowTag)){caller.Copy_Finish();}
    }catch (Exception e){e.printStackTrace();caller.Copy_Finish();}
    }
    public static String reverseBySort(String str){
        if(str == null || str.length() == 1){
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = str.length() -1 ; i >= 0; i--) {
            sb.append(str.charAt(i));//使用StringBuffer從右往左拼接字元
        }
        return sb.toString();
    }
    public static double getDatePoor(Date endDate, Date nowDate) {
        long diff = endDate.getTime() - nowDate.getTime();
        long sec = diff / 1000;
        return (SendTag.equals(NowTag)) ? sec:16;
    }
}
