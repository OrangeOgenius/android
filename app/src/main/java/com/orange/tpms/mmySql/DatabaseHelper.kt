package com.orange.tpms.mmySql

import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException
import android.database.sqlite.SQLiteException
import android.util.Log
import java.io.File


class DatabaseHelper(private val mContext: Context) : SQLiteOpenHelper(mContext, DB_NAME, null, 1) {
    companion object {
        internal var DB_NAME = "usb_tx_mmy.db"
    }
    var DB_PATH = mContext.getDatabasePath(DB_NAME)

    lateinit var db: SQLiteDatabase

    override fun onCreate(db: SQLiteDatabase) {
        // TODO Auto-generated method stub

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO Auto-generated method stub

    }

    /**
     * This method will create database in application package /databases
     * directory when first time application launched
     */
    @Throws(IOException::class)
    fun createDataBase() {
        try {
//            copyDataBase()
        } catch (mIOException: IOException) {
            mIOException.printStackTrace()
            throw Error("Error copying database")
        }
        /*
        val mDataBaseExist = checkDataBase()
        if (!mDataBaseExist) {
            this.readableDatabase
            try {
                copyDataBase()
            } catch (mIOException: IOException) {
                mIOException.printStackTrace()
                throw Error("Error copying database")
            }
        }*/
    }

    /** This method checks whether database is exists or not  */
    fun checkDataBase(): Boolean {
        try {
            val mPath = "$DB_PATH"
            val file = File(mPath)
            return file.exists()
        } catch (e: SQLiteException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * This method will copy database from /assets directory to application
     * package /databases directory
     */
//    @Throws(IOException::class)
//    private fun copyDataBase() {
//        try {
//            val mInputStream = mContext.assets.open(DB_NAME)
//            val outFileName = "$DB_PATH"
//            var file=File("$DB_PATH".replace(DB_NAME,""))
//            file.mkdirs()
//            Log.d("file",file.path)
//            val mOutputStream = FileOutputStream(outFileName)
//            val buffer = ByteArray(1024)
//            var count = 0
//            while (count != -1) {
//                if(count !=0){
//                    mOutputStream.write(buffer, 0, count)
//                }
//                count = mInputStream.read(buffer, 0, buffer.size)
//            }
//            mOutputStream.flush()
//            mOutputStream.close()
//            mInputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }

    /** This method open database for operations  */
    @Throws(SQLException::class)
    fun openDataBase(): Boolean {
        val mPath = "$DB_PATH"
        db = SQLiteDatabase.openDatabase(
            mPath, null,
            SQLiteDatabase.OPEN_READONLY
        )
        return db.isOpen
    }

    /** This method close database connection and released occupied memory  */
    @Synchronized
    override fun close() {
        db.close()
        SQLiteDatabase.releaseMemory()
        super.close()
    }

}