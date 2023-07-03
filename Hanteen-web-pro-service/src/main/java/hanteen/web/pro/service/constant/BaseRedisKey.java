package hanteen.web.pro.service.constant;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-29
 */
public interface BaseRedisKey {

    String name();

    /**
     * @param suffixes 后缀
     * @return 完整的redis key
     */
    default String key(Object... suffixes) {
        StringBuilder sb = new StringBuilder(name());
        if (suffixes != null && suffixes.length > 0) {
            Arrays.stream(suffixes).forEach(suffix -> sb.append("_").append(suffix));
        }
        return sb.toString();
    }
}
