package hanteen.web.pro.service.user.impl;

import static hanteen.web.pro.service.constant.TaskType.work;

import hanteen.web.pro.service.model.UserWorkTaskInfo;
import hanteen.web.pro.service.user.UserTaskManager;
import hanteen.web.pro.service.user.UserTaskService;
import hanteen.web.pro.service.util.UserTaskUtils;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-07-03
 */
public class UserTaskServiceImpl implements UserTaskService {

    private static final UserTaskManager WORK_TASK_MANAGER = UserTaskUtils.getTaskManager(work);

    @Override
    public void submitWorkTask(UserWorkTaskInfo task) {
        long taskId = 10086L; //generate task id
        long count = WORK_TASK_MANAGER.submit(taskId, task);
        //上报队列长度
        long newTask = WORK_TASK_MANAGER.fetch();
        UserWorkTaskInfo taskInfo = WORK_TASK_MANAGER.getTaskInfo(newTask, UserWorkTaskInfo.class);
    }
}
