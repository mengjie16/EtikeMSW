package models;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.joda.time.DateTime;

import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import odms.context.AuthManageContext;
import play.data.binding.As;
import play.data.validation.Match;
import play.data.validation.Required;

/**
 * 授权管理
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月5日 下午12:34:51
 */
public class AuthManage {

    public String _id;
    /* 模块标示 */
    public String moudleId;
    @Match("album")
    public String type;
    @As(",")
    public List<Long> users;
    @As(",")
    public List<String> preset;
    public String createTime;

    public List<WechatUser> wechatUsers;

    public Map toMap() {
        Map map = Maps.newHashMap();
        map.put("moudleId", moudleId);
        map.put("type", type);
        fiterData();
        map.put("users", users);
        map.put("preset", preset);
        map.put("createTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }

    private void fiterData() {
        if (MixHelper.isEmpty(users)) {
            users = Lists.newArrayList();
        }
        if (MixHelper.isEmpty(preset)) {
            preset = Lists.newArrayList();
        }
        users.removeIf(new Predicate<Long>() {

            @Override
            public boolean test(Long t) {
                // TODO Auto-generated method stub
                return t == null;
            }
        });
        preset.removeIf(new Predicate<String>() {

            @Override
            public boolean test(String t) {
                // TODO Auto-generated method stub
                return Strings.isNullOrEmpty(t);
            }
        });
    }

    /**
     * 保存当前
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午12:59:31
     */
    public boolean save() {
        AuthManageContext context = new AuthManageContext();
        if (!Strings.isNullOrEmpty(this._id)) {
            fiterData();
            context.updateFieldByParams(_id, "preset", preset);
            context.updateFieldByParams(_id, "users", users);
            return true;
        }
        return context.save(this);
    }

    /**
     *  根据模块标示查找数据
     *
     * @param moudleId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午1:04:58
     */
    public static AuthManage findByMoubleId(String moudleId, String type) {
        AuthManageContext context = new AuthManageContext();
        AuthManage am = context.findByField("moudleId", moudleId, type);
        if (am != null) {
            am.fiterData();
        }
        return am;
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午10:35:22
     */
    public static AuthManage findById(String id) {
        AuthManageContext context = new AuthManageContext();
        AuthManage am = context.findById(id);
        if (am != null) {
            am.fiterData();
        }
        return am;
    }

    /**
     * 根据条件删除相应数据
     *
     * @param type
     * @param moudleId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午11:44:03
     */
    public static boolean deleteByCondition(String type, String moudleId) {
        AuthManageContext context = new AuthManageContext();
        return context.deleteByTypeMoudle(type, moudleId);

    }

    /**
     * 转换详情
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月5日 下午6:16:20
     */
    public void parseTodetail() {
        if (MixHelper.isNotEmpty(this.users)) {
            this.wechatUsers = Lists.transform(this.users, new Function<Long, WechatUser>() {

                @Override
                public WechatUser apply(Long id) {
                    // TODO Auto-generated method stub
                    return WechatUser.findById(id);
                }

            });
        }
    }

}
