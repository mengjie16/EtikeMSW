package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import models.Favorite;

public interface FavoriteMapper {
    @Delete("delete from "+Favorite.TABLE_NAME+" where id = ${id} and item_id=#{itemId}")
    int delete(@Param("id") long id,@Param("itemId") long itemId);
    
    @Select("select * from " + Favorite.TABLE_NAME + " where retailerId = #{retailer_id}")
    List<Favorite> selectList(long retailerId);

    int insert(Favorite favorite);
    
    
}
