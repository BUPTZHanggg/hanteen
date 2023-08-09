package hanteen.web.pro.service;

import static java.lang.Math.abs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.common.base.Joiner;

import hanteen.web.pro.model.mybatis.constant.UserTitle;
import hanteen.web.pro.model.mybatis.entity.User;
import hanteen.web.pro.model.utils.JsonUtils;
import hanteen.web.pro.service.constant.UserState;
import hanteen.web.pro.service.model.Artist;
import hanteen.web.pro.service.model.UserScreeningBasicCondition;
import hanteen.web.pro.service.user.GetUserInfoService;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-05-09
 */
@SpringBootTest
public class BaseTest {

    private static final String CONDITION_STR = "[{\n"
            + "          \"blackList\": [444],\n"
            + "          \"needCheckSeniority\":false,\n"
            + "\"minPaperCount\":15,\n"
            + "          \"userTitle\": \"ASSOCIATE_PROFESSOR\"\n"
            + "      },{\n"
            + "          \"blackList\": [333],\n"
            + "          \"needCheckSeniority\":false,\n"
            + " \"age\":\"50-100;\",\n"
            + "\"minPaperCount\":20,\n"
            + "          \"userTitle\": \"PROFESSOR\"\n"
            + "      },{\n"
            + "          \"blackList\": [111,222],\n"
            + "          \"needCheckSeniority\":true,\n"
            + " \"age\":\"60-100;\",\n"
            + "\"minPaperCount\":30,\n"
            + "          \"userTitle\": \"ACADEMICIAN\"\n"
            + "      }\n"
            + "]";

    @Test
    public void testCondition() {
        List<UserScreeningBasicCondition> userScreeningBasicConditions =
                JsonUtils.fromJSON(CONDITION_STR, List.class, UserScreeningBasicCondition.class); //一般是读配置
        Collections.sort(userScreeningBasicConditions);
        UserTitle title = userScreeningBasicConditions.stream()
                .filter(condition -> condition.isOnFor(1111L, 90, 100, true))
                .sorted()
                .findFirst()
                .map(UserScreeningBasicCondition::getUserTitle)
                .orElse(UserTitle.UNKNOWN);
        System.out.println(title);
    }

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
