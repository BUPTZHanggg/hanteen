package hanteen.web.pro.start;

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hanteen.web.pro.model.utils.JsonUtils;
import hanteen.web.pro.service.model.User;
import hanteen.web.pro.service.user.UserInfoService;
import hanteen.web.pro.web.model.GreetingReq;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserInfoService userInfoService;

    @Before
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testGreeting() throws Exception {
        GreetingReq req = new GreetingReq();
        req.setAge(11);
        req.setName("aaa");
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/v1/hanteen/user/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtils.toJsonString(req).getBytes()));
    }

    @Test
    public void batchProcessUserInfo() {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setUserName(i + "123");
            User user1 = new User();
            user1.setUserName(i + "123");
            userList.add(user);
            userList.add(user1);
        }
        userInfoService.batchProcessUserInfo(userList);
//        sleepUninterruptibly(2000, TimeUnit.MICROSECONDS);
    }

    @Test
    public void batchGetUserInfo() {
        userInfoService.batchUpdateUserInfo0();
    }

}
