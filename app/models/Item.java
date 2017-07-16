package models;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.CacheUtils;
import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import enums.DeliverType;
import enums.ItemStatus;
import enums.constants.CacheType;
import models.mappers.ItemMapper;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import utils.ExchangeRateUtil;
import vos.BrandResult;
import vos.CateResult;
import vos.FreightSearchVo;
import vos.ItemPropertieVo;
import vos.ItemSearchResult;
import vos.ItemSearchVo;
import vos.ItemVo;
import vos.PriceRangeVo;

/**
 * 
 * 商品信息
 * 
 * @author tr0j4n
 * @since v1.0
 * @created 2016年4月11日 下午8:13:55
 */
public class Item implements Serializable {

    public static final String TABLE_NAME = "item";
    private static final Logger log = LoggerFactory.getLogger(Item.class);
    /** 自增主键 */
    public long id;
    @Required
    @MaxSize(64)
    @Match("^[a-zA-Z0-9_\u4e00-\u9fa5(\\S|\\s)*]+$")
    /** 产品的标题 */
    public String title;
    /** 产品的id，SPU */
    public long productId;
    /** 供应商id */
    public int supplierId;
    /** 供应商名称 */
    public String supplierName;
    @Required
    /** 类目id */
    public long cateId;
    /** 品牌id */
    public long brandId;
    @Required
    /** 商品主图 */
    public String picUrl;
    /** 商品数量 */
    public int num;
    @Required
    /** 产地。国内，瑞典等 */
    public String origin;
    @Required
    @MaxSize(64)
    /** 商家自己的编码，货号 */
    public String outNo;
    /** 发货方式 */
    @Required
    public DeliverType deliverType;
    /** 计量单位，如瓶、罐、袋 */
    public String unit;
    /** 建议零售价 */
    @Min(0)
    public int retailPrice;
    /** 供货价 */
    public int supplyPrice;
    /** 净重量 单位：千克 */
    public double netWeight;
    /** 毛重量 单位：千克 */
    public double grossWeight;
    /** 品质系数（100-10000） */
    public int quality;
    /** 价格区间、页面展示位拿货价格，摆放时按range从低到高排列 */
    @CheckWith(value = PriceRangeCheck.class, message = "价格区间不正确")
    public List<PriceRangeVo> priceRanges;

    public List<ItemSku> skus;

    /** 总销量 */
    public int soldQuantity;
    /** 初始销量 */
    public int initialQuantity;
    /** 商品的各项SKU属性，风格，承重类似 */
    public List<ItemPropertieVo> properties;
    /** 分销价，一件代发价 */
    public int distPrice;
    /** 详情，最大25000个字节 */
    @MaxSize(25000)
    public String detail;
    /** 手机详情，最大25000字节 */
    @MaxSize(25000)
    public String mobileDetail;
    /**
     * 运费模版
     */
    public long freightTemp;

    /** 发货地址列表 */
    @Transient
    public List<ItemLocation> sendGoodLocations;

    // -----------管理员才有的字段
    /** 商品参考链接 */
    public List<KeyValue> referUrls;
    /** 商品备注 */
    public String note;
    // ---------------------

    @Required
    /** 是否在架显示 */
    public ItemStatus status;
    /** 代销价格是否应用 */
    public boolean distPriceUse;
    /** 批发价格区间是否应用 */
    public boolean priceRangeUse;
    /** 商品创建时间 */
    public Date createTime;
    /** 商品更新时间 */
    public Date updateTime;
    
    @Transient
    public int cny2eur;
    
   
    public int getCny2eur() {
        return cny2eur;
    }

    
    public void setCny2eur(int cny2eur) {
        
        this.cny2eur = (int)(retailPrice * ExchangeRateUtil.getExchangeRate()/100) ;
    }

    /**
     * 价格区间校验
     * 
     * @author Calm
     * @since v1.0
     * @created 2016年6月1日 下午3:25:23
     */
    public static class PriceRangeCheck extends Check {

        @Override
        public boolean isSatisfied(Object objClass, Object field) {
            List<PriceRangeVo> priceRanges = (List<PriceRangeVo>) field;
            if (MixHelper.isEmpty(priceRanges)) {
                return true;
            }
            // 检查重复
            for (PriceRangeVo vo : priceRanges) {
                if (vo.count(priceRanges) > 1) {
                    return false;
                }
            }
            // 排序
            priceRanges.sort(new Comparator<PriceRangeVo>() {

                @Override
                public int compare(PriceRangeVo v0, PriceRangeVo v1) {
                    if (v0.range > v1.range) {
                        return 1;
                    }
                    if (v0.range == v1.range) {
                        return 0;
                    }
                    return -1;
                }
            });
            return true;
        }
    }

    /**
     * 根据id查找商品
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年4月29日 上午1:41:58
     */
    public static Item findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 查找宝贝商品详情
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月14日 上午1:45:51
     */
    public static Item itemDetailById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            Item item = mapper.selectById(id);
            if (item != null) {
                item.sendGoodLocations = ItemLocation.findItemLactionsByItemId(item.id);
            }
            return item;
        } finally {
            ss.close();
        }
    }

    /**
     * 返回宝贝详情视图缓存
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月28日 下午6:59:09
     */
    public static ItemVo itemDetailCacheById(long id) {
        String key = CacheType.ITEM_DETAIL_DATA.getKey(id);
        ItemVo vo = CacheUtils.get(key);
        if (vo == null) {
            Item item = itemDetailById(id);
            vo = ItemVo.valueOfItem(item, false);
            CacheUtils.set(key, vo, CacheType.ITEM_DETAIL_DATA.expiredTime);
        }
        return vo;
    }

    /**
     * 清除宝贝详情缓存
     *
     * @param id
     * @since v1.0
     * @author Calm
     * @created 2016年7月28日 下午7:00:22
     */
    public static void clearItemDetailCache(long id) {
        String key = CacheType.ITEM_DETAIL_DATA.getKey(id);
        CacheUtils.remove(key);
        clearListCache();
    }

    /**
     * 清除所有上架宝贝列表缓存
     *
     * @since v1.0
     * @author Calm
     * @created 2016年8月24日 下午6:27:23
     */
    public static void clearListCache() {
        String key = CacheType.ITEM_ALL_DATA.getKey();
        CacheUtils.remove(key);
    }

    /**
     * 从缓存中获取所有上架宝贝
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月24日 下午6:30:15
     */
    public static List<Item> allItemListOnlineCache() {
        String key = CacheType.ITEM_ALL_DATA.getKey();
        List<Item> items = CacheUtils.get(key);
        if (items == null) {
            items = allItemListOnline();
            if (items != null) {
                CacheUtils.set(key, items, CacheType.ITEM_ALL_DATA.expiredTime);
            }
        }
        return items;
    }

    /**
     * 获取所有上架宝贝
     * TODO Comment.
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月24日 下午6:33:53
     */
    public static List<Item> allItemListOnline() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.selectListOnline();
        } finally {
            ss.close();
        }
    }

    /**
     * 根据属性查找商品信息(一条)
     *
     * @param fieldName
     * @param value
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年4月29日 上午1:43:28
     */
    public static Item findByField(String fieldName, Object value) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.selectByField(fieldName, value);
        } finally {
            ss.close();
        }
    }

    /**
     * 更新商品信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年4月29日 上午1:45:20
     */
    public boolean updateById() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            mapper.updateById(this);
            // 清除缓存
            clearItemDetailCache(this.id);
        } catch (Exception ex) {
            return false;
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 删除商品信息by id
     *
     * @since v1.0
     * @author Calm
     * @created 2016年4月29日 上午1:45:59
     */
    public static void deleteById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            mapper.deleteById(id);
            clearItemDetailCache(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 保存商品信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年4月29日 上午1:47:33
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            if (this.id > 0) {
                mapper.updateById(this);
                clearItemDetailCache(this.id);
                return true;
            }
            this.createTime = DateTime.now().toDate();
            this.updateTime = this.createTime;
            mapper.insert(this);
            clearItemDetailCache(this.id);
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 保存商品信息并保存地址信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午4:20:40
     */
    public boolean saveWithItemLocation() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            if (this.id > 0) {
                mapper.updateById(this);
                clearItemDetailCache(this.id);
            } else {
                this.createTime = DateTime.now().toDate();
                this.updateTime = this.createTime;
                mapper.insert(this);
                clearItemDetailCache(this.id);
            }
            // 保存地址信息
            if (!this.saveLocation()) {
                log.error("{}", "save item location error");
            }
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 保存商品地址信息
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月2日 下午4:24:44
     */
    public boolean saveLocation() {
        if (MixHelper.isEmpty(sendGoodLocations)) {
            return false;
        }
        long itemId = this.id;
        List<ItemLocation> listParams = Lists.transform(sendGoodLocations, new Function<ItemLocation, ItemLocation>() {

            @Override
            public ItemLocation apply(ItemLocation input) {
                input.itemId = itemId;
                return input;
            }
        });
        return ItemLocation.saveBatch(listParams, itemId);
    }

    /**
     * 
     * 更新一个商品的status
     *
     * @param id
     * @param status
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年11月10日 下午5:48:16
     */
    public static boolean updateStatus(long id, ItemStatus status) {
        List<Long> ids = Lists.newArrayList(id);
        return batchUpdateStatus(ids, status);
    }

    /**
     * 批量更新商品状态
     *
     * @param ids
     * @param status
     * @since v1.0
     * @author Calm
     * @created 2016年6月15日 下午6:16:20
     */
    public static boolean batchUpdateStatus(List<Long> ids, ItemStatus status) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            mapper.updateStatusByIds(ids, status);
            ss.commit();
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 批量保存
     *
     * @param list
     * @since v1.0
     * @author Calm
     * @created 2016年4月28日 上午12:15:14
     */
    public static void batchSave(List<Item> list) {
        SqlSession ss = SessionFactory.getSqlSessionForBatch();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            for (Item item : list) {
                mapper.insert(item);
            }
            ss.commit();
        } finally {
            ss.close();
        }
    }

    /**
     * 根据查询条件查询商品信息(分页)
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月4日 下午10:35:14
     */
    public static Page<Item> findItemPage(ItemSearchVo vo) {
        // 处理特殊
        if (vo != null && Strings.isNullOrEmpty(vo.aboutBrand)) {
            // 转换为大写
            vo.aboutBrand = StringUtils.lowerCase(vo.aboutBrand);
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            int totalCount = mapper.countTotalVo(vo);
            Page<Item> page = Page.newInstance(vo.pageNo, vo.pageSize, totalCount);
            if (totalCount > 0) {
                page.items = mapper.selectListByPage(vo);
            }
            return page;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据查询条件查找商品列表
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年5月4日 下午10:39:01
     */
    public static List<Item> findItemList(ItemSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            List<Item> itemList = mapper.selectList(vo);
            return itemList;
        } finally {
            ss.close();
        }
    }

    /**
     * 查找所有商品(没有查询条件)
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 上午11:45:29
     */
    public static List<Item> findAllItem() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            List<Item> itemList = mapper.selectListAll();
            return itemList;
        } finally {
            ss.close();
        }
    }

    /**
     * 查找当前宝贝，关联的相关产品
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年6月29日 上午1:25:58
     */
    public static List<Item> findAlisaItemBySupplierId(int supplierId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            List<Item> itemList = mapper.selectListBySupplier(supplierId);
            return itemList;
        } finally {
            ss.close();
        }
    }

    /**
     * 
     * 格式化打印，便于调试
     * @see java.lang.Object#toString()
     * @since  v1.0
     * @author tr0j4n
     * @created 2015-4-20 上午7:59:53
     * @formatter:off
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }
    //@formatter:on

    /**
     * 校验宝贝地址是否重复
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月18日 下午10:01:44
     */
    public boolean checkItemLocationRepeat() {
        if (MixHelper.isNotEmpty(this.sendGoodLocations)) {
            for (ItemLocation location : sendGoodLocations) {
                String locationStr = location.toBaseLocationStr();
                int count = 0;
                for (ItemLocation loca : sendGoodLocations) {
                    if (Objects.equal(locationStr, loca.toBaseLocationStr())) {
                        count++;
                    }
                }
                if (count > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 检查是否已经有商品应用品牌
     *
     * @param brandId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月18日 下午10:37:16
     */
    public static int checkExsitBrandUse(long brandId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            int count = mapper.countByBrandId(brandId);
            return count;
        } finally {
            ss.close();
        }
    }

    /**
     * 检查是否已经有商品应用相关运费模版
     *
     * @param freightTempkey
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月18日 下午10:37:16
     */
    public static int checkExsitFreightTempUse(long freightTempkey) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            int count = mapper.countByFreigthTempKey(freightTempkey + "");
            return count;
        } finally {
            ss.close();
        }
    }

    /**
     * 查找宝贝基本信息
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午4:33:47
     */
    public static Item findBaseInfoById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.selectBaseById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据商品ID查询供应商ID
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月23日 下午12:26:37
     */
    public static int findSupplierIdById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.selectSupplierIdById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据状态统计供应商商品信息
     *
     * @param id
     * @param status
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月3日 下午3:57:07
     */
    public static int countBysupplierIdWithStatus(int id, ItemStatus status) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.countBySupplierIdWithStatus(id, status);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据条件查询商品运费模版配置
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月28日 下午1:58:19
     */
    public static List<Long> findFreightTempByVo(FreightSearchVo vo) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            return mapper.selectFreightTempByVo(vo);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据关键字查询商品信息（商品名称，品牌名称）
     *
     * @param ItemSearchVo vo
     * @return
     * @since v1.0
     * @author Calm
     * @modifier Tr0j4n
     * @created 2016年8月15日 下午10:20:43
     */
    public static ItemSearchResult findByVoPage(ItemSearchVo vo) {
        if (vo == null) {
            return ItemSearchResult.newInstance(0, 0, 0);
        }
        ItemSearchResult results = ItemSearchResult.newInstance(vo.pageNo, vo.pageSize, 0);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            // 匹配的商品集合
            List<Item> items = Lists.newArrayList();
            // 匹配的商品id集合
            List<Long> itemIds = Lists.newArrayList();
            // 查看缓存中是否存在 kw->totalBaseItemIds 缓存
            String cachekey = CacheType.ITEM_SEARCH_LIST_DATA.getKey(vo.getSearchListBaseKey());
            itemIds = CacheUtils.get(cachekey);
            if (MixHelper.isEmpty(itemIds)) {
                // 匹配基本条件的所有商品id集合
                itemIds = mapper.selectListFilterByVo(vo);
                CacheUtils.set(cachekey, itemIds, CacheType.ITEM_SEARCH_LIST_DATA.expiredTime);
            }
            // 获取关键字组合列表
            List<Long> bids = Lists.newArrayList();
            // 当没有品牌id过滤条件，进行品牌关键字匹配查询
            if (vo.brandId <= 0 && vo.keyWords.size() > 0) {
                List<Brand> brands = mapper.selectbIdsByKeyWordInIds(vo.keyWords);
                // 查询到品牌过滤后集合，删减关键字
                if (MixHelper.isNotEmpty(brands)) {
                    results.is_brand = brands.size() == 1;
                    // 移除已经查询过的品牌关键字
                    brands.forEach((b) -> {
                        vo.keyWords.remove(b.name);
                        bids.add(b.id);
                    });
                }
            }
            // 根据品牌信息过滤商品关键字，如果没有品牌过滤条件，将以基本过滤条件集合
            items = mapper.selectIdsInAllIdsOrBids(vo.keyWords, bids, itemIds);
            results.totalCount = items != null ? items.size() : 0;
            if (items != null) {
                items = items.stream().filter(i -> i != null).collect(Collectors.toList());
            }
            // 分页后的商品集合
            if (results.totalCount > 0) {
                vo.itemIds = items.stream().map(i -> i.id).collect(Collectors.toList());
                List<Item> fyItems = mapper.selectListVoByIds(vo);
                // 转换视图
                List<ItemVo> vos = fyItems.stream().map(i -> ItemVo.valueOfItem(i, true)).collect(Collectors.toList());
                             
                results.items = vos;
            }
            // 品牌分组 --------[在未分页前数据集中归类,并且当前有查询结果]
            if (!results.is_brand && results.totalCount > 0) {
                Map<Long, List<String>> brandMap = items.stream().map(v -> Brand.findBrandWithCacheMap(v.brandId))
                    .filter(b -> b != null).collect(Collectors.groupingBy(Brand::getId,
                        Collectors.mapping(Brand::getMainSubName, Collectors.toList())));
                if (brandMap != null) {
                    results.brands = Lists.newArrayList();
                    Iterator it = brandMap.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry<Long, List<String>> br = (Map.Entry) it.next();
                        int count = br.getValue() == null ? 0 : br.getValue().size();
                        String name = count > 0 ? br.getValue().get(0) : "未知";
                        results.brands.add(new BrandResult(count, br.getKey(), name));
                    }
                }
            }

            // 类目分组 --------[在未分页前数据集中归类，如果当前总结果集总没有类目信息，那么返回所有类目]
            Map<Long, List<String>> cateMap = Maps.newConcurrentMap();
            if (results.totalCount > 0) { // 有结果集
                cateMap = items.stream().map(i -> ItemCate.findByIdMap(i.cateId)).filter(c -> c != null).collect(
                    Collectors.groupingBy(ItemCate::getId, Collectors.mapping(ItemCate::getName, Collectors.toList())));
            } else {// 没有结果集
                results.result_empty_desc = vo.getEmptyResultDesc();
                List<Item> allItems = allItemListOnlineCache();
                if (MixHelper.isNotEmpty(allItems)) {
                    cateMap = allItems.stream().map(i -> ItemCate.findByIdMap(i.cateId)).filter(c -> c != null)
                        .collect(Collectors.groupingBy(ItemCate::getId,
                            Collectors.mapping(ItemCate::getName, Collectors.toList())));
                }
            }
            if (cateMap != null) {
                results.cates = Lists.newArrayList();
                Iterator it = cateMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Long, List<String>> cate = (Map.Entry) it.next();
                    int count = cate.getValue() == null ? 0 : cate.getValue().size();
                    String name = count > 0 ? cate.getValue().get(0) : "未知";
                    results.cates.add(new CateResult(count, cate.getKey(), name));
                }
            }
        } finally {
            ss.close();
        }
        return results;
    }
     


    /**
     * 计算单个商品运费
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 下午2:43:31
     */
    public Map<Integer, Integer> calculateFreightTempFee() {
        return FreightTemp.calculateItemFee(freightTemp, grossWeight, 1);
    }

    /**
     * 计算商品运费
     *
     * @param count 商品数量
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 下午2:43:31
     */
    public Map<Integer, Integer> calculateFreightTempFee(int count) {
        return FreightTemp.calculateItemFee(freightTemp, grossWeight, count);
    }

    /**
     * 根据商品数量获取商品订单价格(最终价格)
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 下午2:49:45
     */
    public int itemLastFee(int num) {
        // 暂时不按照数量确定价格，按照批发最小价格计算
        num = num > 0 ? num : 1;
        int price = 0;
        // 查看是否有经销价格,并且已应用
        if (this.priceRangeUse && MixHelper.isNotEmpty(this.priceRanges)) {
            price = this.priceRanges.stream().map(p -> p.price).min((r1, r2) -> {
                return Integer.compare(r1, r2);
            }).get();
            // for(int i = 0;i<this.priceRanges.size();i++){
            // // 最后一个了
            // if ((i+1) == this.priceRanges.size()) {
            // price = priceRanges.get(i).price;
            // break;
            // }
            // if (num >= priceRanges.get(i).range && num < priceRanges.get(i+1).range) {
            // price = priceRanges.get(i).price;
            // break;
            // }
            // }
        }
        // 没有经销价格，查看代销价
        if (price == 0 && this.distPriceUse) {
            price = this.distPrice;
        }
        // // 最后如果还没有价格，根据供货价(专辑商品有供货价)
        // if (price == 0) {
        // price = this.supplyPrice * num;
        // }
        return price;
    }

    /**
     * 商品当前对外出售价格
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年10月8日 下午6:53:58
     */
    public int itemLastFee() {
        int price = 0;
        // 查看是否有经销价格,并且已应用
        if (this.priceRangeUse && MixHelper.isNotEmpty(this.priceRanges)) {
            price = this.priceRanges.stream().map(p -> p.price).min((r1, r2) -> {
                return Integer.compare(r1, r2);
            }).get();
        }
        // 没有经销价格，查看代销价
        if (price == 0 && this.distPriceUse) {
            price = this.distPrice;
        }
        return price;
    }

    public static ItemSearchResult selectListAllByCreateTime() {
    	
        ItemSearchResult results = ItemSearchResult.newInstance(0, 0, 0);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            // 匹配的商品集合
            List<Item> items = Lists.newArrayList();
        
            items = mapper.selectListAllByCreateTime();
            results.totalCount = items != null ? items.size() : 0;
            if (items != null) {
                items = items.stream().filter(i -> i != null).collect(Collectors.toList());
            }
            results.items = items;
        } finally {
            ss.close();
        }
        return results;
    }

	public static ItemSearchResult selectListAllRandom() {
		ItemSearchResult results = ItemSearchResult.newInstance(0, 0, 0);
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            ItemMapper mapper = ss.getMapper(ItemMapper.class);
            // 匹配的商品集合
            List<Item> items = Lists.newArrayList();
        
            items = mapper.selectListAllRandom();
            results.totalCount = items != null ? items.size() : 0;
            if (items != null) {
                items = items.stream().filter(i -> i != null).collect(Collectors.toList());
            }
            results.items = items;
        } finally {
            ss.close();
        }
        return results;
	}
}
