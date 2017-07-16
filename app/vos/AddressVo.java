package vos;

import java.util.Date;

import enums.constants.RegexConstants;
import models.Supplier.ProvinceCheck;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;

public class AddressVo {
    public int id;
    /** 名称 */
    public String name;
    
    public String phone;    
    
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
    
    public AddressVo(){
        
    }
}
