package hanteen.web.pro.service.util;

import java.util.concurrent.ConcurrentHashMap;

import hanteen.web.pro.service.constant.TaskType;
import hanteen.web.pro.service.user.UserTaskManager;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
public class UserTaskUtils {

    private static final ConcurrentHashMap<TaskType, UserTaskManager> taskManagers = new ConcurrentHashMap<>();

    public static UserTaskManager getTaskManager(TaskType type) {
        if (!type.isValid()) {
            throw new IllegalArgumentException("Necessary config is lacked for task " + type);
        }
        return taskManagers.computeIfAbsent(type, k -> new UserTaskManager(type));
    }
}
