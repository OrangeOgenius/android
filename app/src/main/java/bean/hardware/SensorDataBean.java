package bean.hardware;

/**
 * Created by john on 2019/4/14.
 */

public class SensorDataBean {

    private int cmdCode = -1;   // 是否可读取到信息, 1-校验通过, 0-校验不通过
    private String sensor_id;   // 传感器id
    private int id_len;         // 传感器id长度
    private int temp;           // 温度实际值
    private int b_temp;         // 温度基础值
    private int h_temp;         // 温度数值
    private int kpa;            // 胎压
    private int V;              // 电池电压
    private int hasTemp;        // 是否有胎温
    private int hasBatteryV;    // 是否有电池电压
    private int batteryStatus;  // 是否有电池状态
    private int batteryLevel;   // 电池是否饱电, 1-饱电, 0-低电
    private boolean ifPass = false;     // 是否达标

    public SensorDataBean () {
        this.b_temp = 0;
        this.h_temp = 0;
        this.V = 0;
        this.hasTemp = 0;
        this.hasBatteryV = 0;
        this.batteryLevel = 0;
        this.batteryStatus = 0;
        this.kpa = 0;
        this.sensor_id = "";
        this.id_len = 8;
    }

    /**
     * 获取传感器数据
     * @return
     */
    public String getSensorDataStr () {

        return String.format("b_t:%s,h_t:%s,v:%s,has_temp:%s,has_battery:%s,batteryStatus:%s,batteryLevel:%s" +
                ",kpa:%s,sensor_id:%s",
                this.b_temp, h_temp, V, hasTemp, hasBatteryV, batteryStatus, batteryStatus, batteryLevel, kpa,
                sensor_id);
    }

    /**
     * 设置是否达标
     * @return
     */
    public boolean getIfPass () {
        return true;
    }

    public void setCmdCode (int cmdCode) {
        this.cmdCode = cmdCode;
    }
    public int getCmdCode () {
        return this.cmdCode;
    }
    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }
    public String getSensor_id() {
        if (this.id_len >= this.sensor_id.length()) {
            return this.sensor_id;
        }
        String sensor_id = this.sensor_id.substring(this.sensor_id.length() - this.id_len);
        this.sensor_id = sensor_id;
        return this.sensor_id;
    }

    public void setId_len(int id_len) {
        this.id_len = id_len;
    }
    public int getId_len() {
        return id_len;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
    public int getTemp() {
        temp = h_temp-b_temp;
        return temp;
    }

    public void setB_temp(int b_temp) {
        this.b_temp = b_temp;
    }
    public int getB_temp() {
        return b_temp;
    }

    public void setH_temp(int h_temp) {
        this.h_temp = h_temp;
    }
    public int getH_temp() {
        return h_temp;
    }

    public void setKpa(int kpa) {
        this.kpa = kpa;
    }
    public int getKpa() {
        return kpa;
    }

    public void setV(int V) {
        this.V = V;
    }
    public int getV() {
        return 22+(int)(V&0x0F);        // 返回真实电压值, 单位为0.1V
    }

    public void setHasTemp(int hasTemp) {
        this.hasTemp = hasTemp;
    }
    public int getHasTemp() {
        return hasTemp;
    }

    public void setHasBatteryV(int hasBatteryV) {
        this.hasBatteryV = hasBatteryV;
    }
    public int getHasBatteryV() {
        return hasBatteryV;
    }

    public void setBatteryStatus(int batteryStatus) {
        this.batteryStatus = batteryStatus;
    }
    public int getBatteryStatus() {
        return batteryStatus;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    public int getBatteryLevel() {
        return batteryLevel;
    }

    @Override
    public String toString() {
        return "SensorDataBean{" +
                "cmdCode=" + cmdCode +
                ", sensor_id='" + sensor_id + '\'' +
                ", id_len=" + id_len +
                ", temp=" + temp +
                ", b_temp=" + b_temp +
                ", h_temp=" + h_temp +
                ", kpa=" + kpa +
                ", V=" + V +
                ", hasTemp=" + hasTemp +
                ", hasBatteryV=" + hasBatteryV +
                ", batteryStatus=" + batteryStatus +
                ", batteryLevel=" + batteryLevel +
                ", ifPass=" + ifPass +
                '}';
    }
}
