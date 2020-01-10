package com.orange.tpms.bean;

import java.util.ArrayList;
import java.util.List;

public class PublicBean {
    public static String SelectMake="";
    public static String SelectModel="";
    public static String SelectYear="";
    public static String WriteLf="";
    public static String WriteRf="";
    public static String WriteRr="";
    public static String WriteLr="";
    public static String SerialNum="";
    public static String OG_SerialNum="";
    public static String MCU_NUMBER="";
    public static String admin="";
    public static String password="";
    public static int position=1;
    public static int ProgramNumber=1;
    public final static int 檢查傳感器=1;
    public final static int 燒錄傳感器=2;
    public final static int 複製傳感器=3;
    public final static int 設定=4;
    public final static int 學碼步驟=5;
    public final static int 掃描Mmy=6;
    public final static int 掃描Sensor=7;
    public final static int PAD_COPY=8;
    public final static int PAD_PROGRAM=9;
    public final static int Go_Web=10;
    public final static int ID_COPY_OBD=11;
    public final static int OBD_RELEARM=12;
    public static int ScanType=4;
    public static List<String> SensorList=null;
    public static List<String> NewSensorList=null;
    public static ArrayList<String> CopySuccessID=new ArrayList<String>();
    public static List<Boolean> CopySuccess=null;
    public static boolean Update=false;
}
