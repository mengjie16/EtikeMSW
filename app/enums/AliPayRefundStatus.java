package enums;

/**
 * 支付宝退款状态
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月25日 下午4:00:21
 */
public enum AliPayRefundStatus {
    // 退款成功：
        //全额退款情况：trade_status= TRADE_CLOSED，而refund_status=REFUND_SUCCESS
        //非全额退款情况：trade_status= TRADE_SUCCESS，而refund_status=REFUND_SUCCESS
    REFUND_SUCCESS("REFUND_SUCCESS","交易创建，等待买家付款"),
    // 退款关闭
    REFUND_CLOSED("REFUND_CLOSED","交易关闭");

    private AliPayRefundStatus(String _code, String _text) {
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
