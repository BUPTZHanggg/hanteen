package hanteen.web.pro.model.mybatis.model;

/**
 * 员工福利的具体信息
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2023-05-06
 */
public class EmployeeWelfareInfo {

    private double salary;
    private int vocationCount;

    public EmployeeWelfareInfo() {
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getVocationCount() {
        return vocationCount;
    }

    public void setVocationCount(int vocationCount) {
        this.vocationCount = vocationCount;
    }
}
