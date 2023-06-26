package hanteen.web.pro.web.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import hanteen.web.pro.web.controller.GreetingController;
import hanteen.web.pro.web.filter.CommonParamFilter;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-28
 */
@Configuration
public class ApiFilterConfig {

    private static final Logger curLogger = LoggerFactory.getLogger(ApiFilterConfig.class);

    private static final Integer CONTENT_CACHE_LIMIT = 1024;

    //注意：doFilter之前不能使用request.getInputStream()方法取读取输入流
    @Bean()
    public OncePerRequestFilter buildHttpContentCachingWrapperFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
                curLogger.info("ContentCachingRequestWrapper pre");
                filterChain.doFilter(new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT), response);
                curLogger.info("ContentCachingRequestWrapper post");
            }
        };
    }

    @Bean
    public FilterRegistrationBean<CommonParamFilter> buildRequestParamFilter() {
        CommonParamFilter paramFilter = new CommonParamFilter();
        FilterRegistrationBean<CommonParamFilter> bean = new FilterRegistrationBean<>(paramFilter);
//        List<String> urlList = new ArrayList<>();
//        urlList.add("/*");
//        bean.setUrlPatterns(urlList);
        bean.setOrder(1);
        return bean;
    }
}
