package models;

import java.util.Date;

import javax.persistence.Transient;

import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.vo.Page;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import enums.ItemStatus;
import enums.SupplierType;
import models.mappers.SupplierMapper;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.MinSize;
import play.data.validation.Required;
import vos.SupplierSearchVo;
import vos.SupplierVo;

/**
 * 
 * 供货商信息
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月11日 下午8:08:35
 */
public class Supplier implements java.io.Serializable {

    @Transient
    public static final String TABLE_NAME = "supplier";

    public int id;
    @Required
    @MinSize(3)
    /** 公司名字 */
    public String name;
    /** 公司主营产品 */
    public String product;
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

    /** 二级域名，通过domain.eitak.com可以访问到供应商的主页 */
    public String domain;
    /** 供应商类型 */
    public SupplierType type;
    /** 备注 */
    public String note;

    public Date createTime;
    public Date updateTime;

    /**
     * 省份检查
     * 
     * @author Calm
     * @since v1.0
     * @created 2016年8月8日 下午1:57:30
     */
    public static class ProvinceCheck extends Check {

        @Override
        public boolean isSatisfied(Object objClass, Object value) {
            String province = (String) value;
            if (!Strings.isNullOrEmpty(province)) {
                return true;
            }
            Supplier currentClass = (Supplier) objClass;
            if (Objects.equal(currentClass.country, "中国") && Strings.isNullOrEmpty(province)) {
                return false;
            }
            return true;
        }
    }

    /**
     * 供应商数据渲染
     */
    @Transient
    public DataPreview dataPreview;

    public class DataPreview {

        // 在线商品数量
        public int onlineItem = 0;
        // 下架商品数量
        public int hideItem = 0;
        // 订单量
        public int order = 0;
        // 成交金额
        public double amount = 0;
    }

    /**
     * 计算供应商渲染信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月3日 下午3:51:47
     */
    public void calcDataPreview() {
        DataPreview dp = new DataPreview();
        dp.onlineItem = Item.countBysupplierIdWithStatus(this.id, ItemStatus.ONLINE);
        dp.hideItem = Item.countBysupplierIdWithStatus(this.id, ItemStatus.HIDE);
        dp.order = 0;
        dp.amount = 0;
        this.dataPreview = dp;
    }

    /**
     * 保存或更新供货商信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月5日 上午12:04:19
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierMapper mapper = ss.getMapper(SupplierMapper.class);
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

    /**
     * 更新供应商
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月9日 上午10:52:47
     */
    public boolean update() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierMapper mapper = ss.getMapper(SupplierMapper.class);
            Supplier supplier = findById(this.id);
            if (supplier == null) {
                return false;
            }
            this.updateTime = DateTime.now().toDate();
            mapper.updateById(this);
        } catch (Exception ex) {
            return false;
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 更新供应商,vo
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月9日 上午10:52:21
     */
    @Deprecated
    public static boolean updateByVo(SupplierVo vo) {
        if (vo == null) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierMapper mapper = ss.getMapper(SupplierMapper.class);
            Supplier supplier = findById(vo.id);
            if (supplier == null) {
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

    /**
     * 根据id查询供货商信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月5日 上午12:02:44
     */
    public static Supplier findById(int id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierMapper mapper = ss.getMapper(SupplierMapper.class);
            Supplier supplier = mapper.selectById(id);
            return supplier;
        } finally {
            ss.close();
        }
    }

    /**
     * 检索供应商信息(分页)
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月5日 上午1:16:21
     */
    public static Page<Supplier> findPageByVo(SupplierSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierMapper mapper = ss.getMapper(SupplierMapper.class);
            int totalCount = mapper.countTotalVo(vo);
            Page<Supplier> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectListByPage(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

    /**
     * 删除供应商
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月9日 上午11:39:45
     */
    public static boolean deleteById(int id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            SupplierMapper mapper = ss.getMapper(SupplierMapper.class);
            mapper.deleteById(id);
            return true;
        } finally {
            ss.close();
        }
    }

    /**
     * 检查是否有效的地址信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月23日 下午4:23:14
     */
    public boolean checkLocation() {
        // 国家信息必要
        if (Strings.isNullOrEmpty(this.country)) {
            return false;
        }
        // 有国家,又不是中国
        if (this.countryId == 1) {
            // 省或市或区为空，信息不完整
            if (Strings.isNullOrEmpty(this.province) || this.provinceId == 0 || Strings.isNullOrEmpty(this.city)
                || Strings.isNullOrEmpty(this.region)) {
                return false;
            }
        }
        return true;
    }
}
