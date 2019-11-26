package com.orange.tpms.mmySql

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.orange.blelibrary.blelibrary.BleActivity
import com.orange.tpms.R
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.ue.kt_frag.*
import com.orange.tpms.utils.FileDowload
import java.util.*

class ItemDAO(context: Activity) {
    companion object {
        val TAG = "ItemDAO"
        // 編號表格欄位名稱，固定不變
        val KEY_ID = "_id"
        val notin="'NA'"
        // 其它表格欄位名稱
        val MAKE_COLUMN = "Make"
        val MODEL_COLUMN = "Model"
        val YEAR_COLUMN = "Year"
        val MAKE_IMG_COLUMN = "Make_Img"
    }

    //資料庫物件
    private var dbHelper = DatabaseHelper(context)
    private lateinit var db: SQLiteDatabase

    init {
        dbHelper.createDataBase()
        if(dbHelper.checkDataBase()){
            Log.d(TAG, "checkDataBase: true")
            dbHelper.openDataBase()
            db = dbHelper.db}else{
            Log.d(TAG, "checkDataBase: false")
            context.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().clear().commit()
            context.getSharedPreferences("Favorite", Context.MODE_PRIVATE).edit().clear().commit()
            context.finish()
            val intent2 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName())
            context.startActivity(intent2)
        }
    }


    // 關閉資料庫，一般的應用都不需要修改
    fun close() {
        dbHelper.close()
    }
    var favorite= ArrayList<String>()
    fun AddFavorite(act:Activity){
        GetFav(act)
        if(!favorite.contains("${PublicBean.SelectMake}☆${PublicBean.SelectModel}☆${PublicBean.SelectYear}")){ favorite.add("${PublicBean.SelectMake}☆${PublicBean.SelectModel}☆${PublicBean.SelectYear}")}
        SetFav(act)
    }
    fun GetFav(act:Activity){
        favorite.clear()
        val profilePreferences = act.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        val a= profilePreferences.getInt("count",0)
        for(i in 0 until a){
            var tmpdata=profilePreferences.getString("$i","nodata")
            if (!tmpdata.equals("nodata")){   favorite.add(tmpdata)}
        }
    }
    fun SetFav(act:Activity){
        val profilePreferences = act.getSharedPreferences("Favorite", Context.MODE_PRIVATE)
        profilePreferences.edit().putInt("count",favorite.size).commit()
        for(i in 0 until favorite.size){
            profilePreferences.edit().putString("$i",favorite[i]).commit()
        }
    }
    fun GetreLarm(make:String,model:String,year:String,act:Context):String{
        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
        val a= profilePreferences.getString("Language","English")
        var colname="English"
        when(a){
            "繁體中文"->{ colname="`Relearn Procedure (Traditional Chinese)`"}
            "简体中文"->{ colname="`Relearn Procedure (Jane)`"}
            "Deutsch"->{ colname="`Relearn Procedure (German)`"}
            "English"->{ colname="`Relearn Procedure (English)`"}
            "Italiano"->{ colname="`Relearn Procedure (Italian)`"}
        }
        val result = db.rawQuery(
            "select $colname from `Summary table` where make='$make' and model='$model' and year='$year' limit 0,1",
            null)

        if(result.count > 0 ){
            result.moveToFirst()
            if(result.getString(0).isEmpty()){  return act.resources.getString(R.string.norelarm)}else{  return result.getString(0)}

        }else{
            result.close()
            return act.resources.getString(R.string.norelarm)
        }
    }
fun GoOk(code:String,navigationActivity: BleActivity){
    val sql="select `Make`,`Model`,`Year`,`Make_Img` from `Summary table` where `Direct Fit` not in($notin) and `$MAKE_IMG_COLUMN` not in($notin) and `MMY number`='$code' limit 0,1"
    val result = db.rawQuery(
        sql,null)
    if(result.count > 0){
        result.moveToFirst()
        do{
            PublicBean.SelectMake=result.getString(0)
            PublicBean.SelectModel=result.getString(1)
            PublicBean.SelectYear=result.getString(2)
            AddFavorite(navigationActivity)
            when(PublicBean.position){
                PublicBean.檢查傳感器->{
                    navigationActivity.ChangePage(Frag_Check_Sensor_Information(),R.id.frage,"Frag_Check_Sensor_Information",true);
                }
                PublicBean.燒錄傳感器->{
                    navigationActivity.ChangePage(Frag_Program_Number_Choice(),R.id.frage,"Frag_Program_Number_Choice",true);
                }
                PublicBean.複製傳感器->{
                    navigationActivity.ChangePage(Frag_Idcopy_original(),R.id.frage,"Frag_Idcopy_original",true);
                }
                PublicBean.學碼步驟->{
                    navigationActivity.ChangePage(Frag_Relearm_Detail(),R.id.frage,"Frag_Relearm_Detail",true);
                }
                PublicBean.PAD_PROGRAM->{
                    navigationActivity.ChangePage(Frag_Pad_Program_Detail(), R.id.frage,"Frag_Pad_Program_Detail",true);
                }
                PublicBean.PAD_COPY->{
                    navigationActivity.ChangePage(Frag_Pad_Keyin(), R.id.frage,"Frag_Pad_Keyin",true);
                }
                PublicBean.ID_COPY_OBD->{
                    navigationActivity.ChangePage(Frag_Obd_Copy_Detail(), R.id.frage,"Frag_Obd_Copy_Detail",true);
                }
                PublicBean.OBD_RELEARM->{
                    navigationActivity.ChangePage(Frag_Obd_Copy_Detail(), R.id.frage,"Frag_Obd_Copy_Detail",true);
                }
            }
        }while (result.moveToNext())
        // 關閉Cursor物件
        result.close()
    }else{
        result.close()
    }
}
    fun getMakeString(): ArrayList<String>?{
        val makes = arrayListOf<String>()

        val result = db.rawQuery(
            "select distinct $MAKE_COLUMN from `Summary table` order by $MAKE_COLUMN asc",null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                makes.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return makes
        }else{
            result.close()
            return null
        }
    }
    fun getobdmake(): ArrayList<Item>{
        val makes = arrayListOf<Item>()
        val result = db.rawQuery(
            "select distinct `Make`,`Make_img` from `Summary table` where `Make` IS NOT NULL and `Make_img` not in('NA') and `OBD1` not in('NA') order by `Make` asc",null)
        if(result.count > 0){
            result.moveToFirst()
            do{

                val module = Item()
                module.make = result.getString(0)
                module.makeImg=result.getString(1)
                Log.d("make",result.getString(0));
                Log.d("image",result.getString(1));
                makes.add(module)
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return makes
        }else{
            result.close()
            return arrayListOf<Item>()
        }
    }
    fun getMake(act:Activity): ArrayList<Item>{
        try{
            val makes = arrayListOf<Item>()
            var sql="select distinct $MAKE_COLUMN , $MAKE_IMG_COLUMN from `Summary table` where `Direct Fit` not in($notin) and `$MAKE_COLUMN`  IS NOT NULL and `$MAKE_IMG_COLUMN` not in('NA') order by $MAKE_COLUMN asc"
            if(!FileDowload.Internet){sql="select distinct $MAKE_COLUMN , $MAKE_IMG_COLUMN from `Summary table` where `Direct Fit` not in($notin) and `$MAKE_COLUMN`  IS NOT NULL and `$MAKE_IMG_COLUMN` not in('NA') order by $MAKE_COLUMN asc limit 0,1"}
            val result = db.rawQuery(
                sql,null)
            if(result.count > 0){
                result.moveToFirst()
                do{

                    val item = Item()
                    item.make = result.getString(0)
                    item.makeImg = result.getString(1)
                    Log.d("makeImg",item.makeImg)
                    makes.add(item)
                }while (result.moveToNext())
                // 關閉Cursor物件
                result.close()
                // 回傳結果
                return makes
            }else{
                result.close()
                return arrayListOf<Item>()
            }
        }catch (e:Exception){act.getSharedPreferences("Setting", Context.MODE_PRIVATE).edit().putString("mmyname","").commit();return arrayListOf<Item>()}
    }
    fun getobdmodel(name:String): ArrayList<String>{
        val models = arrayListOf<String>()
        val result = db.rawQuery(
            "select distinct model from `Summary table` where make='$name' and `OBD1` not in('NA') order by model asc",null)
        if(result.count > 0){
            result.moveToFirst()
            do{
                models.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return models
        }else{
            result.close()
            return arrayListOf<String>()
        }
    }
    fun getModel(make: String): ArrayList<String>{
        val models = arrayListOf<String>()
        val result = db. rawQuery(
            "select distinct $MODEL_COLUMN from `Summary table` where $MAKE_COLUMN = '$make' and  `Direct Fit` not in($notin) order by $MODEL_COLUMN asc",
            null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                models.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return models
        }else{
            result.close()
            return arrayListOf<String>()
        }
    }

    fun getIdModel(make: String): ArrayList<String>?{
        val models = arrayListOf<String>()

        val result = db. rawQuery(
            "select distinct $MODEL_COLUMN from `Summary table`,idcopy where $MAKE_COLUMN = '$make' and `Summary table`.`Direct Fit` in(`idcopy`.`s19`) order by $MODEL_COLUMN asc",
            null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                models.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return models
        }else{
            result.close()
            return null
        }
    }
    fun getIdYear(make: String,model: String): ArrayList<String>?{
        val years = arrayListOf<String>()

        val result = db. rawQuery(
            "select distinct $YEAR_COLUMN from `Summary table`,idcopy where $MODEL_COLUMN = '$model' and $MAKE_COLUMN = '$make' and `Summary table`.`Direct Fit`in(`idcopy`.`s19`) order by $YEAR_COLUMN asc",
            null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                years.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return years
        }else{
            result.close()
            return null
        }
    }
    fun getLf(s19:String): String?{
        val result = db. rawQuery(
            "select `Lf` from `Summary table` where `Direct Fit`='$s19' order by $YEAR_COLUMN asc limit 0,1",
            null)
        if(result.count > 0){
            result.moveToFirst()
                val a=result.getString(0)
                result.close()
               return a
        }else{
            result.close()
            return "0"
        }
    }
    fun getYear(make: String,model: String): ArrayList<String>{
        val years = arrayListOf<String>()

        val result = db. rawQuery(
            "select distinct $YEAR_COLUMN from `Summary table` where $MODEL_COLUMN = '$model' and $MAKE_COLUMN = '$make' and  `Direct Fit` not in($notin) order by $YEAR_COLUMN asc",
            null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                years.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return years
        }else{
            result.close()
            return arrayListOf<String>()
        }
    }
    fun IsFiveTire(name:String):Boolean{
        val result = db.rawQuery("select `Wheel_Count` from `Summary table` where `OBD1`='$name' and `Wheel_Count` not in('NA') limit 0,1", null)
        if(result.count > 0 ){
            result.moveToFirst()
            do{
                val count=result.getString(0)
                return if (count.contains("5")) true else false
            }while (result.moveToNext())
        }else{
            result.close()
            return false
        }
    }

    fun  getPart(make:String,model:String,year:String):String{
        var data="";
        val result = db. rawQuery(
            "select `OBD1` from `Summary table` where Make='$make' and Model='$model' and year='$year' and `OBD1` not in('NA') limit 0,1",
            null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                data= result.getString(0)
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return data
        }else{
            result.close()
            return ""
        }
    }
    fun getObdYear(make: String,model: String): ArrayList<String>{
        val years = arrayListOf<String>()

        val result = db. rawQuery(
            "select distinct Year from `Summary table` where model='$model' and make='$make' and `OBD1` not in('NA') order by Year asc",
            null)

        if(result.count > 0){
            result.moveToFirst()
            do{
                years.add(result.getString(0))
            }while (result.moveToNext())
            // 關閉Cursor物件
            result.close()
            // 回傳結果
            return years
        }else{
            result.close()
            return arrayListOf<String>()
        }
    }
    fun getS19(s19: String): Boolean{
        val result = db.rawQuery(
            "select count(1) from `Summary table` " +
                    "where `Direct Fit`='$s19'",
            null)

        if(result.count > 0 ){
            result.moveToFirst()
          val a= result.getInt(0)
           return a>0
        }else{
            result.close()
            return false
        }
    }
    fun getMMY(make: String, model: String, year:String): String{
        val result = db.rawQuery(
            "select  `Direct Fit` from `Summary table` " +
                    "where $MAKE_COLUMN = '$make' " +
                    "and $MODEL_COLUMN = '$model' " +
                    "and $YEAR_COLUMN = '$year' and  `Direct Fit` not in('NA') limit 0,1",
            null)

        if(result.count > 0 ){
            result.moveToFirst()
            return result.getString(0)
        }else{
            result.close()
            return ""
        }
    }
    fun GetLf(make: String, model: String, year:String): String{
        val result = db.rawQuery(
            "select  `LF Power` from `Summary table` " +
                    "where $MAKE_COLUMN = '$make' " +
                    "and $MODEL_COLUMN = '$model' " +
                    "and $YEAR_COLUMN = '$year'  limit 0,1",
            null)
        if(result.count > 0 ){
            result.moveToFirst()
            return result.getString(0)
        }else{
            result.close()
            return ""
        }
    }
    fun GetHex(make: String, model: String, year:String): String{
        val result = db.rawQuery(
            "select  `OBD Product No. (hex)` from `Summary table` " +
                    "where $MAKE_COLUMN = '$make' " +
                    "and $MODEL_COLUMN = '$model' " +
                    "and $YEAR_COLUMN = '$year'  limit 0,1",
            null)

        if(result.count > 0 ){
            result.moveToFirst()
            return result.getString(0)
        }else{
            result.close()
            return ""
        }
    }
    // 把Cursor目前的資料包裝為物件
    fun getRecord(cursor: Cursor): Item {
        // 準備回傳結果用的物件
        val result = Item()

        //result.id = cursor.getLong(0)
        result.make = cursor.getString(0)
        result.model =cursor.getString(1)
        result.year = cursor.getString(2)
        result.orangePart = cursor.getString(3)
        result.makeImg = cursor.getString(4)

        // 回傳結果
        return result
    }
    fun GetCopyId(s19:String):Int{
        val result = db.rawQuery("select `ID_Count` from `Summary table` where `Direct Fit`='$s19' and `$MAKE_IMG_COLUMN` not in('NA') limit 0,1", null)
        if(result.count > 0 ){
            result.moveToFirst()
            do{
                val TmpWrite=result.getString(0)
                Log.d("Idcount",TmpWrite)
                return TmpWrite.toInt()
            }while (result.moveToNext())

        }else{
            result.close()
            return 8
        }
    }

//    fun GetreLarm(make:String,model:String,year:String,act:Context):String{
//        val profilePreferences = act.getSharedPreferences("Setting", Context.MODE_PRIVATE)
//        val a= profilePreferences.getString("Language","English")
//        var colname="English"
//        when(a){
//            "繁體中文"->{ colname="`Relearn Procedure (Traditional Chinese)`"}
//            "简体中文"->{ colname="`Relearn Procedure (Jane)`"}
//            "Deutsch"->{ colname="`Relearn Procedure (German)`"}
//            "English"->{ colname="`Relearn Procedure (English)`"}
//            "Italiano"->{ colname="`Relearn Procedure (Italian)`"}
//        }
//        val result = db.rawQuery(
//            "select $colname from `Summary table` where make='$make' and model='$model' and year='$year' limit 0,1",
//            null)
//
//        if(result.count > 0 ){
//            result.moveToFirst()
//            if(result.getString(0).isEmpty()){  return act.resources.getString(R.string.norelarm)}else{  return result.getString(0)}
//
//        }else{
//            result.close()
//            return act.resources.getString(R.string.norelarm)
//        }
//    }
}