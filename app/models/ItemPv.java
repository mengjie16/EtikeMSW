package models;

import java.util.List;
import java.util.Map;

import com.aton.util.CacheUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import enums.constants.CacheType;
import odms.context.ItemPvContext;

/**
 * 商品访问量
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月30日 下午1:16:59
 */
public class ItemPv {

    // 宝贝id
    public long _id;
    // pc 访问量
    public long pc;
    // 无线端 访问量
    public long wireless;
    // pc 专辑商品访问量
    public long pcInAlbum;
    // 无线端 专辑商品访问量
    public long wirelessInAlbum;

    public Map toMap() {
        Map map = Maps.newHashMap();
        map.put("_id", _id);
        map.put("pc", pc);
        map.put("wireless", wireless);
        map.put("pcInAlbum", pcInAlbum);
        map.put("wirelessInAlbumm", wirelessInAlbum);
        return map;
    }

    /**
     * 保存当前信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午1:28:35
     */
    public boolean save() {
        ItemPvContext pvContext = new ItemPvContext();
        return pvContext.save(this);
    }

    /**
     * 查询指定条件数据
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:28:11
     */
    public static ItemPv findById(long id) {
        ItemPvContext pvContext = new ItemPvContext();
        return pvContext.findByItemId(id);
    }

    /**
     * 删除当前信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午1:29:29
     */
    public boolean delete() {
        ItemPvContext pvContext = new ItemPvContext();
        return pvContext.deleteById(this._id);
    }

    /**
     * 自增指定field字段的数据
     *
     * @param itemId
     * @param field
     * @param incrNum
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:18:55
     */
    private static boolean incrementField(long itemId, String field, int incrNum) {
        ItemPvContext pvContext = new ItemPvContext();
        return pvContext.updateFieldByIncrement(itemId, field, incrNum);
    }

    /**
     * 更新商品pc浏览量
     *
     * @param itemId
     * @param incrNum
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:21:52
     */
    public static boolean incrementPc(long itemId, int incrNum) {
        return incrementField(itemId, "pc", incrNum);
    }

    /**
     * 更新商品无线端浏览量
     *
     * @param itemId
     * @param incrNum
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:22:19
     */
    public static boolean incrementWireless(long itemId, int incrNum) {
        return incrementField(itemId, "wireless", 1);
    }

    /**
     * 更新商品pc端 专辑中浏览量
     *
     * @param itemId
     * @param incrNum
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:22:43
     */
    public static boolean incrementPcInAlbum(long itemId, int incrNum) {
        return incrementField(itemId, "pcInAlbum", 1);
    }

    /**
     * 更新商品无线端端 专辑中浏览量
     *
     * @param itemId
     * @param incrNum
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月30日 下午2:22:43
     */
    public static boolean incrementWirelessInAlbum(long itemId, int incrNum) {
        return incrementField(itemId, "wirelessInAlbum", 1);
    }
    
    /**
     * 检查有效的访问
     *
     * @param cacheKey
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年7月31日 上午9:50:16
     */
    public static boolean checkIsValidAccessPv(String cacheKey) {
        if(Strings.isNullOrEmpty(cacheKey)){
            return false;
        }
        String key = CacheType.ITEM_PV_DATA.getKey(cacheKey);
        Object visitied = CacheUtils.get(key);
        if (visitied == null) {
            // 10 分钟的缓存，不能访问
            CacheUtils.set(key, Boolean.TRUE, CacheType.ITEM_PV_DATA.expiredTime);
            return true;
        }
        return false;
    }
}
