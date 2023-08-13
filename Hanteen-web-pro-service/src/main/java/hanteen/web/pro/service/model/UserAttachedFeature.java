package hanteen.web.pro.service.model;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.units.qual.A;

import hanteen.web.pro.service.constant.SupplyIntValue;
import hanteen.web.pro.service.util.LongBitFeaturesHandler;

/**
 * @author zhaohang
 * Created on 2023-08-12
 */
public enum UserAttachedFeature implements SupplyIntValue {
    ADVANCED_INDIVIDUAL(0, 1L), //先进个人
    MILITARY(1, 2L), //军人
    MASTER_OR_ABOVE(2, 4L), //硕士及以上学历
    OVERSEE_RETURNEE(5, 32L), //海归
    ;

    private final int bitOffset; //以bit位的方式
    private final long value; //以value的方式，实际二选一即可

    UserAttachedFeature(int bitOffset, long value) {
        this.bitOffset = bitOffset;
        this.value = value;
    }

    private static final Map<Integer, UserAttachedFeature> BIT_OFFSET_MAP =
            Arrays.stream(values()).collect(toMap(UserAttachedFeature::getValue, identity()));

    public static UserAttachedFeature fromValue(int bitOffset) {
        return BIT_OFFSET_MAP.getOrDefault(bitOffset, null);
    }

    private static final LongBitFeaturesHandler.BitParser<UserAttachedFeature> FEATURE_PARSER =
            LongBitFeaturesHandler.newParser(UserAttachedFeature::fromValue);

    @Override
    public int getValue() {
        return bitOffset;
    }

    public long getValueL() {
        return value;
    }

    //=================================================================================
    //基于bit位的方式封装
    public static Set<UserAttachedFeature> parse(long features) {
        return FEATURE_PARSER.parse(features);
    }

    public static long build(Set<UserAttachedFeature> features) {
        LongBitFeaturesHandler.LongBuilder<UserAttachedFeature> builder =
                LongBitFeaturesHandler.newBuilder(UserAttachedFeature::getValue);
        features.forEach(builder::addFeature);
        return builder.build();
    }

    public static boolean hasFeature(long features, UserAttachedFeature feature) {
        long value = 1L << feature.bitOffset;
        return (features & value) == value;
    }
    //=================================================================================

    public static void main(String[] args) {
        long l = Long.parseLong("101", 2);
        System.out.println(l);
        Set<UserAttachedFeature> parse = parse(l);
        System.out.println(parse);
        System.out.println("has  MILITARY:" + hasFeature(l, MILITARY));
        System.out.println(build(parse));
        System.out.println(build0(parse));
        System.out.println(isMasterOrAbove(l));
        LongBitFeaturesHandler.LongBuilder<UserAttachedFeature> builder =
                LongBitFeaturesHandler.newBuilder(UserAttachedFeature::getValue);
        long build = builder
                .addFeature(ADVANCED_INDIVIDUAL)
                .addFeature(OVERSEE_RETURNEE)
                .removeFeature(ADVANCED_INDIVIDUAL)
                .build();
        System.out.println(build);
        System.out.println(parse(build));
        System.out.println(getFeatures(build));
        System.out.println(isMasterOrAbove(build));
    }

    //=================================================================================
    //基于long值的方式封装
    public static Set<UserAttachedFeature> getFeatures(long value) {
        Set<UserAttachedFeature> result = new HashSet<>();
        for (UserAttachedFeature feature : values()) {
            if ((value & feature.value) == feature.value) {
                result.add(feature);
            }
        }
        return result;
    }

    public static long build0(Set<UserAttachedFeature> features) {
        return features.stream()
                .mapToLong(UserAttachedFeature::getValueL)
                .reduce(0L, (a, b) -> a | b);
    }

    public static boolean isMasterOrAbove(long features) {
        return (features & MASTER_OR_ABOVE.value) == MASTER_OR_ABOVE.value;
    }
    //=================================================================================
}
