
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.*;

import com.alibaba.fastjson.JSONArray;
import com.aton.config.Config;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.util.JsonUtil;
import com.aton.util.MailUtils;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.util.RegexUtils;
import com.aton.util.StringUtils;
import com.aton.util.reflect.FieldProduceRuler;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.util.JSON;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.internal.toplink.embedded.websocket.util.StringUtil;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import enums.AliPayTradeStatus;
import enums.constants.CacheType;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

import play.libs.Codec;
import play.test.*;
import utils.SmsUtil;
import utils.ValidateSignUtil;
import vos.PriceRangeVo;
import models.*;

public class BasicTest extends UnitTest {

    @Test
    public void aVeryImportantThingToTest() {
       // String str = String.valueOf(null);
        AliPayTrade trade = AliPayTrade.findById(1608268118784000L);
        System.out.println(JsonUtil.toJson(trade));
       // FieldProduceRuler ruler = new FieldProduceRuler();
        
    }

    @Test
    public void sendSmsByAliDayu() throws ApiException {
        boolean result = SmsUtil.sendUserModifyPassNotice("18657157520","123456",  "小小的世界");// ("1",
        if (result) {
            System.out.println("短信发送成功!");
        } else {
            System.out.println("短信发送失败!");
        }
    }

    @Test
    public void test_PriceRange() {
        List<PriceRangeVo> priceRanges = Lists.newArrayList();
        PriceRangeVo p1 = new PriceRangeVo();
        p1.price = 200;
        p1.range = 20;
        PriceRangeVo p2 = new PriceRangeVo();
        p2.price = 300;
        p2.range = 30;

        PriceRangeVo p4 = new PriceRangeVo();
        p4.price = 300;
        p4.range = 7000;

        PriceRangeVo p3 = new PriceRangeVo();
        p3.price = 300;
        p3.range = 5000;

        priceRanges.add(p3);
        priceRanges.add(p1);
        priceRanges.add(p2);
        priceRanges.add(p4);
    }

    @Test
    public void test_JSONob() throws JSONException {
        List<PriceRangeVo> priceRanges = Lists.newArrayList();
        PriceRangeVo pr = new PriceRangeVo();
        pr.range = 1;
        pr.price = 200;
        PriceRangeVo pr1 = new PriceRangeVo();
        pr1.range = 2;
        pr1.price = 390;
        priceRanges.add(pr);
        priceRanges.add(pr1);
        org.json.JSONArray jsonarr = new org.json.JSONArray(JsonUtil.toJson(priceRanges));
        List<PriceRangeVo> queryList = Lists.newArrayList();
        for (int i = 0; i < jsonarr.length(); i++) {
            JSONObject obj = jsonarr.getJSONObject(i);
            PriceRangeVo prv = JsonUtil.toBean(obj.toString(), PriceRangeVo.class);
            queryList.add(prv);
        }
        MixHelper.print(queryList);
        JSONObject js = new JSONObject(JsonUtil.toJson(ImmutableMap.of("value", priceRanges)));

        // JsonUtil.toBean(obj.t, clazz)
        // TODO Auto-generated method stub
        // System.out.println(js.toString());

    }

    @Test
    public void test_sendMail() {
        BigDecimal   b   =   new   BigDecimal(4.3055);  
        double   f1   =   b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue(); 
        
        System.out.println(0/1);
    }

    @Test
    public void test_groupWord() {
        String keyWord = "1推车";
        keyWord = StringUtils.upperCase(keyWord);
        String[] strs = new String[] {};
        char[] chars = keyWord.toCharArray();
        // 关键字超出搜索范围
        if (chars.length > 30) {
            return;
        }
        // 过滤字符
        String chineseSymbols = "。？！，、；：“”（）【】—…–．";
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            boolean result = CharUtils.isAsciiPrintable(c) || c >= 0x4e00 && c <= 0x9fbb || chineseSymbols.indexOf(c) != -1;
            if (result) {
                sb.append(c);
            }
        }
        // 最终字符串
        String resStr = sb.toString();
        if (Strings.isNullOrEmpty(resStr)) {
            return;
        }
        // 空格分组
        String[] spaceStr = resStr.split(" ");
        // 分词组合
        for (int i = 0; i < spaceStr.length; i++) {
            String gpStr = spaceStr[i];
            // 字符长度小于2滤过, 单词纯字母或数字滤过
            if (gpStr.length() <= 2 || StringUtils.isAsciiPrintable(gpStr)) {
                strs = ArrayUtils.add(strs, gpStr);
                continue;
            }
            char[] gpChars = gpStr.toCharArray();
            // 分词开始起始位置
            int startIndex = 0;
            // 字符标识（0数字，1字母，2其他字符）
            int ascii = 0;
            for (int j = 0; j < gpChars.length; j++) {
                int nAscii = 2;
                // 数字，英文归组滤过
                if (CharUtils.isAsciiNumeric(gpChars[j])) {
                    nAscii = 0;
                } else if (CharUtils.isAsciiAlpha(gpChars[j])) {
                    nAscii = 1;
                } else {
                    nAscii = 2;
                }
                // 符合条件进行截取
                if (j != 0 && ascii != nAscii || j == gpChars.length - 1) {
                    int end = j == gpChars.length - 1 ? gpChars.length : j;
                    String nStr = gpStr.substring(startIndex, end);
                    // 字符串过长分解，以后可以做语义解析
                    int yl = (int) Math.floor(nStr.length() / 4);
                    boolean adb = false;
                    for (int y = 0; y < yl; y++) {
                        String lStr = nStr.substring(y * 4, (y + 1) == yl ? nStr.length() : (y + 1) * 4);
                        strs = ArrayUtils.add(strs, lStr);
                        adb = true;
                    }
                    if (!adb) {
                        strs = ArrayUtils.add(strs, nStr);
                    }
                    startIndex = j;
                }
                ascii = nAscii;
            }
        }
        System.out.println(Arrays.asList(strs));
        
    }

    @Test
    public void test_isAscii() {
        Map<String,String> paramsn = Maps.newConcurrentMap();
        paramsn.put("1", "update");
        paramsn.put("2", "set");
        System.out.println(paramsn.values());
        boolean match = paramsn.values().stream().filter(s->StringUtils.isNotEmpty(s)).anyMatch(s->RegexUtils.isMatch(s.toLowerCase(), "^where|create|insert|alert|drop|select|update|set|delete$"));
        System.out.println( match);
    }
    
    @Test
    public void test_config(){
        Map payParams = Maps.newHashMap();
        payParams.put("id", "1608262629757952");
        payParams.put("buyer_email", "hubinyuan@hotmail.com");
        payParams.put("seller_email", "biweilun@hotmail.com");
        payParams.put("trade_no", "xxxx-xxxx-xxxx-xxxx");
        payParams.put("trade_status","TRADE_FINISHED");
        payParams.put("return_url", Config.APP_URL + "pay/return");
        payParams.put("gmt_create", "2016-8-26 10:22:00");
        payParams.put("trade_status", AliPayTradeStatus.valueOf(payParams.get("trade_status").toString()));
        // 生成签名
        String sign = ValidateSignUtil.signFromMapWithkey(payParams, Config.alipayKey);
        System.out.println(sign);
        AliPayTrade trade = new AliPayTrade();
        FieldProduceRuler ruler = new FieldProduceRuler();
        ReflectionUtils.injectBean(trade, payParams, ReflectionUtils.CAMEL_STYLE);
        trade.update();
        System.out.println(JsonUtil.toJson(trade));
    }

}
