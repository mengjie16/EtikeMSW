package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.vo.Page;
import com.google.common.base.Strings;

import enums.constants.CacheType;
import enums.constants.RegexConstants;
import models.mappers.UserMapper;
import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.Required;
import play.libs.Codec;
import vos.UserSearchVo;

/**
 * 
 * 登录系统的用户，可能是供应商的，也可能是零售商
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月20日 下午7:56:12
 */
public class User implements java.io.Serializable {

    private static final long serialVersionUID = 20150118192946L;
    public static final String TABLE_NAME = "user";
    private static final Logger log = LoggerFactory.getLogger(User.class);
    /** 系统分配10000开始 */
    public long id;
    @Email
    public String email;
    /** 头像地址 */
    public String avatar;
    /** 电话，也是登录名 */
    @Match(RegexConstants.PHONE)
    public String phone;
    /** 存储MD5(origin password+salt) */
    public String password;
    /** 用户自己登记的姓名，登录后在用户后台显示 */
    public String name;
    /** 6位随机字符 */
    public String salt;
    /** 零售商就是RETAILER，供应商就是SUPPLIER，这里不用枚举 */
    @Required
    @Match("RETAILER|SUPPLIER")
    public String role;
    /** 零售商角色这里就是零售商的id，供应商角色这里就是供应商的id */
    @Min(1)
    public int userId;
    /** 是否是主账号 */
    public boolean isMainAccount;
    /** QQ */
    public String qq;
    /** 微信号 */
    public String weixin;
    /** 是否删除(只做标记) */
    public boolean isDelete;
    /** 是否已认证 */
    public boolean isAuth;
    /** 注册激活时间 */
    public Date createTime;
    /** 最后登录时间 */
    public Date lastLoginTime;

    /**
     * 用户附加公司信息（供应商才有）
     */
    @Transient
    public String company;

    /**
     * 根据ID查询用户信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 下午5:21:45
     */
    public static User findById(long id) {
        User user = findByField("id", id);
        return user;
    }

    /**
     * 
     * 根据id获取用户对象
     * 
     * @param id
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-9 下午3:35:08
     */
    public static User findByIdWichCache(long id) {
        String key = CacheType.USER_INFO.getKey(id);
        User user = CacheUtils.get(key);
        if (user != null) {
            return user;
        }

        // Fetch from DB if null
        user = findById(id);
        if (user != null) {
            CacheUtils.set(key, user, CacheType.USER_INFO.expiredTime);
        }
        return user;
    }

    /**
     * 移除用户信息缓存
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年6月21日 下午4:17:30
     */
    public static void removeCache(long id) {
        String key = CacheType.USER_INFO.getKey(id);
        CacheUtils.remove(key);
    }

    /**
     * 
     * 按照某个字段（属性）获取用户
     * 
     * @param field
     * @param value
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-1 下午4:10:16
     */
    public static User findByField(String field, Object value) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.selectByField(field, value);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据手机查找用户
     *
     * @param phone
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月28日 上午2:22:31
     */
    public static User findByPhone(String phone) {
        return findByField("phone", phone);
    }

    /**
     * 检查邮箱是否重复
     *
     * @param email
     * @param userId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月23日 下午3:42:45
     */
    public static boolean checkEmailUsedByOthers(String email, long userId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.countFieldUsedByOthers("email", email, userId) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 检查用户名是否重复
     *
     * @param name
     * @param userId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月23日 下午3:48:58
     */
    public static boolean checkNameUsedByOthers(String name, long userId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.countFieldUsedByOthers("name", name, userId) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 验证用户密码
     * 
     * @param inputPass
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2015-4-7 上午11:50:21
     */
    public boolean validate(String inputPass) {
        if (Strings.isNullOrEmpty(inputPass)) {
            return false;
        }
        return this.password.equals(Codec.hexMD5(inputPass + this.salt));
    }

    /**
     * 保存用户信息，调用此方法前，要注意password一定是明文的！本函数内会进行加密
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月11日 上午2:15:20
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            if (id > 0) {
                mapper.updateById(this);
                return true;
            }
            // 新增用户
            // 再次检查注册用户数据是否重复
            User userInDb = mapper.checkUserExsit(phone, id);
            if (userInDb != null) {
                log.warn("Checked duplicated regist user={},phone={}", name, phone);
                return false;
            }
            salt = RandomStringUtils.randomNumeric(6);
            password = Codec.hexMD5(password + salt);
            createTime = DateTime.now().toDate();
            mapper.insert(this);
            return true;
        } catch (Exception ex) {
            log.error("User save failed", ex);
            return false;
        } finally {
            ss.close();
        }
    }

    /**
     * 更新用户密码(用户密码及salt一起更新)
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月27日 下午6:55:31
     */
    public boolean savePassword() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            salt = RandomUtils.nextInt(100000, 1000000) + "";
            password = Codec.hexMD5(password + salt);
            mapper.updatePassword(id, password, salt);
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 
     * 删除用户
     *
     * @param id
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月9日 下午10:30:39
     */
    public static boolean deleteById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            mapper.delete(id);
        } catch (Exception ex) {
            return false;
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 认证用户
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月12日 上午11:13:00
     */
    public boolean auth() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            return mapper.authUser(this.id, !this.isAuth) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 检索用户列表
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月23日 下午3:09:50
     */
    public static Page<User> findPageByVo(UserSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);

            int totalCount = mapper.countVo(vo);
            Page<User> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectListByVo(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

    /**
     * 清除excel订单相关数据缓存
     *
     * @since v1.0
     * @author Calm
     * @created 2016年9月21日 下午5:34:51
     */
    public void removeOrderAboutData() {
        // -------------- 清除excel订单相关数据缓存
        // 未解析商品信息的订单视图数据
        CacheUtils.remove(CacheType.RETAILER_ORDER_VO_DATA.getKey(this.userId));
        // excel订单解析后table数据
        CacheUtils.remove(CacheType.RETAILER_ORDER_TABLE_DATA.getKey(this.userId));
        // 订单商品解析数据
        CacheUtils.remove(CacheType.RETAILER_ORDER_PRODUCT_DATA.getKey(this.userId));
        // 订单视图完整信息缓存
        CacheUtils.remove(CacheType.RETAILER_ORDER_VO_ALL.getKey(this.userId));
    }

    /**
     * @param name2
     * @return
     */
    public static List<User> findUserByNameFuzzy(String name) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            List<User> users = mapper.selectListByNameFuzzy(name);
            return users;
        } finally {
            ss.close();
        }
    }

    public static void updateLastLoginTime(User user) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            UserMapper mapper = ss.getMapper(UserMapper.class);
            DateTime dtNow = DateTime.now();
            user.lastLoginTime = dtNow.toDate();
            mapper.updateLastLoginTimeById(user.lastLoginTime, user.id);
        } finally {
            ss.close();
        }
    }
}
