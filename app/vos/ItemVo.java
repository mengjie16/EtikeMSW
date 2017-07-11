package vos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Transient;
import javax.validation.constraints.Min;

import models.Brand;
import models.Item;
import models.ItemCate;
import models.ItemLocation;
import models.ItemSku;
import models.KeyValue;
import models.Item.PriceRangeCheck;
import play.data.binding.As;
import play.data.validation.CheckWith;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import utils.SearchEndDateBinder;

import com.aton.util.MixHelper;
import com.aton.util.StringUtils;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import enums.DeliverType;
import enums.ItemStatus;

/**
 * 商品信息 vo
 * 
 * @author sun
 *
 */
public class ItemVo implements java.io.Serializable {

    public long id;
    /* 品牌信息 */
    public Brand brand;
    /* 分类信息 */
    public ItemCate cate;
    /* 购物车最终价格 */
    public int cartPrice;
    /* 商品规格 */
    public ItemSku sku;
    /* 购物车数量(购物车功能会用到) */
    public int cartCount;
    /**
     * 类目排序显示
     */
    List<ItemCate> cates;
    // 当前商品运费模版数据
    Map<Integer, Integer> itemFreightTemp;
    /** 供货价 */
    public int supplyPrice;
    public double d_supplyPrice;

    /** 净重量 单位：千克 */
    public double netWeight;
    /** 毛重量 单位：千克 */
    public double grossWeight;
    /** 品质系数（100-10000） */
    public int quality;

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

    public double d_retailPrice;

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

    public double d_distPrice;

    /** 详情，最大25000个字节 */
    public String detail;
    /** 手机详情，最大25000字节 */
    public String mobileDetail;
    /** 运费模版 */
    public long freightTemp;
    /** 发货地址列表 */
    public List<ItemLocation> sendGoodLocations;
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

    /** 商品参考链接 */
    public List<KeyValue> referUrls;
    /** 商品备注 */
    public String note;

    /**
     * 转换商品视图至实体
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年10月8日 下午12:28:16
     */
    public Item parseToItem() {
        Item item = new Item();
        item.id = this.id;
        item.title = this.title;
        item.productId = this.productId;
        item.cateId = this.cateId;
        item.brandId = this.brandId;
        item.picUrl = this.picUrl;
        item.num = this.num;
        item.origin = this.origin;
        item.outNo = this.outNo;
        item.deliverType = this.deliverType;
        item.unit = this.unit;
        item.skus = this.skus;
        item.soldQuantity = this.soldQuantity;
        item.initialQuantity = this.initialQuantity;
        item.detail = this.detail;
        item.mobileDetail = this.mobileDetail;
        item.freightTemp = this.freightTemp;
        item.sendGoodLocations = this.sendGoodLocations;
        item.referUrls = this.referUrls;
        item.note = this.note;
        item.status = this.status;
        item.distPriceUse = this.distPriceUse;
        item.priceRangeUse = this.priceRangeUse;
        item.properties = this.properties;
        item.netWeight = this.netWeight;
        item.grossWeight = this.grossWeight;
        item.quality = this.quality;
        // 价格处理(小数保留2位，并转换为分)
        item.retailPrice = new BigDecimal(this.d_retailPrice).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        item.supplyPrice = new BigDecimal(this.d_supplyPrice).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        item.distPrice = new BigDecimal(this.d_distPrice).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        if (MixHelper.isNotEmpty(this.priceRanges)) {
            item.priceRanges = this.priceRanges.stream().map(p -> p.parseToPoint()).collect(Collectors.toList());
        }
        return item;
    }

    /**
     * copy 属性值
     * 
     * @param item
     */
    public static ItemVo valueOfBase(long itemId) {
        Item item = Item.findBaseInfoById(itemId);
        if (item == null)
            return null;
        ItemVo vo = new ItemVo();
        vo.id = item.id;
        vo.title = item.title;
        vo.picUrl = item.picUrl;
        vo.brand = Brand.findBrandWithCacheMap(item.brandId);
        vo.retailPrice = item.retailPrice;
        vo.distPrice = item.distPrice;
        vo.cate = ItemCate.findByIdMap(item.cateId);
        return vo;
    }

    /**
     * 转换vo
     *
     * @param item
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 下午5:31:00
     */
    public static ItemVo valueOfItem(Item item, boolean isBase) {
        if (item == null)
            return null;
        ItemVo iv = new ItemVo();
        iv.id = item.id;
        iv.supplierId = item.supplierId;
        iv.title = item.title;
        iv.picUrl = item.picUrl;
        iv.brand = Brand.findBrandWithCacheMap(item.brandId);
        iv.retailPrice = item.retailPrice;
        iv.distPrice = item.distPrice;
        iv.cate = ItemCate.findByIdMap(item.cateId);
        iv.soldQuantity = item.soldQuantity;
        iv.distPriceUse = item.distPriceUse;
        iv.priceRangeUse = item.priceRangeUse;
        iv.updateTime = item.updateTime;
        iv.supplyPrice = item.supplyPrice;
        iv.netWeight = item.netWeight;
        iv.grossWeight = item.grossWeight;
        iv.quality = item.quality;
        iv.note = item.note;
        iv.num = item.num;
        if (!isBase) {
            // 查找类目信息
            iv.cates = ItemCate.findParentsCateWithSort(item.cateId);
            iv.itemFreightTemp = item.calculateFreightTempFee();
            // -------- 高级信息
            iv.productId = item.productId;
            iv.supplierName = item.supplierName;
            iv.origin = item.origin;
            iv.outNo = item.outNo;
            iv.deliverType = item.deliverType;
            iv.unit = item.unit;
            iv.priceRanges = item.priceRanges;
            iv.skus = item.skus;
            iv.initialQuantity = item.initialQuantity;
            iv.properties = item.properties;
            iv.detail = item.detail;
            iv.mobileDetail = item.mobileDetail;
            iv.freightTemp = item.freightTemp;
            iv.sendGoodLocations = item.sendGoodLocations;
            iv.status = item.status;
            iv.createTime = item.createTime;
            /** 商品更新时间 */
            iv.updateTime = item.updateTime;
            iv.referUrls = item.referUrls;
        }
        return iv;
    }

    /**
     * 转换vo
     *
     * @param items
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月8日 下午5:31:19
     */
    public static List<ItemVo> valueOfItemList(List<Item> items) {
        if (MixHelper.isEmpty(items)) {
            return Lists.newArrayList();
        }
        List<ItemVo> ivs = Lists.transform(items, new Function<Item, ItemVo>() {

            @Override
            public ItemVo apply(Item arg0) {
                // TODO Auto-generated method stub
                return ItemVo.valueOfItem(arg0, true);
            }

        });
        return ivs;
    }
}