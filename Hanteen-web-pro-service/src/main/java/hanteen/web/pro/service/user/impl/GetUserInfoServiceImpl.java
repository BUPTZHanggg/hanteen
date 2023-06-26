package hanteen.web.pro.service.user.impl;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import hanteen.web.pro.model.mybatis.entity.MVCMybatisDemoUser;
import hanteen.web.pro.model.mybatis.mapper.MVCMybatisDemoUserMapper;
import hanteen.web.pro.service.constant.UserTitle;
import hanteen.web.pro.service.model.EmployeeWelfareInfo;
import hanteen.web.pro.service.user.GetUserInfoService;
import hanteen.web.pro.service.user.LockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;



/*
 *
 * @author paida 派哒 zeyu.pzy@alibaba-inc.com
 * @date 2020/10/27
 */

@Service
public class GetUserInfoServiceImpl implements GetUserInfoService {

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

    private void getUser(Model model) {

    }
}
