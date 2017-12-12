package models;

/**
 * 订单中商品信息
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年9月2日 下午1:49:23
 */
public class ProductInfo {

    /** 商品id */
    public long itemId;
    /** 规格 */
    public String sku;
    /** 订单下单，商品价格快照 */
    public double itemPrice;

    public String picUrl;
    public String title;
    public String color;
    public String brandName;
    public double retailPrice;
    public int productNum;

    public ProductInfo() {
    }

    public ProductInfo(long itemId, String sku, int itemPrice) {
        super();
        this.itemId = itemId;
        this.sku = sku;
        this.itemPrice = itemPrice;
    }

}
