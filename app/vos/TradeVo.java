package vos;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Transient;

import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.DateUtils;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.Pandora;
import com.aton.vo.Page;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

import enums.TradeStatus;
import models.Item;
import models.Order;
import models.ProductInfo;
import models.Trade;
import models.mappers.OrderMapper;
import models.mappers.TradeMapper;
import play.data.binding.As;
import vos.TradeSearchVo;

/**
 * 交易数据视图
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月29日 下午9:45:58
 */
public class TradeVo implements java.io.Serializable {

    private static final Logger log = LoggerFactory.getLogger(TradeVo.class);

    // 交易编号
    public long id;
    // 交易标题
    public String caption;
    // 交易总金额
    public int totalFee;
    // 优惠金额
    public int discountFee;
    // 货款总金额(交易总金额)单位：分
    public int cargoFee;
    // 实际支付金额(结款实际金额) 单位：分
    public int payment;
    // 优惠内容
    // public Promotion promotion_details;
    // 物流费用 单位：分
    public int shippingFee;
    // 预计发货时间
    public String expectConsignTime;
    // 发货时间
    public String consignTime;
    // 订单列表
    @Transient
    public List<Order> orders;
    // 订单数量
    public int orderNum;
    // 商品数量统计
    public int itemNum;
    // 已有物流
    public int expbeen;
    // 交易状态(中文描述)
    public String statusStr;
    // 交易状态英文描述
    public String status;
    // 交易创建时间
    public String createTime;
    // 交易关闭时间
    public String closeTime;

    public String note;

    // 交易商品预览列表(最多显示6个)
    public List<TradeItemVo> itemVos;

    // 是否显示取消按钮
    public boolean cancelBtn;

    // 是否显示支付按钮
    public boolean payBtn;

    public String statusColor;

    /**
     * 交易转换为视图
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月14日 上午11:47:38
     */
    public static TradeVo valueOfTrade(Trade trade) {
        if (trade == null) {
            return null;
        }
        TradeVo vo = new TradeVo();
        vo.id = trade.id;
        vo.totalFee = trade.totalFee;
        vo.discountFee = trade.discountFee;
        vo.payment = trade.payment;
        vo.shippingFee = trade.shippingFee;
        vo.cargoFee = trade.cargoFee;
        if (trade.expectConsignTime != null) {
            vo.expectConsignTime = new DateTime(trade.expectConsignTime).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
        }
        if (trade.consignTime != null) {
            vo.consignTime = new DateTime(trade.consignTime).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
        }
        vo.statusStr = trade.status.text;
        vo.status = trade.status.code;
        if (trade.createTime != null) {
            vo.createTime = new DateTime(trade.createTime).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
        }
        if (trade.closeTime != null) {
            vo.closeTime = new DateTime(trade.closeTime).toString(DateUtils.YYYY_MM_DD_HH_MM_SS);
        }
        vo.note = trade.note;
        // 状态颜色
        if (trade.status == TradeStatus.TRADE_SETTLEMENT_BEEN) {
            vo.statusColor = "green";
        } else if (trade.status == TradeStatus.TRADE_USER_CANCELLED) {
            vo.statusColor = "red";
        }
        // 状态按钮
        if (trade.status == TradeStatus.TRADE_AUDITING || trade.status == TradeStatus.TRADE_FAIL_AUDITING || trade.status == TradeStatus.TRADE_UNPAIED) {
            vo.cancelBtn = true;
        }
        // if (trade.status != TradeStatus.TRADE_USER_CANCELLED || trade.status !=
        // TradeStatus.TRADE_AUDITING) {
        // vo.cancelBtn = true;
        // }
        // 解析出预览商品
        if (MixHelper.isNotEmpty(trade.orders)) {
            // 订单数统计
            vo.orderNum = trade.orders.size();
            List<ProductInfo> products = Lists.newArrayList();
            for (Order order : trade.orders) {
                //  已有物流统计
                if (!Strings.isNullOrEmpty(order.expNo)) {
                    vo.expbeen++;
                }
                products.add(order.productInfo);
                // 商品数量统计
                vo.itemNum += order.num;
            }
            vo.itemVos = Lists.newArrayList();
            
            for (ProductInfo product : products) {
                if (product == null) {
                    continue;
                }
                
                vo.itemVos.add(new TradeItemVo(product.itemId, product.picUrl, product.title, product.color, product.brandName, product.retailPrice));
            }
        }
        return vo;
    }
}
