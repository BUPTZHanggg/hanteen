package hanteen.web.pro.web.utils;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-09-07
 */
public class TestUtils {

    static {
        System.out.println(TestUtils.class.getClassLoader());
    }

    public static int getNum() {
        return 1;
    }
}
