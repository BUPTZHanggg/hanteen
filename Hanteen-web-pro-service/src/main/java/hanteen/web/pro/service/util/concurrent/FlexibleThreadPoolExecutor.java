package hanteen.web.pro.service.util.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static hanteen.web.pro.service.util.concurrent.MoreExecutors.buildThreadFactory;
import static hanteen.web.pro.service.util.concurrent.MoreExecutors.newFixedQueueThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import com.google.common.util.concurrent.ForwardingExecutorService;

/**
 * 底层构建的仍然是BlockingEnqueueThreadPoolExecutor
 * 但是用户可以通过实现攻击型接口IntSupplier动态调整线程池的coreSize
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-22
 */
public class FlexibleThreadPoolExecutor extends ForwardingExecutorService {

    private final IntSupplier sizeSupplier;
    private final Supplier<ThreadPoolExecutor> executorSupplier;
    private final BiConsumer<ThreadPoolExecutor, Integer> onChanged;

    private FlexibleThreadPoolExecutor(IntSupplier sizeSupplier,
            IntFunction<ThreadPoolExecutor> executorFactory,
            BiConsumer<ThreadPoolExecutor, Integer> onChanged) {
        this.sizeSupplier = sizeSupplier;
        this.executorSupplier = () -> executorFactory.apply(sizeSupplier.getAsInt());
        this.onChanged = onChanged;
    }

    public static FlexibleThreadPoolExecutor flexible(IntSupplier sizeSupplier,
            IntFunction<ThreadPoolExecutor> executorFactory,
            BiConsumer<ThreadPoolExecutor, Integer> onChanged) {
        checkNotNull(sizeSupplier);
        checkNotNull(executorFactory);
        checkNotNull(onChanged);
        return new FlexibleThreadPoolExecutor(sizeSupplier, executorFactory, onChanged);
    }

    /**
     * 创建动态线程池的工厂方法
     * @param sizeSupplier 用于设置coreSize
     * @param nameFormat  线程命名格式 统一以"flexible-"开头
     * @return
     */
    public static FlexibleThreadPoolExecutor flexible(IntSupplier sizeSupplier, String nameFormat) {
        return flexible(sizeSupplier, n -> {
            String realNameFormat = nameFormat;
            if (realNameFormat != null && realNameFormat.startsWith("flexible")) {
                realNameFormat += "flexible-";
            } else {
                realNameFormat = "flexible-thread-%d";
            }
            return newFixedQueueThreadPool(ThreadPoolExecutorBuilder.newBuilder()
                    .coreSize(n)
                    .queueSize(0)
                    .nameFormat(realNameFormat)
                    .threadFactory(buildThreadFactory(realNameFormat)));
        });
    }

    public static FlexibleThreadPoolExecutor flexible(IntSupplier sizeSupplier,
            IntFunction<ThreadPoolExecutor> executorFactory) {
        return flexible(sizeSupplier, executorFactory, (currPool, newSize) -> {
            if (currPool.getCorePoolSize() > newSize) {
                //调小先变coreSize
                currPool.setCorePoolSize(newSize);
                //BlockingEnqueueThreadPoolExecutor 不支持自定义maxSize 这里做下兼容
                if (!(currPool instanceof BlockingEnqueueThreadPoolExecutor)) {
                    currPool.setMaximumPoolSize(newSize);
                }
            } else if (currPool.getCorePoolSize() < newSize){
                //调大先变maxSize
                if (!(currPool instanceof BlockingEnqueueThreadPoolExecutor)) {
                    currPool.setMaximumPoolSize(newSize);
                }
                currPool.setCorePoolSize(newSize);
            }
        });
    }

    @Override
    protected ExecutorService delegate() {
        int thread = sizeSupplier.getAsInt();
        ThreadPoolExecutor thisThreadPool = executorSupplier.get();
        if (thisThreadPool.getCorePoolSize() != thread) {
            onChanged.accept(thisThreadPool, thread);
        }
        return thisThreadPool;
    }
}
