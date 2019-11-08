package com.orange.tpms.lib.db.tb;

import com.orange.tpms.lib.db.DBModel;

/**
 * 数据库基类(不使用事务)
 * Created by ZZQ on 2016/4/5.
 * Copyright (c) 2016 Wenshanhu.Co.Ltd. All rights reserved.
 */
abstract public class BaseDBModel extends DBModel {

    public static final int VERSION = DBConf.VERSION;
    public static final String[] TABLE = DBConf.TABLE;
    public static final String PATH = DBConf.PATH;

    public BaseDBModel() {
        super(PATH, VERSION, TABLE);
    }

    @Override
    abstract protected String getTableName();
}