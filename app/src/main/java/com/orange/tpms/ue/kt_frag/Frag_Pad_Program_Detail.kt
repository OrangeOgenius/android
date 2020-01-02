package com.orange.tpms.ue.kt_frag


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.orange.jzchi.jzframework.JzActivity
import com.orange.tpms.HttpCommand.Fuction.Upload_IDCopyRecord
import com.orange.tpms.HttpCommand.Fuction.Upload_ProgramRecord
import com.orange.tpms.HttpCommand.SensorRecord
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.activity.KtActivity
import kotlinx.android.synthetic.main.fragment_frag__pad__program__detail.view.*
import java.text.SimpleDateFormat
import java.util.*


class Frag_Pad_Program_Detail : RootFragement(R.layout.fragment_frag__pad__program__detail) {
    var first=true
    val UNLINK=0
    val PROGRAN_SUCCESS=1
    val PROGAMMING=4
    val PROGRAN_FAULSE=2
    val PROGRAN_WAIT=3
    var LFID=""
    var RFID=""
    var LRID=""
    var RRID=""
    var WriteLf=""
    var WriteRf=""
    var WriteLr=""
    var WriteRR=""
    var ISPROGRAMMING=false
    var Idcount=0
    val LF=0
    val RF=1
    val LR=2
    val timer= Timer()
    val RR=3
    var Lf="0"
    lateinit var navActivity: KtActivity
    lateinit var make: String
    lateinit var makeImg: String
    var mmyNum=""
    lateinit var model: String
    lateinit var year: String

    override fun viewInit() {
        navActivity = activity as KtActivity
        make = PublicBean.SelectMake
        model = PublicBean.SelectModel
        year = PublicBean.SelectYear
        if(PublicBean.position==PublicBean.PAD_COPY){
            WriteLf= PublicBean.WriteLf
            WriteRf= PublicBean.WriteRf
            WriteLr= PublicBean.WriteLr
            WriteRR= PublicBean.WriteRr
        }
        mmyNum = navActivity.itemDAO.getMMY(make,model,year)
        Log.e("mmy",mmyNum)
        Idcount=8-navActivity.itemDAO.GetCopyId( mmyNum)
        Lf=navActivity.itemDAO.getLf(mmyNum)!!
        if((mmyNum.equals("RN1628")||mmyNum.equals("SI2048"))&&WriteLf.length==8){
            val WriteLftmp=WriteLf.substring(0,2)+"XX"+WriteLf.substring(4,6)+"YY"
            val WriteRftmp=WriteRf.substring(0,2)+"XX"+WriteRf.substring(4,6)+"YY"
            val WriteLrtmp=WriteLr.substring(0,2)+"XX"+WriteLr.substring(4,6)+"YY"
            val WriteRRtmp=WriteRR.substring(0,2)+"XX"+WriteRR.substring(4,6)+"YY"
            WriteLf=WriteLftmp.replace("XX",WriteLf.substring(6,8)).replace("YY",WriteLf.substring(2,4))
            WriteRf=WriteRftmp.replace("XX",WriteRf.substring(6,8)).replace("YY",WriteRf.substring(2,4))
            WriteLr=WriteLrtmp.replace("XX",WriteLr.substring(6,8)).replace("YY",WriteLr.substring(2,4))
            WriteRR=WriteRRtmp.replace("XX",WriteRR.substring(6,8)).replace("YY",WriteRR.substring(2,4))
        }
        Thread{(activity as KtActivity).BleCommand.Setserial() }.start()
        rootview.mmy_text.text = "$make/$model /$year"
        mmyNum = navActivity.itemDAO.getMMY(make,model,year)
        val mmyname=GetPro(mmyNum,"nodata")
        SensorRecord.SensorCode_Version = mmyname
        first=true
        UpdateUi(LF,UNLINK)
        UpdateUi(RF,UNLINK)
        UpdateUi(LR,UNLINK)
        UpdateUi(RR,UNLINK)
        if(WriteLf.length==8){
            rootview.copy_id_btn.setBackgroundResource(R.drawable.solid)
            rootview.copy_id_btn.setTextColor(navActivity.resources.getColor(R.color.white))
            rootview.Program_bt.setBackgroundResource(R.drawable.stroke)
            rootview.Program_bt.setTextColor(navActivity.resources.getColor(R.color.buttoncolor))
            rootview.Program_bt.setOnClickListener {
                PublicBean.position=PublicBean.PAD_PROGRAM
                JzActivity.getControlInstance().changeFrag(Frag_Pad_Program_Detail(), R.id.frage,"Frag_Pad_Program_Detail",false);
            }
        }else{
            rootview.copy_id_btn.setOnClickListener {
                PublicBean.position=PublicBean.PAD_COPY
                JzActivity.getControlInstance().changeFrag(Frag_Pad_Keyin(), R.id.frage,"Frag_Pad_Keyin",false);
            }
        }

        rootview.animation_view2.speed = 1.0f
        UpdateUiCondition(PROGRAN_WAIT)
        rootview.program.setOnClickListener {
            program()
        }
        rootview.menu.setOnClickListener {
            program()}
        rootview.Relarm.setOnClickListener {
            JzActivity.getControlInstance().changeFrag(Frag_Relearm_Detail(),R.id.frage,"Frag_Relearm_Detail",true);
        }
        UdCondition()

    }


    fun getMem(str: String, m: String): Int {
        var str = str
        var i = 0
        while (str.indexOf(m) != -1) {
            val a = str.indexOf(m)
            str = str.substring(a + 1)
            i++
        }
        return i
    }

    fun UdCondition(){
        handler.post {
//            navActivity.back.isClickable=false
        }
        run=true
        Thread(Runnable {
            try{
                for (i in 0..1) {
                    val Ch1 = navActivity.BleCommand.Command_11(i, 1)
                    var Id1 = navActivity.BleCommand.ID
                    val Ch2 = navActivity.BleCommand.Command_11(i, 2)
                    var Id2 = navActivity.BleCommand.ID
                    if(!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Pad_Program_Detail")){return@Runnable}
                    handler.post(Runnable {
                        if (Ch1) {
                            if(mmyNum.equals("RN1628")||mmyNum.equals("SI2048")){
                                val Writetmp=Id1.substring(0,2)+"XX"+Id1.substring(4,6)+"YY"
                                Id1=Writetmp.replace("XX",Id1.substring(6,8)).replace("YY",Id1.substring(2,4))
                            }
                            Id1=Id1.substring(Idcount)
                            if(i==0){
                                LFID=Id1
                                if(first){UpdateUi(LF,PROGRAN_WAIT)}
                                rootview.Lft.text=LFID
                            }else{
                                RFID=Id1
                                if(first){UpdateUi(RF,PROGRAN_WAIT)}
                                rootview.Rft.text=RFID
                            }
                        } else {
                            if(i==0){
                                LFID=navActivity.resources.getString(R.string.Unlinked)
                                if(first){UpdateUi(LF,UNLINK)}
                                rootview.Lft.text=LFID
                            }else{
                                RFID=navActivity.resources.getString(R.string.Unlinked)
                                if(first){UpdateUi(RF,UNLINK)}
                                rootview.Rft.text=RFID
                            }
                        }
                        if (Ch2) {
                            if(mmyNum.equals("RN1628")||mmyNum.equals("SI2048")){
                                val Writetmp=Id2.substring(0,2)+"XX"+Id2.substring(4,6)+"YY"
                                Id2=Writetmp.replace("XX",Id2.substring(6,8)).replace("YY",Id2.substring(2,4))
                            }
                            Id2=Id2.substring(Idcount)
                            if(i==0){
                                LRID=Id2
                                if(first){UpdateUi(LR,PROGRAN_WAIT)}
                                rootview.Lrt.text=LRID
                            }else{
                                RRID=Id2
                                if(first){UpdateUi(RR,PROGRAN_WAIT)}
                                rootview.Rrt.text=RRID
                            }
                        } else {
                            if(i==0){
                                LRID=navActivity.resources.getString(R.string.Unlinked)
                                rootview.Lrt.text=navActivity.resources.getString(R.string.Unlinked)
                                if(first){UpdateUi(LR,UNLINK)}
                            }else{
                                RRID=navActivity.resources.getString(R.string.Unlinked)
                                if(first){UpdateUi(RR,UNLINK)}
                                rootview.Rrt.text=navActivity.resources.getString(R.string.Unlinked)
                            }
                        }
                    })
                }

                handler.post {
//                    navActivity.back.isClickable=true
                    run=false
                }
                Thread.sleep(4000)
                if(first){
                    UdCondition()}
            }catch (e: Exception){e.printStackTrace()}
//            first=false
        }).start()
    }
    fun UpdateUi(position:Int,situation:Int){
        when(position){
            LF->{
                when(situation){
                    UNLINK->{
                        rootview.Lft.text=navActivity.resources.getString(R.string.Unlinked)
                        rootview.Lfi.visibility=View.GONE
                        rootview.Lft.setBackgroundResource(R.mipmap.icon_input_box_locked)
                        rootview.Lf.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                    PROGRAN_SUCCESS->{
                        rootview.Lft.text=LFID
                        rootview.Lfi.visibility=View.VISIBLE
                        rootview.Lfi.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.correct))
                        rootview.Lft.setBackgroundResource(R.mipmap.icon_input_box_ok)
                        rootview.Lf.setBackgroundResource(R.mipmap.icon_tire_ok)
                    }
                    PROGRAN_FAULSE->{
                        rootview.Lft.text=LFID
                        rootview.Lfi.visibility=View.VISIBLE
                        rootview.Lfi.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.error))
                        rootview.Lft.setBackgroundResource(R.mipmap.icon_input_box_fail)
                        rootview.Lf.setBackgroundResource(R.mipmap.icon_tire_fail)
                    }
                    PROGRAN_WAIT->{
                        rootview.Lft.text=LFID
                        rootview.Lfi.visibility=View.GONE
                        rootview.Lft.setBackgroundResource(R.mipmap.icon_input_box_write)
                        rootview.Lf.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                }
            }
            RF->{
                when(situation){
                    UNLINK->{
                        rootview.Rft.text=navActivity.resources.getString(R.string.Unlinked)
                        rootview.Rfi.visibility=View.GONE
                        rootview.Rft.setBackgroundResource(R.mipmap.icon_input_box_locked)
                        rootview.Rf.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                    PROGRAN_SUCCESS->{
                        rootview.Rft.text=RFID
                        rootview.Rfi.visibility=View.VISIBLE
                        rootview.Rfi.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.correct))
                        rootview.Rft.setBackgroundResource(R.mipmap.icon_input_box_ok)
                        rootview.Rf.setBackgroundResource(R.mipmap.icon_tire_ok)
                    }
                    PROGRAN_FAULSE->{
                        rootview.Rft.text=RFID
                        rootview.Rfi.visibility=View.VISIBLE
                        rootview.Rfi.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.error))
                        rootview.Rft.setBackgroundResource(R.mipmap.icon_input_box_fail)
                        rootview.Rf.setBackgroundResource(R.mipmap.icon_tire_fail)
                    }
                    PROGRAN_WAIT->{
                        rootview.Rft.text=RFID
                        rootview.Rfi.visibility=View.GONE
                        rootview.Rft.setBackgroundResource(R.mipmap.icon_input_box_write)
                        rootview.Rf.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                }
            }
            LR->{
                when(situation){
                    UNLINK->{
                        rootview.Lrt.text=navActivity.resources.getString(R.string.Unlinked)
                        rootview.Lri.visibility=View.GONE
                        rootview.Lrt.setBackgroundResource(R.mipmap.icon_input_box_locked)
                        rootview.Lr.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                    PROGRAN_SUCCESS->{
                        rootview.Lrt.text=LRID
                        rootview.Lri.visibility=View.VISIBLE
                        rootview.Lri.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.correct))
                        rootview.Lrt.setBackgroundResource(R.mipmap.icon_input_box_ok)
                        rootview.Lr.setBackgroundResource(R.mipmap.icon_tire_ok)
                    }
                    PROGRAN_FAULSE->{
                        rootview.Lrt.text=LRID
                        rootview.Lri.visibility=View.VISIBLE
                        rootview.Lri.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.error))
                        rootview.Lrt.setBackgroundResource(R.mipmap.icon_input_box_fail)
                        rootview.Lr.setBackgroundResource(R.mipmap.icon_tire_fail)
                    }
                    PROGRAN_WAIT->{
                        rootview.Lrt.text=LRID
                        rootview.Lri.visibility=View.GONE
                        rootview.Lrt.setBackgroundResource(R.mipmap.icon_input_box_write)
                        rootview.Lr.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                }
            }
            RR->{
                when(situation){
                    UNLINK->{
                        rootview.Rrt.text=navActivity.resources.getString(R.string.Unlinked)
                        rootview.Rri.visibility=View.GONE
                        rootview.Rrt.setBackgroundResource(R.mipmap.icon_input_box_locked)
                        rootview.Rr.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                    PROGRAN_SUCCESS->{
                        rootview.Rrt.text=RRID
                        rootview.Rri.visibility=View.VISIBLE
                        rootview.Rri.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.correct))
                        rootview.Rrt.setBackgroundResource(R.mipmap.icon_input_box_ok)
                        rootview.Rr.setBackgroundResource(R.mipmap.icon_tire_ok)
                    }
                    PROGRAN_FAULSE->{
                        rootview.Rrt.text=RRID
                        rootview.Rri.visibility=View.VISIBLE
                        rootview.Rri.setImageDrawable(navActivity.resources.getDrawable(R.mipmap.error))
                        rootview.Rrt.setBackgroundResource(R.mipmap.icon_input_box_fail)
                        rootview.Rr.setBackgroundResource(R.mipmap.icon_tire_fail)
                    }
                    PROGRAN_WAIT->{
                        rootview.Rrt.text=RRID
                        rootview.Rri.visibility=View.GONE
                        rootview.Rrt.setBackgroundResource(R.mipmap.icon_input_box_write)
                        rootview.Rr.setBackgroundResource(R.mipmap.icon_tire_normal)
                    }
                }
            }
        }
    }
    fun UpdateUiCondition(position: Int){
        rootview.Relarm.visibility=View.GONE
        rootview.menu.visibility=View.GONE
        rootview.program.visibility=View.GONE
        rootview.programing.visibility=View.GONE
        rootview.animation_view2.visibility=View.GONE
        rootview.success.visibility=View.GONE
        when(position){
            PROGRAN_SUCCESS->{
                rootview.success.visibility=View.VISIBLE
                rootview.condition.text=navActivity.resources.getString(R.string.Programming_completed)
                rootview.condition.setTextColor(navActivity.resources.getColor(R.color.buttoncolor))
                rootview.Relarm.visibility=View.VISIBLE
                rootview.menu.visibility=View.VISIBLE
            }
            PROGRAN_WAIT->{
                rootview.program.visibility=View.VISIBLE
                rootview.program.text=navActivity.resources.getString(R.string.Program_sensor)
                rootview.condition.text=navActivity.resources.getString(R.string.Please_insert_the_sensor_into_the_USB_PAD)
                rootview.condition.setTextColor(navActivity.resources.getColor(R.color.buttoncolor))}
            PROGRAN_FAULSE->{
                rootview.program.visibility=View.VISIBLE
                rootview.program.text=navActivity.resources.getString(R.string.RE_PROGRAM)
                rootview.condition.text=navActivity.resources.getString(R.string.Programming_failed_where)
                rootview.condition.setTextColor(navActivity.resources.getColor(R.color.colorPrimary))
            }
            PROGAMMING->{
                rootview.programing.visibility=View.VISIBLE
                rootview.animation_view2.visibility=View.VISIBLE
                rootview.program.visibility=View.VISIBLE
                rootview.program.text=navActivity.resources.getString(R.string.Program_sensor)
                rootview.condition.text=navActivity.resources.getString(R.string.Programming_do_not_move_sensors)
                rootview.condition.setTextColor(navActivity.resources.getColor(R.color.buttoncolor))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
        first=false
    }
    fun Getid(a:String):String
    {
        var id = a.substring(3, a.length)
        if (mmyNum == "RN1628" || mmyNum == "SI2048")
        {
            var Writetmp = id.substring(0, 2) + "XX" + id.substring(4, 6) + "YY"
            id = Writetmp.replace("XX", id.substring(6, 8)).replace("YY", id.substring(2, 4))
        }

        id = id.substring(Idcount)
        Log.d("reid",id)
        return id
    }
    fun program(){
        if(run){return}
        if(!ISPROGRAMMING){
//            navActivity.back.isClickable=false
            first=false
            ISPROGRAMMING=true
            UpdateUiCondition(PROGAMMING)
            Thread(Runnable {
                var condition:Boolean
                if(WriteLf.length==8&&WriteLr.length==8&&WriteRR.length==8&&WriteRf.length==8){
                    val startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    condition=navActivity.BleCommand.ProgramAll("/sdcard/files19/" + mmyNum + ".s19",WriteLf,WriteLr,WriteRf,WriteRR,Lf)
                    val endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    val idrecord: ArrayList<SensorRecord> = ArrayList()
                    if (!WriteLf.equals("00000000")){
                        var a=SensorRecord()
                        a.Car_SensorID = LFID
                        if(navActivity.BleCommand.FALSE_CHANNEL.contains("01")){a.IsSuccess = "false"; } else { a.IsSuccess = "true"; }
                        a.SensorID = WriteLf
                        idrecord.add(a)
                    }
                    if (!WriteLr.equals("00000000")){
                        var a=SensorRecord()
                        a.Car_SensorID = LRID
                        if(navActivity.BleCommand.FALSE_CHANNEL.contains("02")){a.IsSuccess = "false"; } else { a.IsSuccess = "true"; }
                        a.SensorID = WriteLr
                        idrecord.add(a)
                    }
                    if (!WriteRf.equals("00000000")){
                        var a=SensorRecord()
                        a.Car_SensorID = RFID
                        if(navActivity.BleCommand.FALSE_CHANNEL.contains("03")){a.IsSuccess = "false"; } else { a.IsSuccess = "true"; }
                        a.SensorID = WriteRf
                        idrecord.add(a)
                    }
                    if (!WriteRR.equals("00000000")){
                        var a=SensorRecord()
                        a.Car_SensorID = RRID
                        if(navActivity.BleCommand.FALSE_CHANNEL.contains("04")){a.IsSuccess = "false"; } else { a.IsSuccess = "true"; }
                        a.SensorID = WriteRR
                        idrecord.add(a)
                    }
                    Upload_IDCopyRecord(make,model,year,startime,endtime,PublicBean.SerialNum, "USBPad", "IDCOPY", idrecord.size, "ALL", idrecord,activity as KtActivity)
                }else{
                    val startime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    condition=navActivity.BleCommand.ProgramAll("/sdcard/files19/" + mmyNum + ".s19",Lf)
                    val idrecord: ArrayList<SensorRecord> = ArrayList()
                    val endtime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                    for (i in 0 until navActivity.BleCommand.CHANNEL_BLE.size){
                        var a=navActivity.BleCommand.CHANNEL_BLE[i]
                        if(a.substring(0, 2).equals("04")){
                            RRID = Getid(a)
                            var b = SensorRecord()
                            b.SensorID = RRID
                            b.IsSuccess = "true"
                            idrecord.add(b)
                        }
                        if(a.substring(0, 2).equals("03")){
                            RFID = Getid(a)
                            var b = SensorRecord()
                            b.SensorID = RFID
                            b.IsSuccess = "true"
                            idrecord.add(b)
                        }
                        if(a.substring(0, 2).equals("02")){
                            LRID = Getid(a)
                            var b = SensorRecord()
                            b.SensorID = LRID
                            b.IsSuccess = "true"
                            idrecord.add(b)
                        }
                        if(a.substring(0, 2).equals("01")){
                            LFID = Getid(a)
                            var b = SensorRecord()
                            b.SensorID = LFID
                            b.IsSuccess = "true"
                            idrecord.add(b)
                        }
                    }
                    for ( i in 0 until  navActivity.BleCommand.FALSE_CHANNEL.size){
                        var b= SensorRecord()
                        b.IsSuccess = "false"
                        b.SensorID = "error"
                        idrecord.add(b)
                    }
                    Upload_ProgramRecord(make,model,year,startime,endtime,PublicBean.SerialNum, "USBPad", "Program", idrecord.size, "ALL", idrecord,activity as KtActivity)
                }
                handler.post {
//                    navActivity.back.isClickable=true
//                    navActivity.back.setImageResource(R.mipmap.menu)
//                    navActivity.back.setOnClickListener {
//                        GoMenu()
//                    }
                    ISPROGRAMMING=false
                    try{
                        LFID=navActivity.resources.getString(R.string.Unlinked)
                        RFID=navActivity.resources.getString(R.string.Unlinked)
                        LRID=navActivity.resources.getString(R.string.Unlinked)
                        RRID=navActivity.resources.getString(R.string.Unlinked)
                        for(a in navActivity.BleCommand.CHANNEL_BLE){
                            if(a.substring(0,a.indexOf(".")).equals("04")){
                                RRID=a.substring(a.indexOf(".")+1,a.length)
                                if((mmyNum.equals("RN1628")||mmyNum.equals("SI2048"))){
                                    val Writetmp=RRID.substring(0,2)+"XX"+RRID.substring(4,6)+"YY"
                                    RRID=Writetmp.replace("XX",RRID.substring(6,8)).replace("YY",RRID.substring(2,4))
                                }
                                RRID=RRID.substring(Idcount)
                                UpdateUi(RR,PROGRAN_SUCCESS)
                            }
                            if(a.substring(0,a.indexOf(".")).equals("03")){
                                RFID=a.substring(a.indexOf(".")+1,a.length)
                                if((mmyNum.equals("RN1628")||mmyNum.equals("SI2048"))){
                                    val Writetmp=RFID.substring(0,2)+"XX"+RFID.substring(4,6)+"YY"
                                    RFID=Writetmp.replace("XX",RFID.substring(6,8)).replace("YY",RFID.substring(2,4))
                                }
                                RFID=RFID.substring(Idcount)
                                UpdateUi(RF,PROGRAN_SUCCESS)
                            }
                            if(a.substring(0,a.indexOf(".")).equals("02")){
                                LRID=a.substring(a.indexOf(".")+1,a.length)
                                if((mmyNum.equals("RN1628")||mmyNum.equals("SI2048"))){
                                    val Writetmp=LRID.substring(0,2)+"XX"+LRID.substring(4,6)+"YY"
                                    LRID=Writetmp.replace("XX",LRID.substring(6,8)).replace("YY",LRID.substring(2,4))
                                }
                                LRID=LRID.substring(Idcount)
                                UpdateUi(LR,PROGRAN_SUCCESS)
                            }
                            if(a.substring(0,a.indexOf(".")).equals("01")){
                                LFID=a.substring(a.indexOf(".")+1,a.length)
                                if((mmyNum.equals("RN1628")||mmyNum.equals("SI2048"))){
                                    val Writetmp=LFID.substring(0,2)+"XX"+LFID.substring(4,6)+"YY"
                                    LFID=Writetmp.replace("XX",LFID.substring(6,8)).replace("YY",LFID.substring(2,4))
                                }
                                LFID=LFID.substring(Idcount)
                                UpdateUi(LF,PROGRAN_SUCCESS)
                            }
                        }
                        UpdateUiCondition(PROGRAN_SUCCESS)
                        if (!condition)  {
                            for (a in navActivity.BleCommand.FALSE_CHANNEL) {
                                UpdateUiCondition(PROGRAN_FAULSE)
                                when(a){
                                    "04"->{
                                        RRID=navActivity.resources.getString(R.string.error)
                                        UpdateUi(RR,PROGRAN_FAULSE)
                                    }
                                    "03"->{
                                        RFID=navActivity.resources.getString(R.string.error)
                                        UpdateUi(RF,PROGRAN_FAULSE)
                                    }
                                    "02"->{
                                        LRID=navActivity.resources.getString(R.string.error)
                                        UpdateUi(LR,PROGRAN_FAULSE)
                                    }
                                    "01"->{
                                        LFID=navActivity.resources.getString(R.string.error)
                                        UpdateUi(LF,PROGRAN_FAULSE)
                                    }
                                }
                            }
                            for (a in navActivity.BleCommand.BLANK_CHANNEL) {
                                when(a){
                                    "04"->{
                                        UpdateUi(RR,UNLINK)
                                    }
                                    "03"->{
                                        UpdateUi(RF,UNLINK)
                                    }
                                    "02"->{
                                        UpdateUi(LR,UNLINK)
                                    }
                                    "01"->{
                                        UpdateUi(LF,UNLINK)
                                    }
                                }
                            }
                            if(navActivity.BleCommand.FALSE_CHANNEL.size==0&&navActivity.BleCommand.BLANK_CHANNEL.size==0){
                                UpdateUiCondition(PROGRAN_FAULSE)
                                UpdateUi(LF,PROGRAN_FAULSE)
                                UpdateUi(LR,PROGRAN_FAULSE)
                                UpdateUi(RF,PROGRAN_FAULSE)
                                UpdateUi(RR,PROGRAN_FAULSE)
                            }
                        }
                    }catch (e: Exception){e.printStackTrace()}
                }
            }).start()


        }

    }

}
