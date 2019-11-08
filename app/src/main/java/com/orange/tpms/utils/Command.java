package com.orange.tpms.utils;

import com.orange.tpms.lib.hardware.HardwareApp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Command {
    public  static String Rx="";
    public static void Send(String a){
        Rx="";
        byte [] data=GetCrc(a);
        HardwareApp.send(data);
    }
    public static String GetCrcString(String a){
        byte command[]=StringHexToByte(a);
        int xor=0;
        for (int i=0;i<command.length-1;i++){
            xor=xor + command[i];
        }
        command[command.length-1]=(byte)xor;
        return bytesToHex(command);
    }
    public static byte[] GetCrc(String a){
        byte command[]=StringHexToByte(a);
        int xor=0;
        for (int i=0;i<command.length-1;i++){
            xor=xor + command[i];
        }
        command[command.length-1]=(byte)xor;
        return command;
    }
    public static byte[] StringHexToByte(CharSequence cs){
        byte[] bytes = new byte[cs.length()/2];
        for (int i=0;i<(cs.length()/2);i++)
            bytes[i] = (byte) Integer.parseInt(cs.toString().substring(2*i,2*i+2),16);
        return bytes;
    }
    public static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
    public static String GetId(String hex){
        try {
            Send("0A10000E010000HEX000000000000000039F5 ".replace("HEX",hex));
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past=sdf.parse(sdf.format(new Date()));
            while(true){
                Date now=sdf.parse(sdf.format(new Date()));
                double time=getDatePoor(now,past);
                if(time>10||Rx.equals(GetCrcString("F51C000301000A"))||Rx.equals(GetCrcString("F51C000302000A"))){return "false";}
                if(Rx.length()==36){
                    int idcount=Integer.parseInt(Rx.substring(17,18));

                    return Rx.substring(16-idcount,16);
                }
            }
        }catch (Exception e){e.printStackTrace();
        return  "false";}
    }
    public  static double getDatePoor(Date endDate, Date nowDate) {
        long diff = endDate.getTime() - nowDate.getTime();
        long sec = diff/1000;
        return sec;
    }
}
