package hanteen.web.pro.web.model;

import static hanteen.web.pro.service.util.LocalHostUtil.getLocalHostName;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import hanteen.web.pro.service.constant.CommonCode;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-01
 */
@JsonInclude(Include.NON_NULL)
public class CommonMessage<T> {

    private final int code;
    private final String msg;
    private T data;
    private final String ts = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    private final String host = getLocalHostName();

    private CommonMessage(T data) {
        this.code = CommonCode.SUCCESS.getCode();
        this.msg = CommonCode.SUCCESS.name();
        this.data = data;
    }

    private CommonMessage() {
        this.code = CommonCode.SUCCESS.getCode();
        this.msg = CommonCode.SUCCESS.name();
    }

    private CommonMessage(CommonCode code) {
        this.code = code.getCode();
        this.msg = code.getMsg();
    }

    public static <T> CommonMessage<T> ok() {
        return new CommonMessage<>();
    }

    public static <T> CommonMessage<T> ok(T data) {
        return new CommonMessage<>(data);
    }

    public static <T> CommonMessage<T> error(CommonCode code) {
        return new CommonMessage<>(code);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public String getTs() {
        return ts;
    }

    public String getHost() {
        return host;
    }
}
