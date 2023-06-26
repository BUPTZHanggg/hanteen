package hanteen.web.pro.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

/**
 * @author zhaohang <zhaohang06@kuaishou.com>
 * Created on 2022-06-10
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface ImportantService {

    @AliasFor("a2")
    String a1() default "";

    @AliasFor("a1")
    String a2() default "";

    String value() default "";
}
