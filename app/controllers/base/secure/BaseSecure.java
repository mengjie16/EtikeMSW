package controllers.base.secure;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.AppMode;
import com.aton.config.ReturnCode;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.util.RegexUtils;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import controllers.annotations.OpenInWeixinRequiredSupport;
import controllers.annotations.VulnerableSupport;
import controllers.base.BaseController;
import enums.constants.BizConstants;
import enums.constants.CacheType;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Http.Cookie;
import play.mvc.Util;

/**
 * 基础安全校验过滤器
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月7日 下午5:47:09
 */
public class BaseSecure extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseSecure.class);

    /**
     * 防御CSRF攻击（所有的POST请求必须带上token）
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月7日 下午5:45:28
     */
    @Before(priority = 0)
    public static void defenseCheck() {
        // 校验http访问安全
        checkHttpSafe();
        checkParamHttpSafe();
        // POST请求校验
        checkAuthenticityToken();
        // 检查用户是否一定要微信浏览器打开
        checkUserAgent();
    }

    /**
     * POST请求校验
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月7日 下午5:45:50
     */
    @Util
    private static void checkAuthenticityToken() {
        // post 请求校验
        if (AppMode.Mode.TEST == AppMode.get().mode) {
            MixHelper.print("Mode:TEST,Skipped Authenticity");
            return;
        }
        if ("POST".equalsIgnoreCase(request.method)) {
            checkAuthenticity();
        }
    }

    /**
     * 请求失效
     *
     * @param failedReturnCode
     * @since v1.0
     * @author Calm
     * @created 2016年7月7日 下午5:43:00
     */
    @Util
    public static void handlerFailedRequest(int failedReturnCode) {
        renderFailedJson(failedReturnCode);
    }

    /**
     * 
     * 颁发一个合法的可以访问网站的cookie，不论游客还是登录用户
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月9日 下午10:33:28
     */
    @Util
    private static void regrantUserAccessCookie() {
        String uToken = StringUtils.remove(Codec.UUID(), "-");
        response.setCookie(BizConstants.USER_AUTH_COOKIE, uToken);
        String userCookieStateKey = CacheType.REQUEST_USER_COOKIE_STATE.getKey(uToken);
        CacheUtils.set(userCookieStateKey, Boolean.TRUE, CacheType.REQUEST_USER_COOKIE_STATE.expiredTime);
    }

    /**
     * 基本sql注入访问控制
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月23日 下午1:06:39
     */
    @Util
    private static void checkParamHttpSafe() {
        Map<String, String> paramsn = request.params.allSimple();
        if (MixHelper.isEmpty(paramsn)) {
            return;
        }
        boolean match = paramsn.values().parallelStream().filter(s -> !Strings.isNullOrEmpty(s))
            .anyMatch(s -> RegexUtils.isMatch(s.toLowerCase(), "^(where|create|insert|drop|select|set|delete)$"));
        if (!match) {
            return;
        }
        if (!request.isAjax()) {
            log.error("There are evil words in parameters");
            renderTemplate("errors/400.html", "无访问权限");
        }
        renderFailedJson(ReturnCode.WRONG_INPUT, "无访问权限");
    }

    /**
     * 校验服务安全
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月7日 下午5:42:41
     */
    @Util
    private static void checkHttpSafe() {
        // http访问安全校验
        Cookie cookie = request.cookies.get(BizConstants.USER_AUTH_COOKIE);
        // 如果没有cookie设置后，跳转当前
        if (cookie == null || Strings.isNullOrEmpty(cookie.value)) {
            regrantUserAccessCookie();
            // 这里还需要另有安全机制处理
            redirect(request.url);
        }
        String uToken = cookie.value;
        String userCookieStateKey = CacheType.REQUEST_USER_COOKIE_STATE.getKey(uToken);
        Object requestAccessCookie = CacheUtils.get(userCookieStateKey);
        // 说明该userCookie是伪造的，之前并没有颁发过这个cookie。盖伦的Cookie具有检查豁免权
        if (requestAccessCookie == null && !"Garen".equals(uToken)) {
            regrantUserAccessCookie();
            redirect(request.url);
        }

        // 访问时间频率限制
        CacheType cacheType = CacheType.CLIENT_GET_REQUEST_TIME;
        // GET 访问，只有带有特定标注的才需要控制
        if ("GET".equalsIgnoreCase(request.method)) {
            Annotation[] annos = request.invokedMethod.getAnnotations();
            boolean checkNeed = Arrays.asList(annos).stream().anyMatch((a) -> {
                return a.annotationType() == VulnerableSupport.class;
            });
            if (!checkNeed) {
                return;
            }
        } else {
            // 除了get暂时都按照post规则控制
            cacheType = CacheType.CLIENT_POST_REQUEST_TIME;
        }
        String key = cacheType.getKey(uToken, Codec.hexMD5(request.path));
        Object abVisitied = CacheUtils.get(key);
        if (abVisitied == null) {
            // 当前记录，为下一次做准备
            CacheUtils.set(key, Boolean.TRUE, cacheType.expiredTime);
            return;
        }
        // 访问过频
        if (request.isAjax()) {
            renderFailedJson(ReturnCode.INVALID_SESSION, "您的访问过于频繁");
        } else {
            renderArgs.put("errorMessages", ImmutableList.of("错误码:" + ReturnCode.INVALID_SESSION));
            render("Application/error2.html");
        }
    }

    /**
     * 检查用户是否需要微信访问
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午3:55:04
     */
    @Util
    public static void checkUserAgent() {
        if (AppMode.Mode.ONLINE != AppMode.get().mode) {
            // 开发及本地模式不校验，微信打开和微信授权
            renderArgs.put("checkAuth", false);
            return;
        }
        Annotation[] annos = request.invokedMethod.getAnnotations();
        boolean checkNeed = Arrays.asList(annos).stream().anyMatch((a) -> {
            return a.annotationType() == OpenInWeixinRequiredSupport.class;
        });
        if (!checkNeed) {
            return;
        }
        String userAgent = request.headers.get("user-agent").value();
        if (Strings.isNullOrEmpty(userAgent) || userAgent.indexOf("MicroMessenger") == -1) {
            if (!request.isAjax()) {
                log.error("userAgent is error,can't open without wechat !");
                renderTemplate("errors/400.html", "无访问权限！");
            }
            renderFailedJson(ReturnCode.WRONG_INPUT, "无访问权限！");
        }
    }
}
