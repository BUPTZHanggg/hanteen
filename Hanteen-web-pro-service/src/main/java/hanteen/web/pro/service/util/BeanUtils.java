package hanteen.web.pro.service.util;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-24
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(SupplierDecorator.class);

    private static ApplicationContext applicationContext;

    static {
        logger.info("init bean utils");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }

    @Nullable
    public static <T> T getBean(Class<T> clazz) {
        logger.info("get bean:{}", clazz);
        return applicationContext.getBean(clazz);
    }
}
