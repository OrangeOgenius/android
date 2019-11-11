package com.orange.tpms.ue.frag;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.orange.tpms.R;
import com.orange.tpms.bean.ProgramFragBean;
import com.orange.tpms.bean.PublicBean;
import com.orange.tpms.ue.activity.MainActivity;
import com.orange.tpms.utils.OggUtils;
import com.orange.tpms.widget.TitleWidget;

import bean.mmy.MMyBean;

import com.de.rocket.ue.injector.BindView;

import java.util.regex.Pattern;

/**
 * 选择号码
 * Created by haide.yin() on 2019/4/4 9:28.
 */
public class Frag_program_number_choice extends Frag_base {

    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.bt_start)
    Button btStart;
    @BindView(R.id.tv_mmy_title)
    TextView tvTitle;
    @BindView(R.id.et_number)
    EditText etNumber;

    private MMyBean mMyBean;//MMYBean

    @Override
    public int onInflateLayout() {
        return R.layout.frag_program_number_choice;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
    }

    @Override
    public void onNexts(Object o) {
//        if(o instanceof MMyBean){
//            mMyBean = (MMyBean) o;
//            tvTitle.setText(mMyBean.getHex());
//        }
        tvTitle.setText(PublicBean.SelectMake+"/"+PublicBean.SelectModel+"/"+PublicBean.SelectYear);
    }

    @Event(R.id.bt_start)
    private void start(View view){
        OggUtils.hideKeyBoard(activity);
        String result = etNumber.getText().toString();
        Pattern pattern = Pattern.compile("[0-9]*");
        if (!TextUtils.isEmpty(result) && pattern.matcher(result).matches()) {//有可能扫描出来的是一串数字
            PublicBean.ProgramNumber=Integer.valueOf(result);
            toFrag(Frag_program_detail.class,true,true, "");
            /*if(Integer.valueOf(result) > 1){
                toast("目前仅支持一个");
            }else{

            }*/
        }else{
            toast(R.string.app_wrong_format);
        }
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //设置标题
        twTitle.setTvTitle(R.string.app_number_choice);
        //返回
        twTitle.setOnBackListener((view) -> back());
    }
}
