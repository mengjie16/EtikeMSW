package models;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import models.mappers.AlbumMapper;
import models.mappers.BrandMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.thrift.Option;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.DateUtils;
import com.aton.util.JsonUtil;
import com.aton.util.MixHelper;
import com.aton.util.ReflectionUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import vos.ArticleSearchVo;
import vos.ItemSearchVo;
import vos.ItemVo;
import vos.PriceRangeVo;

/**
 * 专辑配置
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年7月1日 下午12:11:52
 */
public class Album implements java.io.Serializable {

    public static final String TABLE_NAME = "album";

    public long id;
    /**
     * 专辑标题
     */
    public String name;

    /**
     * 活动的商品
     */
    public List<AlbumItem> albumItems;
    /**
     * 创建时间
     */
    public Date createTime;

    public Album() {
    }

    public Album(String name) {
        this.name = name;
    }

    /**
     * 将专辑商品价格转换为分
     *
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 上午11:59:32
     */
    public void parseFeePoint() {
        if (MixHelper.isNotEmpty(this.albumItems)) {
            this.albumItems = this.albumItems.stream().map(a -> a.parseFee()).collect(Collectors.toList());
        }
    }

    /**
     * 保存
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午12:26:35
     */
    public boolean save() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            // 价格转换为分
            this.parseFeePoint();
            if (this.id > 0) {
                return mapper.updateById(this) > 0;
            } else {
                this.createTime = DateTime.now().toDate();
                mapper.insert(this);
            }
        } finally {
            ss.close();
        }
        return true;
    }

    /**
     * 更新专辑名称
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午4:15:56
     */
    public boolean updateName() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.updateNameById(this) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 查找列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午12:28:10
     */
    public static List<Album> findList() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.selectList();
        } finally {
            ss.close();
        }
    }

    /**
     * 带商品信息解析的专辑列表
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:37:09
     */
    public static List<Album> findListWithDetial() {
        List<Album> albums = findList();
        if (MixHelper.isNotEmpty(albums)) {
            Lists.transform(albums, new Function<Album, Album>() {

                @Override
                public Album apply(Album input) {
                    if (input != null)
                        input.parseItemDetail();
                    return input;
                }

            });
        }
        return albums;
    }

    /**
     * 删除专辑
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午12:29:05
     */
    public static boolean deleteById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.deleteById(id) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 删除专辑
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 上午12:26:05
     */
    public boolean delete() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.deleteById(this.id) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据ID查询专辑
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午12:30:02
     */
    public static Album findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 检查是否存在相同名称的专辑
     *
     * @param name
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午4:10:24
     */
    public static boolean checkAlbumExsitName(String name) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.countByField("name", name) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 根据专辑ID,商品ID,查找专辑商品
     *
     * @param id
     * @param itemId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月23日 下午4:02:56
     */
    public static AlbumItem findAbumItemByIdWithAlbumId(long id, long itemId) {
        if (id <= 0)
            return null;
        Album album = findById(id);
        if (album != null && MixHelper.isNotEmpty(album.albumItems)) {
            for (AlbumItem item : album.albumItems) {
                if (item.itemId == itemId) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 添加专辑商品
     *
     * @param albumItem
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午1:22:41
     */
    public boolean addAlbumItem(AlbumItem albumItem) {
        if (albumItem == null) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.addAlbumItem(this.id, JsonUtil.toJson(albumItem.parseFee())) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 更新专辑中的商品信息
     *
     * @param index
     * @param albumItem
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午5:10:02
     */
    public boolean updateAlbumItemByIndex(int index, AlbumItem albumItem) {
        if (albumItem == null) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            albumItem = albumItem.parseFee();
            boolean resultItemId = true;
            boolean resultItemPrice = true;
            boolean resultItemPic = true;
            if (albumItem.itemId > 0) {
                resultItemId = mapper.updateAlbumItemIdByIndex(index, albumItem.itemId, this.id) > 0;
            }
            if (albumItem.price > 0) {
                resultItemPrice = mapper.updateAlbumItemPriceByIndex(index, albumItem.price, this.id) > 0;
            }
            if (!Strings.isNullOrEmpty(albumItem.picUrl)) {
                resultItemPic = mapper.updateAlbumItemPicUrlByIndex(index, albumItem.picUrl, this.id) > 0;
            }
            ss.commit();
            if (!resultItemId || !resultItemPrice || !resultItemPic) {
                ss.rollback();
                return false;
            }
        } catch (Exception ex) {
            ss.rollback();
            ex.printStackTrace();
        } finally {
            ss.close();
        }
        return true;
    }

    // /**
    // * 更新指定专辑商品的商品ID
    // *
    // * @param index
    // * @param itemId
    // * @return
    // * @since v1.0
    // * @author Calm
    // * @created 2016年7月20日 下午5:10:47
    // */
    // public boolean updateAlbumItemItemIdByIndex(int index, long itemId) {
    // SqlSession ss = SessionFactory.getSqlSession();
    // try {
    // AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
    // return mapper.updateAlbumItemIdByIndex(index, itemId, this.id) > 0;
    // } finally {
    // ss.close();
    // }
    // }
    //
    // /**
    // * 更新指定专辑商品的价格
    // *
    // * @param index
    // * @param price
    // * @return
    // * @since v1.0
    // * @author Calm
    // * @created 2016年7月20日 下午5:10:36
    // */
    // public boolean updateAlbumItemPriceByIndex(int index, float price) {
    // SqlSession ss = SessionFactory.getSqlSession();
    // try {
    // AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
    // return mapper.updateAlbumItemPriceByIndex(index, price, this.id) > 0;
    // } finally {
    // ss.close();
    // }
    // }
    //
    // /**
    // * 更新指定专辑商品的图片
    // *
    // * @param index
    // * @param picUrl
    // * @return
    // * @since v1.0
    // * @author Calm
    // * @created 2016年7月20日 下午5:00:46
    // */
    // public boolean updateAlbumItemPicUrlByIndex(int index, String picUrl) {
    // SqlSession ss = SessionFactory.getSqlSession();
    // try {
    // AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
    // return mapper.updateAlbumItemPicUrlByIndex(index, picUrl, this.id) > 0;
    // } finally {
    // ss.close();
    // }
    // }

    /**
     * 根据索引删除专辑中商品
     *
     * @param index
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午2:46:23
     */
    public boolean deleteAblumItemByIndex(int index) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.updateAlbumItemByIndex(index, this.id) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 检查专辑商品是否已存在某商品
     *
     * @param itemId
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午4:03:19
     */
    public boolean checkExsitAlbumItemByItemId(long itemId) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AlbumMapper mapper = ss.getMapper(AlbumMapper.class);
            return mapper.selectCountAlbumItemByItemId(itemId, this.id) == 1;
        } finally {
            ss.close();
        }
    }

    /**
     * 检查列表中的专辑商品是否重复
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 下午3:43:19
     */
    public boolean checkExsitAlbumItemInList() {
        if (MixHelper.isEmpty(this.albumItems)) {
            return false;
        }
        for (AlbumItem calbumItem : this.albumItems) {
            int count = 0;
            for (AlbumItem albumItem : this.albumItems) {
                if (calbumItem.itemId == albumItem.itemId) {
                    count++;
                }
            }
            if (count > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析出宝贝基本信息
     *
     * @since v1.0
     * @author Calm
     * @created 2016年7月20日 下午4:40:16
     */
    public void parseItemDetail() {
        if (MixHelper.isNotEmpty(this.albumItems)) {
            this.albumItems = Lists.transform(this.albumItems, new Function<AlbumItem, AlbumItem>() {

                @Override
                public AlbumItem apply(AlbumItem albumItem) {
                    if (albumItem != null) {
                        Item item = Item.findBaseInfoById(albumItem.itemId);
                        if (item != null) {
                            albumItem.title = item.title;
                        }
                    }
                    return albumItem;
                }
            });
        }
    }

    /**
     * 查找专辑商品 pc端渲染
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年10月5日 下午3:32:41
     */
    public List<AlbumItem> parseAlbumItemsForPC() {
        List<AlbumItem> results = Lists.newArrayList();
        Iterator<AlbumItem> albumItems = this.albumItems.iterator();
        // 添加附加信息,过滤库存，商品存在
        while (albumItems.hasNext()) {
            AlbumItem albumItem = albumItems.next();
            if (albumItem == null) {
                continue;
            }
            // 商品信息
            Item item = Item.findBaseInfoById(albumItem.itemId);
            if (item == null) {
                continue;
            }
            // 标题
            albumItem.title = item.title;
            albumItem.brand = Brand.findBrandWithCacheMap(item.brandId);
            albumItem.retailPrice = item.retailPrice;
            // 拼接规格信息
            if (MixHelper.isNotEmpty(item.skus)) {
                List<String> strs = item.skus.stream().map(s -> s.color).collect(Collectors.toList());
                albumItem.sku = com.aton.util.StringUtils.join(strs, ",");
            }
            // 计算利润率
            albumItem.supplyPrice = item.itemLastFee();
            if (item.retailPrice > 0) {
                double profit = new BigDecimal(item.retailPrice - albumItem.supplyPrice)
                    .divide(BigDecimal.valueOf(item.retailPrice), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                albumItem.profit = profit;
            }
            results.add(albumItem);
        }
        return results;
    }

    /**
     * 获取专辑商品并排序
     *
     * @param storeFull
     * @param sort
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年7月21日 下午11:30:11
     */
    public static List<AlbumItem> findAlbumItemsWithSortByAlbumID(long id, boolean storeFull, String sort) {
        List<AlbumItem> results = Lists.newArrayList();
        Album album = Album.findById(id);
        if (album == null || MixHelper.isEmpty(album.albumItems)) {
            return results;
        }
        Iterator<AlbumItem> albumItems = album.albumItems.iterator();
        // 添加附加信息,过滤库存，商品存在
        while (albumItems.hasNext()) {
            AlbumItem albumItem = albumItems.next();
            if (albumItem == null) {
                continue;
            }
            // 商品信息
            Item item = Item.findBaseInfoById(albumItem.itemId);
            if (item == null) {
                continue;
            }
            albumItem.title = item.title;
            // 计算利润率
            albumItem.supplyPrice = item.itemLastFee();
            if (item.retailPrice > 0) {
                double profit = new BigDecimal(item.retailPrice - albumItem.supplyPrice)
                    .divide(BigDecimal.valueOf(item.retailPrice), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                albumItem.profit = profit;
            }
            results.add(albumItem);

        }
        // 列表排序
        if (!Strings.isNullOrEmpty(sort)) {
            String sortStr = StringUtils.substringBefore(sort, "_");
            // asc ,desc
            String type = StringUtils.substringAfter(sort, "_");
            if (Strings.isNullOrEmpty(sortStr)) {
                sortStr = "price";
                type = "a";
            }
            if (Objects.equal(sortStr, "price")) {
                results.sort(Objects.equal(type, "a") ? new Comparator<AlbumItem>() {

                    @Override
                    public int compare(AlbumItem v0, AlbumItem v1) {
                        return v0.sortPrice(v1, "a");
                    }
                } : new Comparator<AlbumItem>() {

                    @Override
                    public int compare(AlbumItem v0, AlbumItem v1) {
                        return v0.sortPrice(v1, "d");
                    }
                });
            } else if (Objects.equal(sortStr, "profit")) {
                results.sort(Objects.equal(type, "a") ? new Comparator<AlbumItem>() {

                    @Override
                    public int compare(AlbumItem v0, AlbumItem v1) {
                        return v0.sortProfit(v1, "a");
                    }
                } : new Comparator<AlbumItem>() {

                    @Override
                    public int compare(AlbumItem v0, AlbumItem v1) {
                        return v0.sortProfit(v1, "d");
                    }
                });
            }
        }

        return results;
    }

}
