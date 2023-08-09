package hanteen.web.pro.service.util;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import java.util.Collection;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-09
 */
public class CompareUtils {

    public static int emptyLowPriority(Collection<?> c1, Collection<?> c2) {
        if ((isEmpty(c1) && isEmpty(c2))
                || (isNotEmpty(c1) && isNotEmpty(c2))) {
            return 0;
        }
        if (isEmpty(c1)) {//c1为空 c2非空
            return 1;
        } else {
            return -1; //c1非空 c2为空
        }
    }
}
