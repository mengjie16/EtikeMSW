package models;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.Min;

import org.apache.thrift.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.util.MixHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import odms.context.ItemLocationContext;
import play.data.validation.MaxSize;
import play.data.validation.Required;

/**
 * 
 * 描述一个物理地址
 * 
 * @author Tr0j4n
 * @since v1.0
 * @created 2016年4月13日 下午11:45:48
 */
public class ItemLocation {

    private static final Logger log = LoggerFactory.getLogger(ItemLocation.class);

    /** 地址主键 */
    public String _id;
    /** 序号 */
    @Required
    @Min(0)
    public int index;
    /** 商品ID */
    public long itemId;
    /** 国家 */
    @Required
    @MaxSize(12)
    public String country;
    /** 国家的地区id */
    public int countryId;
    /** 省 */
    @Required
    @MaxSize(12)
    public String province;
    /** 省的地区id */
    public int provinceId;
    /** 城市 */
    @Required
    @MaxSize(12)
    public String city;
    /** 区域 */
    @Required
    @MaxSize(12)
    public String region;
    /** 具体地址 */
    @MaxSize(32)
    public String address;

    public ItemLocation() {
    }

    /**
     * 参数构造
     * Constructs a <code>ItemLocation</code>
     *
     * @since v1.0
     */
    public ItemLocation(int index, long itemId, String country, int countryId, String province, int provinceId, String city, String region,
        String address) {
        this.index = index;
        this.itemId = itemId;
        this.country = country;
        this.countryId = countryId;
        this.province = province;
        this.provinceId = provinceId;
        this.city = city;
        this.region = region;
        this.address = address;
    }

    public String toBaseLocationStr() {
        return this.province + this.city + this.region + this.address;
    }

    /**
     * to map
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 上午10:20:29
     */
    public Map toMap() {
        Map map = Maps.newHashMap();
        this._id = this.itemId + "_" + this.index;
        map.put("_id", this._id);
        map.put("index", this.index);
        map.put("itemId", this.itemId);
        map.put("country", Option.fromNullable(this.country).or("中国"));
        map.put("countryId", this.countryId == 0 ? 1 : this.countryId);
        map.put("province", this.province);
        map.put("provinceId", this.provinceId);
        map.put("city", this.city);
        map.put("region", this.region);
        map.put("address", this.address);
        return map;
    }

    /**
     * 保存地址信息
     *
     * @param removeOld(是否删除该商品原先的地址信息)
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:28:03
     */
    public boolean save(boolean removeOld) {
        try {
            ItemLocationContext icontext = new ItemLocationContext();
            if (removeOld) {
                icontext.deleteByItemId(this.itemId);
            }
            boolean result = icontext.save(this);
            if (result) {
                Item.clearItemDetailCache(this.itemId);
            }
            return result;
        } catch (Exception ex) {
            log.error("{}", ex);
            return false;
        }
    }

    /**
     * 批量保存商品地址
     *
     * @param params
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:35:04
     */
    public static boolean saveBatch(List<ItemLocation> params) {
        try {
            ItemLocationContext icontext = new ItemLocationContext();
            boolean result = icontext.batchSaveIteamLocation(params);
            return result;
        } catch (Exception ex) {
            log.error("{}", ex);
            return false;
        }
    }

    /**
     * 批量保存
     *
     * @param params
     * @param itemId(在保存之前删除该商品原先地址信息)
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:35:53
     */
    public static boolean saveBatch(List<ItemLocation> params, long itemId) {
        if (MixHelper.isEmpty(params)) {
            return false;
        }
        try {
            ItemLocationContext icontext = new ItemLocationContext();
            if (itemId != 0) {
                icontext.deleteByItemId(itemId);
            }
            boolean result = icontext.batchSaveIteamLocation(params);
            if (result) {
                Item.clearItemDetailCache(itemId);
            }
            return result;
        } catch (Exception ex) {
            log.error("{}", ex);
            return false;
        }
    }

    /**
     * 根据id删除单项地址信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午3:51:33
     */
    public static boolean remove(String id) {
        try {
            ItemLocationContext icontext = new ItemLocationContext();
            boolean result = icontext.deleteById(id);
            return result;
        } catch (Exception ex) {
            log.error("{}", ex);
            return false;
        }
    }

    /**
     * 获取商品发货地址列表
     *
     * @param itemId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 上午1:44:33
     */
    public static List<ItemLocation> findItemLactionsByItemId(long itemId) {
        try {
            ItemLocationContext icontext = new ItemLocationContext();
            List<ItemLocation> result = icontext.findByItemId(itemId);
            return result;
        } catch (Exception ex) {
            log.error("{}", ex);
            return Lists.newArrayList();
        }
    }
}
