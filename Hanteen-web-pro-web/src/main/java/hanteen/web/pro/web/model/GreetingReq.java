package hanteen.web.pro.web.model;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-04-28
 */
public class GreetingReq {

    private String name;
    private int age;

    public GreetingReq() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
