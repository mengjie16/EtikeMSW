package models.mappers;

import java.util.List;

import models.Brand;
import models.Cart;
import models.Item;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import enums.ItemStatus;
import vos.FreightSearchVo;
import vos.ItemSearchVo;
import vos.UserSearchVo;

public interface CartMapper {

    @Select("select * from "+ Cart.TABLE_NAME+" where id = #{id}")
    Cart selectById(@Param("id") long id);
    
  
    int insert(Cart cart);
    
    @Update("update " + Cart.TABLE_NAME + " set cartCount = #{count} where id = #{id}")
    void updateCount(int count, long id);
    
    
    @Delete("delete from "+Cart.TABLE_NAME+" where id = #{id}")
    void deleteById(long id);
    
    @Delete("select * from "+ Cart.TABLE_NAME+" where retailerId = #{retailerId}")
    List<Cart> selectListByRetailer(@Param("retailerId") long retailerId);

    @Select("select id from "+ Cart.TABLE_NAME+" where item_id = #{itemId} and sku_color = #{skuColor}")
    int selectByItemIdAndColor(@Param("itemId") long itemId, @Param("skuColor") String skuColor);
    
    
    
}
