package com.orange.tpms.lib.db.core;

/**
 * key-value对
 * Created by ZZQ on 2016/4/5.
 * Copyright (c) 2016 Wenshanhu.Co.Ltd. All rights reserved.
 */
public class BasicNameValuePair {
    String name;
    Object value;

    public BasicNameValuePair (String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName () {
        return this.name;
    }

    public Object getValue () {
        return this.value;
    }
}
