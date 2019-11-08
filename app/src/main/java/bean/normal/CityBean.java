package bean.normal;

/**
 * 城市bean
 * Created by john on 2019/5/1.
 */
public class CityBean {

    private String name;
    private String name_en;
    private String name_pinyin;
    private String code;
    private String id;
    private String path;
    private String level;

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName_en(String name_en) {
        this.name_en = name_en;
    }
    public String getName_en() {
        return name_en;
    }

    public void setName_pinyin(String name_pinyin) {
        this.name_pinyin = name_pinyin;
    }
    public String getName_pinyin() {
        return name_pinyin;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCode() {
        return code;
    }

    public void setId (String id) {
        this.id = id;
    }
    public String getId () {
        return id;
    }

    public void setPath (String path) {
        this.path = path;
    }
    public String getPath () {
        return path;
    }

    public void setLevel (String level) {
        this.level = level;
    }
    public String getLevel () {
        return level;
    }
}
