package hanteen.web.pro.web.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import hanteen.web.pro.web.interceptor.TokenCheckInterceptor;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-07
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    private TokenCheckInterceptor tokenCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenCheckInterceptor)
                .addPathPatterns("/rest/**");
    }
}
