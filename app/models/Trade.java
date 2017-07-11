package models;

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
import models.mappers.OrderMapper;
import models.mappers.TradeMapper;
import play.data.binding.As;
import vos.TradeSearchVo;

/**
 * 交易数据实体
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月29日 下午9:45:58
 */
public class Trade implements java.io.Serializable {

    public final static String TABLE_NAME = "trade";

    private static final Logger log = LoggerFactory.getLogger(Trade.class);

    // 订单编号
    public long id;
    // 零售商id
    public int retailerId;
    // 订单标题
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
    public Date expectConsignTime;
    // 发货时间
    public Date consignTime;
    // 订单列表
    @Transient
    public List<Order> orders;
    // 交易状态
    public TradeStatus status;
    // 交易创建时间
    public Date createTime;
    // 交易关闭时间
    public Date closeTime;

    public String note;

    public Trade() {
    }

    public Trade(int retailer) {
        this.retailerId = retailer;
    }

    /**
     * 等待审核状态
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 上午10:22:43
     */
    public Trade tradeAuiting() {
        this.status = TradeStatus.TRADE_AUDITING;
        return this;
    }

    public Trade resetFee() {
        this.cargoFee = 0;
        this.shippingFee = 0;
        this.discountFee = 0;
        this.totalFee = 0;
        this.payment = 0;
        return this;
    }

    /**
     * 计算交易金额
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月14日 下午12:42:41
     */
    public Trade calcFee() {
        // 总价
        this.totalFee = this.cargoFee + this.shippingFee;
        // 实际支付金额（实际总金额－优惠金额）
        this.discountFee = this.totalFee - this.payment;
        return this;
    }

    /**
     * 创建交易
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:03:52
     */
    public Trade createWithOrders(List<Order> orders) {
        if (MixHelper.isEmpty(orders)) {
            return null;
        }
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            // 生成唯一交易id
            this.id = makeAvaliableId();
            this.createTime = DateTime.now().toDate();
            int row = mapper.insert(this);
            int result = 0;
            if (row > 0) {
                List<Long> orderIds = Lists.newArrayList();
                orders = orders.stream().map(o -> o.tradeId(this.id).orderId(orderIds)).collect(Collectors.toList());
                OrderMapper orderMapper = ss.getMapper(OrderMapper.class);
                result = orderMapper.inserts(orders);
            }
            ss.commit();
            if (result > 0) {
                return this;
            }
            ss.rollback();
        } catch (Exception ex) {
            ss.rollback();
            log.error("error={}", ex);
            throw (ex);
        } finally {
            ss.close();
        }
        return null;
    }

    /**
     * 更新交易并更新子订单
     *
     * @param orders
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月29日 下午4:10:07
     */
    public boolean updateWithOrders(List<Order> orders) {
        SqlSession ss = SessionFactory.getSqlSessionWithoutAutoCommit();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            // 生成唯一交易id
            int row = mapper.updateFee(this);
            ss.commit();
            if (row > 0 && MixHelper.isNotEmpty(orders)) {
                Iterator<Order> orderIterator = orders.iterator();
                while (orderIterator.hasNext()) {
                    Order order = orderIterator.next();
                    if (!order.tradeId(this.id).save()) {
                        ss.rollback();
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception ex) {
            ss.rollback();
            log.error("error={}", ex);
            return false;
        } finally {
            ss.close();
        }
    }

    /**
     * 更新交易
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:17:12
     */
    public boolean update() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            return mapper.updateById(this) > 0;
        } finally {
            ss.close();
        }
    }
    
    /**
     * 更新交易金额
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年10月14日 下午2:00:09
     */
    public boolean updateFee() {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            return mapper.updateFee(this) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 用户取消交易
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月17日 下午1:08:03
     */
    public boolean cancelByUser() {
        if (this.status == TradeStatus.TRADE_USER_CANCELLED) {
            return false;
        }
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            return mapper.userCancel(this.id) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 系统或管理员取消交易
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月17日 下午1:08:03
     */
    public boolean cancelBySystem() {
        if (this.status == TradeStatus.TRADE_USER_CANCELLED) {
            return false;
        }

        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            return mapper.sysCancel(this.id) > 0;
        } finally {
            ss.close();
        }
    }

    /**
     * 查找交易记录
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:51:27
     */
    public static Trade findById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            return mapper.selectByField("id", id);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据id查询交易记录，交易中带有子订单数据
     *
     * @param id
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月13日 下午10:48:05
     */
    public static Trade findWithOrdersById(long id) {
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            return mapper.selectWithOrdersById(id);
        } finally {
            ss.close();
        }
    }

    /**
     * 根据条件检索交易数据
     *
     * @param vo
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月12日 下午12:56:01
     */
    public static List<Trade> findListWithOrdersByVo(TradeSearchVo vo) {
         if(!Strings.isNullOrEmpty(vo.phone)){
             User usr = User.findByPhone(vo.phone);
             if(usr!=null){
                 vo.retailerId = usr.userId;
             }
         }
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            TradeMapper mapper = ss.getMapper(TradeMapper.class);
            List<Trade> trades = mapper.selectListWithOrderByVo(vo);
            return trades;
        } finally {
            ss.close();
        }
    }

    /**
     * 生成唯一交易id
     *
     * @return
     * @since v1.0
     * @author Calm
     * @created 2016年9月11日 上午10:37:08
     */
    private static long makeAvaliableId() {
        // 生成10位随机数
        long rand10 = Pandora.newInstance(RandomUtils.nextInt(10, 31), RandomUtils.nextInt(10, 31)).makeId(10);
        DateTime dtNow = DateTime.now();
        long id = Longs.tryParse(dtNow.toString("yyMMdd") + rand10);
        while (true) {
            Trade trade = findById(id);
            if (trade == null) {
                // id可用，不重复
                break;
            }
            id++;
            log.warn("Found duplicate trade id={}, generate again", id);
        }
        return id;
    }

}
