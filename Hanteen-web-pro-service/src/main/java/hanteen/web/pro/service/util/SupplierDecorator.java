package hanteen.web.pro.service.util;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hanteen.web.pro.service.user.impl.UserInfoServiceImpl;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-24
 */
public class SupplierDecorator {

    private static final Logger logger = LoggerFactory.getLogger(SupplierDecorator.class);

    public static <T> Supplier<T> singletonSupplier(Supplier<T> supplier) {
        return new SingletonSupplier<>(supplier);
    }

    private static class SingletonSupplier<T> implements Supplier<T> {
        private final Supplier<T> supplier;
        private volatile boolean initialized;
        private T value;

        private SingletonSupplier(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            if (!(this.initialized)) {
                synchronized (this) {
                    if (!(this.initialized)) {
                        T t = this.supplier.get();
                        this.initialized = true;
                        this.value = t;
                        logger.info("Thread {} init the singleton value {}", Thread.currentThread().getName(), t);
                        return t;
                    }
                }
            }
            return this.value;
        }
    }
}
