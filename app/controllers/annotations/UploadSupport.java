package controllers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import utils.QnCloudUtil.QnFileBucket;

/**
 * 
 * 标注controller中需要上传组件的页面的method
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月3日 上午2:17:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface UploadSupport {

    String bucket() default "tusibaby";
}
