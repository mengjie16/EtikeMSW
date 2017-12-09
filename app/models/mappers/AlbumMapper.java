package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import models.Album;

/**
 * 专辑
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年5月30日 下午4:13:06
 */
public interface AlbumMapper {

    @Select("select count(*) from " + Album.TABLE_NAME + " where ${field}=#{value} limit 1")
    int countByField(@Param("field") String field, @Param("value") Object value);

    Album selectById(long id);

    void insert(Album album);

    int updateById(Album album);

    int updateNameById(Album album);

    @Update("update " + Album.TABLE_NAME
            + " SET album_items = JSON_ARRAY_APPEND(album_items,'$',CAST('${album}' AS JSON)) WHERE id =${id}")
    int addAlbumItem(@Param("id") long id, @Param("album") String albumJsonStr);

    @Select("select count(id) from " + Album.TABLE_NAME)
    int count();

    List<Album> selectList();

    @Delete("delete from " + Album.TABLE_NAME + " where id = ${id}")
    int deleteById(@Param("id") long id);

    @Update("update " + Album.TABLE_NAME + " SET album_items = JSON_REMOVE(album_items,'$[${index}]') WHERE id =${id}")
    int updateAlbumItemByIndex(@Param("index") int index, @Param("id") long id);

    @Update("update " + Album.TABLE_NAME
            + " SET album_items = JSON_SET(album_items,'$[${index}].itemId',${itemId}) WHERE id =${id}")
    int updateAlbumItemIdByIndex(@Param("index") int index, @Param("itemId") long itemId, @Param("id") long id);

    @Update("update " + Album.TABLE_NAME
            + " SET album_items = JSON_SET(album_items,'$[${index}].price',${price}) WHERE id =${id}")
    int updateAlbumItemPriceByIndex(@Param("index") int index, @Param("price") double price, @Param("id") long id);

    @Update("update " + Album.TABLE_NAME
            + " SET album_items = JSON_SET(album_items,'$[${index}].picUrl','${picUrl}') WHERE id =${id}")
    int updateAlbumItemPicUrlByIndex(@Param("index") int index, @Param("picUrl") String picUrl, @Param("id") long id);

    @Select("SELECT IFNULL(json_contains(JSON_EXTRACT(album_items,'$[*].itemId'),'[${itemId}]'),0) FROM album WHERE id = ${id}")
    int selectCountAlbumItemByItemId(@Param("itemId") long itemId, @Param("id") long id);
}
