package com.orange.tpms.utils;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
public class FileDowload {
    public static boolean Internet=true;
    public static String ip=(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) ? "35.240.51.141:21":"61.221.15.194:21/OrangeTool";
    private static String encoding = System.getProperty("file.encoding");
    public static String username="orangerd";
    public static String password=(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) ? "orangetpms(~2":"orangetpms";
    public static boolean HaveData(Activity activity){
        try{
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            boolean success=true;
            if(profilePreferences.getString("mmyinit","no").equals("no")){
                Log.d("下載","下載mmy ok");
                if(!DownMMy(activity)){success=false;} ;
            }
            if(profilePreferences.getString("s19init","no").equals("no")){
                Log.d("下載","下載s19 ok");
                if(!DownAllS19(activity)){success=false;}
            }
            return success;
        }catch (Exception e){e.printStackTrace();return false;}
    }
    public static boolean DownMMy( Activity activity) {
        try {
            File DB_PATH = activity.getDatabasePath("usb_tx_mmy.db");
            File file=new File(DB_PATH.getPath().replace("usb_tx_mmy.db",""));
            if(!file.exists()){ if(!file.mkdirs()){return false;}
            }
            return    doloadmmy(DB_PATH.getPath(),activity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean DownS19(String Filename,Activity activity){
        return donloads19(Filename,activity);
    }
    public static boolean DownAllS19(Activity activity){
        try{
            URL url=new URL("http://bento2.orange-electronic.com/Orange%20Cloud/Database/SensorCode/SIII/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            StringBuffer strBuf = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
//                System.out.println(line);
            }
            boolean success=true;
            String[] arg=strBuf.toString().split(" HREF=\"");
            for(String a : arg){
                if(a.contains("SIII")&&a.contains("&lt;dir")){
                    if(!donloads19(a.substring(a.indexOf(">")+1,a.indexOf("<")),activity)){success=false;};
                }
            }
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            profilePreferences.edit().putString("s19init",success ? "yes" : "no").commit();
return success;
        }catch(Exception e){e.printStackTrace(); return false;}
    }
    public static String GetS19Name(String name){
        try{
            URL url=new URL("http://bento2.orange-electronic.com/Orange%20Cloud/Database/SensorCode/SIII/"+name+"/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            StringBuffer strBuf = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
            }
            String[] arg=strBuf.toString().split(" HREF=\"");
            for(String a : arg){
                if(a.contains(".s19")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }

    public static boolean donloads19(String name,Activity activity){
        if(Internet){
            try{
                String s19name=GetS19Name(name);
                SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
                if(profilePreferences.getString(name,"no").equals(s19name)){ return  true; }
                URL url=new URL("http://bento2.orange-electronic.com/Orange%20Cloud/Database/SensorCode/SIII/"+name+"/"+s19name);
                InputStream is=url.openStream();
                FileOutputStream fos=new FileOutputStream(activity.getApplicationContext().getFilesDir().getPath()+"/"+name+".s19");
                int bufferSize = 8192;
                byte[] buf = new byte[bufferSize];
                while(true){
                    int read=is.read(buf);
                    if(read==-1){  break;}
                    fos.write(buf, 0, read);
                }
                is.close();
                fos.close();
                profilePreferences.edit().putString(name,s19name).commit();
                return true;
            }catch (Exception e){e.printStackTrace(); return false;}
        }else{    try{
            InputStream is=activity.getAssets().open("SI2054.s19");
            FileOutputStream fos=new FileOutputStream(activity.getApplicationContext().getFilesDir().getPath()+"/"+name+".s19");
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            while(true){
                int read=is.read(buf);
                if(read==-1){  break;}
                fos.write(buf, 0, read);
            }
            is.close();
            fos.close();
            return true;
        }catch (Exception e){e.printStackTrace();return false;}}
    }
//    F5FE14000FD30300000000B30300000001D2780A
//    F5FE14000FC30300000000A30300000001FFAC0A
//    F5FE14000FD30300000000B30300000001D2780A



    public static boolean doloadmmy(String fileanme,Activity activity){
        if(Internet){  try{
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            String mmyname=mmyname();
            if(profilePreferences.getString("mmyname","").equals(mmyname)){return true;}
            URL url=new URL("http://bento2.orange-electronic.com/Orange%20Cloud/Database/MMY/EU/"+mmyname);
            Log.d("path","http://bento2.orange-electronic.com/Orange%20Cloud/Database/MMY/EU/"+mmyname);
            InputStream is=url.openStream();
            FileOutputStream fos=new FileOutputStream(fileanme);
            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            while(true){
                int read=is.read(buf);
                if(read==-1){  break;}
                fos.write(buf, 0, read);
            }
            is.close();
            fos.close();
            File f= new File(fileanme);

            if (f.exists() && f.isFile()){
                Log.d("path",""+f.length());
            }else{
                Log.d("path","file doesn't exist or is not a file");
            }
            profilePreferences.edit().putString("mmyname",mmyname).putString("mmyinit","yes").commit();
            return f.length() != 0;
        }catch (Exception e){e.printStackTrace(); return false;}}else{
            try{
                InputStream is=activity.getAssets().open("MMY_EU_list_V0.4_190910.db");
                FileOutputStream fos=new FileOutputStream(fileanme);
                int bufferSize = 8192;
                byte[] buf = new byte[bufferSize];
                while(true){
                    int read=is.read(buf);
                    if(read==-1){  break;}
                    fos.write(buf, 0, read);
                }
                is.close();
                fos.close();
                return true;
            }catch (Exception e){e.printStackTrace();return false;}

        }

    }

    public static String mmyname(){
        try{
            URL url=new URL("http://bento2.orange-electronic.com/Orange%20Cloud/Database/MMY/EU/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            StringBuffer strBuf = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
            }
            String[] arg=strBuf.toString().split("HREF=\"");
            for(String a : arg){
                if(a.contains(".db")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }
    public static String McuName(){
        try{
            URL url=new URL("http://bento2.orange-electronic.com/Orange%20Cloud/Drive/USB%20PAD/Firmware/MCU/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            StringBuffer strBuf = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
            }
            String[] arg=strBuf.toString().split(" HREF=\"");
            for(String a : arg){
                if(a.contains(".x2")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }

}