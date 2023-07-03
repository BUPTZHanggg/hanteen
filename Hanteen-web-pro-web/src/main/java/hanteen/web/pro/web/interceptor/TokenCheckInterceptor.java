package hanteen.web.pro.web.interceptor;

import static hanteen.web.pro.service.model.common.CommonThreadLocal.removeToken;
import static hanteen.web.pro.service.model.common.CommonThreadLocal.token;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import hanteen.web.pro.service.constant.CommonCode;
import hanteen.web.pro.service.util.CommonAssert;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-07
 */
@Component
public class TokenCheckInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TokenCheckInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logger.info("TokenCheckInterceptor preHandle");
        logger.info("{}", ((HandlerMethod) handler).getBean().getClass().getName());
        logger.info("{}", ((HandlerMethod) handler).getMethod().getName());
        CommonAssert.assertTrue(isNotBlank(token()), CommonCode.INVALID_PARAM);
        return true;
    }

    /**
     * postHandle只有当被拦截的方法没有抛出异常成功时才会处理
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        logger.info("TokenCheckInterceptor postHandle");
    }

    /**
     * afterCompletion方法无论被拦截的方法抛出异常与否都会执行
     * 但如果preHandle抛出异常则不会执行
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        logger.info("TokenCheckInterceptor afterCompletion");
        removeToken();
    }
}
