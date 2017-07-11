package models;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;
import org.apache.ibatis.session.SqlSession;
import org.joda.time.DateTime;

import com.aton.db.SessionFactory;
import com.aton.util.MixHelper;
import com.aton.util.NumberUtils;
import com.aton.util.Pandora;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

import enums.AliPayTradeStatus;
import models.mappers.AliPayTradeMapper;
import enums.AliPayRefundStatus;

/**
 * 支付宝支付通知＋订单数据结构
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月25日 下午3:57:01
 */
public class AliPayTrade implements java.io.Serializable{
    
    public static final String TABLE_NAME = "alipay_trade";
    
    // ----------基本参数(不做数据录入)
    public Date notifyTime;
    public String notifyType;
    public String notifyId;
    public String signType;
    public String sign;
    
    // ---------- 自定义参数
    //唯一id,即out_trade_no
    public long id;
    public long userId;
    // ---------- 业务参数
    // 商户网站唯一订单号
    public long outTradeNo;
    // 商品名称
    public String subject;
    // 支付类型（取值1）
    public String paymentType;
    // 支付宝交易号
    public String tradeNo;
    // 交易状态
    public AliPayTradeStatus tradeStatus;
    // 交易创建时间
    public Date gmtCreate;
    // 交易付款时间
    public Date gmtPayment;
    // 交易关闭时间
    public Date gmtClose;
    // 卖家支付宝账号
    public String sellerEmail;
    // 买家支付宝账号
    public String buyerEmail;
    // 卖家支付宝账户号
    public String sellerId;
    // 买家支付宝账户号
    public String buyerId;
    // 交易金额
    public int totalFee;
    
    
    /**
     * 生成唯一交易id
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午4:20:40
     */
    public static long makeTradeNo() {
        // 生成10位随机数
        long rand10 = Pandora.newInstance(RandomUtils.nextInt(1, 30), RandomUtils.nextInt(1, 30))
            .makeId(10);
        DateTime dtNow = DateTime.now();

        long id = Longs.tryParse(dtNow.toString("yyMMdd") + rand10);
        while (checkExsitById(id)) {
            id++;
        }
        return id;
    }
    
    /**
     * 创建交易
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:03:52
     */
    public AliPayTrade create(){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AliPayTradeMapper mapper = ss.getMapper(AliPayTradeMapper.class);
            this.id = makeTradeNo();
            this.gmtCreate = DateTime.now().toDate();
            if(mapper.insert(this) > 0){
                return this;
            }
        } finally {
            ss.close();
        }
        return null;
    }
    
    /**
     * 更新交易
     *
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:17:12
     */
    public boolean update(){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AliPayTradeMapper mapper = ss.getMapper(AliPayTradeMapper.class);
            return mapper.updateById(this) > 0;
        } finally {
            ss.close();
        }
    }
    
    /**
     * 检查是否存在
     *
     * @param id
     * @return true 存在
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午5:08:25
     */
    public static boolean checkExsitById(long id){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AliPayTradeMapper mapper = ss.getMapper(AliPayTradeMapper.class);
            return mapper.countByField("id", id) > 0;
        } finally {
            ss.close();
        }
    }
    
    /**
     * 查找支付宝交易记录
     *
     * @param id
     * @return
     * @since  v1.0
     * @author Calm
     * @created 2016年8月25日 下午6:51:27
     */
    public static AliPayTrade findById(long id){
        SqlSession ss = SessionFactory.getSqlSession();
        try {
            AliPayTradeMapper mapper = ss.getMapper(AliPayTradeMapper.class);
            return mapper.selectByField("id", id);
        } finally {
            ss.close();
        }
    }
   
}
