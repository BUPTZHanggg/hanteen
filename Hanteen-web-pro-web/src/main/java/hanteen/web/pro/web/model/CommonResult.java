package hanteen.web.pro.web.model;

import static hanteen.web.pro.service.util.EnumUtils.assertNotDuplicate;

/**
 * @author zhaohang
 * Created on 2022-03-17
 */
public enum CommonResult {

    SUCCESS(200, "成功"),
    ;

    private final int code;
    private final String msg;

    CommonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    static {
        assertNotDuplicate(values(), CommonResult::getCode);
    }
}
