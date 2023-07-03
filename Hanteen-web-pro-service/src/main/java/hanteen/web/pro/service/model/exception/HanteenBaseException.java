package hanteen.web.pro.service.model.exception;

import hanteen.web.pro.service.constant.CommonCode;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-02
 */
public class HanteenBaseException extends RuntimeException {

    private CommonCode code;

    public HanteenBaseException(CommonCode code) {
        super(code.getMsg());
        this.code = code;
    }

    public CommonCode getCode() {
        return code;
    }

    public void setCode(CommonCode code) {
        this.code = code;
    }
}
