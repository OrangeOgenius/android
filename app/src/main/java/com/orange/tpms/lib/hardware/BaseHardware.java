package com.orange.tpms.lib.hardware;

import android.app.Application;

/**
 * Created by john on 2019/6/22.
 */
abstract public class BaseHardware {

    public Application app;

    public BaseHardware (Application app) {
        this.app = app;
    }

    public BaseHardware () {
    }

    public void onCreate (){
    }

    public void setApp (Application app) {
        this.app = app;
    }
}
