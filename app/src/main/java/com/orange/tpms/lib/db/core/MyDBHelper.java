package com.orange.tpms.lib.db.core;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBHelper extends SQLiteOpenHelper {
	
	protected static final String ACTIVITY_TAG="MyDBHelper";
	
	private String[] TABLE;
	
	public MyDBHelper(Context context, String name, CursorFactory factory,
			int version, String[] table) {
		super(context, name, factory, version);
		this.TABLE = table;
		Log.v(ACTIVITY_TAG, name);
	}
	
	/**
	 * @function: 当数据库需要被更新的时候执行,例如删除永久表,创建新表
	 * 			     当打开数据库时传入的版本号与当前的版本号不同时会调用方法(non-Javadoc)
	 * @see SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.v(ACTIVITY_TAG, "onUpgrade");
		createTable (db);		// 创建新表
	}

	/**
	 * @function: 当数据库首次创建时执行该方法,一般将创建表等初始化操作放在本方法中执行(non-Javadoc)
	 * @see SQLiteOpenHelper#onCreate(SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.v(ACTIVITY_TAG, "onCreate");
		
		createTable (db);		// 创建新表
	}

	// 创建表
	private void createTable (SQLiteDatabase db) {
		for (int i=0;i<TABLE.length;i++) {
			if (i>0 && (1 == i%2)) {  // 创建表
				Log.v(ACTIVITY_TAG, ""+i);
				String sql = "create table if not exists "+TABLE[i-1]+TABLE[i];
				Log.v(ACTIVITY_TAG, sql);
				db.execSQL(sql);
			}
		}
	}
}
