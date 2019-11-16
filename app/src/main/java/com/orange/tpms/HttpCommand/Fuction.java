package com.orange.tpms.HttpCommand;

import android.util.Log;
import com.orange.tpms.Callback.Register_C;
import com.orange.tpms.Callback.Reset_C;
import com.orange.tpms.Callback.Sign_In_C;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.support.constraint.Constraints.TAG;

public class Fuction {
    public static final int timeout=10000;
    public static final String wsdl = "http://bento2.orange-electronic.com/App_Asmx/ToolApp.asmx";
    private static RetNode _req(String url_String, String data, int timeout) {
        try{ Log.d(TAG + "_post", "url: " + url_String);
            Log.d(TAG + "_post", "data: " + data);
            URL url = new URL(url_String);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            conn.setRequestProperty("Content-Length", ""+data.getBytes().length);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.write(data.getBytes(StandardCharsets.UTF_8));
            dos.flush();
            RetNode retNode = new RetNode();
            retNode.status=conn.getResponseCode();
            retNode.data = "";
            if(retNode.status==200){
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line = null;
                StringBuffer strBuf = new StringBuffer();
                while ((line = reader.readLine()) != null) {
                    strBuf.append(line);
                }
                retNode.data=strBuf.toString();
                reader.close();
            }
            dos.close();
            Log.d(TAG+"_post", "-------------respond data--------------");
            Log.d(TAG+"_post", "Data:"+retNode.data);
            Log.d(TAG+"_post", "---------------------------------------");
            Log.d(TAG+"_post", "status: "+retNode.status);
            Log.d(TAG+"_post", "-------------data end--------------");
            return retNode;
        }catch (Exception e){
            RetNode retNode = new RetNode();
            retNode.status = -1;
            retNode.data = "";
            Log.d("_post",e.getMessage());
            return retNode;
        }
    }
    public static void ResetPassword(String admin, Reset_C caller){
        try{
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    " <soap12:Body>\n" +
                    " <SysResetPwd xmlns=\"http://tempuri.org/\">\n" +
                    " <UserID>"+admin+"</UserID>\n" +
                    " </SysResetPwd>\n" +
                    " </soap12:Body>\n" +
                    "</soap12:Envelope>");
            caller.Result(_req(wsdl,sb.toString(),timeout).status==200);
        }catch(Exception e){e.printStackTrace();caller.Result(false);}
    }
    public static void ValidateUser(String admin, String password, Sign_In_C caller){
        try{
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    " <soap12:Body>\n" +
                    " <ValidateUser xmlns=\"http://tempuri.org/\">\n" +
                    " <UserID>"+admin+"</UserID>\n" +
                    " <Pwd>"+password+"</Pwd>\n" +
                    " </ValidateUser>\n" +
                    " </soap12:Body>\n" +
                    "</soap12:Envelope>");
            RetNode respnse=_req(wsdl,sb.toString(),timeout);
            caller.result(respnse.data.substring(respnse.data.indexOf("<ValidateUserResult>") + 20, respnse.data.indexOf("</ValidateUserResult>")).equals("true")); ;
        }catch(Exception e){e.printStackTrace();caller.wifierror();}
    }
    public static void Register(String admin, String password, String SerialNum, String storetype, String companyname, String firstname, String lastname, String phone, String State, String city, String streat,
                               String zp, Register_C caller){
        try{
            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                    " <soap12:Body>\n" +
                    " <Register xmlns=\"http://tempuri.org/\">\n" +
                    " <Reg_UserInfo>\n" +
                    " <UserID>"+admin+"</UserID>\n" +
                    " <UserPwd>"+password+"</UserPwd>\n" +
                    " </Reg_UserInfo>\n" +
                    " <Reg_StoreInfo>\n" +
                    " <StoreType>"+storetype+"</StoreType>\n" +
                    " <CompName>"+companyname+"</CompName>\n" +
                    " <FirstName>"+firstname+"</FirstName>\n" +
                    " <LastName>"+lastname+"</LastName>\n" +
                    " <Contact_Tel>"+phone+"</Contact_Tel>\n" +
                    " <Continent>"+State+"</Continent>\n" +
                    " <Country>"+State+"</Country>\n" +
                    " <State>"+city+"</State>\n" +
                    " <City>"+city+"</City>\n" +
                    " <Street>"+streat+"</Street>\n" +
                    " <CompTel>"+companyname+"</CompTel>\n" +
                    " </Reg_StoreInfo>\n" +
                    " <Reg_DeviceInfo>\n" +
                    " <SerialNum>"+SerialNum+"</SerialNum>\n" +
                    " <DeviceType>USBPad</DeviceType>\n" +
                    " <ModelNum>PA001</ModelNum>\n" +
                    " <AreaNo>"+zp+"</AreaNo>\n" +
                    " <Firmware_1_Copy>EU-1.0</Firmware_1_Copy>\n" +
                    " <Firmware_2_Copy>EU-1.0</Firmware_2_Copy>\n" +
                    " <Firmware_3_Copy>EU-1.0</Firmware_3_Copy>\n" +
                    " <DB_Copy>EU-1.0 </DB_Copy>\n" +
                    " <MacAddress>00</MacAddress>\n" +
                    " <IpAddress>00</IpAddress>\n" +
                    " </Reg_DeviceInfo>\n" +
                    " </Register>\n" +
                    " </soap12:Body>\n" +
                    "</soap12:Envelope>");
            RetNode respnse=_req(wsdl,sb.toString(),timeout);
            if(respnse.status!=200){caller.WifiError();}
            if(respnse.data.substring(respnse.data.indexOf("<RegisterResult>")+16,respnse.data.indexOf("</RegisterResult>")).equals("true")){
                caller.Result(true);
            }else{
                caller.Result(false);
            }
        }catch(Exception e){e.printStackTrace();caller.WifiError();}
    }
    public static void Upload_ProgramRecord(String make, String model, String year, String startime, String Endtime, String SreialNum, String Devicetype, String Mode, int SensorCount, String position
            , ArrayList<SensorRecord> idrecord){try{
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                " <soap12:Body>\n" +
                " <Upload_VersionUpdateRecord xmlns=\"http://tempuri.org/\">\n" +
                " <SerialNum>"+SreialNum+"</SerialNum>\n" +
                " <data>\n" +
                " <Device_BurnVersionUpdate>\n" +
                " <DeviceInfo>\n" +
                " <enum_DeviceType>"+Devicetype+"</enum_DeviceType>\n" +
                " <SerialNum>"+SreialNum+"</SerialNum>\n" +
                " <enum_SensorMode>"+Mode+"</enum_SensorMode>\n" +
                " <DateTime_Start>"+startime+"</DateTime_Start>\n" +
                " <DateTime_End>"+Endtime+"</DateTime_End>\n" +
                " <SensorCount>"+SensorCount+"</SensorCount>\n" +
                " <enum_BurnPosition>"+position+"</enum_BurnPosition>\n" +
                " </DeviceInfo>\n" +
                " <CarInfo>\n" +
                " <Type>"+make+"</Type>\n" +
                " <Model>"+model+"</Model>\n" +
                " <Year>"+year+"</Year>\n" +
                " <CarNum>nodata</CarNum>\n" +
                " </CarInfo>\n" +
                " <TireInfo>\n" +
                " <TireBrand>nodata</TireBrand>\n" +
                " <TireModel>nodata</TireModel>\n" +
                " <TireProcDate>nodata</TireProcDate>\n" +
                " </TireInfo>\n" +
                " <ConsumerInfo>\n" +
                " <Name>nodata</Name>\n" +
                " <Age>0</Age>\n" +
                " <Sex>男</Sex>\n" +
                " <Tel>nodata</Tel>\n" +
                " <Email>nodata</Email>\n" +
                " <Continent>nodata</Continent>\n" +
                " <Country>nodata</Country>\n" +
                " <State>nodata</State>\n" +
                " <City>nodata</City>\n" +
                " <Street>nodata</Street>\n" +
                " </ConsumerInfo>\n" +
                " <Record>\n");
        for(SensorRecord record:idrecord){
            sb.append("<VersionUpdate_Record>\n"
                    +" <SensorID>"+record.SensorID+"</SensorID>\n"
                    +"<IsSuccess>"+record.IsSuccess+"</IsSuccess>\n"
                    +"<ModelNo >"+record.ModelNo+"</ModelNo >\n"
                    +"<enum_BurnResult>"+record.enum_BurnResult+"</enum_BurnResult>\n"
                    +"<DB_Version>"+SensorRecord.DB_Version+"</DB_Version>\n"
                    +"<SensorCode_Version>"+SensorRecord.SensorCode_Version+"</SensorCode_Version>\n"
                    +"</VersionUpdate_Record>\n" );
        }
        sb.append(
                " </Record>\n" +
                        " </Device_BurnVersionUpdate>\n" +
                        " </data>\n" +
                        " </Upload_VersionUpdateRecord>\n" +
                        " </soap12:Body>\n" +
                        "</soap12:Envelope>");
        RetNode respnse=_req(wsdl,sb.toString(),timeout);
    }catch(Exception e){ Log.d("upload",e.getMessage());}}
    public static void Upload_IDCopyRecord(String make,String model,String year,String startime,String Endtime,String SreialNum,String Devicetype,String Mode,int SensorCount,String position
            ,ArrayList<SensorRecord> idrecord){try{
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
                " <soap12:Body>\n" +
                " <Upload_IDCopyRecord xmlns=\"http://tempuri.org/\">\n" +
                " <SerialNum>"+SreialNum+"</SerialNum>\n" +
                " <data>\n" +
                " <Device_BurnIDCopy>\n" +
                " <DeviceInfo>\n" +
                " <enum_DeviceType>"+Devicetype+"</enum_DeviceType>\n" +
                " <SerialNum>"+SreialNum+"</SerialNum>\n" +
                " <enum_SensorMode>"+Mode+"</enum_SensorMode>\n" +
                " <DateTime_Start>"+startime+"</DateTime_Start>\n" +
                " <DateTime_End>"+Endtime+"</DateTime_End>\n" +
                " <SensorCount>"+SensorCount+"</SensorCount>\n" +
                " <enum_BurnPosition>"+position+"</enum_BurnPosition>\n" +
                " </DeviceInfo>\n" +
                " <CarInfo>\n" +
                " <Type>"+make+"</Type>\n" +
                " <Model>"+model+"</Model>\n" +
                " <Year>"+year+"</Year>\n" +
                " <CarNum>nodata</CarNum>\n" +
                " </CarInfo>\n" +
                " <TireInfo>\n" +
                " <TireBrand>nodata</TireBrand>\n" +
                " <TireModel>nodata</TireModel>\n" +
                " <TireProcDate>nodata</TireProcDate>\n" +
                " </TireInfo>\n" +
                " <ConsumerInfo>\n" +
                " <Name>nodata</Name>\n" +
                " <Age>0</Age>\n" +
                " <Sex>男</Sex>\n" +
                " <Tel>nodata</Tel>\n" +
                " <Email>nodata</Email>\n" +
                " <Continent>nodata</Continent>\n" +
                " <Country>nodata</Country>\n" +
                " <State>nodata</State>\n" +
                " <City>nodata</City>\n" +
                " <Street>nodata</Street>\n" +
                " </ConsumerInfo>\n" +
                " <Record>\n");
        for(SensorRecord record:idrecord){
            sb.append("<IDCopy_Record>\n"
                    +" <SensorID>"+record.SensorID+"</SensorID>\n"
                    +" <Car_SensorID>"+record.Car_SensorID+"</Car_SensorID>\n"
                    +"<IsSuccess>"+record.IsSuccess+"</IsSuccess>\n"
                    +"<ModelNo >"+record.ModelNo+"</ModelNo >\n"
                    +"<enum_BurnResult>"+record.enum_BurnResult+"</enum_BurnResult>\n"
                    +"</IDCopy_Record>\n" );
        }
        sb.append(
                " </Record>\n" +
                        " </Device_BurnIDCopy>\n" +
                        " </data>\n" +
                        " </Upload_IDCopyRecord>\n" +
                        " </soap12:Body>\n" +
                        "</soap12:Envelope>");
        RetNode respnse=_req(wsdl,sb.toString(),timeout);
        Log.d("upload",respnse.data.toString());
    }catch(Exception e){ Log.d("upload",e.getMessage());}}
//    public static boolean AddIfNotValid(String serialnum){
//        try{
//            return Fuction.Register(LogoActivity.Companion.getAdmin(),LogoActivity.Companion.getPassword(),serialnum,"Distributor","spare","spare","spare","spare","spare","spare","spare","")==0;
//        }catch (Exception e){e.printStackTrace();return false;}
//    }

}
