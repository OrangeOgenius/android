package com.orange.tpms.ue.frag;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.helper.ReadSensorHelper;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.utils.ImageUtil;
import com.orange.tpms.utils.VibMediaUtil;
import com.orange.tpms.widget.CarWidget;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import bean.hardware.SensorDataBean;
import bean.mmy.MMyBean;

import com.de.rocket.ue.injector.BindView;

/**
 * Sensor Information
 * Created by haide.yin() on 2019/3/30 14:28.
 */
public class Frag_check_sensor_location extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.cw_car)
    CarWidget cwCar;//CarWidget
    @BindView(R.id.tv_fr_id)
    TextView tvTopRight;//FRID
    @BindView(R.id.tv_rr_id)
    TextView tvBottomRight;//RRID
    @BindView(R.id.tv_rl_id)
    TextView tvBottomLeft;//RLID
    @BindView(R.id.tv_fl_id)
    TextView tvTopLeft;//FLID
    @BindView(R.id.iv_fr_status)
    ImageView ivTopRightStatus;//FRStatua
    @BindView(R.id.iv_rr_status)
    ImageView ivBottomRightStatus;//RRStatua
    @BindView(R.id.iv_rl_status)
    ImageView ivBottomLeftStatus;//LLStatua
    @BindView(R.id.iv_fl_status)
    ImageView ivTopLeftStatus;//FLStatua
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading
    @BindView(R.id.tv_content)
    TextView tvContent;//Title
    @BindView(R.id.tv_tips)
    TextView tvTips;//Title
    @BindView(R.id.bt_check)
    Button btCheck;//Title
    String ObdHex="00";
    private ReadSensorHelper readSensorHelper;//Helper
    private VibMediaUtil vibMediaUtil;//音效与振动

    private boolean failedOneTime = false;//是否失败过一次
    private CarWidget.CAR_LOCATION carLocation = CarWidget.CAR_LOCATION.TOP_RIGHT;

    private enum CHECK_STATUS{
        STCCESS,//成功
        FAILED,//失败
        CLEAR,//清空
    }

    @Override
    public int onInflateLayout() {
        return R.layout.frag_check_sensor_location;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onDestroyView(){//与initViewFinish对应的生命周期
        super.onDestroyView();
        vibMediaUtil.release();
    }

    @Override
    public void onNexts(Object o) {
            tvContent.setText(ObdHex);
    }

    @Override
    public void onKeyTrigger(){
        trigger();
    }

    @Event(R.id.bt_check)
    private void trigger(View view){
        trigger();
    }

    @Event(R.id.bt_menue)
    private void menue(View view){
        toFrag(Frag_home.class,true,true,null,true);
    }

    /**
     * 读传感器
     */
    private void trigger(){
        clearViewIfFailed();
        //读取传感器资料
        readSensorHelper.readSensor(1,ObdHex,"00");
        vibMediaUtil.playVibrate();
    }

    /**
     * 更新列表信息
     */
    private void updateView(TextView tvSensorid,ImageView ivStatus,String content,CHECK_STATUS checkStatus){
        if(checkStatus == CHECK_STATUS.STCCESS){
            ivStatus.setVisibility(View.VISIBLE);
            ivStatus.setSelected(true);
            tvSensorid.setTextColor(getRoColor(R.color.color_black));
            tvSensorid.setText(content);
        }else if(checkStatus == CHECK_STATUS.FAILED){
            ivStatus.setVisibility(View.VISIBLE);
            ivStatus.setSelected(false);
            tvSensorid.setTextColor(getRoColor(R.color.color_red));
            tvSensorid.setText(content);
        }else if(checkStatus == CHECK_STATUS.CLEAR){
            ivStatus.setVisibility(View.INVISIBLE);
            tvSensorid.setText(content);
        }
    }

    /**
     * 清空View
     */
    private void clearViewIfFailed(){
        if(failedOneTime){
            cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.DEFAULT);
            if(carLocation == CarWidget.CAR_LOCATION.TOP_RIGHT){
                updateView(tvTopRight,ivTopRightStatus,"",CHECK_STATUS.CLEAR);
            }else if(carLocation == CarWidget.CAR_LOCATION.BOTTOM_RIGHT){
                updateView(tvBottomRight,ivBottomRightStatus,"",CHECK_STATUS.CLEAR);
            }else if(carLocation == CarWidget.CAR_LOCATION.BOTTOM_LEFT){
                updateView(tvBottomLeft,ivBottomLeftStatus,"",CHECK_STATUS.CLEAR);
            }else if(carLocation == CarWidget.CAR_LOCATION.TOP_LEFT){
                updateView(tvTopLeft,ivTopLeftStatus,"",CHECK_STATUS.CLEAR);
            }
        }
    }

    /**
     * 更新传感器信息
     */
    private void updateSensorbean(SensorDataBean sensorDataBean,boolean success){
        if(carLocation == CarWidget.CAR_LOCATION.TOP_RIGHT){
            if(success){
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.NORMAL);
                updateView(tvTopRight,ivTopRightStatus,sensorDataBean.getSensor_id(),CHECK_STATUS.STCCESS);
                failedOneTime = false;
                carLocation = CarWidget.CAR_LOCATION.BOTTOM_RIGHT;
            }else{
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.BAD);
                updateView(tvTopRight,ivTopRightStatus,"check failed!",CHECK_STATUS.FAILED);
                if(failedOneTime){
                    failedOneTime = false;
                    carLocation = CarWidget.CAR_LOCATION.BOTTOM_RIGHT;
                }else{
                    failedOneTime = true;
                }
            }
        }else if(carLocation == CarWidget.CAR_LOCATION.BOTTOM_RIGHT){
            if(success){
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.NORMAL);
                updateView(tvBottomRight,ivBottomRightStatus,sensorDataBean.getSensor_id(),CHECK_STATUS.STCCESS);
                failedOneTime = false;
                carLocation = CarWidget.CAR_LOCATION.BOTTOM_LEFT;
            }else{
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.BAD);
                updateView(tvBottomRight,ivBottomRightStatus,"check failed!",CHECK_STATUS.FAILED);
                if(failedOneTime){
                    failedOneTime = false;
                    carLocation = CarWidget.CAR_LOCATION.BOTTOM_LEFT;
                }else{
                    failedOneTime = true;
                }
            }
        }else if(carLocation == CarWidget.CAR_LOCATION.BOTTOM_LEFT){
            if(success){
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.NORMAL);
                updateView(tvBottomLeft,ivBottomLeftStatus,sensorDataBean.getSensor_id(),CHECK_STATUS.STCCESS);
                failedOneTime = false;
                carLocation = CarWidget.CAR_LOCATION.TOP_LEFT;
            }else{
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.BAD);
                updateView(tvBottomLeft,ivBottomLeftStatus,"check failed!",CHECK_STATUS.FAILED);
                if(failedOneTime){
                    failedOneTime = false;
                    carLocation = CarWidget.CAR_LOCATION.TOP_LEFT;
                }else{
                    failedOneTime = true;
                }
            }
        }else if(carLocation == CarWidget.CAR_LOCATION.TOP_LEFT){
            if(success){
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.NORMAL);
                updateView(tvTopLeft,ivTopLeftStatus,sensorDataBean.getSensor_id(),CHECK_STATUS.STCCESS);
                failedOneTime = false;
                ImageUtil.toBagroundGrey(btCheck);
                btCheck.setEnabled(false);
            }else{
                cwCar.setCarStatus(carLocation,CarWidget.CAR_STATUS.BAD);
                updateView(tvTopLeft,ivTopLeftStatus,"check failed!",CHECK_STATUS.FAILED);
                if(failedOneTime){
                    failedOneTime = false;
                    btCheck.setEnabled(false);
                    ImageUtil.toBagroundGrey(btCheck);
                }else{
                    failedOneTime = true;
                }
            }
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        ObdHex=((MainActivity)activity).itemDAO.GetHex(MainActivity.SelectMake,MainActivity.SelectModel,MainActivity.SelectYear);
        //设置标题
        twTitle.setTvTitle(R.string.app_sensor_info_location);
        //返回
        twTitle.setOnBackListener((view) -> back());
        //音效与震动
        vibMediaUtil = new VibMediaUtil(activity);
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        readSensorHelper = new ReadSensorHelper();
        //开始请求
        readSensorHelper.setOnPreRequestListener(() -> lwLoading.show(getResources().getString(R.string.app_loading_data)));
        //结束请求
        readSensorHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //请求失败
        readSensorHelper.setOnFailedRequestListener((object -> toast(object.toString(),2000)));
        //读取轮四个胎情况
        readSensorHelper.setReadSensorListener((sensorDataBean) -> {
            Log.v("yhd-","sensorDataBean:"+sensorDataBean.toString());
            vibMediaUtil.playBeep();
            //读取成功
            updateSensorbean(sensorDataBean,true);
        });
        //读传感器失败
        readSensorHelper.setOnReadFailedListener(() -> {
            vibMediaUtil.playBeep();
            updateSensorbean(null,false);
        });
    }
}
