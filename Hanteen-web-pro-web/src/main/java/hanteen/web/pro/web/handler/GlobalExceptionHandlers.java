package hanteen.web.pro.web.handler;

import static hanteen.web.pro.model.utils.JsonUtils.toJsonString;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import hanteen.web.pro.service.constant.CommonCode;
import hanteen.web.pro.service.model.exception.HanteenBaseException;
import hanteen.web.pro.web.model.CommonMessage;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-02
 */
@RestControllerAdvice
public class GlobalExceptionHandlers {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlers.class);

    @ExceptionHandler(Throwable.class)
    public CommonMessage<?> handleThrowable(HttpServletRequest request, Throwable e) {
        printErrorTrace(request, e);
        return CommonMessage.error(CommonCode.SERVICE_BUSY);
    }

    @ExceptionHandler(HanteenBaseException.class)
    public CommonMessage<?> handleHanteenBaseException(HttpServletRequest request, HanteenBaseException e) {
        printErrorTrace(request, e);
        return CommonMessage.error(e.getCode());
    }

    private void printErrorTrace(HttpServletRequest request, Throwable e) {
        if (e instanceof HanteenBaseException) {
            CommonCode code = ((HanteenBaseException) e).getCode();
            logger.error("[HanteenBaseException]:{}, request:{}", code.getCode(), parseRequestParam(request), e);
        } else {
            logger.error("[{}]:{}, request:{}", e.getClass().getName(), e.getMessage(), parseRequestParam(request), e);
        }
    }

    private String parseRequestParam(HttpServletRequest request) {
        return toJsonString(request.getParameterMap());
    }
}
