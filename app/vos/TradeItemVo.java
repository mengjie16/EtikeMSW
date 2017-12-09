package vos;

/**
 * 交易商品信息 vo
 * 
 * @author sun
 *
 */
public class TradeItemVo implements java.io.Serializable {
    // id
    public long id;
    // 主图
    public String picUrl;

    public String title;
    public String color;
    public String brandName;
    public double retailPrice;

    public TradeItemVo() {
    }

    public TradeItemVo(long id, String picUrl) {
        this.id = id;
        this.picUrl = picUrl;
    }

    public TradeItemVo(long id, String picUrl, String title, String color, String brandName, double retailPrice) {
        super();
        this.id = id;
        this.picUrl = picUrl;
        this.title = title;
        this.color = color;
        this.brandName = brandName;
        this.retailPrice = retailPrice;
    }

}