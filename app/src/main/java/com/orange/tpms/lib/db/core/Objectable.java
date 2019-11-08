package com.orange.tpms.lib.db.core;

import android.content.Context;

public interface Objectable {

	/*
	 * @function: 获取对象
	 */
	public Object getObject();
	
	/*
	 * @function: 关闭对象
	 */
	public void closeObject();
	
	/*
	 * @function: 开启对象
	 */
	public void openObject(Context context, String path);
}
