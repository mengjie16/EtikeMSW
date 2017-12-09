package models;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * 活动列表配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年6月22日 下午3:25:34
 */
public class ItemSetting {

    public long id;
    // 活动商品(单位：分)
    public int price;
    // --- 只是展示使用
    public double currentPrice;

    public String img;
    public String title;

    public Map toMap() {
        return ImmutableMap.of("id", id, "price", price * 100);
    }

    /**
     * 正式应用价格无需再次转分
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月19日 上午2:27:10
     */
    public Map toBuildMap() {
        return ImmutableMap.of("id", id, "price", price);
    }

    /**
     * 转换为详情
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月18日 下午11:36:44
     */
    public ItemSetting detail() {
        Item item = Item.findBaseInfoById(this.id);
        if (item != null) {
            this.img = item.picUrl;
            this.title = item.title;
            this.currentPrice = item.itemLastFee();
        }
        return this;
    }
}
