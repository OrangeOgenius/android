package com.orange.tpms.utils


import java.util.ArrayList

import com.orange.tpms.utils.OgCommand.bytesToHex

object RxCommand {
    //    public static String GetText(byte data[]){
    //
    //    }
    fun A0X10(data: ByteArray): ArrayList<String> {
        if (data.size > 21) {
            var a = bytesToHex(data)
            a = a.substring(10, a.length - 6)
            val re = ArrayList<String>()
            for (i in 0 until a.length / 28) {
                val SENSOR_MODEL = a.substring(i * 28 + 2, i * 28 + 6)
                val APP_VERSION = a.substring(i * 28 + 6, i * 28 + 8)
                val LIB_VERSION = a.substring(i * 28 + 12, i * 28 + 14)
                val STATION = a.substring(i * 28 + 26, i * 28 + 28)
                re.add(SENSOR_MODEL)
                re.add(APP_VERSION)
                re.add(LIB_VERSION)
                re.add(STATION)
            }
            return re
        } else {
            val a = bytesToHex(data)
            val SENSOR_MODEL = a.substring(12, 16)
            val APP_VERSION = a.substring(16, 18)
            val LIB_VERSION = a.substring(22, 24)
            val re = ArrayList<String>()
            re.add(SENSOR_MODEL)
            re.add(APP_VERSION)
            re.add(LIB_VERSION)
            return re
        }
    }

    fun RX(data: ByteArray, command: BleCommand): String {
        //        activity.setRXDATA(bytesToHex(data));
        if (data.size == 21 && data[2].toInt() == 0x10 && data[20].toInt() == 0x0A) {
            val A0X10 = A0X10(data)
            command.SensorModel = A0X10.get(0);
            command.AppVersion = A0X10.get(1);
            command.Lib = A0X10.get(2);
            return "SensorModel:" + A0X10[0] + "\nAppVersion:" + A0X10[1] + "\nLib:" + A0X10[2]
        }
        if (data.size > 21 && data[1] == 0xFE.toByte() && data[data.size - 1] == 0x0A.toByte() && data[2].toInt() == 0x10) {
            val A0X10 = A0X10(data)
            var spn = ""
            var tmpSensorModel = ""
            var tmpAppVersion = ""
            var tmpLib = ""
            for (i in 0 until A0X10.size / 4) {
                spn =
                    spn + "SensorModel:" + A0X10[i * 4] + "\nAppVersion:" + A0X10[i * 4 + 1] + "\nLib:" + A0X10[i * 4 + 2] + "\n" + "Station:" + A0X10[i * 4 + 3] + "\n\n"
                if (i == 0) {
                    tmpSensorModel = A0X10[0]
                    tmpAppVersion = A0X10[1]
                    tmpLib = A0X10[2]
                } else {
                    if (tmpSensorModel != A0X10[i * 4] && tmpAppVersion != A0X10[i * 4 + 1] && tmpLib != A0X10[i * 4 + 2]) {
                        tmpSensorModel = "noequal"
                        tmpAppVersion = "noequal"
                        tmpLib = "noequal"
                        spn =
                            spn + "SensorModel:" + A0X10[i * 4] + "\nAppVersion:" + A0X10[i * 4 + 1] + "\nLib:" + A0X10[i * 4 + 2] + "\n" + "Station:" + A0X10[i * 4 + 3] + "\n不一樣\n"
                    }
                }
            }
            command.SensorModel = tmpSensorModel
            command.AppVersion = tmpAppVersion
            command.Lib = tmpLib
            return spn
        }

        return bytesToHex(data)
    }

}
