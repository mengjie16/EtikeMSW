package enums.constants;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 
 * 业务常量类.
 * 
 * @author youblade
 * @since v0.3.3
 * @created 2013-10-31 下午5:04:10
 */
public class BizConstants {

    /** -------------------- 任务相关---------------------------------- **/
    /**
     * 每单任务服务费用起步价格：6元
     */
    public static final int TASK_STARTING_INGOT = 6;

    
    /** --------------------任务经验值------------------ **/
    public static final int TASK_EXPERIENCE_COMMON = 1;

    
    /** --------------------会员费用设置---------------------------------- **/
    /**
     * 【商家】会员费用
     */
    public static final Map<Integer,Long> SELLER_MEMBER_FEE = Maps.newHashMapWithExpectedSize(4);
    /**
     * 【买手】会员费用
     */
    public static final Map<Integer,Long> BUYER_MEMBER_FEE = Maps.newHashMapWithExpectedSize(5);
    static{
        /** 3个月 180元 */
        SELLER_MEMBER_FEE.put(3, 180*100L);
        /** 6个月 300元 */
        SELLER_MEMBER_FEE.put(6, 300*100L);
        
        /** 3个月 50元 */
        BUYER_MEMBER_FEE.put(3, 50*100L);
        /** 6个月 100元 */
    }
    
    
    /** --------------------View相关：页面渲染、前后端交互---------------------------------- **/
    public static final String MSG = "msg";
    /** 提示消息时的跳转连接 */
    public static final String URL = "url";

    public static final String PLATFORMS = "platforms";
    
    public static final String PAY_PLATFORMS = "payPlatforms";
    
    public static final String USER_PHONE = "user_phone";
    
    public static final String USER_AUTH_COOKIE = "user_auth_cookie";
    
    public static final String USER_AUTH_SESSION = "user_auth_session";
    
    public static final String USER_AUTH_OPENID = "user_auth_wechat_openid";
    
    public static final String CLIENT_REQUEST_TIME = "client_request_time";
    
    
}
