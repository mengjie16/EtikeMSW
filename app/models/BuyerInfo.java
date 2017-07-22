package models;

/**
 * 订单中买家信息
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年9月2日 下午1:49:23
 */
public class BuyerInfo {

    /** 名字 */
    public String name;
    /** 联系方式 */
    public String contact;
    /** 省 */
    public String province;
    /** 城市 */
    public String city;
    /** 区域 */
    public String region;
    /** 具体地址 */
    public String address;
    /** 省份id */
    public int provinceId;
    /**
     * 最初详细地址
     */
    public String address_detail_pcra;

    public BuyerInfo() {
    }

    public BuyerInfo(String name, String contact, String province, String city, String region, String address, int provinceId) {
        super();
        this.name = name;
        this.contact = contact;
        this.province = province;
        this.city = city;
        this.region = region;
        this.address = address;
        this.provinceId = provinceId;
    }
    
    
}
