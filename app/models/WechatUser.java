package models;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Min;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.MailUtils;
import com.aton.util.MixHelper;
import com.aton.util.StringUtils;
import com.aton.vo.Page;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.mchange.lang.CharUtils;

import enums.constants.CacheType;
import models.mappers.ArticleMapper;
import models.mappers.UserMapper;
import models.mappers.WechatUserMapper;
import play.data.validation.Email;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.libs.Codec;
import vos.UserSearchVo;
import vos.WechatUserVo;

/**
 * 微信用户实体
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月4日 下午12:05:46
 */
public class WechatUser implements java.io.Serializable {

    private static final long serialVersionUID = 20150118192946L;
    public static final String TABLE_NAME = "wechat_user";

    private static final Logger log = LoggerFactory.getLogger(WechatUser.class);

    /** 系统分配80000开始 */
    public long id;
    /** 用户头像 */
    public String avatar;
    /** 昵称 */
    public String nick;
    /** openId */
    public String openId;
    /** 公众平台唯一id */
    public String unionid;
    /** 首次授权时间 */
    public Date authTime;
    /** 最后一次授权时间 */
    public Date lastAuthTime;

    /**
     * 保存
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午12:04:16
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            mapper.insert(this);
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 删除微信用户
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午5:55:31
     */
    public static boolean delete(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            return mapper.delete(id) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据微信用户信息json 保存微信用户信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午3:40:39
     */
    public static WechatUser saveFromJsonObject(JSONObject obj) {
        Validate.notNull(obj);
        WechatUser wechatUser = new WechatUser();
        wechatUser.openId = obj.getString("openid");
        wechatUser.nick = Optional.fromNullable(obj.getString("nickname")).or("未知");
        wechatUser.avatar = Optional.fromNullable(obj.getString("headimgurl")).or("未知");
        wechatUser.unionid = Optional.fromNullable(obj.getString("unionid")).or("unionid");
        
        if(Strings.isNullOrEmpty(wechatUser.openId)){
            return null;
        }
        // 已经存在的
        if (WechatUser.findByOpenId(wechatUser.openId) != null) {
            return wechatUser;
        }
        // TODO 没有对旧用户的信息进行更新
        wechatUser.save();
        return wechatUser;
    }

    /**
     * 更新最后一次授权时间
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午12:05:42
     */
    public void updateAuthTimeAndLast() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            if (this.authTime == null) {
                this.authTime = DateTime.now().toDate();
            }
            if (this.lastAuthTime == null) {
                this.lastAuthTime = DateTime.now().toDate();
            }
            mapper.updateById(this);
        } catch (Exception ex) {} finally {
            ss.close();
        }
    }

    /**
     * 批量更新授权用户信息
     *
     * @param ids
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午6:32:13
     */
    public static void updateLastAuthTimeBatch(List<Long> ids) {
        if (MixHelper.isEmpty(ids)) {
            return;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            mapper.updateLastAuthTimeBatch(ids);
        } finally {
            ss.close();
        }
    }

    /**
     * 批量保存初次授权时间
     *
     * @param ids
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 上午11:15:02
     */
    public static void updateAuthTimeBatch(List<Long> ids) {
        if (MixHelper.isEmpty(ids)) {
            return;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            mapper.updateAuthTimeBatch(ids);
        } finally {
            ss.close();
        }
    }

    /**
     * 分页查询微信用户
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午12:15:00
     */
    public static Page<WechatUser> findByVo(WechatUserVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            int totalCount = mapper.countVo(vo);
            Page<WechatUser> pw = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            pw.items = mapper.selectListByVo(vo);
            return pw;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据openid 查询微信用户
     *
     * @param openId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午4:47:53
     */
    public static WechatUser findByOpenId(String openId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            return mapper.selectByOpenId(openId);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午6:19:27
     */
    public static WechatUser findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            WechatUserMapper mapper = ss.getMapper(WechatUserMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 检查用户是否有模块的访问权限
     *
     * @param type
     * @param moudleId
     * @param openId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午4:56:44
     */
    public static boolean checkRoleInMoudle(String type, String moudleId, String openId) {

        // 微信用户查询
        WechatUser wechatUser = WechatUser.findByOpenId(openId);
        if (wechatUser == null) {
            log.info("wechatUser is null,{}", openId);
            return false;
        }
        // 授权校验
        AuthManage am = AuthManage.findByMoubleId(moudleId, type);
        if (am == null) {
            log.info("moudle is not setting ~", moudleId);
            return false;
        }
        log.info("moudle preset list:{}", am.preset);
        if (MixHelper.isNotEmpty(am.users) && am.users.contains(wechatUser.id)) {
            return true;
        }
        // 如果授权用户列表无当前用户，那么继续检查是否预设用户
        if (MixHelper.isNotEmpty(am.preset) && !Strings.isNullOrEmpty(wechatUser.nick)) {
            String isPreset = "";
            // 预设用户
            for (String nick : am.preset) {
                if (Strings.isNullOrEmpty(nick)) {
                    continue;
                }
                nick = nick.trim();
                if (wechatUser.nick.indexOf(nick) != -1) {
                    isPreset = nick;
                    break;
                }
            }
            if (Strings.isNullOrEmpty(isPreset)) {
                return false;
            }
            // 消耗当前nick，添加到正式权限列表
            am.users.add(wechatUser.id);
            am.preset.remove(isPreset);
            am.save();
            log.info("use nick = '{}' okay ~", isPreset);
            // 更新授权时间
            wechatUser.updateAuthTimeAndLast();
            return true;
        }
        return false;
    }
}
