package controllers;

import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aton.config.ReturnCode;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

import controllers.base.BaseController;
import controllers.base.secure.Secure;
import enums.constants.BizConstants;
import enums.constants.CacheType;
import enums.constants.ErrorCode;
import enums.constants.RegexConstants;
import models.Item;
import models.Retailer;
import models.RetailerChannel;
import models.User;
import models.WechatUser;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.data.validation.Validation;
import play.libs.Images;
import play.mvc.Util;
import play.mvc.With;
import utils.SmsUtil;
import utils.WechatHelper;
import vos.ItemSearchResult;

/**
 * 平台控制器
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月15日 上午1:28:17
 */
@With(Secure.class)
public class Application extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * 微信授权路由
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午3:46:27
     */
    public static void WxAuth() {
        // 参数获取
        // 微信code
        String code = params.get("code");
        String rUrl = params.get("rUrl");
        // 跳转微信授权
        String wxUrl = WechatHelper.getAuthToTargetUrl(rUrl, null, "snsapi_userinfo");
        // 获取用户基本信息
        if (Strings.isNullOrEmpty(code)) { // 没有code，返回错误
            redirect(wxUrl);
        }
        // 获取用户信息
        JSONObject data = WechatHelper.getWechatUserInfoByCode(code);
        if (data == null) {
            redirect(wxUrl);
        }

        // 用户信息保存
        WechatUser wechatUser = WechatUser.saveFromJsonObject(data);
        if (wechatUser == null) {
            redirect(wxUrl);
            log.error("Wechat user save FAILED");
        } else {
            log.info("Wechat user save successed");
        }

        // -----------------获取再次redirect url-----------------
        if (!Strings.isNullOrEmpty(rUrl)) { // 不空，逻辑处理
            log.info("redirectUrl:{}", rUrl);
            session.put(BizConstants.USER_AUTH_OPENID, wechatUser.openId);
            redirect(rUrl);
        }
        redirect(wxUrl);
    }

    /**
     * 检查微信用户是否有授权权限
     *
     * @param moudle
     * @param moudleId
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 上午11:37:08
     */
    public static void hasAuthRole(@Required @MinSize(1) String moudle, @Required String moudleId) {
        handleWrongInput(false);
        String openId = session.get(BizConstants.USER_AUTH_OPENID);
        // 跳转微信授权
        if (Strings.isNullOrEmpty(openId)) {
            // session错误 561
            renderFailedJson(ReturnCode.INVALID_SESSION);
        }
        if (WechatUser.checkRoleInMoudle(moudle, moudleId, openId)) {
            // 校验成功 200
            renderSuccessJson();
        }
        // 555
        renderFailedJson(ReturnCode.FAIL);
    }

    public static void indexList1() {
        render();
    }

    /**
     * 默认主页
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 上午1:28:07
     */
    public static void indexList2() {    	
    	ItemSearchResult search_result = Item.selectListAllByCreateTime();
        renderArgs.put("search_result", search_result);
    	
        render();
    }

    /**
     * 
     * 注册页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月24日 下午4:12:31
     */
    public static void regist() {
        // 加载零售商渠道列表
        List<RetailerChannel> rcs = RetailerChannel.getChannelList();
        renderArgs.put("rcs", rcs);
        render();
    }

    /**
     * 
     * 零售商注册数据提交
     *
     * @param retailer
     * @param user
     * @param captcha
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午6:33:42
     */
    public static void doRegist(@Required @Valid Retailer retailer, @Required User user, @Required String captcha) {
        // 输入验证
        if (validation.hasErrors()) {
            flash.error("注册失败,参数填写错误");
            redirect("/regist");
        }
        // 再次校验手机号是否重复
        User retailerUser = User.findByPhone(user.phone);
        if (retailerUser != null) {
            flash.error("注册失败,该手机号已注册");
            redirect("/regist");
        }
        // 验证码验证
        boolean smsCodematched = SmsUtil.checkSmsCode(user.phone, captcha);

        if (!smsCodematched) {
            flash.error("验证码不正确");
            redirect("/regist");
        }
        boolean ret = retailer.addWithUser(user);
        if (ret) {
            redirect("/regist/success");
        }
        flash.error("注册失败");
        redirect("/regist");
    }

    /**
     * 注册成功跳转页面
     *
     * @since v1.0
     * @author Calm
     * @created 2016年9月8日 下午3:28:07
     */
    public static void registSuccess() {
        render();
    }

    /**
     * 
     * 找回密码
     *
     * @param step
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月16日 上午12:00:21
     */
    public static void retrievePass(String step) {
        // 用户请求页面
        renderArgs.put("step", step);
        render();
    }

    /**
     * 
     * 找回密码表单提交
     *
     * @param utm
     * @param smdCode
     * @param password
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月16日 下午2:21:20
     */
    public static void doRetrievePass(@Required @MinSize(64) String utm,
        @Required @Match(RegexConstants.DIGITS6) String smdCode,
        @Required @Match(RegexConstants.PASSWORD) String password) {

        handleWrongInput(true);
        String phone = deMask(utm);
        if (Strings.isNullOrEmpty(phone)) {
            renderJson(ReturnCode.INVALID_PRIVILEGE, "iv参数错误");
        }

        if (!SmsUtil.checkSmsCode(phone, smdCode)) {
            renderJson(ReturnCode.API_CALL_LIMIT, "验证码不正确");
        }

        User user = User.findByPhone(phone);
        user.password = password;
        if (user.savePassword()) {
            SmsUtil.sendUserModifyPassNotice(user.phone, password, user.name);
            renderSuccessJson();
        }
        renderFailedJson(ReturnCode.FAIL, "密码找回失败");
    }

    /**
     * 用户身份验证码短信发送
     *
     * @param phone
     * @param imgCaptcha
     * @since v1.0
     * @author Calm
     * @created 2016年5月27日 下午4:38:12
     */
    public static void sendUserValidSms(@Required @Match(RegexConstants.PHONE) String phone,
        @Required @MaxSize(4) String imgCaptcha) {
        handleWrongInput(true);

        String sysCaptchaCode = session.get("captcha");
        if (!Objects.equal(imgCaptcha, sysCaptchaCode)) {
            log.error("Captcha Image code mismatched,{} inputed, but {} excepted", imgCaptcha, sysCaptchaCode);
            renderFailedJson(ReturnCode.FAIL, "图形校验码错误");
        }

        // 验证手机，必须要存在才行
        if (User.findByPhone(phone) == null) {
            renderFailedJson(ErrorCode.USER_PHONE_NOT_FOUND.code, ErrorCode.USER_PHONE_NOT_FOUND.description);
        }
        // 验证码生成
        String smsCode = RandomUtils.nextInt(100000, 1000000) + "";
        boolean result = SmsUtil.sendUserValidCode(phone, smsCode);
        if (!result) {
            renderFailedJson(ReturnCode.FAIL, "发送失败");
        }
        String infoMask = RandomStringUtils.randomAlphanumeric(64);
        String key = CacheType.SECURE_INFO_MASK.getKey(infoMask);
        CacheUtils.set(key, phone, CacheType.SECURE_INFO_MASK.expiredTime);
        renderJson(ImmutableMap.of("uim", infoMask));
    }

    @Util
    public static String deMask(String infoMask) {
        String key = CacheType.SECURE_INFO_MASK.getKey(infoMask);
        return CacheUtils.get(key);
    }

    /**
     * 验证用户手机号是否存在
     *
     * @param phone
     * @since v1.0
     * @author Calm
     * @created 2016年9月23日 下午1:02:55
     */
    public static void checkPhone(@Required @Match(RegexConstants.PHONE) String phone) {
        handleWrongInput(true);
        // 验证手机
        if (User.findByPhone(phone) != null) {
            renderFailedJson(ErrorCode.USER_PHONE_DUPLICATE.code, ErrorCode.USER_PHONE_DUPLICATE.description);
        }
        renderSuccessJson();
    }

    /**
     * 
     * 发送注册验证码短信
     *
     * @param phone
     * @param imgCaptcha
     * @param garen 管理员才会发这个参数
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月29日 上午12:15:27
     */
    public static void sendRegistSms(@Required @Match(RegexConstants.PHONE) String phone,
        @Required @MaxSize(4) String imgCaptcha, String garen) {
        handleWrongInput(true);

        String sysCaptchaCode = session.get("captcha");
        if (!Objects.equal(imgCaptcha, sysCaptchaCode) && Strings.isNullOrEmpty(garen)) {
            log.error("Captcha Image code mismatched,{} inputed, but {} excepted", imgCaptcha, sysCaptchaCode);
            renderFailedJson(ReturnCode.FAIL, "图形校验码错误");
        }

        // 验证手机
        if (User.findByPhone(phone) != null) {
            renderFailedJson(ErrorCode.USER_PHONE_DUPLICATE.code, ErrorCode.USER_PHONE_DUPLICATE.description);
        }
        // 验证码生成
        String smsCode = RandomUtils.nextInt(100000, 1000000) + "";
        boolean result = SmsUtil.sendRegisterCode(phone, smsCode);
        if (!result) {
            renderFailedJson(ReturnCode.FAIL, "发送失败");
        }
        renderJson(smsCode);
    }

    /**
     * 短信验证码验证
     *
     * @param captcha
     * @param phone
     * @since v1.0
     * @author Calm
     * @created 2016年5月27日 下午2:21:30
     */
    public static void checkSms(@Required @Match(RegexConstants.DIGITS6) String smsCode,
        @Required @Match(RegexConstants.PHONE) String phone) {
        handleWrongInput(true);
        
        boolean matched = SmsUtil.checkSmsCode(phone, smsCode);
        // 验证码是否匹配
        if (!matched) {
            renderFailedJson(ErrorCode.SMS_CODE_ERROR.code, ErrorCode.SMS_CODE_ERROR.description);
        }
        renderSuccessJson();
    }

    /**
     * 
     * 生成图形验证码
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月29日 上午12:00:50
     */
    public static void captcha() {
        Images.Captcha captcha = Images.captcha(110, 40);
        String code = captcha.getText("#E7C831", 4, "123456789abcdefghijkmnpqrstuvwxyz");
        session.put("captcha", code);
        renderBinary(captcha);
    }

    /**
     * 
     * 错误页面
     *
     * @since v1.0
     * @author tr0j4n
     * @created 2016年4月24日 下午4:12:31
     */
    public static void error2() {
        render();
    }
}