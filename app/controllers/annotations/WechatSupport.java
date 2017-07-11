package controllers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import utils.QnCloudUtil.QnFileBucket;

/**
 * 标注controller中需要微信配置组件的页面的method
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年9月22日 下午4:32:08
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface WechatSupport {

    String bucket() default "tusibaby";
}
