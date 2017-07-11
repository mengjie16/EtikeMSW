package models;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;
import odms.context.HomePageSettingBuildsContext;
import odms.context.HomePageSettingContext;

import org.apache.commons.collections.ListUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.constants.CacheType;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;

/**
 * 首页配置实体
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月22日 下午3:25:24
 */
public class HomePageSetting implements java.io.Serializable {

    private static final long serialVersionUID = 20160127125545L;

    public String _id;
    /**
     * 配置主题名称
     */
    public String name;
    /**
     * 大Banner设置
     */
    public List<BigBannerSetting> big_BannerSettings;
    /**
     * 右侧小Banner设置
     */
    public List<RightSmallBannerSetting> right_SmallBannerSettings;
    /**
     * 品牌列表设置
     */
    public List<BrandSetting> brandSettings;
    /**
     * 活动配置
     */
    public List<ActivitySetting> activitySettings;

    public String type = "main";
    /**
     * 是否应用
     */
    public boolean isUse;
    /**
     * 模版修改时间
     */
    public String updateTime;
    /**
     * 创建时间
     */
    public String createTime;

    /** 前端传值设置用。。。 */
    public BigBannerSetting bigBannerSetting;
    public RightSmallBannerSetting right_SmallBannerSettig;
    public BrandSetting brandSetting;
    public ActivitySetting activitySetting;

    public Map toMap() {
        Map map = Maps.newHashMap();
        // 来个空包
        map.put("big_BannerSettings", ListUtils.EMPTY_LIST);
        map.put("right_SmallBannerSettings", ListUtils.EMPTY_LIST);
        map.put("brandSettings", ListUtils.EMPTY_LIST);
        map.put("activitySettings", ListUtils.EMPTY_LIST);

        if (MixHelper.isNotEmpty(big_BannerSettings)) {
            map.put("big_BannerSettings", this.big_BannerSettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        if (MixHelper.isNotEmpty(right_SmallBannerSettings)) {
            map.put("right_SmallBannerSettings", this.right_SmallBannerSettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        if (MixHelper.isNotEmpty(brandSettings)) {
            map.put("brandSettings", this.brandSettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        if (MixHelper.isNotEmpty(activitySettings)) {
            map.put("activitySettings", this.activitySettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        map.put("type", type);
        map.put("isUse", isUse);
        map.put("name", name);
        map.put("updateTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        map.put("createTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }
    
    public Map toBuildMap() {
        Map map = Maps.newHashMap();
        // 来个空包
        map.put("big_BannerSettings", ListUtils.EMPTY_LIST);
        map.put("right_SmallBannerSettings", ListUtils.EMPTY_LIST);
        map.put("brandSettings", ListUtils.EMPTY_LIST);
        map.put("activitySettings", ListUtils.EMPTY_LIST);

        if (MixHelper.isNotEmpty(big_BannerSettings)) {
            map.put("big_BannerSettings", this.big_BannerSettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        if (MixHelper.isNotEmpty(right_SmallBannerSettings)) {
            map.put("right_SmallBannerSettings", this.right_SmallBannerSettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        if (MixHelper.isNotEmpty(brandSettings)) {
            map.put("brandSettings", this.brandSettings.stream().map(b -> b.toMap()).collect(Collectors.toList()));
        }
        if (MixHelper.isNotEmpty(activitySettings)) {
            map.put("activitySettings", this.activitySettings.stream().map(b -> b.toBuildMap()).collect(Collectors.toList()));
        }
        map.put("type", type);
        map.put("isUse", isUse);
        map.put("name", name);
        map.put("updateTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        map.put("createTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
        return map;
    }

    /**
     * 保存当前主题配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:32:29
     */
    public boolean save() {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.save(this);

    }

    /**
     * 更新当前模版编辑时间
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月12日 下午3:26:12
     */
    public static boolean updateTime(String _id) {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.updateFieldById(_id, "updateTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
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
        HomePageSettingContext context = new HomePageSettingContext();
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
        HomePageSettingContext context = new HomePageSettingContext();
        return context.updateFieldByIndexId(_id, field, index, value);
    }

    /**
     * 更新field的值
     *
     * @param _id
     * @param field
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月1日 下午1:27:34
     */
    public static boolean updateValueByField(String _id, String field, Object value) {
        HomePageSettingContext context = new HomePageSettingContext();
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
        HomePageSettingContext context = new HomePageSettingContext();
        return context.removeFieldValueByIndex(_id, field, index);
    }

    /**
     * 查找一个主题
     *
     * @param _id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:30:53
     */
    public static HomePageSetting findById(String _id) {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.findBySettingById(_id);
    }

    /**
     * 在缓存中获取build主题配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午4:22:10
     */
    public static HomePageSetting findBuildByCache() {
        return findHomePageSettingDetailCache("homepage_set_build");
    }

    /**
     * 获取build主题配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:49:41
     */
    public static HomePageSetting findBuildSetting() {
        HomePageSettingBuildsContext context = new HomePageSettingBuildsContext();
        return context.findDefaultSetting();
    }

    /**
     * 查找列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午1:37:14
     */
    public static List<HomePageSetting> findList() {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.findList();
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
    public static long count() {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.findCount();
    }

    /**
     * 检查名字是否重复
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月18日 下午11:03:12
     */
    public static long countName(String name) {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.findCountByFeild("name", name);
    }

    /**
     * 删除当前主页配置模板
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午1:58:39
     */
    public boolean delete() {
        HomePageSettingContext context = new HomePageSettingContext();
        return context.deleteById(this._id);
    }

    /**
     * 使用当前主页配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月13日 下午3:06:55
     */
    public boolean useCurrent() {
        HomePageSettingContext context = new HomePageSettingContext();
        boolean result = context.updateCurrentUseOtherUnUse(this._id);
        // 应用成功更新缓存
        if (result) {
            // 发布到正式模版库
            this.createTime = DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
            this.type = "build";
            this.isUse = true;
            HomePageSettingBuildsContext buildContext = new HomePageSettingBuildsContext();
            String idStr = buildContext.saveCurrent(this);
            if (!Strings.isNullOrEmpty(idStr)) {
                buildContext.updateCurrentUseOtherUnUse(idStr);
            } else {
                return false;
            }
            // 删除默认配置的缓存
            removeHomePageDetailCacheById("homepage_set_build");
        }
        return result;
    }

    /**
     * 在缓存中查询主页配置详细信息列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:24:00
     */
    public static List<HomePageSetting> findHomePageSettingDetailListByCache() {
        String key = CacheType.HOME_PAGE_SETTING_LIST.getKey();
        List<HomePageSetting> homePageSettingList = CacheUtils.get(key);
        if (homePageSettingList == null) {
            homePageSettingList = findHomePageSettingDetailList();
            CacheUtils.set(key, homePageSettingList, CacheType.HOME_PAGE_SETTING_LIST.expiredTime);
        }
        return homePageSettingList;
    }

    /**
     * 查找指定的主页配置信息缓存
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:37:36
     */
    public static HomePageSetting findHomePageSettingDetailCache(String id) {
        String key = CacheType.HOME_PAGE_SETTING.getKey(id);
        HomePageSetting homePageSet = CacheUtils.get(key);
        if (homePageSet == null) {
            if (Objects.equal(id, "homepage_set_build")) {
                homePageSet = findBuildSetting();
            }
            if (homePageSet != null) {
                homePageSet.parseToDeatil();
                CacheUtils.set(key, homePageSet, CacheType.HOME_PAGE_SETTING.expiredTime);
            }
        }
        return homePageSet;
    }

    /**
     * 查询主页配置详细信息列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:35:29
     */
    public static List<HomePageSetting> findHomePageSettingDetailList() {
        List<HomePageSetting> homePageSettingList = findList();
        if (MixHelper.isNotEmpty(homePageSettingList)) {
            homePageSettingList = homePageSettingList.stream().map(h -> h.parseToDeatil()).collect(Collectors.toList());
        }
        return homePageSettingList;
    }

    /**
     * 删除主页设置的详情列表缓存
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:28:20
     */
    public static void removeHomePageListDetailCache() {
        String key = CacheType.HOME_PAGE_SETTING_LIST.getKey();
        CacheUtils.remove(key);
    }

    /**
     * 删除指定ID的主页设置缓存
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月19日 下午4:28:37
     */
    public static void removeHomePageDetailCacheById(String id) {
        String key = CacheType.HOME_PAGE_SETTING.getKey(id);
        CacheUtils.remove(key);
    }

    /**
     * 解析完整信息
     *
     * @since v1.0
     * @author sun
     * @created 2016年7月14日 下午1:31:21
     */
    public HomePageSetting parseToDeatil() {
        // 配置活动商品图片
        if (this != null && MixHelper.isNotEmpty(this.activitySettings)) {
            this.activitySettings = this.activitySettings.stream().map(a -> a.detail()).collect(Collectors.toList());
        }
        // 配置品牌信息
        if (this != null && MixHelper.isNotEmpty(this.brandSettings)) {
            this.brandSettings = this.brandSettings.stream().map(b -> b.detail()).collect(Collectors.toList());

        }
        return this;
    }
}
