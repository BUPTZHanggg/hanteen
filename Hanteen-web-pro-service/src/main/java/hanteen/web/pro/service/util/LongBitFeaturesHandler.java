package hanteen.web.pro.service.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.google.common.base.Preconditions;

/**
 * long值bit位存储信息的处理器
 * @author zhaohang
 * Created on 2023-08-12
 */
public class LongBitFeaturesHandler {

    public static void main(String[] args) {
        long val = 1L;
        for (int i = 0; i < 63;i++) {
            val <<= 1;
        }
        System.out.println(Long.toBinaryString(val));
        System.out.println(Long.MAX_VALUE);
    }

    private static final int LONG_BIT_COUNT = 64;

    public static <T> BitParser<T> newParser(Function<Integer, T> parseFunc) {
        return new BitParser<>(parseFunc);
    }

    public static <T> LongBuilder<T> newBuilder(Function<T, Integer> offsetFunc) {
        return new LongBuilder<>(offsetFunc);
    }

    public static class BitParser<T> {
        private final Function<Integer, T> parseFunc;

        private BitParser(Function<Integer, T> parseFunc) {
            this.parseFunc = parseFunc;
        }

        /**
         * @param features:long型，bit位存储信息
         * @return 解析出来的具体信息
         */
        public Set<T> parse(long features) {
            Set<T> result = new HashSet<>();
            long bitChecker = 1L;
            for (int offset = 0; offset < LONG_BIT_COUNT; offset++) {
                if ((features & bitChecker) == bitChecker) {
                    T feature = parseFunc.apply(offset); //第offset + 1位bit为1时，取出其存储的信息
                    if (feature != null) {
                        result.add(feature);
                    }
                }
                bitChecker <<= 1;
            }
            return result;
        }
    }

    public static class LongBuilder<T> {
        private final Function<T, Integer> offsetFunc;
        private final Map<Integer, Boolean> offsetMap = new ConcurrentHashMap<>();

        private LongBuilder(Function<T, Integer> offsetFunc) {
            this.offsetFunc = offsetFunc;
        }

        public LongBuilder<T> addFeature(T feature) {
            return updateFeatures(feature, true);
        }

        public LongBuilder<T> removeFeature(T feature) {
            return updateFeatures(feature, false);
        }

        private LongBuilder<T> updateFeatures(T feature, boolean buildIn) {
            int offset = offsetFunc.apply(feature);
            Preconditions.checkArgument(offset >= 0 && offset < LONG_BIT_COUNT,
                    "The offset %d for feature %s is out of bound", offset, feature.toString());
            offsetMap.put(offset, buildIn);
            return this;
        }

        public long build() {
            long result = 0L;
            for (Entry<Integer, Boolean> entry : offsetMap.entrySet()) {
                if (entry.getValue()) {
                    result |= (1L << entry.getKey());
                }
            }
            return result;
        }
    }
}
