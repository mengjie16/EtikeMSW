package models.mappers;

import java.util.List;

import models.Brand;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.BrandSearchVo;

/**
 *  商品品牌
 * 
 * @author Calm
 * @since  v1.0
 * @created 2016年5月30日 下午4:13:06
 */
public interface BrandMapper {

    @Select("select * from " + Brand.TABLE_NAME + " where id=#{id} limit 1")
    Brand selectById(long id);
    
    @Select("select * from " + Brand.TABLE_NAME + " where ${field}=#{value} limit 1")
    Brand selectByField(@Param("field") String field, @Param("value") Object value);

    @Insert("insert into " + Brand.TABLE_NAME
        + "(name,secondary_name,pic_url,company,holder,note,descption) "
        + "values(#{name},#{secondaryName},#{picUrl},#{company},#{holder},#{note},#{descption})")
    void insert(Brand brand);
    
    void updateById(Brand brand);
    
    @Select("select count(id) from " + Brand.TABLE_NAME)
    int count();
    
    @Select("select * from " + Brand.TABLE_NAME + " where name like '%${keyWord}%' or secondary_name like '%${keyWord}%' order by id desc limit 20")
    List<Brand> selectByKeyWord(@Param("keyWord") String keyWord);
    
    List<Brand> selectAll();
    
    @Delete("delete from "+Brand.TABLE_NAME+" where id = ${id}")
    int deleteById(@Param("id") long id);
    
    List<Brand> selectByVo(BrandSearchVo vo);
    
    int countByVo(BrandSearchVo vo);
}
