package controllers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Access {

    /**
     * 
     * 角色
     */
    String role();

    /**
     * 
     * 权限的种类
     */
    String type() default "";
}
