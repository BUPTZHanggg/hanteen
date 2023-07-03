package hanteen.web.pro.service.util.concurrent;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-05-12
 */
public class MoreExecutors {

    private static final Logger logger = LoggerFactory.getLogger(MoreExecutors.class);

    private static final long MAX_KEEP_ALIVE_MILLS = MINUTES.toMillis(1);
    private static final boolean INHERIT_MDC = true;

    /**
     * 相比JDK的java.util.concurrent.Executors#newFixedThreadPool方法有以下不同之处：
     * 1.阻塞队列（ArrayBlockingQueue）有指定长度（不传则与coreSize相等），jdk是无限队列（LinkedBlockingQueue）
     * 2.业务自定义线程名称，方便问题定位
     * 3.配置了一个默认的线程最大空闲时间，jdk空闲时间为0
     * 4.自定义拒绝策略，队列满了之后行为是阻塞caller线程
     * @param coreSize 核心线程数
     * @param nameFormat 线程命名格式,建议规范命名xxx-%d
     * @return
     */
    public static ThreadPoolExecutor newFixedQueueThreadPool(int coreSize, String nameFormat) {
        return newFixedQueueThreadPool(ThreadPoolExecutorBuilder.newBuilder()
                .coreSize(coreSize)
                .queueSize(0)
                .nameFormat(nameFormat)
                .threadFactory(buildThreadFactory(nameFormat)));
    }

    /**
     * 借助guava的ThreadFactoryBuilder构建线程工厂
     * @param nameFormat 线程池产生的线程的命名格式，同时也是线程所在ThreadGroup的名字
     * @return
     */
    public static ThreadFactory buildThreadFactory(String nameFormat) {
        ThreadFactoryBuilder factoryBuilder = new ThreadFactoryBuilder();
        ThreadGroup threadGroup = new ThreadGroup(nameFormat); //构造新的线程组 并自定义命名
        factoryBuilder.setThreadFactory(runnable ->
                new Thread(threadGroup, runnable));
        if (isNotBlank(nameFormat)) {
            if (!nameFormat.endsWith("%d")) {
                nameFormat += "%d";
            }
            factoryBuilder.setNameFormat(nameFormat);
        } else {
            factoryBuilder.setNameFormat("thread-in-pool-%d");
        }
        //自定义的异常处理器 见：java.lang.Thread.getUncaughtExceptionHandler
        factoryBuilder.setUncaughtExceptionHandler((t, e) ->
                logger.error("Thread in pool executed exception,", e));
        return factoryBuilder.build();
    }

    public static ThreadPoolExecutor newFixedQueueThreadPool(ThreadPoolExecutorBuilder builder) {
        Objects.requireNonNull(builder);
        ThreadFactory threadFactory =
                builder.getThreadFactory() != null ? builder.getThreadFactory()
                                                   : buildThreadFactory(builder.getNameFormat());
        int threadSize = builder.getCoreSize();
        int queueSize = builder.getQueueSize();
        boolean allowCoreThreadTimeout = builder.isAllowCoreThreadTimeOut();
        long keepAliveMillis = builder.getKeepAliveMills() > 0
                               ? builder.getKeepAliveMills(): MAX_KEEP_ALIVE_MILLS;
        boolean inheritMDCContext = builder.isInheritMDCContext();
        ThreadPoolExecutor executor =
                new BlockingEnqueueThreadPoolExecutor(threadSize, //coreSize
                        queueSize, //queueSize
                        keepAliveMillis, MILLISECONDS,
                        allowCoreThreadTimeout) {
                    @Override
                    public void execute(Runnable task) {
                        Runnable runnable;
                        if (inheritMDCContext) {
                            runnable = buildMDCInheritedRunnable(task);
                        } else {
                            runnable = task;
                        }
                        super.execute(runnable);
                    }
                };
        executor.setThreadFactory(threadFactory);
        return executor;
    }

    private static Runnable buildMDCInheritedRunnable(Runnable task) {
        if (!INHERIT_MDC) {
            return task;
        }
        Map<String, String> callerMDCContext = MDC.getCopyOfContextMap();
//        logger.info("mdc map:{}", toJsonString(callerMDCContext));
        return () -> {
            Map<String, String> currContext = MDC.getCopyOfContextMap();
            if (isNotEmpty(callerMDCContext)) {
                MDC.setContextMap(callerMDCContext);
            }
            try {
                task.run();
            } finally {
                if (isNotEmpty(currContext)) {
                    MDC.setContextMap(currContext);
                }
            }
        };

    }
}
