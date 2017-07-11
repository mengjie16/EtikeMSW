package enums.constants;

/**
 * 常用正则
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年9月26日 下午1:40:15
 */
public class RegexConstants {

    // 手机号
    public final static String PHONE = "^0{0,1}(13[0-9]|14[5-7]|15[0-3]|15[5-9]|18[0-9]|17[0-9])[0-9]{8}$";
    // 电话
    public final static String TELL = "^\\d{3}-\\d{8}|\\d{4}-\\d{7,8}$";
    // 中文
    public final static String CHINESE = "^[\u4e00-\u9fa5]+$";
    // 中英文+数字
    public final static String ABC_NUM_CN = "^[a-zA-Z0-9\u4e00-\u9fa5]+$";
    // 密码
    public final static String PASSWORD = "^\\w{6,20}$";
    // 身份证
    public final static String IDCARD = "^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$";
    // 银行卡
    public final static String BANK = "^\\d{16,19}$";
    // QQ号码
    public final static String QQ = "^[1-9][0-9]{4,}$";
    // 常规ascii字符，双字节
    public final static String ASCII = "^[\\x00-\\xFF]+$";
    // 正整数
    public final static String POS_DIGITS = "^[1-9]\\d*$";
    // 整数
    public final static String DIGITS = "^-?[1-9]\\d*$";
    
    public final static String DIGITS6 = "^[0-9]{6}$";

    public enum Regex {

        PHONE("^1[0-9]{10}$", "手机格式不正确");

        /** 正则 */
        public String pattern;
        public String error;

        private Regex(String _pattern, String _error) {
            this.pattern = _pattern;
            this.error = _error;
        }
    }
}