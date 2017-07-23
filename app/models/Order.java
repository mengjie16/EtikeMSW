package models;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aton.db.SessionFactory;
import com.aton.util.Pandora;
import com.google.common.primitives.Longs;

import domain.NormalToStringStyle;
import enums.OrderStatus;
import enums.Payment;
import models.mappers.OrderMapper;

/**
 * 订单实体
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年8月29日 下午10:06:49
 */
public class Order implements java.io.Serializable {

    public final static String TABLE_NAME = "trade_order";

    private static final Logger log = LoggerFactory.getLogger(Order.class);

    /** 主键 */
    public long id;
    /** 所属交易id */
    public long tradeId;
    /** 标题 */
    public String caption;
    /** 零售商id */
    public int retailerId;
    /** 商品数量 */
    public int num;
    /** 商品总额 */
    public int cargoFee;
    /** 邮费 */
    public int shippingFee;
    /** 总金额，单位分 */
    public int totalFee;
    /** 外部订单号 */
    public String outOrderNo;
    /** 快递公司 */
    public String express;
    /** 快递单号 */
    public String expNo;
    /** 订单创建时间 */
    public Date createTime;
    /** 支付时间 */
    public Date payTime;
    /** 发货时间 */
    public Date consignTime;
    /** 备注 */
    public String note;

    /**
     * 买家信息
     */
    public BuyerInfo buyerInfo;
    /**
     * 商品列表
     */
    public ProductInfo productInfo;

    /** 发票抬头 */
    // public String invoiceTitle;
    /** 发票类型 */
    // public String invoiceType;

    /** 状态 */
     public OrderStatus status;

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
        return ToStringBuilder.reflectionToString(this, new NormalToStringStyle());
    }
    
    public Order(){
    }
    
    public Order(int userId){
        this.retailerId = userId;
    }
    
    public Order(int userId,long tradeId){
        this.retailerId = userId;
        this.tradeId = tradeId;
    }
    
    /**
     * 创建或更新订单
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:03:52
     */
    public boolean save(){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
            if(this.id>0){
                return mapper.updateInfo(this) > 0;
            }
            this.createTime = DateTime.now().toDate();
            this.makeId();
            return mapper.insert(this) > 0;
        } finally {
            ss.close();
        }
    }
    
    public static int creates(List<Order> orders){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
           return mapper.inserts(orders);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 删除订单
     *
     * @param id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年10月26日 下午3:49:38
     */
    public static boolean deleteById(long id){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
           return mapper.deleteById(id) > 0;
        } finally {
            ss.close();
        }
    }
    
   
    public static boolean deleteByTradeId(long id){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
           return mapper.deleteByTradeId(id) > 0;
        } finally {
            ss.close();
        }
    }
    
    
    
    /**
     * 更新订单基本信息
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:17:12
     */
    public boolean update(){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
            return mapper.updateById(this) > 0;
        } finally {
            ss.close();
        }
    }
    
    /**
     * 更新订单
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月29日 下午4:28:34
     */
    public boolean updateInfo(){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
            return mapper.updateInfo(this) > 0;
        } finally {
            ss.close();
        }
    }
    
    
    /**
     * 查找记录
     *
     * @param id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:51:27
     */
    public static Order findById(long id){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
            return mapper.selectById(id);
        } finally {
            ss.close();
        }
    }
    
    /**
     * 设置交易id
     *
     * @param tradeId
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月11日 上午11:01:13
     */
    public Order tradeId(long tradeId){
        if(this.tradeId > 0 ){
            return this;
        }
        this.tradeId = tradeId;
        return this;
    }
    
    /**
     * 生成订单id
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月11日 上午11:51:19
     */
    public Order orderId(List<Long> exsitIds){
        this.id = this.makeAvaliableId(exsitIds);
        exsitIds.add(this.id);
        return this;
    }
    
    /**
     * 生成订单id
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月29日 下午4:14:46
     */
    public Order makeId(){
        this.id = this.makeAvaliableId();
        return this;
    }
    
    /**
     * 生成唯一交易id
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月11日 上午10:37:08
     */
    private static long makeAvaliableId() {
        // 生成10位随机数
        long rand10 = Pandora.newInstance(RandomUtils.nextInt(8, 31), RandomUtils.nextInt(8, 31)).makeId(10);
        DateTime dtNow = DateTime.now();
        long id = Longs.tryParse(dtNow.toString("MMddHH") + rand10);
        while (true) {
            Order order = findById(id);
            if (order == null) {
                // id可用，不重复
                break;
            }
            log.warn("Found duplicate order id={}, generate again", id);
            id++;
        }
        return id;
    }
    
    /**
     * 
     *
     * @param ids
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年9月12日 下午10:48:02
     */
    private long makeAvaliableId(List<Long> ids) {
        // 生成10位随机数
        long rand10 = Pandora.newInstance(RandomUtils.nextInt(8, 31), RandomUtils.nextInt(8, 31)).makeId(10);
        DateTime dtNow = new DateTime(this.createTime);
        long id = Longs.tryParse(dtNow.toString("MMddHH") + rand10);
        while (true) {
            Order order = findById(id);
            if (order == null && ids ==null || !ids.contains(id)) {
                // id可用，不重复
                break;
            }
            log.warn("Found duplicate order id={}, generate again", id);
            id++;
        }
        return id;
    }
    
    
    public static List<Order> findListByTradeId(long tradeId){
        
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            OrderMapper mapper = ss.getMapper(OrderMapper.class);
            return mapper.selectByTradeId(tradeId);
        } finally {
            ss.close();
        }
    }
}
