package controllers;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.config.Config;
import com.aton.config.ReturnCode;
import com.aton.util.CacheUtils;
import com.aton.util.MapUtils;
import com.aton.util.ReflectionUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Doubles;

import controllers.base.BaseController;
import enums.AliPayTradeStatus;
import enums.constants.CacheType;
import models.AliPayTrade;
import play.mvc.Util;
import utils.ValidateSignUtil;

/**
 * 各种支付处理
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月25日 上午10:59:37
 */
public class PayCenter extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(PayCenter.class);

    /**
     * 
     * 支付宝预支付
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2016年3月31日 下午6:08:21
     */
    public static void alipay(double payFee) {
        // 取两位小数
        String totalFeeStr = new java.text.DecimalFormat("0.00").format(payFee);
        int totalFee = (int) (Doubles.tryParse(totalFeeStr) * 100);
        // 创建交易
        AliPayTrade trade = new AliPayTrade();
        trade.subject = "支付：" + totalFeeStr + "元";
        trade.tradeStatus = AliPayTradeStatus.WAIT_BUYER_PAY;
        trade.totalFee = totalFee;
        if (trade.create() == null) {
            renderFailedJson(ReturnCode.SYSTEM_BUSY, "交易创建失败");
        }
        log.info("create trade id={}", trade.id);
        Map payParams = Maps.newHashMap();
        // ---- 必要参数
        payParams.put("service", "create_direct_pay_by_user");
        payParams.put("partner", Config.alipayPid);
        payParams.put("_input_charset", "utf-8");
        payParams.put("notify_url", Config.APP_URL + "/alipay/notify");
        payParams.put("return_url", Config.APP_URL + "/alipay/return");
        payParams.put("error_notify_url", Config.APP_URL + "/alipay/notify/error");
        payParams.put("out_trade_no", trade.id);
        payParams.put("subject", trade.subject);
        payParams.put("payment_type", "1");
        payParams.put("total_fee", totalFeeStr);
        payParams.put("seller_id", Config.alipayPid);
        payParams.put("seller_email", Config.alipayMchEmail);
        payParams.put("body", "支付金额");
        payParams.put("it_b_pay", "2h");

        // 生成签名
        String sign = ValidateSignUtil.signFromMapWithkey(payParams, Config.alipayKey);
        renderArgs.put("payParams", payParams);
        renderArgs.put("sign", sign);
        renderTemplate("PayCenter/alipayForm.html");
    }

    /**
     * 
     * 支付宝预支付
     * 
     * @since v1.0
     * @author tr0j4n
     * @created 2016年3月31日 下午6:08:21
     */
    @Util
    public static void alipay_self(AliPayTrade trade) {

        if (trade == null || trade.tradeStatus != AliPayTradeStatus.WAIT_BUYER_PAY) {
            renderFailedJson(ReturnCode.SYSTEM_BUSY, "交易创建失败");
        }
        log.info("Create trade , id={}", trade.id);

        Map payParams = Maps.newHashMap();
        // ---- 必要参数
        payParams.put("service", "create_direct_pay_by_user");
        payParams.put("partner", Config.alipayPid);
        payParams.put("_input_charset", "utf-8");
        payParams.put("notify_url", Config.APP_URL + "/alipay/notify");
        payParams.put("return_url", Config.APP_URL + "/alipay/return");
        payParams.put("error_notify_url", Config.APP_URL + "/alipay/notify/error");
        payParams.put("out_trade_no", trade.id);
        payParams.put("subject", trade.subject);
        payParams.put("payment_type", "1");
        payParams.put("total_fee", trade.totalFee / 100 + "");
        payParams.put("seller_id", Config.alipayPid);
        payParams.put("seller_email", Config.alipayMchEmail);
        payParams.put("body", "支付金额");
        payParams.put("it_b_pay", "2h");

        // 生成签名
        String sign = ValidateSignUtil.signFromMapWithkey(payParams, Config.alipayKey);
        log.info("alipay sign ={}", sign);
        renderArgs.put("payParams", payParams);
        renderArgs.put("sign", sign);
        renderTemplate("PayCenter/alipayForm.html");
    }

    /**
     * 支付宝的同步回调
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:29:02
     */
    public static void payReturn() {
        log.info("Alipay return url:{}", request.url);
        Map<String, String> paramMap = params.allSimple();
        boolean isSuccess = MapUtils.getBoolean(paramMap, "is_success", false);
        if (!isSuccess) {
            redirect(Config.APP_URL);
        }
        long tradeNo = tradeNotify(paramMap);
        if (tradeNo == 0) {
            redirect(Config.APP_URL);
        }
        // 从缓存中获取支付宝通知URL
        String key = CacheType.PAY_REDIRECT_URL.getKey(tradeNo);
        String redirectUrl = CacheUtils.get(key);
        if (!Strings.isNullOrEmpty(redirectUrl)) {
            CacheUtils.remove(key);
            redirect(Config.APP_URL + redirectUrl);
        }
    }

    /**
     * 支付宝的异步回调
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:28:50
     */
    public static void payNotify() {
        Map<String, String> paramMap = params.allSimple();
        long tradeNo = tradeNotify(paramMap);
        if (tradeNo > 0) {
            renderText("success");
        }
        renderText("fail");
    }

    /**
     * 异步，同步供用交易通知
     *
     * @param paramMap
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:40:31
     */
    @Util
    private static long tradeNotify(Map paramMap) {
        log.info(MapUtils.printVerbose("payNotify", paramMap));

        long outTradeNo = MapUtils.getLong(paramMap, "out_trade_no", 0L);
        if (outTradeNo == 0) {
            return 0;
        }
        // 特殊字段处理
        String trade_status = MapUtils.getString(paramMap, "trade_status");
        paramMap.put("trade_status", AliPayTradeStatus.valueOf(trade_status));
        // 检查系统中是否存在交易
        AliPayTrade trade = AliPayTrade.findById(outTradeNo);
        if (trade == null) {
            log.info("Trade no={} not found", outTradeNo);
            return 0;
        }
        if (trade.tradeStatus != AliPayTradeStatus.WAIT_BUYER_PAY) { // 不是等待支付，说明已被处理
            log.info("Trade status updated to {}", trade.tradeStatus);
            return outTradeNo;
        }
        try {
            ReflectionUtils.injectBean(trade, paramMap, ReflectionUtils.CAMEL_STYLE);
        } catch (Exception ex) {
            log.error("Trade object fields injected failed", ex);
        }
        if (trade.update()) {
            log.info("Trade status updated to {}", trade.tradeStatus);
            return outTradeNo;
        }
        return 0;
    }

    /**
     * 支付宝通知错误
     * 
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:28:17
     */
    public static void errorNotify() {
        Map<String, String> paramMap = params.allSimple();
        log.info(MapUtils.printVerbose("errorNotify", paramMap));
    }
}
