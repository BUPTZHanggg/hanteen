package hanteen.web.pro.service.util.concurrent;

import java.util.concurrent.ThreadFactory;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-05-12
 */
public class ThreadPoolExecutorBuilder {

    private int coreSize;                           //核心线程数
    private int queueSize;                          //任务队列长度
    private String nameFormat;                      //线程名称格式
    private ThreadFactory threadFactory;            //线程工厂
    private long keepAliveMills;                    //最大空闲时间（毫秒）
    private boolean allowCoreThreadTimeOut = true;  //核心线程空闲时是否允许回收
    private boolean inheritMDCContext = true;       //继承主调线程的MDC,默认为true

    public static ThreadPoolExecutorBuilder newBuilder() {
        return new ThreadPoolExecutorBuilder();
    }

    public ThreadPoolExecutorBuilder coreSize(int coreSize) {
        this.coreSize = coreSize;
        return this;
    }

    public ThreadPoolExecutorBuilder queueSize(int queueSize) {
        this.queueSize = queueSize;
        return this;
    }

    public ThreadPoolExecutorBuilder nameFormat(String nameFormat) {
        this.nameFormat = nameFormat;
        return this;
    }

    public ThreadPoolExecutorBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadPoolExecutorBuilder keepAliveMills(long keepAliveMills) {
        this.keepAliveMills = keepAliveMills;
        return this;
    }

    public ThreadPoolExecutorBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    public ThreadPoolExecutorBuilder inheritMDCContext(boolean inheritMDCContext) {
        this.inheritMDCContext = inheritMDCContext;
        return this;
    }

    public int getCoreSize() {
        return coreSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public String getNameFormat() {
        return nameFormat;
    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    public long getKeepAliveMills() {
        return keepAliveMills;
    }

    public boolean isAllowCoreThreadTimeOut() {
        return allowCoreThreadTimeOut;
    }

    public boolean isInheritMDCContext() {
        return inheritMDCContext;
    }
}
