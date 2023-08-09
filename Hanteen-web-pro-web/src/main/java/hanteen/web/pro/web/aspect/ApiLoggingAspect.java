package hanteen.web.pro.web.aspect;

import static hanteen.web.pro.model.utils.JsonUtils.toJsonString;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import hanteen.web.pro.service.constant.CommonCode;
import hanteen.web.pro.service.model.exception.HanteenBaseException;
import hanteen.web.pro.web.model.CommonMessage;

/**
 * https://mrbird.cc/Spring-Boot-Filter-Interceptor.html
 * 执行顺序
 * CommonParamFilter preDoFilter -> ContentCachingRequestWrapper preDoFilter
 * -> TokenCheckInterceptor preHandle
 * -> ApiLoggingAspect loggingRequest
 * -> 业务逻辑
 * -> (ApiLoggingAspect loggingThrowable
 *       -> GlobalExceptionHandlers
 *       -> TokenCheckInterceptor afterCompletion)
 *          or (ApiLoggingAspect loggingResponse
 *              -> TokenCheckInterceptor postHandle
 *              -> TokenCheckInterceptor afterCompletion)
 * -> ContentCachingRequestWrapper postDoFilter -> CommonParamFilter postDoFilter
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-28
 */
@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);
    private static final Boolean enableLoggingRequestBody = Boolean.TRUE; //动态配置
    private static final Boolean enableLoggingResponse = Boolean.TRUE;  //动态配置

    @Pointcut("within(hanteen.web.pro.web.controller.*)")
    public void loggingPointCut() {
    }

    @Before("loggingPointCut()")
    public void loggingRequest() {
        MDC.put("userId", "zhaohang06");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        StringBuilder reqStr = new StringBuilder();
        reqStr.append("[Request] ").append(request.getMethod())
                .append(" ").append(request.getRequestURI());
        if (isNotBlank(request.getQueryString())) {
            reqStr.append("?").append(request.getQueryString());
        }
        if (enableLoggingRequestBody && (request instanceof ContentCachingRequestWrapper)) {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            reqStr.append(" req body:").append(new String(requestWrapper.getContentAsByteArray()));
        }
        logger.info(reqStr.toString());
    }

    @Around("loggingPointCut()")
    public Object loggingResponse(ProceedingJoinPoint point) throws Throwable {
        //耗时打点 +
        Object res = point.proceed();
        //耗时打点 -
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return res;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        StringBuilder resStr = new StringBuilder();
        resStr.append("[Response]")
            .append(" uri:").append(request.getRequestURI());
        if (enableLoggingResponse) {
            if (res instanceof CommonMessage) {
                CommonMessage<?> message = (CommonMessage<?>) res;
                resStr.append(" code:").append(message.getCode())
                        .append(" ,resp body:").append(toJsonString(message.getData()));
            } else {
                resStr.append(" ,resp body:").append(toJsonString(res));
            }
        }
        logger.info(resStr.toString());
        return res;
    }

    @AfterThrowing(pointcut = "loggingPointCut()", throwing = "throwable")
    public void loggingThrowable(Throwable throwable) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String uri = requestAttributes == null ? "unknown" : requestAttributes.getRequest().getRequestURI();
        if (throwable instanceof HanteenBaseException) {
            HanteenBaseException e = (HanteenBaseException) throwable;
            reportException(uri, e.getCode());
        } else {
            reportException(uri, null);
        }
    }

    private void reportException(String uri, CommonCode commonCode) {
        //异常上报
        logger.error("exception {} for {}",
                ofNullable(commonCode).map(CommonCode::getMsg).orElse(""), uri);
    }
}
