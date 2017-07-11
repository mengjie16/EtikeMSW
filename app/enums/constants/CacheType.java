package enums.constants;

import com.aton.util.StringUtils;

/**
 * 
 * 缓存数据类型：汇总所有缓存对象的Key及过期时间.<br>
 * 【注意】：所有加入缓存的对象都要实现Serializable接口
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2015年5月2日 上午7:40:07
 * @formatter:off
 */
public enum CacheType {
    // ===========================用户相关========================================
    /** Restful授权码缓存 */
    AUTH_CODE("AUTH_CODE_", "30d"),

    /** 用户状态:USER_{userId} */
    USER_INFO("USER_", "3d"), 
    
    /** 用户注册短信验证码有效为15分钟 **/
    SMS_CODE("SMS_CODE_", "15min"),
    /** 用这个来禁止用户一分钟内只能发一条 **/
    SMS_SENT_GATE("SMS_SENT_GATE_", "1min"),

    /** 前端用的信息掩码 */
    SECURE_INFO_MASK("SECURE_INFO_MASK_", "1h"),
    
    // ===========================用户相关========================================
    RETAILER_CART_INFO("RETAILER_CART_INFO_", "3d"),
    /**
     * 七牛文件上传凭证:FILE_UPTOKEN,
     * 失效之后需要重新获取
     */
    FILE_UPTOKEN("FILE_UPTOKEN_", "3h"),
    /** 供应商运费模版缓存 */
    SUPPLIER_FREIGHT_TEMP("SUPPLIER_FREIGHT_TEMP_","3d"),
    /** 所有供应商运费模版缓存 */
    SUPPLIER_FREIGHT_TEMPS("SUPPLIER_FREIGHT_TEMPS_","3d"),

    // ===========================微信相关========================================
    /**
     * 微信扫码Ticket缓存，无需去DB取
     */
    WX_TICKET("WX_TICKET_", "5d"),
    
    WX_ACCESSTOKEN("WX_ACCESSTOKEN_", "110min"),
    
    WX_JS_API_TICKET("WX_JS_API_TICKET_", "110min"),

    /** 临时二维码参数缓存 */
    WX_TEMP_QRCODE("WX_TEMP_QRCODE_", "1d"),

    /** 微信支付凭证内容 */
    FILE_CONSTANTS("FILE_CONSTANTS", "3d"),

    /** 微信临时二维码参数添加 */
    QR_LOGIN("QR_LOGIN_", "30s"),

    /** 管理员信息 */
    MANAGER_INFO("MANAGER_", "1d"),

    /** 淘宝店铺类目 */
    TB_SHOP_CATES("TB_SHOP_CATE", "30d"),

    /** 淘宝宝贝类目 */
    ITEM_CATES("ITEM_CATES", "30d"),

    // ==========================文件内容==============================================

    /** 静态文件读入，避免重复IO */
    STATIC_FILE("STATIC_FILE", "10d"),

    /** 系统配置 */
    SYS_CONFIG("SYS_CONFIG_", "1d"),

    // ==========================区域数据==============================================
    /**
     * 行政区域数据(AREA_{ParentId})
     */
    AREA_DATA("AREA_", "30d"),
    AREA_COUNTRY_DATA("AREA_COUNTRY_DATA_", "30d"),
    AREA_SHORT("AREA_SHORT_", "30d"),
    // ===========================类目相关========================================

    /** 顶级宝贝类目 */
    ITEM_CATE_ROOT("ITEM_CATE_ROOT_", "30d"),

    /**
     * 顶级类目下的1~3级子类目:
     */
    ITEM_CATE_LEVEL_LT3("ITEM_CATE_LEVEL_LT3_", "30d"),

    /** 某个类目下的子类目 */
    ITEM_CATE_SUB("ITEM_CATE_SUB_", "30d"),
    /** 系统商品类目缓存 */
    ITEM_CATE("ITEM_CATE_", "30d"),
    ITEM_CATE_MAP("ITEM_CATE_MAP_", "30d"),

    /** 保存系统中的商品详情页 */
    ITEM_DETAIL("ITEM_DETAIL_", "1d"),

    // ===========================帮助文章相关 ========================================
    ARTICLE_DATA("ARTICLE_DATA_", "1d"),

    // =========================== http 访问安全相关 =================================
    
    REQUEST_USER_COOKIE_STATE("REQUEST_USER_COOKIE_STATE", "2h"),
    CLIENT_POST_REQUEST_TIME("CLIENT_POST_REQUEST_TIME_", "2s"), 
    CLIENT_GET_REQUEST_TIME("CLIENT_GET_REQUEST_TIME_", "1s"),

    // =========================== 主页设置相关 =================================
    
    HOME_PAGE_SETTING_LIST("HOME_PAGE_SETTING_LIST_", "30d"), 
    HOME_PAGE_SETTING("HOME_PAGE_SETTING_", "30d"),

    // =========================== 数据相关缓存 =================================
    // 品牌数据缓存
    BRAND_LIST("BRAND_LIST", "30d"),
    BRAND_MAP_DATA("BRAND_MAP_DATA", "30d"),
    // 宝贝类目缓存
    ITEM_CATE_DATA("ITEM_CATE_DATA_", "30d"),
    // 宝贝详情缓存
    ITEM_DETAIL_DATA("ITEM_DETAIL_DATA_", "30d"),

    ITEM_PV_DATA("ITEM_PV_DATA_", "10min"),
    ITEM_STAR_DATA("ITEM_STAR_DATA_", "3d"),
    ITEM_SEARCH_LIST_DATA("ITEM_SEARCH_LIST_DATA_", "15min"),
    ITEM_ALL_DATA("ITEM_ALL_DATA_", "15d"),
    
    // =========================== 支付相关 =====================================
    PAY_REDIRECT_URL("PAY_REDIRECT_URL_", "15d"),
    
    // =========================== 零售商相关 =====================================
    // excel订单解析后table数据
    RETAILER_ORDER_TABLE_DATA("RETAILER_ORDER_TABLE_DATA_", "2h"),
    // 未解析商品信息的订单视图数据
    RETAILER_ORDER_VO_DATA("RETAILER_ORDER_VO_DATA_", "2h"),
    // 订单商品解析数据
    RETAILER_ORDER_PRODUCT_DATA("RETAILER_ORDER_PRODUCT_DATA_", "2h"),
    // 订单视图完整信息缓存
    RETAILER_ORDER_VO_ALL("RETAILER_ORDER_VO_ALL_", "2h");
    // @formatter:on

    private String prefix;
    public String expiredTime;

    CacheType(String prefix, String expiredTime) {
        this.prefix = prefix;
        this.expiredTime = expiredTime;
    }

    /**
     * 
     * 获取CacheType对应的key.
     * 【注意】若key有自定义的后缀部分，使用{@link #getKey(Object)}
     * 
     * @return
     * @since v0.1
     */
    public String getKey() {
        return this.prefix;
    }

    /**
     * 
     * 获取CacheType对应的key.
     * 
     * @param keySuffixs 多个附加对象
     * @return
     * @since v0.1
     */
    public String getKey(Object... keySuffixs) {
        return this.prefix + StringUtils.join(keySuffixs, StringUtils.UNDERLINE);
    }
}
