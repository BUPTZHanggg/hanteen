package hanteen.web.pro.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import hanteen.web.pro.service.util.JsonUtils;
import hanteen.web.pro.web.model.GreetingReq;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setupMockMvc(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testGreeting() throws Exception {
        GreetingReq req = new GreetingReq();
        req.setAge(11);
        req.setName("aaa");
        mockMvc.perform(MockMvcRequestBuilders.post("/rest/v1/hanteen/user/post").content(JsonUtils.toJsonString(req).getBytes()));
    }

}
