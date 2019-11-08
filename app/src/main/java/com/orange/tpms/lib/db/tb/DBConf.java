package com.orange.tpms.lib.db.tb;

/**
 * Created by ZZQ on 2016/7/29.
 */
public class DBConf {

    // 定义数据库表
    public static final String[] TABLE = {
        /** 所有的mmy车型配置 */
        "tb_mmy_all",
        "(" + "make_id integer primary key autoincrement," +                     // id
        // "albumid integer NOT NULL," +		                              // 专辑id
        // "mark integer default 1," +		                                  // 分类
        "make varchar(100)," +		                                      // make
        "model varchar(100)," +		                                  // model
        "year varchar(100)," +		                                  // year
        "hex varchar(100)," +		                                  // 进制
        "prd_num varchar(100)," +		                                  // 生产编号
        "mmy_num varchar(100)," +		                                  // mmy编号
        "lf_power varchar(100)" + ")",                        // lf功率

        /** 比较常用的mmy车型 */
        "tb_mmy_like",
        "(" + "make_id integer NOT NULL," +                     // make_id
        "make varchar(100)," +		                                      // make
        "model varchar(100)," +		                                  // model
        "year varchar(100)," +		                                  // year
        "hex varchar(100)," +		                                  // 进制
        "prd_num varchar(100)," +		                                  // 生产编号
        "mmy_num varchar(100)," +		                                  // mmy编号
        "visit integer NOT NULL default 1," +		                  // 访问量, 默认为1
        "lf_power varchar(100)" + ")"                        // lf功率
    };

    // 定义数据库版本号
    public static final int VERSION = 2;

    // 定义数据库文件存储地址
    public static final String PATH = "orange.db";
}