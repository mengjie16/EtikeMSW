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
    public int itemPrice;

    public ProductInfo() {
    }
}
