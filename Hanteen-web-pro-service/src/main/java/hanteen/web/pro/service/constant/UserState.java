package hanteen.web.pro.service.constant;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-17
 */
public enum UserState implements SupplyIntValue {
    ALIVE(1),
    DEAD(2),
    ;

    @JsonValue
    private final int value;

    private static final Map<Integer, UserState> VALUE_MAP = Stream.of(values())
                    .collect(Collectors.toMap(UserState::getValue, Function.identity()));

    UserState(int value) {
        this.value = value;
    }

    @JsonCreator(mode = Mode.DELEGATING)
    public static UserState valueOf(int value) {
        return VALUE_MAP.getOrDefault(value, ALIVE);
    }

    @Override
    public int getValue() {
        return value;
    }
}
