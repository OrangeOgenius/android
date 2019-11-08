package com.orange.tpms.lib.db.core;

import android.content.Context;
import android.os.Handler;

import java.util.List;
/*
 * @class: Model,主要有三类方法集,分别为
 * 			1、编辑handler
 * 			2、用户请求调用
 * 			3、设置方法
 */
public interface ModelFunc {
	
	// Model专用常量
	public static final int EMPTY = -5;
	public static final int SUCCESS = 0x00;

	public static final String MSG = "msg";
	public static final String IFSUCCESS = "success";
	public static final String DATA = "data";
	public static final String NUM = "num";
	public static final String LIST = "list";
	
	/*
	 * @function: 网络专用状态码常量
	 * 数字-1~ -50, 0~50为网络专用状态码范围
	 */
	public static final int NOFOUND = -1;
	public static final int ERR_SERVER = -2;
	public static final int EXCEPTION = -3;
	public static final int JSONEXCEPTION = -4;

	/*
	 * @function: 将服务器响应处理后传递给主线程handler
	 * @response: 服务器响应
	 * @handler: 主线程handler
	 * @notice: 这几个接口其实非必需，但是为了让后面维护的人对独立出接口处理异常和错误有个认识，可供参考
	 * @tip: 编辑handler
	 */
	abstract int onSuccess(Object obj, Handler handler);
	abstract int onError(Object obj, Handler handler);
	abstract int onException(Object obj, Handler handler);
	
	/*
	 * @function: 设置handler与ui线程通信
	 * @handler: Handler
	 * @tip: 设置方法
	 */
	abstract public void setHandler(Handler handler);
	
	/*
	 * @function: 设置目的地址
	 * @statement: 对于数据库Model,url指数据库名,baseFile指表名
	 * 			      对于网络Model,url和baseFile分别指网址根路径和相对路径，也可指使用url直接指资源路径
	 * @tip: 设置方法
	 */
	abstract public void setUrl(String url, String baseFile);
	
	/*
	 * @function: 设置Context
	 * @tip: 设置方法
	 */
	abstract public void setContext(Context context);
	
	/*
	 * @function: 设置handler和参数
	 * @statement: 对于数据库Model, params指需要传递给数据库的参数
	 * 			   handler指用来接收数据的handler
	 * @tip: 外部请求调用
	 */
	abstract public int loadGet(Handler handler, List<BasicNameValuePair> params);
}

