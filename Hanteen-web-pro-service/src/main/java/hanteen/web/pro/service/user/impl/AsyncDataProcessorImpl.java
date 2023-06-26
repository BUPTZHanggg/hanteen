package hanteen.web.pro.service.user.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import hanteen.web.pro.service.user.AsyncDataProcessor;
import hanteen.web.pro.service.util.concurrent.BlockingEnqueueThreadPoolExecutor;
import hanteen.web.pro.service.util.concurrent.MoreExecutors;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-05-11
 */
@Lazy
@Service
public class AsyncDataProcessorImpl implements AsyncDataProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AsyncDataProcessorImpl.class);


    public static final int POLL_SIZE = 10;

    private ExecutorService asyncDataProcessor;

    @PostConstruct
    private void init() {
        logger.info("{}", this.getClass().getClassLoader());
        logger.info("{}", this.getClass().getClassLoader().getParent());
        logger.info("init thread pool, curr thread:{}", Thread.currentThread().getName());
        asyncDataProcessor = MoreExecutors.newFixedQueueThreadPool(POLL_SIZE, "data-proccesor-%d");
    }

    @Override
    public void executeRunnable() {
        for (int i = 0; i < 100; i++) {
            final int num = i;
            asyncDataProcessor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logger.info("task run " + num + " " + Thread.currentThread().getName());
            });
        }
        try {
            ((BlockingEnqueueThreadPoolExecutor) asyncDataProcessor).await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("pool idle");
    }
}
