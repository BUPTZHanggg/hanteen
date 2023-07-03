package hanteen.web.pro.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.base.Joiner;

import hanteen.web.pro.model.mybatis.entity.User;
import hanteen.web.pro.model.utils.JsonUtils;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-05-09
 */
@SpringBootTest
public class BaseTest {

    @Test
    public void testUser() {
        User user2 = JsonUtils.fromJSON("", User.class);
        User user = JsonUtils.fromJSON("{\n"
                + "\"id\":10086,\n"
                + "\"name\":\"zhao\",\n"
                + "\"age\":18,\n"
                + "\"tile\":\"ASSOCIATE_PROFESSOR\",\n"
                + "\"salary\":2000.1\n"
                + "}", User.class);
        String s = JsonUtils.toJsonString(user);
        User user1 = JsonUtils.fromJSON(s, User.class);
        System.out.println(user.getSalary());
    }

    @Test
    public void testUser1() {
        String join = Joiner.on("_").join("a", "b");
        System.out.println(join);
        List<User> users = new ArrayList<>();
        users.add(new User());
        users.add(new User());
        String join1 = Joiner.on("_").skipNulls().join(users);
        System.out.println(join1);
    }
}
