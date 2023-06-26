package hanteen.web.pro.service.model;

import static hanteen.web.pro.service.util.EnumUtils.assertNotDuplicate;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author zhaohang
 * Created on 2022-03-17
 */
public enum CommonCode {

    SUCCESS(200, "成功"),

    /**
     * 通用异常
     */
    UNKNOWN_ERROR(10100001, "未知错误"),
    INVALID_PARAM(10100002, "非法参数"),
    SERVICE_BUSY(10100001, "服务繁忙"),
    ;

    private final int code;
    private final String msg;

    static {
        assertNotDuplicate(values(), CommonCode::getCode);
    }

    CommonCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    private static final Map<Integer, CommonCode> VALUE_MAP =
            Arrays.stream(values()).collect(toMap(CommonCode::getCode, identity()));

    public static CommonCode fromValue(int code) {
        return VALUE_MAP.getOrDefault(code, UNKNOWN_ERROR);
    }
}
