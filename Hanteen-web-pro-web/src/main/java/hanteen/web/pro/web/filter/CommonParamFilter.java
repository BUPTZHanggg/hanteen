package hanteen.web.pro.web.filter;

import static hanteen.web.pro.service.constant.HttpCommonParams.HTTP_PARAM_TIMESTAMP;
import static hanteen.web.pro.service.constant.HttpCommonParams.HTTP_PARAM_TOKEN;
import static hanteen.web.pro.service.model.common.CommonThreadLocal.setTimestamp;
import static hanteen.web.pro.service.model.common.CommonThreadLocal.setToken;
import static hanteen.web.pro.web.utils.HttpUtils.getCommonParamFromRequest;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-06
 */
public class CommonParamFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CommonParamFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.info("CommonParamFilter pre");
        setToken(getCommonParamFromRequest(request, HTTP_PARAM_TOKEN));
        setTimestamp(getCommonParamFromRequest(request, HTTP_PARAM_TIMESTAMP));
        filterChain.doFilter(request, response);
        logger.info("CommonParamFilter post");
    }
}
