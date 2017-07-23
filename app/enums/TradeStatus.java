package enums;

/**
 * 交易状态
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月29日 下午10:01:29
 */
public enum TradeStatus {

    TRADE_AUDITING("TRADE_AUDITING", "审核中"), 
    TRADE_FAIL_AUDITING("TRADE_FAIL_AUDITING", "审核失败"), 
    //TRADE_CONSIGN_WAIT("TRADE_CONSIGN_WAIT", "待发货"), 
    //TRADE_CONSIGN_BEEN("TRADE_CONSIGN_BEEN", "已发货"), //
    TRADE_SETTLEMENT_BEEN("TRADE_SETTLEMENT_BEEN", "货款已结算"), 
    TRADE_CLOSE("TRADE_CLOSE","交易关闭"), 
    TRADE_USER_CANCELLED("TRADE_USER_CANCELLED", "交易已取消"),
    //TRADE_SYSTEM_CANCELLED("TRADE_SYSTEM_CANCELLED", "系统已取消");
    
    TRADE_UNPAIED("TRADE_UNPAIED","待付款");
    

    private TradeStatus(String _code, String _text) {
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
