package com.orange.tpms.Callback;

import com.orange.tpms.bean.SensorData;

public interface Copy_C {
     void Copy_Finish();
     void Copy_Next(boolean success,int position);
}
