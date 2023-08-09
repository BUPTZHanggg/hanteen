package hanteen.web.pro.service.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;

import hanteen.web.pro.service.model.Artist;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-04
 */
public class RandomUtils {

    public static <E> E selectByWeight(Function<E, Integer> weightFunc, Collection<E> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            return null;
        }
        int sum = candidates.stream().mapToInt(weightFunc::apply).sum();
        //这里一定要每次都调一下current()，不可以多线程共用 static ThreadLocalRandom RANDOM = ThreadLocalRandom.current(); 这种方式
        int random = ThreadLocalRandom.current().nextInt(sum); //[0,sum)
        int  toAccumulate = 0;
        for (E candidate : candidates) {
            int curr = weightFunc.apply(candidate);
            toAccumulate += curr;
            if (toAccumulate > random) {
                return candidate;
            }
        }
        return null; //理论走不到这里，其实也可以抛个异常
    }

    static ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    /**
     * @link <a href="https://zhuanlan.zhihu.com/p/435280965">...</a>
     * @param args
     */
    public static void main(String[] args) {
        //RANDOM的实例化是main线程执行的，因此main线程的SEED是正常初始化的（在current()中），所以下面两行都是正常的
        System.out.println(RANDOM.nextInt(10));
        System.out.println(RANDOM.nextInt(10));

        //下面的线程由于都没有调用current()，SEED都没有初始化，所有线程的随机值都相同
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread() + " random:" + RANDOM.nextInt(10));
            }).start();
        }

        List<Artist> candidates = new ArrayList<Artist>(){{
            add(new Artist("111"));
            add(new Artist("222"));
        }};
        Artist artist = RandomUtils.selectByWeight(Artist::getWeight, candidates);
        System.out.println(artist.getName());
        Artist artist1 = RandomUtils.selectByWeight(candidates);
        System.out.println(artist1.getName());
    }

    public static <E extends HasWeight> E selectByWeight(Collection<E> candidates) {
        if (CollectionUtils.isEmpty(candidates)) {
            return null;
        }
        int sum = candidates.stream().mapToInt(E::getWeight).sum();
        //这里一定要每次都调一下current()，不可以多线程共用 static ThreadLocalRandom RANDOM = ThreadLocalRandom.current(); 这种方式
        int random = ThreadLocalRandom.current().nextInt(sum); //[0,sum)
        int  toAccumulate = 0;
        for (E candidate : candidates) {
            int curr = candidate.getWeight();
            toAccumulate += curr;
            if (toAccumulate > random) {
                return candidate;
            }
        }
        return null; //理论走不到这里，其实也可以抛个异常
    }

    public interface HasWeight {
        int getWeight();
    }

}
