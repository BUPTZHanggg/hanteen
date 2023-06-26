package hanteen.web.pro.service.constant;

import java.util.function.Supplier;

import hanteen.web.pro.service.model.EmployeeWelfareInfo;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-05-06
 */
public enum UserTitle implements Supplier<EmployeeWelfareInfo> {
    ACADEMICIAN, //院士
    PROFESSOR,  //教授
    ASSOCIATE_PROFESSOR, //副教授
    ;

    private final Supplier<EmployeeWelfareInfo> welfareInfoSupplier;

    UserTitle() {
        //这里可以根据具体的title下发对应的配置
        this.welfareInfoSupplier = EmployeeWelfareInfo::new;
    }

    @Override
    public EmployeeWelfareInfo get() {
        return welfareInfoSupplier.get();
    }
}
