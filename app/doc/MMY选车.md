## 一、目录

[TOC]

## 二、接口

- 包目录

> package com.orange.tpms.lib.api;

### 2.1 直接从Excel加载所有的车型配置

> MMy.LoadAssetsExcel(Context context)

- 调用代码示例

```code
	ArrayList<MMyBean> arrayList = MMy.LoadAssetsExcel(this.context);
	if (arrayList == null) {
		// 如果为空
		return ;
	}

	for (int i=0;i<arrayList.size();i++) {
		MMyBean mMyBean = arrayList.get(i);
		MMy.Print(mMyBean);
	}
```

### 2.2 将Excel文件加载到sqlite

> MMy.LoadMMy(Context context)

- 调用代码示例

```code
	boolea  is_success = MMy.LoadMMy(this.context);
```

### 2.3 从sqlite中获取所有的MMy车型配置

> MMy.getAllMMy(Context context)

- 调用代码示例

```code
	ArrayList<MMyBean> arrayList = MMy.getAllMMy(this.context);
```

### 2.4 根据制造商来获取车型配置

> MMy.getMMyWithMake (Context context, String make)

- 调用代码示例

```code
	ArrayList<MMyBean> arrayList = MMy.getAllMMy(this.context);
```

### 2.5 所有的车型logo文件

> assets/carlogo/*.png  其中*为车型的make, 可以根据此make到2.4获取车型配置

### 2.6 获取我最近访问的所有MMY车型

> 按照请求量排名

- 参数

> MMy.getMyMMy(Context context)

- 请求

```json
	ArrayList<MMyBean> arrayList = MMy.getMyMMy(Context context)
```

### 2.7 获取我最近访问的所有MMY车型里指定的车型

> 按照请求量排名

- 参数

> MMy.getMyMMyWithMake(Context context, String make)

- 请求

```json
	ArrayList<MMyBean> arrayList = MMy.getMyMMyWithMake(Context context, String make)
```

### 2.8 记录MMY车型的访问量

> 每次点击logo对应的车型里的系列时执行一次

- 参数

> MMy.visit (Context context, String make_id)		// make_id从MMyBean里获取

### 2.9 删除收藏的MMY车型

- 参考代码
> MMy.rmMyLikeMMy(context, make_id);


### 3.0 添加收藏的MMY车型

- 参考代码

> MMy.addMyLikeMMy (context, make_id);

### 3.1 根据MMY_NUM查看车型

``` code
ArrayList<MMyBean> arrayList = MMy.getMMyWithMMyNum(context, "3002013");
for (int i=0;i<arrayList.size();i++) {
  MMyBean mMyBean = arrayList.get(i);
  MMy.Print(mMyBean);
}
```

### 3.2 根据PRD_NUM查看车型

``` code
ArrayList<MMyBean> arrayList = MMy.getMMyWithPrdNum(context, "BEA017");
for (int i=0;i<arrayList.size();i++) {
  MMyBean mMyBean = arrayList.get(i);
  MMy.Print(mMyBean);
}
```

