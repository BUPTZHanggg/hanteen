package hanteen.web.pro.service.util;

import static com.google.common.base.Predicates.alwaysFalse;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-02
 */
public class RetryUtils {

    private static final Logger logger = LoggerFactory.getLogger(RetryUtils.class);

    public static void main(String[] args) {
        try {
            Integer retry = callWithRetry(2, 100L, e -> e instanceof FileNotFoundException, () -> {
                int a = 10;
                if (a >= 10) {
                    return a / 0;
                } else {
                    throw new FileNotFoundException(); //像这种可能抛出的受检异常 需要手动处理
                }
            });
        } catch (Throwable e) {
            int a = 0;
        }
    }

    /**
     * @param maxAttempts:最大重试次数，不做取值限制，<=0时不重试
     * @param retryPeriodMills:重试间隔
     * @param skipRetry:断言函数，用于决策是否跳出重试逻辑
     * @param func:待执行的函数
     * @return
     * @param <T>
     * @param <X>
     * @throws X:可能抛出的异常类型，与func中的异常关联
     */
    public static <T, X extends Throwable> T callWithRetry(int maxAttempts, long retryPeriodMills,
            Predicate<Throwable> skipRetry, ThrowableCallable<T, X> func) throws X {
        int attempts = 0;
        Throwable toThrow;
        do {
            try {
                return func.call();
            } catch (Throwable e) {
                if (skipRetry.test(e)) { //对特定的异常可以跳出重试
                    //可以做个上报
                    throw e;
                } else {
                    if (e instanceof Error) {
                        //要做上报
                        logger.error("Error occurred while retry", e);
                    }
                    if (retryPeriodMills > 0) {
                        sleepUninterruptibly(retryPeriodMills, TimeUnit.MICROSECONDS);
                    }
                    toThrow = e;
                    if (++attempts <= maxAttempts) {
                        logger.warn("Try to execute retry (times:{}) for {}", attempts, e.toString());
                    }
                }
            }
        } while (attempts <= maxAttempts);
        throw (X) toThrow;
    }

    public static <T, X extends Throwable> T callWithRetry(int maxAttempts, long retryPeriodMills,
            ThrowableCallable<T, X> func) throws X {
        return callWithRetry(maxAttempts, retryPeriodMills, alwaysFalse(), func);
    }

    public static <T, X extends Throwable> T runWithRetry(int maxAttempts, long retryPeriodMills,
            Predicate<Throwable> skipRetry, ThrowableRunnable<X> func) throws X {
        return callWithRetry(maxAttempts, retryPeriodMills, skipRetry, () -> {
            func.run();
            return null;
        });
    }

    public static <T, X extends Throwable> T runWithRetry(int maxAttempts, long retryPeriodMills,
            ThrowableRunnable<X> func) throws X {
        return runWithRetry(maxAttempts, retryPeriodMills, alwaysFalse(), func);
    }
}

/**
 * 这里（下同）封装了一个泛型，主要目的：
 * 1.对于重试中的func，不希望所有异常都被吞掉（比如出现了某种异常就跳出重试）
 * 2.对于重试方法本身抛出来的上述的某种异常，期望受检异常需要强制处理，而非受检异常无需强制处理
 * @param <X>
 */
interface ThrowableRunnable<X extends Throwable> {
    void run() throws X;
}

interface ThrowableCallable<T, X extends Throwable> {
    T call() throws X;
}
