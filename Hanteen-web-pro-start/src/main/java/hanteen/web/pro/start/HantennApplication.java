package hanteen.web.pro.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"hanteen.web.pro.*"})
public class HantennApplication {

    public static void main(String[] args) {
        SpringApplication.run(HantennApplication.class, args);
    }

}
