package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.vo.Page;

import enums.constants.RegexConstants;
import models.mappers.RetailerAddressMapper;
import play.data.validation.Match;
import vos.AddressSearchVo;
import vos.AddressVo;

public class RetailerAddress extends Location {

    @Transient
    public static final String TABLE_NAME = "retailer_address";

    public long id;
    /** 名称 */
    public String name;

    @Match(RegexConstants.PHONE)
    public String phone;

    public int retailerId;
    public Date createTime;
    public Date updateTime;

    public boolean defaultAddress;

    public String IDcard;
    public String gender;

    public static boolean updateByVo(AddressVo vo) {
        if (vo == null) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            RetailerAddress address = findById(vo.id);
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

    public static boolean update(RetailerAddress retailerAddress) {
        if (retailerAddress == null) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            RetailerAddress address = findById(retailerAddress.id);
            if (address == null) {
                return false;
            }
            mapper.update(retailerAddress);
        } finally {
            ss.close();
        }
        return true;
    }

    public static RetailerAddress findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            RetailerAddress address = mapper.selectById(id);
            return address;
        } finally {
            ss.close();
        }
    }

    public static Page<RetailerAddress> findPageByVo(AddressSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            int totalCount = mapper.countTotalVo(vo);
            Page<RetailerAddress> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectListByPage(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

    public static boolean save(RetailerAddress address) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            if (address.id > 0) {
                mapper.update(address);
            } else {
                DateTime dtNow = DateTime.now();
                address.createTime = dtNow.toDate();
                address.updateTime = dtNow.toDate();
                mapper.insert(address);
            }
        } finally {
            ss.close();
        }
        return true;
    }

    public static List<RetailerAddress> findListByRetailerId(int retailerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            return mapper.selectList(retailerId);
        } finally {
            ss.close();
        }
    }

    @Override
    public String toBaseLocationStr() {
        return name + phone + province + city + region + address;
    }

    public static boolean deleteById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            mapper.deleteById(id);
        } finally {
            ss.close();
        }
        return true;
    }

    public static RetailerAddress findByDefaultAddress(int retailerId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            RetailerAddress address = mapper.selectByDefaultAddress(retailerId);
            return address;
        } finally {
            ss.close();
        }
    }

    public static boolean updateDefaultAddress(int retailerId, long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerAddressMapper mapper = ss.getMapper(RetailerAddressMapper.class);
            mapper.setDefaultAddress(retailerId, id);
        } finally {
            ss.close();
        }
        return true;
    }

}
