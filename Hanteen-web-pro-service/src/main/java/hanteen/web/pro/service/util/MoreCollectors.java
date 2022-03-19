package hanteen.web.pro.service.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author zhaohang
 * Created on 2022-03-18
 */
public class MoreCollectors {

    public static <T, K> Collector<T, ?, Map<K, List<T>>>
        groupingByWithNullKeys(Function<T, K> classifier) {
        return Collectors.toMap(
                classifier,
                Collections::singletonList,
                (List<T> oldL, List<T> newE) -> {
                    List<T> merged = new ArrayList<>(oldL.size() + 1);
                    merged.addAll(oldL);
                    merged.addAll(newE);
                    return merged;
                }
        );
    }
}
