package controllers.base;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.AppMode;
import com.aton.config.ReturnCode;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.vo.AjaxResult;
import com.aton.vo.Page;
import com.aton.vo.PageAjaxResult;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.taobao.api.internal.util.WebUtils;

import controllers.base.secure.Secure;
import controllers.base.secure.SessionInvalidException;
import enums.constants.ErrorCode;
import models.User;
import play.Play;
import play.mvc.Catch;
import play.mvc.Controller;

// @formatter:off
/**
 * 
 * 所有Controller的父类，提供公用的便利方法、统一处理异常等.<br>
 * 
 * 【注意】：
 * 1.不要在此类中添加@Before方法，由于Secure\FeatureCheck等拦截器类继承自该类，此类中定义的@Before方法会被重复调用. <br>
 * 		See <a href="http://www.playframework.com/documentation/1.2.5/controllers#interceptions">Play官方文档</a> <br>
 * 
 * 2.添加@After方法时需注意测试，避免上述问题 
 * 
 * @author youblade
 * @since v0.1
 */
// @formatter:on
public class BaseController extends Controller {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    public static final String TAG = "BaseController";

    /**
     * 前后端交互时，存储认证用户身份标识的字段
     */
    public static final String FIELD_USER = "user";
    public static final String FIELD_MANAGER = "manager";

    /**
     * 对返回数据进行包装，格式为：{"code":200,"msg":"","results":"输入对象的JSON结构"}
     */
    protected static void renderJson(Object obj) {
        renderJSON(JsonUtil.toJson(new AjaxResult(ReturnCode.OK, StringUtils.EMPTY, obj)));
    }

    /**
     * 对返回数据进行包装，格式为：{"code":code,"msg":"","results":"输入对象的JSON结构"}
     */
    protected static void renderJson(int code, Object obj) {
        renderJSON(JsonUtil.toJson(new AjaxResult(code, StringUtils.EMPTY, obj)));
    }

    /**
     * 对返回数据进行包装，格式为：{"code":200,"msg":"","results":"输入对象的JSON结构","totalCount":100}
     */
    protected static void renderPageJson(List<?> list, int totalCount) {
        Validate.notNull(list);
        Validate.isTrue(totalCount >= 0);
        renderJSON(JsonUtil.toJson(new PageAjaxResult(list, totalCount)));
    }

    /**
     * 对分页的数据进行输出
     */
    protected static void renderPageJson(Page<?> pageData) {
        renderPageJson(pageData.items, pageData.totalCount);
    }

    /**
     * 对返回数据进行包装，格式为：{"code":200,"msg":""}
     */
    protected static void renderSuccessJson() {
        renderJSON(JsonUtil.toJson(new AjaxResult(ReturnCode.OK)));
    }

    /**
     * 对返回数据进行包装，格式为：{"code":输入的code,"msg":""}
     */
    protected static void renderFailedJson(int code) {
        renderJSON(JsonUtil.toJson(new AjaxResult(code)));
    }

    /**
     * 对返回数据进行包装，格式为：{"code":输入的code,"msg":"输入的msg"}
     */
    protected static void renderFailedJson(int code, String msg) {
        renderJSON(JsonUtil.toJson(new AjaxResult(code, msg)));
    }

    /**
     * 
     * 全局的Session失效异常处理.
     * 
     * @param e
     * @since v0.1
     */
    @Catch(SessionInvalidException.class)
    static void handle(SessionInvalidException e) {
        log.error("---catch SessionInvalidException---");
        handleIllegalRequest(e.code);
    }

    /**
     * 
     * 全局的运行时异常处理.
     * 
     * @param e
     * @since v0.1
     */

    @Catch(RuntimeException.class)
    static void handleServerError(Throwable e) {
        // 为开发时方便调试，显示异常堆栈信息到500页面
        if (Play.mode.isDev() && !request.isAjax()) {
            e.printStackTrace();
            return;
        }
        log.error("===T_T===>Server Fatal Error<===T_T===", e.getMessage());
        log.error(e.getMessage(), e);

        // 针对Ajax请求返回特定格式的消息
        if (request.isAjax()) {
            renderFailedJson(ReturnCode.INNER_ERROR);
        }

        // 跳转到宕机页面
        render("errors/500.html", e);
    }

    /**
     * 
     * 处理非法请求：未认证、会话过期等
     * 
     * @param failedReturnCode 针对Ajax请求返回的错误码
     * @since v0.1
     */
    protected static void handleIllegalRequest(int failedReturnCode) {

        // 针对Ajax请求返回特定格式的消息
        if (request.isAjax()) {
            renderFailedJson(failedReturnCode);
        }

        // 转向登录页
        redirect("/login?redirectUrl=" + WebUtils.encode(request.url));
    }

    /**
     * 
     * 处理错误的用户输入.
     * 
     * @param withErrMsg
     * @since v0.1
     */
    protected static void handleWrongInput(boolean withErrMsg) {
        if (!validation.hasErrors()) {
            return;
        }
        for (play.data.validation.Error error : validation.errors()) {
            try {
                log.error("Input wrong: {}/{}", error.getKey(), error.message());
            } catch (Exception e) {
                log.warn("Input wrong, and Exception={} when log", e.getMessage());
            }

            // 开发测试时打印参数校验错误，便于调试
            if (AppMode.get().isNotOnline()) {
                MixHelper.print("Input wrong: {}", error.message());
            }
            if (!request.isAjax()) {
                renderTemplate("errors/400.html");
            }

            if (withErrMsg) {
                renderFailedJson(ReturnCode.WRONG_INPUT, error.message());
            }
            renderFailedJson(ReturnCode.WRONG_INPUT);
        }
    }

    /**
     * 处理参数错误，跳转400
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月23日 下午12:06:20
     */
    protected static void handleWrongInput() {
        if (!request.isAjax()) {
            renderTemplate("errors/400.html");
        }
        renderFailedJson(ReturnCode.WRONG_INPUT);
    }

    /**
     * 获取当前会话中的用户
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午5:17:56
     */
    public static User getCurrentUser() {
        // Secure校验通过后，已设置用户数据
        return renderArgs.get(Secure.FIELD_USER, User.class);
    }

    /**
     * 获取当前附带参数的URL
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月22日 下午4:44:06
     */
    protected static String getCurrentUrlWithParam() {
        StringBuilder builder = new StringBuilder();
        if (request.secure) {
            builder.append("https://");
        } else {
            builder.append("http://");
        }
        builder.append(request.host);
        builder.append(request.url);
        return builder.toString();
    }
}
