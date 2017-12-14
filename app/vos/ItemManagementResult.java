package vos;

import models.Brand;
import models.Item;
import play.data.validation.Required;

/**
 * 
 * 商品信息
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月11日 下午8:13:55
 */
public class ItemManagementResult {

    public long id;
    public String title;
    /** 类目id */
    public long cateId;
    /** 品牌id */
    public long brandId;
    public String brandName;
    @Required
    /** 商品主图 */
    public String picUrl;
    /** 商家自己的编码，货号 */
    public String outNo;
    public double retailPrice;
    /** 供货价 */
    public double supplyPrice;

    /**
     * 
     */
    public static ItemManagementResult valueOfItem(Item item) {
        if (item == null)
            return null;
        ItemManagementResult im = new ItemManagementResult();
        im.id = item.id;
        im.title = item.title;
        im.cateId = item.cateId;
        im.brandId = item.brandId;
        im.picUrl = item.picUrl;
        im.outNo = item.outNo;
        im.retailPrice = item.retailPrice;
        im.supplyPrice = item.retailPrice;
        im.brandName = Brand.findBrandWithCacheMap(item.brandId).name;
        return im;
    }

}
