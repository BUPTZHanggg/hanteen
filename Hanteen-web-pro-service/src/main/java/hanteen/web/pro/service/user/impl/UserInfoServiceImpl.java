package hanteen.web.pro.service.user.impl;


import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static hanteen.web.pro.model.utils.JsonUtils.fromJSON;
import static java.lang.Math.abs;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import hanteen.web.pro.model.mybatis.constant.UserTitle;
import hanteen.web.pro.model.mybatis.model.EmployeeWelfareInfo;
import hanteen.web.pro.service.model.User;
import hanteen.web.pro.service.model.UserProfileView;
import hanteen.web.pro.service.user.LockService;
import hanteen.web.pro.service.user.UserInfoService;
import hanteen.web.pro.service.util.BeanUtils;
import hanteen.web.pro.service.util.LocalHostUtil;
import hanteen.web.pro.service.util.SupplierDecorator;
import hanteen.web.pro.service.util.concurrent.HanteenExecutors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;



/*
 *
 * @author paida 派哒 zeyu.pzy@alibaba-inc.com
 * @date 2020/10/27
 */

@Service
public class UserInfoServiceImpl implements UserInfoService {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceImpl.class);

    private static final int HOST_BIT = 10; //机器码占有的bit位
    private static final int SEQ_BIT = 11; //序列号占有的bit位

    // 雪花id：1bit固定为0（正数） + 42位时间戳 + 10位机器码 + 11位自增序列号
    // 42位时间戳能用到22世纪
    private static final int SEQ_OFFSET = 0;
    private static final int HOST_OFFSET = SEQ_BIT;
    private static final int TS_OFFSET = HOST_OFFSET + HOST_BIT;

    private static final long HOST_MAX = ~(-1L << HOST_BIT);
    private static final long SEQ_MAX = ~(-1L << SEQ_BIT);

    private static final String USER_PROFILE_VIEW_STR = "[\n"
            + "      {\n"
            + "          \"key\": \"name\",\n"
            + "          \"viewParam\": \"姓名\",\n"
            + "          \"viewValue\": \"张三\",\n"
            + "          \"mappingType\":\"ADD_0\"\n"
            + "      },\n"
            + "      {\n"
            + "          \"key\": \"title\",\n"
            + "          \"viewParam\": \"职称\",\n"
            + "          \"viewValue\": \"教授\",\n"
            + "          \"mappingType\":\"ADD_1\"\n"
            + "      }\n"
            + "]";

    private static final String USER_PROFILE_INFO = "{\n"
            + "          \"name\": \"三一一\",\n"
            + "          \"title\": \"教授\"\n"
            + "      }";

    public static final int POLL_SIZE = 10;
    public static final int USER_SHARD_SIZE = 20;
    private ThreadPoolExecutor pool;
    private ListeningExecutorService listeningExecutor;
    private static String userTimestamp;
    private static final Supplier<UserProfileLoader> PROFILE_LOADER_SUPPLIER
            = SupplierDecorator.singletonSupplier(() -> BeanUtils.getBean(UserProfileLoader.class)); //这样的好处是即便UserProfileLoader在容器中不是单例的bean，也能做到单例的效果
//    private static final Supplier<UserProfileLoader> PROFILE_LOADER_SUPPLIER = () -> BeanUtils.getBean(UserProfileLoader.class); //这样的话如果UserProfileLoader在容器中不是单例的bean，就会存在多个loader同时执行

    @Resource
    private LockService lockService;

    @PostConstruct
    private void init() {
        pool = HanteenExecutors.newFixedQueueThreadPool(POLL_SIZE, "user-process-%d");
        listeningExecutor = MoreExecutors.listeningDecorator(pool);
    }

    @Override
    public void getUserInfoById(String id, Model model)
    {
        List<EmployeeWelfareInfo> welfareInfos =
                Stream.of(UserTitle.values()).map(UserTitle::get).collect(Collectors.toList());
        lockService.runWithLock("getUser" + id, "getUser", () -> getUser(model));

        //search by id, get UserInfo
//        MVCMybatisDemoUser user = mVCMybatisDemoUserMapper.queryUserInfo(id);
//        model.addAttribute("name", user.getId())
//                .addAttribute("age", user.getAge())
//                .addAttribute("height", user.getHeight())
//                .addAttribute("weight", user.getWeight());
    }

    @Override
    public long generateUserId() {
        long tsCode = System.currentTimeMillis(); //随机码
        long seq = SEQ_MAX & getUserId(); //用户id（一般由发号器或数据库自增主键生成）
        long hostCode = HOST_MAX & Optional.of(LocalHostUtil.getLocalHostName()).map(String::hashCode).orElse(0); //机器码
        return (Long.MAX_VALUE & (tsCode << TS_OFFSET)) | (hostCode << HOST_OFFSET) | (seq << SEQ_OFFSET);
    }

    @Override
    public String getUserProfile() {
        String join = Joiner.on("_").join("a", "b");
        return join;
    }

    @Override
    public List<UserProfileView> getProfileView() {
//        checkNotNull(PROFILE_LOADER_SUPPLIER.get(), "No available loader for profile");
        PROFILE_LOADER_SUPPLIER.get().load();
        logger.info("user time:{}", userTimestamp);
        List<UserProfileView> userProfileViews =
                fromJSON(USER_PROFILE_VIEW_STR, List.class, UserProfileView.class);
        List<UserProfileView> result = new ArrayList<>();
        JsonNode jsonNode = fromJSON(USER_PROFILE_INFO, JsonNode.class);
        userProfileViews.forEach(view -> {
            UserProfileView curr = new UserProfileView();
            curr.setViewParam(view.getViewParam());
            String origin = jsonNode.get(view.getKey()).asText();
            String real = ofNullable(origin)
                    .map(o -> view.getMappingFunc().apply(o))
                    .orElse("未知");
            curr.setViewValue(real);
            result.add(curr);
        });
        return result;
    }

    @Override
    public void batchProcessUserInfo(List<User> userList) {
        List<List<User>> batchProcessList = initList();
        for (User user : userList) {
            batchProcessList.get(getProcessShard(user.getUserName())).add(user);
        }
        //相同名字的用户由同一个线程处理
        batchProcessList.forEach(users -> pool.submit(() -> batchProcessUser(users)));
    }

    private void batchProcessUser(List<User> userList) {
        userList.forEach(user -> System.out.println(user.getUserName() + ":" + Thread.currentThread().getName()));
    }

    public static int getProcessShard(String userName) {
        if (isBlank(userName)) {
            return 0;
        }
        return abs(userName.hashCode() % USER_SHARD_SIZE);
    }

    private List<List<User>> initList() {
        List<List<User>> result = new ArrayList<>();
        for (int i = 0; i < USER_SHARD_SIZE; i++) {
            result.add(new ArrayList<>());
        }
        return result;
    }

    private void getUser(Model model) {

    }

    /**
     * guava方案
     */
    @Override
    public void batchGetUserInfo() {
        ListenableFuture<String> info1 = listeningExecutor.submit(() ->
                Thread.currentThread().getName() + " :info1");
        ListenableFuture<String> info2 = listeningExecutor.submit(() ->
                Thread.currentThread().getName() + " :info2");
        ListenableFuture<String> info3 = listeningExecutor.submit(() -> {
            sleepUninterruptibly(3000, TimeUnit.MILLISECONDS);
            System.out.println("sleep done");
            return Thread.currentThread().getName() + " :info3";
        });
        try {
            //这里在1s内如果某个任务没完成，会直接报错
            //适用于必须在1s内返回的场景
            Futures.allAsList(info1, info2, info3).get(1000L, TimeUnit.MILLISECONDS);
            System.out.println(info1.get());
            System.out.println(info2.get());
            System.out.println(info3.get());
        } catch (Exception e) {
            logger.error("get user info failed", e);
            sleepUninterruptibly(3000L, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 替代方案 java8
     */
    @Override
    public void batchGetUserInfo0() {
        CompletableFuture<String> info1 = CompletableFuture.supplyAsync(()
                -> Thread.currentThread().getName() + " :info1", pool);
        CompletableFuture<String> info2 = CompletableFuture.supplyAsync(()
                -> Thread.currentThread().getName() + " :info2", pool);
        CompletableFuture<String> info3 = CompletableFuture.supplyAsync(()
                -> {
            sleepUninterruptibly(3000, TimeUnit.MILLISECONDS);
            System.out.println("sleep done");
            return Thread.currentThread().getName() + " :info3";
            }, pool);
        try {
            CompletableFuture.allOf(info1, info2, info3).get(1000L, TimeUnit.MILLISECONDS);
            System.out.println(info1.get());
            System.out.println(info2.get());
            System.out.println(info3.get());
        } catch (Exception e) {
            logger.error("get user info failed", e);
            sleepUninterruptibly(3000L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void batchUpdateUserInfo() {
        ListenableFuture<Boolean> info1 = listeningExecutor.submit(() -> {
            System.out.println(Thread.currentThread().getName() + " :update info1");
            return true;
        });
        ListenableFuture<Boolean> info2 = listeningExecutor.submit(() -> {
            System.out.println(Thread.currentThread().getName() + " :update info2");
            sleepUninterruptibly(2000L, TimeUnit.MILLISECONDS);
            throw new RuntimeException(Thread.currentThread().getName() + ":error");
//            return true;
        });
        //info1和info2都更新成功，才可以更新info3
        ListenableFuture<Boolean> result = Futures.whenAllSucceed(info1, info2)
                .call(() -> {
                    List<Boolean> resultList = Futures.allAsList(info1, info2).get();
                    System.out.println("info1 2 finished");
                    return resultList.stream().allMatch(s -> s) && updateInfo3();
        }, listeningExecutor);
        try {
            result.get();
        }  catch (Exception e) {
            logger.error("update user info failed", e);
        }
        sleepUninterruptibly(5000L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void batchUpdateUserInfo0() {
        CompletableFuture<Boolean> info1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " :update info1");
            return true;
        }, pool);
        CompletableFuture<Boolean> info2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " :update info2");
            throw new RuntimeException(Thread.currentThread().getName() + ":error");
//            return true;
        }, pool);
//        System.out.println(info2.join());
        CompletableFuture<Boolean> finalResult =
                CompletableFuture.allOf(info1, info2).thenApplyAsync(v -> {
                    System.out.println(Thread.currentThread().getName() + "begin final");
                    Stream<Boolean> resultList = Stream.of(info1, info2)
                            .map(CompletableFuture::join);
                    return resultList.allMatch(s -> s) && updateInfo3();
                }, pool);
        try {
            System.out.println(finalResult.get(1000L, TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            logger.error("update user info failed", e);
        }
    }

    private boolean updateInfo3() {
        System.out.println(Thread.currentThread().getName() + " :update info3");
        return true;
    }

    @Component
    @Lazy
    @Scope("prototype") //为了测试上面的singletonSupplier，这里手动改成多例模式
    private static class UserProfileLoader {

        private static final Logger logger = LoggerFactory.getLogger(UserProfileLoader.class);

        private ScheduledExecutorService executorService;
        private final AtomicBoolean threadInitialized = new AtomicBoolean(false);
        private static final int PERIOD_SECONDS = 5;

        public UserProfileLoader() {
            logger.info("create loader");
        }

        @PostConstruct
        public void init() {
            ThreadFactory factory = new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("user-profile-load-thread-%d")
                    .build();
            executorService = Executors.newSingleThreadScheduledExecutor(factory);
        }

        public void load() {
            if (threadInitialized.get()) {
                return;
            }
            if (threadInitialized.compareAndSet(false, true)) {
                Runtime.getRuntime().addShutdownHook(new Thread(this::stop, "zhaoh-t"));
                executorService.scheduleAtFixedRate(this::doLoad, 0L, PERIOD_SECONDS, TimeUnit.SECONDS);
            }
        }

        private void stop() {
            logger.info("stop the schedule thread");
            MoreExecutors.shutdownAndAwaitTermination(executorService, 30, TimeUnit.SECONDS);
        }

        private void doLoad() {
            logger.info("start load user profile by thread:{}", Thread.currentThread().getName());
            userTimestamp = String.valueOf(System.currentTimeMillis());
        }
    }
}
