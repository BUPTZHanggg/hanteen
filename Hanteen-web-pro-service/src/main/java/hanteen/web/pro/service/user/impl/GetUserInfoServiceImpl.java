package hanteen.web.pro.service.user.impl;


import static hanteen.web.pro.model.utils.JsonUtils.fromJSON;
import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import hanteen.web.pro.model.mybatis.constant.UserTitle;
import hanteen.web.pro.model.mybatis.entity.User;
import hanteen.web.pro.model.mybatis.model.EmployeeWelfareInfo;
import hanteen.web.pro.model.utils.JsonUtils;
import hanteen.web.pro.service.model.UserProfileView;
import hanteen.web.pro.service.user.GetUserInfoService;
import hanteen.web.pro.service.user.LockService;
import hanteen.web.pro.service.util.LocalHostUtil;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;



/*
 *
 * @author paida 派哒 zeyu.pzy@alibaba-inc.com
 * @date 2020/10/27
 */

@Service
public class GetUserInfoServiceImpl implements GetUserInfoService {

    private static final int HOST_BIT = 10; //机器码占有的bit位
    private static final int SEQ_BIT = 11; //序列号占有的bit位

    // 雪花id：1bit固定为0（正数） + 42位时间戳 + 10位机器码 + 11位自增序列号
    // 42位时间戳能用到22世纪
    private static final int SEQ_OFFSET = 0;
    private static final int HOST_OFFSET = SEQ_BIT;
    private static final int TS_OFFSET = HOST_OFFSET + HOST_BIT;

    private static final long HOST_MAX = ~(-1L << HOST_BIT);
    private static final long SEQ_MAX = ~(-1L << SEQ_BIT);

    private static final String USER_PROFILE_VIEW_STR = "[\n"
            + "      {\n"
            + "          \"key\": \"name\",\n"
            + "          \"viewParam\": \"姓名\",\n"
            + "          \"viewValue\": \"张三\",\n"
            + "          \"mappingType\":\"ADD_0\"\n"
            + "      },\n"
            + "      {\n"
            + "          \"key\": \"title\",\n"
            + "          \"viewParam\": \"职称\",\n"
            + "          \"viewValue\": \"教授\",\n"
            + "          \"mappingType\":\"ADD_1\"\n"
            + "      }\n"
            + "]";

    private static final String USER_PROFILE_INFO = "{\n"
            + "          \"name\": \"三一一\",\n"
            + "          \"title\": \"教授\"\n"
            + "      }";

    @Resource
    private LockService lockService;

    @Override
    public void getUserInfoById(String id, Model model)
    {
        List<EmployeeWelfareInfo> welfareInfos =
                Stream.of(UserTitle.values()).map(UserTitle::get).collect(Collectors.toList());
        lockService.runWithLock("getUser" + id, "getUser", () -> getUser(model));

        //search by id, get UserInfo
//        MVCMybatisDemoUser user = mVCMybatisDemoUserMapper.queryUserInfo(id);
//        model.addAttribute("name", user.getId())
//                .addAttribute("age", user.getAge())
//                .addAttribute("height", user.getHeight())
//                .addAttribute("weight", user.getWeight());
    }

    @Override
    public long generateUserId() {
        long tsCode = System.currentTimeMillis(); //随机码
        long seq = SEQ_MAX & getUserId(); //用户id（一般由发号器或数据库自增主键生成）
        long hostCode = HOST_MAX & Optional.of(LocalHostUtil.getLocalHostName()).map(String::hashCode).orElse(0); //机器码
        return (Long.MAX_VALUE & (tsCode << TS_OFFSET)) | (hostCode << HOST_OFFSET) | (seq << SEQ_OFFSET);
    }

    @Override
    public String getUserProfile() {
        String join = Joiner.on("_").join("a", "b");
        return join;
    }

    @Override
    public List<UserProfileView> getProfileView() {
        List<UserProfileView> userProfileViews =
                fromJSON(USER_PROFILE_VIEW_STR, List.class, UserProfileView.class);
        List<UserProfileView> result = new ArrayList<>();
        JsonNode jsonNode = fromJSON(USER_PROFILE_INFO, JsonNode.class);
        userProfileViews.forEach(view -> {
            UserProfileView curr = new UserProfileView();
            curr.setViewParam(view.getViewParam());
            String origin = jsonNode.get(view.getKey()).asText();
            String real = ofNullable(origin)
                    .map(o -> view.getMappingFunc().apply(o))
                    .orElse("未知");
            curr.setViewValue(real);
            result.add(curr);
        });
        return result;
    }

    private void getUser(Model model) {

    }
}
