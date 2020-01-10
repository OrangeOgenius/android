package com.orange.tpms.ue.kt_frag


import android.app.Dialog
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.orange.jzchi.jzframework.JzActivity
import com.orange.jzchi.jzframework.callback.SetupDialog
import com.orange.tpms.R
import com.orange.tpms.RootFragement
import com.orange.tpms.bean.PublicBean
import com.orange.tpms.bean.SensorData
import com.orange.tpms.ue.activity.KtActivity
import com.orange.tpms.utils.OgCommand
import com.orange.tpms.utils.VibMediaUtil
import com.orange.tpms.widget.CarWidget
import kotlinx.android.synthetic.main.fragment_frag__check__location.view.*

class Frag_Check_Location : RootFragement(R.layout.fragment_frag__check__location) {
    lateinit var cwCar: CarWidget//CarWidget
    lateinit var tvTopRight: TextView//FRID
    lateinit var tvBottomRight: TextView//RRID
    lateinit var tvBottomLeft: TextView//RLID
    lateinit var tvTopLeft: TextView//FLID
    lateinit var ivTopRightStatus: ImageView//FRStatua
    lateinit var ivBottomRightStatus: ImageView//RRStatua
    lateinit var ivBottomLeftStatus: ImageView//LLStatua
    lateinit var ivTopLeftStatus: ImageView//FLStatua
    lateinit var tvContent: TextView//Title
    lateinit var tvTips: TextView//Title
    var idcount=8
    lateinit var btCheck: Button//Title
    var ObdHex = "00"
    lateinit var vibMediaUtil: VibMediaUtil//音效与振动
    var failedOneTime = false//是否失败过一次
    var carLocation: CarWidget.CAR_LOCATION = CarWidget.CAR_LOCATION.TOP_LEFT

    enum class CHECK_STATUS {
        STCCESS, //成功
        FAILED, //失败
        CLEAR
        //清空
    }

    override fun viewInit() {
        idcount = (activity as KtActivity).itemDAO.GetCopyId(
            (activity as KtActivity).itemDAO.getMMY(
                PublicBean.SelectMake,
                PublicBean.SelectModel,
                PublicBean.SelectYear
            )
        )
        cwCar = rootview.findViewById(R.id.cw_car)
        tvTopRight = rootview.findViewById(R.id.tv_rr_id)
        tvBottomRight = rootview.findViewById(R.id.tv_rl_id)
        tvBottomLeft = rootview.findViewById(R.id.tv_fl_id)
        tvTopLeft = rootview.findViewById(R.id.tv_fr_id)
        ivTopRightStatus = rootview.findViewById(R.id.iv_rr_status)
        ivBottomRightStatus = rootview.findViewById(R.id.iv_rl_status)
        ivBottomLeftStatus = rootview.findViewById(R.id.iv_fl_status)
        ivTopLeftStatus = rootview.findViewById(R.id.iv_fr_status
        )
        tvContent = rootview.findViewById(R.id.tv_content)
        btCheck = rootview.findViewById(R.id.bt_check)
        rootview.bt_menue.setOnClickListener { GoMenu() }
        rootview.bt_check.setOnClickListener { trigger() }
        initView()
        for(i in 0 until PublicBean.CopySuccessID.size){
            when(i){
                0->{tvTopLeft.text=PublicBean.CopySuccessID[i]}
                1->{tvTopRight.text=PublicBean.CopySuccessID[i]}
                2->{tvBottomRight.text=PublicBean.CopySuccessID[i]}
                3->{tvBottomLeft.text=PublicBean.CopySuccessID[i]}
            }
        }

        rootview.tv_content.text = "${PublicBean.SelectMake}/${PublicBean.SelectModel}/${PublicBean.SelectYear}"
    }


    override fun onKeyTrigger() {
        super.onKeyTrigger()
        trigger()
    }

    /**
     * 初始化页面
     */
    private fun initView() {
        ObdHex = (activity as KtActivity).itemDAO.GetHex(
            PublicBean.SelectMake,
            PublicBean.SelectModel,
            PublicBean.SelectYear
        );
        //音效与震动
        vibMediaUtil = VibMediaUtil(activity)
    }
var position=0;
    /**
     * 读传感器
     */
    private fun trigger() {
        if (run) {
            return
        }
        run = true
        clearViewIfFailed()
        vibMediaUtil.playVibrate()
        JzActivity.getControlInstance().showDiaLog(R.layout.data_loading, false, false)
        Thread {
            val a = OgCommand.GetId(ObdHex, "00")
            handler.post {
                run = false
                if (!JzActivity.getControlInstance().getNowPageTag().equals("Frag_Check_Location")) {
                    return@post
                }
                vibMediaUtil.playBeep()
                JzActivity.getControlInstance().closeDiaLog()
                if (a.success) {
                    if (PublicBean.CopySuccessID != null && PublicBean.CopySuccessID.size>position&&PublicBean.CopySuccessID[position].contains(a.id)) {
                        position++;
                        updateSensorbean(a, true);
                    } else {
                        updateSensorbean(a, false);
                        if(!failedOneTime){position++}
                    }

                } else {
                    JzActivity.getControlInstance().toast(resources.getString(R.string.app_read_failed))
                }
            }
        }.start()
    }

    /**
     * 更新列表信息
     */
    private fun updateView(tvSensorid: TextView, ivStatus: ImageView, content: String, checkStatus: CHECK_STATUS) {
        if (checkStatus == CHECK_STATUS.STCCESS) {
            ivStatus.visibility = View.VISIBLE
            ivStatus.isSelected = true
            tvSensorid.setTextColor(resources.getColor(R.color.color_black))
            tvSensorid.text = content
        } else if (checkStatus == CHECK_STATUS.FAILED) {
            ivStatus.visibility = View.VISIBLE
            ivStatus.isSelected = false
            tvSensorid.setTextColor(resources.getColor(R.color.color_red))
            tvSensorid.text = content
        } else if (checkStatus == CHECK_STATUS.CLEAR) {
            ivStatus.visibility = View.INVISIBLE
            tvSensorid.text = content
        }
    }

    /**
     * 清空View
     */
    private fun clearViewIfFailed() {
        if (failedOneTime) {
            cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.DEFAULT)
            if (carLocation == CarWidget.CAR_LOCATION.TOP_RIGHT) {
                updateView(tvTopRight, ivTopRightStatus, "", CHECK_STATUS.CLEAR)
            } else if (carLocation == CarWidget.CAR_LOCATION.BOTTOM_RIGHT) {
                updateView(tvBottomRight, ivBottomRightStatus, "", CHECK_STATUS.CLEAR)
            } else if (carLocation == CarWidget.CAR_LOCATION.BOTTOM_LEFT) {
                updateView(tvBottomLeft, ivBottomLeftStatus, "", CHECK_STATUS.CLEAR)
            } else if (carLocation == CarWidget.CAR_LOCATION.TOP_LEFT) {
                updateView(tvTopLeft, ivTopLeftStatus, "", CHECK_STATUS.CLEAR)
            }
        }
    }

    /**
     * 更新传感器信息
     */
    private fun updateSensorbean(sensorDataBean: SensorData?, success: Boolean) {
        if (carLocation == CarWidget.CAR_LOCATION.TOP_RIGHT) {
            if (success) {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.NORMAL)
                updateView(tvTopRight, ivTopRightStatus, sensorDataBean!!.id, CHECK_STATUS.STCCESS)
                failedOneTime = false
                carLocation = CarWidget.CAR_LOCATION.BOTTOM_RIGHT
            } else {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.BAD)
                updateView(tvTopRight, ivTopRightStatus, "check failed!", CHECK_STATUS.FAILED)
                if (failedOneTime) {
                    failedOneTime = false
                    carLocation = CarWidget.CAR_LOCATION.BOTTOM_RIGHT
                } else {
                    failedOneTime = true
                }
            }
        } else if (carLocation == CarWidget.CAR_LOCATION.BOTTOM_RIGHT) {
            if (success) {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.NORMAL)
                updateView(tvBottomRight, ivBottomRightStatus, sensorDataBean!!.id, CHECK_STATUS.STCCESS)
                failedOneTime = false
                carLocation = CarWidget.CAR_LOCATION.BOTTOM_LEFT
            } else {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.BAD)
                updateView(tvBottomRight, ivBottomRightStatus, "check failed!", CHECK_STATUS.FAILED)
                if (failedOneTime) {
                    failedOneTime = false
                    carLocation = CarWidget.CAR_LOCATION.BOTTOM_LEFT
                } else {
                    failedOneTime = true
                }
            }
        } else if (carLocation == CarWidget.CAR_LOCATION.BOTTOM_LEFT) {
            if (success) {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.NORMAL)
                updateView(tvBottomLeft, ivBottomLeftStatus, sensorDataBean!!.id, CHECK_STATUS.STCCESS)
                failedOneTime = false
                btCheck.isEnabled = false
            } else {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.BAD)
                updateView(tvBottomLeft, ivBottomLeftStatus, "check failed!", CHECK_STATUS.FAILED)
                if (failedOneTime) {
                    failedOneTime = false
                    btCheck.isEnabled = false
                } else {
                    failedOneTime = true
                }
            }
        } else if (carLocation == CarWidget.CAR_LOCATION.TOP_LEFT) {
            if (success) {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.NORMAL)
                updateView(tvTopLeft, ivTopLeftStatus, sensorDataBean!!.id, CHECK_STATUS.STCCESS)
                failedOneTime = false
                carLocation = CarWidget.CAR_LOCATION.TOP_RIGHT
            } else {
                cwCar.setCarStatus(carLocation, CarWidget.CAR_STATUS.BAD)
                updateView(tvTopLeft, ivTopLeftStatus, "check failed!", CHECK_STATUS.FAILED)
                if (failedOneTime) {
                    failedOneTime = false
                    carLocation = CarWidget.CAR_LOCATION.TOP_RIGHT
                } else {
                    failedOneTime = true
                }
            }
        }
    }

}
