package com.orange.tpms.utils

import android.util.Log
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.HttpCommand.Fuction.AddIfNotValid
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.FormatConvert.getCRC16
import com.orange.tpms.utils.OgCommand.*
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.xor

public class BleCommand {
    var act= (JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager
    public var SensorModel = "nodata"
    public var AppVersion = "nodata"
    public var Lib = "nodata"
    var Appver = ""
    var AppverInspire = "nodata"
    var Boover = "101"
    var IC = 0
    var ID = ""
    var FALSE_CHANNEL = ArrayList<String>()
    var BLANK_CHANNEL = ArrayList<String>()
    var CHANNEL_BLE = ArrayList<String>()
    var writeid = ""
    fun Command12(ic: Int, channel: Int, id: String): Boolean {
        try {
            val check = 30
            val commeand = "0ASS120008CCIDXXXXF5".replace("SS", bytesToHex(byteArrayOf(ic.toByte())))
                .replace("CC", bytesToHex(byteArrayOf(channel.toByte()))).replace("ID", id)
            SendData(getCRC16(commeand), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 2) {
                    SendData(getCRC16(commeand), check)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal > 3) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    val g = checkcommand(act.BleHelper.RxData.substring(10, 12))
                    if (g) {
                        ID = act.BleHelper.RxData.substring(14, 22)
                    }
                    return g
                }
                Thread.sleep(100)
                
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }
//    F5FE10001FC0002B030108073ED96427E21E00A0002B030108073ED96427E21E014A080A
//    F5FE10001FD0002B030108073ED96427E21E00B0002B030108073ED96427E21E01E5FD0A
    fun Command03(): Boolean {
        try {
            val check = 22
            SendData(getCRC16("0AFE03000754504D539CC8F5"), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 1) {
                    SendData(getCRC16("0AFE03000754504D539CC8F5"), check)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal == 1) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    IC = StringHexToByte(act.BleHelper.RxData.substring(12, 14))[0].toInt() / 2
                    return true
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command_11(ic: Int, channel: Int): Boolean {
        try {
            val check = 30
            val commeand = "0ASS110004CCXXXXF5".replace("SS", bytesToHex(byteArrayOf(ic.toByte())))
                .replace("CC", bytesToHex(byteArrayOf(channel.toByte())))
            SendData(getCRC16(commeand), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 2) {
                    SendData(getCRC16(commeand), check)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal == 1) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    val g = checkcommand(act.BleHelper.RxData.substring(10, 12))
                    if (g) {
                        ID = act.BleHelper.RxData.substring(14, 22)
                    }
                    return g
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun checkcommand(a: String): Boolean {
        return getBit(StringHexToByte(a)[0]).substring(7, 8) == "0"
    }

    fun writesensorID(id: String): Boolean {
        try {
            SendData(getCRC16("0A0012000801IDXXXXF5".replace("ID", id)), 30)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                if (act.BleHelper.RxData.length == 30 && act.BleHelper.RxData.substring(14, 22).equals(id)) {
                    writeid = act.BleHelper.RxData.substring(14, 22)
                    break
                }
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 5) {
                    return false
                }
                Thread.sleep(100)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Setserial(): Boolean {
        try {
            SendData("0A0004000754504D535610F5", 32)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                if (act.BleHelper.RxData.contains("F50004000B")) {
                    val ser = "SP:" + act.BleHelper.RxData.substring(14, 26)
                    PublicBean.SerialNum = ser
                    AddIfNotValid(ser, "USBPad", JzActivity.getControlInstance().getRootActivity() as KtActivity)
                    break
                }
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 2) {
                    return false
                }
                Thread.sleep(100)
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    //    public  boolean mainflow(){
    //        try{
    //            SendData(StringHexToByte(getCRC16("0A0011000401XXXXF5")),30);
    //            return true;
    //        }catch (Exception e){e.printStackTrace();
    //            return false;}
    //    }
    //read sensor ID amd main flow (READ)
    fun ReadSensorId(): Boolean {
        try {
            SendData(getCRC16("0A0010000754504D53XXXXF5"), 42)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    //    public  boolean ReadSensorIdSingle(){
    //        try{
    //            SendData(StringHexToByte(getCRC16("0A0016000601C006XXXXF5")));
    //            return true;
    //        }catch (Exception e){e.printStackTrace();
    //            return false;}
    //    }
    //Clear APP端 sensor code
    fun ClearSensor(): Boolean {
        try {
            SendData(getCRC16("0A0014000D4F52414E474554504D53XXXXF5"), 34)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 1) {
                    SendData(getCRC16("0A0014000D4F52414E474554504D53XXXXF5"), 34)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal > 3) {
                    return false
                }
                if (act.BleHelper.RxData.length == 34) {
                    if (act.BleHelper.RxData.substring(4, 6).equals("14")) {
                        return true
                    }
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    //ProgramSensor
    fun ProgramSensor(filename: String, Lf: String): Boolean {
        try {
            SendData(getCRC16("0A00150008LF154504D53XXXXF5".replace("LF", Lf)), 30)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                if (act.BleHelper.RxData.length > 8 && act.BleHelper.RxData.substring(
                        act.BleHelper.RxData.length - 8,
                        act.BleHelper.RxData.length - 8 + 2
                    ).equals("3E")
                ) {
                    writeid = act.BleHelper.RxData.substring(14, 22)
                    return true
                }
                if (act.BleHelper.RxData.length > 12 && act.BleHelper.RxData.substring(
                        12,
                        14
                    ).equals("01") || act.BleHelper.RxData.length > 12 && act.BleHelper.RxData.substring(
                        12,
                        14
                    ).equals("02") || act.BleHelper.RxData.length > 12 && act.BleHelper.RxData.substring(
                        12,
                        14
                    ).equals("03")
                ) {
                    ClearSensor()
                    return false
                }
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 10) {
                    return false
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }



    fun AddCommand(data: String, Long: Int): String {
        val length = StringBuffer(Integer.toHexString(data.length / 2 + 5))
        while (length.length < 4) {         //CRC检验一般为4位，不足4位补0
            length.insert(0, "0")
        }
        val row = StringBuffer(Integer.toHexString(Long))
        while (row.length < 2) {         //CRC检验一般为4位，不足4位补0
            row.insert(0, "0")
        }
        val TmpCommand = "0A0013" + length.toString() + row + data + data.substring(18, 20) + "XXXXF5"
        return getCRC16(TmpCommand)
    }

    fun LogData(filename: String): Boolean {
        try {
            val fr = FileInputStream(filename)
            val br = BufferedReader(InputStreamReader(fr))
            val sb = StringBuilder()
            val ln = 512
            while (br.ready()) {
                val s = br.readLine()
                if (s != null && s != "null") {
                    sb.append(s)
                }
            }
            var Long = 0
            if (sb.length % ln == 0) {
                Long = sb.length / ln
            } else {
                Long = sb.length / ln + 1
            }
            for (i in 0 until Long) {
                if (i == Long - 1) {
                    val a = sb.toString().substring(i * ln, sb.length)
                    if (a.length >= 20) {
                        SendData(AddCommand(a, i), 34)
                    }
                } else {
                    SendData(AddCommand(sb.substring(i * ln, i * ln + ln), i), 34)
                }
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
                val past = sdf.parse(sdf.format(Date()))
                while (true) {
                    if (act.BleHelper.RxData.length > 12 && checkcommand(act.BleHelper.RxData.substring(10, 12))) {
                        break
                    }
                    val now = sdf.parse(sdf.format(Date()))
                    val time = getDatePoor(now, past).toDouble()
                    if (time > 3) {
                        return false
                    }
                    Thread.sleep(100)
                }
            }
            fr.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    //取得時間差
    //    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //    Date d1=sdf.parse("2019-06-12 16:26:10");
    //    Date d2=sdf.parse(sdf.format(new Date()));
    //     System.out.println(getDatePoor(d1,d2));
    fun getDatePoor(endDate: Date, nowDate: Date): Int {
        val diff = endDate.time - nowDate.time
        val sec = diff / 1000
        return sec.toInt()
    }

    fun CheckS19(filename: String): Boolean {
        try {
            val fr = FileInputStream(filename)
            val br = BufferedReader(InputStreamReader(fr))
            val a = br.readLine()
            val b = br.readLine()
            if (SensorModel == a.substring(4, 8) && AppVersion == a.substring(8, 10) && Lib == b.substring(0, 2)) {
                fr.close()
                return true
            } else {
                fr.close()
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun ProgramStep(filename: String, Lf: String): Boolean {
        ClearChech()
        try {
            ReadSensorId()
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            val fal = 0
            while (SensorModel == "nodata" && AppVersion == "nodata" && Lib == "nodata") {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 10) {
                    return false
                }
            }
            if (CheckS19(filename)) {
                return ProgramSensor(filename, Lf)
            } else {
                if (!ClearSensor()) {
                    return false
                }
                return if (!LogData(filename)) {
                    false
                } else ProgramSensor(filename, Lf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command14(): Boolean {
        try {
            val check = (8 + 6 * IC) * 2
            Log.d("WriteReback", check.toString() + "" + "ic" + IC)
            SendData(getCRC16("0AFE14000D4F52414E474554504D53XXXXF5"), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 2) {
                    SendData(getCRC16("0AFE14000D4F52414E474554504D53XXXXF5"), check)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal > 3) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    var g = true
                    for (i in 0 until IC) {
                        val tmp = act.BleHelper.RxData.substring(10, act.BleHelper.RxData.length - 6)
                        if (checkcommand(tmp.substring(i * 12, i * 12 + 2))) {
                            g = false
                        }
                    }
                    return g
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun ProgramAll(filename: String, Lf: String): Boolean {
        ClearChech()
        try {
            if (!Command03()) {
                return false
            }
            if (!Command10_FE()) {
                return false
            }
            if (CheckS19(filename)) {
                return Command15(Lf)
            } else {
                if (!Command14()) {
                    return false
                }
                if (!LogData(filename)) {
                    return false
                }
                return if (!Command17()) {
                    false
                } else Command15(Lf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command17(): Boolean {
        try {
            val check = (8 + 10 * (IC - 1)) * 2
            SendData(getCRC16("0AFE1700094F52414E4745A7C4F5"), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 10) {
                    SendData(getCRC16("0AFE1700094F52414E4745A7C4F5"), check)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal > 1) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    var g = true
                    for (i in 0 until IC - 1) {
                        val tmp = act.BleHelper.RxData.substring(10, act.BleHelper.RxData.length - 6)
                        if (!checkcommand(tmp.substring(i * 14, i * 14 + 2))) {
                            g = false
                        }
                    }
                    return g
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command15(Lf: String): Boolean {
        try {
            FALSE_CHANNEL = ArrayList()
            CHANNEL_BLE = ArrayList()
            BLANK_CHANNEL = ArrayList()
            val check = (8 + 7 * 2 * IC) * 2
            SendData(getCRC16("0AFE150008LF154504D53XXXXF5".replace("LF", Lf)), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            val fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 20) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    var g = true
                    for (i in 0 until IC * 2) {
                        val tmp = act.BleHelper.RxData.substring(10, act.BleHelper.RxData.length - 6)
                        if (!checkcommand(tmp.substring(i * 14, i * 14 + 2))) {
                            g = false
                            Log.w("WriteReback", "失敗channel" + tmp.substring(i * 14 + 2, i * 14 + 4))
                            if (tmp.substring(i * 14 + 4, i * 14 + 12) == "00018001") {
                                BLANK_CHANNEL.add(tmp.substring(i * 14 + 2, i * 14 + 4))
                            } else {
                                FALSE_CHANNEL.add(tmp.substring(i * 14 + 2, i * 14 + 4))
                            }
                        } else {
                            CHANNEL_BLE.add(
                                tmp.substring(i * 14 + 2, i * 14 + 4) + "." + tmp.substring(
                                    i * 14 + 4,
                                    i * 14 + 12
                                )
                            )
                        }
                    }
                    return g
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command10_FE(): Boolean {
        try {
            val check = (14 * IC + 8) * 2
            Log.e("ic",""+check)
            SendData(getCRC16("0AFE10000754504D537331F5"), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 2) {
                    SendData(getCRC16("0AFE10000754504D537331F5"), check)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal > 3) {
                    return false
                }
                if (act.BleHelper.RxData.length == check && SensorModel != "nodata" && AppVersion != "nodata" && Lib != "nodata") {
                    return true
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun ProgramAll(filename: String, ID1: String, ID2: String, ID3: String, ID4: String, Lf: String): Boolean {
        ClearChech()
        try {
            if (!Command03()) {
                return false
            }
            if (!Command10_FE()) {
                return false
            }
            if (CheckS19(filename)) {
                return Command25(ID1, ID2, ID3, ID4, Lf)
            } else {
                if (!Command14()) {
                    return false
                }
                if (!LogData(filename)) {
                    return false
                }
                return if (!Command17()) {
                    false
                } else Command25(ID1, ID2, ID3, ID4, Lf)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command01(): Boolean {
        try {
            SendData("0A000100094F52414E4745ACF3F5", 32)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 2) {
                    SendData(getCRC16("0A000100094F52414E4745ACF3F5"), 32)
                    past = sdf.parse(sdf.format(Date()))
                    fal++
                }
                if (fal > 3) {
                    return false
                }
                if (act.BleHelper.RxData.length == 32) {
                    Appver = act.BleHelper.RxData.substring(20, 24)
                    Boover = act.BleHelper.RxData.substring(16, 20)
                    return true
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun Command25(ID1: String, ID2: String, ID3: String, ID4: String, Lf: String): Boolean {
        try {
            FALSE_CHANNEL = ArrayList()
            CHANNEL_BLE = ArrayList()
            BLANK_CHANNEL = ArrayList()
            var command = "0AFE250017LF1ID1LF2ID2LF3ID3LF4ID4XXXXF5".replace("LF", Lf)
            val check = 72
            command = command.replace("ID1", ID1).replace("ID2", ID2).replace("ID3", ID3).replace("ID4", ID4)
            SendData(getCRC16(command), check)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 20) {
                    return false
                }
                if (act.BleHelper.RxData.length == check) {
                    var g = true
                    for (i in 0..3) {
                        val tmp = act.BleHelper.RxData.substring(10, act.BleHelper.RxData.length - 6)
                        if (!checkcommand(tmp.substring(i * 14, i * 14 + 2))) {
                            g = false
                            Log.w("WriteReback", "失敗channel" + tmp.substring(i * 14 + 2, i * 14 + 4))
                            if (tmp.substring(i * 14 + 4, i * 14 + 12) == "00018001") {
                                BLANK_CHANNEL.add(tmp.substring(i * 14 + 2, i * 14 + 4))
                            } else {
                                FALSE_CHANNEL.add(tmp.substring(i * 14 + 2, i * 14 + 4))
                            }
                        } else {
                            Log.w("WriteReback", "成功channel" + tmp.substring(i * 14 + 2, i * 14 + 4))
                            CHANNEL_BLE.add(
                                tmp.substring(i * 14 + 2, i * 14 + 4) + "." + tmp.substring(
                                    i * 14 + 4,
                                    i * 14 + 12
                                )
                            )
                        }
                    }
                    return g
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun ClearChech() {
        SensorModel = "nodata"
        AppVersion = "nodata"
        Lib = "nodata"
    }

    //    public boolean A0xEB(){
    //        try{
    //            socket.write((getCRC16("0AFEEB000D4F52414E474554504D533A72F5")),0);
    //            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    //            Date past=sdf.parse(sdf.format(new Date()));
    //            while(true){
    //                Date now=sdf.parse(sdf.format(new Date()));
    //                double time=getDatePoor(now,past);
    //                if(time>5){return  false;}
    //                if((act.BleHelper.RxData.equals("F5FEEB0005C0003B480A")||act.BleHelper.RxData.equals("F5FEEB0007C000A001597F0A"))){
    //                    return true;
    //                }
    //            }
    //        }catch (Exception e){e.printStackTrace(); return false;}
    //    }
    //    public int HandShake(String mpass){
    //        //0代表失敗
    //        //1模組在Boot loader流程
    //        //2模組在主程式流程
    //        //3在app中
    //try{
    //    String command=addcheckbyte("0A"+mpass+"000030000F5");
    //    socket.write((command),0);
    //    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    //    Date past=sdf.parse(sdf.format(new Date()));
    //    while(true){
    //        Date now=sdf.parse(sdf.format(new Date()));
    //        double time=getDatePoor(now,past);
    //        if(time>2){return  -1;}
    //        if(act.BleHelper.RxData.length()==14&&act.BleHelper.RxData.equals("F5"+mpass+"0000311E70A")){
    //          return 0;
    //            }
    //        if(act.BleHelper.RxData.length()==14&&act.BleHelper.RxData.equals(addcheckbyte("F5"+mpass+"0000321000A"))){
    //            return 1;
    //        }
    //        if(act.BleHelper.RxData.length()==22){
    //            return 2;
    //        }
    //    }
    //}catch (Exception e){e.printStackTrace();return  -1;}
    //    }
    //    public boolean ClearFlush(){
    //        try{
    //            String command=addcheckbyte("0A0000030000F5");
    //            SendData(command,14);
    //            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    //            Date past=sdf.parse(sdf.format(new Date()));
    //            while(true){
    //
    //            }
    //        }catch (Exception e){e.printStackTrace();return false;}
    //    }
    fun SendData(data: String, check: Int) {
        act.BleHelper.writeHex(getCRC16(data),act.RxChannel,act.TxChannel)
    }

    //    public  boolean WriteFlash(final Context context,  final int Ind,final String filename,String mcpass){
    //        try{
    //            FileInputStream fo=new FileInputStream(context.getApplicationContext().getFilesDir().getPath()+"/"+filename+".s2");
    //            InputStreamReader fr = new InputStreamReader(fo);
    //            BufferedReader br = new BufferedReader(fr);
    //            StringBuilder sb = new StringBuilder();
    //            while (br.ready()) {
    //                String s=br.readLine();
    //                s=s.replace("null","");
    //                sb.append(s);
    //            }
    //            int Long=0;
    //            if(sb.length()%Ind == 0){Long=sb.length()/Ind;
    //            }else{Long=sb.length()/Ind+1;}
    //            Log.d("總行數",""+Long);
    //            for(int i=0;i<Long;i++){
    //                if(i==Long-1){
    //                    Log.d("行數",""+i);
    //                    String data=bytesToHex(sb.substring(i*Ind, sb.length()).getBytes());
    //                    int length=Ind+2;
    //                    check(Convvvert(data,Integer.toHexString(length),mcpass),mcpass);
    //                    act.getHandler().post(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            act.LoadingUI(act.getResources().getString(R.string.update), 100);
    //                        }
    //                    });
    //                    return true;
    //                }else{
    //                    String data=bytesToHex(sb.substring(i*Ind, i*Ind+Ind).getBytes());
    //                    Log.d("行數",""+i);
    //                    int length=Ind+2;
    //                    float finalI = i;
    //                    float finalLong = Long;
    //                    act.getHandler().post(new Runnable() {
    //                        @Override
    //                        public void run() {
    //                            act.LoadingUI(act.getResources().getString(R.string.update), (int)((finalI / finalLong)*100));
    //                        }
    //                    });
    //                    if(!check(Convvvert(data,Integer.toHexString(length),mcpass),mcpass)){
    //                        return false;
    //                    }
    //                }
    //            }
    //            fr.close();
    //            return true;
    //        }catch(Exception e){e.printStackTrace();return false;}
    //    }
    //    public  boolean check(String data,String mcpass){
    //        try{
    //            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    //            Date past=sdf.parse(sdf.format(new Date()));
    //            int fal=0;
    //            socket.write(addcheckbyte(data),14);
    //            while(fal<5){
    //                Date now=sdf.parse(sdf.format(new Date()));
    //                double time=getDatePoor(now,past);
    //                if(time>0.3){
    //                    past=sdf.parse(sdf.format(new Date()));
    //                    SendData(addcheckbyte(data),14);
    //                    fal++;
    //                }
    //                if(act.BleHelper.RxData.length()==14&&act.BleHelper.RxData.equals(addcheckbyte("F5"+mcpass+"2000300F40A"))||act.BleHelper.RxData.equals(addcheckbyte("F5"+mcpass+"B000300000A"))){
    //                    return true;
    //                }
    //            }
    //            return false;
    //        }catch (Exception e){e.printStackTrace();return false;}
    //    }
    fun Convvvert(data: String, length: String, mcpass: String): String {
        var length = length
        var command = "0A" + mcpass + "2LX00F5"
        while (length.length < 4) {
            length = "0$length"
        }
        command = addcheckbyte(command.replace("L", length).replace("X", data))
        return command
    }

    //Reboot
    fun Reboot(): Boolean {
        try {
            val a = "0A0D00030000F5"
            SendData(addcheckbyte(a), 14)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past).toDouble()
                if (time > 3) {
                    return false
                }
                if (act.BleHelper.RxData.equals("F501000300F70A")) {
                    return true
                }
                Thread.sleep(100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    //    public boolean GoProgram(String mcpass){
    //        try{
    //            String a="0A"+mcpass+"300030000F5";
    //            socket.write(addcheckbyte(a),14);
    //            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    //            Date past=sdf.parse(sdf.format(new Date()));
    //            while(true){
    //                Date now=sdf.parse(sdf.format(new Date()));
    //                double time=getDatePoor(now,past);
    //                if(time>3){return false;}
    //                if(act.BleHelper.RxData.length()==14&&act.BleHelper.RxData.equals(addcheckbyte("F5"+mcpass+"1000300CC0A"))){return true;}
    //            }
    //        }catch (Exception e){e.printStackTrace();return false;}
    //    }
    fun Inapp() {
        val a = "0A0E00030007F5"
        SendData(addcheckbyte(a), 28)
    }

    companion object {
        fun addcheckbyte(com: String): String {
            val a = StringHexToByte(com)
            var checkbyte = a[0]
            for (i in 1 until a.size - 2) {
                checkbyte = checkbyte xor a[i]
            }
            a[a.size - 2] = checkbyte
            return bytesToHex(a)
        }
    }
}
