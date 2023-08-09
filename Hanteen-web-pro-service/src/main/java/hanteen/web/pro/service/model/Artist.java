package hanteen.web.pro.service.model;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import hanteen.web.pro.service.constant.UserState;
import hanteen.web.pro.service.util.RandomUtils.HasWeight;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-03-18
 */
public class Artist implements HasWeight {
    private UserState userState;
    private String name;

    public Artist(String name) {
        this.name = name;
    }

    public UserState getUserState() {
        return userState;
    }

    public void setUserState(UserState userState) {
        this.userState = userState;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getWeight() {
        return 50;
    }
}
