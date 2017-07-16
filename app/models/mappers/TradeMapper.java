package models.mappers;

import java.util.Date;
import java.util.List;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import enums.TradeStatus;
import models.Trade;
import vos.TradeSearchVo;


/**
 * 交易
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月25日 下午4:25:28
 */
public interface TradeMapper {
    
    @Select("select count(id) from " + Trade.TABLE_NAME + " where ${field}=#{value} limit 1")
    int countByField(@Param("field") String field, @Param("value") Object value);
    
    @Select("select * from " + Trade.TABLE_NAME + " where ${field}=#{value} limit 1")
    Trade selectByField(@Param("field") String field, @Param("value") Object value);
    
    int insert(Trade trade);
    
    int updateById(Trade trade);
    
    int updateFee(Trade trade);
    
    @Update("update " + Trade.TABLE_NAME + " set status='TRADE_USER_CANCELLED',close_time=now() where id = ${id}")
    int userCancel(@Param("id") long id);
    
    @Update("update " + Trade.TABLE_NAME + " set status='TRADE_USER_CANCELLED',close_time=now() where id = ${id}")
    int sysCancel(@Param("id") long id);
    
    List<Trade> selectList();
    
    Trade selectWithOrdersById(@Param("tradeId") long tradeId);

    List<Trade> selectListWithOrderByVo(TradeSearchVo vo);
    
    List<Trade> selectListWithOrderTradeStatusByVo(@Param("vo")TradeSearchVo vo,@Param("tradeStatus")TradeStatus tradeStatus);
    
}
