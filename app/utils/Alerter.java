package utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.AppMode;
import com.aton.config.Config;
import com.aton.util.MailUtils;

/**
 * 
 * 警报器，对系统异常行为进行报警
 * 
 * @author bwl
 * @since v1.3.1211
 * @created 2015年12月15日 下午4:16:07
 */
public class Alerter {

    private static final Logger log = LoggerFactory.getLogger(Alerter.class);

    /**
     * 
     * 发邮件通知开发人员
     *
     * @param subject 邮件标题
     * @param body 邮件正文
     * @since v1.3.1211
     * @author bwl
     * @created 2015年12月15日 下午4:17:43
     */
    public static void informDevelopers(String subject, String body) {
        // 不在线上正式环境，不发邮件
        if (AppMode.get().isNotOnline()) {
            return;
        }
        for (String mail : Config.developerMails) {
            // 发邮件给开发人员
            boolean result = MailUtils.sendTextMail(mail, "[tusibaby]" + subject, body);
            if (!result) {
                log.error("Send mail to developer {} failed", mail);
            }
        }
    }

    /**
     * 
     * 发邮件通知开发人员
     *
     * @param subject 邮件标题
     * @param ex 异常
     * @since v1.3.1211
     * @author bwl
     * @created 2015年12月15日 下午4:26:50
     */
    public static void informDevelopers(String subject, Throwable ex) {
        // 堆栈拿出为字符串
        String body = ExceptionUtils.getFullStackTrace(ex);
        informDevelopers(subject, body);
    }

}
