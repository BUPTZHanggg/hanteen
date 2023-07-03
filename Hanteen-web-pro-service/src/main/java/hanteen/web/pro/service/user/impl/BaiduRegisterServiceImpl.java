package hanteen.web.pro.service.user.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
@Lazy
@Service
public class BaiduRegisterServiceImpl extends AbstractRegisterService {

    @Override
    public String getPortal() {
        return "baidu"; //一般是读配置
    }

    @Override
    public void register() {
        String userName = getUserName();
        //百度的注册逻辑
        System.out.println("百度注册");
    }
}
