package utils;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aton.config.Config;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.util.WebUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import enums.constants.CacheType;
import play.libs.IO;
import play.libs.WS;

/**
 * 微信公众号相关功能
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月5日 下午2:00:59
 */
public class WechatHelper {

    private static final Logger log = LoggerFactory.getLogger(WechatHelper.class);

    /**
     * 微信公共号的开发ID
     */
    public final static String APP_ID;
    /**
     * 微信公共号的Secret
     */
    public final static String APP_SECRET;
    /**
     * 微信支付的Key
     */
    public final static String APP_PAY_KEY;
    /**
     * 微信支付商户号
     */
    public final static String APP_PAY_MCHID;

    static {
        APP_ID = Config.getProperty("wechat.appid");
        APP_SECRET = Config.getProperty("wechat.secret");
        APP_PAY_KEY = Config.getProperty("wechat.pay.key");
        APP_PAY_MCHID = Config.getProperty("wechat.pay.mchid");
    }

    /**
     * 
     * 检查微信GET过来信息是否真实
     * 
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-16 下午4:22:00
     */
    public static boolean checkSignature(String signature, String timestamp, String nonce) {

        return true;
    }

    /**
     * 
     * 向微信服务器post数据
     * 
     * @param url
     * @param postData
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-15 下午2:27:58
     */
    public static String postWechat(String url, String postData) {
        String result = "";

        try {
            result = WS.url(url).body(postData).post().getString("utf-8");
        } catch (Exception e) {
            log.error("Error occurred when post data to wechat  :\n {}", e.getMessage());
        }
        JSONObject jsonObj = JSONObject.parseObject(result);
        // 没有错误直接返回JSON
        if (!jsonObj.containsKey("errcode") || jsonObj.getInteger("errcode") == 0) {
            return result;
        }
        // 有错误代码存在，需要处理
        int errcode = jsonObj.getIntValue("errcode");
        // 需要重新刷新token的代码
        int[] tokenErrorCodes = { 40014, 42001 };
        if (Arrays.binarySearch(tokenErrorCodes, errcode) != -1) {
            getAccessToken(true);
        }
        return null;
    }

    /**
     * 向微信服务器获取access_token
     *
     * @param forceRefresh
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年10月8日 上午11:05:35
     */
    public static String getAccessToken(boolean forceRefresh) {
        String key = CacheType.WX_ACCESSTOKEN.getKey();
        String accessToken = CacheUtils.get(key);
        if (Strings.isNullOrEmpty(accessToken) || forceRefresh) {
            String tokenUrl = MessageFormat.format(Config.getProperty("wechat.access.token.url"), APP_ID, APP_SECRET);
            accessToken = get(tokenUrl);
            if (!Strings.isNullOrEmpty(accessToken)) {
                // 从JSON中解析出token
                JSONObject jObj = JSON.parseObject(accessToken);
                accessToken = jObj.getString("access_token");
                if (!Strings.isNullOrEmpty(accessToken)) {
                    CacheUtils.set(key, accessToken, CacheType.WX_ACCESSTOKEN.expiredTime);
                    log.info("accessToken reset success");
                }
            }
        }
        return accessToken;
    }

    /**
     * 
     * 网页授权获取用户基本信息
     * http://mp.weixin.qq.com/wiki/17/c0f37d5704f0b64713d5d2c37b468d75.html
     *
     * @param code
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-10-20 上午10:41:55
     */
    public static String getOpenIdByOathCode(String code) {
        return null;
    }

    /**
     * 
     * GET方式向微信服务器请求数据
     *
     * @param url
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015年11月19日 下午8:52:19
     */
    public static String get(String url) {
        String result = "";
        try {
            result = WS.url(url).get().getString("utf-8");
        } catch (Exception e) {
            log.error("Error occurred when post data to wechat", e);
        }
        handleError(result);
        return result;
    }

    /**
     * 
     * 生成二维码的Ticket
     * http://mp.weixin.qq.com/wiki/18/28fc21e7ed87bec960651f0ce873ef8a.html
     * 
     * @param locationStr deskid=桌子id&posid=位置id
     * @return 二维码图片的ticket
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-15 下午1:47:54
     */
    public static String generateQrcodeTicket(String locationStr) {
        Validate.isTrue(locationStr.length() < 65 && locationStr.length() > 0, "Scene_str length invalid");

        // 产生取二维码所要post的JSON数据
        String applyJsonTmp = "'{'\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": '{'\"scene\": '{'\"scene_str\": \"{0}\"'}'}'}'";
        String postData = MessageFormat.format(applyJsonTmp, locationStr);
        // 生成取二维码ticket的URL
        String accessToken = getAccessToken(false);
        String getTicketUrl = MessageFormat.format(Config.getProperty("wechat.qrcode.create.url"), accessToken);
        // 发送数据
        String outJson = postWechat(getTicketUrl, postData);
        // 从JSON中解析出token
        JSONObject jObj = JSON.parseObject(outJson);
        String ticket = jObj.getString("ticket");
        log.info("Ticket={}", ticket);

        return ticket;
    }

    /**
     * 
     * 返回图片素材内容
     *
     * @param mediaId
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-8-31 下午5:12:47
     */
    public static byte[] getPictureMaterial(String mediaId) {
        String accessToken = getAccessToken(false);
        String getMaterialUrl = MessageFormat.format(Config.getProperty("wechat.material.get.url"), accessToken);
        String postData = "{\"media_id\":\"" + mediaId + "\"}";
        InputStream result = null;

        try {
            result = WS.url(getMaterialUrl).body(postData).post().getStream();
        } catch (Exception e) {
            log.error("Error occurred when post data to wechat  :\n {}", e.getMessage());
        }
        byte[] bs = IO.readContent(result);
        return bs;
    }

    /**
     * 
     * 客服接口-发纯文本消息<br/>
     * 
     * @see {@link http://mp.weixin.qq.com/wiki/1/70a29afed17f56d537c833f89be979c9.html} <br/>
     *      在48小时内不限制发送次数
     * 
     * @param openId
     * @param text
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-15 下午4:40:21
     */
    public static void sendTextMessage(String openId, String text) {
        Validate.notEmpty(openId, "Parameter [openId] empty");
        Validate.notEmpty(text, "Parameter [text] empty");
        // 产生发消息所要post的JSON数据
        String jsonTmp = "{\"touser\":\"%s\",\"msgtype\":\"text\",\"text\":{\"content\":\"%s\"}}";
        String postData = String.format(jsonTmp, openId, text);
        // 生成发消息的URL
        String accessToken = getAccessToken(false);
        String sendMsgUrl = MessageFormat.format(Config.getProperty("wechat.message.send.url"), accessToken);
        // 发送数据
        postWechat(sendMsgUrl, postData);

    }

    /**
     * 
     * 生成微信支付URI
     * 
     * @param productId
     * @return
     * @since v1.0
     * @author Lawrence
     * @created 2015年4月30日 下午6:22:22
     */
    public static String payUri(String productId) {
        Map map = new HashMap<String, String>();
        map.put("appid", APP_ID);
        map.put("mch_id", APP_PAY_MCHID);
        map.put("product_id", productId);
        String timeStamp = Long.toString(new DateTime().plusHours(8).getMillis() / 1000);
        map.put("time_stamp", timeStamp);
        map.put("nonce_str", (Math.random() + "").replaceAll("\\.", ""));
        String qrcodeUrl = MessageFormat.format("weixin://wxpay/bizpayurl?{0}&sign={1}", concatQueryStringFromMap(map),
            sign(map));
        return qrcodeUrl;
    }

    private static String concatQueryStringFromMap(Map map) {
        Validate.isTrue(!MixHelper.isEmpty(map), "Parameter [Map] is empty");
        // key排序
        Map sortMap = new TreeMap<String, String>(map);
        sortMap = Maps.filterValues(sortMap, new Predicate<String>() {

            public boolean apply(String input) {
                // 过滤Map中value值为空的元素
                return !Strings.isNullOrEmpty(input);
            }
        });
        String keyValPairs = Joiner.on("&").withKeyValueSeparator("=").join(sortMap);
        return keyValPairs;
    }

    /**
     * 
     * 微信支付签名
     * 
     * @param map
     * @return
     * @since v1.0
     * @author Lawrence
     * @modify tr0j4n
     * @created 2015年4月30日 下午6:22:57
     */
    public static String sign(Map map) {
        String keyValPairs = concatQueryStringFromMap(map);
        keyValPairs = keyValPairs + "&key=" + APP_PAY_KEY;
        String signStr = DigestUtils.md5Hex(keyValPairs).toUpperCase();
        return signStr;
    }

    /**
     * 
     * 判断签名是否正确
     * 
     * @param map
     * @param key
     * @return
     * @since v1.0
     * @author Lawrence
     * @created 2015-5-6 上午10:24:55
     */
    public static boolean isSignValid(Map map) {
        Validate.isTrue(!MixHelper.isEmpty(map), "Parameter [Map] is empty");

        String sign = map.get("sign").toString();
        if (StringUtils.isBlank(sign)) {
            return false;
        }
        map.remove("sign");
        return sign(map).equals(sign);
    }

    /**
     * 
     * 将Map转为Xml，1层而已
     * 
     * @param map
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-5-5 下午4:47:21
     */
    public static String mapToXml(Map<String, String> map) {
        Validate.isTrue(!MixHelper.isEmpty(map), "Parameter map couldn't be blank");
        StringBuilder sb = new StringBuilder("<xml>");
        for (Map.Entry entry : map.entrySet()) {
            Joiner.on("").appendTo(sb, "<", entry.getKey(), "><![CDATA[", entry.getValue(), "]]></", entry.getKey(),
                ">");
        }
        return sb.toString() + "</xml>";
    }

    /**
     * 
     * 长链接转短链接
     * 
     * @param longUrl
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-5-9 下午3:18:27
     */
    public static String shortifyUrl(String longUrl) {
        // 生成取二维码ticket的URL
        String accessToken = getAccessToken(false);
        String getUrl = MessageFormat.format(Config.getProperty("wechat.util.long2short.url"), accessToken);
        String postData = MessageFormat.format("'{'\"action\":\"long2short\",\"long_url\":\"{0}\"'}'", longUrl);
        String outJson = postWechat(getUrl, postData);
        // 从JSON中解析出token
        JSONObject jObj = JSON.parseObject(outJson);
        String shortUrl = jObj.getString("short_url");
        return shortUrl;
    }

    /**
     * 
     * 过滤微信昵称(除了中文，大小写英文，数字之外的char，都过滤掉)
     * 
     * @param str
     * @return
     * @since v1.0
     * @author crazyNew
     * @created 2015年8月13日 下午3:59:04
     */
    public static String filterNickName(String str) {
        char[] chars = str.toCharArray();
        String chineseSymbols = "。？！，、；：“”（）【】—…–．";
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            boolean result = CharUtils.isAsciiPrintable(c) || c >= 0x4e00 && c <= 0x9fbb
                || chineseSymbols.indexOf(c) != -1;
            if (result) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取自定义静默授权跳转链接跳转目标链接
     * @formatter:off
     * https://open.weixin.qq.com/connect/oauth2/authorize?appid={0}&redirect_uri={1}&response_type=code&scope={2}&state={3}#wechat_redirect
     * @formatter:on
     * 
     * @param rUrl
     * @param scope (snsapi_userinfo,snsapi_base)
     * @return
     * @since v1.0
     * @author CalmLive
     * @created 2016年1月25日 下午7:56:13
     */
    public static String getAuthToTargetUrl(String targetUrl, String state, String scope) {
        // 微信授权Url
        String wxAuthUrl = Config.getProperty("wechat.connect.url");
        if (Strings.isNullOrEmpty(wxAuthUrl)) {
            return null;
        }
        return MessageFormat.format(wxAuthUrl, APP_ID,
            WebUtils.encode(Config.getProperty("wechat.oauth.domain") + "?rUrl=" + WebUtils.encode(targetUrl)), scope,
            state);
    }

    /**
     * 根据code获取用户授权用的相关信息
     * @formatter:off
     * https://api.weixin.qq.com/sns/oauth2/access_token?appid={0}&secret={1}&code={2}&grant_type=authorization_code
     * 
     * @param code
     * @return
     *         {
     *         "access_token":"ACCESS_TOKEN",
     *         "expires_in":7200,
     *         "refresh_token":"REFRESH_TOKEN",
     *         "openid":"OPENID",
     *         "scope":"SCOPE"
     *         }
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午2:43:20
     * @formatter:on
     */
    public static JSONObject getAuthDataInfoByCode(String code) {
        // Url
        String url = Config.getProperty("wechat.sns.code");
        if (Strings.isNullOrEmpty(url)) {
            return null;
        }
        url = MessageFormat.format(url, APP_ID, APP_SECRET, code);
        String results = get(url, null);
        if (!Strings.isNullOrEmpty(results) && !results.contains("errcode")) {
            JSONObject json = JSONObject.parseObject(results);
            return json;
        }
        return null;
    }

    /**
     * 拉取scope 为 snsapi_userinfo 的用户信息
     *
     * @param code
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午3:10:26
     */
    public static JSONObject getWechatUserInfoByCode(String code) {
        JSONObject authData = getAuthDataInfoByCode(code);
        log.info("base authData {}", authData);
        if (authData == null) {
            return null;
        }

        String openId = authData.getString("openid");
        String accessToken = authData.getString("access_token");
        String url = Config.getProperty("wechat.snsapi.userinfo");
        if (!Strings.isNullOrEmpty(url)) {
            url = MessageFormat.format(url, accessToken, openId);
            String results = get(url, null);
            log.info("userinfo \n{}", results);
            if (!Strings.isNullOrEmpty(results) && !results.contains("errcode")) {
                JSONObject json = JSONObject.parseObject(results);
                return json;
            }
        }
        // 如果没有得到详细信息返回基本信息（openId）
        return authData;
    }

    /**
     * 为外部添加授权功能
     *
     * @param targetUrl
     * @param state
     * @return
     * @since v1.0.7
     * @author CalmLive
     * @created 2016年2月25日 下午4:52:29
     */
    public static String getAuthToTargetUrlOut(String targetUrl, String state) {
        // 微信授权Url
        String wxAuthUrl = Config.getProperty("wechant.connect.url");
        if (Strings.isNullOrEmpty(wxAuthUrl)) {
            return null;
        }
        String domainRUrl = WebUtils
            .encode(Config.getProperty("wechat.oauth.domain") + "/Out/OAuth?rUrl=" + WebUtils.encode(targetUrl));
        return MessageFormat.format(wxAuthUrl, APP_ID, domainRUrl, state);
    }

    /**
     * 获取jspApi_ticket
     *
     * @param forceRefresh 是否强制刷新
     *
     * @return
     * @since v1.0.7
     * @author CalmLive
     * @created 2016年2月26日 上午9:43:12
     */
    public static String getJsapiTicket(boolean forceRefresh) {
        String key = CacheType.WX_JS_API_TICKET.getKey();
        String jsTicket = CacheUtils.get(key);
        if (Strings.isNullOrEmpty(jsTicket) || forceRefresh) {
            String tokenUrl = MessageFormat.format(Config.getProperty("wechat.jsapi.ticket.url"),
                getAccessToken(false));
            jsTicket = get(tokenUrl);
            if (!Strings.isNullOrEmpty(jsTicket)) {
                // 从JSON中解析出token
                JSONObject jObj = JSON.parseObject(jsTicket);
                jsTicket = jObj.getString("ticket");
                CacheUtils.set(key, jsTicket, CacheType.WX_JS_API_TICKET.expiredTime);
                log.info("js_api_ticket reset success");
            }
        }
        return jsTicket;
    }

    /**
     * 
     * POST方式向微信服务器请求数据
     * 
     * @param url
     * @param postData
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-15 下午2:27:58
     */
    public static String post(String url, String postData) {
        String result = "";
        try {
            result = WS.url(url).body(postData).post().getString("utf-8");
        } catch (Exception e) {
            log.error("Error occurred when post data to wechat  :\n {}", e.getMessage());
        }
        handleError(result);
        return result;
    }

    /**
     * 向微信服务器发送get 请求
     *
     * @param url
     * @param param
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午2:59:24
     */
    public static String get(String url, Map param) {
        String result = "";
        try {
            result = WS.url(url).get().getString("utf-8");
        } catch (Exception e) {
            log.error("Error occurred when get data to wechat  :\n {}", e.getMessage());
        }
        handleError(result);
        return result;
    }

    /**
     * 
     * 处理微信请求回来的错误码
     *
     * @param json
     * @since v1.0
     * @author tr0j4n
     * @created 2015年11月19日 下午9:00:27
     */
    private static void handleError(String json) {
        if (!json.contains("errcode")) {
            return;
        }
        JSONObject jsonObj = JSONObject.parseObject(json);
        // 没有错误直接返回JSON
        if (!jsonObj.containsKey("errcode") || jsonObj.getInteger("errcode") == 0) {
            return;
        }
        log.error(jsonObj.toString());
    }
}
