package hanteen.web.pro.service.util.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在jdk原生线程池的基础上，做了以下定制化实现：
 * 1.定制化拒绝策略（不允许业务方主动set），队列满了之后行为是阻塞caller线程
 *   @see BlockingEnqueueThreadPoolExecutor#setRejectedExecutionHandler(RejectedExecutionHandler)
 * 2.由于1，舍弃了原生线程池最大线程数量的概念（maxSize = coreSize）
 *   @see BlockingEnqueueThreadPoolExecutor#setMaximumPoolSize(int)
 * 3.基于内部类Notifier的等待通知机制，线程池的caller线程可以通过CallerNoticedThreadPoolExecutor#await方法，
 *   阻塞等待至线程池达到空闲状态(队列中没有任务且没有正在被执行的任务)
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-05-23
 */
public class BlockingEnqueueThreadPoolExecutor extends ThreadPoolExecutor {

    private static final Logger logger = LoggerFactory.getLogger(BlockingEnqueueThreadPoolExecutor.class);

    /**
     * 统计加入线程池的任务数量
     */
    private final AtomicInteger acceptedTaskCount = new AtomicInteger();

    private final Notifier notifier = new Notifier();

    public BlockingEnqueueThreadPoolExecutor(int coreSize, int queueSize, long keepAliveTime,
            TimeUnit unit, boolean allowCoreThreadTimeOut) {
        super(coreSize, //coreSize
                coreSize, //maxSize
                keepAliveTime, unit,
                new ArrayBlockingQueue<>(Math.max(coreSize, queueSize)), //队列长度不低于核心线程数，避免核心线程频繁空闲
                new BlockingAskQueuePolicy() //自定义拒绝策略 实现队列满了之后的阻塞行为
        );
        super.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
        throw new UnsupportedOperationException(
                "Customized reject policy is not allowed");
    }

    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (super.getCorePoolSize() > corePoolSize) {
            //调小先变coreSize
            super.setCorePoolSize(corePoolSize);
            super.setMaximumPoolSize(corePoolSize);
        } else {
            //调大先变maxSize
            super.setMaximumPoolSize(corePoolSize);
            super.setCorePoolSize(corePoolSize);
        }
    }

    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        throw new UnsupportedOperationException(
                "Customized max size is not allowed.");
    }

    /**
     * 阻塞等待至线程池达到空闲状态（队列中没有任务且没有正在被执行的任务）
     * 与原生的 ThreadPoolExecutor#awaitTermination()不同，不需要关联线程池的shut down操作
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        notifier.awaitFinished();
    }

    /**
     * 对原生的ThreadPoolExecutor#execute(Runnable)进行包装
     * 在任务提交前进行数量统计,该数量即判断线程池是否达到空闲状态(队列中没有任务且没有正在被执行的任务)的依据
     * @see BlockingEnqueueThreadPoolExecutor#afterExecute(Runnable, Throwable)
     */
    @Override
    public void execute(Runnable task) {
        acceptedTaskCount.incrementAndGet();
        try {
            super.execute(task);
        } catch (Exception e) {
            //主要为了catch自定义拒绝策略中抛出的RejectedExecutionException
            acceptedTaskCount.decrementAndGet();
            throw e;
        }
    }

    /**
     * 对原生的对原生的ThreadPoolExecutor#afterExecute(Runnable, Throwable)进行包装
     * 任务执行后，线程池中已提交任务数量会减少。
     * 如果任务数量为零，则调用Notifier#signalBlocker()，唤醒在此ThreadPoolExecutor实例上等待的线程。
     */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t != null) {
            logger.error("Task throw exception while running", t);
        }

        //这里上了一个较重的锁 避免notifier.signalBlocker()由多个线程同时执行
        synchronized (this) {
            acceptedTaskCount.decrementAndGet();
//            logger.info("{} tasks remaining", acceptedTaskCount.get());
            if (acceptedTaskCount.get() == 0) {
                notifier.signalBlocker();
            }
        }
    }

    private static class BlockingAskQueuePolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            BlockingQueue<Runnable> workQueue = executor.getQueue();
            boolean taskAccepted = false;
            while (!taskAccepted) {
                if (executor.isShutdown()) {
                    throw new RejectedExecutionException(
                            "The pool has been shutdown while attempting to offer rejected task.");
                }

                try {
                    //如果队列满了则阻塞
                    workQueue.put(r);
                    //任务入队成功，如果没有可用线程，需要创建一个新的核心线程
                    if (executor.getPoolSize() == 0) {
                        executor.prestartCoreThread();
                    }
                    taskAccepted = true;
                    logger.info("[Rejected Policy] Success to offer one rejected task");
                } catch (InterruptedException e) {
                    //do nothing 如果阻塞过程中被唤醒，重新请求入队
                }
            }
        }
    }

    /**
     * 基于等待-通知机制，当线程池完成所有提交任务后通知线程池的caller线程
     * @see BlockingEnqueueThreadPoolExecutor#await()
     */
    private static class Notifier {
        private Thread blocker = null; //阻塞在BlockingEnqueueThreadPoolExecutor#await()的线程，同一时刻只能存在一个blocker
        private boolean isFinished = false; //标识线程池的空闲状态（队列中没有任务且没有正在被执行的任务）
        private final Lock lock = new ReentrantLock();
        private final Condition finished = lock.newCondition();

        public Notifier() {
        }

        /**
         * BlockingEnqueueThreadPoolExecutor.await()的实现
         * @throws InterruptedException Condition.await()抛出
         */
        private void awaitFinished() throws InterruptedException {
            lock.lock();
            try {
                if (blocker != null) {
                    throw new IllegalStateException("The pool only allows one blocker!"
                            + "Curr block:" + blocker.getName());
                }
                blocker = Thread.currentThread();
                while (!isFinished) {
                    finished.await();
                }
            } finally {
                lock.unlock();
                blocker = null;
            }
        }

        /**
         * 通知blocker线程池完成了所有已提交任务
         */
        private void signalBlocker() {
            lock.lock();
            try {
                isFinished = true;
                finished.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}
