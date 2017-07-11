package models;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.mappers.ArticleMapper;
import models.mappers.ItemMapper;
import odms.context.H5ActivityPageSettingContext;
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
 * h5活动页面配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月15日 上午11:13:25
 */
public class H5ActivityPageSetting implements java.io.Serializable {

    public String _id;

    /**
     * 活动标题
     */
    public String title;
    /**
     * 是否应用
     */
    public boolean isUse;

    /**
     * 组合商品列表
     */
    public List<Long[]> groupItems = Lists.newArrayList();
    /**
     * 组合商品列表vo
     */
    public List<List<ItemVo>> groupItemVos = Lists.newArrayList();

    /**
     * 创建时间
     */
    public String createTime;

    public Map toMap() {
        Map map = Maps.newHashMap();
        map.put("title", title);
        map.put("groupItems", groupItems);
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
    public static H5ActivityPageSetting findById(String _id) {
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
    public static H5ActivityPageSetting findCurrentUse() {
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
    public static List<H5ActivityPageSetting> findList(int pageNumber, int pageSize) {
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
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
        H5ActivityPageSettingContext context = new H5ActivityPageSettingContext();
        return context.deleteById(this._id);
    }

    /**
     * 解析详情
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月15日 下午1:07:29
     */
    public void toDetail() {
        if (this != null && MixHelper.isNotEmpty(this.groupItems)) {
            this.groupItemVos = Lists.transform(this.groupItems, new Function<Long[], List<ItemVo>>() {

                @Override
                public List<ItemVo> apply(Long[] ids) {
                    List<ItemVo> vos = Lists.newArrayList();
                    if (MixHelper.isNotEmpty(ids)) {
                        for (int i = 0; i < ids.length; i++) {
                            ItemVo iv = ItemVo.valueOfBase(ids[i]);
                            if (iv != null) {
                                vos.add(iv);
                            }
                        }
                    }
                    return vos;
                }
            });
        }
    }
}
