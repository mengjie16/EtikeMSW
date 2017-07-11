package vos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.aton.util.MixHelper;
import com.aton.vo.Page;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import enums.DeliverType;
import enums.ItemStatus;
import models.Brand;
import models.ItemCate;
import play.data.binding.As;
import play.libs.Codec;

/**
 * 商品信息检索视图数据结构
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年4月29日 上午1:48:39
 */
public class ItemSearchVo extends Page {

    /**
     * 商品id
     */
    public long id;
    /** 商品列表页搜索条件 */
    private String kw;
    public double price_start;
    public double price_end;
    // 一件代发
    public boolean dp_use;
    // 批发价格
    public boolean pr_use;
    public List<String> keyWords;
    public List<Long> itemIds;
    /** --------------- */
    /** 类目id */
    public long cateId;
    /** 品牌id */
    public long brandId;
    public String title;
    /** 供应商名称 */
    public String supplierName;
    /** 关于供应商的信息 */
    public String aboutSupplier;
    /** 关于品牌的信息 */
    public String aboutBrand;
    /** 供应商ID **/
    public long supplierId;
    /** 产地。国内，瑞典等 */
    public String origin;
    /** 商家自己的编码，货号 */
    public String outNo;
    /** 是否在架显示 */
    public ItemStatus status;
    /** 发货方式 */
    public DeliverType deliverType;
    /** 商品创建时间 */
    @As("yyyy-MM-dd")
    public Date createTimeStart;
    /** 商品更新时间 */
    public Date createTimeEnd;

    public static ItemSearchVo newInstance() {
        ItemSearchVo vo = new ItemSearchVo();
        return vo;
    }

    /**
     * 获取商品搜索列表页基本查询条件缓存key
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月24日 上午11:34:09
     */
    public String getSearchListBaseKey() {
        StringBuilder key = new StringBuilder();
        key.append(brandId).append("|").append(cateId).append("|");
        key.append(dp_use).append("|").append(pr_use);
        return Codec.encodeBASE64(key.toString());
    }

    /**
     * 
     * 设置vo的关键词
     *
     * @param kwStr
     * @since v1.0
     * @author tr0j4n
     * @created 2016年9月9日 上午3:38:54
     */
    public void kw(String kwStr) {
        // 先两边trim，防止带入两边空格查询会直接报错
        String str = StringUtils.trim(kwStr);
        // 30长度截取
        str = StringUtils.substring(str, 0, 30);
        // 要对关键词做过滤，去掉所有的标点符号，只留下中文，英文，数字，空格
        str = StringUtils.removePattern(str, "[^\\da-zA-Z\u4e00-\u9fa5\\s]");
        kw = str;
        // ----------------接下来是进行关键词解析----------------
        str = StringUtils.lowerCase(str);
        // 空格切开，不管中间是几个空格，作为一个切
        String[] words = StringUtils.splitByWholeSeparator(str, null);
        keyWords = new ArrayList<String>();
        for (String word : words) {
            // 将中英文，数字切碎
            String[] ws = StringUtils.splitByCharacterType(word);
            keyWords.addAll(Arrays.asList(ws));
        }
        MixHelper.print("x");
    }

    /**
     * 
     * 返回kw参数
     *
     * @return
     * @since v1.0
     * @author tr0j4n
     * @created 2016年9月9日 上午3:39:31
     */
    public String kw() {
        return kw;
    }

    /**
     * 获取查询为空的结果描述
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月24日 下午5:15:02
     */
    public String getEmptyResultDesc() {
        // 关键词优先表达
        if (!Strings.isNullOrEmpty(kw)) {
            return kw;
        }
        // 类目表达
        if (cateId > 0) {
            ItemCate ic = ItemCate.findByIdMap(cateId);
            if (ic != null) {
                return "类目:" + ic.name;
            }
        }
        // 品牌表达
        if (brandId > 0) {
            Brand bd = Brand.findBrandWithCacheMap(brandId);
            if (bd != null) {
                return "品牌:" + bd.getMainSubName();
            }
        }
        // 批发价格表达
        if (pr_use) {
            return "批发";
        }
        // 代发价表达
        if (dp_use) {
            return "代发";
        }
        return "";
    }

    //@formatter:off
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(ItemSearchVo.class)
            .add("kw", kw).add("pageNo", pageNo)
            .add("cateId", cateId)
            .add("brandId", brandId).toString();
    }
    //@formatter:on
}