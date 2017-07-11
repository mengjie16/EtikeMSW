package models.mappers;

import java.util.Date;
import java.util.List;


import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import models.Order;


/**
 * 订单
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月25日 下午4:25:28
 */
public interface OrderMapper {
    
    @Select("select count(id) from " + Order.TABLE_NAME + " where ${field}=#{value} limit 1")
    int countByField(@Param("field") String field, @Param("value") Object value);
    
    @Select("select * from " + Order.TABLE_NAME + " where ${field}=#{value} limit 1")
    Order selectByField(@Param("field") String field, @Param("value") Object value);
    
    @Delete("delete from " + Order.TABLE_NAME + " where id=#{id} limit 1")
    int deleteById(@Param("id") long value);
    
    Order selectById(long id);
    
    List<Order> selectByTradeId(@Param("id") long id);
    
    int insert(Order order);
    
    int inserts(@Param("orders") List<Order> orders);
    
    int updateById(Order order);
    
    int updateInfo(Order order);
    
    int updateStatusById(Order order);
    
    List<Order> selectList();
    
}
