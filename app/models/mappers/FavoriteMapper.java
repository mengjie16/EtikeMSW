package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import models.Cart;
import models.Favorite;
import models.Retailer;

public interface FavoriteMapper {
    @Delete("delete from "+Favorite.TABLE_NAME+" where id=#{id} ")
    int delete(@Param("id") long id);
    
    @Select("select * from " + Favorite.TABLE_NAME + " where retailer_id = #{retailerId}")
    List<Favorite> selectList(long retailerId);

    int insert(Favorite favorite);
    
    @Select("select count(*) from " + Favorite.TABLE_NAME + " where retailer_id = #{retailerId} and item_id=#{itemId}")
    int selectById(@Param("itemId") long itemId,@Param("retailerId") long retailerId);
    
    
}
