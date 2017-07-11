package com.aton.config;

import java.io.File;
import java.util.List;

import play.Play;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

/**
 * 
 * 项目的配置获取类
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2015-6-19 上午10:24:40
 */
public class Config extends ConfigBase {

    /**
     * Excel模板文件路径
     */
    public static File excelTempFileFolder = new File(confDir, "excel");

    /**
     * 出问题时，发送给开发人员
     */
    public static List<String> developerMails = Lists.newArrayList(getProperty("developer.mails").split(","));

    /**
     * 信鸽推送AccessId
     * 
     * @see http://xg.qq.com/xg/apps/ctr_app/get_app_info?app_id=2100137554
     */
   // public static long xgPushAccessId = Longs.tryParse(getProperty("xinge.accessId"));
    /**
     * 信鸽推送SecretKey
     */
   // public static String xgPushSecretKey = getProperty("xinge.secretKey");

    /** static-items目录 */
    public static File staticItmes = new File(confDir, "static-items");

    /** 模板目录 */
  //  public static File templateDir = new File(confDir, "templates");

    /** 模板目录 */
   // public static File wx_payDir = new File(confDir, "wx-pay");

    /**
     * 平台二级菜单配置
     */
   // public static File subMenuKey = new File(staticItmes, "sub-menu.json");

    /** public目录 */
    public static File publicDir = new File(Play.applicationPath, "public");

    /**
     * SOA服务器端口
     */
    //public static int soaPort = Integer.parseInt(getProperty("soa.port"));
    /**
     * 激活密码/解锁密码 请求路径
     */
    public static String launcherUrl = getProperty("launcher.url");

    /**
     * 微信证书文件
     */
    //public static File wx_pay_certfile = new File(wx_payDir, "wx_apiclient_cert.p12");

    /**
     * 支付宝商户号
     */
    public final static String alipayPid = getProperty("alipay.pid");
    /**
     * 支付宝商户秘钥
     */
    public final static String alipayKey = getProperty("alipay.key");
    /**
     * 支付宝商户email
     */
    public final static String alipayMchEmail = getProperty("alipay.mch.email");
}
