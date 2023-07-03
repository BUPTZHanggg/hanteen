package hanteen.web.pro.service.user.impl;

import hanteen.web.pro.service.user.UserRegisterService;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
public abstract class AbstractRegisterService implements UserRegisterService {

    //各实现类的通用逻辑，不需要分别实现
    @Override
    public String getUserName() {
        return "zhaohang";
    }
}
