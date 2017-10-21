package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.Pandora;
import com.aton.vo.Page;
import com.google.common.primitives.Longs;

import models.mappers.RetailerMapper;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import vos.RetailerSearchVo;

/**
 * 
 * 零售商，其实这里就是淘宝店主
 * 
 * @author Tr0j4n
 * @since v1.0
 * @created 2016年4月11日 下午11:06:13
 */
public class Retailer implements java.io.Serializable {

    private static final long serialVersionUID = -6675391648954670041L;

    private static final Logger log = LoggerFactory.getLogger(User.class);

    @Transient
    public static final String TABLE_NAME = "retailer";

    /** 系统中的主键 */
    public int id;
    /** 名称 */
    public String name;
    /** 网店Url地址 */
    @URL
    public String shopUrl;
    @Required
    @MinSize(1)
    public String channel;
    /** 备注 */
    public String note;

    public Date createTime;
    public Date updateTime;
    

    /**
     * 查找零售商信息by ID
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年4月28日 上午12:25:05
     */
    public static Retailer findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerMapper mapper = ss.getMapper(RetailerMapper.class);
            Retailer retailer = mapper.selectByField("id", id);
            return retailer;
        } finally {
            ss.close();
        }
    }

    /**
     * 保存或更新零售商信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年4月28日 上午12:46:49
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerMapper mapper = ss.getMapper(RetailerMapper.class);
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
     * 保存零售商信息并保存用户信息<br/>
     * 该方法仅供用户自己注册时使用
     *
     * @param user
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月11日 上午1:47:31
     */
    public boolean addWithUser(User user) {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            RetailerMapper mapper = ss.getMapper(RetailerMapper.class);
            DateTime dtNow = DateTime.now();
            this.createTime = dtNow.toDate();
            this.updateTime = dtNow.toDate();

            mapper.insert(this);

            user.userId = id;
            user.role = "RETAILER";
            user.name = "eitak_" + user.phone;

            // 保存用户信息
            user.save();
            ss.commit();
            return true;
        } catch (Exception ex) {
            log.error("Save with user failed", ex);
            ss.rollback();
            return false;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据查询条件查询零售商信息(分页)
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月4日 下午10:35:14
     */
    public static Page<Retailer> findPageByVo(RetailerSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerMapper mapper = ss.getMapper(RetailerMapper.class);
            int totalCount = mapper.countTotalVo(vo);
            Page<Retailer> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectListByPage(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

    /**
     * 删除零售商
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月9日 上午11:37:42
     */
    public static boolean deleteById(int id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RetailerMapper mapper = ss.getMapper(RetailerMapper.class);
            mapper.deleteById(id);
            return true;
        } finally {
            ss.close();
        }
    }
}
