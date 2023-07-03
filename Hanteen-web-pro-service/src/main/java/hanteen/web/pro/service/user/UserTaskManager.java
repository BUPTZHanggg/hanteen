package hanteen.web.pro.service.user;

import static hanteen.web.pro.model.utils.JsonUtils.fromJSON;
import static hanteen.web.pro.model.utils.JsonUtils.toJsonString;
import static org.apache.commons.lang3.StringUtils.isBlank;

import hanteen.web.pro.service.constant.TaskType;
import hanteen.web.pro.service.constant.UserRedisKey;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-06-30
 */
public class UserTaskManager {

    private final UserRedisKey queueKey;
    private final UserRedisKey infoKey;

    public UserTaskManager(TaskType type) {
        this.queueKey = type.getQueueKey();
        this.infoKey = type.getInfoKey();
    }

    public <T> long submit(long taskId, T taskInfo, Object... subKeys) {
        String key = infoKey.key(taskId);
        String infoStr = toJsonString(taskInfo);
        //1.jedis set info
        //2.jedis rpush String.valueOf(taskId);
        String queueKeyStr = queueKey.key(subKeys);
        long totalCount = 100L; //rpush返回队列长度
        return totalCount;
    }

    public long fetch() {
        //jides lpop taskId
        String str = "";
        return isBlank(str) ? 0 : Long.parseLong(str);
    }

    public <T> T getTaskInfo(long taskId, Class<T> clazz) {
        String key = infoKey.key(taskId);
        //jedis get
        String infoStr = "";
        return isBlank(infoStr) ? null : fromJSON(infoStr, clazz);
    }

}
