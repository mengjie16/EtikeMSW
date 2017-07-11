package enums;

/**
 * 支付宝交易状态
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月25日 下午4:00:21
 */
public enum AliPayTradeStatus {
    // 交易创建，等待买家付款
    WAIT_BUYER_PAY("WAIT_BUYER_PAY","交易创建，等待买家付款"),
    // 在指定时间段内未支付时关闭的交易；在交易完成全额退款成功时关闭的交易
    TRADE_CLOSED("TRADE_CLOSED","交易关闭"),
    // 交易成功，且可对该交易做操作，如：多级分润、退款等
    TRADE_SUCCESS("TRADE_SUCCESS","交易成功"),
    // 等待卖家收款（买家付款后，如果卖家账号被冻结）
    TRADE_PENDING("TRADE_PENDING","等待卖家收款"), 
    // 交易成功且结束，即不可再做任何操作
    TRADE_FINISHED("TRADE_FINISHED","交易结束");
    
    private AliPayTradeStatus(String _code, String _text) {
        this.code = _code;
        this.text = _text;
    }

    public String code;
    public String text;

    @Override
    public String toString() {
        return code.toUpperCase();
    }
}
