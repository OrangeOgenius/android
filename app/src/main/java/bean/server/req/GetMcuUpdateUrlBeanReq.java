package bean.server.req;

/**
 * 获取指定版本的固件
 * Created by john on 2019/3/28.
 */
public class GetMcuUpdateUrlBeanReq {
    private int index;
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
}
