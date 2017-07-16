package models;

import java.util.Date;

import javax.persistence.Transient;
import javax.validation.Valid;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.config.ReturnCode;
import com.aton.db.SessionFactory;
import com.aton.vo.Page;

import enums.constants.RegexConstants;
import models.Supplier.ProvinceCheck;
import models.mappers.AddressMapper;
import models.mappers.SupplierMapper;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MinSize;
import play.data.validation.Required;
import vos.AddressSearchVo;
import vos.AddressVo;


public class Address implements java.io.Serializable {
    
    
    @Transient
    public static final String TABLE_NAME = "address";

    
    public int id;
    /** 名称 */
    public String name;
    
    @Match(RegexConstants.PHONE)
    public String phone;    
    
    @Required
    @MinSize(1)
    /** 国家 */
    public String country;
    /** 国家的地区id */
    public int countryId;
    /** 省 */
    @CheckWith(value = ProvinceCheck.class, message = "省份需填写")
    public String province;
    /** 省的地区id */
    public int provinceId;
    
    /** 城市 */
    public String city;
    /** 区域 */
    public String region;
    /** 具体地址 */
    public String address;
    
    public Date createTime;
    public Date updateTime;
    
    public static boolean updateByVo(AddressVo vo) {
        if (vo == null) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AddressMapper mapper = ss.getMapper(AddressMapper.class);
            Address address = findById(vo.id);
            if (address == null) {
                return false;
            }
            mapper.updateByVo(vo);
        } catch (Exception ex) {
            return false;
        } finally {
            ss.close();
        }
        return true;
    }


    private static Address findById(int id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AddressMapper mapper = ss.getMapper(AddressMapper.class);
            Address address = mapper.selectById(id);
            return address;
        } finally {
            ss.close();
        }
    }
    
    public static Page<Address> findPageByVo(AddressSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AddressMapper mapper = ss.getMapper(AddressMapper.class);
            int totalCount = mapper.countTotalVo(vo);
            Page<Address> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectListByPage(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }


    public  boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AddressMapper mapper = ss.getMapper(AddressMapper.class);
            if (this.id > 0) {
                mapper.updateById(this);
            } else {
                DateTime dtNow = DateTime.now();
                this.createTime = dtNow.toDate();
                this.updateTime = dtNow.toDate();
                mapper.insert(this);
            }
        } catch (Exception ex) {
            return false;
        } finally {
            ss.close();
        }
        return true;
    }


    public static boolean save(Address address) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AddressMapper mapper = ss.getMapper(AddressMapper.class);
            if (address.id > 0) {
                mapper.updateById(address);
            } else {
                DateTime dtNow = DateTime.now();
                address.createTime = dtNow.toDate();
                address.updateTime = dtNow.toDate();
                mapper.insert(address);
            }
        } catch (Exception ex) {
            return false;
        } finally {
            ss.close();
        }
        return true;
    }
    
    

}
