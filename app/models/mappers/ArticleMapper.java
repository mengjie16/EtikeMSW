package models.mappers;

import java.util.Date;
import java.util.List;

import models.Article;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.ArticleSearchVo;

/**
 *  商品品牌
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年5月30日 下午4:13:06
 */
public interface ArticleMapper {

    @Select("select * from " + Article.TABLE_NAME + " where id=#{id} and is_delete = false limit 1")
    Article selectById(long id);
    
    @Select("select * from " + Article.TABLE_NAME + " where create_time <= '${createTime}' and id!=${id} and is_delete =false and is_public=true order by create_time desc limit 1")
    Article selectPrev(@Param("id") long id,@Param("createTime") String createTime);
    
    @Select("select * from " + Article.TABLE_NAME + " where create_time >= '${createTime}' and id!=${id} and is_delete =false and is_public=true order by create_time asc limit 1")
    Article selectNext(@Param("id") long id,@Param("createTime") String createTime);
    
    @Select("select * from " + Article.TABLE_NAME + " where ${field}=#{value} limit 1")
    Article selectByField(@Param("field") String field, @Param("value") Object value);

    @Insert("insert into " + Article.TABLE_NAME
        + "(author_id,author_name,title,type_name,type_id,content,is_delete,is_public,is_inlist,create_time) "
        + "values(#{authorId},#{authorName},#{title},#{typeName},#{typeId},#{content},#{isDelete},#{isPublic},#{isInList},#{createTime})")
    void insert(Article article);
    
    void updateById(Article article);
    
    @Select("select count(id) from " + Article.TABLE_NAME)
    int count();
       
    List<Article> selectList();
    
    @Delete("delete from "+Article.TABLE_NAME+" where id = ${id}")
    int deleteById(@Param("id") long id);
    
    List<Article> selectByVo(ArticleSearchVo vo);
    
    int countTotalVo(ArticleSearchVo vo);
    
    @Update("update "+Article.TABLE_NAME+" set is_delete = 1 where id=#{id}")
    void delete(@Param("id")long id);
}
