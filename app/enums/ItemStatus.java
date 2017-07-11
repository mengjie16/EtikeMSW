package enums;

/**
 * 宝贝状态
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年5月30日 下午12:43:29
 */
public enum ItemStatus {
    //@formatter:off
	AUDITING("AUDITING","审核中"),
	FAIL_AUDITING("FAIL_AUDITING","审核失败"),
	ONLINE("ONLINE","在架"),
	HIDE("HIDE","被隐藏"), 
	DELETED("DELETED","被删除");
	//@formatter:on

    private ItemStatus(String _code, String _text) {
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
