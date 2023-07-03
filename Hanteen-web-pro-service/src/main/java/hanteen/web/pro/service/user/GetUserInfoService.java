package hanteen.web.pro.service.user;

import org.springframework.ui.Model;

public interface GetUserInfoService {

    default long getUserId() { //接口中default方法都是public的（default不是修饰符的概念了）
        return 100L; //用户id（一般由发号器或数据库自增主键生成）
    }

    void getUserInfoById(String id, Model model);

    long generateUserId();

    String getUserProfile();
}
