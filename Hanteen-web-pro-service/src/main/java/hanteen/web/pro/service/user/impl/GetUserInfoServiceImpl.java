package hanteen.web.pro.service.user.impl;


import hanteen.web.pro.model.mybatis.entity.MVCMybatisDemoUser;
import hanteen.web.pro.model.mybatis.mapper.MVCMybatisDemoUserMapper;
import hanteen.web.pro.service.user.GetUserInfoService;

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

    @Autowired
    protected MVCMybatisDemoUserMapper mVCMybatisDemoUserMapper;

    @Override
    public void getUserInfoById(String id, Model model)
    {


        //search by id, get UserInfo
        MVCMybatisDemoUser user = mVCMybatisDemoUserMapper.queryUserInfo(id);
        model.addAttribute("name", user.getId())
                .addAttribute("age", user.getAge())
                .addAttribute("height", user.getHeight())
                .addAttribute("weight", user.getWeight());
    }
}
