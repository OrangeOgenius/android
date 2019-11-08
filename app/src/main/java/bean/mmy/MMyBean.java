package bean.mmy;

import java.io.Serializable;

/**
 * MMY车辆配置
 *
 * @author john
 */
public class MMyBean implements Serializable {

    private String make_id;
    private String make;        // 制造商
    private String model;       // 商标
    private String year;        // 年份
    private String hex;         // 值
    private String lf_power;    // 功率值
    private String unit;          // 单位
    private String prd_num;     // 出产编号
    private String mmy_num;     // mmy number

    public MMyBean () {
        this.make_id = "";
        this.make = "";
        this.model = "";
        this.year = "";
        this.hex = "";
        this.lf_power = "";
        this.unit = "";
        this.prd_num = "";
        this.mmy_num = "";
    }

    public void setPrd_num(String prd_num) {
        this.prd_num = prd_num;
    }

    public String getPrd_num() {
        return prd_num;
    }

    public void setMmy_num(String mmy_num) {
        this.mmy_num = mmy_num;
    }

    public String getMmy_num() {
        return mmy_num;
    }

    // 单位
    public void setUnit (String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return this.unit;
    }

    public void setMake_id(String make_id) {
        this.make_id = make_id;
    }

    public String getMake_id() {
        return make_id;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getMake() {
        return make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setHex(String hex) {
        this.hex = hex;
    }

    public String getHex() {
        return hex;
    }

    public void setLf_power(String lf_power) {
        this.lf_power = lf_power;
    }

    public String getLf_power() {
        return lf_power;
    }

    @Override
    public String toString() {
        return "MMyBean{" +
                "make_id='" + make_id + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                ", hex='" + hex + '\'' +
                ", lf_power='" + lf_power + '\'' +
                '}';
    }
}