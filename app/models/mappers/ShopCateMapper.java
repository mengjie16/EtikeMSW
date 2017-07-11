package models.mappers;

import java.util.List;

import models.ShopCate;

import org.apache.ibatis.annotations.Select;

public interface ShopCateMapper {

    /**
     * 
     * 获取店铺类目中的一级类目
     * 
     * @param id
     * @return
     * @since v0.1
     * @author youblade
     * @created 2013-8-14 下午8:56:05
     */
    @Select("select *  from " + ShopCate.TABLE_NAME + " c  where c.hot > 0  order by c.hot desc")
    public List<ShopCate> selectAll();

    @Select("select * from " + ShopCate.TABLE_NAME + " where id=#{id}")
    public ShopCate selectById(int id);

}