package hanteen.web.pro.service.util;

import static com.google.common.base.Preconditions.checkState;
import static hanteen.web.pro.service.util.MoreCollectors.groupingByWithNullKeys;
import static org.apache.commons.lang3.ArrayUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhaohang
 * Created on 2022-03-17
 */
public class EnumUtils {

    public static <T extends Enum<T>> void assertNotDuplicate(T[] values, Function<T, ?> classifier) {
        if (isEmpty(values)) {
            return;
        }
//      Map<?, List<T>> groupMap = Stream.of(values).collect(Collectors.groupingBy(classifier));
        Map<?, List<T>> groupMap = Stream.of(values).collect(groupingByWithNullKeys(classifier));
        groupMap.forEach((k, v) -> checkState(v.size() == 1,
                "[%s] %s have duplicate key %s", v.get(0).getClass().getName(), v.toString(), k));
    }
}
