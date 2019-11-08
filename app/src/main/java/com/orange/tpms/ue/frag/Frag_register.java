package com.orange.tpms.ue.frag;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.Event;
import com.de.rocket.ue.layout.PercentRelativeLayout;
import com.orange.tpms.HttpCommand.Fuction;
import com.orange.tpms.R;
import com.orange.tpms.helper.RegisterHelper;
import com.orange.tpms.widget.LoadingWidget;
import com.orange.tpms.widget.TitleWidget;

import org.angmarch.views.NiceSpinner;

import bean.server.req.RegisterBeanReq;
import com.de.rocket.ue.injector.BindView;

/**
 * 注册页
 * Created by haide.yin() on 2019/3/26 14:28.
 */
public class Frag_register extends Frag_base {

    @BindView(R.id.prl_contaner)
    PercentRelativeLayout prlContaner;//Contaner
    @BindView(R.id.v_title_bar)
    TitleWidget twTitle;//Title
    @BindView(R.id.sp_country)
    NiceSpinner spCountry;//Country
    @BindView(R.id.sp_state)
    NiceSpinner spState;//State
    @BindView(R.id.sp_city)
    NiceSpinner spCity;//City
    @BindView(R.id.et_email)
    EditText etEmail;//tEmail
    @BindView(R.id.et_password)
    EditText etPassword;//Password
    @BindView(R.id.et_password_repeat)
    EditText etPasswordRepeat;//PasswordRepeat
    @BindView(R.id.et_product_number)
    EditText etProductNumber;//ProductNumber
    @BindView(R.id.et_firstname)
    EditText etFirstName;//FirstName
    @BindView(R.id.et_lastname)
    EditText etLastName;//LastName
    @BindView(R.id.et_company)
    EditText etCompany;//Company
    @BindView(R.id.et_contact)
    EditText etContact;//Contact
    @BindView(R.id.et_street)
    EditText etStreet;//Street
    @BindView(R.id.ldw_loading)
    LoadingWidget lwLoading;//Loading

    private RegisterHelper registerHelper;//Helper
    private RegisterBeanReq registerBeanReq = new RegisterBeanReq();//注册的Bean

    @Override
    public int onInflateLayout() {
        return R.layout.frag_register;
    }

    @Override
    public void initViewFinish(View inflateView) {
        initView();
        initHelper();
    }

    @Override
    public void onNexts(Object o) {

    }

    @Event(R.id.bt_cancel)
    private void cancel(View view){
        back();
    }

    @Event(R.id.bt_sign_up)
    private void register(View view){
        String account = etEmail.getText().toString();
        String password = etPassword.getText().toString();
//        String serial=etProductNumber.getText().toString();
//        String storetype=etCompany
        if(TextUtils.isEmpty(account) || TextUtils.isEmpty(password)){
            toast(R.string.app_account_password_empty);
            return;
        }
        toast(account,2000);
        registerBeanReq.setUserName(account);
        registerBeanReq.setPassword(password);
        registerBeanReq.setAddress(etStreet.getText().toString());
        registerBeanReq.setContactName(etContact.getText().toString());
        registerBeanReq.setOfficeTelephoneNumber(etCompany.getText().toString());
        registerBeanReq.setUserTitle(etFirstName.getText().toString());
        registerHelper.register(activity,registerBeanReq);
//        Fuction.Register(account,password,)
    }

    /**
     * 初始化页面
     */
    private void initView() {
        //空白区域点击隐藏键盘
        prlContaner.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
        //隐藏Loading的文字
        lwLoading.getTvLoading().setVisibility(View.GONE);
        //设置标题
        twTitle.setTvTitle(R.string.app_register_title);
        //返回
        twTitle.setOnBackListener(view -> back());
        //选中
        spCountry.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            registerBeanReq.setCountry((String) parent.getItemAtPosition(position));
            registerHelper.getProvinces(activity,position);
        });
        //选中
        spState.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            registerBeanReq.setState((String) parent.getItemAtPosition(position));
            registerHelper.getCities(activity,position);
        });
        //选中
        spCity.setOnSpinnerItemSelectedListener((parent, view, position, id) -> {
            registerBeanReq.setCity((String) parent.getItemAtPosition(position));
        });
    }

    /**
     * 初始化Helper
     */
    private void initHelper() {
        registerHelper = new RegisterHelper();
        //开始请求
        registerHelper.setOnPreRequestListener(() -> lwLoading.show(R.mipmap.img_data_upload_and_loading,getResources().getString(R.string.app_registrating),true));
        //结束请求
        registerHelper.setOnFinishRequestListener(() -> lwLoading.hide());
        //注册成功
        registerHelper.setOnRegisterSuccessListener(() -> {
            toast(R.string.app_regist_success);
            back();
        });
        //注册失败
        registerHelper.setOnRegisterFailedListener((this::toast));
        //读取国家列表回调
        registerHelper.setOnGetCountriesListener(list -> {
            registerBeanReq.setCountry(list.get(0));
            spCountry.attachDataSource(list);
        });
        //读取省份列表回调
        registerHelper.setOnGetProvincesListener(list -> {
            registerBeanReq.setState(list.get(0));
            spState.attachDataSource(list);
        });
        //读取城市列表回调
        registerHelper.setOnGetCitiesListener(list -> {
            registerBeanReq.setCity(list.get(0));
            spCity.attachDataSource(list);
        });
        //读取国家列表
        //registerHelper.getCountries(activity);
    }
}
