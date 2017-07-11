package controllers.base.secure;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.Constant;
import com.aton.config.AppMode;
import com.aton.config.ReturnCode;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.StringUtils;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.taobao.api.internal.util.WebUtils;

import controllers.annotations.UploadSupport;
import controllers.annotations.UserLogonSupport;
import enums.constants.BizConstants;
import enums.constants.CacheType;
import models.HomePageSetting;
import models.Supplier;
import models.User;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Http.Cookie;
import play.mvc.Util;
import utils.QnCloudUtil.QnFileBucket;

/**
 * 
 * 安全校验过滤器<br>
 * 
 * 1、检查用户（包括普通用户、Admin等）操作是否合法.<br>
 * 2、对普通用户的操作进行log，并持久化到MongoDB.<br>
 * 
 * @priority = 1~3
 * @author tr0j4n
 * @since v1.0
 */
public class Secure extends BaseSecure {

    private static final Logger log = LoggerFactory.getLogger(Secure.class);

    /**
     * 普通用户的Restful身份验证码
     */
    public static final String FIELD_AUTH = "authcode";

    public static final String ADMIN_FIELD_AUTH = "admin_authcode";

    public static final String UPLOAD_BUCKET = "upload_bucket";

    public static final String UPLOAD_DOMAIN = "upload_domain";

    /**
     * 
     * 最先进来的拦截分发
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月3日 上午1:28:34
     */
    @Before(priority = 1)
    public static void commonFilter() {
        // 打印URL访问
        log.debug("{} {} -{}", request.remoteAddress, request.url,
            DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        /* --------------- route rule check ----------------------- */
        // 管理员路由全部需要拦截判定
        if (request.url.startsWith("/sys")) {
            checkAdminAccess();
        }
        // 供应商路由校验
        if (request.url.startsWith("/supplier")) {
            checkSupplierAccess();
        }
        // 零售商路由校验
        if (request.url.startsWith("/retailer")) {
            checkRetailerAccess();
        }
        // 如果用户处于登录状态，装载用户信息到render中
        User user = tryGetUserFromContainer();
        // 添加用户数据，供页面显示用
        if (user != null) {
            renderArgs.put(FIELD_USER, user);
        }

        // ------------- 页面配置信息输出
        if (!"POST".equalsIgnoreCase(request.method) && !request.isAjax()) {
            // 查找主页配置
            HomePageSetting homeSet = HomePageSetting.findBuildByCache();
            renderArgs.put("homeSet", homeSet);
        }

        // ------------- 资源及相关特殊需求
        // 正式环境
        if (AppMode.get().mode == AppMode.Mode.ONLINE) {
            renderArgs.put("IS_ONLINE", true);
        } else { // 开发及其他环境
            renderArgs.put("IS_ONLINE", false);
        }
    }

    /**
     * 
     * 用户不登录页面不能访问
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月3日 上午2:21:06
     */
    @Before(priority = 2)
    public static void checkAuthentication() {
        Annotation[] annos = request.invokedMethod.getAnnotations();
        boolean logonNeed = Arrays.asList(annos).stream().anyMatch((a) -> {
            return a.annotationType() == UserLogonSupport.class;
        });
        if (!logonNeed) {
            return;
        }
        User user = tryGetUserFromContainer();
        // 获得不到用户信息，就不让他看
        if (user == null) {
            handleIllegalRequest(ReturnCode.INVALID_SESSION);
        }
    }

    /**
     * 
     * 页面需要提供资源配置
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月3日 上午1:27:15
     */
    @Before(priority = 3)
    public static void resourceNeedFilter() {
        Annotation[] annos = request.invokedMethod.getAnnotations();
        List<Annotation> annoList = Arrays.asList(annos);
        if (MixHelper.isEmpty(annoList)) {
            return;
        }
        boolean need = annoList.stream().anyMatch((a) -> {
            return a.annotationType() == UploadSupport.class;
        });
        // 上传资源配置
        if (need) {
            // 设置七牛图片上传空间
            if (AppMode.get().mode == AppMode.Mode.DEV) {
                renderArgs.put(UPLOAD_DOMAIN, QnFileBucket.DEV.domain);
            } else if (AppMode.get().mode == AppMode.Mode.ONLINE) {
                renderArgs.put(UPLOAD_DOMAIN, QnFileBucket.ONLINE_PUBLIC.domain);
            } else {
                renderArgs.put(UPLOAD_DOMAIN, QnFileBucket.DEV.domain);
            }
        }
    }

    /*
     * 是否本地开发测试
     */
    @Util
    private static boolean isDevOnLocalHost() {
        String ip = request.remoteAddress;

        // 本地开发测试模式下无来访IP
        if (StringUtils.isBlank(ip) && AppMode.get().mode == AppMode.Mode.TEST) {
            return true;
        }

        // 根据访问的IP来判断
        return Constant.LOCAL_HOST_IP.equals(ip) && AppMode.get().isNotOnline();
    }

    /*
     * 检查是否有管理员操作权限
     */
    @Util
    static void checkAdminAccess() {
        if (AppMode.get().isNotOnline()) {
            return;
        }
        if (Objects.equal(request.path, "/sys/enter") || Objects.equal(request.path, "/sys/doLogin")) {
            return;
        }
        // 获取用户信息,检查用户是否管理员
        User user = tryGetManageFromContainer();
        if (user != null) {
            return;
        }
        // 转向管理员登录页
        redirect("/sys/enter?redirectUrl=" + WebUtils.encode(request.url));
    }

    /**
     * 
     * 尝试获取普通用户
     *
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月3日 上午1:58:13
     */
    @Util
    public static User tryGetUserFromContainer() {

        // 首次校验
        Cookie atonCookie = request.cookies.get(FIELD_AUTH);
        if (atonCookie == null) {
            return null;
        }
        String code = atonCookie.value;
        int codeLength = code.length();
        if (codeLength == 0) {
            return null;
        }
        String num1Str = code.substring(1, 2);
        String num2Str = code.substring(code.length() - 1, code.length());

        int num1 = NumberUtils.toInt(num1Str);
        int num2 = NumberUtils.toInt(num2Str);
        if (num1 + num2 != 8) {
            return null;
        }
        // 去缓存里找，有没有authcode对应的userId
        String cacheKey = CacheType.AUTH_CODE.getKey(code);
        Long usrId = CacheUtils.get(cacheKey);
        if (usrId == null) {
            return null;
        }

        // 通过缓存取得用户的数据
        User user = User.findByIdWichCache(usrId);

        return user;
    }

    /**
     * 试图获取管理员
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 下午6:41:36
     */
    public static User tryGetManageFromContainer() {

        // 首次校验
        Cookie atonCookie = request.cookies.get(ADMIN_FIELD_AUTH);
        if (atonCookie == null) {
            return null;
        }
        String code = atonCookie.value;
        int codeLength = code.length();
        if (codeLength == 0) {
            return null;
        }
        String num1Str = code.substring(1, 2);
        String num2Str = code.substring(code.length() - 1, code.length());

        int num1 = NumberUtils.toInt(num1Str);
        int num2 = NumberUtils.toInt(num2Str);
        if (num1 + num2 != 8) {
            return null;
        }
        // 去缓存里找，有没有authcode对应的userId
        String cacheKey = CacheType.AUTH_CODE.getKey(code);
        Long usrId = CacheUtils.get(cacheKey);
        if (usrId == null) {
            return null;
        }

        // 通过缓存取得用户的数据
        User user = new User();
        return user;
    }

    /**
     * 用户退出
     *
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午4:24:24
     */
    @Util
    public static void removeUserByContainer() {
        // 首次校验
        Cookie atonCookie = request.cookies.get(FIELD_AUTH);
        if (atonCookie == null) {
            return;
        }
        String code = atonCookie.value;
        int codeLength = code.length();
        if (codeLength == 0) {
            return;
        }
        String num1Str = code.substring(1, 2);
        String num2Str = code.substring(code.length() - 1, code.length());

        int num1 = NumberUtils.toInt(num1Str);
        int num2 = NumberUtils.toInt(num2Str);
        if (num1 + num2 != 8) {
            return;
        }
        // 去缓存里找，有没有authcode对应的userId
        String cacheKey = CacheType.AUTH_CODE.getKey(code);
        Long usrId = CacheUtils.get(cacheKey);
        // 移除缓存
        response.removeCookie(FIELD_AUTH);
        CacheUtils.remove(cacheKey);
        if (usrId != null) {
            User.removeCache(usrId);
        }
    }

    /**
     * 管理员登录设置
     *
     * @param usr
     * @since v1.0
     * @author Calm
     * @created 2016年7月26日 下午12:48:21
     */
    @Util
    public static void setAdminToContainer(User usr) {
        Validate.notNull(usr);
        // 生成Authcode
        UUID uuid = UUID.randomUUID();
        String code = StringUtils.replace(uuid.toString(), "-", "");
        int num1 = RandomUtils.nextInt(0, 9);
        int num2 = 8 - num1;
        code = code.substring(0, 1) + num1 + code.substring(1) + num2;

        String cacheKey = CacheType.AUTH_CODE.getKey(code);
        CacheUtils.set(cacheKey, Long.valueOf(usr.id), CacheType.AUTH_CODE.expiredTime);
        log.info("User id={}, Admin_AuthCode={} set to security container", usr.id, code);

        response.setCookie(ADMIN_FIELD_AUTH, code, "300d");
    }

    /**
     * 
     * 把用户设置进安全容器
     *
     * @param usr
     * @since v1.0
     * @author tr0j4n
     * @created 2016年5月3日 上午2:02:25
     */
    @Util
    public static void setUserToContainer(User usr) {
        setUserToContainer(usr, CacheType.AUTH_CODE.expiredTime);
    }

    /**
     * 设置
     *
     * @param usr
     * @param duration
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午4:07:19
     */
    @Util
    public static void setUserToContainer(User usr, String duration) {
        Validate.notNull(usr);
        Validate.notEmpty(duration);
        // 生成Authcode
        UUID uuid = UUID.randomUUID();
        String code = StringUtils.replace(uuid.toString(), "-", "");
        int num1 = RandomUtils.nextInt(0, 9);
        int num2 = 8 - num1;
        code = code.substring(0, 1) + num1 + code.substring(1) + num2;

        String cacheKey = CacheType.AUTH_CODE.getKey(code);

        CacheUtils.set(cacheKey, Long.valueOf(usr.id), duration);
        log.info("User id={}, AuthCode={} set to security container with duration", usr.id, code);

        response.setCookie(FIELD_AUTH, code, duration);
    }

    /**
     * 供应商route校验
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 上午1:16:38
     */
    @Util
    static void checkSupplierAccess() {
        // 获取用户信息,检查用户是否管理员
        User user = tryGetUserFromContainer();
        if (user == null) {
            if (request.isAjax()) {
                // 看情况是否需要ajax和普通请求反应
                renderFailedJson(ReturnCode.INVALID_SESSION);
            }
            redirect("/login?redirectUrl=" + WebUtils.encode(request.url));
        }
        if (Objects.equal(user.role, "SUPPLIER")) {
            // 输出供应商信息
            Supplier supplier = Supplier.findById(user.userId);
            supplier.calcDataPreview();
            renderArgs.put("supplier", supplier);
            return;
        }
        renderFailedJson(ReturnCode.INVALID_SESSION);
    }

    /**
     * 零售商route校验
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 上午1:16:57
     */
    @Util
    static void checkRetailerAccess() {
        // 获取用户信息,检查用户是否管理员
        User user = tryGetUserFromContainer();
        if (user == null) {
            if (request.isAjax()) {
                // 看情况是否需要ajax和普通请求反应
                renderFailedJson(ReturnCode.INVALID_SESSION);
            }
            redirect("/login?redirectUrl=" + WebUtils.encode(request.url));
        }
        if (Objects.equal(user.role, "RETAILER")) {
            // TODO 零售商预览信息
            return;
        }
        // 看情况是否需要ajax和普通请求反应
        renderFailedJson(ReturnCode.INVALID_SESSION);
    }

    /**
     * 获取有效访问pv可行的key
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月31日 上午9:39:51
     */
    @Util
    public static String getValidAccessPvCookieKey() {
        // http访问安全校验的cookie
        Cookie safeCookie = request.cookies.get(BizConstants.USER_AUTH_COOKIE);
        // 用户登录状态下的cookie
        Cookie userLoginCookie = request.cookies.get(FIELD_AUTH);
        String key = "";
        if (safeCookie != null) {
            key += Optional.fromNullable(safeCookie.value).or("");
        }
        if (userLoginCookie != null) {
            key += Optional.fromNullable(userLoginCookie.value).or("");
        }
        if (Strings.isNullOrEmpty(key)) {
            key = Codec.hexMD5(key);
        }
        return key;
    }

}
