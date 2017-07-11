package models.mappers;

import java.util.List;

import models.Retailer;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import vos.RetailerSearchVo;

/**
 * 零售商数据操纵
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年4月25日 上午12:39:22
 */
public interface RetailerMapper {

    @Select("select * from " + Retailer.TABLE_NAME + " where id=#{id}")
    Retailer selectById(long id);

    @Select("select * from " + Retailer.TABLE_NAME + " where ${field}=#{value} limit 1")
    Retailer selectByField(@Param("field") String field, @Param("value") Object value);

    int insert(Retailer retailer);

    void updateById(Retailer retailer);

    @Select("select count(id) from " + Retailer.TABLE_NAME)
    int count();

    @Delete("delete from " + Retailer.TABLE_NAME + " where id = #{id}")
    int deleteById(int id);

    List<Retailer> selectListByPage(RetailerSearchVo vo);

    int countTotalVo(RetailerSearchVo vo);
}
