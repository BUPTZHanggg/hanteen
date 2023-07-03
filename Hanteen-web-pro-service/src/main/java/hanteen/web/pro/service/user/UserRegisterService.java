package hanteen.web.pro.service.user;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
public interface UserRegisterService {

    String getPortal(); //这个方法也可以返回一个集合 一个service的实现cover多个门户的注册逻辑

    String getUserName();

    void register();
}
