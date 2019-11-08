package com.orange.tpms.lib.db.core;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;
/*
 * @class: Controller
 * @purpose: 本App架构中将Model定义为, 有数据传输过程的数据源，以Model的形式统一起来，数据传输统一用Handler发送到Controller处理
 * 			 Model可以有多种,如网络Model, 数据库Model, 蓝牙Model, 但是最终统一封装起来为了用于某一功能和数据需求的只有Controller
 * 			 一般我们对数据源的使用都是间接通过经过处理的Controller来使用，这样可以达到适配多种数据源的目的。
 */
public abstract class Controller implements ControllerFunc {
	
	private static final String ACTIVITY_TAG="Controller";   // 日志
	
	// 状态码定义,范围为-1000~-2000,1000~2000(包括其子类)
	public static final int SUCCESS = 1000;
	public static final int EMPTY = -1000;
	public static final int EXCEPTION = -1001;
	
	// 网络model定义
	private ModelFunc responser;
	
	// 接收网络数据
	private Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg){
			
			Log.v(Controller.ACTIVITY_TAG, "网络状态码:"+msg.what);
			
			if (msg.what == ModelFunc.SUCCESS){  // 当网络数据记录不为空时执行
				handleMsg(msg);
			} else if (msg.what == ModelFunc.EMPTY || 
					   msg.what == 257){  // 当网络数据记录为空时执行
				handleNullMsg(msg);
			} else {  // 服务器或网络异常
				handleException(msg);
			}
		}
	};
	
	/*
	 * @function: 设置responser
	 */
	public void setModel (ModelFunc responser) {
		this.responser = (ModelFunc)responser;
	}
	
	public void start(final String Url, final String baseFile, final List<BasicNameValuePair> params, final Context context){
		
		new Thread(){
		
			@Override
			public void run(){
				
				Log.v(Controller.ACTIVITY_TAG, "Launch new Thread.");
				relateModel(responser, context, Url, baseFile, params);
			}
		}.start();
		
	}
	
	/*
	 * @function: 绑定相关model(Json.java)
	 */
	protected void relateModel(ModelFunc responser, final Context context, final String Url, final String baseFile, List<BasicNameValuePair> params) {
		
		// TODO Auto-generated method stub
		responser.setHandler(handler);
		responser.setUrl(Url, baseFile);    // 设置访问网址时
		responser.setContext(context);
		
		try{
			responser.loadGet(handler, params);
			//Thread.sleep(1000);
		} catch (Exception e) {
			Log.v(Controller.ACTIVITY_TAG, "IOException");
		}
	}
	
}
