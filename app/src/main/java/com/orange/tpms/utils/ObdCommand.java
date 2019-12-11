package com.orange.tpms.utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import com.orange.tpms.Callback.Obd_C;
import com.orange.tpms.R;
import com.orange.tpms.bean.ID_Beans;
import com.orange.tpms.ue.activity.KtActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.orange.blelibrary.blelibrary.tool.FormatConvert.StringHexToByte;

public class ObdCommand {
    public static String WRITE_SUCCESS = "F502000300F40A";
    public static String Program_Flash_Fail = "F502000302F60A";
    public static String VERIFY_FAIL = "F502000303F70A";
    public KtActivity act;
    public String AppVersion="";
    public  String donloads19 = "OBDB_APP_TO001_191030";
    //自動設定checkbyteF5020005000000F20A
    public String addcheckbyte(String com) {
        byte a[] = StringHexToByte(com);
        byte checkbyte = a[0];
        for (int i = 1; i < a.length - 2; i++) {
            checkbyte ^= a[i];
        }
        a[a.length - 2] = checkbyte;
        return bytesToHex(a);
    }

    //握手
    public boolean HandShake() {
        try {
            String a = "0A0000030000F5";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            act.getBleServiceControl().WriteCmd(addcheckbyte(a), 14);
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 3) {
                    return false;
                }
                if (act.getRXDATA().length() == 14) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.d("CommandError", e.getMessage());
            return false;
        }

    }

    public ID_Beans GetId(String count) {
        ID_Beans id = new ID_Beans();
        try {
            String a = "60BF0001" + count + "FF0A";
            act.getBleServiceControl().WriteCmd(GetXOR(a), 52);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal=0;
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 1) {
                    fal++;
                    past = sdf.parse(sdf.format(new Date()));
                    act.getBleServiceControl().WriteCmd(GetXOR(a), 52);
                }
                if(fal==5){  id.success= false;
                    return  id;}
                if (act.getRXDATA().length() == 52) {
                    id.success=true;
                    id.LF=act.getRXDATA().substring(8,16);
                    id.RF=act.getRXDATA().substring(16,24);
                    id.LR=act.getRXDATA().substring(24,32);
                    id.RR=act.getRXDATA().substring(32,40);
                    id.SP=act.getRXDATA().substring(40,48);
                    if(id.LF.equals("FFFFFFFF")&&id.RF.equals("FFFFFFFF")&&id.LR.equals("FFFFFFFF")&&id.RR.equals("FFFFFFFF")&&id.SP.equals("FFFFFFFF")){
                        id.success=false;
                    }
                    return  id;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  id;
        }
    }

    //Reboot
    public boolean Reboot() {
        try {
            String a = "0A0D00030000F5";
            act.getBleServiceControl().WriteCmd(addcheckbyte(a), 14);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 7) {
                    return false;
                }
                if (act.getRXDATA().equals("F501000300F70A")) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.d("CommandError", e.getMessage());
            return false;
        }
    }

    public boolean WriteVersion(){
        try{
            byte [] command=GetXOR("0ACA0015DDFFF5".replace("DD",bytesToHex(donloads19.replace(".srec","").getBytes())));
            act.getBleServiceControl().WriteCmd(command,14);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal = 0;
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 1) {
                    if(fal==1){return false;}
                    past = sdf.parse(sdf.format(new Date()));
                    act.getBleServiceControl().WriteCmd(command,14);
                    fal++;
                }
                if (act.getRXDATA().length()==14) {
                    Log.d("BLEDATA","寫入版本");
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean GoBootloader(){
        try{
            act.getBleServiceControl().WriteCmd(GetXOR("0ACD010100FFF5"),26);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal = 0;
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 10                                                                                                                                                                                        ) {
                    if(fal==1){return false;}
                    past = sdf.parse(sdf.format(new Date()));
                    act.getBleServiceControl().WriteCmd(GetXOR("0ACD010100FFF5"),26);
                    fal++;
                }
                if (act.getRXDATA().contains("F5CD010100CD0A01000300F70A")) {
                    Log.d("BLEDATA","進入燒錄");
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    // 燒寫&amp;驗證Flash
    public boolean WriteFlash(final Context context, final String FileName, final int Ind, final KtActivity act) {
        try {
            FileInputStream fo = new FileInputStream(context.getApplicationContext().getFilesDir().getPath() + "/" + FileName + ".srec");
            InputStreamReader fr = new InputStreamReader(fo);
            BufferedReader br = new BufferedReader(fr);
            StringBuilder sb = new StringBuilder();
            while (br.ready()) {
                String s = br.readLine();
                if(s!=null){ s = s.replace("null", "");
                    sb.append(s);}
            }
            int Long = 0;
            if (sb.length() % Ind == 0) {
                Long = sb.length() / Ind;
            } else {
                Long = sb.length() / Ind + 1;
            }
            Log.d("總行數", "" + Long);
            for (int i = 0; i < Long; i++) {
                int b = i;
                if (b >= 255) {
                    b = b - 255;
                }
                StringBuffer result = new StringBuffer(Integer.toHexString(b));
                while (result.length() < 2) {
                    result.insert(0, "0");
                }
                String cont = result.toString().toUpperCase();
                if (i == Long - 1) {
                    Log.d("write", "以跑完" + i);
                    String data = bytesToHex(sb.substring(i * Ind, sb.length()).getBytes());
                    int length = sb.substring(i * Ind, sb.length()).getBytes().length + 3;
                    act.getBleServiceControl().WriteCmd(Convvvert(data, Integer.toHexString(length), cont), 16);
                    act.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            act.ShowDaiLog(R.layout.normal_dialog,false,true, dialog -> {
                                TextView text=dialog.findViewById(R.id.tit);
                                text.setText(act.getResources().getString(R.string.Programming)+"..."+100+"%");
                            });

                        }
                    });
                    return true;
                } else {
                    String data = bytesToHex(sb.substring(i * Ind, i * Ind + Ind).getBytes());
                    Log.d("行數", "" + i);
                    int length = sb.substring(i * Ind, i * Ind + Ind).getBytes().length + 3;
                    if (!check(Convvvert(data, Integer.toHexString(length), cont))) {
                        return false;
                    }
                    final int finalI = i;
                    final int finalLong = Long;
                    act.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            act.ShowDaiLog(R.layout.normal_dialog,false,true, dialog -> {
                                TextView text=dialog.findViewById(R.id.tit);
                                text.setText(act.getResources().getString(R.string.Programming)+"..."+finalI*100/finalLong+"%");
                            });

                        }
                    });
                }
            }
            fr.close();
            return true;
        } catch (Exception e) {
            Log.d("CommandError", e.getMessage());
            return false;
        }
    }

    //設定tireid
    public void setTireId(final ArrayList<String> Id, Obd_C caller) {
        try{
            ArrayList<String> tmpsend = new ArrayList<>();
            tmpsend.add("60A200FFFFFFFFC20A");
            int i = 1;
            for (String id : Id) {
                id = AddEmpty(id);
                if (id != null) {
                    tmpsend.add(addcheckbyte("60A20XidFF0A".replace("id", id).replace("X", "" + i)));
                }
                i++;
            }
            tmpsend.add("60A2FFFFFFFFFF3D0A");
            for (String a : tmpsend) {
                    act.getBleServiceControl().WriteCmd(a, 18);
                    Thread.currentThread().sleep(50);
            }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                Date past = sdf.parse(sdf.format(new Date()));
                while (true) {
                    Date now = sdf.parse(sdf.format(new Date()));
                    double time = getDatePoor(now, past);
                    if (time > 10) {
                        caller.result_C(false);
                        break;
                    }
                    if (act.getRXDATA().equals("60B201FFFFFFFFD30A")) {
                        caller.result_C(true);
                        break;
                    }
                }
        }catch (Exception e){e.printStackTrace();caller.result_C(false);}

    }

    public static String AddEmpty(String a) {
        switch (a.length()) {
            case 6:
                return "00" + a;
            case 7:
                return "0" + a;
            case 8:
                return a;
        }
        return null;
    }

    private static String bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static String getDateTime() {

        SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm:ss:SSS");

        Date date = new Date();

        String strDate = sdFormat.format(date);

//System.out.println(strDate);

        return strDate;

    }

    public String Convvvert(String data, String length, String line) {
        String command = "0A02LHX00F5";
        while(length.length()<4){length="0"+length;}
        if (line.equals("F5")) {
            line = "00";
        }
        if (line.length() > 2) {
            line = "00";
        }
        command = addcheckbyte(command.replace("L", length).replace("X", data).replace("H", line));
        return command;
    }

    public boolean check(String data) {
        act.getBleServiceControl().WriteCmd(addcheckbyte(data), 16);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal = 0;
            while (fal < 20) {
//                Thread.currentThread().sleep(20);
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 1) {
                    past = sdf.parse(sdf.format(new Date()));
                    act.getBleServiceControl().WriteCmd(addcheckbyte(data), 16);
                    fal++;
                }
                if (act.getRXDATA().length() >= 16) {
//                    Thread.currentThread().sleep(100);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            Log.d("CommandError", e.getMessage());
            return false;
        }
    }
    public static double getDatePoor(Date endDate, Date nowDate) {
        long diff = endDate.getTime() - nowDate.getTime();
        long sec = diff / 1000;
        return sec;
    }

public  boolean AskVersion(){
        try{
            act.getBleServiceControl().WriteCmd(GetXOR("0ACF000100FFF5"),54);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date past = sdf.parse(sdf.format(new Date()));
            int fal = 0;
            while (true) {
                Date now = sdf.parse(sdf.format(new Date()));
                double time = getDatePoor(now, past);
                if (time > 2) {
                    return false;
                }
                if (act.getRXDATA().length()==54) {
                    AppVersion=(act.getRXDATA().substring(8,50));
                    Log.d("BLEDATA","版本號:"+AppVersion);
                    return true;
                }
            }
        }catch (Exception e){
e.printStackTrace();
return false;
        }
}
public boolean GoApp(){
    try{
        act.getBleServiceControl().WriteCmd(GetXOR("0ACD000100FFF5"),14);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        Date past = sdf.parse(sdf.format(new Date()));
        int fal = 0;
        while (true) {
            Date now = sdf.parse(sdf.format(new Date()));
            double time = getDatePoor(now, past);
            if(fal==3){return false;}
            if (time > 1) {
                past = sdf.parse(sdf.format(new Date()));
                act.getBleServiceControl().WriteCmd(GetXOR("0ACD000100FFF5"),14);
                fal++;
            }
            if (act.getRXDATA().length()==14) {
                Log.d("BLEDATA","進入app");
                return true;
            }
        }
    }catch (Exception e){
        e.printStackTrace();
        return false;
    }
}
    public static byte[] GetXOR(String a) {
        byte command[] = StringHexToByte(a);
        int xor = 0;
        for (int i = 0; i < command.length - 2; i++) {
            xor = xor ^ command[i];
        }
        command[command.length - 2] = (byte) xor;
        return command;
    }
}
