package models;

import java.util.Date;
import java.util.List;
import java.util.Map;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;
import odms.context.ActivityPageSettingContext;
import odms.context.HomePageSettingContext;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;
import vos.ItemVo;

/**
 * 活动页面配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月1日 下午12:11:52
 */
public class ActivityPageSetting implements java.io.Serializable {

    public String _id;
    /**
     * 活动标题
     */
    public String title;
    /**
     * 活动Banner主图
     */
    public String bannerImg;
    /**
     * 活动的商品
     */
    public List<ActivityItem> activityItems;
    /*
     * 仅仅作为传参数使用
     */
    public ActivityItem activityItem;
    /**
     * 页面居中海报图片
     */
    public String middlePoster;
    /**
     * 底部活动商品
     */
    public List<Long> bottomItemIds;
    /**
     * 仅仅作为参数使用
     */
    public Long itemId;
    /**
     * 类型
     */
    public String type = "activity";
    /**
     * 是否应用
     */
    public boolean isUse;
    /**
     * 创建时间
     */
    public String createTime;

    /**
     * 底部活动商品,前端显示作用
     */
    public List<ItemVo> bottomItems;

    public Map toMap() {
        Map map = Maps.newHashMap();
        List<Map> ai_maps = Lists.newArrayList();
        // 活动商品
        if (MixHelper.isNotEmpty(activityItems)) {
            ai_maps = Lists.transform(activityItems, new Function<ActivityItem, Map>() {

                @Override
                public Map apply(ActivityItem arg0) {
                    return arg0.toMap();
                }

            });

        }
        if (MixHelper.isEmpty(this.bottomItemIds)) {
            this.bottomItemIds = Lists.newArrayList();
        }
        map.put("activityItems", ai_maps);
        map.put("title", title);
        map.put("bannerImg", bannerImg);
        map.put("middlePoster", middlePoster);
        map.put("bottomItemIds", bottomItemIds);
        map.put("isUse", isUse);
        map.put("createTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }

    /**
     * 保存当前活动配置配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:32:29
     */
    public boolean save() {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.save(this);

    }

    /**
     * 保存并返回id
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:45:27
     */
    public boolean saveReturnId() {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        String id = context.saveReturnId(this);
        if (Strings.isNullOrEmpty(id)) {
            return false;
        }
        this._id = id;
        return true;
    }

    /**
     * 元素组增加元素
     *
     * @param _id
     * @param field
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:38:28
     */
    public static boolean pushValueToField(String _id, String field, Object value) {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.pushValueToFieldById(_id, field, value);
    }

    /**
     * 更新域内的内容
     *
     * @param _id
     * @param field
     * @param index
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月28日 上午11:36:39
     */
    public static boolean updateValueByFieldIndex(String _id, String field, int index, Object value) {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.updateFieldByIndexId(_id, field, index, value);
    }

    /**
     * 更新域内的内容
     * 
     * @param _id
     * @param field
     * @param value
     * @return
     */
    public static boolean updateValueByField(String _id, String field, Object value) {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.updateFieldById(_id, field, value);
    }

    /**
     * 元素组删除元素
     *
     * @param _id
     * @param field
     * @param index
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午3:51:34
     */
    public static boolean removeValueByFieldIndex(String _id, String field, int index) {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.removeFieldValueByIndex(_id, field, index);
    }

    /**
     * 根据ID查询活动配置信息
     *
     * @param _id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:30:53
     */
    public static ActivityPageSetting findById(String _id) {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.findBySettingById(_id);
    }

    /**
     * 获取默认活动配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午4:22:10
     */
    public static ActivityPageSetting findCurrentUse() {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.findCurrentUseSetting();
    }

    /**
     * 活动列表查询
     *
     * @param pageNumber
     * @param pageSize
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午2:03:46
     */
    public static List<ActivityPageSetting> findList(int pageNumber, int pageSize) {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.findPageList(pageNumber, pageSize);
    }

    /**
     * 统计个数
     *
     * @param type
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:31:17
     */
    public static int count() {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.findCount();
    }

    /**
     * 应用活动设置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月3日 下午12:50:34
     */
    public boolean useActivityPageSetting() {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.updateFieldById(this._id, "isUse", this.isUse);
    }

    /**
     * 删除数据
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月3日 下午2:17:34
     */
    public boolean delete() {
        ActivityPageSettingContext context = new ActivityPageSettingContext();
        return context.deleteById(this._id);
    }
}
