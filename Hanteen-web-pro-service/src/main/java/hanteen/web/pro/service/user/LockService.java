package hanteen.web.pro.service.user;

import java.util.function.Supplier;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-04-20
 */
public interface LockService {

    void runWithLock(String lockKey, String action, Runnable runnable);

    <T> T callWithLock(String lockKey, String action, Supplier<T> supplier);

    <T> T callWithLock(String lockKey, String action, Supplier<T> supplier, Supplier<T> onLockFail);
}
