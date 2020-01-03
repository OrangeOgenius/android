package com.orange.tpms.utils

import android.app.Dialog
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.Callback.Obd_C
import com.orange.tpms.R
import com.orange.tpms.bean.ID_Beans
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand.StringHexToByte
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.xor


class ObdCommand {
    var act= (JzActivity.getControlInstance().getRootActivity() as KtActivity).BleManager
    var AppVersion = ""
    var donloads19 = "OBDB_APP_TO001_191030"
    //自動設定checkbyteF5020005000000F20A
    fun addcheckbyte(com: String): String {
        val a = StringHexToByte(com)
        var checkbyte = a[0]
        for (i in 1 until a.size - 2) {
            checkbyte = checkbyte xor a[i]
        }
        a[a.size - 2] = checkbyte
        return bytesToHex(a)
    }

    //握手
    fun HandShake(): Boolean {
        try {
            val a = "0A0000030000F5"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            act.BleHelper.writeHex(addcheckbyte(a),act.BleHelper.RXchannel,act.BleHelper.TXchannel)
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 3) {
                    return false
                }
                if (act.BleHelper.RxData.length == 14) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.d("CommandError", e.message)
            return false
        }

    }

    fun GetId(count: String): ID_Beans {
        val id = ID_Beans()
        try {
            val a = "60BF0001" + count + "FF0A"
            act.BleHelper.writeBytes(GetXOR(a),act.BleHelper.RXchannel,act.BleHelper.TXchannel)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 1) {
                    fal++
                    past = sdf.parse(sdf.format(Date()))
                    act.BleHelper.writeBytes(GetXOR(a), act.BleHelper.RXchannel,act.BleHelper.TXchannel)
                }
                if (fal == 5) {
                    id.success = false
                    return id
                }
                if (act.BleHelper.RxData.length == 52) {
                    id.success = true
                    id.LF = act.BleHelper.RxData.substring(8, 16)
                    id.RF = act.BleHelper.RxData.substring(16, 24)
                    id.LR = act.BleHelper.RxData.substring(24, 32)
                    id.RR = act.BleHelper.RxData.substring(32, 40)
                    id.SP = act.BleHelper.RxData.substring(40, 48)
                    if (id.LF == "FFFFFFFF" && id.RF == "FFFFFFFF" && id.LR == "FFFFFFFF" && id.RR == "FFFFFFFF" && id.SP == "FFFFFFFF") {
                        id.success = false
                    }
                    return id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return id
        }

    }

    //Reboot
    fun Reboot(): Boolean {
        try {
            val a = "0A0D00030000F5"
            act.BleHelper.writeHex(addcheckbyte(a),act.RxChannel,act.TxChannel)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 7) {
                    return false
                }
                if (act.BleHelper.RxData.equals("F501000300F70A")) {
                    return true
                }
            }
        } catch (e: Exception) {
            Log.d("CommandError", e.message)
            return false
        }

    }

    fun WriteVersion(): Boolean {
        try {
            val command =
                GetXOR("0ACA0015DDFFF5".replace("DD", bytesToHex(donloads19.replace(".srec", "").toByteArray())))
            act.BleHelper.writeBytes(command,act.RxChannel,act.TxChannel)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 1) {
                    if (fal == 1) {
                        return false
                    }
                    past = sdf.parse(sdf.format(Date()))
                    act.BleHelper.writeBytes(command,act.RxChannel,act.TxChannel)
                    fal++
                }
                if (act.BleHelper.RxData.length == 14) {
                    Log.d("BLEDATA", "寫入版本")
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun GoBootloader(): Boolean {
        try {
            act.BleHelper.writeBytes(GetXOR("0ACD010100FFF5"),act.RxChannel,act.TxChannel)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 10) {
                    if (fal == 1) {
                        return false
                    }
                    past = sdf.parse(sdf.format(Date()))
                    act.BleHelper.writeBytes(GetXOR("0ACD010100FFF5"),act.RxChannel,act.TxChannel)
                    fal++
                }
                if (act.BleHelper.RxData.contains("F5CD010100CD0A01000300F70A")) {
                    Log.d("BLEDATA", "進入燒錄")
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    // 燒寫&amp;驗證Flash
    fun WriteFlash(context: Context, FileName: String, Ind: Int): Boolean {
        try {
            val fo = FileInputStream(context.applicationContext.filesDir.path + "/" + FileName + ".srec")
            val fr = InputStreamReader(fo)
            val br = BufferedReader(fr)
            val sb = StringBuilder()
            while (br.ready()) {
                var s: String? = br.readLine()
                if (s != null) {
                    s = s.replace("null", "")
                    sb.append(s)
                }
            }
            var Long = 0
            if (sb.length % Ind == 0) {
                Long = sb.length / Ind
            } else {
                Long = sb.length / Ind + 1
            }
            Log.d("總行數", "" + Long)
            for (i in 0 until Long) {
                var b = i
                if (b >= 255) {
                    b = b - 255
                }
                val result = StringBuffer(Integer.toHexString(b))
                while (result.length < 2) {
                    result.insert(0, "0")
                }
                val cont = result.toString().toUpperCase()
                if (i == Long - 1) {
                    Log.d("write", "以跑完$i")
                    val data = bytesToHex(sb.substring(i * Ind, sb.length).toByteArray())
                    val length = sb.substring(i * Ind, sb.length).toByteArray().size + 3
                    act.BleHelper.writeHex(Convvvert(data, Integer.toHexString(length), cont), act.RxChannel,act.TxChannel)
                    JzActivity.getControlInstance().getRootActivity().handler.post {
                        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true,object:SetupDialog{
                            override fun dismess() {

                            }

                            override fun keyevent(event: KeyEvent): Boolean {
                                return false
                            }

                            override fun setup(rootview: Dialog) {
                                val text:TextView = rootview.findViewById(R.id.tit)
                                text.text=(JzActivity.getControlInstance().getRootActivity().resources.getString(R.string.Programming) + "..." + 100 + "%")
                            }

                        })
                    }
                    return true
                } else {
                    val data = bytesToHex(sb.substring(i * Ind, i * Ind + Ind).toByteArray())
                    Log.d("行數", "" + i)
                    val length = sb.substring(i * Ind, i * Ind + Ind).toByteArray().size + 3
                    if (!check(Convvvert(data, Integer.toHexString(length), cont))) {
                        return false
                    }
                    val finalLong = Long
                    JzActivity.getControlInstance().getRootActivity().handler.post {
                        JzActivity.getControlInstance().showDiaLog(R.layout.normal_dialog, false, true, object :SetupDialog{
                            override fun dismess() {

                            }

                            override fun keyevent(event: KeyEvent): Boolean {
                               return false
                            }

                            override fun setup(rootview: Dialog) {
                                val text:TextView = rootview.findViewById(R.id.tit)
                                text.text=(JzActivity.getControlInstance().getRootActivity().resources.getString(R.string.Programming) + "..." + i * 100 / finalLong + "%")
                            }
                        })
                    }
                }
            }
            fr.close()
            return true
        } catch (e: Exception) {
            Log.d("CommandError", e.message)
            return false
        }

    }

    //設定tireid
    fun setTireId(Id: ArrayList<String>, caller: Obd_C) {
        try {
            val tmpsend = ArrayList<String>()
            tmpsend.add("60A200FFFFFFFFC20A")
            var i = 1
            for (id in Id) {
                var id2=id
                id2 = AddEmpty(id2)
                if (id != null) {
                    tmpsend.add(addcheckbyte("60A20XidFF0A".replace("id", id).replace("X", "" + i)))
                }
                i++
            }
            tmpsend.add("60A2FFFFFFFFFF3D0A")
            for (a in tmpsend) {
                act.BleHelper.writeHex(a,act.RxChannel,act.TxChannel)
                Thread.sleep(50)
            }
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 10) {
                    caller.result_C(false)
                    break
                }
                if (act.BleHelper.RxData.equals("60B201FFFFFFFFD30A")) {
                    caller.result_C(true)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            caller.result_C(false)
        }

    }

    fun Convvvert(data: String, length: String, line: String): String {
        var length = length
        var line = line
        var command = "0A02LHX00F5"
        while (length.length < 4) {
            length = "0$length"
        }
        if (line == "F5") {
            line = "00"
        }
        if (line.length > 2) {
            line = "00"
        }
        command = addcheckbyte(command.replace("L", length).replace("X", data).replace("H", line))
        return command
    }

    fun check(data: String): Boolean {
        act.BleHelper.writeHex(addcheckbyte(data),act.RxChannel,act.TxChannel)
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (fal < 20) {
                //                Thread.currentThread().sleep(20);
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 1) {
                    past = sdf.parse(sdf.format(Date()))
                    act.BleHelper.writeHex(addcheckbyte(data), act.BleHelper.RXchannel,act.BleHelper.TXchannel)
                    fal++
                }
                if (act.BleHelper.RxData.length >= 16) {
                    //                    Thread.currentThread().sleep(100);
                    return true
                }
            }
            return false
        } catch (e: Exception) {
            Log.d("CommandError", e.message)
            return false
        }

    }

    fun AskVersion(): Boolean {
        try {
            act.BleHelper.writeBytes(GetXOR("0ACF000100FFF5"),act.RxChannel,act.TxChannel)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            val past = sdf.parse(sdf.format(Date()))
            val fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (time > 2) {
                    return false
                }
                if (act.BleHelper.RxData.length == 54) {
                    AppVersion = act.BleHelper.RxData.substring(8, 50)
                    Log.d("BLEDATA", "版本號:$AppVersion")
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    fun GoApp(): Boolean {
        try {
            act.BleHelper.writeBytes(GetXOR("0ACD000100FFF5"),act.RxChannel,act.TxChannel)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
            var past = sdf.parse(sdf.format(Date()))
            var fal = 0
            while (true) {
                val now = sdf.parse(sdf.format(Date()))
                val time = getDatePoor(now, past)
                if (fal == 3) {
                    return false
                }
                if (time > 1) {
                    past = sdf.parse(sdf.format(Date()))
                    act.BleHelper.writeBytes(GetXOR("0ACD000100FFF5"),act.RxChannel,act.TxChannel)
                    fal++
                }
                if (act.BleHelper.RxData.length == 14) {
                    Log.d("BLEDATA", "進入app")
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    companion object {
        var WRITE_SUCCESS = "F502000300F40A"
        var Program_Flash_Fail = "F502000302F60A"
        var VERIFY_FAIL = "F502000303F70A"
        fun setScreenSleepTime(millisecond: Int, context: Context) {
            try {
                Settings.System.putInt(
                    context.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT,
                    millisecond
                )
            } catch (localException: Exception) {
                localException.printStackTrace()
            }

        }

        fun AddEmpty(a: String): String {
            var temp=a
            while (a.length<8){
                temp="0$temp"
            }
           
            return temp
        }

        private fun bytesToHex(hashInBytes: ByteArray): String {
            val sb = StringBuilder()
            for (b in hashInBytes) {
                sb.append(String.format("%02X", b))
            }
            return sb.toString()
        }

        //System.out.println(strDate);
        val dateTime: String
            get() {

                val sdFormat = SimpleDateFormat("HH:mm:ss:SSS")

                val date = Date()

                return sdFormat.format(date)

            }

        fun getDatePoor(endDate: Date, nowDate: Date): Double {
            val diff = endDate.time - nowDate.time
            val sec = diff / 1000
            return sec.toDouble()
        }

        fun GetXOR(a: String): ByteArray {
            val command = StringHexToByte(a)
            var xor:Byte = 0
            for (i in 0 until command.size - 2) {
                xor = xor xor command[i]
            }
            command[command.size - 2] = xor.toByte()
            return command
        }
    }
}
