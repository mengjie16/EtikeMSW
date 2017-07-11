package models;

/**
 * 
 * 标准的，统一化的产品信息。前期用不上
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月11日 下午8:05:56
 */
public class Product implements java.io.Serializable {

    public long id;

    public String name;
    /** 单位 */
    public String unit;
    public String picUrl;
    /** Key:value这样的键值对的属性 */
    public String props;

    /**
     * 价格
     */
    public int price;

}
