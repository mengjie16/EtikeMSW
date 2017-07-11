package vos;

import com.aton.util.StringUtils;

/**
 * 供货商视图数据结构
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月9日 上午11:13:13
 */
public class SupplierVo{

    public int id;
    /** 名字 */
    public String name;
    /** 公司名字 */
    public String company;
    /** 公司主营行业 */
    public String industry;
    /** 公司主营产品 */
    public String product;
    /** 国家 */
    public String country;
    /** 国家的地区id */
    public int countryId;
    /** 省 */
    public String province;
    /** 省的地区id */
    public int provinceId;
    /** 城市 */
    public String city;
    /** 区域 */
    public String region;
    /** 具体地址 */
    public String address;
    /** 公司邮箱 */
    public String email;

    public SupplierVo() {

    }
}