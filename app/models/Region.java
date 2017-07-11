package models;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import models.mappers.RegionMapper;

import org.apache.ibatis.session.SqlSession;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import antlr.StringUtils;
import enums.constants.CacheType;

/**
 * 
 * 行政区域（省、市、区等）
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2015-4-3 下午2:27:33
 */
public class Region implements java.io.Serializable {

    private static final long serialVersionUID = 20151218123655L;

    public static final String TABLE_NAME = "region";
    public int id;
    public String name;

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int parentId;
    // 直辖市简写
    public static final List<String> zx_region = ImmutableList.of("北京", "天津", "上海", "重庆");
    // 自治区简写
    public static final List<String> zzq_region = ImmutableList.of("内蒙古", "广西", "宁夏", "新疆", "西藏");
    // 特别行政区简写
    public static final List<String> tb_region = ImmutableList.of("香港", "澳门");

    /**
     * 区域类型
     * .area区域
     * 1:country/国家;
     * 2:province/省/自治区/直辖市;
     * 3:city/地区(省下面的地级市);
     * 4:district/县/市(县级市)/区;
     * abroad:海外.
     * 比如北京市的area_type = 2,朝阳区是北京市的一个区,所以朝阳区的area_type = 4.
     */
    public int type;
    public String zip;

    /**
     * 
     * 根据父id取得区域数据
     * 
     * @param id
     * @return
     * @since v0.1
     * @author moloch
     * @created 2014-7-28 下午5:37:31
     */
    public static List<Region> findByParentId(int id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RegionMapper rmapper = ss.getMapper(RegionMapper.class);
            return rmapper.selectByParentId(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据父id取得区域数据 in cache
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @modifier Tr0j4n
     * @created 2016年8月10日 上午10:31:34
     */
    public static List<Region> findByParentIdWithCache(int id) {
        String key = CacheType.AREA_DATA.getKey(id);
        // 获取缓存的地区信息
        List<Region> list = CacheUtils.get(key);
        if (MixHelper.isNotEmpty(list)) {
            return list;
        }
        // 缓存信息为空时，查询db存入缓存中(缓存30天)
        list = findByParentId(id);
        if (MixHelper.isNotEmpty(list)) {
            CacheUtils.set(key, list, CacheType.AREA_DATA.expiredTime);
        } else {
            return Lists.newArrayList();
        }
        return list;
    }

    /**
     * 查询省级区域列表(简写)
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月21日 下午2:12:58
     */
    public static List<Region> findLevel2ShortWithCache() {
        String key = CacheType.AREA_SHORT.getKey(1);
        List<Region> list = CacheUtils.get(key);
        if (MixHelper.isNotEmpty(list)) {
            return list;
        }
        list = findByParentId(1);
        list = list.stream().map((r) -> {
            if (!Strings.isNullOrEmpty(r.name)) {
                if (r.name.indexOf("黑龙") != -1 || r.name.indexOf("内蒙") != -1) {
                    r.name = r.name.substring(0, 3);
                } else {
                    r.name = r.name.substring(0, 2);
                }
            }
            return r;
        }).sorted((r1, r2) -> Integer.compare(r1.name.length(), r2.name.length())).collect(Collectors.toList());
        if (MixHelper.isNotEmpty(list)) {
            CacheUtils.set(key, list, CacheType.AREA_SHORT.expiredTime);
        } else {
            return Lists.newArrayList();
        }
        return list;
    }

    /**
     * 查询省级区域列表(简写)的键值对
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月22日 下午2:32:51
     */
    public static Map<Integer, String> findLevel2ShortIdvsName() {
        List<Region> regions = findLevel2ShortWithCache();
        Map<Integer, String> mis = regions.stream().filter(r -> r != null).collect(Collectors.toMap(Region::getId, Region::getName));
        if (mis == null) {
            return Maps.newHashMap();
        }
        return mis;
    }

    /**
     * 查找最高节点地区信息(中国,阿富汗)
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年4月28日 上午12:15:39
     */
    public static List<Region> findRoot() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RegionMapper rmapper = ss.getMapper(RegionMapper.class);
            return rmapper.selectByParentId(0);
        } finally {
            ss.close();
        }
    }

    /**
     * 查找所有国家
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月23日 下午1:29:05
     */
    public static List<Region> findCountrysByCache() {

        String key = CacheType.AREA_COUNTRY_DATA.getKey();
        List<Region> list = CacheUtils.get(key);
        if (MixHelper.isNotEmpty(list)) {
            return list;
        }
        list = findRoot();
        if (MixHelper.isNotEmpty(list)) {
            CacheUtils.set(key, list, CacheType.AREA_COUNTRY_DATA.expiredTime);
        } else {
            return Lists.newArrayList();
        }
        return list;
    }

    /**
     * 模糊查询国家或省份信息
     *
     * @param keyWord
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月30日 下午3:26:13
     */
    public static List<Region> findRootByKeyWord(String keyWord) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            RegionMapper rmapper = ss.getMapper(RegionMapper.class);
            return rmapper.selectRootBykeyWord(keyWord);
        } finally {
            ss.close();
        }
    }

    /**
     * 批量保存
     *
     * @param list
     * @since v1.0
     * @author Calm
     * @created 2016年4月28日 上午12:15:14
     */
    public static void batchSave(List<Region> list) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            RegionMapper mapper = ss.getMapper(RegionMapper.class);
            for (Region region : list) {
                mapper.insert(region);
            }
            ss.commit();
        } finally {
            ss.close();
        }
    }
}
