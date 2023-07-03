package hanteen.web.pro.service.constant;

import static hanteen.web.pro.service.constant.UserRedisKey.userLifeTaskInfo;
import static hanteen.web.pro.service.constant.UserRedisKey.userLifeTasks;
import static hanteen.web.pro.service.constant.UserRedisKey.userWorkTaskInfo;
import static hanteen.web.pro.service.constant.UserRedisKey.userWorkTasks;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-29
 */
public enum TaskType {
    work(userWorkTasks, userWorkTaskInfo),
    life(userLifeTasks, userLifeTaskInfo),
    ;

    private final UserRedisKey queueKey;
    private final UserRedisKey infoKey;

    TaskType(UserRedisKey queueKey, UserRedisKey infoKey) {
        this.queueKey = queueKey;
        this.infoKey = infoKey;
    }

    public UserRedisKey getQueueKey() {
        return queueKey;
    }

    public UserRedisKey getInfoKey() {
        return infoKey;
    }
    public boolean isValid() {
        return queueKey != null && infoKey != null;
    }
}
