package models;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;

import com.aton.util.CacheUtils;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.constants.CacheType;
import enums.constants.RegexConstants;
import odms.context.FreightTempContext;
import play.data.binding.As;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import vos.FreightSearchVo;

/**
 * 运费模板
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月8日 下午5:49:30
 */
public class FreightTemp implements java.io.Serializable {

    /**
     * 模板id
     */
    public long _id;
    /**
     * 用户id(供应商，管理员都可以)
     */
    public int userId;
    /**
     * 模板名称
     */
    @Match(RegexConstants.ABC_NUM_CN)
    @MaxSize(20)
    public String tempName;

    // 模板类型(动态，静态)
    @Match("dynamic|static")
    public String tempType;
    /**
     * 配置列表
     */
    @As(",")
    @CheckWith(value = CheckFreight.class, message = "省份运费配置不正确")
    public List<Freight> freights = Lists.newArrayList();

    public Date createTime;

    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    /**
     * 检查运费配置
     * 
     * @author Calm
     * @since v1.0
     * @created 2016年8月10日 上午10:25:57
     */
    static class CheckFreight extends Check {

        @Override
        public boolean isSatisfied(Object classObj, Object valueObj) {
            // 当前运费配置列表
            List<Freight> freights = (List<Freight>) valueObj;
            if (MixHelper.isEmpty(freights)) {
                return true;
            }
            List<Region> provinces = Region.findByParentIdWithCache(1);

            // 过滤运费配置为空及重复项
            List<Freight> fiterFreights = freights.stream()
                .filter(f -> f.provinces != null && f.provinces.size() > 0 && f.price >= 0)
                .peek(f -> f.provinces.stream().distinct()).collect(Collectors.toList());

            // 所有省份键值对
            Map<Integer, String> stdProvinceMap = provinces.stream()
                .collect(Collectors.toMap(reg -> reg.id, reg -> reg.name));
            // 设置的价位档
            for (Freight fei : fiterFreights) {
                fei.provinces.parallelStream().forEach(p -> {
                    stdProvinceMap.remove(p);
                });
            }

            return stdProvinceMap.isEmpty();
        }

    }

    public Map toMap() {
        List<Map> freightMap = Lists.newArrayList();
        // 运费省份配置列表
        if (MixHelper.isNotEmpty(freights)) {
            freightMap = freights.stream().map(f -> f.toMap()).collect(Collectors.toList());
        }
        Date now = DateTime.now().toDate();
        Map params = Maps.newHashMap();
        params.put("tempName", StringUtils.trim(tempName));
        params.put("userId", userId);
        params.put("tempType", tempType);
        params.put("freights", freightMap);
        params.put("createTime", now);
        return params;
    }

    /**
     * 根据重量计算当前运费模板价格
     *
     * @param singleItemGrossWeight 单件商品的毛重
     * @param count 商品的件数
     * @return 省份对应的邮费Map
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午1:44:50
     */
    public Map<Integer, Integer> calcShippFee(double singleItemGrossWeight, int count) {
        Validate.isTrue(count > 0, "Item count invalid");
        Map<Integer, Integer> result = Maps.newHashMap();

        // 静态模板
        if ("static".equals(tempType)) {
            for (Freight freight : freights) {
                for (Integer ikey : freight.provinces) {
                    result.put(ikey, (int) freight.price * count);
                }
            }
            return result;
        }
        // 动态模板，遍历几个档
        for (Freight freight : freights) {
            // 首重价格,或无法计算价格
            double price = 0;
            double weight = singleItemGrossWeight * count;
            // 未超出首重
            if (weight <= freight.firstWeight) {
                price = freight.fwPrice;
            } else { // 超出首重
                // 单件总邮费为，首重价格＋续重千克＊续重价格
                price = (weight - freight.firstWeight) / freight.addedWeight * freight.awPrice + freight.fwPrice;
            }
            // 邮费(金额元向上取整［520分＝6元］，邮费不可能很大)
            int lastPrice = (int) (Math.ceil(price / 100) * 100);
            for (Integer ikey : freight.provinces) {
                result.put(ikey, lastPrice);
            }
        }

        return result;
    }

    /**
     * 保存当前信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月22日 下午6:32:29
     */
    public boolean save() {
        FreightTempContext context = new FreightTempContext();
        if (_id == 0) {
            // 新增
            boolean result = context.saveReturnId(this);
            if (result) {
                clearFreightTempCache();
            }
        }
        // 更新
        // 运费省份配置列表
        if (MixHelper.isEmpty(freights)) {
            return false;
        }
        List<Map> freightMap = freights.parallelStream().map(f -> f.toMap()).collect(Collectors.toList());
        Map<String, Object> params = Maps.newHashMap();
        params.put("freights", freightMap);
        if (!Strings.isNullOrEmpty(tempName)) {
            tempName = StringUtils.trim(tempName);
            params.put("tempName", tempName);
        }
        boolean result = context.updateFieldById(_id, params);
        if (result) {
            clearFreightTempCache();
        }
        return result;
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
    public static FreightTemp findById(long id) {
        FreightTempContext context = new FreightTempContext();
        return context.findById(id);
    }

    /**
     * 移除缓存中供应商模板信息
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年8月10日 下午10:24:33
     */
    private void clearFreightTempCache() {
        String key = CacheType.SUPPLIER_FREIGHT_TEMPS.getKey(userId);
        CacheUtils.remove(key);
        CacheUtils.remove(CacheType.SUPPLIER_FREIGHT_TEMPS.getKey("all"));
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
        FreightTempContext context = new FreightTempContext();
        if (context.deleteById(this._id)) {
            this.clearFreightTempCache();
            return true;
        }
        return false;
    }

    /**
     * 检查当前模板是否已有宝贝应用
     *
     * @param index
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月11日 下午4:06:03
     */
    public int checkItemUse() {
        // 获取当前模板
        int count = Item.checkExsitFreightTempUse(this._id);
        return count;
    }

    /**
     * 检查模板是否重复
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 上午11:48:12
     */
    public boolean checkNameDuplicate() {
        FreightTempContext context = new FreightTempContext();
        return context.checkFieldValueExsit(this._id, "tempName", this.tempName);
    }

    /**
     * 查找所有运费模板(用户下所有模板)
     *
     * @param userId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午1:18:14
     */
    public static List<FreightTemp> findUserFreightTempAllInCache(int userId) {
        String key = CacheType.SUPPLIER_FREIGHT_TEMPS.getKey(userId);
        List<FreightTemp> temps = CacheUtils.get(key);
        if (MixHelper.isEmpty(temps)) {
            FreightTempContext context = new FreightTempContext();
            temps = context.findUserAll(userId);
            if (MixHelper.isNotEmpty(temps)) {
                CacheUtils.set(key, temps, CacheType.SUPPLIER_FREIGHT_TEMPS.expiredTime);
            }
        }
        return temps;
    }

    /**
     * 
     * 计算商品运费
     *
     * @param tempId
     * @param grossWeight 单件商品的毛重
     * @param count
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月7日 下午3:46:42
     */
    public static Map<Integer, Integer> calculateItemFee(long tempId, double grossWeight, int count) {
        // 查找运费模板
        FreightTemp freightTemp = findById(tempId);
        if (freightTemp == null) {
            return null;
        }
        return freightTemp.calcShippFee(grossWeight, count);
    }

    /**
     * 分页查询运费模板
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 下午2:02:59
     */
    public static Page<FreightTemp> findFreightTempByVo(FreightSearchVo vo) {
        FreightTempContext context = new FreightTempContext();
        Page<FreightTemp> pages = Page.newInstance(1, 10, 0);
        if (vo == null || Strings.isNullOrEmpty(vo.aboutBrand) && Strings.isNullOrEmpty(vo.aboutSupplier)) {
            pages.items = context.findAll(null, 1, 10);
            pages.totalCount = pages.items.size();
        } else {
            pages = Page.newInstance(vo.pageNo, vo.pageSize, 0);
            List<Long> ids = Item.findFreightTempByVo(vo);
            if (MixHelper.isNotEmpty(ids)) {
                pages.items = context.findAll(ids, vo.pageNo, vo.pageSize);
                pages.totalCount = pages.items.size();
            }
        }
        return pages;
    }
}
