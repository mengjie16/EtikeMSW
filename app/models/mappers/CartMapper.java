package models.mappers;

import java.util.List;

import models.Brand;
import models.Cart;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


public interface CartMapper {

    @Select("select * from "+ Cart.TABLE_NAME+" where id = #{id}")
    Cart selectById(@Param("id") long id);
    
  
    int insert(Cart cart);
    
    @Update("update " + Cart.TABLE_NAME + " set cart_count = #{cartCount} where id = #{id}")
    void updateCount(@Param("cartCount") int cartCount, @Param("id")long id);
    
    
    @Delete("delete from "+ Cart.TABLE_NAME +" where id = #{id}")
    void deleteById(long id);
    
    @Select("select * from "+ Cart.TABLE_NAME +" where retailer_id = #{retailerId}")
    List<Cart> selectListByRetailer(@Param("retailerId") long retailerId);

    @Select("select * from "+ Cart.TABLE_NAME+" where item_id = #{itemId} and sku_color = #{skuColor}")
    Cart selectByItemIdAndColor(@Param("itemId") long itemId, @Param("skuColor") String skuColor);
    
    
    
}
