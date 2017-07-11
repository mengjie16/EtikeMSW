package models.mappers;

import java.util.Date;
import java.util.List;

import models.FootMark;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.ArticleSearchVo;

/**
 * 足迹
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年8月1日 下午6:05:52
 */
public interface FootMarkMapper {
    
    @Select("select count(*) from " + FootMark.TABLE_NAME + " where ${field}=#{value} limit 1")
    int countByField(@Param("field") String field, @Param("value") Object value);
    
    @Select("select * from " + FootMark.TABLE_NAME+" where id=#{id} limit 1")
    FootMark selectById(long id);
    
    @Insert("insert into "+FootMark.TABLE_NAME+"(user_id,item_id,create_time) values(#{userId},#{itemId},#{createTime})")
    void insert(FootMark album);
    
    @Select("select count(id) from " + FootMark.TABLE_NAME)
    int count();
    
    @Select("select * from " + FootMark.TABLE_NAME + " where user_id=#{userId} order by create_time desc ")
    List<FootMark> selectListByUserId(@Param("userId") long userId);
    
    @Delete("delete from "+FootMark.TABLE_NAME+" where id = ${id}")
    int deleteById(@Param("id") long id);
 
}
