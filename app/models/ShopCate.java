package models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 店铺自定义类目
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月27日 下午4:51:05
 */
public class ShopCate implements java.io.Serializable {

    private static final long serialVersionUID = 8383401375108767742L;

    private static final Logger log = LoggerFactory.getLogger(ShopCate.class);

    public static final String TABLE_NAME = "shop_cate";

    public int id;
    /** 供应商的id */
    public int supplierId;

    public String name;
    /** 排序权重 */
    public int ordinal;
}
