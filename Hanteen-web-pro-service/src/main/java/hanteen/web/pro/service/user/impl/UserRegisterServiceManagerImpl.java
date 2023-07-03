package hanteen.web.pro.service.user.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import hanteen.web.pro.service.constant.CommonCode;
import hanteen.web.pro.service.model.exception.HanteenBaseException;
import hanteen.web.pro.service.user.UserRegisterService;
import hanteen.web.pro.service.user.UserRegisterServiceManager;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
@Lazy
@Service
public class UserRegisterServiceManagerImpl implements UserRegisterServiceManager {
    private static final Logger logger = LoggerFactory.getLogger(UserRegisterServiceManagerImpl.class);

    private final Map<String, UserRegisterService> serviceMap = new HashMap<>();

    @Autowired
    public void init(UserRegisterService[] services) {
        for (UserRegisterService service : services) {
            serviceMap.put(service.getPortal(), service);
        }
    }

    @Override
    public UserRegisterService getRegisterService(String portal) {
        UserRegisterService service = serviceMap.get(portal);
        if (service == null) {
            logger.warn("No available register service for {}", portal);
            throw new HanteenBaseException(CommonCode.OPERATION_UNSUPPORTED);
        }
        return service;
    }
}
