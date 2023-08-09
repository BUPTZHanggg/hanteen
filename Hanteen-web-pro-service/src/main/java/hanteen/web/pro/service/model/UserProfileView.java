package hanteen.web.pro.service.model;

import java.util.function.Function;

import org.apache.commons.lang3.EnumUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-03
 */
public class UserProfileView {
    private String key;
    private String viewParam;
    private String viewValue;
    private MappingType mappingType;
    private Function<String, String> mappingFunc;

    public UserProfileView() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getViewParam() {
        return viewParam;
    }

    public void setViewParam(String viewParam) {
        this.viewParam = viewParam;
    }

    public String getViewValue() {
        return viewValue;
    }

    public void setViewValue(String viewValue) {
        this.viewValue = viewValue;
    }

    public MappingType getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = EnumUtils.getEnum(MappingType.class, mappingType, MappingType.UNKNOWN);
        this.mappingFunc = this.mappingType.getMappingFunc();
    }

    @JsonIgnore
    public Function<String, String> getMappingFunc() {
        return mappingFunc;
    }

    public enum MappingType {
        UNKNOWN,
        ADD_0,
        ADD_1,
        ;

        private Function<String, String> getMappingFunc() {
            switch (this) {
                case ADD_0:
                    return (origin) -> origin + "0";
                case ADD_1:
                    return (origin) -> origin + "1";
                case UNKNOWN:
                default:
                    return null;
            }
        }
    }
}
