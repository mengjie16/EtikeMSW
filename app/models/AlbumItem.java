package models;

import java.util.Date;

import org.joda.time.DateTime;

import com.google.common.base.Objects;

/**
 * 专辑商品
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月20日 上午11:24:46
 */
public class AlbumItem {
    // 商品ID
    public long itemId;
    // 商品品牌（pc端用）
    public Brand brand;
    // 商品规格（pc端用）
    public String sku;
    // 商品零售价（pc端用）
    public double retailPrice;
    // 商品供货价（pc端用）
    public double supplyPrice;
    // 商品标题
    public String title;
    // 单位分
    public double price;
    // 商品主图
    public String picUrl;
    // 利润
    public double profit;
    // 更新时间
    public Date updateTime;

    public AlbumItem() {
    }

    public AlbumItem(long itemId, double price) {
        this.itemId = itemId;
        this.price = price;
        this.updateTime = DateTime.now().toDate();
    }

    public AlbumItem(long itemId, double price, String picUrl) {
        this.itemId = itemId;
        this.price = price;
        this.picUrl = picUrl;
        this.updateTime = DateTime.now().toDate();
    }

    /**
     * 价格转换为分
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午12:00:44
     */
    public AlbumItem parseFee() {
        this.price *= 100;
        return this;
    }

    /**
     * 检查当前宝贝ID是否存在(在新添加的时候需要校验)
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:30:20
     */
    public boolean checkExsitItem() {
        return Item.findBaseInfoById(this.itemId) != null;
    }

    /**
     * 价格排序
     *
     * @param other
     * @param type
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 上午12:27:47
     */
    public int sortPrice(AlbumItem other, String type) {
        if (other == null)
            return 0;
        // 当前价格大于下一个价格
        if (this.price > other.price) {
            return Objects.equal(type, "a") ? 1 : -1;
        }
        if (this.price < other.price) {
            return 0;
        }
        return Objects.equal(type, "a") ? -1 : 1;
    }

    /**
     * 折扣排序
     *
     * @param other
     * @param type
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月22日 上午12:27:31
     */
    public int sortProfit(AlbumItem other, String type) {
        if (other == null)
            return 0;
        // 当前利润大于下一个利润
        if (this.profit > other.profit) {
            return Objects.equal(type, "a") ? 1 : -1;
        }
        if (this.profit == other.profit) {
            return 0;
        }
        return Objects.equal(type, "a") ? -1 : 1;
    }
}
