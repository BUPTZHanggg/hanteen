package hanteen.web.pro.service.user.impl;

import static hanteen.web.pro.service.model.CommonCode.SERVICE_BUSY;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import hanteen.web.pro.service.user.LockService;
import hanteen.web.pro.service.util.CommonAssert;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-04-20
 */
@Service
public class LockServiceImpl implements LockService {

    @Override
    public void runWithLock(String lockKey, String action, Runnable runnable) {
        callWithLock(lockKey, action, () -> {
            runnable.run();
            return null;
        });
    }

    //加锁失败直接抛异常
    @Override
    public <T> T callWithLock(String lockKey, String action, Supplier<T> supplier) {
        List<String> lockKeys = new ArrayList<>(); //可能需要获取多把锁（比如不同粒度的）
        Collections.sort(lockKeys);
        boolean lockSuccess = tryLock(lockKeys, 5, 5);//重试次数和间隔 可配置
        CommonAssert.assertTrue(lockSuccess, SERVICE_BUSY);
        try {
            return supplier.get();
        } finally {
            releaseLock(lockKeys);
        }
    }

    //加锁失败返回指定结果
    @Override
    public <T> T callWithLock(String lockKey, String action, Supplier<T> supplier, Supplier<T> onLockFail) {
        List<String> lockKeys = new ArrayList<>(); //可能需要获取多把锁（比如不同粒度的）
        Collections.sort(lockKeys);
        if (tryLock(lockKeys, 5, 5)) {
            try {
                return supplier.get();
            } finally {
                releaseLock(lockKeys);
            }
        } else {
            return onLockFail.get();
        }
    }

    private boolean tryLock(List<String> lockKeys, int maxRetryTimes, int retryIntervalMs) {
        int retryTime = 0;
        while (retryTime++ < maxRetryTimes) {
            //遍历 setnx加锁
            List<String> successKeys = new ArrayList<>(); //记录下成功的
            if (successKeys.size() == lockKeys.size()) {
                return true;
            }
            //失败后需要释放成功了的锁
            releaseLock(successKeys);
            try {
                Thread.sleep(retryIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); //再中断一下
            }
        }
        return false;
    }

    private void releaseLock(List<String> lockKeys) {
        Collections.reverse(lockKeys);
        //遍历 del key
    }
}
