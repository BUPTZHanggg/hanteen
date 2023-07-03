package hanteen.web.pro.service.user.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
@Lazy
@Service
public class SouhuRegisterServiceImpl extends AbstractRegisterService {
    @Override
    public String getPortal() {
        return "souhu";
    }

    @Override
    public void register() {
        String userName = getUserName();
        //搜狐的注册逻辑
        System.out.println("搜狐注册");
    }
}
