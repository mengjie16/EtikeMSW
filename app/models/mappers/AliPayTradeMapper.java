package models.mappers;

import java.util.Date;
import java.util.List;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import models.AliPayTrade;


/**
 * 支付宝交易
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月25日 下午4:25:28
 */
public interface AliPayTradeMapper {
    
    @Select("select count(id) from " + AliPayTrade.TABLE_NAME + " where ${field}=#{value} limit 1")
    int countByField(@Param("field") String field, @Param("value") Object value);
    
    @Select("select * from " + AliPayTrade.TABLE_NAME + " where ${field}=#{value} limit 1")
    AliPayTrade selectByField(@Param("field") String field, @Param("value") Object value);
    
    int insert(AliPayTrade trade);
    
    int updateById(AliPayTrade trade);
    
    List<AliPayTrade> selectList();
}
