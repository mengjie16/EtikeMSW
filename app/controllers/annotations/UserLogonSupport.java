package controllers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 标注必须用户登录的页面才可以访问的controller的method
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月3日 上午2:18:26
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UserLogonSupport {

    String value() default "";
}
