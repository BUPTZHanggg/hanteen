package hanteen.web.pro.model.mybatis.entity;


import static java.util.Collections.emptyMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Suppliers;

import hanteen.web.pro.model.mybatis.constant.UserTitle;
import hanteen.web.pro.model.mybatis.model.UserExtInfoKey;
import hanteen.web.pro.model.utils.JsonUtils;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-28
 */
public class User {

    private long id;
    @JsonProperty("name")
    private String userName;
    private int age;
    private UserTitle title;
    private String extInfo;
    private double salary;

    //反序列化后获取extInfo内的数据
    @JsonIgnore
    private transient final Supplier<Map<String, Object>> resolvedExtInfo = Suppliers
            .memoize(() -> StringUtils.isNotEmpty(extInfo) ? JsonUtils.fromJSON(extInfo) : emptyMap());
    //序列化时更新extInFO
    @JsonIgnore
    private transient final Map<String, Object> extInfoMap = new HashMap<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getTitle() {
        return Optional.ofNullable(title)
                .map(UserTitle::name)
                .orElse(UserTitle.UNKNOWN.name());
    }

    public void setTitle(String title) {
        this.title = EnumUtils.getEnum(UserTitle.class, title, UserTitle.UNKNOWN);
    }

    @JsonIgnore
    public void setTitleEnum(UserTitle titleEnum) {
        this.title = titleEnum;
    }

    @JsonIgnore
    public UserTitle getTitleEnum() {
        return this.title;
    }

    public void setSalary(double salary) {
        this.salary = salary;
        extInfoMap.put(UserExtInfoKey.salary.name(), salary);
    }

    public double getSalary() {
        return this.salary > 0 ? this.salary
                   : MapUtils.getDouble(resolvedExtInfo.get(),
                           UserExtInfoKey.salary.name(), 0D);
    }

    public String getExtInfo() {
        return JsonUtils.toJsonString(extInfoMap);
    }

    public void setExtInfo(String extInfo) {
        this.extInfo = extInfo;
        Map<String, Object> temp = StringUtils.isNotEmpty(extInfo) ? JsonUtils.fromJSON(extInfo) : emptyMap();
        for (String key: temp.keySet()) {
            extInfoMap.put(key, temp.get(key));
        }
    }
}
