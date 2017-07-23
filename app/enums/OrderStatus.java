package enums;

/**
 * 
 * 订单状态
 * 
 * @author tr0j4n
 * @since  v1.0
 * @created 2016年5月27日 下午6:50:17
 * @formatter:off
 */
public enum OrderStatus {
    
    ORDER_CONSIGN_WAIT("ORDER_CONSIGN_WAIT", "待发货"), 
    ORDER_CONSIGN_BEEN("ORDER_CONSIGN_BEEN", "已发货"),
    ORDER_SUCCESS("ORDER_SUCCESS","订单成功") ,
    ORDER_REFUNDING("ORDER_REFUNDING","退货退款") ,
    ORDER_REFUNDING_FINISH("ORDER_REFUNDING_FINISH","退货完成"),
    TRADE_UNPAIED("TRADE_UNPAIED","待付款");
    
    public String code;
    public String text;

    OrderStatus(String _code, String _text) {
        this.code = _code;
        this.text = _text;
    }
}
