package utils;

import org.apache.commons.lang3.Validate;
import org.apache.thrift.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aton.config.AppMode;
import com.aton.config.Config;
import com.aton.util.CacheUtils;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.taobao.api.ApiException;
import com.taobao.api.BaseTaobaoRequest;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.TaobaoResponse;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;

import enums.constants.CacheType;

/**
 * 发短信的帮助类
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年5月26日 上午11:52:03
 */
public class SmsUtil {

    public static final Logger log = LoggerFactory.getLogger(SmsUtil.class);

    /**
     * top分配的key
     */
    private static String APP_KEY;
    /**
     * top分配的密钥
     */
    private static String APP_SECRET;
    /**
     * api url(视开发环境模式)
     */
    private static String API_URL;

    /**
     * 一定是正式api
     */
    private static String API_URL_FINAL;

    /**
     * 初始化
     */
    static {
        APP_KEY = Config.getProperty("top.appkey");
        APP_SECRET = Config.getProperty("top.secret");
        API_URL_FINAL = Config.getProperty("top.http.api.url");
        if (AppMode.get().mode == AppMode.Mode.ONLINE) {
            API_URL = Config.getProperty("top.http.api.url");
        } else {
            API_URL = Config.getProperty("top.http.api.sandbox.url");
        }
    }

    /**
     * 错误处理
     *
     * @param json
     * @since v1.0
     * @author Calm
     * @created 2016年5月26日 下午1:14:08
     */
    private static void handleError(String json) {
        if (!json.contains("error_response")) {
            return;
        }
        log.error(json);
    }

    /**
     * 淘宝api default execute
     *
     * @param req
     * @param apiUrl
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月26日 下午2:54:51
     */
    private static JSONObject tbDefaultExecute(BaseTaobaoRequest req, String apiUrl) {
        TaobaoClient client = new DefaultTaobaoClient(Option.fromNullable(apiUrl).or(API_URL), APP_KEY, APP_SECRET,
            "json");
        try {
            TaobaoResponse rsp = client.execute(req);
            String result = rsp.getBody();
            log.info("response result:{}", result);
            if (Strings.isNullOrEmpty(result)) {
                return null;
            }
            handleError(result);
            return JSONObject.parseObject(result);
        } catch (ApiException e) {
            log.error("api exception: {}", e);
            return null;
        }
    }

    /**
     * 调用阿里大鱼API发送短信
     *
     * @param extend 可选
     * @param smsFreeSignName 必填
     * @param smsTemplateCode 必填
     * @param smsParamJson 必填
     * @param phone 必填
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月26日 下午1:23:16
     */
    private static boolean invokeSendSmsApi(String extend, String smsFreeSignName, String smsTemplateCode,
        String smsParamJson, String phone) {
        AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
        req.setSmsType("normal");
        if (!Strings.isNullOrEmpty(extend)) { // 可选
            req.setExtend(extend);
        }
        req.setSmsFreeSignName(smsFreeSignName);
        req.setSmsParamString(smsParamJson);
        req.setRecNum(phone);
        req.setSmsTemplateCode(smsTemplateCode);
        JSONObject jsonObj = tbDefaultExecute(req, API_URL_FINAL);
        if (jsonObj == null || jsonObj.getJSONObject("error_response") != null) {
            // 邮件通知
            Alerter.informDevelopers("Send sms failed ", jsonObj != null ? jsonObj.toJSONString() : "无相关描述");
            return false;
        }
        try {
            String pKey = req.getApiMethodName().replace(".", "_").concat("_response");
            JSONObject resultObj = jsonObj.getJSONObject(pKey).getJSONObject("result");
            boolean result = resultObj.getIntValue("err_code") == 0;
            log.info("Sms message sent. Phone={}, Json={}", phone, smsParamJson);
            return result;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 发送注册验证码
     *
     * @param phone
     * @param code
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月26日 下午5:36:16
     */
    public static boolean sendRegisterCode(String phone, String code) {
        Validate.notEmpty(code);
        Validate.notEmpty(phone);

        if (!allowedSend(phone)) {
            return false;
        }

        String codeStr = "{\"code\":\"" + code + "\",\"product\":\"吐司宝贝\"}";
        boolean result = invokeSendSmsApi(null, "吐司宝贝", "SMS_9661787", codeStr, phone);

        sendFinished(phone, code);
        return result;
    }

    /**
     * 发送用户身份验证验证码
     *
     * @param phone
     * @param code
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 上午12:44:33
     */
    public static boolean sendUserValidCode(String phone, String code) {
        Validate.notEmpty(code);
        Validate.notEmpty(phone);

        if (!allowedSend(phone)) {
            return false;
        }

        String codeStr = "{\"code\":\"" + code + "\",\"product\":\"吐司宝贝\"}";
        boolean result = invokeSendSmsApi(null, "吐司宝贝", "SMS_10210515", codeStr, phone);

        sendFinished(phone, code);
        return result;
    }

    /**
     * 发送用户密码修改通知短信
     *
     * @param phone 手机
     * @param pass 密码
     * @param userName 用户名
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月7日 下午12:35:09
     */
    public static boolean sendUserModifyPassNotice(String phone, String pass, String userName) {
        Validate.notEmpty(pass);
        Validate.notEmpty(phone);
        Validate.notEmpty(userName);

        String infoContent = "{\"name\":\"" + userName + "\",\"password\":\"" + pass + "\"}";
        boolean result = invokeSendSmsApi(null, "吐司宝贝", "SMS_10642328", infoContent, phone);

        return result;
    }

    /**
     * 
     * 检查输入的验证码是否与系统中的一致
     *
     * @param phone 手机号
     * @param smsCode 用户输入的6位数字验证码
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午1:20:30
     */
    public static boolean checkSmsCode(String phone, String smsCode) {
        Validate.notEmpty(phone);
        Validate.notEmpty(smsCode);

        String key = CacheType.SMS_CODE.getKey(phone);
        String code = CacheUtils.get(key);

        if (!Objects.equal(smsCode, code)) {
            log.error("Captcha mismatched. Phone {} entered {}, but {} expected", phone, smsCode, code);
            return false;
        }

        return true;
    }

    /**
     * 
     * 判断一个手机号是否可以发送验证码
     *
     * @param phone
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午1:36:04
     */
    public static boolean allowedSend(String phone) {
        Validate.notEmpty(phone);

        String key = CacheType.SMS_SENT_GATE.getKey(phone);
        String sent = CacheUtils.get(key);

        if (Strings.isNullOrEmpty(sent)) {
            return true;
        }
        log.error("Phone={} try to send sms earlier. Blocked", phone);
        return false;
    }

    /**
     * 
     * 发送完成，设置2个缓存
     *
     * @param phone
     * @param smsCode
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午3:16:40
     */
    public static void sendFinished(String phone, String smsCode) {
        String key = CacheType.SMS_SENT_GATE.getKey(phone);
        CacheUtils.set(key, "SENT", CacheType.SMS_SENT_GATE.expiredTime);

        key = CacheType.SMS_CODE.getKey(phone);
        CacheUtils.set(key, smsCode, CacheType.SMS_CODE.expiredTime);
    }

    /**
     * 
     * 获得某个手机号的验证码
     *
     * @param phone
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月8日 下午3:19:08
     */
    public static String querySmsCode(String phone) {
        String key = CacheType.SMS_CODE.getKey(phone);
        return CacheUtils.get(key);
    }
}
