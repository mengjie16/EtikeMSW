package models;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;
import odms.context.StarItemContext;
import odms.context.HomePageSettingContext;
import odms.context.StarItemContext;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.ItemStatus;
import enums.constants.CacheType;
import play.data.binding.As;
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
public class StarItemSetting implements java.io.Serializable {

    public long _id;
    /**
     * 商品
     */
    @As(",")
    public List<Long> itemIds;

    /**
     * 创建时间
     */
    public String updateTime;

    /**
     * 前端显示作用
     */
    public List<ItemVo> items;

    public Map toMap() {
        Map map = Maps.newHashMap();
        map.put("itemIds", itemIds);
        map.put("updateTime", DateTime.now().toString(DateUtils.YYYY_MM_DD_HH_MM_SS));
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
        StarItemContext context = new StarItemContext();
        if (context.save(this)) {
            clearCache();
            return true;
        }
        return false;
    }

    /**
     * 更新域内的内容
     * 
     * @param _id
     * @param field
     * @param value
     * @return
     */
    public static boolean updateValueByField(long _id, String field, Object value) {
        StarItemContext context = new StarItemContext();
        if (context.updateFieldById(_id, field, value)) {
            clearCache();
            return true;
        }
        return false;
    }

    /**
     * 根据ID查询信息
     *
     * @param _id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月25日 下午1:30:53
     */
    public static StarItemSetting findById(String _id) {
        StarItemContext context = new StarItemContext();
        return context.findBySettingById(_id);
    }

    /**
     * 查找首个
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午2:36:21
     */
    public static StarItemSetting findFirst() {
        StarItemContext context = new StarItemContext();
        return context.findFirst();
    }

    /**
     * 从缓存中查找明星产品配置
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午3:42:40
     */
    public static StarItemSetting findDefaultByCache() {
        String key = CacheType.ITEM_STAR_DATA.getKey("deault");
        StarItemSetting sis = CacheUtils.get(key);
        if (sis == null) {
            sis = StarItemSetting.findFirst();
            if (sis != null) {
                sis.parseToItemVos();
            }
            CacheUtils.set(key, sis, CacheType.ITEM_STAR_DATA.expiredTime);
        }
        return sis;
    }

    /**
     * 清除缓存
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午3:43:39
     */
    public static void clearCache() {
        String key = CacheType.ITEM_STAR_DATA.getKey("deault");
        CacheUtils.remove(key);
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
        StarItemContext context = new StarItemContext();
        return context.deleteById(this._id);
    }

    /**
     * 检查商品是否存在
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午2:47:01
     */
    public long checkItemExsit() {
        if (MixHelper.isEmpty(this.itemIds)) {
           return 0;
        }
        // 检查每项商品
        for (long id : this.itemIds) {
            Item item = Item.findById(id);
            // 商品不存在
            if (item == null || item.status != ItemStatus.ONLINE) {
                return id;
            }
        }
        return 0;
    }

    /**
     * 检查商品重复
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午3:54:36
     */
    public long checkItemRepeat() {
        if (MixHelper.isEmpty(this.itemIds)) {
            return 0;
        }
        for (long id : this.itemIds) {
            int count = 0;
            for (long cid : this.itemIds) {
                if (cid == id) {
                    count++;
                }
            }
            if (count > 1) {
                return id;
            }
        }
        return 0;
    }

    /**
     * 解析出详情
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月17日 上午2:38:16
     */
    public void parseToItemVos() {
        if (MixHelper.isEmpty(this.itemIds)) {
            return;
        }
        List<Item> items = this.itemIds.stream().map(i -> Item.findById(i)).collect(Collectors.toList());
        if (MixHelper.isNotEmpty(items)) {
            this.items = items.stream().map(i -> ItemVo.valueOfItem(i, true)).collect(Collectors.toList());
        }
    }
}
