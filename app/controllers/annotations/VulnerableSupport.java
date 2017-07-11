package controllers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import utils.QnCloudUtil.QnFileBucket;

/**
 * 标注controller中需要控制访问频率的GET的method
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年7月31日 下午4:01:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface VulnerableSupport {
}
