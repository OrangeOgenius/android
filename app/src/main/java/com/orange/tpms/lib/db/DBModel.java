package com.orange.tpms.lib.db;

/*
 * @class: 数据库操作基类(单例)
 * @statement: 数据查询和插入数据等操作有两种方式
 * 				1、异步执行,通过getData返回,以对象重写的方式执行
 * 				2、同步执行,直接返回,以类静态函数的形式执行
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.orange.tpms.lib.db.core.BasicNameValuePair;
import com.orange.tpms.lib.db.core.ModelFunc;
import com.orange.tpms.lib.db.core.MyDBHelper;
import com.orange.tpms.lib.db.core.Objectable;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ZZQ
 * @copyright Copyright (c) 2015 Shuanghua.Co.Ltd. All rights reserved.
 * @version 2015-11-4 上午9:54:08
 */
public abstract class DBModel implements ModelFunc, Objectable {

	private static final String ACTIVITY_TAG="DBModel";
	
	// 操作类型
	public static final String ACTION = "dbmodelaction";
	public static final String INSERT = "insert";  // 插入操作
	public static final String QUERY = "query";  // 查询操作
	public static final String DELETE = "delete";  // 删除操作
	public static final String EXECUTE = "execute"; // 执行操作
	public static final String UNKNOWN = "unknown"; // 未知操作
	
	// 执行语句
	public static final String SQL = "sql";
	
	protected Handler handler = null;
	SQLiteOpenHelper sqlHelper = null;
	
	// 数据库path
	private String path = "schema.db";
	
	// 数据库表名(每个继承者必须重写这tableName,否则按默认值)
	public String tableName = "";
	
	// 数据库context
	private Context context = null;
	
	// 数据库版本
	private int version = 0;
	
	// 数据库记录ID
	public static final String ID = "id";
	
	public String[] TABLE;
	
	public DBModel (String path, int version, String[] table) {
		this.path = path;
		this.version = version;
		this.TABLE = table;
		Log.v(ACTIVITY_TAG, "DBModel");
		this.tableName = getTableName ();
	}
	
	/**
	 * 设定表名
	 */
	abstract protected String getTableName ();
	
	/**
	 * 获取dbHelper(若未创建则创建)
	 */
	synchronized public SQLiteOpenHelper getHelper (Context context, String path) {
		if (this.sqlHelper == null) {  // sqlHelper为空则新建
			this.context = context;
			this.path = path;
			sqlHelper = newHelper();
		}
		return sqlHelper;
	}
	
	/**
	 * 获取dbHelper(若未创建则创建)
	 */
	synchronized public SQLiteOpenHelper getHelper() {
		if (this.sqlHelper == null) {
			sqlHelper = newHelper();
		}
		Log.v(ACTIVITY_TAG, "getHelper");
		
		return sqlHelper;
	}
	
	/**
	 * 新建一个数据库句柄(原先的句柄被关闭)
	 */
	synchronized private SQLiteOpenHelper newHelper () {
		Log.v(ACTIVITY_TAG, "newHelper");
		try {
			// 关闭数据库句柄重新新建返回
			if (this.sqlHelper != null){
				this.sqlHelper.close();
			}
			Log.v(ACTIVITY_TAG, "new SQLiteOpenHelper");
			this.sqlHelper = new MyDBHelper(this.context, this.path, null, this.version, TABLE);
			
			Log.v(ACTIVITY_TAG, "new SQLiteOpenHelper");
		} catch (Exception e) {
			this.onError(null, handler);
			return null;
		}
		return this.sqlHelper;
	}

	/**
	 * 关闭数据库
	 */
	synchronized public void close () {
		if (this.sqlHelper != null) {
			this.sqlHelper.close();
		}
	}
	
	/**
	 * 设置handler与ui线程通信
	 */
	synchronized public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	/**
	 * 数据库不存在和未建立导致的错误(non-Javadoc)
	 */
	synchronized public int onError(Object response, Handler handler) {
		// TODO Auto-generated method stub
		String result = "Db error";
		Message msg = new Message();
		Bundle b = new Bundle();
		
		b.putString(MSG, result);
		msg.what = ERR_SERVER;
		msg.setData(b);
		handler.sendMessage(msg);
		return 0;
	}

	/**
	 * 连接数据库成功但是出现字段解析识别异常(non-Javadoc)
	 */
	synchronized public int onException(Object response, Handler handler) {
		// TODO Auto-generated method stub
		String result = "check for params or Sql";
		Message msg = new Message();
		Bundle b = new Bundle();
		
		b.putString(MSG, result);
		msg.what = EXCEPTION;
		msg.setData(b);
		handler.sendMessage(msg);
		return 0;
	}

	public int onSuccess(Object response, Handler handler) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setUrl(String dbName, String tableName) {
		// TODO Auto-generated method stub
		if (dbName != "" && dbName != null) {
			this.path = dbName;
		}

		if (tableName != "" && tableName != null) {
			this.tableName = tableName;
		}
		return ;
	}
	
	public void setContext(Context context) {
		// TODO Auto-generated method stub
		this.context = context;
	}
	
	/**
	 */
	synchronized public int loadGet(Handler handler, List<BasicNameValuePair> params) {
		// TODO Auto-generated method stub
		// 获取数据库操作句柄
		SQLiteOpenHelper dbHelper = this.getHelper();
		List<BasicNameValuePair> myParams = new ArrayList<BasicNameValuePair>();
		String action = UNKNOWN;

		// 获取action字段,判断是插入还是查询操作
		for (int i=0;i<params.size();i++){
			if (params.get(i).getName().equals(ACTION)) {  // 不当参数
				if (params.get(i).getValue().equals(INSERT)) {
					// 执行插入操作
					action = INSERT;
				} else if (params.get(i).getValue().equals(QUERY)) {
					// 执行更新操作
					action = QUERY;
				} else if (params.get(i).getValue().equals(DELETE)) {
					// 执行删除操作
					action = DELETE;
				} else if (params.get(i).getValue().equals(EXECUTE)) {
					// 执行操作
					action = EXECUTE;
				} else {	action = UNKNOWN; 	continue;	}
			} else {  // 作为下一步参数
				myParams.add(new BasicNameValuePair(params.get(i).getName(), params.get(i).getValue()));
			}
		}
		if (action.equals(INSERT)) {  // 执行插入操作
			try {
				insert (dbHelper, myParams, handler);
			} catch (Exception e) {
				onException (INSERT, handler);
			}
		} else if (action.equals(QUERY)) {  // 执行查询操作
			try {
				query (dbHelper, myParams, handler);
			} catch (Exception e) {
				onException (QUERY, handler);
			}
		} else if (action.equals(DELETE)) {
			try {
				delete (dbHelper, myParams, handler);
			} catch (Exception e) {
				onException (DELETE, handler);
			}
		} else if (action.equals(EXECUTE)) {
			try {
				execute(dbHelper, myParams, handler);
			} catch (Exception e) {
				onException (EXECUTE, handler);
			}
		} else {  // 参数出现错误
			onException(null, handler);
		}
		dbHelper.close();
		return 0;
	}
	
	/**
	 * 执行插入操作
	 */
	synchronized public int insert(SQLiteOpenHelper dbHelper,
			List<BasicNameValuePair> params, Handler handler) throws JSONException {
		// TODO Auto-generated method stub
		Log.v("SQL", "insert");
		execute(dbHelper, params, handler);
		return 0;
	}
	
	/**
	 * 执行查询操作(non-Javadoc)
	 */
	synchronized public int query(SQLiteOpenHelper dbHelper,
			List<BasicNameValuePair> params, Handler handler) throws JSONException{
		// TODO Auto-generated method stub
		Message msg = null;
		try {
			msg = Query(dbHelper, params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler.sendMessage(msg);  // 发送
		return 0;
	}
	
	/**
	 * 执行删除操作(non-Javadoc)
	 */
	synchronized public int delete(SQLiteOpenHelper dbHelper,
			List<BasicNameValuePair> params, Handler handler) throws JSONException{
		// TODO Auto-generated method stub
		Log.v("SQL", "delete");
		execute(dbHelper, params, handler);
		return 0;
	}

	synchronized public int execute(SQLiteOpenHelper dbHelper,
									List<BasicNameValuePair> params, Handler handler)
			throws JSONException {
		
		Message msg = Execute(dbHelper, params);
		handler.sendMessage(msg);  // 发送
		
		return 0;
	}
	
	synchronized public static final Message Execute(SQLiteOpenHelper dbHelper,
			List<BasicNameValuePair> params) {
		
		// TODO Auto-generated method stub
		String sql = "";
		for (int i=0;i<params.size();i++) {
			if (params.get(i).getName().equals(SQL)) {
				sql = (String) params.get(i).getValue();
				Log.v("SQL", sql);
				if (sql == null || sql.equals("")) {
					continue;
				} else {
					dbHelper.getReadableDatabase().execSQL(sql);
				}
			}
		}
		Message msg = new Message();
		Bundle b = new Bundle();
		
		if (sql != "") {
			b.putString(MSG, "success");
			b.putBoolean(IFSUCCESS, true);
			b.putBundle(DATA, null);
		} else {
			b.putString(MSG, "fail");
			b.putBoolean(IFSUCCESS, false);
			b.putBundle(DATA, null);
		}

		msg.what = SUCCESS;
		msg.setData(b);
		return msg;
	}
	
	/**
	 * 获取数据库句柄
	 */
	synchronized public Object getObject() {
		return sqlHelper;
	}

	synchronized public void closeObject() {
		// TODO Auto-generated method stub
		sqlHelper.close();
	}

	/**
	 * 使用getObject前需先openObjet开启数据库
	 */
	synchronized public void openObject(Context context, String path) {
		// TODO Auto-generated method stub
		// 少了这条语句会报错
		this.setContext(context);
		this.setUrl("", path);
		sqlHelper = this.getHelper();
	}
	
	synchronized public static final Message Query(SQLiteOpenHelper dbHelper,
			List<BasicNameValuePair> params) {
		// TODO Auto-generated method stub
		// Log.v("SQL", "query");
		String sql = "";
		for (int i=0;i<params.size();i++) {
			if (params.get(i).getName().equals(SQL)) {
				sql = (String) params.get(i).getValue();
				break;
			}
		}
		Message msg = new Message();
		Bundle b = new Bundle();
		Log.v("SQL", sql);
		
		if (sql != ""){
			Cursor cursor = null;
			
			SQLiteDatabase sqldb = dbHelper.getReadableDatabase();
			
			try {
				cursor = sqldb.rawQuery(sql, null);
				Bundle dataBundle = new Bundle();
				
				String[] columnNames = cursor.getColumnNames();
				
				String[][] columnValues = new String[cursor.getColumnCount()][cursor.getCount()];
				// 保存全部属性数组
				int cols = 0;  // 表示列
				int rows = 0;  // 表示行
				while (cursor.moveToNext()) {
					for (cols=0;cols<cursor.getColumnCount();cols++) {			// 整一行保存
						columnValues[cursor.getColumnIndex(columnNames[cols])][rows] = 
							cursor.getString(cursor.getColumnIndex(columnNames[cols]));
					}
					rows++;
				}
				// 保存全部数组到dataBundle里
				for (int i=0;i<cursor.getColumnCount();i++) {
					//DebugTools.v("SQL", columnNames[i]);
					dataBundle.putStringArray(columnNames[i], columnValues[i]);
				}
				b.putString(MSG, "success");
				b.putBoolean(IFSUCCESS, true);
				b.putBundle(DATA, dataBundle);
			} catch (Exception e) {
				
				e.printStackTrace();
				b.putString(MSG, "exception");
				b.putBoolean(IFSUCCESS, false);
				b.putBundle(DATA, null);
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
		} else {
			b.putString(MSG, "lack sql");
			b.putBoolean(IFSUCCESS, false);
			b.putBundle(DATA, null);
		}
		msg.what = SUCCESS;
		msg.setData(b);
		return msg;
	}
	
}
