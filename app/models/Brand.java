package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import models.mappers.BrandMapper;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.data.validation.URL;
import vos.BrandSearchVo;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Strings;

import enums.constants.CacheType;

/**
 * 
 * 品牌实体
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年5月24日 下午7:18:14
 */
public class Brand implements java.io.Serializable {

    public static final String TABLE_NAME = "brand";

    private static final Logger log = LoggerFactory.getLogger(Brand.class);

    /** 系统分配自增主键 */
    public long id;

    public long getId() {
        return this.id;
    }

    /** 主名字，比如爱他美 */
    @Required
    @MinSize(2)
    @MaxSize(64)
    @Match("^[a-zA-Z0-9\u4e00-\u9fa5_/&-]+$")
    public String name;

    public String getName() {
        return this.name;
    }

    public Brand getCurrent() {
        return this;
    }

    public String getMainSubName() {
        if (!Strings.isNullOrEmpty(this.secondaryName)) {
            return this.name + "/" + this.secondaryName;
        }
        return this.name;
    }

    /** 从名字，可能是Aptamil，等真实的名字，但是不常被人所知 */
    @Required
    @MinSize(2)
    @MaxSize(64)
    @Match("^[a-zA-Z0-9\u4e00-\u9fa5_/&-]+$")
    public String secondaryName;
    /** 图片，规格94x26 */
    @Required
    @MaxSize(256)
    @URL
    public String picUrl;
    /** 品牌最根源的公司 */
    @Required
    @MinSize(5)
    @MaxSize(30)
    @Match("^[a-zA-Z0-9\u4e00-\u9fa5]+$")
    public String company;

    /** 和平台对接的，代表这个品牌的，有授权能力的人或者组织 */
    @Required
    @MinSize(5)
    @MaxSize(30)
    @Match("^[a-zA-Z0-9\u4e00-\u9fa5]+$")
    public String holder;
    /** 备注 */
    @Required
    @MinSize(5)
    @MaxSize(80)
    // @Match("^[a-zA-Z0-9\u4e00-\u9fa5_/&-]+$")
    public String note;
    /* 描述 */
    @Required
    @MinSize(5)
    @MaxSize(80)
    public String descption;

    /**
     * 保存品牌信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午4:58:06
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            if (this.id > 0) {
                mapper.updateById(this);
            } else {
                mapper.insert(this);
            }
            clearListCache();
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 品牌名称及从名称的关键字查询
     *
     * @param keyWord
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午4:59:38
     */
    public static List<Brand> findBykeyWord(String keyWord) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            return mapper.selectByKeyWord(keyWord);
        } finally {
            ss.close();
        }
    }

    /**
     * 分页查询品牌信息
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午6:47:37
     */
    public static Page<Brand> findBrandPage(BrandSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            int totalCount = mapper.countByVo(vo);
            Page<Brand> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectByVo(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据ID删除品牌
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午5:56:23
     */
    public static boolean delBrandById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            boolean result = mapper.deleteById(id) > 0;
            if (result) {
                // 清除列表缓存
                clearListCache();
            }
            return result;
        } finally {
            ss.close();
        }
    }

    /**
     * 删除品牌
     *
     * @param id
     * @param clearList
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月28日 下午6:42:54
     */
    public static boolean delBrandById(long id, boolean clearList) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            boolean result = mapper.deleteById(id) > 0;
            if (result) {
                // 清除列表缓存
                clearListCache();
            }
            return result;
        } finally {
            ss.close();
        }
    }


    /**
     * 根据ID查询品牌信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午6:04:05
     */
    public static Brand findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 获取品牌列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月16日 下午3:12:58
     */
    public static List<Brand> findAllList() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            BrandMapper mapper = ss.getMapper(BrandMapper.class);
            return mapper.selectAll();
        } finally {
            ss.close();
        }
    }

    /**
     *  从缓存中获取品牌列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月28日 下午6:30:49
     */
    public static List<Brand> findListByCache() {
        String key_list = CacheType.BRAND_LIST.getKey();
        List<Brand> brands = CacheUtils.get(key_list);
        if (MixHelper.isEmpty(brands)) {
            brands = findAllList();
            CacheUtils.set(key_list, brands, CacheType.BRAND_LIST.expiredTime);
        }
        return brands;

    }

    /**
     * 清除品牌列表缓存
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月28日 下午6:40:20
     */
    public static void clearListCache() {
        String key_list = CacheType.BRAND_LIST.getKey();
        String key_map = CacheType.BRAND_MAP_DATA.getKey();
        // 清除缓存的品牌列表
        CacheUtils.remove(key_list);
        CacheUtils.remove(key_map);
    }

    /**
     * 在缓存Map中获取品牌信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月23日 上午11:24:09
     */
    public static Brand findBrandWithCacheMap(long id) {
        String key_map = CacheType.BRAND_MAP_DATA.getKey();
        Map<Long, Brand> brandMap = CacheUtils.get(key_map);
        if (MixHelper.isEmpty(brandMap)) {
            List<Brand> brands = findListByCache();
            if (MixHelper.isEmpty(brands)) {
                return null;
            }
            brandMap = brands.parallelStream().collect(Collectors.toMap(Brand::getId, Brand::getCurrent));
            CacheUtils.set(key_map, brandMap, CacheType.BRAND_LIST.expiredTime);
        }
        return brandMap == null ? null : brandMap.get(id);
    }
}
