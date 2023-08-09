package hanteen.web.pro.service.model;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;

import hanteen.web.pro.model.mybatis.constant.UserTitle;
import hanteen.web.pro.service.util.CompareUtils;

/**
 * 筛选用户的基础条件
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-08-09
 */
public class UserScreeningBasicCondition implements Comparable<UserScreeningBasicCondition> {
    private Set<Long> blackList = Collections.emptySet();
    private boolean needCheckSeniority;
    private Set<Integer> age; //这里用guava的RangeSet可读性强一些，但已经被弃用了
    private int minPaperCount;
    private UserTitle userTitle;

    public UserScreeningBasicCondition() {
    }

    public Set<Long> getBlackList() {
        return blackList;
    }

    public void setBlackList(Set<Long> blackList) {
        this.blackList = blackList;
    }

    public boolean isNeedCheckSeniority() {
        return needCheckSeniority;
    }

    public void setNeedCheckSeniority(boolean needCheckSeniority) {
        this.needCheckSeniority = needCheckSeniority;
    }

    public Set<Integer> getAge() {
        return age;
    }

    public void setAge(String age) {
        if (isNotBlank(age)) {
            String[] rangeArray = age.split(";");
            if (rangeArray.length > 0) {
                Set<Integer> ageSet = new HashSet<>();
                Stream.of(rangeArray).forEach(range -> {
                    Set<Integer> curr = parseRangeStr(range);
                    if (isNotEmpty(curr)) {
                        ageSet.addAll(curr);
                    }
                });
                this.age = ageSet;
            }
        }
    }

    @JsonIgnore
    private Set<Integer> parseRangeStr(String rangeStr) {
        String[] rangeArr = rangeStr.split("-");
        try {
            if (rangeArr.length < 1) {
                return null;
            } else if (rangeArr.length == 1) {
                int singleAge = Integer.parseInt(rangeArr[0]);
                return Collections.singleton(singleAge);
            } else {
                int low = Integer.parseInt(rangeArr[0]);
                int up = Integer.parseInt(rangeArr[1]);
                if (low >= up) {
                    return null;
                }
                return IntStream.rangeClosed(low, up)
                        .boxed()
                        .collect(Collectors.toSet());
            }
        } catch (Exception e) {
            return null;
        }
    }

    public int getMinPaperCount() {
        return minPaperCount;
    }

    public void setMinPaperCount(int minPaperCount) {
        this.minPaperCount = minPaperCount;
    }

    public UserTitle getUserTitle() {
        return userTitle;
    }

    public void setUserTitle(UserTitle userTitle) {
        this.userTitle = userTitle;
    }

    public static void main(String[] args) {
        List<UserScreeningBasicCondition> list = new ArrayList<>();
        UserScreeningBasicCondition u1 = new UserScreeningBasicCondition();
        UserScreeningBasicCondition u2 = new UserScreeningBasicCondition();
        UserScreeningBasicCondition u3 = new UserScreeningBasicCondition();
//        u2.setBlackList(Collections.singleton(1111L));
        u2.setAge("10-15;14-18;");
        list.add(u1);
        list.add(u2);
        list.add(u3);
        System.out.println(list.toString());
        Collections.sort(list);
        System.out.println(list.toString());
    }

    @JsonIgnore
    public boolean isOnFor(long userId, int age, int paperCount, boolean senioritySufficient) {
        if (isNotEmpty(blackList) && blackList.contains(userId)) {
            return false;
        }
        if (minPaperCount > 0 && minPaperCount > paperCount) {
            return false;
        }
        if (needCheckSeniority && !senioritySufficient) {
            return false;
        }
        return isEmpty(this.age) || this.age.contains(age);
    }

    @Override
    public int compareTo(UserScreeningBasicCondition o) {
        if (this == o) {
            return 0;
        }
        //返回值大于0 o在前 否者o在后
        return ComparisonChain.start()
                .compare(this.blackList, o.getBlackList(), CompareUtils::emptyLowPriority) //内容为空排在后面
                .compare(this.age, o.age, CompareUtils::emptyLowPriority)
                .compare(o.getMinPaperCount(), this.minPaperCount) //值小的排在后面
                .result();
    }

    @Override
    public String toString() {
        return "UserScreeningBasicCondition{" +
                "blackList=" + blackList +
                ", age=" + age +
                '}';
    }
}
