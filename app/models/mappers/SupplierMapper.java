package models.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import models.Supplier;
import vos.SupplierSearchVo;
import vos.SupplierVo;

/**
 * 供货商数据操纵
 * 
 * @author Calm
 * @since v1.0
 * @created 2016年4月25日 上午12:39:22
 */
public interface SupplierMapper {

    @Select("select * from " + Supplier.TABLE_NAME + " where id=#{id}")
    Supplier selectById(long id);

    @Select("select * from " + Supplier.TABLE_NAME + " where ${field}=#{value} limit 1")
    Supplier selectByField(@Param("field") String field, @Param("value") Object value);

    int insert(Supplier supplier);

    void updateById(Supplier supplier);

    @Deprecated
    void updateByVo(SupplierVo suppliervo);

    @Select("select count(id) from " + Supplier.TABLE_NAME)
    int count();

    @Delete("delete from " + Supplier.TABLE_NAME + " where id = #{id}")
    void deleteById(long id);

    List<Supplier> selectListByPage(SupplierSearchVo vo);

    int countTotalVo(SupplierSearchVo vo);
}
