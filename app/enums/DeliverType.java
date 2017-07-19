package enums;

/**
 * 
 * 发货方式
 * 
 * @author tr0j4n
 * @since  v1.0
 * @created 2016年4月11日 下午8:33:36
 */
public enum DeliverType {
	
    DOMESTIC_SPOT("国内现货"),
    BONDED_WAREHOUSE_GOODS("保税仓商品"),
    ONLINE_ORDER("国内R货"),
    UN_LOGISTICS("保税Q仓商品"),
    OVERSEA_DIRECT_DELIVERY("海外直邮");
    public String description;

	private DeliverType(String description){
		this.description = description;
	}
}
