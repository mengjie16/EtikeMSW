package models;

import java.util.Map;

import play.data.validation.MaxSize;
import play.data.validation.Required;

import com.aton.util.ReflectionUtils;

/**
 * 
 * 描述一个物理地址(用作零售商发货使用地址)
 * 
 * @author Tr0j4n
 * @since  v1.0
 * @created 2016年4月13日 下午11:45:48
 */
public class Location {

    public long id;
    /** 国家 */
    public String country;
    /** 国家的地区id */
    public int countryId;
    /** 省 */
    @Required
    @MaxSize(12)
    public String province;
    /** 省的地区id */
    public int provinceId;
    /** 城市 */
    @Required
    @MaxSize(12)
    public String city;
    /** 区域 */
    @Required
    @MaxSize(12)
    public String region;
    /** 具体地址 */
    @MaxSize(32)
    public String address;

}
