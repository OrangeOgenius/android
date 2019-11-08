package com.orange.tpms.lib.db.core;

import android.os.Message;

public interface ControllerFunc {
	/*
	 * @function: 当json解析出现异常时执行
	 */
	public void handleException(Message msg);
	
	/*
	 * @function: 当记录条数为0时执行
	 */
	public void handleNullMsg(Message msg); 
	
	/*
	 * @function: 当记录条数不为0时执行
	 */
	public void handleMsg(Message msg);  // 处理model发来的msg
	
	/*
	 * @function: 将msg中数据转换成ListItemClass的形式
	 * @msg: 为网络数据
	 * @state: 用来表示网络状态(是否异常,成功获取数据等)
	 */
	public int getData(Object[] obj, int state);
	
	/*
	 * @function: 设置Model-Controller联系
	 */
	public void setModel(ModelFunc obj);
}
