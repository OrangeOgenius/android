package com.orange.tpms.utils;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import com.orange.tpms.Callback.Donload_C;
import com.orange.tpms.Callback.Update_C;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDowload {
    public static boolean Internet=true;
    public static String ip=(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) ? "35.240.51.141:21":"61.221.15.194:21/OrangeTool";
    private static String encoding = System.getProperty("file.encoding");
    public static String username="orangerd";
    public static String password=(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) ? "orangetpms(~2":"orangetpms";
    public static void HaveData(Activity activity, Update_C caller){
        try{
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            boolean success=true;
            if(profilePreferences.getString("mmyinit","no").equals("no")){
                Log.d("下載","下載mmy ok");
                if(!DownMMy(activity)){success=false;} ;
            }
            if(profilePreferences.getString("s19init","no").equals("no")){
                Log.d("下載","下載s19 ok");
                if(!DownAllS19(activity,caller)){success=false;}
            }
            if(profilePreferences.getString("muc","no").equals("no")){
                Log.d("下載","下載muc ok");
                if(!DownMuc(activity)){success=false;}
            }
            if(profilePreferences.getString("obdinit","no").equals("no")){
                if(!DownAllObd(activity,caller)){success=false;Log.e("下載","下載Obd 失敗");}
            }
            caller.Finish(success);
        }catch (Exception e){e.printStackTrace();caller.Finish(false);}
    }
    public static void ChechUpdate(Activity activity, Update_C caller){
        try{
                if(!DownMMy(activity)){caller.Finish(false);return;} ;
                if(!DownAllS19(activity,caller)){caller.Finish(false);return;}
                if(!DownMuc(activity)){caller.Finish(false);return;}
                if(!Downloadapk(activity,caller)){caller.Finish(false);return;}
                if(!DownAllObd(activity,caller)){caller.Finish(false);return;}
                caller.Finish(true);
        }catch (Exception e){e.printStackTrace();caller.Finish(false);}
    }

    public static boolean DownMMy( Activity activity) {
        try {
            File DB_PATH = activity.getDatabasePath("usb_tx_mmy.db");
            File file=new File(DB_PATH.getPath().replace("usb_tx_mmy.db",""));
            if(!file.exists()){ if(!file.mkdirs()){return false;} }
            return  doloadmmy(DB_PATH.getPath(),activity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean DownAllObd(Activity activity,Update_C caller){
        try{
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OBD%20DONGLE/",10);
            if(response.equals("nodata")){return false;}
            boolean success=true;
            String[] arg=response.split("HREF=\"");
            for(int i=0;i<arg.length;i++){
                if(i !=1 && arg[i].contains("OBD%20DONGLE")){
                    Log.e("obd",arg[i].substring(arg[i].indexOf(">")+1,arg[i].indexOf("<")));
                    if(!DonloadObd(arg[i].substring(arg[i].indexOf(">")+1,arg[i].indexOf("<")),activity)){success=false;};
                }
                caller.Updateing(i*100/arg.length/3+(100/3)*2);
            }
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            profilePreferences.edit().putString("obdinit",success ? "yes" : "no").commit();
            return success;
        }catch(Exception e){e.printStackTrace(); return false;}
    }
    public static boolean DonloadObd(String name, Activity activity) {
        try {
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            String donloadobd = GetObdName(name);
            if(donloadobd.equals("nodata")){
                Log.e("obd失敗",name);
                return false;}
            if(donloadobd.equals(profilePreferences.getString("obd"+name,"nodata"))){return true;}
            boolean result=FileDonload(activity.getApplicationContext().getFilesDir().getPath() + "/" + name + ".srec","https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OBD%20DONGLE/" + name + "/" + donloadobd,30,progress -> {

            });
            if(!result){
                Log.e("obd失敗",name);
                return false;}
            profilePreferences.edit().putString("obd"+name,donloadobd).commit();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public static String GetObdName(String name) {
        try {
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OBD%20DONGLE/"+name,10);
            if(response.equals("nodata")){return response;}
            String[] arg = response.toString().split(" HREF=\"");
            for (String a : arg) {
                if (a.contains(".srec")) {
                    return (a.substring(a.indexOf(">") + 1, a.indexOf("<")));
                }
            }
            return "nodata";
        } catch (Exception e) {
            e.printStackTrace();
            return "nodata";
        }
    }
    public static boolean DownS19(String Filename,Activity activity){
        return donloads19(Filename,activity);
    }
    public static boolean DownAllS19(Activity activity,Update_C caller){
        try{
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Database/SensorCode/SIII/",10);
            if(response.equals("nodata")){return false;}
            boolean success=true;
            String[] arg=response.toString().split("HREF=\"");
            for(int i=0;i<arg.length;i++){
                if(i !=0 && arg[i].contains("SIII")){
                    if(!donloads19(arg[i].substring(arg[i].indexOf(">")+1,arg[i].indexOf("<")),activity)){success=false;};
                }
                caller.Updateing(i*100/arg.length/3);
            }
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            profilePreferences.edit().putString("s19init",success ? "yes" : "no").commit();
            return success;
        }catch(Exception e){e.printStackTrace(); return false;}
    }
    public static String GetS19Name(String name){
        try{
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Database/SensorCode/SIII/"+name+"/",10);
            if(response.equals("nodata")){return response;}
            String[] arg=response.toString().split(" HREF=\"");
            for(String a : arg){
                if(a.contains(".s19")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }
    public static String GetMucName(){
        try{
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OG/Firmware/",10);
          if(response.equals("nodata")){return response;}
            String[] arg=response.toString().split(" HREF=\"");
            for(String a : arg){
                if(a.contains(".x2")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }
    public static String GetApkName(){
        try{
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OG/APP%20Software/",10);
            if(response.equals("nodata")){return response;}
            String[] arg=response.split(" HREF=\"");
            for(String a : arg){
                if(a.contains(".apk")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }
    public static boolean DownMuc(Activity activity){
        try{
            String mcu=GetMucName();
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            boolean result=FileDonload(activity.getApplicationContext().getFilesDir().getPath()+"/update.x2","https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OG/Firmware/"+mcu,30,progress -> {

            });
            if(result){profilePreferences.edit().putString("mcu",mcu).commit();}
            return result;
        }catch (Exception e){e.printStackTrace();return false;}
    }
    public static boolean Downloadapk(Activity activity,Update_C caller){
        try{
            String apk=GetApkName();
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            if(profilePreferences.getString("apk",PackageUtils.getVersionCode(activity)+".apk").equals(apk)){ return  true; }
            profilePreferences.edit().putString("apk",PackageUtils.getVersionCode(activity)+"");
            boolean result=FileDonload("/sdcard/update/update.apk","https://bento2.orange-electronic.com/Orange%20Cloud/Drive/OG/APP%20Software/"+apk,1200,progress -> {
                caller.Updateing(progress/3+33);
            });
            if(result){profilePreferences.edit().putString("apk",apk).commit();}
            Log.e("apkdown","下載完成");
            return result;
        }catch (Exception e){e.printStackTrace();return false;}
    }
    public static boolean donloads19(String name,Activity activity){
            try{
                String s19name=GetS19Name(name);
                SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
                if(profilePreferences.getString(name,"no").equals(s19name)){ return  true; }
                boolean result=FileDonload("/sdcard/files19/"+name+".s19","https://bento2.orange-electronic.com/Orange%20Cloud/Database/SensorCode/SIII/"+name+"/"+s19name,30,progress -> {

                });
                if(result){profilePreferences.edit().putString(name,s19name).commit();}
                return result;
            }catch (Exception e){e.printStackTrace(); return false;}
    }
    public static boolean doloadmmy(String fileanme,Activity activity){
       try{
            SharedPreferences profilePreferences = activity.getSharedPreferences("Setting", Context.MODE_PRIVATE);
            String mmyname=mmyname();
            if(profilePreferences.getString("mmyname","").equals(mmyname)){return true;}
            boolean result=FileDonload(fileanme,"https://bento2.orange-electronic.com/Orange%20Cloud/Database/MMY/EU/"+mmyname,30, progress -> {

            });
            File f= new File(fileanme);
            if (f.exists() && f.isFile()){
                Log.d("path",""+f.length());
            }else{
                Log.d("path","file doesn't exist or is not a file");
            }
            if(result){profilePreferences.edit().putString("mmyname",mmyname).putString("mmyinit","yes").commit();}
            return result;
        }catch (Exception e){e.printStackTrace(); return false;}
    }

    public static String mmyname(){
        try{
            String response=GetText("https://bento2.orange-electronic.com/Orange%20Cloud/Database/MMY/EU/",10);
            if(response.equals("nodata")){return response;}
            String[] arg=response.toString().split("HREF=\"");
            for(String a : arg){
                if(a.contains(".db")){  return (a.substring(a.indexOf(">")+1,a.indexOf("<")));}
            }
        }catch(Exception e){e.printStackTrace();}
        return "nodata";
    }
public static boolean FileDonload(String path, String url, int timeout, Donload_C caller){
        try{
            Log.d("path",path);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(1000*timeout);
            InputStream is =conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(path);
//            double contentsize=conn.getContentLength();
            int bufferSize = 8192;
            double prread=0;
            byte[] buf = new byte[bufferSize];
            while (true) {
                int read = is.read(buf);
                prread+=read;
                if (read == -1) {
                    break;
                }
                fos.write(buf, 0, read);
                caller.Updateing((int)(prread*100/33766558));
            }
            is.close();
            fos.close();
            return true;
        }catch (Exception e){e.printStackTrace();
        Log.e("錯誤",e.getMessage());
        return false;}
}
    public static boolean FileDonload(String path,String url,int timeout,Update_C caller){
        try{
            Log.d("path",path);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(1000*timeout);
            InputStream is =conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(path);

            int bufferSize = 8192;
            byte[] buf = new byte[bufferSize];
            while (true) {
                int read = is.read(buf);
                if (read == -1) {
                    break;
                }
                fos.write(buf, 0, read);
            }
            is.close();
            fos.close();
            return true;
        }catch (Exception e){e.printStackTrace();
            Log.e("錯誤",e.getMessage());
            return false;}
    }
public static String GetText(String url,int timeout){
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setConnectTimeout(timeout*1000);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line = null;
            StringBuffer strBuf = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                strBuf.append(line);
            }
           return  strBuf.toString();
        }catch (Exception e){
            Log.e("錯誤",e.getMessage());
            e.printStackTrace();return "nodata";}
}
}