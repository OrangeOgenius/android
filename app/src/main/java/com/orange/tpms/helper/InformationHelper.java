package com.orange.tpms.helper;

import android.content.Context;

import com.orange.tpms.R;
import com.orange.tpms.bean.InformationBean;
import com.orange.tpms.lib.db.share.SettingShare;

import java.util.ArrayList;
import java.util.List;

public class InformationHelper extends BaseHelper {

    private List<InformationBean> informationList;//信息列表

    /**
     * 读取信息列表
     */
    public void getInformation(Context context) {
        preRequestNext();
        informationList = new ArrayList<>();//数据源
        SettingShare.Information information = SettingShare.getSystemInformation();
        informationList.add(new InformationBean(context.getString(R.string.infomation_name),information.systemName));
        informationList.add(new InformationBean(context.getString(R.string.infomation_module),information.sysModule));
        informationList.add(new InformationBean(context.getString(R.string.infomation_serial_number),information.serialNumber));
        informationList.add(new InformationBean(context.getString(R.string.infomation_version),information.version));
        informationList.add(new InformationBean(context.getString(R.string.infomation_data_version),information.dataVersion));
        getInformationNext(informationList);
        finishRequestNext();
    }

    /* *********************************  获取自动锁定列表  ************************************** */

    private OnGetInformationListener onGetInformationListener;

    public void getInformationNext(List<InformationBean> arrayList) {
        if (onGetInformationListener != null) {
            runMainThread(() -> onGetInformationListener.onGetInformation(arrayList));
        }
    }

    public void setOnGetInformationListener(OnGetInformationListener onGetInformationListener) {
        this.onGetInformationListener = onGetInformationListener;
    }

    public interface OnGetInformationListener {
        void onGetInformation( List<InformationBean> arrayList);
    }
}
