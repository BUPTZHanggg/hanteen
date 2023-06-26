package hanteen.web.pro.service.model.common;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-06
 */
public class CommonThreadLocal {

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<String> TIMESTAMP = new ThreadLocal<>();

    @Nonnull
    public static String token() {
        return firstNonNull(TOKEN.get(), StringUtils.EMPTY);
    }

    public static void setToken(String token) {
        if (token != null) {
            TOKEN.set(token);
        }
    }

    public static void removeToken() {
        TOKEN.remove();
    }

    @Nonnull
    public static String timestamp() {
        return firstNonNull(TIMESTAMP.get(), StringUtils.EMPTY);
    }

    public static void setTimestamp(String timestamp) {
        if (timestamp != null) {
            TIMESTAMP.set(timestamp);
        }
    }
}
