package enums.constants;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * Controller返回的各个错误代码
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2015-4-30 下午4:37:44
 */
public enum ErrorCode {

    //@formatter:off
    // ====== 用户相关
    USER_PHONE_DUPLICATE(20001, "User.phone.duplicate", "该手机号已被使用"),
    USER_PHONE_BUSINESS_LIMIT(20002, "User.phone.business.limit", "1分钟内已发送验证码，请查看您的短信"),
    USER_PHONE_NOT_FOUND(20002, "User.phone.notFound", "用户手机号不存在"),
    USER_EMAIL_DUPLICATE(20003, "User.email.duplicate", "用户邮箱已被使用"),
    USER_NAME_DUPLICATE(20004, "User.name.duplicate", "用户名称已被使用"),
    USER_NOT_FOUND(20005, "User.notFound", "用户不存在"),
    USER_LOCATION_UNFULL(20006, "User.locationUnfull", "地址信息不完整"),
    
    // ===== 短信相关
    
    SMS_CODE_ERROR(20007, "Sms.codeError", "验证码不正确或已失效"),
    
    // ===== 订单相关
    ORDER_ITEM_COUNT_ERROR(50001, "order.itemCountError", "商品数量必须大于0"),
    ORDER_BUYER_NAME_EMPTY(50002, "order.buyerNameEmpty", "客户姓名不能为空"),
    ORDER_BUYER_CONTACT_EMPTY(50003, "order.buyerContactEmpty", "客户联系方式不能为空"),
    ORDER_BUYER_CONTACT_INVALID(50004, "order.buyerContactInvalid", "客户联系方式不合法"),
    ORDER_BUYER_PROVINCE_EMPTY(50005, "order.buyerProvinceEmpty", "客户地址(省)不能为空"),
    ORDER_BUYER_CITY_EMPTY(50006, "order.buyerCityEmpty", "客户地址(省)不能为空"),
    ORDER_BUYER_ADDRESS_EMPTY(50007, "order.buyerAddressEmpty", "客户地址(详细地址)不能为空"),
    ORDER_BUYER_PROVINCE_INVALID(50007, "order.buyerProvinceInvalid", "客户地址(省)无法解析到平台"),
    ORDER_ITEM_UNEXSIT_ERROR(50008, "order.itemUnExsitError", "订单商品不存在或已下架"),
    ORDER_SHIPPINGFEE_ERROR(50009, "order.shippingFeeError", "订单商品未配置运费模版(或省份解析出错)"),
    ORDER_STATUS_CANNOT_CHANGE(50010, "order.statusCannotChange", "非等待发货状态订单,不可修改"),
    ORDER_NOT_FOUND(50011, "order.notFound", "订单ID:%s,不存在"),
    
    ORDER_BUYER_IDCARD_EMPTY(50015, "order.buyerIdcardEmpty", "客户身份证不能为空"),
    ORDER_BUYER_GENDER_EMPTY(50015, "order.buyerGenderEmpty", "客户性别不能为空"),
	
	//=====地址
	 RETAILERADDRESS_NAME_EMPTY(50012, "order.buyerNameEmpty", "客户姓名不能为空"),
	 RETAILERADDRESS_PHONE_EMPTY(50013, "order.buyerContactEmpty", "客户联系方式不能为空"),
	 RETAILERADDRESS_PHONE_INVALID(50014, "order.buyerContactInvalid", "客户联系方式不合法");
	 
   
    //@formatter:on
    /** 数值 */
    public int code;
    /** 英文的错误解释 */
    public String text;
    /** 中文注释 */
    @JSONField(serialize = false)
    public String description;

    private ErrorCode(int code, String text, String description) {
        this.code = code;
        this.text = text;
        this.description = description;
    }
}